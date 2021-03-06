/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2011 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.web.svclayer.support;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opennms.api.reporting.ReportMode;
import org.opennms.api.reporting.parameter.ReportParameters;
import org.opennms.core.utils.LogUtils;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.reporting.core.DeliveryOptions;
import org.opennms.reporting.core.svclayer.ReportServiceLocatorException;
import org.opennms.reporting.core.svclayer.ReportWrapperService;
import org.opennms.web.svclayer.SchedulerService;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.webflow.execution.RequestContext;

/**
 * <p>DefaultSchedulerService class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.8.1
 */
public class DefaultSchedulerService implements InitializingBean, SchedulerService {

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String PARAMETER_ERROR = "Report parameters did not match the definition for the report please contact your OpenNMS administrator";
    private static final String SCHEDULER_ERROR = "An exception occurred when scheduling the report";
    private static final String TRIGGER_PARSE_ERROR = "An error occurred parsing the cron expression. It was not possible to schedule the report";
    private static final String REPORTID_ERROR = "An error occurred locating the report service bean";
    
    private Scheduler m_scheduler;
    private JobDetail m_jobDetail;
    private String m_triggerGroup;
    private ReportWrapperService m_reportWrapperService;

    /**
     * <p>afterPropertiesSet</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        log().debug("Adding job " + m_jobDetail.getName() + " to scheduler");
        m_scheduler.addJob(m_jobDetail, true);

    }

    /**
     * <p>getTriggerDescriptions</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<TriggerDescription> getTriggerDescriptions() {

        List<TriggerDescription> triggerDescriptions = new ArrayList<TriggerDescription>();

        try {
            String[] triggerNames = m_scheduler.getTriggerNames(m_triggerGroup);
            for (int j = 0; j < triggerNames.length; j++) {
                TriggerDescription description = new TriggerDescription();
                description.setNextFireTime(m_scheduler.getTrigger(
                                                                   triggerNames[j],
                                                                   m_triggerGroup).getNextFireTime());
                description.setTriggerName(triggerNames[j]);
                triggerDescriptions.add(description);

            }
        } catch (SchedulerException e) {
            log().error("exception lretrieving trigger descriptions", e);
        }

        return triggerDescriptions;

    }

    /** {@inheritDoc} */
    public Boolean exists(String triggerName) {

        Boolean found = false;

        try {
            Trigger trigger = m_scheduler.getTrigger(triggerName,
                                                     m_triggerGroup);
            if (trigger != null) {
                found = true;
            }
        } catch (SchedulerException e) {
            log().error("exception looking up trigger name: " + triggerName);
            log().error(e.getMessage());
        }

        return found;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.opennms.web.svclayer.support.SchedulerService#removeTrigger(java
     * .lang.String)
     */
    /** {@inheritDoc} */
    public void removeTrigger(String triggerName) {
        try {
            m_scheduler.unscheduleJob(triggerName, m_triggerGroup);
        } catch (SchedulerException e) {
            log().error(
                        "exception when attempting to remove trigger "
                                + triggerName);
            log().error(e.getMessage());
        }

    }

