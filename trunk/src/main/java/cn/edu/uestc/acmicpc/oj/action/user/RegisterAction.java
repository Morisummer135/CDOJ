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

package cn.edu.uestc.acmicpc.oj.action.user;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import cn.edu.uestc.acmicpc.db.dto.impl.UserDTO;
import cn.edu.uestc.acmicpc.db.entity.User;
import cn.edu.uestc.acmicpc.ioc.dto.UserDTOAware;
import cn.edu.uestc.acmicpc.oj.action.BaseAction;
import cn.edu.uestc.acmicpc.util.annotation.LoginPermit;

import com.opensymphony.xwork2.validator.annotations.EmailValidator;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * Action for register
 */
@Controller
@LoginPermit(NeedLogin = false)
public class RegisterAction extends BaseAction implements UserDTOAware {

  private static final long serialVersionUID = -2854303130010851540L;

  @Autowired
  private UserDTO userDTO;

  /**
   * Register action.
   * <p/>
   * Check register information and pass then to validator, if the information is correct, write the
   * user's information into database.
   *
   * @return <strong>JSON</strong> signal, process in front
   */
  @Validations(
      requiredStrings = {
          @RequiredStringValidator(fieldName = "userDTO.userName",
              key = "error.userName.validation"),
          @RequiredStringValidator(fieldName = "userDTO.password",
              key = "error.password.validation"),
          @RequiredStringValidator(fieldName = "userDTO.nickName",
              key = "error.nickName.validation"),
          @RequiredStringValidator(fieldName = "userDTO.email", key = "error.email.validation"),
          @RequiredStringValidator(fieldName = "userDTO.school", key = "error.school.validation"),
          @RequiredStringValidator(fieldName = "userDTO.studentId",
              key = "error.studentId.validation") },
      stringLengthFields = {
          @StringLengthFieldValidator(fieldName = "userDTO.password",
              key = "error.password.validation", minLength = "6", maxLength = "20", trim = false),
          @StringLengthFieldValidator(fieldName = "userDTO.school",
              key = "error.school.validation", minLength = "1", maxLength = "100", trim = false),
          @StringLengthFieldValidator(fieldName = "userDTO.studentId",
              key = "error.studentId.validation", minLength = "1", maxLength = "20", trim = false),
          @StringLengthFieldValidator(fieldName = "userDTO.nickName",
              key = "error.nickName.validation", minLength = "2", maxLength = "20", trim = false) },
      fieldExpressions = {
          @FieldExpressionValidator(fieldName = "userDTO.passwordRepeat",
              expression = "userDTO.password == userDTO.passwordRepeat",
              key = "error.passwordRepeat.validation"),
          @FieldExpressionValidator(fieldName = "userDTO.departmentId",
              expression = "userDTO.departmentId in global.departmentList.{departmentId}",
              key = "error.department.validation") }, emails = { @EmailValidator(
          fieldName = "userDTO.email", key = "error.email.validation") })
  public
      String toRegister() {
    try {
      if (userService.getUserByUserName(userDTO.getUserName()) != null) {
        addFieldError("userDTO.userName", "User name has been used!");
        return INPUT;
      }
      if (userService.getUserByEmail(userDTO.getEmail()) != null) {
        addFieldError("userDTO.email", "Email has benn used!");
        return INPUT;
      }

      // TODO I don't know why struts2 regex validation doesn't work now.
      Pattern pattern = Pattern.compile("\\b^[a-zA-Z0-9_]{4,24}$\\b");
      Matcher matcher = pattern.matcher(userDTO.getUserName());
      if (!matcher.find()) {
        addFieldError("userDTO.userName",
            "Please enter 4-24 characters consist of A-Z, a-z, 0-9 and '_'.");
        return INPUT;
      }

      userDTO.setDepartmentId(userDTO.getDepartmentId());
      User user = userDTO.getEntity();
      userService.createNewUser(user);
      session.put("userName", user.getUserName());
      session.put("password", user.getPassword());
      session.put("lastLogin", user.getLastLogin());
      session.put("userType", user.getType());
      json.put("result", "ok");
    } catch (Exception e) {
      e.printStackTrace();
      json.put("result", "error");
      json.put("error_msg", "Unknown exception occurred.");
    }
    return JSON;
  }

  @Override
  public UserDTO getUserDTO() {
    return userDTO;
  }

  @Override
  public void setUserDTO(UserDTO userDTO) {
    this.userDTO = userDTO;
  }
}
