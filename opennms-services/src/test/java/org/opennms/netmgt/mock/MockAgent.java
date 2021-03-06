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

package org.opennms.netmgt.mock;

import java.util.SortedMap;
import java.util.TreeMap;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

/**
 * Represents a MockAgent 
 *
 * @author brozow
 */
public class MockAgent {
    
    private SortedMap<OID, SubAgent> m_subAgents = new TreeMap<OID, SubAgent>();

    public void addSubAgent(SubAgent subAgent) {
        m_subAgents.put(subAgent.getBaseOID(), subAgent);
        
    }

    /**
     * @param oid
     * @return
     */
    public VariableBinding getNext(OID oid) {
        VariableBinding result = null;
        for (SubAgent subAgent : m_subAgents.values()) {
            result = subAgent.getNext(oid);
            if (result != null) {
                return result;
            }
        }
        
        return result;
    }

    /**
     * @param oid
     * @return
     */
    public VariableBinding get(OID oid) {
        for (OID agentKey : m_subAgents.keySet()) {
            if (oid.startsWith(agentKey)) {
                SubAgent subAgent = m_subAgents.get(agentKey);
                return subAgent.get(oid);
            }
        }
        return null;
    }


}
