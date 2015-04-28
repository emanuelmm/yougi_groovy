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
import org.yougi.event.business.SessionBean
import org.yougi.event.business.TrackBean
import org.yougi.event.entity.Event
import org.yougi.event.entity.SessionEvent
import org.yougi.event.entity.Track
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
class SessionMBean implements Serializable {

  @EJB
  private SessionBean sessionBean
  @EJB
  private EventBean eventBean
  @EJB
  private TrackBean trackBean

  @Inject
  @ManagedProperty('#{param.id}')
  String id

  @Inject
  @ManagedProperty('#{param.eventId}')
  String eventId

  @Inject
  VenueSelectionMBean venueSelectionMBean

  SessionEvent session
  List<Event> events
  List<SessionEvent> sessions
  List<SessionEvent> relatedSessions
  List<SessionEvent> sessionsInTheSameRoom
  List<SessionEvent> sessionsInParallel
  List<Track> tracks
  String selectedEvent
  String selectedTrack

  List<SessionEvent> getSessions() {
    if (sessions == null) {
      Event event = new Event(selectedEvent)
      sessions = sessionBean.findSessionsWithSpeakers(event)
    }
    sessions
  }

  List<SessionEvent> getRelatedSessions() {
    if (relatedSessions == null) {
      relatedSessions = sessionBean.findRelatedSessions(session)
    }
    relatedSessions
  }

  List<SessionEvent> getSessionsInTheSameRoom() {
    if (sessionsInTheSameRoom == null) {
      sessionsInTheSameRoom = sessionBean.findSessionsInTheSameRoom(session)
    }
    sessionsInTheSameRoom
  }

  List<SessionEvent> getSessionsInParallel() {
    if(sessionsInParallel == null) {
      sessionsInParallel = sessionBean.findSessionsInParallel(session)
    }
    sessionsInParallel
  }

  List<Track> getTracks() {
    if(tracks == null) {
      Event event = new Event(selectedEvent)
      tracks = trackBean.findTracks(event)
    }
    tracks
  }

  List<Event> getEvents() {
    if (events == null) {
      events = eventBean.findParentEvents()
    }
    events
  }

  SessionEvent getPreviousSession() {
    sessionBean.findPreviousSession(session)
  }

  SessionEvent getNextSession() {
    sessionBean.findNextSession(session)
  }

  @PostConstruct
  void load() {
    if (eventId) {
      Event event = eventBean.find(eventId)
      session = new SessionEvent()
      session.setEvent(event)
      selectedEvent = event.id
      venueSelectionMBean.setSelectedEvent(selectedEvent)
    }

    if (id) {
      session = sessionBean.find(id)
      Event event = session.getEvent()
      selectedEvent = event.id
      if(session.track) {
        selectedTrack = session.track.id
      }
      venueSelectionMBean.setSelectedEvent(selectedEvent)
      if(session.room) {
        venueSelectionMBean.setSelectedVenue(session.room.venue.id)
        venueSelectionMBean.setSelectedRoom(session.room.id)
      }
    }

    if(session == null) {
      session = new SessionEvent()
    }
  }

  String save() {
    Event evt = eventBean.find(selectedEvent)
    session.setEvent(evt)
    session.setRoom(venueSelectionMBean.getRoom())

    if(selectedTrack) {
      Track track = new Track(selectedTrack)
      session.setTrack(track)
    }

    sessionBean.save(session)
    'event?faces-redirect=true&tab=2&id=' + eventId
  }

  String remove() {
    sessionBean.remove(session.id)
    'event?faces-redirect=true&tab=2&id=' + eventId
  }
}
