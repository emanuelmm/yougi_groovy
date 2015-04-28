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

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Due to the user-unfriendly Java TimeZone implementation, this class was
 * created to represent friendly time zones to end-users. User friendly time zones
 * are stored in the database for immediate changes in case of public time zone
 * changes.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = 'timezone')
class Timezone implements Serializable {
  private static final Integer INTERVAL_RAW_OFFSET = 3600000
  private static final Integer MINUTE_SCALE = 60000

  @Id
  String id
  @Column(name = 'raw_offset')
  Integer rawOffset
  String label
  @Column(name = 'default_tz')
  Boolean defaultTz

  Timezone() {
    rawOffset = 0
  }

  String getOffset() {
    StringBuilder sb = new StringBuilder()
    Integer absRawOffset = Math.abs(this.rawOffset)

    if(this.rawOffset >= 0) {
      sb.append('+')
    } else {
      sb.append('-')
    }

    sb.append(String.format('%02d', absRawOffset/INTERVAL_RAW_OFFSET))
    sb.append(':')

    if((this.rawOffset % INTERVAL_RAW_OFFSET) != 0) {
      sb.append(String.format('%02d', (absRawOffset - ((absRawOffset / INTERVAL_RAW_OFFSET) * INTERVAL_RAW_OFFSET)) / MINUTE_SCALE))
    } else {
      sb.append('00')
    }
    sb.toString()
  }

  String getSign() {
    String sign
    if(this.rawOffset >= 0) {
      sign = '+'
    } else {
      sign = '-'
    }
    sign
  }

  void setSign(String sign) {
    if ('-'.equals(sign)) {
      if (this.rawOffset < 0) {
        this.rawOffset *= -1
      }
    } else {
      if(this.rawOffset >= 0) {
        this.rawOffset *= +1
      }
    }
  }

  Integer getOffsetHour() {
    Integer absRawOffset = Math.abs(this.rawOffset)
    return absRawOffset / INTERVAL_RAW_OFFSET
  }

  void setOffsetHour(Integer offsetHour) {
    Integer rest = 0
    if((this.rawOffset % INTERVAL_RAW_OFFSET) != 0) {
      Integer absRawOffset = Math.abs(this.rawOffset)
      rest = absRawOffset - ((absRawOffset / INTERVAL_RAW_OFFSET) * INTERVAL_RAW_OFFSET)
    }

    Integer newRawOffset = (offsetHour * INTERVAL_RAW_OFFSET) + rest

    if(this.rawOffset >= 0) {
      this.rawOffset = newRawOffset
    } else {
      this.rawOffset = newRawOffset * -1
    }
  }

  Integer getOffsetMinute() {
    Integer absRawOffset = Math.abs(this.rawOffset)
    Integer minutes = 0
    if((this.rawOffset % INTERVAL_RAW_OFFSET) != 0) {
      minutes = (absRawOffset - ((absRawOffset / INTERVAL_RAW_OFFSET) * INTERVAL_RAW_OFFSET)) / MINUTE_SCALE
    }
    minutes
  }

  void setOffsetMinute(Integer offsetMinute) {
    Integer offsetHour = getOffsetHour()
    if(this.rawOffset >= 0) {
      this.rawOffset = (offsetHour * INTERVAL_RAW_OFFSET) + (offsetMinute * MINUTE_SCALE)
    } else {
      this.rawOffset = ((offsetHour * INTERVAL_RAW_OFFSET) + (offsetMinute * MINUTE_SCALE)) * -1
    }
  }

  String getOffsetLabel() {
    '('+ getOffset() +') '+ label
  }

  TimeZone getTimeZone() {
    TimeZone.getTimeZone(id)
  }

  @Override
  String toString() {
    '('+ getOffset() +') '+ label
  }
}
