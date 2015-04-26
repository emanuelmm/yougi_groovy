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

import org.yougi.knowledge.business.ArticleBean
import org.yougi.knowledge.business.WebSourceBean
import org.yougi.knowledge.entity.Article
import org.yougi.annotation.ManagedProperty

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class ArticleMBean {

  @EJB
  private ArticleBean articleBean
  @EJB
  private WebSourceBean webSourceBean

  Article article
  List<Article> articlesFromSameSource
  List<Article> otherPublishedArticles

  @Inject
  @ManagedProperty('#{param.id}')
  String id

  @Inject
  @ManagedProperty('#{param.pl}')
  String permanentLink

  @Inject
  UnpublishedArticlesMBean unpublishedArticlesMBean

  List<Article> getArticlesFromSameSource() {
    if(articlesFromSameSource == null) {
      articlesFromSameSource = articleBean.findPublishedArticles(article)
    }
    articlesFromSameSource
  }

  List<Article> getOtherPublishedArticles() {
    if(otherPublishedArticles == null) {
      otherPublishedArticles = articleBean.findOtherPublishedArticles(article.webSource)
    }
    otherPublishedArticles
  }

  @PostConstruct
  void load() {
    if(id) {
      article = articleBean.find(id)
    } else if(permanentLink) {
      article = unpublishedArticlesMBean.getArticle(permanentLink)
    } else {
      article = new Article()
    }
  }

  String publish() {
    unpublishedArticlesMBean.removeArticle(article)
    articleBean.publish(article)
    'web_source?faces-redirect=true&id='+ article.webSource.id
  }

  String unpublish() {
    articleBean.unpublish(article)
    unpublishedArticlesMBean.addArticle(article)
    'web_source?faces-redirect=true&id='+ article.webSource.id
  }

  String save() {
    articleBean.save(article)
    'web_source?faces-redirect=true&id='+ article.webSource.id
  }

  void reloadFromSource() {
    article = articleBean.find(article.id)
    article = webSourceBean.loadOriginalArticle(article)
    article.setPublished(true)
  }
}
