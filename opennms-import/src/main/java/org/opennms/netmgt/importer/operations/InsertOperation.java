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

package org.opennms.netmgt.importer.operations;

import java.util.LinkedList;
import java.util.List;

import org.opennms.netmgt.model.EntityVisitor;
import org.opennms.netmgt.model.OnmsDistPoller;
import org.opennms.netmgt.xml.event.Event;

/**
 * <p>InsertOperation class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
public class InsertOperation extends AbstractSaveOrUpdateOperation {
    
    /**
     * <p>Constructor for InsertOperation.</p>
     *
     * @param foreignSource a {@link java.lang.String} object.
     * @param foreignId a {@link java.lang.String} object.
     * @param nodeLabel a {@link java.lang.String} object.
     * @param building a {@link java.lang.String} object.
     * @param city a {@link java.lang.String} object.
     */
    public InsertOperation(String foreignSource, String foreignId, String nodeLabel, String building, String city) {
		super(foreignSource, foreignId, nodeLabel, building, city);
	}

	/**
	 * <p>doPersist</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<Event> doPersist() {
        OnmsDistPoller distPoller = getDistPollerDao().get("localhost");
        getNode().setDistPoller(distPoller);
        getNodeDao().save(getNode());
        
    	final List<Event> events = new LinkedList<Event>();

    	EntityVisitor eventAccumlator = new AddEventVisitor(events);

    	getNode().visit(eventAccumlator);
        
    	return events;
    }

    /**
     * <p>toString</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString() {
	return "INSERT: Node: "+getNode().getLabel();
    }

}
