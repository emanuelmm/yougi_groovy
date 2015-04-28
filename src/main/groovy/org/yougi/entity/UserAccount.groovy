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
package org.yougi.entity

import org.yougi.reference.DeactivationType
import org.yougi.reference.Gender

import javax.persistence.*

/**
 * Represents the user account.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name='user_account')
class UserAccount implements Serializable, Identified {

  @Id
  String id

  @Column(name='first_name', nullable=false)
  String firstName

  @Column(name='last_name', nullable=false)
  String lastName

  @Enumerated(EnumType.ORDINAL)
  @Column(nullable=false)
  Gender gender

  String email

  @Transient
  String emailConfirmation

  @Column(name='unverified_email')
  String unverifiedEmail

  @Column(name='confirmation_code')
  String confirmationCode

  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  @Column(name='registration_date')
  Date registrationDate

  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  @Column(name='last_update')
  Date lastUpdate

  Boolean deactivated = false

  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  @Column(name='deactivation_date')
  Date deactivationDate

  @Column(name='deactivation_reason')
  String deactivationReason

  @Enumerated(EnumType.ORDINAL)
  @Column(name='deactivation_type')
  DeactivationType deactivationType

  String website
  String twitter

  @ManyToOne
  @JoinColumn(name='country')
  Country country

  @ManyToOne
  @JoinColumn(name='province')
  Province province

  @ManyToOne
  @JoinColumn(name='city')
  City city

  @Column(name='timezone')
  String timeZone

  @Column(name = 'public_profile')
  Boolean publicProfile

  @Column(name = 'mailing_list')
  Boolean mailingList

  Boolean news

  @Column(name='general_offer')
  Boolean generalOffer

  @Column(name = 'job_offer')
  Boolean jobOffer

  Boolean event
  Boolean sponsor
  Boolean speaker
  Boolean verified = false

  String getFullName() {
    def str = ''
    str <<= firstName
    str <<= ' '
    str <<= lastName
    str
  }

  void setUnverifiedEmail(String unverifiedEmail) {
    if(unverifiedEmail) {
      unverifiedEmail = unverifiedEmail.toLowerCase()
    } else {
      unverifiedEmail = null
    }
  }

  /**
   * Update the email with the value of the unverified email and clean the
   * unverifiedEmail.
   */
  void setEmailAsVerified() {
    email = unverifiedEmail
    unverifiedEmail = null
  }

  /**
   * @return Independent of the verification of the email, this method returns
   * the available email address for posting email messages.
   */
  String getPostingEmail() {
    // In case there is an unverified email, it has the priority to be in
    // the message recipient.
    if(unverifiedEmail) {
      return unverifiedEmail
    } else {
      // If unverified email is null it means that the email is valid and it
      // can be used in the message recipient.
      return email
    }
  }

  Boolean getDeactivated() {
    if(deactivated) {
      return deactivated
    } else {
      return false
    }
  }

  String getTwitterReference() {
    this.twitter == null? this.twitter : '@' + this.twitter
  }

  String getTwitterURL() {
    'http://twitter.com/' + this.twitter
  }

  void setTwitter(String twitter) {
    if(!twitter) {
      this.twitter = null
    } else if(twitter.contains('@')) {
      this.twitter = twitter.replace('@', '')
    } else {
      this.twitter = twitter
    }
  }

  void setEmailConfirmation(String emailConfirmation) {
    this.emailConfirmation = emailConfirmation.toLowerCase()
  }

  /**
   * Defines a confirmation code to the user. It usually happens when the user
   * fill in the registration form and the email address needs to be confirmed
   * or when (s)he needs to change the password.
   */
  void defineNewConfirmationCode() {
    UUID uuid = UUID.randomUUID()
    this.confirmationCode = uuid.toString().replaceAll('-', '').toUpperCase()
  }

  /**
   * Set the confirmation code as null. Should be called when the confirmation
   * code is confirmed and can be discarded.
   */
  void resetConfirmationCode() {
    this.confirmationCode = null
  }

  boolean getConfirmed() {
    confirmationCode
  }

  @Override
  String toString() {
    fullName
  }

}
