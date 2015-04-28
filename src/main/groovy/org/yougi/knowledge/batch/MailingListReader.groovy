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
package org.yougi.knowledge.batch;

import org.yougi.business.ApplicationPropertyBean;
import org.yougi.reference.Properties;

import javax.annotation.Resource;
import javax.batch.api.chunk.AbstractItemReader;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
public class MailingListReader extends AbstractItemReader {

    @EJB
    private ApplicationPropertyBean applicationPropertyBean;

    @Resource(lookup = "java:/mail/yougi")
    private Session mailSession;

    private Folder inbox;
    private int messageCount;
    private int messageNumber = 1;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        Store store = mailSession.getStore(applicationPropertyBean.getPropertyValue(Properties.EMAIL_SERVER_TYPE));
        store.connect();

        inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);
        messageCount = inbox.getMessageCount();
    }

    @Override
    public Message readItem() throws Exception {
        Message message = null;
        if(messageNumber < messageCount) {
            message = inbox.getMessage(messageNumber++);
        }
        return message;
    }
}
