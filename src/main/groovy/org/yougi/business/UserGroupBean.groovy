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
package org.yougi.business

import org.yougi.entity.AccessGroup
import org.yougi.entity.UserAccount
import org.yougi.entity.UserGroup

import javax.ejb.Stateless
import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.persistence.PersistenceContext
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Business logic to manage the relationship between users and access groups.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
class UserGroupBean {

  private static final Logger LOGGER = Logger.getLogger(UserGroupBean.class.getSimpleName())

  @PersistenceContext
  private EntityManager em

  List<UserAccount> findUsersGroup(AccessGroup accessGroup) {
    em.createQuery('select ug.userAccount from UserGroup ug where ug.accessGroup = :accessGroup order by ug.userAccount.firstName', UserAccount)
      .setParameter('accessGroup', accessGroup)
      .getResultList()
  }

  List<UserGroup> findUsersGroups(AccessGroup accessGroup) {
    em.createQuery('select ug from UserGroup ug where ug.accessGroup = :accessGroup', UserGroup)
      .setParameter('accessGroup', accessGroup)
      .getResultList()
  }

  /**
   * @param userAccount the user account that is member of one or more groups.
   * @return the list of groups registrations of the informed user account.
   */
  List<UserGroup> findUserGroups(UserAccount userAccount) {
    em.createQuery('select ug from UserGroup ug where ug.userAccount = :userAccount', UserGroup)
      .setParameter('userAccount', userAccount)
      .getResultList()
  }

  boolean isUserPartOfGroup(UserGroup userGroup) {
    isUserPartOfGroup(userGroup.getUserAccount(), userGroup.getAccessGroup())
  }

  boolean isUserPartOfGroup(UserAccount userAccount, AccessGroup accessGroup) {
    try {
      UserGroup userGroup = em.createQuery('select ug from UserGroup ug where ug.userAccount = :userAccount and ug.accessGroup = :accessGroup', UserGroup)
        .setParameter('userAccount', userAccount)
        .setParameter('accessGroup', accessGroup)
        .getSingleResult()
      return userGroup != null
    } catch (NoResultException nre) {
      LOGGER.log(Level.INFO, nre.getMessage())
      return false
    }
  }

  void update(AccessGroup accessGroup, List<UserGroup> userGroups) {
    if(userGroups.isEmpty()) {
      em.createQuery('delete from UserGroup ug where ug.accessGroup = :accessGroup')
        .setParameter('accessGroup', accessGroup)
        .executeUpdate()
      return
    }

    List<UserGroup> currentUserGroups = findUsersGroups(accessGroup)

    for(UserGroup userGroup: currentUserGroups) {
      if(!userGroups.contains(userGroup)) {
        em.remove(userGroup)
      }
    }

    for(UserGroup userGroup: userGroups) {
      if(!currentUserGroups.contains(userGroup)) {
        em.persist(userGroup)
      }
    }
  }

  void removeUserFromAllGroups(UserAccount userAccount) {
    em.createQuery('delete from UserGroup ug where ug.userAccount = :userAccount')
      .setParameter('userAccount', userAccount)
      .executeUpdate()
  }

  void add(UserGroup userGroup) {
    if(!isUserPartOfGroup(userGroup)) {
      em.persist(userGroup)
    }
  }

  /**
   * Change the username of the user in all groups that it is part of.
   * @param userAccount the user account whose username is going to change.
   */
  void changeUsername(UserAccount userAccount) {
    List<UserGroup> usersGroups = findUserGroups(userAccount)
    for(UserGroup userGroup: usersGroups) {
      userGroup.setUsername(userAccount.email)
    }
  }
}
