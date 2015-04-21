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
package org.yougi.partnership.business;

import org.yougi.business.AbstractBean;
import org.yougi.util.EntitySupport;
import org.yougi.entity.UserAccount;
import org.yougi.partnership.entity.Partner;
import org.yougi.partnership.entity.Representative;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages partners of the user group.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class RepresentativeBean extends AbstractBean<Representative> {

    private static final Logger LOGGER = Logger.getLogger(RepresentativeBean.class.getSimpleName());

    @PersistenceContext
    private EntityManager em;

    @EJB
    private PartnerBean partnerBean;

    public RepresentativeBean() {
        super(Representative.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Representative findRepresentative(UserAccount person) {
    	try {
            return em.createQuery("select r from Representative r where r.person = :person", Representative.class)
                                      .setParameter("person", person)
                                      .getSingleResult();
    	} catch(NoResultException nre) {
            LOGGER.log(Level.INFO, nre.getMessage());
    		return null;
    	}
    }

    public List<UserAccount> findRepresentativePersons(Partner partner) {
    	return em.createQuery("select r.person from Representative r where r.partner = :partner order by r.person.firstName asc", UserAccount.class)
    	         .setParameter("partner", partner)
    	         .getResultList();
    }

    public List<Representative> findRepresentatives(Partner partner) {
    	return em.createQuery("select r from Representative r where r.partner = :partner order by r.person.firstName asc", Representative.class)
    	         .setParameter("partner", partner)
    	         .getResultList();
    }

    /**
     * Update the list of representatives of a partner according to the number
     * of persons informed.
     * */
    public void save(Partner partner, List<UserAccount> persons) {

    	partnerBean.save(partner);

    	if(persons == null) {
            return;
        }

    	// Create new representatives using the received parameters.
        List<Representative> representatives = new ArrayList<>();
        Representative representative;
        for(UserAccount person: persons) {
            representative = new Representative(partner, person);
            representative.setId(EntitySupport.generateEntityId());
            representatives.add(representative);
        }

        /* If no representative was created because no person was informed then
         * it means that the partner does not have representatives anymore, and
         * the existing ones are removed. */
        if(representatives.isEmpty()) {
            em.createQuery("delete from Representative r where r.partner = :partner")
                    .setParameter("partner", partner)
                    .executeUpdate();
            return;
        }

        List<Representative> currentRepresentatives = findRepresentatives(partner);

        for(Representative rep: currentRepresentatives) {
            if(!representatives.contains(rep)) {
                em.remove(rep);
            }
        }

        for(Representative rep: representatives) {
            if(!currentRepresentatives.contains(rep)) {
                em.persist(rep);
            }
        }
    }
}