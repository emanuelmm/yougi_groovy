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
import org.yougi.business.TimezoneBean
import org.yougi.entity.Timezone
import org.yougi.reference.Properties
import org.yougi.util.ResourceBundleHelper as RBH
import org.yougi.util.StringUtils

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.faces.application.FacesMessage
import javax.faces.context.FacesContext
import javax.inject.Inject
import javax.inject.Named
import javax.servlet.http.HttpServletRequest
import java.io.Serializable
import java.util.List
import java.util.Map

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
public class ApplicationPropertiesMBean implements Serializable {

  @EJB
  private ApplicationPropertyBean applicationPropertyBean
  @EJB
  TimezoneBean timezoneBean
  @Inject
  FacesContext context
  @Inject
  HttpServletRequest request
  def applicationProperties = [:]
  Boolean sendEmails
  Boolean receiveEmails
  Boolean captchaEnabled
  List<Timezone> timezones

  List<Timezone> getTimezones() {
    if(timezones == null) {
      timezones = timezoneBean.findTimezones()
    }
    timezones
  }

  @PostConstruct
  void load() {
    applicationProperties = applicationPropertyBean.findApplicationProperties()
    if (applicationProperties[Properties.URL.key]) {
      applicationProperties[Properties.URL.key] = getUrl()
    }

    sendEmails = applicationProperties[Properties.SEND_EMAILS.key] == 'true' ?: false
    receiveEmails = applicationProperties[Properties.RECEIVE_EMAILS.key] == 'true' ?: false
    captchaEnabled = applicationProperties[Properties.CAPTCHA_ENABLED.key] == 'true' ?: false

    if(applicationProperties[Properties.TIMEZONE.key]) {
      Timezone timezone = timezoneBean.findDefaultTimezone()
      applicationProperties[Properties.TIMEZONE.key] = timezone.id
    }
  }

  String save() {
    applicationProperties[Properties.SEND_EMAILS.key] = sendEmails as String
    applicationProperties[Properties.RECEIVE_EMAILS.key] = receiveEmails as String
    applicationProperties[Properties.CAPTCHA_ENABLED.key] = captchaEnabled as String
    applicationPropertyBean.save(applicationProperties)

    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, RBH.getMessage('infoPropertiesSaved'), ''))
    'properties'
  }

  private String getUrl() {
    String serverName = request.serverName
    int serverPort = request.serverPort
    String contextPath = request.contextPath
    serverName + (serverPort != 80 ? ':' + serverPort : '') + contextPath ?: ''
  }
}
