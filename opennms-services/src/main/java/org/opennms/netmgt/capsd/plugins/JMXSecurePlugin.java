/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2011 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.capsd.plugins;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.opennms.core.utils.ParameterMap;
import org.opennms.protocols.jmx.connectors.ConnectionWrapper;
import org.opennms.protocols.jmx.connectors.JMXSecureConnectionFactory;

/**
 * <p>JMXSecurePlugin class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
public class JMXSecurePlugin extends JMXPlugin
{

	/** {@inheritDoc} */
	public ConnectionWrapper getMBeanServerConnection(Map<String,Object> parameterMap, InetAddress address)
	{
		return JMXSecureConnectionFactory.getMBeanServerConnection(parameterMap, address);
	}

	/** {@inheritDoc} */
	public String getProtocolName(Map<String,Object> map)
	{
		return ParameterMap.getKeyedString(map, "friendly-name", "ssl-jmxmp");
	}

	/** {@inheritDoc} */
	public boolean isProtocolSupported(InetAddress address)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("username", "jmxmp");
		map.put("password", "jmxmp");
		map.put("friendly-name", "ssl-jmxmp");

		return isProtocolSupported(address, map);
	}
}
