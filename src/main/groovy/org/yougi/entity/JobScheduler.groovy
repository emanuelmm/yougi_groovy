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

import org.yougi.reference.JobFrequencyType

import javax.persistence.*

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Inheritance
@Table(name = 'job_scheduler')
@DiscriminatorColumn(name = 'frequency_type')
abstract class JobScheduler implements Serializable, Identified {

  @Id
  String id
  String name

  @ManyToOne
  @JoinColumn(name = 'default_owner')
  private UserAccount defaultOwner

  @Temporal(TemporalType.DATE)
  @Column(name = 'start_date')
  Date startDate

  @Temporal(TemporalType.TIME)
  @Column(name = 'start_time')
  Date startTime

  @Temporal(TemporalType.DATE)
  @Column(name = 'end_date')
  Date endDate

  Integer frequency
  String description
  Boolean active

  JobExecution getNextJobExecution() {
    getNextJobExecution(defaultOwner)
  }

  abstract JobExecution getNextJobExecution(UserAccount owner)

  abstract JobFrequencyType getFrequencyType()

  Boolean getExpired() {
    Calendar today = Calendar.getInstance()
    today.set(Calendar.HOUR_OF_DAY, 23)
    today.set(Calendar.MINUTE, 59)
    today.set(Calendar.SECOND, 59)
    return (endDate != null) && (endDate.compareTo(today.getTime()) > 0)
  }

  /**
   * Checks whether the scheduler interval is valid.
   * @return true the start date is greater than or equal to now and start date is less than or equal to end date.
   * false if otherwise.
   * */
  protected boolean checkInterval() {
    boolean valid = true
    Calendar today = Calendar.getInstance()
    if(today.getTime().compareTo(startDate) > 0) {
      valid = false
    } else if(endDate != null && (startDate.compareTo(endDate) > 0)) {
      valid = false
    }
    valid
  }

  /**
   * @return the original start time of the job execution. The date for the next job execution should be calculated
   * from this date.
   * */
  protected Calendar getJobExecutionStartTime() {
    Calendar jobExecutionStartTime = Calendar.getInstance()
    jobExecutionStartTime.setTime(startDate)
    if(this.startTime != null) {
      Calendar jobStartTime = Calendar.getInstance()
      jobStartTime.setTime(this.startTime)
      jobExecutionStartTime.set(Calendar.HOUR_OF_DAY, jobStartTime.get(Calendar.HOUR_OF_DAY))
      jobExecutionStartTime.set(Calendar.MINUTE, jobStartTime.get(Calendar.MINUTE))
    } else {
      jobExecutionStartTime.set(Calendar.HOUR_OF_DAY, 0)
      jobExecutionStartTime.set(Calendar.MINUTE, 0)
    }
    jobExecutionStartTime
  }

  @Override
  String toString() {
    name
  }
}
