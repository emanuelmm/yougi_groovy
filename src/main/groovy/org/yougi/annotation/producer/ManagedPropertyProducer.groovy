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
package org.yougi.annotation.producer

import org.yougi.annotation.ManagedProperty

import javax.el.ELContext
import javax.el.ExpressionFactory
import javax.el.ValueExpression
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces
import javax.enterprise.inject.spi.InjectionPoint
import javax.faces.application.Application
import javax.faces.context.FacesContext

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
class ManagedPropertyProducer {

  @Produces @ManagedProperty('') @Dependent
  String getStringManagedProperty(InjectionPoint injectionPoint) {
    (String) getObjectManagedProperty(injectionPoint, String)
  }

  private Object getObjectManagedProperty(InjectionPoint injectionPoint, Class expectedType) {
    String value = injectionPoint.getAnnotated().getAnnotation(ManagedProperty).value()
    FacesContext context = FacesContext.currentInstance
    Application application = context.getApplication()
    ExpressionFactory ef = application.getExpressionFactory()
    ELContext elContext = context.getELContext()
    ValueExpression ve = ef.createValueExpression(elContext, value, expectedType)
    ve.getValue(elContext)
  }
}
