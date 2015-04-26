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

import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Embeddable
@Access(AccessType.FIELD)
class Address {

  String address
  @ManyToOne
  @JoinColumn(name = 'city')
  City city

  @ManyToOne
  @JoinColumn(name = 'province')
  Province province

  @ManyToOne
  @JoinColumn(name = 'country')
  Country country

  @Column(name = 'postal_code')
  String postalCode

  @Override
  String toString() {
    def fullAddress = ''
    def commaSeparator = ', '
    if (address) {
      fullAddress <<= address
    }

    if (city) {
      if (fullAddress) {
        fullAddress <<= commaSeparator
      }
      fullAddress <<= city.name
    }

    if (province) {
      if (fullAddress) {
        fullAddress <<= commaSeparator
      }
      fullAddress <<= province.name
    }

    if (country) {
      if (fullAddress) {
        fullAddress <<= ' - '
      }
      fullAddress <<= country.name
    }

    if (postalCode) {
      if (fullAddress) {
        fullAddress <<= '.'
      }
      fullAddress <<= ' '
      if (country) {
        fullAddress <<= ': '
        fullAddress <<= country.name
      }
    }

    fullAddress
  }
}
