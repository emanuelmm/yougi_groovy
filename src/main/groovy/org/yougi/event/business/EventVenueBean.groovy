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
package org.yougi.event.business;

import org.yougi.business.AbstractBean;
import org.yougi.event.entity.Event;
import org.yougi.event.entity.EventVenue;
import org.yougi.event.entity.Venue;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Manages the allocation of events in venues.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class EventVenueBean extends AbstractBean<EventVenue> {

    @PersistenceContext
    private EntityManager em;

    public EventVenueBean() {
        super(EventVenue.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Venue> findEventVenues(Event event) {
        return em.createQuery("select ev.venue from EventVenue ev where ev.event.id = :event order by ev.venue.name asc", Venue.class)
                 .setParameter("event", event.getId() )
                 .getResultList();
    }

    public List<Venue> findVenues(Event except) {
        return em.createQuery("select v from Venue v where v not in (select ev.venue from EventVenue ev where ev.event = :event) order by v.name asc", Venue.class)
                 .setParameter("event", except)
                 .getResultList();
    }

    public List<Event> findEvents(Venue except) {
        return em.createQuery("select e from Event e where e not in (select ev.event from EventVenue ev where ev.venue = :venue) order by e.name asc", Event.class)
                 .setParameter("venue", except)
                 .getResultList();
    }

    public List<Event> findEventsVenue(Venue venue) {
        return em.createQuery("select ev.event from EventVenue ev where ev.venue = :venue order by ev.event.name asc", Event.class)
                                         .setParameter("venue", venue)
                                         .getResultList();
    }
}