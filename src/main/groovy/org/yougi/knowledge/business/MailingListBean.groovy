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
package org.yougi.knowledge.business;

import org.yougi.business.AbstractBean;
import org.yougi.knowledge.entity.MailingList;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the business logic related to the management of mailing lists.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class MailingListBean extends AbstractBean<MailingList> {
    private static final Logger LOGGER = Logger.getLogger(MailingListBean.class.getSimpleName());

    @PersistenceContext
    private EntityManager em;

    public MailingListBean() {
        super(MailingList.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<MailingList> findMailingLists() {
        return em.createQuery("select ml from MailingList ml order by ml.name asc", MailingList.class).getResultList();
    }

    public MailingList findMailingListByEmail(String email) {
        try {
            return em.createQuery("select ml from MailingList ml where ml.email = :email", MailingList.class)
                                   .setParameter("email", email)
                                   .getSingleResult();
        } catch(NoResultException nre) {
            LOGGER.log(Level.INFO, nre.getMessage());
            return null;
        }
    }
}