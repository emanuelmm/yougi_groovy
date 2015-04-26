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
import org.yougi.event.business.SponsorshipEventBean
import org.yougi.event.entity.Event
import org.yougi.event.entity.SponsorshipEvent
import org.yougi.partnership.business.PartnerBean
import org.yougi.partnership.entity.Partner
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
class SponsorshipEventMBean implements Serializable {

  @EJB
  private SponsorshipEventBean sponsorshipEventBean
  @EJB
  private EventBean eventBean
  @EJB
  private PartnerBean partnerBean

  @Inject
  @ManagedProperty('#{param.id}')
  String id

  @Inject
  @ManagedProperty('#{param.eventId}')
  String eventId

  Event event
  SponsorshipEvent sponsorshipEvent
  List<SponsorshipEvent> sponsorshipsEvent
  List<Event> events
  List<Partner> partners

  String selectedEvent
  String selectedPartner

  List<SponsorshipEvent> getSponsorshipsEvent() {
    if (sponsorshipsEvent == null) {
      sponsorshipsEvent = sponsorshipEventBean.findSponsorshipsEvent(event)
    }
    sponsorshipsEvent
  }

  BigDecimal getSumAmounts() {
    BigDecimal sum = BigDecimal.ZERO
    List<SponsorshipEvent> es = getSponsorshipsEvent()
    for(SponsorshipEvent sponsor: es) {
      sum = sum.add(sponsor.getAmount())
    }
    sum
  }

  List<Event> getEvents() {
    if (events == null) {
      events = eventBean.findParentEvents()
    }
    events
  }

  List<Partner> getPartners() {
    if (partners == null) {
      partners = partnerBean.findPartners()
    }
    partners
  }

  @PostConstruct
  void load() {
    if (eventId) {
      event = eventBean.find(eventId)
      selectedEvent = event.id
    }

    if (id) {
      sponsorshipEvent = sponsorshipEventBean.find(id)
      selectedEvent = sponsorshipEvent.event.id
      selectedPartner = sponsorshipEvent.partner.id
    } else {
      sponsorshipEvent = new SponsorshipEvent()
    }
  }

  String save() {
    Event evt = eventBean.find(selectedEvent)
    sponsorshipEvent.setEvent(evt)
    Partner spon = partnerBean.find(selectedPartner)
    sponsorshipEvent.setPartner(spon)
    sponsorshipEventBean.save(sponsorshipEvent)
    'event?faces-redirect=true&tab=6&id=' + evt.id
  }

  String remove() {
    sponsorshipEventBean.remove(sponsorshipEvent.id)
    'event?faces-redirect=true&tab=6&id=' + event.id
  }
}
