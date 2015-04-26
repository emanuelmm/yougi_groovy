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
package org.yougi.event.entity

import org.yougi.entity.Identified
import org.yougi.entity.UserAccount

import javax.persistence.*

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = 'attendee')
class Attendee implements Serializable, Identified {

  @Id
  String id

  @ManyToOne
  @JoinColumn(name = 'event')
  Event event

  @ManyToOne
  @JoinColumn(name = 'attendee')
  UserAccount userAccount

  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  @Column(name = 'registration_date')
  Date registrationDate

  Boolean attended

  @Column(name='certificate_fullname')
  private String certificateFullname

  @Column(name='certificate_event')
  String certificateEvent

  @Column(name='certificate_venue')
  String certificateVenue

  @Temporal(TemporalType.DATE)
  @Column(name='certificate_date')
  Date certificateDate

  @Column(name='certificate_code')
  String certificateCode

  String getFullName() {
    userAccount.fullName
  }

  void generateCertificateData() {
    if(this.certificateCode == null && attended) {
      this.certificateFullname = this.userAccount.getFullName()
      this.certificateEvent = this.event.getName()
      this.certificateDate = this.event.getStartDate()
      this.certificateCode = UUID.randomUUID().toString().toUpperCase()
      this.certificateVenue = this.event.getVenues().get(0).getName()
    }
  }

  /**
   * It sets the certificate code to its default value if the member did not
   * attended. The default value is not valid for certificate validation.
   */
  void resetCertificateCode() {
    if(!attended) {
      certificateFullname = null
      certificateEvent = null
      certificateVenue = null
      certificateDate = null
      certificateCode = null
    }
  }

  @Override
  String toString() {
    userAccount.fullName
  }
}
