/*
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 2008 The OpenNMS Group, Inc.  All rights reserved.
 * OpenNMS(R) is a derivative work, containing both original code, included code and modified
 * code that was published under the GNU General Public License. Copyrights for modified
 * and included code are below.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * For more information contact:
 * OpenNMS Licensing       <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 */

package org.opennms.opennmsd;


import org.opennms.opennmsd.AbstractEventForwarder
import groovy.xml.MarkupBuilder

class DefaultEventForwarder extends AbstractEventForwarder {
    
    String host = InetAddress.getLocalHost().hostName;
    String openNmsHost;
    
    protected void forwardEvents(List eventsToFoward) {
        
        System.err.println("openNmsHost is ${openNmsHost}")
        Socket socket = new Socket(openNmsHost, 5817);
        socket.outputStream.withWriter { out ->
        
          def xml = new MarkupBuilder(out);
          xml.log {
              events {
                  for(NNMEvent e in eventsToFoward) {
                      event {
                          uei("uei.opennms.org/internal/discovery/${e.name}")
                          source("opennmsd")
                          time(e.timeStamp)
                          host(m_host)
                          'interface'(e.sourceAddress)
                      }
                  }
              }
          }
        }
        
    }

}