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

package org.opennms.netmgt.utils;

import java.lang.NumberFormatException;
import java.util.StringTokenizer;

import org.opennms.core.utils.ThreadCategory;

/**
 * A class containing a method to determine if a string represents
 * a vaild IP address
 *
 * @author ranger
 * @version $Id: $
 */
public class IpValidator extends Object {
    /**
     * <p>isIpValid</p>
     *
     * @param ipAddr a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean isIpValid(String ipAddr) {
        ThreadCategory log = ThreadCategory.getInstance(IpValidator.class);
        StringTokenizer token = new StringTokenizer(ipAddr, ".");
        if(token.countTokens() != 4) {
            if (log.isDebugEnabled())
                log.debug("Invalid format for IpAddress " + ipAddr);
            return false;
        }
        int temp;
        int i = 0;
        while (i < 4) {
            try{
                temp = Integer.parseInt(token.nextToken(), 10);
                if (temp < 0 || temp > 255) {
                    if (log.isDebugEnabled())
                        log.debug("Invalid value " + temp + " in IpAddress");
                    return false;
                }
                i++;
            } catch (NumberFormatException ex) {
                if (log.isDebugEnabled())
                    log.debug("Invalid format for IpAddress, " + ex);
                return false;
            }
        }
        return true;
    }     
}
