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
package org.yougi.web.model

import org.yougi.business.CityBean
import org.yougi.business.TimezoneBean
import org.yougi.business.UserAccountBean
import org.yougi.entity.*
import org.yougi.annotation.ManagedProperty

import groovy.transform.CompileStatic;

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.inject.Named
import javax.persistence.metamodel.StaticMetamodel;

import java.io.Serializable
import java.util.List

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class CityMBean implements Serializable {
	private static final long serialVersionUID = 1L

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
		this.city = new City()
	}

	def getCities() {
		if(!this.cities) {
			this.cities = cityBean.findAll()
		}
		this.cities
	}

	def getInhabitants() {
		userAccountBean.findInhabitantsFrom(this.city)
	}

	def getTimezones() {
		if(!this.timezones) {
			this.timezones = timezoneBean.findTimezones()
		}
		this.timezones
	}

	@PostConstruct
	void load() {
		if (this.id) {
			this.city = cityBean.find(id)

			locationMBean.initialize()

			if (this.city.country) {
				locationMBean.setSelectedCountry(this.city.country.acronym)
			}

			if (this.city.province) {
				locationMBean.setSelectedProvince(this.city.province.id)
			}
		}
	}

	String save() {
		Country country = this.locationMBean.getCountry()
		if (country) {
			this.city.country = country
		}

		Province province = this.locationMBean.getProvince()
		if (province) {
			this.city.province = province
		}

		cityBean.save(this.city)

		'cities?faces-redirect=true'
	}

	String remove() {
		cityBean.remove(city.id)

		'cities?faces-redirect=true'
	}
}