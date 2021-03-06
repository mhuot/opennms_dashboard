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

package org.opennms.core.soa;

/**
 * RegistrationListener
 *
 * @author brozow
 * @version $Id: $
 */
public interface RegistrationListener<T> {
    
    /**
     * <p>providerRegistered</p>
     *
     * @param registration a {@link org.opennms.core.soa.Registration} object.
     * @param provider a T object.
     * @param <T> a T object.
     */
    public void providerRegistered(Registration registration, T provider);
    
    /**
     * <p>providerUnregistered</p>
     *
     * @param registration a {@link org.opennms.core.soa.Registration} object.
     * @param provider a T object.
     */
    public void providerUnregistered(Registration registration, T provider);

}
