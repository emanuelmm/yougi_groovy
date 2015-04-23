/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
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
import org.yougi.entity.Province
import org.yougi.business.GenericPersistence
import javax.inject.Inject

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
class ProvinceBean implements Serializable {

  @Inject
  private GenericPersistence persistence

  def find(id) {
    persistence.findById(Province, id)
  }

  def save(Province p) {
    Province existPersistence = find(p.id)
    if (existPersistence) {
      persistence.update(p)
    } else {
      persistence.save(p)
    }
  }

  def remove(Province p) {
    persistence.remove(Province, p.id)
  }

  def findAll() {
    def jpql = 'select p from Province p order by p.country.name, p.name asc'
    persistence.findAll(jpql, Province)
  }

  def findByCountry(Country country) {
    def jpql = 'select p from Province p where p.country = ?1 order by p.name asc'
    persistence.findAllWithParam(jpql, country, Province)
  }
}
