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

package org.opennms.features.dashboard.client.layout;

import org.opennms.features.dashboard.client.portlet.IBasicPortlet;

/**
 * author: Tharindu Munasinghe (tharindumunasinghe@gmail.com)
 * org.opennms.features.dashboard
 */
public interface IBasicDBLayout {
    int addNewPortlet(IBasicPortlet portlet);

    int addNewPortlet(int x, int y, IBasicPortlet portlet);

    void setContent(int index);

    void closePortlet(int window);

    void setHeaderCaption(int window, String text);

    void selectPortlet(int window);

    void clearDashboard();
}
