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

import javax.annotation.PostConstruct
import javax.enterprise.context.RequestScoped
import javax.faces.context.ExternalContext
import javax.faces.context.FacesContext
import javax.inject.Named

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class ErrorMBean {

  private final ExternalContext externalContext = FacesContext.currentInstance.externalContext
  private Throwable throwable

  boolean getThrowableExists() {
    throwable != null
  }

  def getStatusCode() {
    externalContext.requestMap['javax.servlet.error.status_code']
  }

  def getMessage() {
    externalContext.requestMap['javax.servlet.error.message']
  }

  def getExceptionType() {
    externalContext.requestMap['javax.servlet.error.exception_type']
  }

  def getStackTrace() {
    buildStackTrace(throwable, '')
  }

  private String buildStackTrace(Throwable throwable, String stackTrace) {
    def stackContent = stackTrace
    if (!throwable) {
      stackContent = '-'
    }

    def caused = '\n...has caused: '
    def indentation = '   '
    if (throwable.cause) {
      stackContent += buildStackTrace(throwable.cause, stackContent)
    } else {
      caused = '\nRoot Cause: '
    }

    if (throwable.message) {
      StackTraceElement[] elements = throwable.stackTrace
      stackContent += caused
      stackContent += throwable.class.name
      stackContent += ': '
      stackContent += throwable.message
      stackContent += '\n'
      for (StackTraceElement e : elements) {
        stackContent += indentation
        stackContent += e.className
        stackContent += ':'
        stackContent += e.methodName
        stackContent += ':'
        stackContent += e.lineNumber
        stackContent += '\n'
      }
    }

    stackContent
  }

  def getRequestURI() {
    externalContext.requestMap['javax.servlet.error.request_uri']
  }

  def getServletName() {
    externalContext.requestMap['javax.servlet.error.servlet_name']
  }

  @PostConstruct
  void load() {
    throwable = (Throwable) externalContext.requestMap['javax.servlet.error.exception']

    def content = ['Code: ', getStatusCode(), '\n',
                   getExceptionType(), '\n',
                   'Description: ', getMessage(), '\n',
                   'Page: ', getRequestURI(), '\n',
                   'Trace: \n',
                   getStackTrace()]

    def body = ''
    for (i in content) {
      body += i
    }

    //EmailMessage emailMessage = new EmailMessage(this.getMessage(), body.toString(), )
  }
}
