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

import org.yougi.entity.City
import org.yougi.entity.Country
import org.yougi.entity.Identified
import org.yougi.entity.Province

import javax.persistence.*
import java.io.Serializable

/**
 * Venue is the place where an event is organized.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = 'venue')
class Venue implements Serializable, Identified {

  @Id
  String id
  String name
  String address
  @ManyToOne
  @JoinColumn(name = 'country')
  Country country
  @ManyToOne
  @JoinColumn(name = 'province')
  Province province
  @ManyToOne
  @JoinColumn(name = 'city')
  City city
  String latitude
  String longitude
  String website

  String getLocation() {
    StringBuilder location = new StringBuilder()
    String separator = ''
    if (city) {
      location.append(city.getName())
      separator = ', '
    }

    if (country) {
      location.append(separator)
      location.append(country.getName())
    }
    location.toString()
  }

  @Override
  String toString() {
    name
  }
}
