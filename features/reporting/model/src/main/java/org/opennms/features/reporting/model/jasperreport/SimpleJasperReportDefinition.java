/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2007-2011 The OpenNMS Group, Inc.
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

package org.opennms.features.reporting.model.jasperreport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.features.reporting.model.basicreport.BasicReportDefinition;

@XmlRootElement(name = "simple-jasper-report")
public class SimpleJasperReportDefinition implements BasicReportDefinition,
        JasperReportDefinition {

    private String m_id;
    private String m_repositoryId;
    private String m_engine;
    private String m_template;
    private String m_description;
    private String m_displayName;
    private String m_reportService;
    private boolean m_online;
    private boolean m_allowAccess;
    
    @XmlElement(name = "description")
    public String getDescription() {
        return m_description;
    }
    @XmlElement(name = "display-name")
    public String getDisplayName() {
        return m_displayName;
    }
    @XmlElement(name = "engine")
    public String getEngine() {
        return m_engine;
    }
    @XmlElement(name = "id")
    public String getId() {
        return m_id;
    }
    @XmlElement(name = "online")
    public boolean getOnline() {
        return m_online;
    }
    @XmlElement(name = "report-service")
    public String getReportService() {
        return m_reportService;
    }
    public String getRepositoryId() {
        return m_repositoryId;
    }
    @XmlElement(name = "template")
    public String getTemplate() {
        return m_template;
    }
    public void setDescription(String description) {
        m_description = description;
    }

    @Override
    public boolean getAllowAccess() {
        return m_allowAccess;
    }

    @Override
    public void setAllowAccess(boolean allowAccess) {
        m_allowAccess = allowAccess;
    }

    public void setDisplayName(String displayName) {
        m_displayName = displayName;
    }
    public void setEngine(String engine) {
        m_engine = engine;
    }
    public void setId(String id) {
        m_id = id;
    }
    public void setOnline(boolean online) {
        m_online = online;
    }
    public void setReportService(String reportService) {
        m_reportService = reportService;
    }
    public void setTemplate(String template) {
        m_template = template;
    }

    @Override
    public String toString() {
        return "SimpleJasperReportDefinition [m_id=" + m_id
                + ", m_repositoryId=" + m_repositoryId + ", m_engine="
                + m_engine + ", m_template=" + m_template
                + ", m_description=" + m_description + ", m_displayName="
                + m_displayName + ", m_reportService=" + m_reportService
                + ", m_online=" + m_online + "]";
    }
}
