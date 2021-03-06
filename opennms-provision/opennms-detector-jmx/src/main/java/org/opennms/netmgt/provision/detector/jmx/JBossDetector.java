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

package org.opennms.netmgt.provision.detector.jmx;

import org.opennms.netmgt.provision.detector.jmx.client.JBossClient;
import org.opennms.netmgt.provision.detector.jmx.client.JMXClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
/**
 * <p>JBossDetector class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
@Scope("prototype")
public class JBossDetector extends JMXDetector {
    
    private static String DEFAULT_SERVICE_NAME = "JBoss";
    private static int DEFAULT_JBOSS_PORT = 1099;
    
    /**
     * <p>Constructor for JBossDetector.</p>
     */
    public JBossDetector() {
        super(DEFAULT_SERVICE_NAME, DEFAULT_JBOSS_PORT);
    }

    /** {@inheritDoc} */
    @Override
    protected JMXClient getClient() {
        JBossClient client = new JBossClient();
        return client;
    }

    /** {@inheritDoc} */
    @Override
    protected void onInit() {
        expectBeanCount(greatThan(0));
    }

}
