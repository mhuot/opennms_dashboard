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

package org.opennms.netmgt.importer;

import org.opennms.netmgt.eventd.EventIpcManager;




/**
 * Model Importer
 *
 * @author ranger
 * @version $Id: $
 */
public class ModelImporter extends BaseImporter {

    private EventIpcManager m_eventManager;

	/**
	 * <p>Constructor for ModelImporter.</p>
	 */
	public ModelImporter() {
    }

	/**
	 * <p>getEventManager</p>
	 *
	 * @return a {@link org.opennms.netmgt.eventd.EventIpcManager} object.
	 */
	public EventIpcManager getEventManager() {
	    return m_eventManager;
	}

	/**
	 * <p>setEventManager</p>
	 *
	 * @param eventManager a {@link org.opennms.netmgt.eventd.EventIpcManager} object.
	 */
	public void setEventManager(EventIpcManager eventManager) {
		m_eventManager = eventManager;
	}
    
}
