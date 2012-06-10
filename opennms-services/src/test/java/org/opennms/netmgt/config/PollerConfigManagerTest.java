/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2008-2011 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.config;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.endsWith;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;

import java.io.IOException;
import java.net.InetAddress;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import junit.framework.TestCase;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.opennms.core.test.MockLogAppender;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.config.poller.Package;
import org.opennms.netmgt.config.poller.Parameter;
import org.opennms.netmgt.config.poller.Rrd;
import org.opennms.netmgt.config.poller.Service;
import org.opennms.netmgt.dao.FilterDao;
import org.opennms.netmgt.dao.support.RrdTestUtils;
import org.opennms.netmgt.filter.FilterDaoFactory;
import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.netmgt.model.OnmsMonitoredService;
import org.opennms.netmgt.model.OnmsServiceType;
import org.opennms.netmgt.rrd.RrdDataSource;
import org.opennms.netmgt.rrd.RrdStrategy;
import org.opennms.netmgt.rrd.RrdUtils;
import org.opennms.test.ConfigurationTestUtils;
import org.opennms.test.mock.EasyMockUtils;

public class PollerConfigManagerTest extends TestCase {
    private EasyMockUtils m_mocks = new EasyMockUtils();
    @SuppressWarnings("unchecked")
    private RrdStrategy<Object,Object> m_rrdStrategy = m_mocks.createMock(RrdStrategy.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        MockLogAppender.setupLogging();
        
        FilterDao filterDao = createMock(FilterDao.class);
        expect(filterDao.getActiveIPAddressList(isA(String.class))).andReturn(new ArrayList<InetAddress>(0)).anyTimes();
        replay(filterDao);
        FilterDaoFactory.setInstance(filterDao);
        
        RrdTestUtils.initializeNullStrategy();
        RrdUtils.setStrategy(m_rrdStrategy);
        
        SnmpPeerFactory.setInstance(new SnmpPeerFactory(ConfigurationTestUtils.getInputStreamForConfigFile("snmp-config.xml")));
    }

    @Override
    protected void runTest() throws Throwable {
        super.runTest();
        MockLogAppender.assertNoWarningsOrGreater();
    }

    public void testSaveResponseTimeDataWithLocaleThatUsesCommasForDecimals() throws Exception {
    	Properties p = new Properties();
    	p.setProperty("org.opennms.netmgt.ConfigFileConstants", "ERROR");
    	MockLogAppender.setupLogging(p);

        Locale.setDefault(Locale.FRENCH);
        
        // Make sure we actually have a valid test
        NumberFormat nf = NumberFormat.getInstance();
        assertEquals("ensure that the newly set default locale (" + Locale.getDefault() + ") uses ',' as the decimal marker", "1,5", nf.format(1.5));
        
        PollerConfigManager mgr = new TestPollerConfigManager();
        
        OnmsMonitoredService svc = new OnmsMonitoredService();
        OnmsServiceType svcType = new OnmsServiceType();
        svcType.setName("HTTP");
        svc.setServiceType(svcType);
        OnmsIpInterface intf = new OnmsIpInterface();
        intf.setIpAddress(InetAddressUtils.addr("1.2.3.4"));
        svc.setIpInterface(intf);
        
        Package pkg = new Package();
        Service pkgService = new Service();
        pkgService.setName("HTTP");
        addParameterToService(pkgService, "ds-name", "http");
        addParameterToService(pkgService, "rrd-repository", "/foo");
        pkg.addService(pkgService);
        Rrd rrd = new Rrd();
        rrd.setStep(300);
        rrd.addRra("bogusRRA");
        pkg.setRrd(rrd);
        
        expect(m_rrdStrategy.getDefaultFileExtension()).andReturn(".rrd").anyTimes();
        expect(m_rrdStrategy.createDefinition(isA(String.class), isA(String.class), isA(String.class), anyInt(), isAList(RrdDataSource.class), isAList(String.class))).andReturn(new Object());
        m_rrdStrategy.createFile(isA(Object.class));
        expect(m_rrdStrategy.openFile(isA(String.class))).andReturn(new Object());
        m_rrdStrategy.updateFile(isA(Object.class), isA(String.class), endsWith(":1.5"));
        m_rrdStrategy.closeFile(isA(Object.class));

        m_mocks.replayAll();
        mgr.saveResponseTimeData("Tuvalu", svc, 1.5, pkg);
        m_mocks.verifyAll();
    }

    private void addParameterToService(Service pkgService, String key, String value) {
        Parameter param = new Parameter();
        param.setKey(key);
        param.setValue(value);
        pkgService.addParameter(param);
    }
    
    @SuppressWarnings("unchecked")
    private static <T> List<T> isAList(Class<T> clazz) {
        return isA(List.class);
    }
    
    public static class TestPollerConfigManager extends PollerConfigManager {
        public TestPollerConfigManager() throws MarshalException, ValidationException, IOException {
            super(ConfigurationTestUtils.getInputStreamForConfigFile("poller-configuration.xml"), "foo", false);
        }

        @Override
        protected void saveXml(String xml) throws IOException {
            throw new UnsupportedOperationException("dude, where's my car?  Oh, yeah, this method isn't supported");
        }

        @Override
        public void update() throws IOException, MarshalException, ValidationException {
            throw new UnsupportedOperationException("dude, where's my car?  Oh, yeah, this method isn't supported");
        }
    }
}
