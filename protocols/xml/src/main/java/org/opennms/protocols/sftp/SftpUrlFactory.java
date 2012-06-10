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
package org.opennms.protocols.sftp;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * A factory for creating SFTP URL Handler objects.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class SftpUrlFactory implements URLStreamHandlerFactory {

    /* (non-Javadoc)
     * @see java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
     */
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (SftpUrlHandler.PROTOCOL.equals(protocol)) {
            return new SftpUrlHandler();
        }
        if (Sftp3gppUrlHandler.PROTOCOL.equals(protocol)) {
            return new Sftp3gppUrlHandler();
        }
        return null;
    }

}
