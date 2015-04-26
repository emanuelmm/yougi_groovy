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
import org.yougi.business.CountryBean
import org.yougi.business.ProvinceBean
import org.yougi.business.TimezoneBean
import org.yougi.entity.City
import org.yougi.entity.Country
import org.yougi.entity.Province
import org.yougi.entity.Timezone

import javax.inject.Inject
import javax.ejb.EJB
import javax.faces.view.ViewScoped
import javax.inject.Named
import java.util.logging.Logger

/**
 * This class is used to manage the update of the fields country, province and
 * city, based on the selection of the user. When the user selects the country,
 * its provinces and cities are listed in the respective fields. When the user
 * selects a province, its cities are listed in the respective field. This class
 * should be used every time at least 2 of the location fields are presented to
 * the user.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 *
 */
@Named
@ViewScoped
class LocationMBean implements Serializable {

  private static final Logger LOGGER = Logger.getLogger(LocationMBean.class.getSimpleName())

  @Inject
  private CountryBean countryBean
  @Inject
  private ProvinceBean provinceBean
  @EJB
  private CityBean cityBean
  @EJB
  private TimezoneBean timezoneBean
  List<Country> countries
  List<Province> provinces
  List<City> cities
  List<Timezone> timezones
  String selectedCountry
  String selectedProvince
  String selectedCity
  String selectedTimeZone
  String cityNotListed
  boolean initialized

  List<Country> getCountries() {
    if(countries == null) {
      countries = countryBean.findCountries()
    }
    countries
  }

  List<Province> getProvinces() {
    if (selectedCountry) {
      Country country = new Country(selectedCountry)
      provinces = provinceBean.findByCountry(country)
      return provinces
    } else {
      return new ArrayList<>()
    }
  }

  List<City> getCities() {
    if (selectedCountry != null && selectedProvince == null) {
      Country country = new Country(selectedCountry)
      cities = cityBean.findByCountry(country, false)
    } else if (selectedProvince != null) {
      Province province = new Province(id:selectedProvince)
      cities = cityBean.findByProvince(province, false)
    }
    cities
  }

  List<Timezone> getTimezones() {
    if(timezones == null) {
      timezones = timezoneBean.findTimezones()
    }
    timezones
  }

  List<String> findCitiesStartingWith(String initials) {
    List<City> cits = cityBean.findStartingWith(initials)
    List<String> citiesStartingWith = new ArrayList<>()
    for (City city : cits) {
      citiesStartingWith.add(city.getName())
    }
    citiesStartingWith
  }

  /**
   * @return an instance of City not registered yet, according to the
   * parameters informed by the user.
   */
  City getNotListedCity() {
    City newCity = null
    if (this.cityNotListed != null && !this.cityNotListed.isEmpty()) {
      newCity = new City(null, this.cityNotListed)
      newCity.setCountry(getCountry())
      newCity.setProvince(getProvince())
      newCity.setValid(false)
    }
    newCity
  }

  Country getCountry() {
    if (this.selectedCountry != null) {
      return countryBean.findCountry(this.selectedCountry)
    } else {
      return null
    }
  }

  Province getProvince() {
    if (this.selectedProvince != null && !this.selectedProvince.isEmpty()) {
      return provinceBean.find(this.selectedProvince)
    } else {
      return null
    }
  }

  City getCity() {
    if (this.selectedCity != null && !this.selectedCity.isEmpty()) {
      return cityBean.find(this.selectedCity)
    } else {
      return null
    }
  }

  void setSelectedCountry(String selectedCountry) {
    this.selectedCountry = selectedCountry
    selectedProvince = null
    selectedCity = null
  }

  void setSelectedProvince(String selectedProvince) {
    selectedProvince = selectedProvince
    selectedCity = null
  }

  void initialize() {
    countries = null
    provinces = null
    cities = null
    selectedCountry = null
    selectedProvince = null
    selectedCity = null
    initialized = true
    LOGGER.info('LocationBean initialized for a new use.')
  }

}
