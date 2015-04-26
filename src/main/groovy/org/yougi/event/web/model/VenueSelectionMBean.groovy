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

import org.yougi.event.business.EventVenueBean
import org.yougi.event.business.RoomBean
import org.yougi.event.business.VenueBean
import org.yougi.event.entity.Event
import org.yougi.event.entity.Room
import org.yougi.event.entity.Venue
import javax.ejb.EJB
import javax.faces.view.ViewScoped
import javax.inject.Named

/**
 * This class is used to manage the selection of the venue and room of an event
 * session. The selected venue updates the list of rooms related with that venue.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@ViewScoped
class VenueSelectionMBean implements Serializable {

  @EJB
  private VenueBean venueBean
  @EJB
  private EventVenueBean eventVenueBean
  @EJB
  private RoomBean roomBean

  List<Venue> venues
  List<Room> rooms
  String selectedVenue
  String selectedRoom
  String selectedEvent

  List<Venue> getVenues() {
    Event event = new Event(selectedEvent)
    venues = eventVenueBean.findEventVenues(event)
    venues
  }

  List<Room> getRooms() {
    if (selectedVenue) {
      Venue venue = new Venue(selectedVenue)
      rooms = roomBean.findRooms(venue)
    } else {
      rooms = null
    }
    rooms
  }

  Venue getVenue() {
    if (selectedVenue) {
      return venueBean.find(selectedVenue)
    }
    null
  }

  Room getRoom() {
    if (selectedRoom) {
      return roomBean.find(selectedRoom)
    }
    null
  }

  void setSelectedVenue(String selectedVenue) {
    this.selectedVenue = selectedVenue
    selectedRoom = null
  }

}
