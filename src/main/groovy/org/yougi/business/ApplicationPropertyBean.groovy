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

import org.yougi.entity.ApplicationProperty
import org.yougi.reference.Properties

import javax.ejb.Stateless
import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.persistence.PersistenceContext
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
class ApplicationPropertyBean {

  private static final Logger LOGGER = Logger.getLogger(ApplicationPropertyBean.class.getSimpleName())

  @PersistenceContext
  private EntityManager em

  Map<String, String> findApplicationProperties() {
    Map<String, String> propertiesMap = new HashMap<>()
    List<ApplicationProperty> properties = em.createQuery('select ap from ApplicationProperty ap', ApplicationProperty).getResultList()
    for(ApplicationProperty p: properties) {
      propertiesMap.put(p.propertyKey, p.propertyValue)
    }

    // If there is no property in the database, it creates all properties according to the enumeration Properties.
    if(propertiesMap.isEmpty()) {
      Properties[] props = Properties.values()
      for(int i = 0; i < props.length; i++) {
        propertiesMap.put(props[i].key, props[i].defaultValue)
      }
      create(propertiesMap)
    } else if(Properties.values().length > propertiesMap.size()) {
      // If there is more properties in the enumeration than in the database, then additional enumerations are persisted.
      Properties[] props = Properties.values()
      for(int i = 0; i < props.length; i++) {
        if(!propertiesMap.containsKey(props[i].key)) {
          propertiesMap.put(props[i].key, props[i].defaultValue)
          create(props[i].getKey(), props[i].defaultValue)
        }
      }
    } else if(Properties.values().length < propertiesMap.size()) {
      // If there is more persisted properties than in the enumeration, then exceding properties are removed.
      // entries from database
      Set<Map.Entry<String, String>> propEntries = propertiesMap.entrySet()
      Iterator<Map.Entry<String, String>> iProps = propEntries.iterator()
      Map.Entry<String, String> entry
      Properties[] props = Properties.values()
      while(iProps.hasNext()) {
        entry = iProps.next()
        for(int i = 0; i < props.length; i++) {
          if(!entry.key.equals(props[i].key)) {
            remove(entry.key)
          }
        }
      }
    }
    propertiesMap
  }

  /**
   * Returns the ApplicationProperty that corresponds to the informed enum
   * property. If the ApplicationProperty does not exist, then it creates one
   * with the default value.
   */
  ApplicationProperty findApplicationProperty(Properties properties) {
    ApplicationProperty applicationProperty
    try {
      applicationProperty = em.createQuery('select ap from ApplicationProperty ap where ap.propertyKey = :key', ApplicationProperty)
        .setParameter('key', properties.key)
        .getSingleResult()
    } catch(NoResultException nre) {
      LOGGER.log(Level.INFO, nre.message)
      Map<String, String> applicationProperties = findApplicationProperties()
      String key = properties.key
      applicationProperty = new ApplicationProperty(key, applicationProperties.get(key))
    }
    applicationProperty
  }

  String getPropertyValue(Properties properties) {
    ApplicationProperty applicationProperty = findApplicationProperty(properties)
    applicationProperty.propertyValue
  }

  void save(Map<String, String> properties) {
    List<ApplicationProperty> existingProperties = em.createQuery('select ap from ApplicationProperty ap', ApplicationProperty).getResultList()
    String value
    for(ApplicationProperty p: existingProperties) {
      value = properties.get(p.propertyKey)
      p.propertyValue = value
      em.merge(p)
    }
  }

  private ApplicationProperty create(Map<String, String> properties) {
    Set<Map.Entry<String, String>> props = properties.entrySet()
    Iterator<Map.Entry<String, String>> iProps = props.iterator()
    ApplicationProperty appProp = null
    Map.Entry<String, String> entry
    while(iProps.hasNext()) {
      entry = iProps.next()
      appProp = new ApplicationProperty(entry.getKey(), entry.getValue())
      em.persist(appProp)
    }
    appProp
  }

  private ApplicationProperty create(String key, String value) {
    ApplicationProperty appProp = new ApplicationProperty(key, value)
    em.persist(appProp)
    appProp
  }

  private void remove(String key) {
    ApplicationProperty applicationProperty = em.find(ApplicationProperty, key)
    em.remove(applicationProperty)
  }
}
