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
import org.yougi.business.TimezoneBean;
import org.yougi.business.UserAccountBean;
import org.yougi.entity.*;
import org.yougi.annotation.ManagedProperty;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
public class CityMBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private CityBean cityBean;

    @EJB
    private TimezoneBean timezoneBean;

    @EJB
    private UserAccountBean userAccountBean;

    @Inject
    @ManagedProperty("#{param.id}")
    private String id;

    @Inject
    private LocationMBean locationMBean;

    private City city;

    private List<City> cities;
    private List<Timezone> timezones;

    public CityMBean() {
        this.city = new City();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocationMBean getLocationMBean() {
        return locationMBean;
    }

    public void setLocationMBean(LocationMBean locationMBean) {
        this.locationMBean = locationMBean;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<City> getCities() {
        if(this.cities == null) {
            this.cities = cityBean.findAll();
        }
        return this.cities;
    }

    public List<UserAccount> getInhabitants() {
        return userAccountBean.findInhabitantsFrom(this.city);
    }

    public List<Timezone> getTimezones() {
        if(this.timezones == null) {
            this.timezones = timezoneBean.findTimezones();
        }
        return this.timezones;
    }

    @PostConstruct
    public void load() {
        if (this.id != null && !this.id.isEmpty()) {
            this.city = cityBean.find(id);

            locationMBean.initialize();

            if (this.city.getCountry() != null) {
                locationMBean.setSelectedCountry(this.city.getCountry().getAcronym());
            }

            if (this.city.getProvince() != null) {
                locationMBean.setSelectedProvince(this.city.getProvince().getId());
            }
        }
    }

    public String save() {
        Country country = this.locationMBean.getCountry();
        if (country != null) {
            this.city.setCountry(country);
        }

        Province province = this.locationMBean.getProvince();
        if (province != null) {
            this.city.setProvince(province);
        }

        cityBean.save(this.city);

        return "cities?faces-redirect=true";
    }

    public String remove() {
        cityBean.remove(city.getId());
        return "cities?faces-redirect=true";
    }
}