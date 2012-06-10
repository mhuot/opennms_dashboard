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

package org.opennms.web.element;

import java.util.HashMap;
import java.util.Map;

import org.opennms.netmgt.linkd.DbStpInterfaceEntry;


/**
 * <p>StpInterface class.</p>
 *
 * @author <a href="mailto:antonio@opennms.it">Antonio Russo</a>
 */
public class StpInterface
{
        int     m_nodeId;
        int     m_bridgeport;
		int     m_ifindex;
		int     m_stpportstate;
		int     m_stpportpathcost;
		int     m_stpportdesignatedcost;
		int     m_stpvlan;
		String  m_ipaddr;
		String  m_stpdesignatedroot;
        String  m_stpdesignatedbridge;
		String  m_stpdesignatedport;
        String  m_lastPollTime;
        char    m_status;
        int		m_stprootnodeid;
        int		m_stpbridgenodeid;

        /**
         * <p>String identifiers for the enumeration of values:</p>
         * <ul>
         * <li>{@link DbStpInterfaceEntry#STP_PORT_DISABLED}</li>
         * <li>{@link DbStpInterfaceEntry#STP_PORT_BLOCKING}</li>
         * <li>{@link DbStpInterfaceEntry#STP_PORT_LISTENING}</li>
         * <li>{@link DbStpInterfaceEntry#STP_PORT_LEARNING}</li>
         * <li>{@link DbStpInterfaceEntry#STP_PORT_FORWARDING}</li>
         * <li>{@link DbStpInterfaceEntry#STP_PORT_BROKEN}</li>
         * </ul>
         */ 
        public static final String[] STP_PORT_STATUS = new String[] {
            null,         //0 (not a valid index)
            "Disabled",   //1
            "Blocking",   //2
            "Listening",  //3
            "Learning",   //4
            "Forwarding", //5
            "Broken",     //6
          };

        private static final Map<Character, String> statusMap = new HashMap<Character, String>();
      	
        static {
            statusMap.put( DbStpInterfaceEntry.STATUS_ACTIVE, "Active" );
            statusMap.put( DbStpInterfaceEntry.STATUS_UNKNOWN, "Unknown" );
            statusMap.put( DbStpInterfaceEntry.STATUS_DELETED, "Deleted" );
            statusMap.put( DbStpInterfaceEntry.STATUS_NOT_POLLED, "Not Active" );
        }


        /* package-protected so only the NetworkElementFactory can instantiate */
        StpInterface()
        {
        }

        /* package-protected so only the NetworkElementFactory can instantiate */
        StpInterface(        int     nodeId,
	int     bridgeport,
	int     ifindex,
	int     stpportstate,
	int     stpportpathcost,
	int     stpportdesignatedcost,
	int     stpvlan,
	String  stpdesignatedroot,
	String  stpdesignatedbridge,
	String  stpdesignatedport,
	String  lastPollTime,
	char    status,
	int		stprootnodeid,
	int 	stpbridgenodeid
)
        {
                m_nodeId = nodeId;
                m_bridgeport = bridgeport;
				m_ifindex = ifindex;
				m_stpportstate = stpportstate;
				m_stpportpathcost = stpportpathcost;
				m_stpportdesignatedcost = stpportdesignatedcost;
				m_stpvlan = stpvlan;
                m_stpdesignatedbridge = stpdesignatedbridge;
                m_stpdesignatedroot = stpdesignatedroot;
				m_stpdesignatedport = stpdesignatedport;
				m_lastPollTime = lastPollTime; 
                m_status = status;
                m_stprootnodeid = stprootnodeid;
                m_stpbridgenodeid = stpbridgenodeid;
        }

        /**
         * <p>toString</p>
         *
         * @return a {@link java.lang.String} object.
         */
        public String toString()
        {
                StringBuffer str = new StringBuffer("Node Id = " + m_nodeId + "\n" );
                str.append("Bridge number of ports = " + m_bridgeport + "\n" );
                str.append("At Last Poll Time = " + m_lastPollTime + "\n" );
                str.append("Node At Status= " + m_status + "\n" );
                return str.toString();
        }

		/**
		 * <p>get_bridgeport</p>
		 *
		 * @return a int.
		 */
		public int get_bridgeport() {
			return m_bridgeport;
		}

