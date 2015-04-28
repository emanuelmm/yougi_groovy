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

import org.yougi.business.JobExecutionBean
import org.yougi.business.JobSchedulerBean
import org.yougi.business.UserAccountBean
import org.yougi.entity.*
import org.yougi.reference.JobFrequencyType
import org.yougi.reference.JobStatus
import org.yougi.util.DateTimeUtils
import org.yougi.util.EntitySupport
import org.yougi.util.ResourceBundleHelper
import org.yougi.annotation.ManagedProperty

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.faces.application.FacesMessage
import javax.faces.component.UIComponent
import javax.faces.context.FacesContext
import javax.faces.validator.ValidatorException
import javax.inject.Inject
import javax.inject.Named
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class JobSchedulerMBean {

  private static final Logger LOGGER = Logger.getLogger(JobSchedulerMBean.class.getSimpleName())

  @EJB
  private JobSchedulerBean jobSchedulerBean
  @EJB
  private JobExecutionBean jobExecutionBean
  @EJB
  private UserAccountBean userAccountBean

  List<JobScheduler> jobsScheduled
  List<JobScheduler> jobsExpired
  List<JobExecution> jobExecutions
  List<String> jobNames
  List<UserAccount> userAccounts
  Boolean workingHoursOnly

  @Inject
  @ManagedProperty('#{param.id}')
  String id
  @Inject
  JobScheduleMBean jobScheduleMBean

  Date startDate
  Date startTime
  Date endDate
  Date endTime

  JobSchedulerHourly getJobSchedulerHourly() {
    JobScheduler jobScheduler = this.getJobScheduler()
    if(jobScheduler instanceof JobSchedulerHourly) {
      return (JobSchedulerHourly) jobScheduler
    }
    null
  }

  JobSchedulerDaily getJobSchedulerDaily() {
    JobScheduler jobScheduler = this.getJobScheduler()
    if(jobScheduler instanceof JobSchedulerDaily) {
      return (JobSchedulerDaily) jobScheduler
    }
    null
  }

  String getSelectedOwner() {
    UserAccount userAccount = jobScheduleMBean.getDefaultOwner()
    if(userAccount != null) {
      return userAccount.getId()
    } else {
      return null
    }
  }

  void setSelectedOwner(String selectedOwner) {
    jobScheduleMBean.setDefaultOwner(selectedOwner)
  }

  JobFrequencyType getFrequencyType() {
    jobScheduleMBean.getJobScheduler().getFrequencyType()
  }

  void setFrequencyType(JobFrequencyType frequencyType) {
    jobScheduleMBean.changeJobFrequencyType(frequencyType)
  }

  List<JobScheduler> getJobsScheduled() {
    if(jobsScheduled == null) {
      jobsScheduled = jobSchedulerBean.findAllScheduled()
    }
    jobsScheduled
  }

  List<JobScheduler> getJobsExpired() {
    if(jobsExpired == null) {
      jobsExpired = jobSchedulerBean.findAllExpired()
    }
    jobsExpired
  }

  List<JobExecution> getJobExecutions() {
    if(jobExecutions == null) {
      jobExecutions = jobExecutionBean.findJobExecutions(this.jobScheduleMBean.getJobScheduler())
      for(JobExecution jobExecution : this.jobExecutions) {
        if(jobExecution.getStatus().equals(JobStatus.SCHEDULED)) {
          jobExecution.setTimeout(jobExecutionBean.findTimeout(jobExecution))
        }
      }
    }
    jobExecutions
  }

  List<String> getJobNames() {
    if(jobNames == null) {
      jobNames = jobSchedulerBean.getJobXmlNames()
      // If the job is already scheduled it is removed from the list.
      List<JobScheduler> scheduled = jobSchedulerBean.findAllScheduled()
      for(JobScheduler jobScheduler : scheduled) {
        for(String jobName: jobNames) {
          if(jobName.equals(jobScheduler.getName())) {
            jobNames.remove(jobName)
            break
          }
        }
      }
    }
    jobNames
  }

  List<UserAccount> getUserAccounts() {
    if(userAccounts == null) {
      userAccounts = userAccountBean.findAllActiveAccounts()
    }
    userAccounts
  }

  void validateStartDate(FacesContext context, UIComponent component, Object value) {
    startDate = (Date)value
    LOGGER.log(Level.INFO, 'startDate {0}', this.startDate)
  }

  void validateStartTime(FacesContext context, UIComponent component, Object value) {
    startTime = (Date)value
    LOGGER.log(Level.INFO, 'startTime {0}', this.startTime)
  }

  void validateEndDate(FacesContext context, UIComponent component, Object value) {
    endDate = (Date)value
    LOGGER.log(Level.INFO, 'endDate {0}', endDate)
  }

  void validateEndTime(FacesContext context, UIComponent component, Object value) {
    endTime = (Date) value
    LOGGER.log(Level.INFO, 'endTime {0}', this.endTime)

    Date startDateAndTime = DateTimeUtils.mergeDateAndTime(this.startDate, this.startTime)
    LOGGER.log(Level.INFO, 'startDateAndTime {0}', startDateAndTime)

    Date endDateAndTime = DateTimeUtils.mergeDateAndTime(this.endDate, this.endTime)
    LOGGER.log(Level.INFO, 'endDateAndTime {0}', endDateAndTime)

    if (startDateAndTime.compareTo(endDateAndTime) > 0) {
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.getMessage('errorCode0015'), null))
    }
  }

  @PostConstruct
  void load() {
    if(EntitySupport.isIdValid(id)) {
      jobScheduleMBean.loadJobScheduler(id)

      if(jobScheduleMBean.getJobScheduler() instanceof JobSchedulerHourly) {
          JobSchedulerHourly jobSchedulerHourly = (JobSchedulerHourly) this.jobScheduleMBean.getJobScheduler()
          this.workingHoursOnly = jobSchedulerHourly.getWorkingHoursOnly()
          this.endTime = jobSchedulerHourly.getEndTime()
      } else if(this.jobScheduleMBean.getJobScheduler() instanceof JobSchedulerDaily) {
          JobSchedulerDaily jobSchedulerDaily = (JobSchedulerDaily) this.jobScheduleMBean.getJobScheduler()
          this.workingHoursOnly = jobSchedulerDaily.getWorkingDaysOnly()
      }
    }
  }

  String save() {
    if(this.jobScheduleMBean.getJobScheduler().getFrequencyType() == JobFrequencyType.HOURLY) {
      JobSchedulerHourly jobSchedulerHourly = (JobSchedulerHourly) jobScheduleMBean.getJobScheduler()
      jobSchedulerHourly.setWorkingHoursOnly(this.workingHoursOnly)
      jobSchedulerHourly.setEndTime(this.endTime)
    }
    else if(this.jobScheduleMBean.getJobScheduler().getFrequencyType() == JobFrequencyType.DAILY) {
      JobSchedulerDaily jobSchedulerDaily = (JobSchedulerDaily) jobScheduleMBean.getJobScheduler()
      jobSchedulerDaily.setWorkingDaysOnly(this.workingHoursOnly)
    }

    if(EntitySupport.isIdNotValid(jobScheduleMBean.getJobScheduler())) {
      jobSchedulerBean.save(jobScheduleMBean.getJobScheduler())
    } else {
      jobSchedulerBean.update(jobScheduleMBean.getJobScheduler())
    }
    'job_schedulers'
  }

  String activate() {
    String id = this.jobScheduleMBean.getJobScheduler().getId()
    JobScheduler jobScheduler = jobSchedulerBean.find(id)
    jobSchedulerBean.activate(jobScheduler)
    'job_scheduler?faces-redirect=true&id='+ id
  }

  String deactivate() {
    String id = this.jobScheduleMBean.getJobScheduler().getId()
    JobScheduler jobScheduler = jobSchedulerBean.find(id)
    jobSchedulerBean.deactivate(jobScheduler)
    'job_scheduler?faces-redirect=true&id='+ id
  }

  String schedule() {
    String id = this.jobScheduleMBean.getJobScheduler().getId()
    JobScheduler jobScheduler = jobSchedulerBean.find(id)
    jobExecutionBean.schedule(jobScheduler)
    'job_scheduler?faces-redirect=true&id='+ id
  }

  String remove() {
    jobSchedulerBean.remove(jobScheduleMBean.jobScheduler.id)
    'job_schedulers'
  }
}
