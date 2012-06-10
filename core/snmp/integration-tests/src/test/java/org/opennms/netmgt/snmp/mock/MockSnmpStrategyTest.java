package org.opennms.netmgt.snmp.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.InetAddress;

import org.junit.Before;
import org.junit.Test;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.core.utils.LogUtils;
import org.opennms.netmgt.snmp.CollectionTracker;
import org.opennms.netmgt.snmp.ColumnTracker;
import org.opennms.netmgt.snmp.SnmpAgentAddress;
import org.opennms.netmgt.snmp.SnmpAgentConfig;
import org.opennms.netmgt.snmp.SnmpObjId;
import org.opennms.netmgt.snmp.SnmpResult;
import org.opennms.netmgt.snmp.SnmpUtils;
import org.opennms.netmgt.snmp.SnmpValue;
import org.opennms.netmgt.snmp.SnmpWalker;
import org.springframework.core.io.ClassPathResource;

public class MockSnmpStrategyTest {
    private static MockSnmpStrategy m_strategy;
    private InetAddress m_agentAddress = InetAddressUtils.addr("127.0.0.1");
    private int m_agentPort = 1691;

    @Before
    public void setUp() throws Exception {
        m_strategy = new MockSnmpStrategy();
        MockSnmpStrategy.setDataForAddress(new SnmpAgentAddress(m_agentAddress, m_agentPort), new ClassPathResource("loadSnmpDataTest.properties"));
        System.setProperty("org.opennms.snmp.strategyClass", m_strategy.getClass().getName());
    }
    
    @Test
    public void testGetSingleValue() throws Exception {
        SnmpObjId[] oids = new SnmpObjId[] { SnmpObjId.get(".1.3.5.1.1.3.0") };
        
        SnmpValue[] values = m_strategy.get(getAgentConfig(), oids);
        
        assertNotNull("values should not be null", values);
        assertEquals("values list size", 1, values.length);
        assertSnmpValueEquals("values[0]", SnmpValue.SNMP_INT32, 42, values[0]);
    }
    
    @Test
    public void testGetMultipleValues() throws Exception {
        final SnmpObjId[] oids = new SnmpObjId[] {
                SnmpObjId.get(".1.3.5.1.1.3.0"),
                SnmpObjId.get(".1.3.5.1.1.4.0"),
        };
        
        final SnmpValue[] values = m_strategy.get(getAgentConfig(), oids);
        
        assertNotNull("values should not be null", values);
        assertEquals("values list size", 2, values.length);
        assertSnmpValueEquals("values[0]", SnmpValue.SNMP_INT32, 42, values[0]);
        assertSnmpValueEquals("values[1]", SnmpValue.SNMP_GAUGE32, 42, values[1]);
    }
    
    @Test
    public void testGetBulkMultipleValues() throws Exception {
        final SnmpObjId[] oids = new SnmpObjId[] {
                SnmpObjId.get(".1.3.5.1.1.3.0"),
                SnmpObjId.get(".1.3.5.1.1.4.0"),
        };
        
        final SnmpValue[] values = m_strategy.get(getAgentConfig(), oids);
        
        assertNotNull("values should not be null", values);
        assertEquals("values list size", 2, values.length);
        assertSnmpValueEquals("values[0]", SnmpValue.SNMP_INT32, 42, values[0]);
        assertSnmpValueEquals("values[1]", SnmpValue.SNMP_GAUGE32, 42, values[1]);
    }
    
    @Test
    public void testGetNextSingleValue() throws Exception {
        final SnmpObjId[] oids = new SnmpObjId[] { SnmpObjId.get(".1.3.5.1.1.3.0") };
        
        final SnmpValue[] values = m_strategy.getNext(getAgentConfig(), oids);
        
        assertNotNull("values should not be null", values);
        assertEquals("values list size", 1, values.length);
        // Expect the *next* value, so for .1.3.5.1.1.4.0
        assertSnmpValueEquals("values[0]", SnmpValue.SNMP_GAUGE32, 42, values[0]);
    }
    
    @Test
    public void testGetNextMultipleValues() throws Exception {
        final SnmpObjId[] oids = new SnmpObjId[] {
                SnmpObjId.get(".1.3.5.1.1.3.0"),
                SnmpObjId.get(".1.3.5.1.1.4.0"),
        };
        
        final SnmpValue[] values = m_strategy.getNext(getAgentConfig(), oids);
        
        assertNotNull("values should not be null", values);
        assertEquals("values list size", 2, values.length);
        // Expect the *next* value, so for .1.3.5.1.1.4.0
        assertSnmpValueEquals("values[0]", SnmpValue.SNMP_GAUGE32, 42, values[0]);
        // Expect the *next* value, so for .1.3.5.1.1.5.0
        assertSnmpValueEquals("values[1]", SnmpValue.SNMP_COUNTER32, 42, values[1]);
    }

