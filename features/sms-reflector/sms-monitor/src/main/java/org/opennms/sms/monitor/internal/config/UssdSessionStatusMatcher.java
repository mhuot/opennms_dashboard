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

package org.opennms.sms.monitor.internal.config;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.core.utils.LogUtils;
import org.opennms.sms.monitor.MobileSequenceSession;
import org.opennms.sms.reflector.smsservice.MobileMsgRequest;
import org.opennms.sms.reflector.smsservice.MobileMsgResponse;
import org.opennms.sms.reflector.smsservice.UssdResponse;
import org.smslib.USSDSessionStatus;

/**
 * <p>UssdSessionStatusMatcher class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
@XmlRootElement(name="session-status")
public class UssdSessionStatusMatcher extends SequenceResponseMatcher {

	/**
	 * <p>Constructor for UssdSessionStatusMatcher.</p>
	 */
	public UssdSessionStatusMatcher() {
	}

	/**
	 * <p>Constructor for UssdSessionStatusMatcher.</p>
	 *
	 * @param text a {@link java.lang.String} object.
	 */
	public UssdSessionStatusMatcher(String text) {
		setText(text);
	}
	
	/**
	 * <p>Constructor for UssdSessionStatusMatcher.</p>
	 *
	 * @param status a {@link org.smslib.USSDSessionStatus} object.
	 */
	public UssdSessionStatusMatcher(USSDSessionStatus status) {
		setText(status.name());
	}

	/** {@inheritDoc} */
	@Override
    public boolean matches(MobileSequenceSession session, MobileMsgRequest request, MobileMsgResponse response) {
        LogUtils.tracef(this, "ussdStatusIs(%s, %s)", getText(), request, response);
        return response instanceof UssdResponse && session.ussdStatusMatches(getText(), ((UssdResponse)response).getSessionStatus());
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ussdStatusIs(" + getText() + ")";
    }

}
