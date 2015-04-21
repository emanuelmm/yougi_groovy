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

import org.yougi.business.CountryBean
import org.yougi.business.ProvinceBean
import org.yougi.entity.Country
import org.yougi.entity.Province
import org.yougi.annotation.ManagedProperty

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.faces.model.SelectItem
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class ProvinceMBean {

  @Inject
  CountryBean countryBean
  @EJB
  ProvinceBean provinceBean
  @Inject
  @ManagedProperty('#{param.id}')
  String id
  Country selectedCountry
  Province province

  def getCountries() {
    List<Country> countries = countryBean.findCountries()
    List<SelectItem> selectItems = new ArrayList<>()
    SelectItem selectItem = new SelectItem('', 'Select...')
    selectItems.add(selectItem)
    for (Country country : countries) {
      selectItem = new SelectItem(country.acronym, country.name)
      selectItems.add(selectItem)
    }
    selectItems
  }

  SelectItem[] getAssociatedCountries() {
    List<Country> countries = countryBean.findAssociatedCountries()
    SelectItem[] options = new SelectItem[countries.size() + 1]
    options[0] = new SelectItem('', 'Select')
    for (int i = 0 ; i < countries.size() ; i++) {
      options[i + 1] = new SelectItem(countries.get(i), countries.get(i).name)
    }
    options
  }

  List<Province> getProvinces() {
    provinceBean.findAll()
  }

  String getSelectedCountry() {
    if (selectedCountry == null) {
      return null
    }
    selectedCountry.acronym
  }

  void setSelectedCountry(String acronym) {
    if (!acronym) {
      return
    }
    selectedCountry = countryBean.findCountry(acronym)
  }

  @PostConstruct
  void load() {
    if (id) {
      province = provinceBean.find(id)
      selectedCountry = province.country
    } else {
      province = new Province()
    }
  }

  String save() {
    province.country = selectedCountry
    provinceBean.save(province)
    'provinces?faces-redirect=true'
  }

  String remove() {
    provinceBean.remove(province.id)
    'provinces?faces-redirect=true'
  }
}
