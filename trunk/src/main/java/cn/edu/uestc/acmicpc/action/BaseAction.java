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

package cn.edu.uestc.acmicpc.action;

import cn.edu.uestc.acmicpc.annotation.LoginPermit;
import cn.edu.uestc.acmicpc.db.dao.UserDAO;
import cn.edu.uestc.acmicpc.db.entity.User;
import cn.edu.uestc.acmicpc.interceptor.AppInterceptor;
import cn.edu.uestc.acmicpc.interceptor.iface.IActionInterceptor;
import cn.edu.uestc.acmicpc.ioc.UserDAOAware;
import cn.edu.uestc.acmicpc.util.AppException;
import cn.edu.uestc.acmicpc.util.Global;
import cn.edu.uestc.acmicpc.util.StringUtil;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;

/**
 * Base action support, add specified common elements in here.
 *
 * @author <a href="mailto:lyhypacm@gmail.com">fish</a>
 * @version 5
 */
public class BaseAction extends ActionSupport
        implements RequestAware, SessionAware, ApplicationAware, IActionInterceptor,
        ServletResponseAware, ServletRequestAware, UserDAOAware {
    private static final long serialVersionUID = -3221772654123596229L;

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
     * Current login user.
     */
    protected User currentUser;

    /**
     * userDAO for user login check.
     */
    protected UserDAO userDAO = null;

    /**
     * redirect flag.
     */
    protected final String REDIRECT = "redirect";

    /**
     * Implement {@link ApplicationAware} interface, with Ioc.
     *
     * @param application application attribute
     */
    @Override
    public void setApplication(Map<String, Object> application) {
        this.application = application;
    }

    /**
     * Implement {@link RequestAware} interface, with Ioc.
     *
     * @param request request attribute
     */
    @Override
    public void setRequest(Map<String, Object> request) {
        this.request = request;
    }

    /**
     * Implement {@link SessionAware} interface, with Ioc.
     *
     * @param session session attribute
     */
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    @Override
    public void onActionExecuting(AppInterceptor.ActionInfo actionInfo) {
        checkIE6(actionInfo);
    }

    @Override
    public void onActionExecuted() {
    }

    /**
     * Check <strong>IE6</strong> browser.
     *
     * @param actionInfo
     */
    private void checkIE6(AppInterceptor.ActionInfo actionInfo) {
        String user_agent = httpServletRequest.getHeader("User-Agent");
        if (user_agent != null && user_agent.indexOf("MSIE 6") > -1) {
            try {
                // TODO "/WEB-INF/views/shared/ie6.jsp" is error page, fixed it.
                httpServletRequest.getRequestDispatcher("/WEB-INF/views/shared/ie6.jsp").forward(
                        httpServletRequest, httpServletResponse);
            } catch (Exception e) {
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
    protected String get(String param) {
        return httpServletRequest.getParameter(param);
    }

    /**
     * Redirect to specific url with no message.
     *
     * @param url expected url
     * @return
     */
    protected String redirect(String url) {
        return redirect(url, null);
    }

    /**
     * Redirect to specific url and popup a message.
     *
     * @param url expected url
     * @param msg information message
     * @return
     */
    protected String redirect(String url, String msg) {
        request.put("msg", msg == null ? "" : msg);
        request.put("url", url == null ? "" : url);
        return REDIRECT;
    }

    /**
     * Get current login user, if no user had login, return {@code null}.
     *
     * @return current login user entity
     */
    protected User getCurrentUser() {
        String userName = (String) request.get("userName");
        String password = (String) request.get("password");
        Date lastLogin = (Date) request.get("lastLogin");
        User user = userDAO.getUserByName(userName);
        if (user == null || !user.getPassword().equals(password)
                || !user.getLastLogin().equals(lastLogin))
            return null;
        return user;
    }

    /**
     * Get actual absolute path of a virtual path.
     *
     * @param path path which we want to transform
     * @return actual path
     */
    protected String getContextPath(String path) {
        String result = StringUtil.choose(httpServletRequest.getContextPath(), "") + "/";
        result += path != null && path.startsWith("/") ? path.substring(1) : "";
        return result;
    }

    /**
     * Check user type.
     *
     * @param actionInfo action information object
     */
    private void checkAuth(AppInterceptor.ActionInfo actionInfo) {

        LoginPermit permit = actionInfo.getController().getAnnotation(
                LoginPermit.class);
        try {
            LoginPermit p2 = actionInfo.getController()
                    .getMethod(actionInfo.getAction().getName())
                    .getAnnotation(LoginPermit.class);
            permit = p2 != null ? p2 : permit;
        } catch (Exception e) {
        }
        User user = getCurrentUser();
        try {
            if (permit == null || !permit.NeedLogin()) {
                currentUser = user;
                request.put("currentUser", user);
                return;
            }
            if (user == null) {
                // TODO "/user/log" is login page
                redirect(getContextPath("/user/log"));
                actionInfo.setCancel(true);
                actionInfo.setActionResult(REDIRECT);
                return;
            } else if (permit.value() != Global.AuthenticationType.NORMAL) {
                if (user.getType() != permit.value().ordinal()) {
                    throw new AppException("This user is not "
                            + permit.value().getDescription() + ".");
                }
                // TODO add extra option here
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

    @Override
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Redirect to reference page with specific message.
     *
     * @param msg message content
     * @return redirect signal
     */
    protected String redirectToRefer(String msg) {
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
}
