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
package org.yougi.event.entity

import org.yougi.entity.Identified
import org.yougi.entity.PublicContent

import javax.persistence.*
import javax.xml.bind.annotation.XmlRootElement
import java.io.Serializable

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = 'event')
@XmlRootElement
class Event implements Serializable, Identified, PublicContent {

  @Id
  String id
  String name

  @ManyToOne
  @JoinColumn(name = 'parent')
  Event parent

  @Column(name = 'start_date')
  @Temporal(javax.persistence.TemporalType.DATE)
  Date startDate

  @Column(name = 'start_time')
  @Temporal(javax.persistence.TemporalType.TIME)
  Date startTime

  @Column(name = 'end_date')
  @Temporal(javax.persistence.TemporalType.DATE)
  Date endDate

  @Column(name = 'end_time')
  @Temporal(javax.persistence.TemporalType.TIME)
  Date endTime

  String description

  @Column(name = 'short_description')
  String shortDescription

  @Column(name = 'certificate_template')
  String certificateTemplate

  @Transient
  List<Venue> venues

  @Override
  String getSummary() {
    if(shortDescription) {
      return shortDescription
    } else {
      return description
    }
  }

  @Override
  String getContent() {
    shortDescription
  }

  @Override
  public String getUrl() {
    '/event/event'
  }

  String getAuthor() {
    null
  }

  @Override
  String toString() {
    (this.parent != null? this.parent.toString() + ' - ' : '') + this.name
  }
}