		/**
		 * <p>get_ifindex</p>
		 *
		 * @return a int.
		 */
		public int get_ifindex() {
			return m_ifindex;
		}

		/**
		 * <p>get_lastPollTime</p>
		 *
		 * @return a {@link java.lang.String} object.
		 */
		public String get_lastPollTime() {
			return m_lastPollTime;
		}

		/**
		 * <p>get_nodeId</p>
		 *
		 * @return a int.
		 */
		public int get_nodeId() {
			return m_nodeId;
		}

		/**
		 * <p>get_status</p>
		 *
		 * @return a char.
		 */
		public char get_status() {
			return m_status;
		}

		/**
		 * <p>get_stpdesignatedbridge</p>
		 *
		 * @return a {@link java.lang.String} object.
		 */
		public String get_stpdesignatedbridge() {
			return m_stpdesignatedbridge;
		}

		/**
		 * <p>get_stpdesignatedport</p>
		 *
		 * @return a {@link java.lang.String} object.
		 */
		public String get_stpdesignatedport() {
			return m_stpdesignatedport;
		}

		/**
		 * <p>get_stpdesignatedroot</p>
		 *
		 * @return a {@link java.lang.String} object.
		 */
		public String get_stpdesignatedroot() {
			return m_stpdesignatedroot;
		}

		/**
		 * <p>get_stpportdesignatedcost</p>
		 *
		 * @return a int.
		 */
		public int get_stpportdesignatedcost() {
			return m_stpportdesignatedcost;
		}

		/**
		 * <p>get_stpportpathcost</p>
		 *
		 * @return a int.
		 */
		public int get_stpportpathcost() {
			return m_stpportpathcost;
		}

		/**
		 * <p>get_stpportstate</p>
		 *
		 * @return a int.
		 */
		public int get_stpportstate() {
			return m_stpportstate;
		}

		/**
		 * <p>getStpPortState</p>
		 *
		 * @return a {@link java.lang.String} object.
		 */
		public String getStpPortState() {
		    try {
		        return STP_PORT_STATUS[m_stpportstate];
		    } catch (ArrayIndexOutOfBoundsException e) {
		        return STP_PORT_STATUS[DbStpInterfaceEntry.STP_PORT_DISABLED];
		    }
		}
			
		/**
		 * <p>get_stpvlan</p>
		 *
		 * @return a int.
		 */
		public int get_stpvlan() {
			return m_stpvlan;
		}

        /**
         * <p>get_stpbridgenodeid</p>
         *
         * @return Returns the m_stpdesignatedbridgenodeid.
         */
        public int get_stpbridgenodeid() {
            return m_stpbridgenodeid;
        }
        /**
         * <p>get_stprootnodeid</p>
         *
         * @return Returns the m_stpdesignatedrootnodeid.
         */
        public int get_stprootnodeid() {
            return m_stprootnodeid;
        }
		/**
		 * <p>get_ipaddr</p>
		 *
		 * @return Returns the m_ipaddr.
		 */
		public String get_ipaddr() {
			return m_ipaddr;
		}
		/**
		 * <p>set_ipaddr</p>
		 *
		 * @param m_ipaddr The m_ipaddr to set.
		 */
		public void set_ipaddr(String m_ipaddr) {
			this.m_ipaddr = m_ipaddr;
		}
		
	    /**
	     * <p>getStatusString</p>
	     *
	     * @return a {@link java.lang.String} object.
	     */
	    public String getStatusString() {
	        return statusMap.get( new Character(m_status) );
	    }

	    /**
	     * <p>getVlanColorIdentifier</p>
	     *
	     * @return a {@link java.lang.String} object.
	     */
	    public String getVlanColorIdentifier() {
	        int red = 128;
	        int green = 128;
	        int blue = 128;
	        int redoffset = 47;
	        int greenoffset = 29;
	        int blueoffset = 23;
	        if (m_stpvlan == 0) return "";
	        if (m_stpvlan == 1) return "#FFFFFF";
	        red = (red + m_stpvlan * redoffset)%255;
	        green = (green + m_stpvlan * greenoffset)%255;
	        blue = (blue + m_stpvlan * blueoffset)%255;
	        if (red < 64) red = red+64;
	        if (green < 64) green = green+64;
	        if (blue < 64) blue = blue+64;
	        return "#"+Integer.toHexString(red)+Integer.toHexString(green)+Integer.toHexString(blue);
	    }

}
