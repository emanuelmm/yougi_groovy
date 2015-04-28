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

import javax.ejb.Stateless
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
class CommunityMemberBean extends AbstractBean<CommunityMember> {

  @PersistenceContext
  private EntityManager em

  CommunityMemberBean() {
    super(CommunityMember)
  }

  @Override
  protected EntityManager getEntityManager() {
    em
  }

  List<CommunityMember> findBy(Community community) {
    em.createQuery('select cm from CommunityMember cm where cm.community.id = :community', CommunityMember)
      .setParameter('community', community.getId())
      .getResultList()
  }

  List<CommunityMember> findBy(UserAccount member) {
    em.createQuery('select cm from CommunityMember cm where cm.member.id = :member', CommunityMember)
      .setParameter('member', member.getId())
      .getResultList()
  }
}
