/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General Public License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.yougi.web.model

import org.yougi.business.ApplicationPropertyBean
import org.yougi.business.AuthenticationBean
import org.yougi.business.UserAccountBean
import org.yougi.entity.ApplicationProperty
import org.yougi.entity.Authentication
import org.yougi.reference.Properties
import org.yougi.entity.UserAccount
import org.yougi.exception.BusinessLogicException
import org.yougi.util.ResourceBundleHelper
import org.yougi.annotation.ManagedProperty
import org.yougi.annotation.UserName

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.faces.application.FacesMessage
import javax.faces.component.UIComponent
import javax.faces.context.FacesContext
import javax.faces.validator.ValidatorException
import javax.inject.Inject
import javax.inject.Named
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class ChangePasswordMBean {

  private static final Logger LOGGER = Logger.getLogger(ChangePasswordMBean.class.getSimpleName())

  @EJB
  private UserAccountBean userAccountBean
  @EJB
  private AuthenticationBean authenticationBean
  @EJB
  private ApplicationPropertyBean applicationPropertyBean

  @Inject
  @ManagedProperty('#{param.cc}')
  String confirmationCode

  @Inject
  private FacesContext context

  @Inject
  @UserName
  String username
  String currentPassword
  String password
  String passwordConfirmation

  Boolean invalid = Boolean.FALSE

  // Beginning of password validation
  void validatePassword(FacesContext context, UIComponent component, Object value) {
    password = (String) value
  }

  void validatePasswordConfirmation(FacesContext context, UIComponent component, Object value) {
    passwordConfirmation = (String) value
    if(!passwordConfirmation.equals(password)) {
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.getMessage('errorCode0005'), null))
    }
  }
  // End of password validation

  @PostConstruct
  void load() {
    if(confirmationCode) {
      UserAccount userAccount = userAccountBean.findByConfirmationCode(confirmationCode)
      Authentication authentication = authenticationBean.findByUserAccount(userAccount)
      if(userAccount) {
        username = authentication.username
      } else {
        invalid = true
      }
    }
  }

  /**
   * @return returns the next step in the navigation flow.
   */
  String requestPasswordChange() {
    try {
      ApplicationProperty url = applicationPropertyBean.findApplicationProperty(Properties.URL)
      String serverAddress = url.getPropertyValue()
      userAccountBean.requestConfirmationPasswordChange(username, serverAddress)
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.getMessage('infoCode0003', username), null))
      return 'change_password'
    } catch(BusinessLogicException ble) {
      LOGGER.log(Level.INFO, ble.getMessage(), ble)
      context.addMessage(null, new FacesMessage(ble.getMessage()))
      return 'request_password_change'
    }
  }

  /**
   * It changes the password in case the user has forgotten it. It checks whether
   * the confirmation code sent to the user's email is valid before proceeding
   * with the password change.
   * @return returns the next step in the navigation flow.
   */
  String changeForgottenPassword() {
    UserAccount userAccount = userAccountBean.findByConfirmationCode(confirmationCode.trim().toUpperCase())

    if(userAccount == null) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.getMessage('errorCode0012'), null))
      return 'change_password'
    }

    try {
      userAccountBean.changePassword(userAccount, this.password)
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.getMessage('infoCode0004'), null))
    } catch (BusinessLogicException e) {
      LOGGER.log(Level.INFO, e.getMessage(), e)
      context.addMessage(null, new FacesMessage(e.getMessage()))
      return 'change_password'
    }
    'login'
  }

  /**
   * It changes the password in case the user still knows his(er) own password.
   * @return returns the next step in the navigation flow.
   */
  String changePassword() {
    UserAccount userAccount = userAccountBean.findByUsername(username)
    if(!authenticationBean.passwordMatches(userAccount, currentPassword)) {
      context.addMessage(null, new FacesMessage('The current password does not match.'))
      return 'change_password'
    }

    try {
      userAccountBean.changePassword(userAccount, this.password)
    } catch (BusinessLogicException e) {
      LOGGER.log(Level.INFO, e.getMessage(), e)
      context.addMessage(null, new FacesMessage(e.getMessage()))
      return 'change_password'
    }
    'profile?faces-redirect=true'
  }
}
