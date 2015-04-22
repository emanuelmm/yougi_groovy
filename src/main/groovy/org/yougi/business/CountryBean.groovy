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

import org.yougi.entity.Country

import javax.ejb.Stateless
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.util.List
import javax.transaction.Transactional
import java.io.Serializable

/**
 * Manages data of countries, states or provinces and cities because these
 * three entities are strongly related and because they are too simple to
 * have an exclusive business class.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Transactional
class CountryBean implements Serializable {

  @PersistenceContext
  private EntityManager em

  Country findCountry(String acronym) {
    def obj = null
    if (acronym) {
      obj = em.find(Country, acronym)
    }
    obj
  }

  List<Country> findCountries() {
    def jpql = 'select c from Country c order by c.name asc'
    em.createQuery(jpql, Country).getResultList()
  }

  List<Country> findAssociatedCountries() {
    def jpql = 'select distinct p.country from Province p order by p.country asc'
    em.createQuery(jpql, Country).getResultList()
  }

  void saveCountry(Country country) {
    em.merge(country)
  }

  void removeCountry(String id) {
    Country country = em.find(Country, id)
    if (country) {
      em.remove(country)
    }
  }
}
