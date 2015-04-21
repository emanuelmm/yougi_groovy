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
package org.yougi.event.web.model;

import org.yougi.event.business.EventBean;
import org.yougi.event.business.SessionBean;
import org.yougi.event.business.TrackBean;
import org.yougi.event.entity.Event;
import org.yougi.event.entity.SessionEvent;
import org.yougi.event.entity.Track;
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
public class SessionMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private EventBean eventBean;

    @EJB
    private TrackBean trackBean;

    @Inject
    @ManagedProperty("#{param.id}")
    private String id;

    @Inject
    @ManagedProperty("#{param.eventId}")
    private String eventId;

    @Inject
    private VenueSelectionMBean venueSelectionMBean;

    private SessionEvent session;

    private List<Event> events;
    private List<SessionEvent> sessions;
    private List<SessionEvent> relatedSessions;
    private List<SessionEvent> sessionsInTheSameRoom;
    private List<SessionEvent> sessionsInParallel;
    private List<Track> tracks;

    private String selectedEvent;
    private String selectedTrack;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setVenueSelectionMBean(VenueSelectionMBean venueSelectionMBean) {
        this.venueSelectionMBean = venueSelectionMBean;
    }

    public SessionEvent getSession() {
        return session;
    }

    public void setSession(SessionEvent session) {
        this.session = session;
    }

    public List<SessionEvent> getSessions() {
        if (this.sessions == null) {
            Event event = new Event(selectedEvent);
            this.sessions = sessionBean.findSessionsWithSpeakers(event);
        }
        return this.sessions;
    }

    public List<SessionEvent> getRelatedSessions() {
        if (this.relatedSessions == null) {
            this.relatedSessions = sessionBean.findRelatedSessions(this.session);
        }
        return this.relatedSessions;
    }

    public List<SessionEvent> getSessionsInTheSameRoom() {
        if (this.sessionsInTheSameRoom == null) {
            this.sessionsInTheSameRoom = sessionBean.findSessionsInTheSameRoom(this.session);
        }
        return this.sessionsInTheSameRoom;
    }

    public List<SessionEvent> getSessionsInParallel() {
        if(this.sessionsInParallel == null) {
            this.sessionsInParallel = sessionBean.findSessionsInParallel(this.session);
        }
        return sessionsInParallel;
    }

    public List<Track> getTracks() {
        if(this.tracks == null) {
            Event event = new Event(selectedEvent);
            this.tracks = trackBean.findTracks(event);
        }
        return this.tracks;
    }

    public String getSelectedEvent() {
        return this.selectedEvent;
    }

    public void setSelectedEvent(String selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public String getSelectedTrack() {
        return this.selectedTrack;
    }

    public void setSelectedTrack(String selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    public List<Event> getEvents() {
        if (this.events == null) {
            this.events = eventBean.findParentEvents();
        }
        return this.events;
    }

    public SessionEvent getPreviousSession() {
        return sessionBean.findPreviousSession(this.session);
    }

    public SessionEvent getNextSession() {
        return sessionBean.findNextSession(this.session);
    }

    @PostConstruct
    public void load() {
        if (this.eventId != null && !this.eventId.isEmpty()) {
            Event event = eventBean.find(eventId);
            this.session = new SessionEvent();
            this.session.setEvent(event);
            this.selectedEvent = event.getId();
            this.venueSelectionMBean.setSelectedEvent(this.selectedEvent);
        }

        if (this.id != null && !this.id.isEmpty()) {
            this.session = sessionBean.find(this.id);
            Event event = this.session.getEvent();
            this.selectedEvent = event.getId();
            if(this.session.getTrack() != null) {
                this.selectedTrack = this.session.getTrack().getId();
            }
            this.venueSelectionMBean.setSelectedEvent(this.selectedEvent);
            if(this.session.getRoom() != null) {
                this.venueSelectionMBean.setSelectedVenue(this.session.getRoom().getVenue().getId());
                this.venueSelectionMBean.setSelectedRoom(this.session.getRoom().getId());
            }
        }

        if(this.session == null) {
            this.session = new SessionEvent();
        }
    }

    public String save() {
        Event evt = eventBean.find(selectedEvent);
        this.session.setEvent(evt);

        this.session.setRoom(this.venueSelectionMBean.getRoom());

        if(this.selectedTrack != null && !this.selectedTrack.isEmpty()) {
            Track track = new Track(this.selectedTrack);
            this.session.setTrack(track);
        }

        sessionBean.save(this.session);
        return "event?faces-redirect=true&tab=2&id=" + this.eventId;
    }

    public String remove() {
        sessionBean.remove(this.session.getId());
        return "event?faces-redirect=true&tab=2&id=" + this.eventId;
    }
}