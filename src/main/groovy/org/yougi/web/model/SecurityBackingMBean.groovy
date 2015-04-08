/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General Public License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.yougi.web.model

import org.yougi.annotation.ManagedProperty
import org.yougi.business.UserAccountBean
import org.yougi.reference.Role
import org.yougi.util.ResourceBundleHelper

import groovy.transform.CompileStatic

import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.faces.application.FacesMessage
import javax.faces.context.FacesContext
import javax.inject.Inject
import javax.inject.Named
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class SecurityBackingMBean {

  private static final Logger LOGGER = Logger.getLogger(this.simpleName)

  @EJB
  private UserAccountBean userAccountBean

  String username
  String password

  @ManagedProperty(value='#{sessionScope}')
  Map<String, Object> sessionMap

  @Inject
  private FacesContext context

  @Inject
  private HttpServletRequest request

  boolean isUserSignedIn() {
    return sessionMap.containsKey('signedUser')
  }

  String login() {
    println 'HAIL !!!'
    if(userAccountBean.thereIsNoAccount()) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.getMessage('infoFirstUser'), ''))
      return '/registration'
    } else {
      return '/login?faces-redirect=true'
    }
  }

  String register() {
    if(userAccountBean.thereIsNoAccount()) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.getMessage('infoFirstUser'), ''))
      return '/registration'
    } else {
      return '/registration?faces-redirect=true'
    }
  }

  /**
   * Perform the logout of the user by removing the user from the session and
   * destroying the session.
   * @return The next step in the navigation flow.
   */
  String logout() {
    HttpSession session = (HttpSession) context.externalContext.getSession(false)
    try {
      request.logout()
      session.invalidate()
    } catch(ServletException se) {
      LOGGER.log(Level.INFO, se.message, se)
      return '/index?faces-redirect=true'
    }
    return '/index?faces-redirect=true'
  }

  Boolean getIsUserAdministrator() {
    return isUserInRole(Role.ADMIN)
  }

  Boolean getIsUserLeader() {
    return isUserInRole(Role.LEADER)
  }

  Boolean getIsUserHelper() {
    return isUserInRole(Role.HELPER)
  }

  Boolean getIsUserPartner() {
    return isUserInRole(Role.PARTNER)
  }

  Boolean getIsUserSpeaker() {
    return isUserInRole(Role.SPEAKER)
  }

  private Boolean isUserInRole(Role role) {
    return request.isUserInRole(role.toString())
  }
}
