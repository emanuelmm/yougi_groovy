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
import org.yougi.entity.UserAccount

import javax.persistence.*
import java.io.Serializable

/**
 * Person with knowledge and experience to give a speech in an event, respecting
 * the scope of subjects in the domain explored by the user group event.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = 'speaker')
class Speaker implements Serializable, Identified {

  @Id
  String id

  @ManyToOne
  @JoinColumn(name = 'user_account', nullable=false)
  UserAccount userAccount

  @Column(name = 'short_cv')
  String shortCv

  String experience
  String organization

  String getFullName() {
    if(userAccount) {
      userAccount.getFullName()
    }
    null
  }

  @Override
  String toString() {
    getFullName()
  }
}
