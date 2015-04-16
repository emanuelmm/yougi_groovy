/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General License as published by the
 * Free Software Foundation either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.yougi.knowledge.web.model

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.inject.Named

import org.yougi.annotation.ManagedProperty
import org.yougi.knowledge.entity.MailingList

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class MailingListMBean implements Serializable {

	@EJB
	private org.yougi.knowledge.business.MailingListBean mailingListBean

	@Inject
	@ManagedProperty('#{param.id}')
	String id
	MailingList mailingList
	def mailingLists

	List<MailingList> getMailingLists() {
		if(mailingLists == null) {
			mailingLists = mailingListBean.findMailingLists()
		}
		mailingLists
	}

	@PostConstruct
	void load() {
		if(id) {
			mailingList = mailingListBean.find(id)
		} else {
			mailingList = new MailingList()
		}
	}

	String save() {
		mailingListBean.save(mailingList)
		'mailing_lists?faces-redirect=true'
	}

	String remove() {
		mailingListBean.remove(mailingList.id)
		'mailing_lists?faces-redirect=true'
	}
}