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
package org.yougi.knowledge.entity

import org.yougi.entity.Identified
import org.yougi.entity.UserAccount

import javax.persistence.*

/**
 * Entity class representing an external website used as a source of content for
 * the community.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = 'web_source')
class WebSource implements Serializable, Identified {

  @Id
  String id
  String title
  String feed

  @ManyToOne
  @JoinColumn(name='provider')
  UserAccount provider

  @Transient
  List<Article> articles

  void addArticle(Article article) {
    if(articles == null) {
      articles = new ArrayList<>()
    }
    articles.add(article)
  }

  void removeArticle(Article article) {
    if(article && articles) {
      articles.remove(article)
    }
  }

  @Override
  String toString() {
    title
  }
}
