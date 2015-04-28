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
package org.yougi.knowledge.web.model

import org.yougi.knowledge.business.WebSourceBean
import org.yougi.knowledge.entity.Article
import org.yougi.knowledge.entity.WebSource

import javax.ejb.EJB
import javax.enterprise.context.SessionScoped
import javax.inject.Named

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@SessionScoped
class UnpublishedContentMBean implements Serializable {

  WebSource webSource

  @EJB
  private WebSourceBean webSourceBean

  WebSource getWebSource() {
    webSource
  }

  void setWebSource(WebSource webSource) {
    if(webSource == null || !webSource.equals(webSource)) {
      reset()
    }
    webSource = webSource
  }

  void reset() {
    webSource = null
  }

  List<Article> getArticles() {
    webSource.getArticles()
  }

  Article getArticle(String permanentLink) {
    List<Article> articles = webSource.getArticles()
    if(articles == null || permanentLink == null) {
      return null
    }

    Article article = new Article(null, permanentLink)
    for(Article art: articles) {
      if(art.equals(article)) {
        article = art
          break
      }
    }
    article
  }

  void loadWebSource() {
    webSource = webSourceBean.loadWebSource(webSource)
  }

  void addArticle(Article article) {
    article.setId(null)
    article.setPublished(Boolean.FALSE)
    webSource.addArticle(article)
  }

  void removeArticle(Article article) {
    webSource.removeArticle(article)
  }
}
