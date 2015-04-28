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
import org.yougi.event.business.RoomBean
import org.yougi.event.business.SessionBean
import org.yougi.event.business.VenueBean
import org.yougi.event.entity.*
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
class RoomMBean implements Serializable {

  @EJB
  private RoomBean roomBean
  @EJB
  private SessionBean sessionBean
  @EJB
  private EventBean eventBean
  @EJB
  private VenueBean venueBean

  @Inject
  @ManagedProperty('#{param.id}')
  String id
  @Inject
  @ManagedProperty('#{param.eventId}')
  String eventId
  @Inject
  @ManagedProperty('#{param.venueId}')
  String venueId

  Event event
  Room room
  List<SessionEvent> sessions
  List<Speaker> speakers
  List<Venue> venues
  String selectedVenue

  RoomMBean() {
    room = new Room()
  }

  List<SessionEvent> getSessions() {
    if (sessions == null) {
      sessions = sessionBean.findSessionsByRoom(event, room)
    }
    sessions
  }

  List<Speaker> getSpeakers() {
    if(speakers == null) {
      speakers = sessionBean.findSessionSpeakersByRoom(event, room)
    }
    speakers
  }

  List<Venue> getVenues() {
    if(venues == null) {
      venues = venueBean.findVenues()
    }
    venues
  }

  @PostConstruct
  void load() {
    if (eventId) {
      event = eventBean.find(eventId)
    }
    if (venueId) {
      selectedVenue = venueId
    }
    if (id) {
      room = roomBean.find(id)
      selectedVenue = room.venue.id
    }
  }

  String save() {
    room.setVenue(new Venue(selectedVenue))
    roomBean.save(room)
    'venue?faces-redirect=true&tab=1&id='+ selectedVenue
  }
}
