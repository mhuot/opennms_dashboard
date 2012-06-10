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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.opennms.netmgt.model.ReportCatalogEntry;
import org.opennms.reporting.core.svclayer.ReportStoreService;
import org.opennms.web.command.ManageDatabaseReportCommand;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * <p>ManageDatabaseReportController class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.8.1
 */
public class ManageDatabaseReportController extends SimpleFormController {

    private int m_pageSize;
    private ReportStoreService m_reportStoreService;
    
    /**
     * <p>Constructor for ManageDatabaseReportController.</p>
     */
    public ManageDatabaseReportController() {
        setFormView("report/database/manage");
    }

    /**
     * <p>setReportStoreService</p>
     *
     * @param reportStoreService a {@link org.opennms.reporting.core.svclayer.ReportStoreService} object.
     */
    public void setReportStoreService(ReportStoreService reportStoreService) {
        m_reportStoreService = reportStoreService;
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
        List<ReportCatalogEntry> reportCatalog = m_reportStoreService.getAll();
        Map<String, Object> formatMap = m_reportStoreService.getFormatMap();
        PagedListHolder<ReportCatalogEntry> pagedListHolder = new PagedListHolder<ReportCatalogEntry>(reportCatalog);
        pagedListHolder.setPageSize(m_pageSize);
        int page = ServletRequestUtils.getIntParameter(req, "p", 0);
        pagedListHolder.setPage(page); 
        data.put("formatMap", formatMap);
        data.put("pagedListHolder", pagedListHolder);
        return data;

    }
    
    /** {@inheritDoc} */
    @Override
    protected ModelAndView onSubmit(Object command) throws Exception {
        ManageDatabaseReportCommand manageCommand = (ManageDatabaseReportCommand) command;
        m_reportStoreService.delete(manageCommand.getIds());
        ModelAndView mav = new ModelAndView(getSuccessView());
        return mav;
    }
    
    
}
