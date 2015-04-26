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
package org.yougi.partnership.business

import groovy.transform.CompileStatic

import java.util.logging.Level
import java.util.logging.Logger

import javax.ejb.EJB
import javax.ejb.Stateless
import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.persistence.PersistenceContext

import org.yougi.business.AbstractBean
import org.yougi.entity.UserAccount
import org.yougi.partnership.entity.Partner
import org.yougi.partnership.entity.Representative
import org.yougi.util.EntitySupport

/**
 * Manages partners of the user group.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
class RepresentativeBean extends AbstractBean<Representative> {

    private static final Logger LOGGER = Logger.getLogger(this.simpleName)

    @PersistenceContext
    EntityManager em

    @EJB
	PartnerBean partnerBean

    RepresentativeBean() {
        super(Representative)
    }

    @Override
    protected EntityManager getEntityManager() {
        em
    }

    Representative findRepresentative(UserAccount person) {
    	try {
            em.createQuery('select r from Representative r where r.person = :person', Representative)
                                      .setParameter('person', person)
                                      .getSingleResult()
    	} catch(NoResultException nre) {
            LOGGER.log(Level.INFO, nre.message)
    		null
    	}
    }

    List<UserAccount> findRepresentativePersons(Partner partner) {
    	em.createQuery('select r.person from Representative r where r.partner = :partner order by r.person.firstName asc', UserAccount)
    	         .setParameter('partner', partner)
    	         .getResultList()
    }

    List<Representative> findRepresentatives(Partner partner) {
    	em.createQuery('select r from Representative r where r.partner = :partner order by r.person.firstName asc', Representative)
    	         .setParameter('partner', partner)
    	         .getResultList()
    }

    /**
     * Update the list of representatives of a partner according to the number
     * of persons informed.
     * */
    void save(Partner partner, List<UserAccount> persons) {

    	partnerBean.save(partner)

    	if(persons == null) {
            return
        }

    	// Create new representatives using the received parameters.
        def representatives = new ArrayList<>()
        Representative representative
        persons.each {
            representative = new Representative('partner':partner, 'person':it)
            representative.setId(EntitySupport.generateEntityId())
            representatives.add(representative)
        }

        /* If no representative was created because no person was informed then
         * it means that the partner does not have representatives anymore, and
         * the existing ones are removed. */
        if(representatives.isEmpty()) {
            em.createQuery('delete from Representative r where r.partner = :partner')
                    .setParameter('partner', partner)
                    .executeUpdate()
            return
        }

        def currentRepresentatives = findRepresentatives(partner)

        currentRepresentatives.each {
            if(!representatives.contains(it)) {
                em.remove(it)
            }
        }

        representatives.each {
            if(!currentRepresentatives.contains(it)) {
                em.persist(it)
            }
        }
    }
}