/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.jasper.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceIdParser {
	
	Pattern m_nodePattern;
	Pattern m_resourcePattern;
	
	public ResourceIdParser() {
		m_nodePattern = Pattern.compile("node\\W(\\d.*?)\\W");
		m_resourcePattern = Pattern.compile("responseTime\\W(.*)\\W");
	}
	
	public String getNodeId(String resourceId) {
		return getMatch(m_nodePattern.matcher(resourceId));
	}

	public String getResource(String resourceId) {
		return getMatch(m_resourcePattern.matcher(resourceId));
	}
	
	private String getMatch(Matcher m) {
		m.find();
		return m.group(1);
	}
}
