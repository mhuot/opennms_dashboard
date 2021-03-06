/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2011 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.poller.remote;

import org.quartz.JobDetail;

/**
 * <p>PollJobDetail class.</p>
 *
 * @author <a href="mailto:brozow@opennms.org">Mathew Brozowski</a>
 * @version $Id: $
 */
public class PollJobDetail extends JobDetail {
    
    /** Constant <code>GROUP="pollJobGroup"</code> */
    public static final String GROUP = "pollJobGroup";

	private static final long serialVersionUID = -6499411861193543030L;
	
	/**
	 * <p>Constructor for PollJobDetail.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param jobClass a {@link java.lang.Class} object.
	 */
	public PollJobDetail(String name, Class<?> jobClass) {
		super(name, GROUP, jobClass);
	}
	
	/**
	 * <p>setPollService</p>
	 *
	 * @param pollService a {@link org.opennms.netmgt.poller.remote.PollService} object.
	 */
	public void setPollService(PollService pollService) {
		getJobDataMap().put("pollService", pollService);
	}
	
	/**
	 * <p>setPolledService</p>
	 *
	 * @param polledService a {@link org.opennms.netmgt.poller.remote.PolledService} object.
	 */
	public void setPolledService(PolledService polledService) {
		getJobDataMap().put("polledService", polledService);
	}
	
	/**
	 * <p>setPollerFrontEnd</p>
	 *
	 * @param pollerFrontEnd a {@link org.opennms.netmgt.poller.remote.PollerFrontEnd} object.
	 */
	public void setPollerFrontEnd(PollerFrontEnd pollerFrontEnd) {
		getJobDataMap().put("pollerFrontEnd", pollerFrontEnd);
	}
	
}
