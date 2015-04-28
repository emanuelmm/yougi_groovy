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
package org.yougi.util

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
class UrlUtils {

  private UrlUtils() {}

  private static final String HTTP = 'http://'
  private static final String HTTPS = 'https://'

  static String setProtocol(String url) {
    String urlWithProtocol = url
    if(url != null && !(url.contains(HTTP) || url.contains(HTTPS))) {
      urlWithProtocol = HTTP + url
    }
    urlWithProtocol
  }

  static String removeProtocol(String url) {
    String urlWithoutProtocol = url
    if(url.contains(HTTP)) {
      urlWithoutProtocol = url.replace(HTTP, '')
    } else if(url.contains(HTTPS)) {
      urlWithoutProtocol = url.replace(HTTPS, '')
    }
    urlWithoutProtocol
  }

  static String concatUrlFragment(String url, String fragment) {
    String urlWithFragment = url
    if(url.endsWith('/') && fragment.startsWith('/')) {
      urlWithFragment = url + fragment.substring(1)
    } else if((url.endsWith('/') && !fragment.startsWith('/')) ||
        (!url.endsWith('/') && fragment.startsWith('/'))) {
      urlWithFragment = url + fragment
    } else {
      urlWithFragment = url + '/' + fragment
    }
    urlWithFragment
  }

  static boolean isRelative(String url) {
    !url.contains('http')
  }
}
