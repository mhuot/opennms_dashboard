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

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
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
import org.opennms.netmgt.dao.NotificationDao;
import org.opennms.netmgt.model.OnmsNotification;
import org.opennms.netmgt.model.OnmsNotificationCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.spi.resource.PerRequest;

@Component
/**
 * <p>NotificationRestService class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.8.1
 */
@PerRequest
@Scope("prototype")
@Path("notifications")
public class NotificationRestService extends OnmsRestService {
    @Autowired
    private NotificationDao m_notifDao;
    
    @Context 
    UriInfo m_uriInfo;

    @Context
    SecurityContext m_securityContext;
    
    /**
     * <p>getNotification</p>
     *
     * @param notifId a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.model.OnmsNotification} object.
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{notifId}")
    @Transactional
    public OnmsNotification getNotification(@PathParam("notifId") String notifId) {
        readLock();
        try {
        	OnmsNotification result= m_notifDao.get(new Integer(notifId));
        	return result;
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
            return Integer.toString(m_notifDao.countAll());
        } finally {
            readUnlock();
        }
    }

    /**
     * <p>getNotifications</p>
     *
     * @return a {@link org.opennms.netmgt.model.OnmsNotificationCollection} object.
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Transactional
    public OnmsNotificationCollection getNotifications() {
        readLock();
        
        try {
            final CriteriaBuilder builder = new CriteriaBuilder(OnmsNotification.class);
            applyQueryFilters(m_uriInfo.getQueryParameters(), builder);
            builder.orderBy("notifyId").desc();
    
            OnmsNotificationCollection coll = new OnmsNotificationCollection(m_notifDao.findMatching(builder.toCriteria()));
    
            coll.setTotalCount(m_notifDao.countMatching(builder.count().toCriteria()));
    
            return coll;
        } finally {
            readUnlock();
        }
    }
    
    /**
     * <p>updateNotification</p>
     *
     * @param notifId a {@link java.lang.String} object.
     * @param ack a {@link java.lang.Boolean} object.
     */
    @PUT
    @Path("{notifId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Transactional
    public void updateNotification(@PathParam("notifId") String notifId, @FormParam("ack") Boolean ack) {
        writeLock();
        
        try {
        	OnmsNotification notif=m_notifDao.get(new Integer(notifId));
        	if(ack==null) {
        		throw new  IllegalArgumentException("Must supply the 'ack' parameter, set to either 'true' or 'false'");
        	}
           	processNotifAck(notif,ack);
        } finally {
            writeUnlock();
        }
    }
    
	/**
	 * <p>updateNotifications</p>
	 *
	 * @param params a {@link org.opennms.web.rest.MultivaluedMapImpl} object.
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Transactional
	public void updateNotifications(final MultivaluedMapImpl params) {
	    writeLock();
	    
	    try {
    		Boolean ack=false;
    		if(params.containsKey("ack")) {
    			ack="true".equals(params.getFirst("ack"));
    			params.remove("ack");
    		}
    
    		final CriteriaBuilder builder = new CriteriaBuilder(OnmsNotification.class);
    		applyQueryFilters(params, builder);
    		
    		for (final OnmsNotification notif : m_notifDao.findMatching(builder.toCriteria())) {
    			processNotifAck(notif, ack);
    		}
	    } finally {
	        writeUnlock();
	    }
	}


	private void processNotifAck( OnmsNotification notif, Boolean ack) {
		if(ack) {
       		notif.setRespondTime(new Date());
       		notif.setAnsweredBy(m_securityContext.getUserPrincipal().getName());
    	} else {
    		notif.setRespondTime(null);
    		notif.setAnsweredBy(null);
    	}
       	m_notifDao.save(notif);
	}
}
