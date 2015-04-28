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

import org.yougi.business.UserAccountBean
import org.yougi.entity.UserAccount
import org.yougi.exception.BusinessLogicException
import org.yougi.util.ResourceBundleHelper
import org.yougi.annotation.ManagedProperty

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.faces.application.FacesMessage
import javax.faces.context.FacesContext
import javax.inject.Inject
import javax.inject.Named
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class ChangeEmailMBean {

  private static final Logger LOGGER = Logger.getLogger(ChangeEmailMBean.class.getSimpleName())

  @EJB
  private UserAccountBean userAccountBean

  @Inject
  @ManagedProperty('#{param.cc}')
  String confirmationCode

  @Inject
  UserProfileMBean userProfileMBean

  UserAccount userAccount
  String currentEmail
  String newEmail
  String newEmailConfirmation

  ChangeEmailMBean() {
    userAccount = new UserAccount()
  }

  @PostConstruct
  void load() {
    if (!confirmationCode) {
      userAccount = userProfileMBean.userAccount
    }
  }

  /**
   * Compares the informed email with its respective confirmation.
   *
   * @return true if the email matches with its confirmation.
   */
  private boolean isEmailConfirmed() {
    return newEmail.equals(newEmailConfirmation)
  }

  /**
   * It changes the user's email.
   *
   * @return returns the next step in the navigation flow.
   */
  String changeEmail() {

    // If password doesn't match its confirmation.
    if (!isEmailConfirmed()) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage('The email confirmation does not match.'))
      return 'change_password'
    }

    try {
      userAccountBean.changeEmail(userAccount, this.newEmail)
    } catch (BusinessLogicException e) {
      LOGGER.log(Level.INFO, e.getMessage(), e)
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ResourceBundleHelper.getMessage(e.getMessage())))
      return 'change_email'
    }
    'profile?faces-redirect=true'
  }

  String confirmEmailChange() {
    try {
      userAccountBean.confirmEmailChange(confirmationCode)
    } catch(BusinessLogicException e) {
      LOGGER.log(Level.INFO, e.getMessage(), e)
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ResourceBundleHelper.getMessage(e.getMessage())))
      return 'change_email_confirmation'
    }
    'index?faces-redirect=true'
  }
}
