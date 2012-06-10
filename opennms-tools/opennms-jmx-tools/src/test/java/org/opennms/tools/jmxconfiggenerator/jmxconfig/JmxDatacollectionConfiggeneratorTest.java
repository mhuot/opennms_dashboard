/**
 * *****************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2007-2011 The OpenNMS Group, Inc. OpenNMS(R) is Copyright (C)
 * 1999-2011 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OpenNMS(R). If not, see: http://www.gnu.org/licenses/
 *
 * For more information contact: OpenNMS(R) Licensing <license@opennms.org>
 * http://www.opennms.org/ http://www.opennms.com/
 * *****************************************************************************
 */
package org.opennms.tools.jmxconfiggenerator.jmxconfig;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennms.xmlns.xsd.config.jmx_datacollection.JmxDatacollectionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Simon Walter <simon.walter@hp-factory.de>
 * @author Markus Neumann <markus@opennms.com>
 */
public class JmxDatacollectionConfiggeneratorTest {

    private static Logger logger = LoggerFactory.getLogger(JmxDatacollectionConfiggenerator.class);
    private JmxDatacollectionConfiggenerator jmxConfiggenerator;
    private MBeanServer platformMBeanServer;

    @Before
    public void setUp() throws Exception {
        jmxConfiggenerator = new JmxDatacollectionConfiggenerator();
        platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = new ObjectName("org.opennms.tools.jmxconfiggenerator.jmxconfig:type=JmxTest");
        JmxTestMBean testMBean = new JmxTest();
        platformMBeanServer.registerMBean(testMBean, objectName);
    }

    @After
    public void tearDown() throws Exception {
        jmxConfiggenerator = null;
        platformMBeanServer.unregisterMBean(new ObjectName("org.opennms.tools.jmxconfiggenerator.jmxconfig:type=JmxTest"));
        platformMBeanServer = null;
    }

    @Test
    public void testGenerateJmxConfigModelSkipJvmMBeans() {
        JmxDatacollectionConfig jmxConfigModel = jmxConfiggenerator.generateJmxConfigModel(platformMBeanServer, "testService", false, false);
        Assert.assertEquals(1, jmxConfigModel.getJmxCollection().size());
        Assert.assertEquals(1, jmxConfigModel.getJmxCollection().get(0).getMbeans().getMbean().size());
        Assert.assertEquals("org.opennms.tools.jmxconfiggenerator.jmxconfig.JmxTest", jmxConfigModel.getJmxCollection().get(0).getMbeans().getMbean().get(0).getName());
        Assert.assertEquals(3, jmxConfigModel.getJmxCollection().get(0).getMbeans().getMbean().get(0).getAttrib().size());
    }

    @Test
    public void testGenerateJmxConfigModelRunWritableMBeans() {
        JmxDatacollectionConfig jmxConfigModel = jmxConfiggenerator.generateJmxConfigModel(platformMBeanServer, "testService", false, true);
        Assert.assertEquals(1, jmxConfigModel.getJmxCollection().size());
        Assert.assertEquals(1, jmxConfigModel.getJmxCollection().get(0).getMbeans().getMbean().size());
        Assert.assertEquals("org.opennms.tools.jmxconfiggenerator.jmxconfig.JmxTest", jmxConfigModel.getJmxCollection().get(0).getMbeans().getMbean().get(0).getName());
        Assert.assertEquals(4, jmxConfigModel.getJmxCollection().get(0).getMbeans().getMbean().get(0).getAttrib().size());
    }

    @Test
    public void testGenerateJmxConfigModelRunJvmMBeans() {
        JmxDatacollectionConfig jmxConfigModel = jmxConfiggenerator.generateJmxConfigModel(platformMBeanServer, "testService", true, false);
        Assert.assertEquals(1, jmxConfigModel.getJmxCollection().size());
        Assert.assertTrue(10 < jmxConfigModel.getJmxCollection().get(0).getMbeans().getMbean().size());
        Assert.assertEquals("org.opennms.tools.jmxconfiggenerator.jmxconfig.JmxTest", jmxConfigModel.getJmxCollection().get(0).getMbeans().getMbean().get(0).getName());
        Assert.assertEquals(3, jmxConfigModel.getJmxCollection().get(0).getMbeans().getMbean().get(0).getAttrib().size());
    }

    //@Test
    public void testGenerateJmxConfigCassandraLocal() {
        MBeanServerConnection mBeanServerConnection = jmxConfiggenerator.createMBeanServerConnection("localhost", "7199", null, null, false, false);
        JmxDatacollectionConfig jmxConfigModel = jmxConfiggenerator.generateJmxConfigModel(mBeanServerConnection, "cassandra", false, false);
        Assert.assertEquals(1, jmxConfigModel.getJmxCollection().size());
        Assert.assertEquals(35, jmxConfigModel.getJmxCollection().get(0).getMbeans().getMbean().size());
        Assert.assertEquals("org.apache.cassandra.internal.MemtablePostFlusher", jmxConfigModel.getJmxCollection().get(0).getMbeans().getMbean().get(0).getName());
    }

    //@Test
    public void testGenerateJmxConfigJmxMp() {
        MBeanServerConnection mBeanServerConnection = jmxConfiggenerator.createMBeanServerConnection("connect.opennms-edu.net", "9998", null, null, false, true);
        logger.debug("MBeanServerConnection: '{}'",mBeanServerConnection);
        JmxDatacollectionConfig jmxConfigModel = jmxConfiggenerator.generateJmxConfigModel(mBeanServerConnection, "RemoteRepository", true, true);
        Assert.assertEquals(1, jmxConfigModel.getJmxCollection().size());
        Assert.assertEquals(35, jmxConfigModel.getJmxCollection().get(0).getMbeans().getMbean().size());
        Assert.assertEquals("org.apache.cassandra.internal.MemtablePostFlusher", jmxConfigModel.getJmxCollection().get(0).getMbeans().getMbean().get(0).getName());
    }
}
