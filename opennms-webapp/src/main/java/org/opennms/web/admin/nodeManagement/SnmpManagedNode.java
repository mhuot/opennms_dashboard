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

package org.opennms.web.admin.nodeManagement;

import java.util.ArrayList;
import java.util.List;

/**
 * A servlet that stores node, interface, service information for setting up
 * SNMP data collection
 *
 * @author <A HREF="mailto:tarus@opennms.org">Tarus Balog </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 * @author <A HREF="mailto:tarus@opennms.org">Tarus Balog </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 * @version $Id: $
 * @since 1.8.1
 */
public class SnmpManagedNode {
    /**
     */
    protected int nodeID;

    /**
     */
    protected String nodeLabel;

    /**
     * 
     */
    protected List<SnmpManagedInterface> interfaces;

    /**
     * <p>Constructor for SnmpManagedNode.</p>
     */
    public SnmpManagedNode() {
        interfaces = new ArrayList<SnmpManagedInterface>();
    }

    /**
     * <p>Setter for the field <code>nodeID</code>.</p>
     *
     * @param id a int.
     */
    public void setNodeID(int id) {
        nodeID = id;
    }

    /**
     * <p>Setter for the field <code>nodeLabel</code>.</p>
     *
     * @param label a {@link java.lang.String} object.
     */
    public void setNodeLabel(String label) {
        nodeLabel = label;
    }

    /**
     * <p>addInterface</p>
     *
     * @param newInterface a {@link org.opennms.web.admin.nodeManagement.SnmpManagedInterface} object.
     */
    public void addInterface(SnmpManagedInterface newInterface) {
        interfaces.add(newInterface);
    }

    /**
     * <p>getInterfaceCount</p>
     *
     * @return a int.
     */
    public int getInterfaceCount() {
        return interfaces.size();
    }

    /**
     * <p>Getter for the field <code>nodeID</code>.</p>
     *
     * @return a int.
     */
    public int getNodeID() {
        return nodeID;
    }

    /**
     * <p>Getter for the field <code>nodeLabel</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getNodeLabel() {
        return nodeLabel;
    }

    /**
     * <p>Getter for the field <code>interfaces</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<SnmpManagedInterface> getInterfaces() {
        return interfaces;
    }
}
