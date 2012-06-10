package org.opennms.netmgt.snmp.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.opennms.core.concurrent.LogPreservingThreadFactory;
import org.opennms.core.utils.LogUtils;
import org.opennms.netmgt.snmp.CollectionTracker;
import org.opennms.netmgt.snmp.SnmpAgentAddress;
import org.opennms.netmgt.snmp.SnmpAgentConfig;
import org.opennms.netmgt.snmp.SnmpObjId;
import org.opennms.netmgt.snmp.SnmpValue;
import org.opennms.netmgt.snmp.SnmpWalker;

public class MockSnmpWalker extends SnmpWalker {

	private static class MockPduBuilder extends WalkerPduBuilder {
        private List<SnmpObjId> m_oids = new ArrayList<SnmpObjId>();

        public MockPduBuilder(final int maxVarsPerPdu) {
            super(maxVarsPerPdu);
            reset();
        }

        @Override
        public void reset() {
            m_oids.clear();
        }

        public List<SnmpObjId> getOids() {
            return new ArrayList<SnmpObjId>(m_oids);
        }

        @Override
        public void addOid(final SnmpObjId snmpObjId) {
            m_oids.add(snmpObjId);
        }

        @Override
        public void setNonRepeaters(final int numNonRepeaters) {
        }

        @Override
        public void setMaxRepetitions(final int maxRepetitions) {
        }
    }
	
	private static class MockVarBind {
		SnmpObjId m_oid;
		SnmpValue m_value;
		
		public MockVarBind(SnmpObjId oid, SnmpValue value) {
			m_oid = oid;
			m_value = value;
		}

		public SnmpObjId getOid() {
			return m_oid;
		}

		public SnmpValue getValue() {
			return m_value;
		}
		
		
	}

	private final SnmpAgentAddress m_agentAddress;
	private final int m_snmpVersion;
    private final PropertyOidContainer m_container;
    private final ExecutorService m_executor;

    public MockSnmpWalker(final SnmpAgentAddress agentAddress, int snmpVersion, final PropertyOidContainer container, final String name, final CollectionTracker tracker, int maxVarsPerPdu) {
        super(agentAddress.getAddress(), name, maxVarsPerPdu, 1, tracker);
        m_agentAddress = agentAddress;
        m_snmpVersion = snmpVersion;
        m_container = container;
        m_executor = Executors.newSingleThreadExecutor(
            new LogPreservingThreadFactory(getClass().getSimpleName(), 1, false)
        );
    }

    @Override
    protected WalkerPduBuilder createPduBuilder(final int maxVarsPerPdu) {
        return new MockPduBuilder(maxVarsPerPdu);
    }

    @Override
    protected void sendNextPdu(final WalkerPduBuilder pduBuilder) throws IOException {
        final MockPduBuilder builder = (MockPduBuilder)pduBuilder;
        final List<SnmpObjId> oids = builder.getOids();
        LogUtils.debugf(this, "'Sending' tracker PDU of size " + oids.size());

        m_executor.submit(new ResponseHandler(oids));
    }

    @Override
    protected void handleDone() {
    	LogUtils.debugf(this, "handleDone()");
    	super.handleDone();
    }

    @Override
    protected void handleAuthError(final String msg) {
    	LogUtils.debugf(this, "handleAuthError(%s)", msg);
    	super.handleAuthError(msg);
    }
    
    @Override
    protected void handleError(final String msg) {
    	LogUtils.debugf(this, "handleError(%s)", msg);
    	super.handleError(msg);
    }
    
    @Override
    protected void handleError(final String msg, final Throwable t) {
    	LogUtils.debugf(this, t, "handleError(%s, %s)", msg, t.getLocalizedMessage());
    	super.handleError(msg, t);
    }
    
    @Override
    protected void handleFatalError(final Throwable e) {
    	LogUtils.debugf(this, e, "handleFatalError(%s)", e.getLocalizedMessage());
    	super.handleFatalError(e);
    }

    @Override
    protected void handleTimeout(final String msg) {
    	LogUtils.debugf(this, "handleTimeout(%s)", msg);
    	super.handleTimeout(msg);
    }

    @Override
    protected void close() throws IOException {
        m_executor.shutdown();
    }

    @Override
    protected void buildAndSendNextPdu() throws IOException {
    	LogUtils.debugf(this, "buildAndSendNextPdu()");
    	super.buildAndSendNextPdu();
    }

    private final class ResponseHandler implements Runnable {
		private final List<SnmpObjId> m_oids;

		private ResponseHandler(final List<SnmpObjId> oids) {
			m_oids = oids;
		}

		@Override
		public void run() {
		    handleResponses();
		}

	    protected void handleResponses() {
	    	LogUtils.debugf(this, "handleResponses(%s)", m_oids);
	        try {
	            if (m_container == null) {
	            	LogUtils.infof(this, "No SNMP response data configured for %s; pretending we've timed out.", m_agentAddress);
	            	Thread.sleep(100);
	            	handleTimeout("No MockSnmpAgent data configured for '" + m_agentAddress + "'.");
	            	return;
	            }

	            List<MockVarBind> responses = new ArrayList<MockVarBind>(m_oids.size());
	            		
	            int errorStatus = 0;
	            int errorIndex = 0;
	            int index = 1; // snmp index start at 1
	            for (final SnmpObjId oid : m_oids) {
	            	SnmpObjId nextOid = m_container.findNextOidForOid(oid);
	            	if (nextOid == null) { 
		            	LogUtils.debugf(this, "No OID following %s", oid);
	            		if (m_snmpVersion == SnmpAgentConfig.VERSION1) {
	            			if (errorStatus == 0) { // for V1 only record the index of the first failing varbind
	            				errorStatus = CollectionTracker.NO_SUCH_NAME_ERR;
	            				errorIndex = index;
	            			}
	            		}
            			responses.add(new MockVarBind(oid, MockSnmpValue.END_OF_MIB));
	            	} else {
	            		responses.add(new MockVarBind(nextOid, m_container.findValueForOid(nextOid)));
	            	}
	            	index++;
	            }

	            if (!processErrors(errorStatus, errorIndex)) {
	            	LogUtils.debugf(this, "Responding with PDU of size %d.", responses.size());
	            	for(MockVarBind vb : responses) {
	                	processResponse(vb.getOid(), vb.getValue());
	                }
	            } 
				buildAndSendNextPdu();

	        } catch (final Throwable t) {
	            handleFatalError(t);
	        }
	    }
    }
}
