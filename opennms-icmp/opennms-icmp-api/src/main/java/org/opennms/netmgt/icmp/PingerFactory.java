/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.icmp;

import org.opennms.core.utils.LogUtils;

/**
 * <p>PingerFactory class.</p>
 *
 * @author <A HREF="mailto:seth@opennms.org">Seth Leger</A>
 * @author <A HREF="mailto:brozow@opennms.org">Matt Brozowski</A>
 */
public abstract class PingerFactory {
	/**
     * The {@link Pinger} instance.
     */
    private static Pinger m_pinger;

    /**
     * Returns an implementation of the default {@link Pinger} class
     *
     * @return a {@link Pinger} object.
     */
    public static Pinger getInstance() {
        if (m_pinger == null) {
            String pingerClassName = System.getProperty("org.opennms.netmgt.icmp.pingerClass", "org.opennms.netmgt.icmp.jni6.Jni6Pinger");
            Class<? extends Pinger> clazz = null;
            try {
                clazz = Class.forName(pingerClassName).asSubclass(Pinger.class);
                m_pinger = clazz.newInstance();
            } catch (ClassNotFoundException e) {
                IllegalArgumentException ex = new IllegalArgumentException("Unable to find class named " + pingerClassName, e);
                LogUtils.errorf(PingerFactory.class, ex, ex.getMessage());
                throw ex;
            } catch (InstantiationException e) {
                IllegalArgumentException ex = new IllegalArgumentException("Error trying to create pinger of type " + clazz, e);
                LogUtils.errorf(PingerFactory.class, ex, ex.getMessage());
                throw ex;
            } catch (IllegalAccessException e) {
                IllegalArgumentException ex = new IllegalArgumentException("Unable to create pinger of type " + clazz + ".  It does not appear to have a public constructor", e);
                LogUtils.errorf(PingerFactory.class, ex, ex.getMessage());
                throw ex;
            } catch (Throwable e) {
                IllegalArgumentException ex = new IllegalArgumentException("Unexpected exception thrown while trying to create pinger of type " + clazz, e);
                LogUtils.errorf(PingerFactory.class, ex, ex.getMessage());
                throw ex;
            }
        }
        return m_pinger;
    }

    /**
     * <p>setIpcManager</p>
     *
     * @param pinger a {@link Pinger} object.
     */
    public static void setInstance(Pinger pinger) {
        m_pinger = pinger;
    }
    
    /**
     * This is here for unit testing so we can reset this class before
     * every test.
     */
    protected static void reset() {
        m_pinger = null;
    }
    
}
