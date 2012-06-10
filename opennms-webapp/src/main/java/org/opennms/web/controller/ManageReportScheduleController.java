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

package org.opennms.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.opennms.web.command.ManageReportScheduleCommand;
import org.opennms.web.svclayer.SchedulerService;
import org.opennms.web.svclayer.support.TriggerDescription;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * <p>ManageReportScheduleController class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.8.1
 */
public class ManageReportScheduleController extends SimpleFormController {
    
    private SchedulerService m_reportSchedulerService;
    private int m_pageSize;
    
    /**
     * <p>Constructor for ManageReportScheduleController.</p>
     */
    public ManageReportScheduleController() {
        setFormView("report/database/manageSchedule");
    }

    /**
     * <p>setReportSchedulerService</p>
     *
     * @param schedulerService a {@link org.opennms.web.svclayer.SchedulerService} object.
     */
    public void setReportSchedulerService(SchedulerService schedulerService) {
        m_reportSchedulerService = schedulerService;
    }
    /**
     * <p>setPageSize</p>
     *
     * @param pageSize a int.
     */
    public void setPageSize(int pageSize) {
        m_pageSize = pageSize;
    }
    
    /** {@inheritDoc} */
    @Override
    protected Map<String, Object> referenceData(HttpServletRequest req) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        PagedListHolder<TriggerDescription> pagedListHolder = new PagedListHolder<TriggerDescription>(m_reportSchedulerService.getTriggerDescriptions());
        pagedListHolder.setPageSize(m_pageSize);
        int page = ServletRequestUtils.getIntParameter(req, "p", 0);
        pagedListHolder.setPage(page); 
        data.put("pagedListHolder", pagedListHolder);
        return data;

    }
    
    /** {@inheritDoc} */
    @Override
    protected ModelAndView onSubmit(Object command) throws Exception {
        ManageReportScheduleCommand manageCommand = (ManageReportScheduleCommand) command;
        m_reportSchedulerService.removeTriggers((manageCommand.getTriggerNames()));
        ModelAndView mav = new ModelAndView(getSuccessView());
        return mav;
    }

}
