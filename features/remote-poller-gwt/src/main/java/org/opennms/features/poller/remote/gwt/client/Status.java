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


/**
 * <p>Status class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.8.1
 */
package org.opennms.features.poller.remote.gwt.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
public enum Status implements Serializable, IsSerializable {
    DOWN,
    DISCONNECTED,
    MARGINAL,
	UP,
	STOPPED,
	UNKNOWN;

	/**
	 * <p>getColor</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getColor() {
		String color;
		if (this.equals(Status.UP)){
			color = "#00ff00";
		} else if (this.equals(Status.MARGINAL)) {
			color = "#ffff00";
		} else if (this.equals(Status.DOWN)) {
			color = "#ff0000";
		} else if (this.equals(Status.DISCONNECTED)) {
			color = "#ff8800";
        } else if (this.equals(Status.STOPPED)) {
            color = "#aaaaaa";
		} else {
			color = "#dddddd";
		}
		return color;
	}

	/**
	 * <p>getStyle</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getStyle() {
		String cssClass;
		if (this.equals(Status.UP)) {
			cssClass = "statusUp";
		} else if (this.equals(Status.MARGINAL)) {
			cssClass = "statusMarginal";
		} else if (this.equals(Status.DOWN)) {
			cssClass = "statusDown";
		} else if (this.equals(Status.DISCONNECTED)){
			cssClass = "statusDisconnected";
        } else if (this.equals(Status.STOPPED)){
            cssClass = "statusStopped";
		} else {
			cssClass = "statusUninitialized";
		}
		return cssClass;
	}
}
