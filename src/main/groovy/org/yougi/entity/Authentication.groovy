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

import org.yougi.util.Base64Encoder

import javax.persistence.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Represents the authentication credentials of the user.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name='authentication')
class Authentication implements Serializable {

  @Id
  String username

  @Column(nullable=false)
  String password

  @ManyToOne
  @JoinColumn(name='user_account')
  UserAccount userAccount

  /**
   * Hash a raw password using the SHA-256 algorithm.
   * @param rawPassword non-hashed password informed by the user.
   * @return the hashed password.
   */
  String hashPassword(String rawPassword) {
    MessageDigest md
    byte[] stringBytes
    try {
      md = MessageDigest.getInstance('SHA-256')
      stringBytes = rawPassword.getBytes('UTF8')

      byte[] stringCriptBytes = md.digest(stringBytes)
      char[] encoded = Base64Encoder.encode(stringCriptBytes)
      return String.valueOf(encoded)
    } catch(NoSuchAlgorithmException nsae) {
      throw new SecurityException('The Requested encoding algorithm was not found in this execution platform.', nsae)
    } catch(UnsupportedEncodingException uee) {
      throw new SecurityException('UTF8 is not supported in this execution platform.', uee)
    }
  }
}
