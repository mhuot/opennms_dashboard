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

package org.opennms.netmgt.dao;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import org.opennms.netmgt.model.OnmsAtInterface;

public interface AtInterfaceDao extends OnmsDao<OnmsAtInterface, Integer> {

	void markDeletedIfNodeDeleted();

    void deactivateForNodeIdIfOlderThan(int nodeid, Timestamp scanTime);

    Collection<OnmsAtInterface> findByMacAddress(final String macAddress);

    void setStatusForNode(Integer nodeid, Character action);

    void setStatusForNodeAndIp(Integer nodeid, String ipAddr, Character action);

    void setStatusForNodeAndIfIndex(Integer nodeid, Integer ifIndex, Character action);

    void saveAtInterface(Connection dbConn, OnmsAtInterface at) throws SQLException;

    OnmsAtInterface findByNodeAndAddress(final Integer nodeId, final InetAddress ipAddress, final String macAddress);

    /**
     * Get the {@link OnmsAtInterface} that goes with a given address and
     * node. If it does not exist, but the IP interface does exist, then
     * create it. If an equivalent IP interface does *not* exist, returns
     * null.
     * 
     * @param dbConn
     *            the database connection, if necessary
     * @param ipaddress
     *            the IP address to look up
     * @param node
     *            the {@link LinkableNode} associated with the interface (if
     *            known)
     * @return an {@link OnmsAtInterface}
     * @throws SQLException
     */
    OnmsAtInterface getAtInterfaceForAddress(final Connection dbConn, final InetAddress address);
}
