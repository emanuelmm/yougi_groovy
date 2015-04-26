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
package org.yougi.web.model

import de.bripkens.gravatar.DefaultImage
import de.bripkens.gravatar.Gravatar
import de.bripkens.gravatar.Rating

import javax.enterprise.context.RequestScoped
import javax.inject.Named
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author Daniel Cunha - danielsoro@gmail.com
 */
@Named
@RequestScoped
class ProfilePictureMBean {

  private static final Logger LOGGER = Logger.getLogger(ProfilePictureMBean.class.getSimpleName())

  String getPictureFromEmail(String email) throws IOException {
    validateUrl(getGravatarImageUrl(email, 85))
  }

  String getPictureForMembersList(String email) throws IOException {
    validateUrl(getGravatarImageUrl(email, 100))
  }

  String getGravatarImageUrl(String email, int size) {
    return new Gravatar()
      .setHttps(true)
      .setSize(size)
      .setRating(Rating.PARENTAL_GUIDANCE_SUGGESTED)
      .setStandardDefaultImage(DefaultImage.HTTP_404)
      .getUrl(email)
  }

  private String validateUrl(String gravataUrl) throws IOException {
    try{
      return isNotFound(new URL(gravataUrl)) ? getDefaultAvatar() : gravataUrl
    } catch(UnknownHostException uhe){
      LOGGER.log(Level.INFO, uhe.getMessage(), uhe)
      return getDefaultAvatar()
    }
  }

  private boolean isNotFound(URL url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection()
    connection.connect()
    connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND
  }

  private String getDefaultAvatar() {
    '/images/default_avatar.jpg'
  }
}
