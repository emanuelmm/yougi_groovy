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
package org.yougi.web.model

import org.yougi.business.CityBean
import org.yougi.business.TimezoneBean
import org.yougi.business.UserAccountBean
import org.yougi.entity.*
import org.yougi.annotation.ManagedProperty

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.inject.Named
import javax.persistence.metamodel.StaticMetamodel

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class CityMBean implements Serializable {

  @EJB
  CityBean cityBean
  @EJB
  TimezoneBean timezoneBean
  @EJB
  UserAccountBean userAccountBean

  @Inject
  @ManagedProperty('#{param.id}')
  String id

  @Inject
  LocationMBean locationMBean

  City city

  def cities
  def timezones

  CityMBean() {
    city = new City()
  }

  def getCities() {
    if(!cities) {
      cities = cityBean.findAll()
    }
    cities
  }

  def getInhabitants() {
    userAccountBean.findInhabitantsFrom(city)
  }

  def getTimezones() {
    if(!timezones) {
      timezones = timezoneBean.findTimezones()
    }
    timezones
  }

  @PostConstruct
  void load() {
    if (id) {
      city = cityBean.find(id)
      locationMBean.initialize()
      if (city.country) {
        locationMBean.setSelectedCountry(city.country.acronym)
      }

      if (city.province) {
        locationMBean.setSelectedProvince(city.province.id)
      }
    }
  }

  String save() {
    Country country = locationMBean.getCountry()
    if (country) {
      city.country = country
    }

    Province province = locationMBean.getProvince()
    if (province) {
      city.province = province
    }

    cityBean.save(city)
    'cities?faces-redirect=true'
  }

  String remove() {
    cityBean.remove(city.id)
    'cities?faces-redirect=true'
  }
}
