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

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opennms.api.reporting.ReportFormat;
import org.opennms.core.utils.WebSecurityUtils;
import org.opennms.reporting.core.svclayer.ReportStoreService;
import org.opennms.web.servlet.MissingParameterException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * <p>DownloadReportController class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.8.1
 */
public class DownloadReportController extends AbstractController {

    private ReportStoreService m_reportStoreService;

    /** {@inheritDoc} */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String[] requiredParameters = new String[] { "locatorId", "format" };

        for (String requiredParameter : requiredParameters) {
            if (request.getParameter(requiredParameter) == null) {
                throw new MissingParameterException(requiredParameter,
                                                    requiredParameters);
            }
        }

        try {
            Integer reportCatalogEntryId = new Integer(WebSecurityUtils.safeParseInt(request.getParameter("locatorId")));
            
            String requestFormat = new String(request.getParameter("format"));

            if ((ReportFormat.PDF == ReportFormat.valueOf(requestFormat))
                    || (ReportFormat.SVG == ReportFormat.valueOf(requestFormat)) ) {
                response.setContentType("application/pdf;charset=UTF-8");
                response.setHeader("Content-disposition", "inline; filename="
                                   + reportCatalogEntryId.toString()
                                   + ".pdf");
                response.setHeader("Pragma", "public");
                response.setHeader("Cache-Control", "cache");
                response.setHeader("Cache-Control", "must-revalidate");
            }
            if(ReportFormat.CSV == ReportFormat.valueOf(requestFormat)) {
                response.setContentType("text/csv;charset=UTF-8");
                response.setHeader("Content-disposition", "inline; filename="
                                   + reportCatalogEntryId.toString()
                                   + ".csv");
                response.setHeader("Pragma", "public");
                response.setHeader("Cache-Control", "cache");
                response.setHeader("Cache-Control", "must-revalidate");
            }
            m_reportStoreService.render(
                                        reportCatalogEntryId,
                                        ReportFormat.valueOf(requestFormat),
                                        (OutputStream) response.getOutputStream());
        } catch (NumberFormatException e) {
            // TODO something useful here.
        }

        return null;
    }
    
    /**
     * <p>setReportStoreService</p>
     *
     * @param reportStoreService a {@link org.opennms.reporting.core.svclayer.ReportStoreService} object.
     */
    public void setReportStoreService(ReportStoreService reportStoreService) {
        m_reportStoreService = reportStoreService;
    }

}
