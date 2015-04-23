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

import javax.transaction.Transactional
import javax.inject.Inject
import org.yougi.business.GenericPersistence

/**
 * Manages data of countries, states or provinces and cities because these
 * three entities are strongly related and because they are too simple to
 * have an exclusive business class.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Transactional
class CountryBean implements Serializable {

  @Inject
  GenericPersistence persistence

  Country findCountry(String acronym) {
    def obj = null
    if (acronym) {
      obj = persistence.findById(Country, acronym)
    }
    obj
  }

  def findCountries() {
    persistence.findAll(Country, true, 'name')
  }

  List<Country> findAssociatedCountries() {
    def jpql = 'select distinct p.country from Province p order by p.country asc'
    persistence.findAll(jpql, Country)
  }

  void saveCountry(Country country) {
    Country c = persistence.findById(Country, country.acronym)
    if (c) {
      persistence.update(country)
    } else {
      persistence.save(country)
    }
  }

  void removeCountry(String id) {
    persistence.remove(Country, id)
  }
}
