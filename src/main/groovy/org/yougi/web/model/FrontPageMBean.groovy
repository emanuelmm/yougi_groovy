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

import org.yougi.entity.PublicContent
import org.yougi.event.business.EventBean
import org.yougi.event.entity.Event
import org.yougi.knowledge.business.ArticleBean
import org.yougi.knowledge.entity.Article

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.faces.view.ViewScoped
import javax.inject.Named
import java.io.Serializable
import java.util.ArrayList
import java.util.List

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@ViewScoped
class FrontPageMBean implements Serializable {

  private static final long serialVersionUID = 1L

  @EJB
  EventBean eventBean
  @EJB
  ArticleBean articleBean
  List<PublicContent> publicContents
  PublicContent mainPublicContent

  FrontPageMBean() {
    publicContents = new ArrayList<>()
  }

  List<PublicContent> getPublicContents() {
    publicContents
  }

  PublicContent getMainPublicContent() {
    mainPublicContent
  }

  @PostConstruct
  void init() {
    List<Event> comingEvents = eventBean.findUpCommingEvents()
    List<Article> publishedArticles = articleBean.findPublishedArticles()
    publicContents.addAll(comingEvents)
    publicContents.addAll(publishedArticles)

    if(publicContents) {
      mainPublicContent = publicContents.remove(0)
    }
  }
}
