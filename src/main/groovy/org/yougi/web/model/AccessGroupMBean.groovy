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
package org.yougi.web.model;

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.inject.Named

import org.primefaces.model.DualListModel
import org.yougi.annotation.ManagedProperty
import org.yougi.business.AccessGroupBean
import org.yougi.business.UserAccountBean
import org.yougi.business.UserGroupBean
import org.yougi.entity.AccessGroup
import org.yougi.entity.UserAccount

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class AccessGroupMBean {

    @EJB
    AccessGroupBean accessGroupBean

    @EJB
    UserAccountBean userAccountBean

    @EJB
    UserGroupBean userGroupBean

    @Inject
    @ManagedProperty("#{param.id}")
    String groupId

    AccessGroup group

    // List of members for the picklist.
    DualListModel<UserAccount> members

    List<AccessGroup> getGroups() {
        accessGroupBean.findAccessGroups()
    }

    @PostConstruct
    void load() {
        def allUsers = userAccountBean.findAllActiveAccounts()
        def target = new ArrayList<>()

        if(groupId) {
            this.group = accessGroupBean.find(this.groupId)
            target.addAll(userGroupBean.findUsersGroup(group))
            allUsers.removeAll(target)
        } else {
            this.group = new AccessGroup()
        }
        this.members = new DualListModel<>(allUsers, target)
    }

    @SuppressWarnings("rawtypes")
    String save() {
        def selectedMembers = new ArrayList<>()
        def membersIds = this.members.getTarget()
        UserAccount userAccount
        
		for(int i = 0; i < membersIds.size(); i++) {
            userAccount = new UserAccount(((UserAccount)membersIds.get(i)).getId())
            selectedMembers.add(userAccount)
        }
        accessGroupBean.save(this.group, selectedMembers)
        
		"groups?faces-redirect=true"
    }
}