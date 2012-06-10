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
package org.opennms.protocols.xml.collector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.opennms.protocols.http.HttpUrlHandler;
import org.opennms.protocols.sftp.Sftp3gppUrlHandler;
import org.opennms.protocols.sftp.SftpUrlConnection;
import org.opennms.protocols.sftp.SftpUrlHandler;

/**
 * A factory for creating URL objects.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class UrlFactory {

    /**
     * Instantiates a new URL factory.
     */
    private UrlFactory() {}

    /**
     * Gets the URL Object.
     * <p>This method has been created because it is not possible to call URL.setURLStreamHandlerFactory more than once.</p>
     * 
     * @param urlStr the URL String
     * @return the URL Object
     * @throws MalformedURLException the malformed URL exception
     */
    public static URL getUrl(String urlStr) throws MalformedURLException {
        URL url = null;
        if (urlStr.startsWith(SftpUrlHandler.PROTOCOL + "://")) {
            url = new URL(null, urlStr, new SftpUrlHandler());
        } else if (urlStr.startsWith(Sftp3gppUrlHandler.PROTOCOL + "://")) {
            url = new URL(null, urlStr, new Sftp3gppUrlHandler());
        } else if (urlStr.startsWith(HttpUrlHandler.PROTOCOL + "://")) {
            url = new URL(null, urlStr, new HttpUrlHandler());
        } else {
            url = new URL(urlStr);
        }
        return url;
    }

    /**
     * Disconnect.
     *
     * @param connection the URL connection
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void disconnect(URLConnection connection) throws IOException {
        if (connection != null && connection instanceof SftpUrlConnection) // We need to be sure to close the connections for SFTP
            ((SftpUrlConnection)connection).disconnect();
    }
}
