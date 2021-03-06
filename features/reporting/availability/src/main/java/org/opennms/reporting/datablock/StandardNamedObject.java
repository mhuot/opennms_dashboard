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

package org.opennms.reporting.datablock;

/**
 * This class gives a name to the object.
 *
 * @author <A HREF="mailto:jacinta@oculan.com">Jacinta Remedios </A>
 * @author <A HREF="http://www.oculan.com">oculan.com </A>
 * @author <A HREF="mailto:jacinta@oculan.com">Jacinta Remedios </A>
 * @author <A HREF="http://www.oculan.com">oculan.com </A>
 * @version $Id: $
 */
public class StandardNamedObject extends Object {
    /**
     * The name of the object
     */
    private String m_name;

    /**
     * Default Constructor.
     */
    public StandardNamedObject() {
        m_name = new String();
    }

    /**
     * Constructor.
     *
     * @param name a {@link java.lang.String} object.
     */
    public StandardNamedObject(String name) {
        m_name = new String(name);
    }

    /**
     * Set the name
     *
     * @param name
     *            The name to be set.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * Return the name
     *
     * @return the name.
     */
    public String getName() {
        return m_name;
    }
}
