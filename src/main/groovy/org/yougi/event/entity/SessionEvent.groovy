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

import javax.persistence.*

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = 'session')
class SessionEvent implements Serializable, Identified {

  @Id
  String id

  @ManyToOne
  @JoinColumn(name='event')
  Event event

  String name
  String description

  @Column(name='detailed_description')
  String detailedDescription

  @Enumerated(EnumType.STRING)
  @Column(name='experience_level')
  ExperienceLevel experienceLevel

  String topics

  @Column(name = 'start_date')
  @Temporal(TemporalType.DATE)
  Date startDate

  @Column(name = 'end_date')
  @Temporal(TemporalType.DATE)
  Date endDate

  @Column(name = 'start_time')
  @Temporal(TemporalType.TIME)
  Date startTime

  @Column(name = 'end_time')
  @Temporal(TemporalType.TIME)
  Date endTime

  @ManyToOne
  @JoinColumn(name='room')
  Room room

  @ManyToOne
  @JoinColumn(name='track')
  Track track

  Boolean approved

  @Transient
  List<Speaker> speakers

  @Override
  String toString() {
    name
  }
}
