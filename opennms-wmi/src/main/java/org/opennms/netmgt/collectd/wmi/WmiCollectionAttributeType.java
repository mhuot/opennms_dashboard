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

package org.opennms.netmgt.collectd.wmi;

import org.opennms.netmgt.config.collector.AttributeGroupType;
import org.opennms.netmgt.config.collector.CollectionAttribute;
import org.opennms.netmgt.config.collector.CollectionAttributeType;
import org.opennms.netmgt.config.collector.Persister;
import org.opennms.netmgt.config.wmi.Attrib;

/**
 * <p>WmiCollectionAttributeType class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
public class WmiCollectionAttributeType implements CollectionAttributeType {
        Attrib m_attribute;
        AttributeGroupType m_groupType;

        /**
         * <p>Constructor for WmiCollectionAttributeType.</p>
         *
         * @param attribute a {@link org.opennms.netmgt.config.wmi.Attrib} object.
         * @param groupType a {@link org.opennms.netmgt.config.collector.AttributeGroupType} object.
         */
        public WmiCollectionAttributeType(final Attrib attribute, final AttributeGroupType groupType) {
            m_groupType = groupType;
            m_attribute = attribute;
        }

        /**
         * <p>getGroupType</p>
         *
         * @return a {@link org.opennms.netmgt.config.collector.AttributeGroupType} object.
         */
        public AttributeGroupType getGroupType() {
            return m_groupType;
        }

        /** {@inheritDoc} */
        public void storeAttribute(final CollectionAttribute attribute, final Persister persister) {
            if ("string".equalsIgnoreCase(m_attribute.getType())) {
                persister.persistStringAttribute(attribute);
            } else {
                persister.persistNumericAttribute(attribute);
            }
        }

        /**
         * <p>getName</p>
         *
         * @return a {@link java.lang.String} object.
         */
        public String getName() {
            return m_attribute.getAlias();
        }

        /**
         * <p>getType</p>
         *
         * @return a {@link java.lang.String} object.
         */
        public String getType() {
            return m_attribute.getType();
        }
}
