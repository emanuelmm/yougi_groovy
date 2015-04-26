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

import org.yougi.business.UserAccountBean
import org.yougi.entity.UserAccount
import org.yougi.event.business.SessionBean
import org.yougi.event.business.SpeakerBean
import org.yougi.event.entity.Event
import org.yougi.event.entity.SessionEvent
import org.yougi.event.entity.Speaker
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
class SpeakerMBean implements Serializable {

  @EJB
  private SpeakerBean speakerBean
  @EJB
  private SessionBean sessionBean
  @EJB
  private UserAccountBean userAccountBean

  @Inject
  @ManagedProperty('#{param.id}')
  String id

  @Inject
  @ManagedProperty('#{param.eventId}')
  String eventId

  Event event
  Speaker speaker
  List<Event> events
  List<SessionEvent> sessions
  List<UserAccount> userAccounts
  List<Speaker> speakers
  String selectedUserAccount

  SpeakerMBean() {
    speaker = new Speaker()
  }

  List<Speaker> getSpeakers() {
    if (speakers == null) {
      if(event) {
        speakers = speakerBean.findSpeakers(event)
      } else {
        speakers = speakerBean.findSpeakers()
      }
    }
    speakers
  }

  List<Event> getEvents() {
    if(events == null && speaker) {
      events = sessionBean.findEventsSpeaker(speaker)
    }
    events
  }

  List<SessionEvent> getSessions() {
    if(sessions == null) {
      if(event) {
        sessions = sessionBean.findSessions(event)
      } else if (speaker) {
        sessions = sessionBean.findSessionsSpeaker(speaker)
      }
    }
    sessions
  }

  List<UserAccount> getUserAccounts() {
    if(userAccounts == null) {
      userAccounts = speakerBean.findSpeakerCandidates(speaker.userAccount)
    }
    userAccounts
  }

  @PostConstruct
  void load() {
    if (id) {
      speaker = speakerBean.find(id)
      selectedUserAccount = speaker.userAccount.id
    }
  }

  String save() {
    UserAccount userAccount = userAccountBean.find(selectedUserAccount)
    speaker.setUserAccount(userAccount)
    speakerBean.save(speaker)
    getNextPage()
  }

  String remove() {
    speakerBean.remove(speaker.id)
    getNextPage()
  }

  private String getNextPage() {
    if (eventId) {
      return 'event?faces-redirect=true&tab=3&id='+ eventId
    } else {
      return 'speakers?faces-redirect=true&eventId='+ eventId
    }
  }
}
