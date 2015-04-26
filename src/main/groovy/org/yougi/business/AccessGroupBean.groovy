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

import org.yougi.entity.*
import org.yougi.exception.BusinessLogicException
import org.yougi.util.EntitySupport

import javax.ejb.EJB
import javax.ejb.Stateless
import javax.mail.MessagingException
import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.persistence.PersistenceContext
import javax.persistence.PersistenceException
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
class AccessGroupBean extends AbstractBean<AccessGroup> {

  private static final Logger LOGGER = Logger.getLogger(AccessGroupBean.class.getSimpleName())

  @PersistenceContext
  private EntityManager em
  @EJB
  private AuthenticationBean authenticationBean
  @EJB
  private UserGroupBean userGroupBean
  @EJB
  private MessengerBean messengerBean
  @EJB
  private MessageTemplateBean messageTemplateBean

  public static final String ADMIN_GROUP = 'admins'
  public static final String DEFAULT_GROUP = 'members'

  AccessGroupBean() {
    super(AccessGroup)
  }

  @Override
  protected EntityManager getEntityManager() {
    em
  }

  AccessGroup findAccessGroupByName(String name) {
    AccessGroup ag
    try {
      def jpql = 'select ag from AccessGroup ag where ag.name = :name'
      ag = em.createQuery(jpql, AccessGroup).setParameter('name', name).getSingleResult()
    } catch(NoResultException nre) {
      LOGGER.log(Level.INFO, nre.getMessage())
    }
    ag
  }

  AccessGroup findDefaultAccessGroup() {
    AccessGroup defaultUserGroup
    try {
      defaultUserGroup = em.createQuery('select ag from AccessGroup ag where ag.userDefault = :default', AccessGroup)
        .setParameter('default', Boolean.TRUE)
        .getSingleResult()
    } catch(NoResultException nre) {
      LOGGER.log(Level.INFO, nre.getMessage())
      defaultUserGroup = new AccessGroup(DEFAULT_GROUP,'Default Members Group')
      defaultUserGroup.setId(EntitySupport.generateEntityId())
      defaultUserGroup.setUserDefault(Boolean.TRUE)
      em.persist(defaultUserGroup)
    }
    defaultUserGroup
  }

  /** Returns the existing administrative group. If it doesn't find anyone
   *  then a new one is created and returned. */
  AccessGroup findAdministrativeGroup() {
    AccessGroup group
    try {
      group = em.createQuery('select ag from AccessGroup ag where ag.name = :name', AccessGroup)
        .setParameter('name', ADMIN_GROUP)
        .getSingleResult()
    } catch(NoResultException nre) {
      LOGGER.log(Level.INFO, nre.getMessage())
      group = new AccessGroup(ADMIN_GROUP,'Administrators Group')
      group.setId(EntitySupport.generateEntityId())
      em.persist(group)
    }
    group
  }

  List<AccessGroup> findAccessGroups() {
    em.createQuery('select ag from AccessGroup ag order by ag.name', AccessGroup).getResultList()
  }

  void sendGroupAssignmentAlert(UserAccount userAccount, AccessGroup accessGroup) throws BusinessLogicException {
    MessageTemplate messageTemplate = messageTemplateBean.find('09JDIIE82O39IDIDOSJCHXUDJJXHCKP0')
    messageTemplate.setVariable('userAccount.firstName', userAccount.firstName)
    messageTemplate.setVariable('accessGroup.name', accessGroup.name)
    EmailMessage emailMessage = messageTemplate.buildEmailMessage()
    emailMessage.setRecipient(userAccount)

    try {
      messengerBean.sendEmailMessage(emailMessage)
    } catch(MessagingException me) {
      LOGGER.log(Level.WARNING, 'Error when sending the group assignment alert to '+ userAccount.fullName, me)
    }
  }

  @Override
  AccessGroup save(AccessGroup entity) {
    throw new BusinessLogicException('Please use the save method that accepts the list of members by parameter')
  }

  AccessGroup save(AccessGroup accessGroup, List<UserAccount> members) {
    if (accessGroup.getUserDefault()) {
      AccessGroup defaultGroup = findDefaultAccessGroup()
      defaultGroup.setUserDefault(false)
    }

    if (EntitySupport.isIdValid(accessGroup.id)) {
      try {
        AccessGroup group = findAccessGroupByName(accessGroup.name)
        if (group) {
          throw new PersistenceException('A group named '+ accessGroup.name +' already exists.')
        }
      } catch(NoResultException nre) {
        LOGGER.log(Level.INFO, nre.message)
        accessGroup.id = EntitySupport.generateEntityId()
        em.persist(accessGroup)
      }
    } else {
      accessGroup = em.merge(accessGroup)
    }

    if (members) {
      Authentication auth
      List<UserGroup> usersGroup = new ArrayList<>()
      for (UserAccount member: members) {
        auth = authenticationBean.findByUserId(member.id)
        usersGroup.add(new UserGroup(accessGroup, auth))
      }
      userGroupBean.update(accessGroup, usersGroup)
    }
    accessGroup
  }
}
