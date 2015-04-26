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
package org.yougi.event.web.model

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.inject.Named

import org.yougi.annotation.ManagedProperty
import org.yougi.event.business.EventBean
import org.yougi.event.business.EventVenueBean
import org.yougi.event.business.RoomBean
import org.yougi.event.business.SessionBean
import org.yougi.event.business.VenueBean
import org.yougi.event.entity.Event
import org.yougi.event.entity.Room
import org.yougi.event.entity.SessionEvent
import org.yougi.event.entity.Venue
import org.yougi.web.model.LocationMBean

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class VenueMBean implements Serializable {

  @EJB
  private VenueBean venueBean
  @EJB
  private RoomBean roomBean
  @EJB
  private SessionBean sessionBean
  @EJB
  private EventBean eventBean
  @EJB
  private EventVenueBean eventVenueBean

  @Inject
  @ManagedProperty('#{param.id}')
  String id

  @Inject
  @ManagedProperty('#{param.eventId}')
  String eventId

  @Inject
  LocationMBean locationMBean

  Event event
  Venue venue
  List<Room> rooms
  List<Event> events
  List<Venue> venues

  VenueMBean() {
    venue = new Venue()
  }

  List<SessionEvent> getSessions(Room room) {
    sessionBean.findSessionsByRoom(event, room)
  }

  List<Room> getRooms() {
    if(rooms == null) {
      rooms = roomBean.findRooms(venue)
    }
    rooms
  }

  List<Venue> getVenues() {
    if(venues == null) {
      venues = venueBean.findVenues()
    }
    venues
  }

  List<Event> getEvents() {
    if(events == null) {
      events = eventVenueBean.findEventsVenue(venue)
    }
    events
  }

  @PostConstruct
  void load() {
    if (eventId) {
      event = eventBean.find(eventId)
    }

    if (id) {
      venue = venueBean.find(id)
      locationMBean.initialize()

      if (venue.country) {
        locationMBean.setSelectedCountry(venue.country.acronym)
      }

      if (venue.province) {
        locationMBean.setSelectedProvince(venue.province.id)
      }

      if (venue.city) {
        locationMBean.setSelectedCity(venue.city.id)
      }
    }
  }

  String save() {
    venue.setCountry(locationMBean.country)
    venue.setProvince(locationMBean.province)
    venue.setCity(locationMBean.city)
    venueBean.save(venue)
    'venues?faces-redirect=true'
  }
}
