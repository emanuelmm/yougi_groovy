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

import org.primefaces.model.chart.PieChartModel
import org.yougi.annotation.ManagedProperty
import org.yougi.annotation.UserName
import org.yougi.business.UserAccountBean
import org.yougi.entity.UserAccount
import org.yougi.event.business.AttendeeBean
import org.yougi.event.business.EventBean
import org.yougi.event.business.EventVenueBean
import org.yougi.event.business.SessionBean
import org.yougi.event.business.SpeakerBean
import org.yougi.event.business.SponsorshipEventBean
import org.yougi.event.business.TrackBean
import org.yougi.event.entity.Attendee
import org.yougi.event.entity.Event
import org.yougi.event.entity.SessionEvent
import org.yougi.event.entity.Speaker
import org.yougi.event.entity.SponsorshipEvent
import org.yougi.event.entity.Track
import org.yougi.event.entity.Venue
import org.yougi.util.DateTimeUtils
import org.yougi.util.ResourceBundleHelper
import org.yougi.util.WebTextUtils
import org.yougi.web.model.UserProfileMBean

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class EventMBean {

  @EJB
  private EventBean eventBean
  @EJB
  private SessionBean sessionBean
  @EJB
  private SpeakerBean speakerBean
  @EJB
  private TrackBean trackBean
  @EJB
  private AttendeeBean attendeeBean
  @EJB
  private UserAccountBean userAccountBean
  @EJB
  private EventVenueBean eventVenueBean
  @EJB
  private SponsorshipEventBean sponsorshipEventBean

  @Inject
  @ManagedProperty('#{param.id}')
  String id

  @Inject
  private UserProfileMBean userProfileMBean

  @Inject
  @UserName
  String username

  Event event
  Attendee attendee
  String selectedParent

  List<Event> events
  List<Event> subEvents
  List<Event> parentEvents
  List<Venue> venues
  List<SessionEvent> sessions
  List<Track> tracks
  List<Speaker> speakers
  List<Attendee> attendees
  List<SponsorshipEvent> sponsors

  Long numberPeopleAttending
  Long numberPeopleAttended

  /**
   * @return true if the event ocurred on the day before today.
   */
  Boolean getHappened() {
    TimeZone tz = TimeZone.getTimeZone(userProfileMBean.getTimeZone())
    Calendar today = Calendar.getInstance(tz)
    event.getStartDate().before(today.getTime())
  }

  /**
   * @return true if the member has the intention to attend the event. It does
   * not mean that s(he) actually attended it.
   */
  Boolean getIsAttending() {
    attendee && attendee.id
  }

  /**
   * @return true if the member actually attended the event.
   */
  Boolean getAttended() {
    attendee?.attended
  }

  List<Event> getEvents() {
    if (events == null) {
      events = eventBean.findParentEvents()
    }
    events
  }

  List<Event> getParentEvents() {
    if (parentEvents == null) {
      parentEvents = eventBean.findParentEvents()
    }
    parentEvents
  }

  List<Venue> getVenues() {
    if(venues == null) {
      venues = eventVenueBean.findEventVenues(event)
    }
    venues
  }

  List<Event> getSubEvents() {
    if (subEvents == null) {
      subEvents = eventBean.findEvents(event)
    }
    subEvents
  }

  List<SessionEvent> getSessions() {
    if (sessions == null) {
      sessions = sessionBean.findSessionsWithSpeakers(event)
    }
    sessions
  }

  List<SessionEvent> getSessions(Event event) {
    sessionBean.findSessionsWithSpeakers(event)
  }

  List<Track> getTracks() {
    if (tracks == null) {
      tracks = trackBean.findTracks(event)
    }
    tracks
  }

  List<Track> getTracks(Event event) {
    trackBean.findTracks(event)
  }

  List<Speaker> getSpeakers() {
    if (speakers == null) {
      speakers = speakerBean.findSpeakers(event)
    }
    speakers
  }

  List<Speaker> getSpeakers(Event event) {
    speakerBean.findSpeakers(event)
  }

  List<Attendee> getAttendees() {
    if (attendees == null) {
      attendees = attendeeBean.findAllAttendees(event)
    }
    attendees
  }

  List<SponsorshipEvent> getSponsors() {
    if(sponsors == null) {
      sponsors = sponsorshipEventBean.findSponsorshipsEvent(event)
    }
    sponsors
  }

  PieChartModel getAttendanceRateChartModel() {
    PieChartModel pieChartModel = new PieChartModel()
    pieChartModel.set('Registered', numberPeopleAttending)
    pieChartModel.set('Attended', numberPeopleAttended)
    pieChartModel
  }

  String getFormattedEventDescription() {
    WebTextUtils.convertLineBreakToHTMLParagraph(event.getDescription())
  }

  String getFormattedRegistrationDate() {
    if (attendee == null) {
      return ''
    }
    DateTimeUtils.getFormattedDate(attendee.registrationDate, ResourceBundleHelper.getMessage('formatDate'))
  }

  @PostConstruct
  void load() {
    if (id) {
      event = eventBean.find(id)
      if(event.parent) {
        selectedParent = event.parent.id
      }

      UserAccount person = userAccountBean.findByUsername(username)
      attendee = attendeeBean.find(event, person)

      numberPeopleAttending = attendeeBean.findNumberPeopleAttending(event)
      numberPeopleAttended = attendeeBean.findNumberPeopleAttended(event)
    } else {
      event = new Event()
    }
  }

  String save() {
    if(selectedParent) {
      event.setParent(new Event(selectedParent))
    }
    eventBean.save(event)
    'events?faces-redirect=true'
  }

  String remove() {
    eventBean.remove(event.id)
    'events?faces-redirect=true'
  }

}
