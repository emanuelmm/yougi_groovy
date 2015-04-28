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

import org.yougi.event.business.EventBean
import org.yougi.event.business.EventVenueBean
import org.yougi.event.business.VenueBean
import org.yougi.event.entity.Event
import org.yougi.event.entity.EventVenue
import org.yougi.event.entity.Venue
import org.yougi.annotation.ManagedProperty

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class EventVenueMBean implements Serializable {

  @EJB
  private EventBean eventBean
  @EJB
  private VenueBean venueBean
  @EJB
  private EventVenueBean eventVenueBean

  @Inject
  @ManagedProperty('#{param.eventId}')
  String eventId

  @Inject
  @ManagedProperty('#{param.venueId}')
  String venueId

  List<Venue> venues
  List<Event> events
  String selectedVenue
  String selectedEvent

  List<Venue> getVenues() {
    if(this.venues == null) {
      if(!this.selectedEvent) {
        this.venues = venueBean.findVenues()
      } else {
        Event event = new Event(selectedEvent)
        this.venues = eventVenueBean.findVenues(event)
      }
    }
    this.venues
  }

  List<Event> getEvents() {
    if(this.events == null) {
      if(!this.selectedVenue) {
        this.events = eventBean.findParentEvents()
      } else {
        Venue venue = new Venue(selectedVenue)
        this.events = eventVenueBean.findEvents(venue)
      }
    }
    this.events
  }

  @PostConstruct
  void load() {
    if (eventId) {
      selectedEvent = eventId
    }
    if (venueId) {
      selectedVenue = venueId
    }
  }

  String save() {
    EventVenue eventVenue = new EventVenue()
    eventVenue.setEvent(new Event(selectedEvent))
    eventVenue.setVenue(new Venue(selectedVenue))
    eventVenueBean.save(eventVenue)
    getNextPage()
  }

  String cancel() {
    getNextPage()
  }

  String getNextPage() {
    if (eventId) {
      return 'event?faces-redirect=true&tab=5&id='+ selectedEvent
    } else {
      return 'venue?faces-redirect=true&id='+ selectedVenue
    }
  }
}
