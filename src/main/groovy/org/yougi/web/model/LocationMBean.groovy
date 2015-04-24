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
package org.yougi.web.model;

import org.yougi.business.CityBean;
import org.yougi.business.CountryBean;
import org.yougi.business.ProvinceBean;
import org.yougi.business.TimezoneBean;
import org.yougi.entity.City;
import org.yougi.entity.Country;
import org.yougi.entity.Province;
import org.yougi.entity.Timezone;

import javax.inject.Inject;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
public class LocationMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(LocationMBean.class.getSimpleName());

    @Inject
    private CountryBean countryBean;

    @Inject
    private ProvinceBean provinceBean;

    @EJB
    private CityBean cityBean;

    @EJB
    private TimezoneBean timezoneBean;

    private List<Country> countries;

    private List<Province> provinces;

    private List<City> cities;

    private List<Timezone> timezones;

    private String selectedCountry;

    private String selectedProvince;

    private String selectedCity;

    private String selectedTimeZone;

    private String cityNotListed;

    private boolean initialized;

    public List<Country> getCountries() {
        if(this.countries == null) {
            this.countries = countryBean.findCountries();
        }
        return this.countries;
    }

    public List<Province> getProvinces() {
        if (this.selectedCountry != null) {
            Country country = new Country(selectedCountry);
            this.provinces = provinceBean.findByCountry(country);
            return this.provinces;
        } else {
            return new ArrayList<>();
        }
    }

    public List<City> getCities() {
        if (selectedCountry != null && selectedProvince == null) {
            Country country = new Country(selectedCountry);
            this.cities = cityBean.findByCountry(country, false);
        } else if (selectedProvince != null) {
            Province province = new Province(id:selectedProvince);
            this.cities = cityBean.findByProvince(province, false);
        }
        return this.cities;
    }

    public List<Timezone> getTimezones() {
        if(this.timezones == null) {
            this.timezones = timezoneBean.findTimezones();
        }
        return this.timezones;
    }

    public List<String> findCitiesStartingWith(String initials) {
        List<City> cits = cityBean.findStartingWith(initials);
        List<String> citiesStartingWith = new ArrayList<>();
        for (City city : cits) {
            citiesStartingWith.add(city.getName());
        }
        return citiesStartingWith;
    }

    public String getCityNotListed() {
        return cityNotListed;
    }

    /**
     * @return an instance of City not registered yet, according to the
     * parameters informed by the user.
     */
    public City getNotListedCity() {
        City newCity = null;
        if (this.cityNotListed != null && !this.cityNotListed.isEmpty()) {
            newCity = new City(null, this.cityNotListed);
            newCity.setCountry(getCountry());
            newCity.setProvince(getProvince());
            newCity.setValid(false);
        }
        return newCity;
    }

    public void setCityNotListed(String cityNotListed) {
        this.cityNotListed = cityNotListed;
    }

    public Country getCountry() {
        if (this.selectedCountry != null) {
            return countryBean.findCountry(this.selectedCountry);
        } else {
            return null;
        }
    }

    public Province getProvince() {
        if (this.selectedProvince != null && !this.selectedProvince.isEmpty()) {
            return provinceBean.find(this.selectedProvince);
        } else {
            return null;
        }
    }

    public City getCity() {
        if (this.selectedCity != null && !this.selectedCity.isEmpty()) {
            return cityBean.find(this.selectedCity);
        } else {
            return null;
        }
    }

    public String getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
        this.selectedProvince = null;
        this.selectedCity = null;
    }

    public String getSelectedProvince() {
        return selectedProvince;
    }

    public void setSelectedProvince(String selectedProvince) {
        this.selectedProvince = selectedProvince;
        this.selectedCity = null;
    }

    public String getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }

    public String getSelectedTimeZone() {
        return selectedTimeZone;
    }

    public void setSelectedTimeZone(String selectedTimeZone) {
        this.selectedTimeZone = selectedTimeZone;
    }

    public void initialize() {
        this.countries = null;
        this.provinces = null;
        this.cities = null;

        this.selectedCountry = null;
        this.selectedProvince = null;
        this.selectedCity = null;

        this.initialized = true;

        LOGGER.info("LocationBean initialized for a new use.");
    }

    public boolean isInitialized() {
        return this.initialized;
    }
}
