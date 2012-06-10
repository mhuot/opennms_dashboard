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
import org.opennms.sms.reflector.smsservice.SmsRequest;
import org.opennms.sms.reflector.smsservice.SmsResponse;

/**
 * <p>SmsFromRecipientResponseMatcher class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
@XmlRootElement(name="from-recipient")
public class SmsFromRecipientResponseMatcher extends SequenceResponseMatcher {

	/** {@inheritDoc} */
	@Override
    public boolean matches(MobileSequenceSession session, MobileMsgRequest request, MobileMsgResponse response) {
        LogUtils.tracef(this, "smsFromRecipient.matches(%s, %s)", request, response);
        if (request instanceof SmsRequest && response instanceof SmsResponse) {
        	return equals(((SmsResponse)response).getOriginator(), ((SmsRequest)request).getRecipient());
        }

        return false;
    }

    private boolean equals(String orig, String recip) {
        return orig.replaceFirst("^\\+", "").equals(recip.replaceFirst("^\\+", ""));
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "smsFromRecipient()";
    }
    

}
