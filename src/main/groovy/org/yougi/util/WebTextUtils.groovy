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
@Singleton
class WebTextUtils {

  /**
   * This method replaces every line break in the text by a html paragraph.
   * Empty lines are ignored. It returns a text that appears formatted in a
   * html page.
   */
  String convertLineBreakToHTMLParagraph(String str) {
    def result
    if (!str) {
      result = null
    }
    StringBuilder formattedStr = new StringBuilder()
    StringTokenizer st = new StringTokenizer(str, '\n')
    String token
    while (st.hasMoreTokens()) {
      token = st.nextToken().trim()
      if (token) {
        formattedStr.append('<p>')
        formattedStr.append(token)
        formattedStr.append('</p>')
      }
    }
    result = formattedStr.toString()
    result
  }
}
