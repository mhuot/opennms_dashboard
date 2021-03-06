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

package org.opennms.sms.monitor.internal.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>SequenceOperationMatch class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="match")
public class SequenceOperationMatch {
	@XmlAttribute(name="type")
	private String m_type = "success";
	
	@XmlValue
	private String m_value;
	
	/**
	 * <p>Constructor for SequenceOperationMatch.</p>
	 */
	public SequenceOperationMatch() {
	}
	
	/**
	 * <p>Constructor for SequenceOperationMatch.</p>
	 *
	 * @param type a {@link java.lang.String} object.
	 */
	public SequenceOperationMatch(String type) {
		setType(type);
	}
	
	/**
	 * <p>Constructor for SequenceOperationMatch.</p>
	 *
	 * @param type a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 */
	public SequenceOperationMatch(String type, String value) {
		setType(type);
		setValue(value);
	}

	/**
	 * <p>getType</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getType() {
		return m_type;
	}
	
	/**
	 * <p>setType</p>
	 *
	 * @param type a {@link java.lang.String} object.
	 */
	public void setType(String type) {
		m_type = type;
	}

	/**
	 * <p>getValue</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getValue() {
		return m_value;
	}
	
	/**
	 * <p>setValue</p>
	 *
	 * @param value a {@link java.lang.String} object.
	 */
	public void setValue(String value) {
		m_value = value;
	}
}
