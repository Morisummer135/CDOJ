/*
 *
 *  * cdoj, UESTC ACMICPC Online Judge
 *  * Copyright (c) 2013 fish <@link lyhypacm@gmail.com>,
 *  * 	mzry1992 <@link muziriyun@gmail.com>
 *  *
 *  * This program is free software; you can redistribute it and/or
 *  * modify it under the terms of the GNU General Public License
 *  * as published by the Free Software Foundation; either version 2
 *  * of the License, or (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program; if not, write to the Free Software
 *  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package cn.edu.uestc.acmicpc.oj.action;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;

import cn.edu.uestc.acmicpc.db.entity.User;
import cn.edu.uestc.acmicpc.ioc.service.UserServiceAware;
import cn.edu.uestc.acmicpc.oj.interceptor.AppInterceptor;
import cn.edu.uestc.acmicpc.oj.interceptor.iface.IActionInterceptor;
import cn.edu.uestc.acmicpc.oj.service.iface.UserService;
import cn.edu.uestc.acmicpc.oj.view.PageInfo;
import cn.edu.uestc.acmicpc.util.Global;
import cn.edu.uestc.acmicpc.util.Settings;
import cn.edu.uestc.acmicpc.util.StringUtil;
import cn.edu.uestc.acmicpc.util.annotation.LoginPermit;
import cn.edu.uestc.acmicpc.util.exception.AppException;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Base action support, add specified common elements in here.
 */
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class BaseAction extends ActionSupport implements RequestAware, SessionAware,
    ApplicationAware, IActionInterceptor, ServletResponseAware, ServletRequestAware,
    UserServiceAware, ApplicationContextAware {

  private static final long serialVersionUID = -3221772654123596229L;

  /**
   * Number of records per page
   */
  protected static final Long RECORD_PER_PAGE = 50L;

  /**
   * Global constant
   */
  @Autowired
  private Global global;

  public Global getGlobal() {
    return global;
  }

  public void setGlobal(Global global) {
    this.global = global;
  }

  protected Map<Integer, Global.AuthorStatusType> problemStatus;

  @Autowired
  protected ApplicationContext applicationContext;

  /**
   * Request attribute map.
   */
  protected Map<String, Object> request;

  /**
   * Session attribute map.
   */
  protected Map<String, Object> session;

  /**
   * Application attribute map.
   */
  protected Map<String, Object> application;

  /**
   * Http Servlet Request.
   */
  protected HttpServletRequest httpServletRequest;

  /**
   * Http Servlet Response.
   */
  protected HttpServletResponse httpServletResponse;

  /**
   * Http Session.
   */
  protected HttpSession httpSession;

  /**
   * Servlet Context.
   */
  protected ServletContext servletContext;

  /**
   * Current toLogin user.
   */
  protected User currentUser;

  /**
   * redirect flag.
   */
  protected final String REDIRECT = "redirect";

  /**
   * to index flag.
   */
  protected final String TOINDEX = "toIndex";

  /**
   * JSON flag.
   */
  protected final String JSON = "json";

  /**
   * JSON result.
   */
  protected Map<String, Object> json = new HashMap<>();

  @Autowired
  protected UserService userService;

  /**
   * Global settings for actions.
   */
  @Autowired
  protected Settings settings;

  public void setSettings(Settings settings) {
    this.settings = settings;
  }

  @Override
  public void setApplication(Map<String, Object> application) {
    this.application = application;
  }

  @Override
  public void setRequest(Map<String, Object> request) {
    this.request = request;
  }

  @Override
  public void setSession(Map<String, Object> session) {
    this.session = session;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onActionExecuting(AppInterceptor.ActionInfo actionInfo) {
    checkIE6(actionInfo);
    checkAuth(actionInfo);
    if (session.containsKey("problemStatus")) {
      this.problemStatus = (Map<Integer, Global.AuthorStatusType>) session.get("problemStatus");
    } else {
      this.problemStatus = new HashMap<>();
    }
  }

  @Override
  public void onActionExecuted() {
  }

  /**
   * Check <strong>IE6</strong> browser.
   *
   * @param actionInfo action information entity
   */
  private void checkIE6(AppInterceptor.ActionInfo actionInfo) {
    String user_agent = httpServletRequest.getHeader("User-Agent");
    if (user_agent != null && user_agent.contains("MSIE 6")) {
      try {
        // TODO "/WEB-INF/views/shared/ie6.jsp" is error page, fixed it.
        httpServletRequest.getRequestDispatcher("/WEB-INF/views/shared/ie6.jsp").forward(
            httpServletRequest, httpServletResponse);
      } catch (Exception ignored) {
      }
      actionInfo.setCancel(true);
    }
  }

  /**
   * Get http request parameter.
   *
   * @param param parameter name
   * @return parameter value
   */
  protected String getHttpParameter(String param) {
    return httpServletRequest.getParameter(param);
  }

  /**
   * Redirect to specific url with no message.
   *
   * @param url expected url
   * @return <strong>REDIRECT</strong> signal
   */
  protected String redirect(String url) {
    return redirect(url, null);
  }

  /**
   * Redirect to specific url and popup a message.
   *
   * @param url expected url
   * @param msg information message
   * @return <strong>REDIRECT</strong> signal
   */
  protected String redirect(String url, String msg) {
    request.put("msg", msg == null ? "" : msg);
    request.put("url", url == null ? "" : url);
    return REDIRECT;
  }

  /**
   * Get current toLogin user, if no user had toLogin, return {@code null}.
   *
   * @return current toLogin user entity
   */
  User getCurrentUserEntity() {
    try {
      String userName = (String) session.get("userName");
      String password = (String) session.get("password");
      Timestamp lastLogin = (Timestamp) session.get("lastLogin");
      User user = userService.getUserByUserName(userName);
      if (user == null || !user.getPassword().equals(password)
          || !user.getLastLogin().equals(lastLogin))
        return null;
      return user;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public User getCurrentUser() {
    return currentUser;
  }

  /**
   * Get actual absolute path of a virtual path.
   *
   * @param path path which we want to transform
   * @return actual path
   */
  String getContextPath(String path) {
    String result = StringUtil.choose(httpServletRequest.getContextPath(), "") + "/";
    result += path != null && path.startsWith("/") ? path.substring(1) : "";
    return result;
  }

  /**
   * Get action url by namespace, action name and parameter.
   * <p/>
   * <strong>Example</strong> getActionURL("/problem","page","/1") return
   * acm.uestc.edu.cn/problem/page/1 getActionURL("/problem","page","?id=1") return
   * acm.uestc.edu.cn/problem/page?id=1
   *
   * @param namespace action's namespace
   * @param name action name
   * @param parameterString parameter list for action
   * @return action url
   */
  String getActionURL(String namespace, String name, String parameterString) {
    String result = namespace.equals("/") ? "" : namespace;
    result = result + "/" + name;
    if (parameterString != null)
      result = result + parameterString;
    return getContextPath(result);
  }

  /**
   * Get action url by namespace and action name
   *
   * @param namespace action's namespace
   * @param name action name
   * @return action url
   */
  protected String getActionURL(String namespace, String name) {
    return getActionURL(namespace, name, null);
  }

  /**
   * Check user type.
   *
   * @param actionInfo action information object
   */
  private void checkAuth(AppInterceptor.ActionInfo actionInfo) {

    LoginPermit permit = actionInfo.getController().getAnnotation(LoginPermit.class);
    try {
      LoginPermit p2 =
          actionInfo.getController().getMethod(actionInfo.getAction().getName())
              .getAnnotation(LoginPermit.class);
      permit = p2 != null ? p2 : permit;
    } catch (Exception ignored) {
    }
    User user = getCurrentUserEntity();
    try {
      if (permit == null || !permit.NeedLogin()) {
        currentUser = user;
        request.put("currentUser", user);
        return;
      }
      if (user == null) {
        redirect(getActionURL("/", "index"));
        actionInfo.setCancel(true);
        actionInfo.setActionResult(REDIRECT);
        return;
      } else if (permit.value() != Global.AuthenticationType.NORMAL) {
        if (user.getType() != permit.value().ordinal()) {
          throw new AppException("This user is not " + permit.value().getDescription() + ".");
        }
      }
    } catch (AppException e) {
      actionInfo.setCancel(true);
      actionInfo.setActionResult(setError(e.getMessage()));
    }
    currentUser = user;
    request.put("currentUser", currentUser);
  }

  /**
   * Put error message into request and return error signal.
   *
   * @param message error message
   * @return error signal
   */
  protected String setError(String message) {
    request.put("errorMsg", message);
    return ERROR;
  }

  /**
   * Put exception's error message into request and return error signal.
   *
   * @param e application exception
   * @return error signal
   */
  protected String setError(AppException e) {
    return setError(e.getMessage());
  }

  @Override
  public void setServletRequest(HttpServletRequest request) {
    httpServletRequest = request;
    httpSession = httpServletRequest.getSession();
    servletContext = httpSession.getServletContext();
  }

  @Override
  public void setServletResponse(HttpServletResponse response) {
    httpServletResponse = response;
  }

  /**
   * Redirect to reference page with specific message.
   *
   * @param msg message content
   * @return redirect signal
   */
  String redirectToRefer(String msg) {
    return redirect(httpServletRequest.getHeader("Referer"), msg);
  }

  /**
   * Redirect to reference page with no message.
   *
   * @return redirect signal
   */
  protected String redirectToRefer() {
    return redirectToRefer(null);
  }

  /**
   * save current page information from post.
   */
  private Long currentPage;

  public void setCurrentPage(Long currentPage) {
    this.currentPage = currentPage;
  }

  /**
   * Build a page html content according to number of records, records per page, base URL and
   * display distance.
   * <p/>
   * <strong>Example:</strong> Get total and set it into {@code buildPageInfo} method: <br />
   * {@code PageInfo pageInfo = buildPageInfo(articleDAO.count(), RECORD_PER_PAGE,
   * getContextPath("") + "/Problem", null);}
   *
   * @param count total number of records
   * @param countPerPage number of records per page
   * @param baseURL base URL
   * @param displayDistance display distance for page numbers
   * @return return a PageInfo object and put the HTML content into request attribute list.
   */
  protected PageInfo buildPageInfo(Long count, Long countPerPage, String baseURL,
      Integer displayDistance) {
    PageInfo pageInfo =
        PageInfo.create(count, countPerPage, baseURL,
            displayDistance == null ? 4 : displayDistance, currentPage);
    request.put("pageInfo", pageInfo.getHtmlString());
    return pageInfo;
  }

  /**
   * Write string content into web page.
   *
   * @param content content
   */
  protected void out(String content) throws IOException {
    httpServletResponse.getWriter().write(content);
  }

  public Map<String, Object> getJson() {
    return json;
  }

  public void setJson(Map<String, Object> json) {
    this.json = json;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void setUserService(UserService userService) {
    this.userService = userService;
  }
}
