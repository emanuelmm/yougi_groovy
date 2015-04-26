/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General License as published by the
 * Free Software Foundation either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.yougi.knowledge.web.model

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.inject.Named

import org.yougi.annotation.ManagedProperty
import org.yougi.business.UserAccountBean
import org.yougi.entity.UserAccount
import org.yougi.knowledge.business.ArticleBean
import org.yougi.knowledge.business.WebSourceBean
import org.yougi.knowledge.entity.Article
import org.yougi.knowledge.entity.WebSource
import org.yougi.util.UrlUtils

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class WebSourceMBean {

  @EJB
  UserAccountBean userAccountBean
  @EJB
  WebSourceBean webSourceBean
  @EJB
  ArticleBean articleBean
  WebSource webSource
  def webSources
  def publishedArticles
  def membersWithWebsite
  String selectedMember
  String website
  @Inject
  @ManagedProperty('#{param.id}')
  String id

  @Inject
  UnpublishedArticlesMBean unpublishedArticlesMBean

  List<WebSource> getWebSources() {
    if(webSources == null) {
      webSources = webSourceBean.findWebResources()
    }
    webSources
  }

  List<UserAccount> getMembersWithWebsite() {
    if(membersWithWebsite == null) {
      membersWithWebsite = webSourceBean.findNonReferencedProviders()
    }
    membersWithWebsite
  }

  String getWebsite() {
    if(selectedMember && website == null) {
      webSource.provider = userAccountBean.find(selectedMember)
      website = webSource.provider.website
      website = UrlUtils.setProtocol(website)
    }
    website
  }

  String getTitle() {
    if(selectedMember && webSource.title == null) {
      webSource = webSourceBean.loadWebSource(webSource)
    }
    webSource.title
  }

  String getFeed() {
    if(selectedMember && webSource.feed == null) {
      webSource = webSourceBean.loadWebSource(webSource)
    }
    webSource.feed
  }

  void updateWebSource() {
    webSource = webSourceBean.loadWebSource(website)
    UserAccount userAccount = userAccountBean.findByWebsite(website)
    if(userAccount) {
      selectedMember = userAccount.id
    }
  }

  List<Article> getPublishedArticles() {
    if(publishedArticles == null) {
      publishedArticles = articleBean.findPublishedArticles(webSource)
    }
    publishedArticles
  }

  List<Article> getUnpublishedArticles() {
    if(unpublishedArticlesMBean.unpublishedArticles == null) {
      unpublishedArticlesMBean.unpublishedArticles = webSourceBean.loadUnpublishedArticles(webSource)
    }
    unpublishedArticlesMBean.unpublishedArticles
  }

  @PostConstruct
  void load() {
    if(id) {
      webSource = webSourceBean.find(id)
      if(webSource.provider) {
        selectedMember = webSource.provider.id
      }
      unpublishedArticlesMBean.webSource = webSource
    } else {
      webSource = new WebSource()
    }
  }

  String save() {
    if(selectedMember) {
      webSource.provider = userAccountBean.find(selectedMember)
    }
    webSourceBean.save(webSource)
    'web_sources'
  }

  String remove() {
    webSourceBean.remove(webSource.id)
    'web_sources?faces-redirect=true'
  }
}
