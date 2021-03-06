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

package org.opennms.features.poller.remote.gwt.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.opennms.features.poller.remote.gwt.client.location.LocationInfo;

public interface ApplicationView {

    /**
     * <p>updateTimestamp</p>
     */
    public abstract void updateTimestamp();
    
    public abstract void setStatusMessage(String statusMessage);

    public abstract Set<Status> getSelectedStatuses();

    public abstract void initialize();

    public abstract void updateSelectedApplications( Set<ApplicationInfo> applications);

    public abstract void updateLocationList(
            ArrayList<LocationInfo> locationsForLocationPanel);

    public abstract void setSelectedTag(String selectedTag, List<String> allTags);

    public abstract void updateApplicationList(
            ArrayList<ApplicationInfo> applications);

    public abstract void updateApplicationNames(
            TreeSet<String> allApplicationNames);

    public abstract void fitMapToLocations(GWTBounds locationBounds);

    public abstract GWTBounds getMapBounds();

    public abstract void showLocationDetails(final String locationName,
            String htmlTitle, String htmlContent);

    public abstract void placeMarker(final GWTMarkerState markerState);

}