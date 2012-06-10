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

package org.opennms.util.ilr;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opennms.util.ilr.LogMessage;
import org.opennms.util.ilr.SimpleLogMessage;


public class SimpleLogMessageTest {
	@Test
	public void testGetService() {
		LogMessage log = SimpleLogMessage.create("2010-05-26 12:12:38,027 DEBUG [CollectdScheduler-50 Pool-fiber11] Collectd: collector.collect: collectData: end: 24/216.216.217.254/SNMP");
		assertEquals("24/216.216.217.254/SNMP",log.getServiceID());
	}
	//WRITE THIS TEST
	@Test
	public void testGetDate() {
	    
	}
	//WRITE THIS TEST
	@Test
	public void testGetServiceID() {
	    
	}
	//WRITE THIS TEST
	@Test
	public void testGetThread() {
	    
	}
}
