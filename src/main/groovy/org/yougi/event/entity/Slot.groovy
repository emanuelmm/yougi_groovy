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
 * Enforce the size of sessions within a event. If an event has slots it means
 * that the event should have sessions exactly at the size of one of its slots.
 * If an event doesn't have slots, it means that its sessions can have any sort
 * of time allocation. Slots heavily contribute to simplify the conference
 * planning.
 *
 * A slot should respect the time constraints imposed by its event. Therefore,
 * the date, start time and end time should be within the event interval.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name='slot')
class Slot implements Serializable, Identified {

  @Id
  String id

  @ManyToOne
  @JoinColumn(name = 'event')
  Event event

  @Column(name = 'date_slot')
  @Temporal(TemporalType.DATE)
  Date when

  @Column(name = 'start_time')
  @Temporal(TemporalType.TIME)
  Date startTime

  @Column(name = 'end_time')
  @Temporal(TemporalType.TIME)
  Date endTime

}