    @Test
    public void testSetSingleValue() throws Exception {
    	m_strategy.set(getAgentConfig(), SnmpObjId.get(".1.3.5.1.1.3.0"), m_strategy.getValueFactory().getInt32(4));
    	
    	final SnmpValue result = m_strategy.get(getAgentConfig(), SnmpObjId.get(".1.3.5.1.1.3.0"));
    	assertNotNull(result);
    	assertEquals(4, result.toInt());
    }

    @Test
    public void testSetBadAgent() throws Exception {
    	final SnmpAgentConfig sac = getAgentConfig();
    	sac.setAddress(InetAddressUtils.addr("1.2.3.4"));

    	m_strategy.set(sac, SnmpObjId.get(".1.3.5.1.1.3.0"), m_strategy.getValueFactory().getInt32(4));
    	
    	final SnmpValue result = m_strategy.get(sac, SnmpObjId.get(".1.3.5.1.1.3.0"));
    	
    	assertNull(result);
    }

    @Test
    public void testSetMultipleValues() throws Exception {
        final SnmpObjId[] oids = new SnmpObjId[] {
                SnmpObjId.get(".1.3.5.1.1.3.0"),
                SnmpObjId.get(".1.3.5.1.1.4.0")
        };
        final SnmpValue[] values = new SnmpValue[] {
        		m_strategy.getValueFactory().getInt32(4),
        		m_strategy.getValueFactory().getGauge32(5)
        };

        m_strategy.set(getAgentConfig(), oids, values);
    	
    	final SnmpValue[] results = m_strategy.get(getAgentConfig(), oids);
    	assertNotNull(results);
    	assertEquals(2, results.length);
    	assertEquals(4, results[0].toInt());
    	assertEquals(5, results[1].toInt());
    }

    @Test
    public void testSetMultipleBadAgent() throws Exception {
    	final SnmpAgentConfig sac = getAgentConfig();
    	sac.setAddress(InetAddressUtils.addr("1.2.3.4"));

        final SnmpObjId[] oids = new SnmpObjId[] {
                SnmpObjId.get(".1.3.5.1.1.3.0"),
                SnmpObjId.get(".1.3.5.1.1.4.0")
        };
        final SnmpValue[] values = new SnmpValue[] {
        		m_strategy.getValueFactory().getInt32(4),
        		m_strategy.getValueFactory().getGauge32(5)
        };

        m_strategy.set(sac, oids, values);
    	
    	final SnmpValue[] results = m_strategy.get(sac, oids);
    	assertNotNull(results);
    	assertEquals(2, results.length);
    	assertNull(results[0]);
    	assertNull(results[1]);
    }

    @Test
    public void testTracker() throws Exception {
        final CountingColumnTracker ct = new CountingColumnTracker(SnmpObjId.get(".1.3.5.1.1"));

        walk(ct, 10, 3);
        assertEquals("number of columns returned must match test data", Long.valueOf(9).longValue(), ct.getCount());
    }

    @Test
    public void testTrackerTimeout() throws Exception {
        final CountingColumnTracker ct = new CountingColumnTracker(SnmpObjId.get(".1.3.5.1.1"));
        final SnmpAgentConfig sac = getAgentConfig();
        sac.setPort(12345);
        final SnmpWalker walker = SnmpUtils.createWalker(sac, "test", ct);
        assertNotNull(walker);
        walker.start();
        walker.waitFor();
        assertEquals("it should match no columns (timeout)", Long.valueOf(0).longValue(), ct.getCount());
    }
    
    private void assertSnmpValueEquals(final String message, final int expectedType, final int expectedValue, final SnmpValue value) {
        assertEquals(message + " getType()", expectedType, value.getType());
        assertEquals(message + " toInt()", expectedValue, value.toInt());
    }

    private SnmpAgentConfig getAgentConfig() {
        final SnmpAgentConfig config = new SnmpAgentConfig();
        config.setAddress(m_agentAddress);
        config.setPort(m_agentPort);
        config.setVersion(SnmpAgentConfig.VERSION1);
        config.setMaxVarsPerPdu(20);
        config.setMaxRepetitions(20);
        return config;
    }

    private void walk(final CollectionTracker c, final int maxVarsPerPdu, final int maxRepetitions) throws Exception {
        final SnmpAgentConfig config = getAgentConfig();
        final SnmpWalker walker = SnmpUtils.createWalker(config, "test", c);
        assertNotNull(walker);
        walker.start();
        walker.waitFor();
    }

    static private class CountingColumnTracker extends ColumnTracker {
        private long m_count = 0;
        public CountingColumnTracker(final SnmpObjId base) {
            super(base);
        }
        public CountingColumnTracker(final SnmpObjId base, final int maxRepetitions) {
            super(base, maxRepetitions);
        }
        public long getCount() {
            return m_count;
        }
        @Override
        protected void storeResult(final SnmpResult res) {
            LogUtils.debugf(this, "storing result %s", res);
            m_count++;
        }

    }
}
