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

package org.openoss.opennms.spring.qosd.spring;

import java.util.Hashtable;
import java.util.Properties;

import javax.oss.fm.monitor.AlarmKey;
import javax.oss.fm.monitor.AlarmValue;

import org.opennms.core.utils.ThreadCategory;
import org.openoss.opennms.spring.qosd.AlarmListConnectionManager;
import org.openoss.opennms.spring.qosd.PropertiesLoader;
import org.openoss.opennms.spring.qosd.QoSDimpl2;
import org.openoss.ossj.jvt.fm.monitor.OOSSAlarmValue;
import org.openoss.ossj.fm.monitor.spring.AlarmMonitorDao;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * <p>AlarmListConnectionManagerSpringImpl class.</p>
 *
 * @author openoss
 * @version $Id: $
 */
public class AlarmListConnectionManagerSpringImpl implements AlarmListConnectionManager {

	boolean init=false; // set true if init called
	ThreadCategory log;
	int status = DISCONNECTED; //  this changes to CONNECTED when the bean is instantiated 
	
	// ************************
    // Spring DAO setters
    // ************************
	
	AlarmMonitorDao alarmMonitorDao; // j2ee alarmMonitorDao - to be encapsulated in spring

	/**
	 * <p>Setter for the field <code>alarmMonitorDao</code>.</p>
	 *
	 * @param alarmMonitorDao the alarmMonitorDao to set
	 */
	public void setAlarmMonitorDao(AlarmMonitorDao alarmMonitorDao) {
		this.alarmMonitorDao = alarmMonitorDao;
	}
	
	/**
	 * used to hold a local reference to the application context from which this bean was started
	 */
	private ClassPathXmlApplicationContext m_context=null; // used to passapplication context to OssBeans

	/**
	 * {@inheritDoc}
	 *
	 * Used by jmx mbean QoSD to pass in Spring Application context
	 */
	public  void setApplicationContext(ClassPathXmlApplicationContext m_context){
		this.m_context = m_context;
	}
	
	// ************************
    // Constructor and methods
    // ************************
	
	/**
	 * The AlarmListConnectionManagerSpringImpl instantiates the AlarmMonitorBean within OpenNMS without Jboss
	 * This is an alternative to contacting the bean in jboss. This only deals with XML alarms
	 */
	public AlarmListConnectionManagerSpringImpl() {
	}

	/* (non-Javadoc)
	 * @see org.openoss.opennms.spring.qosd.AlarmListConnectionManager#getStatus()
	 */
	/**
	 * <p>Getter for the field <code>status</code>.</p>
	 *
	 * @return a int.
	 */
	public int getStatus() {
		
		return status;
	}

	/* (non-Javadoc)
	 * @see org.openoss.opennms.spring.qosd.AlarmListConnectionManager#init(org.openoss.opennms.spring.qosd.PropertiesLoader, java.util.Properties)
	 */
	/** {@inheritDoc} */
	public void init(PropertiesLoader props, Properties env) {
		log = QoSDimpl2.getLog();	//Get a reference to the QoSD logger

		try {
			if (log.isDebugEnabled()) log.debug("AlarmListConnectionManagerSpringImpl.init() initialising AlarmMonitorDao. Setting alarmMonitorDao.setLogName to:"+ log.getName());
			alarmMonitorDao.setLogName(log.getName());
			alarmMonitorDao.init();
			
		}
		catch (Throwable ex) {
			log.error("AlarmListConnectionManagerSpringImpl.init() problem creating AlarmMonitorDao"+ ex);
		}
		init = true;		//inform the thread that it has been initialised 
							//and can execute the run() method.
		status = CONNECTED;
		
	}

	/* (non-Javadoc)
	 * @see org.openoss.opennms.spring.qosd.AlarmListConnectionManager#kill()
	 */
	/**
	 * <p>kill</p>
	 */
	public void kill() {
		try {
			//alarmMonitorDao.ejbRemove(); TODO - NEED TO CLOSE BEAN PROPERLY
		}catch (Throwable ex) {
			log.error("AlarmListConnectionManagerSpringImpl.init() problem stopping alarmMonitorDao"+ ex);
		}
		status = DISCONNECTED;

	}

	/* (non-Javadoc)
	 * @see org.openoss.opennms.spring.qosd.AlarmListConnectionManager#reset_list(java.lang.String)
	 */
	/** {@inheritDoc} */
	public void reset_list(String _rebuilt_message) {
		this.alarmMonitorDao.rebuildAlarmList(_rebuilt_message );
	}

	/* (non-Javadoc)
	 * @see org.openoss.opennms.spring.qosd.AlarmListConnectionManager#run()
	 */
	/**
	 * <p>run</p>
	 *
	 * @throws java.lang.IllegalStateException if any.
	 */
	public void run() throws IllegalStateException {
		if(!init)
			throw new IllegalStateException("AlarmListSpringConnectionManagerThread - You must call init() before calling run()");
		
	}

	/* (non-Javadoc)
	 * @see org.openoss.opennms.spring.qosd.AlarmListConnectionManager#send(java.util.Hashtable)
	 */
	/** {@inheritDoc} */
	public void send(Hashtable<AlarmKey,AlarmValue> alarmList) {
		this.alarmMonitorDao.updateAlarmList(alarmList);
	}

	/* (non-Javadoc)
	 * @see org.openoss.opennms.spring.qosd.AlarmListConnectionManager#start()
	 */
	/**
	 * <p>start</p>
	 */
	public void start() {
		this.run();

	}

	/**
	 * Makes a new empty alarm value object
	 * NOTE THIS IS A PATCH to proxy for JVTAlarmMonitorSession.makeAlarmValue()
	 *
	 * @return a javax$oss$fm$monitor$AlarmValue object.
	 */
	public  javax.oss.fm.monitor.AlarmValue makeAlarmValue(){
		return new OOSSAlarmValue();
	}

	/**
	 * Makes a new alarm value object pre-populated with internal objects
	 * which have been made from a local invarient specification.
	 * NOTE THIS IS A PATCH to proxy for JVTAlarmMonitorSession
	 *
	 * @return a javax$oss$fm$monitor$AlarmValue object.
	 */
	public javax.oss.fm.monitor.AlarmValue makeAlarmValueFromSpec() {
		javax.oss.fm.monitor.AlarmValue alarmValueSpecification = (javax.oss.fm.monitor.AlarmValue)m_context.getBean("alarmValueSpecification");
			return alarmValueSpecification;
		}

}
