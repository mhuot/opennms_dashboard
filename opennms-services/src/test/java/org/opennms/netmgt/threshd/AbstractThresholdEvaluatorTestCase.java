/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2011 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.threshd;

import org.opennms.netmgt.xml.event.Event;
import org.opennms.netmgt.xml.event.Parm;

import junit.framework.TestCase;

/**
 * @author jeffg
 *
 */
public abstract class AbstractThresholdEvaluatorTestCase extends TestCase {
    protected static void parmPresentAndValueNonNull(Event event, String parmName) {
        boolean parmPresent = false;
        
        for (Parm parm : event.getParmCollection()) {
            if (parmName.equals(parm.getParmName())) {
                assertNotNull("Value content of parm '" + parmName + "'", parm.getValue().getContent());
                parmPresent = true;
            }
        }
        assertTrue("Parm '" + parmName + "' present", parmPresent);
    }
    
    protected static void parmPresentWithValue(Event event, String parmName, String expectedValue) {
        boolean parmPresent = false;
        
        for (Parm parm : event.getParmCollection()) {
            if (parmName.equals(parm.getParmName())) {
                parmPresent = true;
                if (expectedValue.equals(parm.getValue().getContent())) {
                    assertNotNull("Value content of parm '" + parmName + "'", parm.getValue().getContent());
                    assertEquals("Value content of parm '" + parmName + "' should be '" + expectedValue + "'", expectedValue, parm.getValue().getContent());
                    parmPresent = true;
                }
            }
        }
        assertTrue("Parm '" + parmName + "' present", parmPresent);
    }

    /* just here to make surefire happy */
    public void testDoNothing() {
    }
}
