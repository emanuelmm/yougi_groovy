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
package org.yougi.entity

import javax.persistence.*

/**
 * Represents the allocation of users in groups.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = 'user_group')
class UserGroup implements Serializable {

  @EmbeddedId
  UserGroupId id

  @ManyToOne
  @JoinColumn(name='user_id', insertable = false, updatable = false)
  UserAccount userAccount

  @ManyToOne
  @JoinColumn(name='group_id', insertable = false, updatable = false)
  AccessGroup accessGroup

  String username

  @Column(name = 'group_name')
  String groupName

  UserGroup() {
  }

  UserGroup(AccessGroup accessGroup, Authentication authentication) {
    this.accessGroup = accessGroup
    this.userAccount = authentication.getUserAccount()
    this.id = new UserGroupId(this.accessGroup.getId(), this.userAccount.getId())
    this.username = authentication.getUsername()
    this.groupName = this.accessGroup.getName()
  }

  void setAuthentication(Authentication authentication) {
    userAccount = authentication.getUserAccount()
    if(id == null) {
      id = new UserGroupId()
    }
    id.setUserId(userAccount.id)
    username = authentication.username
  }

  void setAccessGroup(AccessGroup accessGroup) {
    this.accessGroup = accessGroup
    if(id == null) {
      id = new UserGroupId()
    }
    id.setGroupId(accessGroup.id)
    groupName = accessGroup.name
  }

  @Override
  String toString() {
    username
  }
}
