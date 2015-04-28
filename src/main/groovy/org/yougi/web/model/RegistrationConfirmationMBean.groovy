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
import org.yougi.util.ResourceBundleHelper
import org.yougi.annotation.ManagedProperty

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.faces.application.FacesMessage
import javax.faces.context.FacesContext
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class RegistrationConfirmationMBean {

  @EJB
  private UserAccountBean userAccountBean
  @Inject
  private FacesContext context

  UserAccount userAccount

  @Inject
  @ManagedProperty('#{param.code}')
  String code
  String informedCode
  Boolean validated

  RegistrationConfirmationMBean() {
    userAccount = new UserAccount()
  }

  /**
   * Validates the registration when the confirmation code comes
   * through query string.
   */
  @PostConstruct
  void load() {
    if(code) {
      userAccount = userAccountBean.confirmUser(code)
      if(userAccount) {
        validated = Boolean.TRUE
      } else {
        validated = Boolean.FALSE
        context.addMessage(informedCode, new FacesMessage(FacesMessage.SEVERITY_WARN, ResourceBundleHelper.getMessage('warnCode0003'), ''))
      }
    }
  }

  /**
   * Validates the registration when the confirmation code is
   * manually informed.
   */
  String confirmUser() {
    if(informedCode) {
      userAccount = userAccountBean.confirmUser(informedCode)
      if(userAccount) {
        validated = Boolean.TRUE
      } else {
        validated = Boolean.FALSE
      }

      if(!validated) {
        context.addMessage(informedCode, new FacesMessage(FacesMessage.SEVERITY_WARN, ResourceBundleHelper.getMessage('warnCode0003'), ''))
      }
    }
    'registration_confirmation'
  }
}
