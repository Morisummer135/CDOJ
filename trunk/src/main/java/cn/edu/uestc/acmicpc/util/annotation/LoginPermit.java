package cn.edu.uestc.acmicpc.util.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.edu.uestc.acmicpc.util.settings.Global;

/**
 * Login permission controller.
 * <p/>
 * Use this annotation to validate users' types.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginPermit {

  /**
   * Set user type needed. The user type can refer to
   * {@link cn.edu.uestc.acmicpc.util.settings.Global.AuthenticationType}.
   *
   * @return User type needed.
   * @see cn.edu.uestc.acmicpc.util.settings.Global.AuthenticationType
   */
  public Global.AuthenticationType value() default Global.AuthenticationType.NORMAL;

  /**
   * Need user toLogin or not
   *
   * @return if this action will need user toLogin, set it true.
   */
  public boolean NeedLogin() default true;
}
