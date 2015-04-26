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
import org.yougi.entity.PublicContent

import javax.persistence.*
import javax.xml.bind.annotation.XmlRootElement
import java.io.Serializable

/**
 * Entity class representing an extracted article from a web source, making it
 * permanently available for publication.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = 'article')
@XmlRootElement
class Article implements Serializable, Identified, PublicContent {

  @Id
  String id
  String title
  String author

  @ManyToOne
  @JoinColumn(name='web_source')
  WebSource webSource

  String content
  String summary

  @Column(name='perm_link')
  String permanentLink

  String topics

  @Temporal(javax.persistence.TemporalType.DATE)
  Date publication

  Boolean published

  @Override
  String getUrl() {
    '/knowledge/article'
  }

  /**
   * @return Returns the content if it exists or the summary if the content
   * doesn't exist.
   */
  String getText() {
    if(!content) {
      return summary
    } else {
      return content
    }
  }

  @Override
  String toString() {
    title
  }
}
