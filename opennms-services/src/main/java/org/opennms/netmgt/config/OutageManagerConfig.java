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

package org.opennms.netmgt.config;

/**
 * <p>OutageManagerConfig interface.</p>
 *
 * @author brozow
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @version $Id: $
 */
public interface OutageManagerConfig {
    /**
     * Return the number of writer threads to be started.
     *
     * @return the number of writer threads to be started
     */
    public int getWriters();

    /**
     * Return the SQL statemet to get the next outage ID.
     *
     * @return the SQL statemet to get the next outage ID
     */
    public String getGetNextOutageID();

    /**
     * Return a boolean flag to indicate if a deleteService should be propagated
     * to the interface or node level deletion when approciate.
     *
     * @return true for delete propagation otherwise false.
     */
    public boolean deletePropagation();
}
