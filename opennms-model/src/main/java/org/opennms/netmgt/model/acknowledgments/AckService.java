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

package org.opennms.netmgt.model.acknowledgments;

import java.util.Collection;

import org.opennms.netmgt.model.OnmsAcknowledgment;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional boundary for processing acknowledgments
 *
 * @author <a href="mailto:david@opennms.org">David Hustace</a>
 * @author <a href="makilto:jeffg@opennms.org">Jeff Gehlbach</a>
 */
public interface AckService {

    /**
     * <p>processAck</p>
     *
     * @param ack a {@link org.opennms.netmgt.model.OnmsAcknowledgment} object.
     */
    @Transactional(readOnly=false)
    void processAck(OnmsAcknowledgment ack);
    
    /**
     * <p>processAcks</p>
     *
     * @param acks a {@link java.util.Collection} object.
     */
    @Transactional(readOnly=false)
    void processAcks(Collection<OnmsAcknowledgment> acks);

}
