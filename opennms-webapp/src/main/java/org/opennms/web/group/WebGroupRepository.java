/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2011 The OpenNMS Group, Inc.
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

package org.opennms.web.group;

/**
 * WebGroupRepository
 *
 * @author brozow
 * @version $Id: $
 * @since 1.8.1
 */
public interface WebGroupRepository {
    
    /**
     * <p>groupExists</p>
     *
     * @param groupName a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean groupExists(String groupName);
    
    /**
     * <p>getGroup</p>
     *
     * @param groupName a {@link java.lang.String} object.
     * @return a {@link org.opennms.web.group.WebGroup} object.
     */
    WebGroup getGroup(String groupName);
    
    /**
     * <p>saveGroup</p>
     *
     * @param group a {@link org.opennms.web.group.WebGroup} object.
     */
    void saveGroup(WebGroup group);

    /**
     * <p>deleteGroup</p>
     *
     * @param groupName a {@link java.lang.String} object.
     */
    void deleteGroup(String groupName);

    /**
     * <p>renameGroup</p>
     *
     * @param oldName a {@link java.lang.String} object.
     * @param newName a {@link java.lang.String} object.
     */
    void renameGroup(String oldName, String newName);

}
