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

import org.yougi.util.EntitySupport
import org.yougi.entity.Identified

import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Order
import javax.persistence.criteria.Root

@Transactional
class GenericPersistence implements Serializable {

  @PersistenceContext
  private EntityManager em

  public <T> T findById(Class<T> t, Object id) {
    em.find(t, id)
  }

  public <T> T save(T t) {
    if (t instanceof Identified) {
      if (EntitySupport.isIdNotValid(t)) {
        t.setId(EntitySupport.generateEntityId())
      }
    }
    em.persist(t)
    em.flush()
    t
  }

  void remove(Class<?> klass, Serializable id) {
    Object ref = em.getReference(klass, id)
    em.remove(ref)
  }

  public <T> T update(T t) {
    em.merge(t)
  }

  public <T> List<T> findAll(String jpql, Class<T> klass) {
    em.createQuery(jpql, klass).getResultList()
  }

  public <T> List<T> findAll(Class<T> klass) {
    CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(klass)
    cq.select(cq.from(klass))
    em.createQuery(cq).getResultList()
  }

  public <T> List<T> findAllWithParam(String jpql, Object param, Class<T> klass) {
    em.createQuery(jpql, klass).setParameter(1, param).getResultList()
  }

  public <T> List<T> findAll(Class<T> klass, boolean asc, String orderBy) {
    CriteriaBuilder cb = em.getCriteriaBuilder()
    CriteriaQuery<T> criteria = cb.createQuery(klass)
    Root<T> root = criteria.from(klass)
    criteria.select(root)
    Order order
    if (asc) {
      order = cb.asc(root.get(orderBy))
    } else {
      order = cb.desc(root.get(orderBy))
    }
    criteria.orderBy(order)
    em.createQuery(criteria).getResultList()
  }

}