    /**
     * <p>removeTriggers</p>
     *
     * @param triggerNames an array of {@link java.lang.String} objects.
     */
    public void removeTriggers(String[] triggerNames) {
        for (String triggerName : triggerNames) {
            removeTrigger(triggerName);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.opennms.web.svclayer.support.SchedulerService#addCronTrigger(org
     * .opennms.web.report.database.model.DatabaseReportCriteria,
     * java.lang.String, java.lang.String, java.lang.String,
     * org.springframework.webflow.execution.RequestContext)
     */
    /** {@inheritDoc} */
    public String addCronTrigger(String id, ReportParameters criteria,
            DeliveryOptions deliveryOptions,
            String cronExpression, RequestContext context) {

        CronTrigger cronTrigger = null;
        
        try {            
            if (m_reportWrapperService.validate(criteria,id) == false ) {
                log().error(PARAMETER_ERROR);
                context.getMessageContext().addMessage(
                                                       new MessageBuilder().error().defaultText(
                                                                                                PARAMETER_ERROR).build());
                return ERROR;
            } else {
                try {
                    cronTrigger = new CronTrigger();
                    cronTrigger.setGroup(m_triggerGroup);
                    cronTrigger.setName(deliveryOptions.getInstanceId());
                    cronTrigger.setJobName(m_jobDetail.getName());
                    cronTrigger.setCronExpression(cronExpression);
                    // cronTrigger = new CronTrigger(triggerName, m_triggerGroup,
                    // cronExpression);
                } catch (ParseException e) {
                    log().error(TRIGGER_PARSE_ERROR, e);
                    context.getMessageContext().addMessage(
                                                           new MessageBuilder().error().defaultText(
                                                                                                    TRIGGER_PARSE_ERROR).build());
                    return ERROR;
                }

                cronTrigger.setJobName(m_jobDetail.getName());
                cronTrigger.getJobDataMap().put("criteria", (ReportParameters) criteria);
                cronTrigger.getJobDataMap().put("reportId", id);
                cronTrigger.getJobDataMap().put("mode", ReportMode.SCHEDULED);
                cronTrigger.getJobDataMap().put("deliveryOptions",
                                                (DeliveryOptions) deliveryOptions);
                try {
                    m_scheduler.scheduleJob(cronTrigger);
                } catch (SchedulerException e) {
                    log().error(SCHEDULER_ERROR, e);
                    context.getMessageContext().addMessage(
                                                           new MessageBuilder().error().defaultText(
                                                                                                    SCHEDULER_ERROR).build());
                    return ERROR;
                }

                return SUCCESS;
            }
        } catch (ReportServiceLocatorException e) {
            log().error(REPORTID_ERROR);
            context.getMessageContext().addMessage(
                                                   new MessageBuilder().error().defaultText(
                                                                                            REPORTID_ERROR).build());
            return ERROR;
        }

        
    }

    /*
     * (non-Javadoc)
     * @see
     * org.opennms.web.svclayer.support.SchedulerService#execute(org.opennms
     * .web.report.database.model.DatabaseReportCriteria, java.lang.String,
     * org.springframework.webflow.execution.RequestContext)
     */
    /** {@inheritDoc} */
    public String execute(String id, ReportParameters criteria,
            DeliveryOptions deliveryOptions, RequestContext context) {

        try {
            if (m_reportWrapperService.validate(criteria,id) == false ) {
                context.getMessageContext().addMessage(new MessageBuilder().error().defaultText(PARAMETER_ERROR).build());
                return ERROR;
            } else {
                SimpleTrigger trigger = new SimpleTrigger(deliveryOptions.getInstanceId(), m_triggerGroup, new Date(), null, 0, 0L);
                trigger.setJobName(m_jobDetail.getName());
                trigger.getJobDataMap().put("criteria", (ReportParameters) criteria);
                trigger.getJobDataMap().put("reportId", id);
                trigger.getJobDataMap().put("mode", ReportMode.IMMEDIATE);
                trigger.getJobDataMap().put("deliveryOptions", (DeliveryOptions) deliveryOptions);
                try {
                    m_scheduler.scheduleJob(trigger);
                } catch (SchedulerException e) {
                    LogUtils.warnf(this, e, SCHEDULER_ERROR);
                    context.getMessageContext().addMessage(new MessageBuilder().error().defaultText(SCHEDULER_ERROR).build());
                    return ERROR;
                }

                return SUCCESS;
            }
        } catch (ReportServiceLocatorException e) {
            LogUtils.errorf(this, e, REPORTID_ERROR);
            context.getMessageContext().addMessage(new MessageBuilder().error().defaultText(REPORTID_ERROR).build());
            return ERROR;
        }


    }

    private ThreadCategory log() {
        return ThreadCategory.getInstance();
    }

    /**
     * <p>setScheduler</p>
     *
     * @param scheduler a {@link org.quartz.Scheduler} object.
     */
    public void setScheduler(Scheduler scheduler) {
        m_scheduler = scheduler;
    }

    /**
     * <p>setJobDetail</p>
     *
     * @param reportJob a {@link org.quartz.JobDetail} object.
     */
    public void setJobDetail(JobDetail reportJob) {
        m_jobDetail = reportJob;
    }

    /**
     * <p>setTriggerGroup</p>
     *
     * @param triggerGroup a {@link java.lang.String} object.
     */
    public void setTriggerGroup(String triggerGroup) {
        m_triggerGroup = triggerGroup;
    }

    /**
     * <p>setReportWrapperService</p>
     *
     * @param reportWrapperService a {@link org.opennms.reporting.core.svclayer.ReportWrapperService} object.
     */
    public void setReportWrapperService(ReportWrapperService reportWrapperService) {
        m_reportWrapperService = reportWrapperService;
    }


}
