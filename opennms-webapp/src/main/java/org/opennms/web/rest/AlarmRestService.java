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

package org.opennms.web.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.opennms.core.criteria.CriteriaBuilder;
import org.opennms.netmgt.dao.AlarmDao;
import org.opennms.netmgt.model.AckAction;
import org.opennms.netmgt.model.OnmsAcknowledgment;
import org.opennms.netmgt.model.OnmsAlarm;
import org.opennms.netmgt.model.OnmsAlarmCollection;
import org.opennms.netmgt.model.acknowledgments.AckService;
import org.opennms.web.springframework.security.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.spi.resource.PerRequest;

@Component
@PerRequest
@Scope("prototype")
@Path("alarms")
public class AlarmRestService extends AlarmRestServiceBase {

    @Autowired
    private AlarmDao m_alarmDao;
    
    @Autowired
    private AckService m_ackService;

    @Context 
    UriInfo m_uriInfo;

    @Context
    SecurityContext m_securityContext;
    
    /**
     * <p>getAlarm</p>
     *
     * @param alarmId a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.model.OnmsAlarm} object.
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{alarmId}")
    @Transactional
    public OnmsAlarm getAlarm(@PathParam("alarmId") final String alarmId) {
        readLock();
        try {
            return m_alarmDao.get(new Integer(alarmId));
        } finally {
            readUnlock();
        }
    }
    
    /**
     * <p>getCount</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("count")
    @Transactional
    public String getCount() {
        readLock();
        try {
            return Integer.toString(m_alarmDao.countAll());
        } finally {
            readUnlock();
        }
    }

    /**
     * <p>getAlarms</p>
     *
     * @return a {@link org.opennms.netmgt.model.OnmsAlarmCollection} object.
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Transactional
    public OnmsAlarmCollection getAlarms() {
        readLock();
        
        try {
            final CriteriaBuilder builder = getCriteriaBuilder(m_uriInfo.getQueryParameters(), false);
            builder.distinct();
            final OnmsAlarmCollection coll = new OnmsAlarmCollection(m_alarmDao.findMatching(builder.toCriteria()));
    
            //For getting totalCount
            coll.setTotalCount(m_alarmDao.countMatching(builder.clearOrder().limit(0).offset(0).toCriteria()));
    
            return coll;
        } finally {
            readUnlock();
        }
    }
    
    /**
     * <p>updateAlarm</p>
     *
     * @param alarmId a {@link java.lang.String} object.
     * @param ack a {@link java.lang.Boolean} object.
     */
    @PUT
	@Path("{alarmId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Transactional
	public void updateAlarm(@PathParam("alarmId") final Integer alarmId, final MultivaluedMapImpl formProperties) {
        writeLock();

    	try {
    		if (alarmId == null) {
    			throw new IllegalArgumentException("Unable to determine alarm ID to update based on query path.");
    		}

    		final String ackValue = formProperties.getFirst("ack");
            formProperties.remove("ack");
            final String escalateValue = formProperties.getFirst("escalate");
            formProperties.remove("escalate");
            final String clearValue = formProperties.getFirst("clear");
            formProperties.remove("clear");
            final String ackUserValue = formProperties.getFirst("ackUser");
            formProperties.remove("ackUser");
            
    		final OnmsAlarm alarm = m_alarmDao.get(alarmId);
        	if (alarm == null) {
        		throw new IllegalArgumentException("Unable to locate alarm with ID '" + alarmId + "'");
        	}

    		final String ackUser = ackUserValue == null? m_securityContext.getUserPrincipal().getName() : ackUserValue;
    		assertUserCredentials(ackUser);

    		final OnmsAcknowledgment acknowledgement = new OnmsAcknowledgment(alarm, ackUser);
    		acknowledgement.setAckAction(AckAction.UNSPECIFIED);
    		if (ackValue != null) {
    			if (Boolean.parseBoolean(ackValue)) {
        			acknowledgement.setAckAction(AckAction.ACKNOWLEDGE);
        		} else {
        			acknowledgement.setAckAction(AckAction.UNACKNOWLEDGE);
        		}
    		} else if (escalateValue != null) {
    			if (Boolean.parseBoolean(escalateValue)) {
    				acknowledgement.setAckAction(AckAction.ESCALATE);
    			}
    		} else if (clearValue != null) {
    			if (Boolean.parseBoolean(clearValue)) {
    				acknowledgement.setAckAction(AckAction.CLEAR);
    			}
    		} else {
    			throw new IllegalArgumentException("Must supply one of the 'ack', 'escalate', or 'clear' parameters, set to either 'true' or 'false'.");
    		}
    		m_ackService.processAck(acknowledgement);
        } finally {
            writeUnlock();
        }
	}

	/**
	 * <p>updateAlarms</p>
	 *
	 * @param formProperties a {@link org.opennms.web.rest.MultivaluedMapImpl} object.
	 */
	@PUT
	@Transactional
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateAlarms(final MultivaluedMapImpl formProperties) {
	    writeLock();
	    
	    try {
			final String ackValue = formProperties.getFirst("ack");
			formProperties.remove("ack");
			final String escalateValue = formProperties.getFirst("escalate");
			formProperties.remove("escalate");
			final String clearValue = formProperties.getFirst("clear");
			formProperties.remove("clear");

			final CriteriaBuilder builder = getCriteriaBuilder(formProperties, false);
    		builder.distinct();
    		builder.limit(0);
    		builder.offset(0);

    		final String ackUser = formProperties.containsKey("ackUser")? formProperties.getFirst("ackUser") : m_securityContext.getUserPrincipal().getName();
    		formProperties.remove("ackUser");
			assertUserCredentials(ackUser);

    		for (final OnmsAlarm alarm : m_alarmDao.findMatching(builder.toCriteria())) {
        		final OnmsAcknowledgment acknowledgement = new OnmsAcknowledgment(alarm, ackUser);
        		acknowledgement.setAckAction(AckAction.UNSPECIFIED);
        		if (ackValue != null) {
        			if (Boolean.parseBoolean(ackValue)) {
	        			acknowledgement.setAckAction(AckAction.ACKNOWLEDGE);
	        		} else {
	        			acknowledgement.setAckAction(AckAction.UNACKNOWLEDGE);
	        		}
        		} else if (escalateValue != null) {
        			if (Boolean.parseBoolean(escalateValue)) {
        				acknowledgement.setAckAction(AckAction.ESCALATE);
        			}
        		} else if (clearValue != null) {
        			if (Boolean.parseBoolean(clearValue)) {
        				acknowledgement.setAckAction(AckAction.CLEAR);
        			}
        		} else {
        			throw new IllegalArgumentException("Must supply one of the 'ack', 'escalate', or 'clear' parameters, set to either 'true' or 'false'.");
        		}
        		m_ackService.processAck(acknowledgement);
    		}
	    } finally {
	        writeUnlock();
	    }
	}

	private void assertUserCredentials(final String ackUser) {
		final String currentUser = m_securityContext.getUserPrincipal().getName();
		if (!(m_securityContext.isUserInRole(Authentication.ROLE_ADMIN)) && !(ackUser.equals(currentUser))) {
			throw new IllegalArgumentException("You are logged in as non-admin user '" + currentUser + "', but you are trying to update an alarm as another user ('" + ackUser + "')!");
		}
	}
	
}
