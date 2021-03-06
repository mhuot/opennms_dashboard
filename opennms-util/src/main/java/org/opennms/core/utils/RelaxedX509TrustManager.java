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

package org.opennms.core.utils;

import java.security.cert.CertificateException;

import javax.net.ssl.X509TrustManager;

/**
 * This class is used to change the behaviour of the X509TrustManager that is
 * used to validate certificates from an HTTPS server. With this class all
 * certificates will be approved
 *
 * @author <a href="mailto:jason@opennms.org">Jason</a>
 * @author <a href="http://www.opennms.org">OpenNMS</a>
 */
public class RelaxedX509TrustManager implements X509TrustManager {
    /** {@inheritDoc} */
    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
    }

    /** {@inheritDoc} */
    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
    }

    /**
     * <p>getAcceptedIssuers</p>
     *
     * @return an array of {@link java.security.cert.X509Certificate} objects.
     */
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
