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

package org.opennms.netmgt.linkd;

import static org.opennms.core.utils.InetAddressUtils.str;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opennms.core.utils.DBUtils;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.core.utils.LogUtils;
import org.opennms.netmgt.capsd.snmp.SnmpStore;
import org.opennms.netmgt.dao.AtInterfaceDao;
import org.opennms.netmgt.dao.IpInterfaceDao;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.linkd.snmp.CdpCacheTableEntry;
import org.opennms.netmgt.linkd.snmp.Dot1dBaseGroup;
import org.opennms.netmgt.linkd.snmp.Dot1dBasePortTableEntry;
import org.opennms.netmgt.linkd.snmp.Dot1dStpGroup;
import org.opennms.netmgt.linkd.snmp.Dot1dStpPortTableEntry;
import org.opennms.netmgt.linkd.snmp.Dot1dTpFdbTableEntry;
import org.opennms.netmgt.linkd.snmp.IpNetToMediaTableEntry;
import org.opennms.netmgt.linkd.snmp.IpRouteCollectorEntry;
import org.opennms.netmgt.linkd.snmp.QBridgeDot1dTpFdbTableEntry;
import org.opennms.netmgt.linkd.snmp.VlanCollectorEntry;
import org.opennms.netmgt.model.OnmsAtInterface;
import org.opennms.netmgt.model.OnmsIpRouteInterface;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsStpInterface;
import org.opennms.netmgt.model.OnmsStpNode;
import org.opennms.netmgt.model.OnmsVlan;

public abstract class AbstractQueryManager implements QueryManager {
    protected Linkd m_linkd;

    @Override
    public void setLinkd(final Linkd linkd) {
        m_linkd = linkd;
    }

    @Override
    public Linkd getLinkd() {
        return m_linkd;
    }

    protected void sendNewSuspectEvent(final InetAddress ipaddress, final InetAddress ipowner, final String name) {
        getLinkd().sendNewSuspectEvent(ipaddress, ipowner, name);
    }

    public abstract NodeDao getNodeDao();

    public abstract IpInterfaceDao getIpInterfaceDao();

    public abstract AtInterfaceDao getAtInterfaceDao();

    protected abstract int getIfIndexByName(Connection dbConn, int targetCdpNodeId, String cdpTargetDevicePort) throws SQLException;

    protected abstract int getNodeidFromIp(Connection dbConn, InetAddress cdpTargetIpAddr) throws SQLException;

    protected abstract RouterInterface getNodeidMaskFromIp(Connection dbConn, InetAddress nexthop) throws SQLException;

    protected abstract RouterInterface getNodeFromIp(Connection dbConn, InetAddress nexthop) throws SQLException;

    protected abstract int getSnmpIfType(Connection dbConn, int nodeId, Integer ifindex) throws SQLException;

    protected abstract void saveIpRouteInterface(final Connection dbConn, OnmsIpRouteInterface ipRouteInterface) throws SQLException;

    protected abstract void saveVlan(final Connection dbConn, final OnmsVlan vlan) throws SQLException;

    protected abstract void saveStpNode(Connection dbConn, final OnmsStpNode stpNode) throws SQLException;

    protected abstract void saveStpInterface(final Connection dbConn, final OnmsStpInterface stpInterface) throws SQLException;

    protected abstract List<String> getPhysAddrs(final int nodeId, final DBUtils d, final Connection dbConn) throws SQLException;

    protected abstract void markOldDataInactive(final Connection dbConn, final Timestamp now, final int nodeid) throws SQLException;

    protected OnmsNode getNode(Integer nodeId) throws SQLException {
        return getNodeDao().get(nodeId);
    }

    protected void processIpNetToMediaTable(final LinkableNode node, final SnmpCollection snmpcoll, final Connection dbConn, final Timestamp scanTime) throws SQLException {
        if (LogUtils.isDebugEnabled(this)) {
            if (snmpcoll.getIpNetToMediaTable().size() > 0) {
                LogUtils.debugf(this, "processIpNetToMediaTable: Starting ipNetToMedia table processing for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            } else {
                LogUtils.debugf(this, "processIpNetToMediaTable: Zero ipNetToMedia table entries for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            }
        }

        // the AtInterfaces used by LinkableNode where to save info
        final List<OnmsAtInterface> atInterfaces = new ArrayList<OnmsAtInterface>();

        for (final IpNetToMediaTableEntry ent : snmpcoll.getIpNetToMediaTable()) {

            final int ifindex = ent.getIpNetToMediaIfIndex();

            if (ifindex < 0) {
                LogUtils.warnf(this, "processIpNetToMediaTable: invalid ifindex %s", ifindex);
                continue;
            }

            final InetAddress ipaddress = ent.getIpNetToMediaNetAddress();
            final String hostAddress = InetAddressUtils.str(ipaddress);

            if (ipaddress == null || ipaddress.isLoopbackAddress() || hostAddress.equals("0.0.0.0")) {
                LogUtils.warnf(this, "processIpNetToMediaTable: invalid IP: %s", hostAddress);
                continue;
            }

            final String physAddr = ent.getIpNetToMediaPhysAddress();

            if (physAddr == null || physAddr.equals("000000000000") || physAddr.equalsIgnoreCase("ffffffffffff")) {
                LogUtils.warnf(this, "processIpNetToMediaTable: invalid MAC address %s for IP %s", physAddr, hostAddress);
                continue;
            }

            LogUtils.debugf(this, "processIpNetToMediaTable: trying save ipNetToMedia info: IP address %s, MAC address %s, ifIndex %d", hostAddress, physAddr, ifindex);

            // get an AtInterface but without setting MAC address
            final OnmsAtInterface at = getAtInterfaceDao().getAtInterfaceForAddress(dbConn, ipaddress);
            if (at == null) {
                LogUtils.debugf(this, "processIpNetToMediaTable: no node found for IP address %s.", hostAddress);
                sendNewSuspectEvent(ipaddress, snmpcoll.getTarget(), snmpcoll.getPackageName());
                continue;
            }

            at.setSourceNodeId(node.getNodeId());

            if (at.getMacAddress() != null && !at.getMacAddress().equals(physAddr)) {
                LogUtils.infof(this, "processIpNetToMediaTable: Setting OnmsAtInterface MAC address to %s but it used to be '%s' (IP Address = %s, ifIndex = %d)", physAddr, at.getMacAddress(), hostAddress, ifindex);
            }
            at.setMacAddress(physAddr);

            if (at.getIfIndex() != null && !at.getIfIndex().equals(ifindex)) {
                LogUtils.infof(this, "processIpNetToMediaTable: Setting OnmsAtInterface ifIndex to %d but it used to be '%s' (IP Address = %s, MAC = %s)", ifindex, at.getIfIndex(), hostAddress, physAddr);
            }
            at.setIfIndex(ifindex);

            at.setLastPollTime(scanTime);
            at.setStatus(DbAtInterfaceEntry.STATUS_ACTIVE);

            // add AtInterface to list of valid interfaces
            atInterfaces.add(at);

            getAtInterfaceDao().saveAtInterface(dbConn, at);
        }
        // set AtInterfaces in LinkableNode
        node.setAtInterfaces(atInterfaces);
    }

    protected void processCdpCacheTable(final LinkableNode node, final SnmpCollection snmpcoll, final Connection dbConn, final Timestamp scanTime) throws SQLException {
        if (LogUtils.isDebugEnabled(this)) {
            if (snmpcoll.getCdpCacheTable().size() > 0) {
                LogUtils.debugf(this, "processCdpCacheTable: Starting CDP cache table processing for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            } else {
                LogUtils.debugf(this, "processCdpCacheTable: Zero CDP cache table entries for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            }
        }

        List<CdpInterface> cdpInterfaces = new ArrayList<CdpInterface>();

        for (final CdpCacheTableEntry cdpEntry : snmpcoll.getCdpCacheTable()) {
            final int cdpAddrType = cdpEntry.getCdpCacheAddressType();

            if (cdpAddrType != CDP_ADDRESS_TYPE_IP_ADDRESS) {
                LogUtils.warnf(this, "processCdpCacheTable: CDP address type not valid: %d", cdpAddrType);
                continue;
            }

            final InetAddress cdpTargetIpAddr = cdpEntry.getCdpCacheAddress();
            final String hostAddress = InetAddressUtils.str(cdpTargetIpAddr);

            if (cdpTargetIpAddr == null || cdpTargetIpAddr.isLoopbackAddress() || hostAddress.equals("0.0.0.0")) {
                LogUtils.debugf(this, "processCdpCacheTable: IP address is not valid: %s", hostAddress);
                continue;
            }

            LogUtils.debugf(this, "processCdpCacheTable: IP address found: %s", hostAddress);

            final int cdpIfIndex = cdpEntry.getCdpCacheIfIndex();

            if (cdpIfIndex < 0) {
                LogUtils.debugf(this, "processCdpCacheTable: ifIndex not valid: %d", cdpIfIndex);
                continue;
            }

            LogUtils.debugf(this, "processCdpCacheTable: ifIndex found: %d", cdpIfIndex);

            final String cdpTargetDevicePort = cdpEntry.getCdpCacheDevicePort();

            if (cdpTargetDevicePort == null) {
                LogUtils.warnf(this, "processCdpCacheTable: Target device port not found. Skipping.");
                continue;
            }

            LogUtils.debugf(this, "processCdpCacheTable: Target device port name found: %s", cdpTargetDevicePort);

            int targetCdpNodeId = -1;

            if (!cdpTargetIpAddr.isLoopbackAddress() && hostAddress.equals("0.0.0.0")) {
                targetCdpNodeId = getNodeidFromIp(dbConn, cdpTargetIpAddr);
            }

            if (targetCdpNodeId == -1) {
                LogUtils.infof(this, "processCdpCacheTable: No node ID found: interface %s not added to linkable SNMP node. Skipping.", hostAddress);
                sendNewSuspectEvent(cdpTargetIpAddr, snmpcoll.getTarget(), snmpcoll.getPackageName());
                continue;
            }

            final int cdpTargetIfindex = getIfIndexByName(dbConn, targetCdpNodeId, cdpTargetDevicePort);

            if (cdpTargetIfindex == -1) {
                LogUtils.infof(this, "processCdpCacheTable: No valid target ifIndex found: interface not added to linkable SNMP node. Skipping.");
                continue;
            }

            final CdpInterface cdpIface = new CdpInterface(cdpIfIndex);
            cdpIface.setCdpTargetNodeId(targetCdpNodeId);
            cdpIface.setCdpTargetIpAddr(cdpTargetIpAddr);
            cdpIface.setCdpTargetIfIndex(cdpTargetIfindex);

            LogUtils.debugf(this, "processCdpCacheTable: Adding interface to linkable SNMP node: %s", cdpIface);

            cdpInterfaces.add(cdpIface);
        }
        node.setCdpInterfaces(cdpInterfaces);
    }

    protected void processRouteTable(final LinkableNode node, final SnmpCollection snmpcoll, final Connection dbConn, final Timestamp scanTime) throws SQLException {
        if (LogUtils.isDebugEnabled(this)) {
            if (snmpcoll.getIpRouteTable().size() > 0) {
                LogUtils.debugf(this, "processRouteTable: Starting route table processing for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            } else {
                LogUtils.debugf(this, "processRouteTable: Zero route table entries for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            }
        }

        List<RouterInterface> routeInterfaces = new ArrayList<RouterInterface>();

        for (final SnmpStore ent : snmpcoll.getIpRouteTable()) {
            Integer ifindex = ent.getInt32(IpRouteCollectorEntry.IP_ROUTE_IFINDEX);

            final InetAddress nexthop = ent.getIPAddress(IpRouteCollectorEntry.IP_ROUTE_NXTHOP);

            if (nexthop == null) {
                LogUtils.warnf(this, "processRouteTable: next hop not found on node %d. Skipping.", node.getNodeId());
                continue;
            }

            final InetAddress routedest = ent.getIPAddress(IpRouteCollectorEntry.IP_ROUTE_DEST);
            if (routedest == null) {
                LogUtils.warnf(this, "processRouteTable: route destination not found on node %d. Skipping.", node.getNodeId());
                continue;
            }

            final InetAddress routemask = ent.getIPAddress(IpRouteCollectorEntry.IP_ROUTE_MASK);

            if (routemask == null) {
                LogUtils.warnf(this, "processRouteTable: route mask not found on node %d. Skipping.", node.getNodeId());
                continue;
            }

            if (ifindex == null || ifindex < 0) {
                LogUtils.warnf(this, "processRouteTable: Invalid ifIndex %d on node %d. Skipping.", ifindex, node.getNodeId());
                continue;
            } else if (ifindex == 0) {
                // According to the RFC, if the ifindex is zero (0) then this indicates that no
                // particular interface was specified. We need to figure out the ifindex in that case.
                /*
                inetCidrRouteIfIndex OBJECT-TYPE
                   SYNTAX InterfaceIndexOrZero
                   DESCRIPTION
                       "The ifIndex value that identifies the local interface
                       through which the next hop of this route should be
                       reached.  A value of 0 is valid and represents the
                       scenario where no interface is specified."
                   ::= { inetCidrRouteEntry 7 }
                */
                ifindex = getIfIndexFromRouteTableEntries(nexthop, snmpcoll.getIpRouteTable());
                if (ifindex < 1) {
                    LogUtils.warnf(this, "processRouteTable: Invalid ifIndex %d on node %d. Skipping.", ifindex, node.getNodeId());
                    continue;
                }
            }

            LogUtils.debugf(this, "processRouteTable: parsing routeDest/routeMask/nextHop: %s/%s/%s - ifIndex = %d", str(routedest), str(routemask), str(nexthop), ifindex);

            final Integer routemetric1 = ent.getInt32(IpRouteCollectorEntry.IP_ROUTE_METRIC1);

            /**
             * FIXME: send routedest 0.0.0.0 to discoverylink remember that
             * now nexthop 0.0.0.0 is not parsed, anyway we should analyze
             * this case in link discovery so here is the place where you can
             * have this info saved for now is discarded. See DiscoveryLink
             * for more details......
             */

            // the routerinterface constructor set nodeid, ifindex, netmask for nexthop address
            // try to find on snmpinterface table
            RouterInterface routeIface = getNodeidMaskFromIp(dbConn, nexthop);

            // if target node is not snmp node always try to find info
            // on ipinterface table
            if (routeIface == null) {
                routeIface = getNodeFromIp(dbConn, nexthop);
            }

            if (routeIface == null) {
                LogUtils.infof(this, "processRouteTable: No node ID found for next hop IP address %s. Not adding the IP route interface to the linkable SNMP node.", str(nexthop));
                // try to find it in ipinterface
                sendNewSuspectEvent(nexthop, snmpcoll.getTarget(), snmpcoll.getPackageName());
            } else {
                int snmpiftype = -2;

                if (ifindex >= 0)
                    snmpiftype = getSnmpIfType(dbConn, node.getNodeId(), ifindex);

                if (snmpiftype == -1) {
                    LogUtils.warnf(this, "processRouteTable: interface has an invalid ifType (%d). Skipping.", snmpiftype);
                } else if (nexthop.isLoopbackAddress()) {
                    LogUtils.infof(this, "processRouteTable: next hop is a loopback address. Skipping.");
                } else if (InetAddressUtils.str(nexthop).equals("0.0.0.0")) {
                    LogUtils.infof(this, "processRouteTable: next hop is a broadcast address. Skipping.");
                } else if (nexthop.isMulticastAddress()) {
                    LogUtils.infof(this, "processRouteTable: next hop is a multicast address. Skipping.");
                } else if (routemetric1 == null || routemetric1 < 0) {
                    LogUtils.infof(this, "processRouteTable: Route metric is invalid. Skipping.");
                } else {
                    LogUtils.debugf(this, "processRouteTable: Interface has a valid ifType (%d). Adding.", snmpiftype);

                    routeIface.setRouteDest(routedest);
                    routeIface.setRoutemask(routemask);
                    routeIface.setSnmpiftype(snmpiftype);
                    routeIface.setIfindex(ifindex);
                    routeIface.setMetric(routemetric1);
                    routeIface.setNextHop(nexthop);
                    routeInterfaces.add(routeIface);

                }
            }

            final Integer routemetric2 = ent.getInt32(IpRouteCollectorEntry.IP_ROUTE_METRIC2);
            final Integer routemetric3 = ent.getInt32(IpRouteCollectorEntry.IP_ROUTE_METRIC3);
            final Integer routemetric4 = ent.getInt32(IpRouteCollectorEntry.IP_ROUTE_METRIC4);
            final Integer routemetric5 = ent.getInt32(IpRouteCollectorEntry.IP_ROUTE_METRIC5);
            final Integer routetype = ent.getInt32(IpRouteCollectorEntry.IP_ROUTE_TYPE);
            final Integer routeproto = ent.getInt32(IpRouteCollectorEntry.IP_ROUTE_PROTO);

            // always save info to DB
            if (snmpcoll.getSaveIpRouteTable()) {

                final OnmsNode onmsNode = getNode(node.getNodeId());
                final OnmsIpRouteInterface ipRouteInterface = new OnmsIpRouteInterface();
                ipRouteInterface.setLastPollTime(scanTime);
                ipRouteInterface.setNode(onmsNode);
                ipRouteInterface.setRouteDest(str(routedest));
                ipRouteInterface.setRouteIfIndex(ifindex);
                ipRouteInterface.setRouteMask(str(routemask));
                ipRouteInterface.setRouteMetric1(routemetric1);
                ipRouteInterface.setRouteMetric2(routemetric2);
                ipRouteInterface.setRouteMetric3(routemetric3);
                ipRouteInterface.setRouteMetric4(routemetric4);
                ipRouteInterface.setRouteMetric5(routemetric5);
                ipRouteInterface.setRouteNextHop(str(nexthop));
                ipRouteInterface.setRouteProto(routeproto);
                ipRouteInterface.setRouteType(routetype);
                ipRouteInterface.setStatus(DbAtInterfaceEntry.STATUS_ACTIVE);

                saveIpRouteInterface(dbConn, ipRouteInterface);
            }
        }
        node.setRouteInterfaces(routeInterfaces);
    }

    private static Integer getIfIndexFromRouteTableEntries(InetAddress nexthop, Collection<SnmpStore> entries) {
        for (SnmpStore entry : entries) {
            final InetAddress routedest = entry.getIPAddress(IpRouteCollectorEntry.IP_ROUTE_DEST);
            if (routedest == null) {
                continue;
            }

            final InetAddress routemask = entry.getIPAddress(IpRouteCollectorEntry.IP_ROUTE_MASK);
            if (routemask == null) {
                continue;
            }

            // Use a binary AND to determine if the next hop is in the subnet for this entry
            if (InetAddressUtils.toInteger(routemask).and(InetAddressUtils.toInteger(nexthop)).equals(InetAddressUtils.toInteger(routedest))) {
                Integer retval =  entry.getInt32(IpRouteCollectorEntry.IP_ROUTE_IFINDEX);
                LogUtils.debugf(AbstractQueryManager.class, "processRouteTable: found ifindex based on subnet mask: %d", retval);
                return retval;
            }
        }
        return -1;
    }

    protected void processVlanTable(final LinkableNode node, final SnmpCollection snmpcoll, final Connection dbConn, final Timestamp scanTime) throws SQLException {
        if (LogUtils.isDebugEnabled(this)) {
            if (snmpcoll.getVlanTable().size() > 0) {
                LogUtils.debugf(this, "processVlanTable: Starting VLAN table processing for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            } else {
                LogUtils.debugf(this, "processVlanTable: Zero VLAN table entries for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            }
        }

        final List<OnmsVlan> vlans = new ArrayList<OnmsVlan>();

        for (final SnmpStore ent : snmpcoll.getVlanTable()) {
            final Integer vlanIndex = ent.getInt32(VlanCollectorEntry.VLAN_INDEX);

            if (vlanIndex == null || vlanIndex < 0) {
                LogUtils.debugf(this, "processVlanTable: VLAN ifIndex was invalid (%d). Skipping.", vlanIndex);
                continue;
            }

            String vlanName = ent.getDisplayString(VlanCollectorEntry.VLAN_NAME);
            if (vlanName == null) {
                vlanName = "default-" + vlanIndex;
                LogUtils.debugf(this, "processVlanTable: No VLAN name found. Setting to '%s'.", vlanName);
            }

            Integer vlanType = ent.getInt32(VlanCollectorEntry.VLAN_TYPE);
            if (vlanType == null) {
                vlanType = DbVlanEntry.VLAN_TYPE_UNKNOWN;
            }

            Integer vlanStatus = ent.getInt32(VlanCollectorEntry.VLAN_STATUS);
            if (vlanStatus == null) {
                vlanStatus = DbVlanEntry.VLAN_STATUS_UNKNOWN;
            }

            final OnmsNode onmsNode = getNode(node.getNodeId());
            final OnmsVlan vlan = new OnmsVlan(vlanIndex, vlanName, vlanStatus, vlanType);
            vlan.setLastPollTime(scanTime);
            vlan.setNode(onmsNode);
            vlan.setStatus(DbVlanEntry.STATUS_ACTIVE);
            vlans.add(vlan);

            LogUtils.debugf(this, "processVlanTable: Saving VLAN entry: %s", vlan);

            saveVlan(dbConn, vlan);

        }
        node.setVlans(vlans);
    }

    protected void processDot1DBase(final LinkableNode node, final SnmpCollection snmpcoll, final DBUtils d, final Connection dbConn, final Timestamp scanTime, final OnmsVlan vlan, final SnmpVlanCollection snmpVlanColl) throws SQLException {

        LogUtils.debugf(this, "processDot1DBase: Starting dot1dBase processing.");

        final Dot1dBaseGroup dod1db = snmpVlanColl.getDot1dBase();

        final String baseBridgeAddress = dod1db.getBridgeAddress();
        if (baseBridgeAddress == null || baseBridgeAddress == "000000000000") {
            LogUtils.infof(this, "processDot1DBase: Invalid base bridge address (%s) on node %d", baseBridgeAddress, node.getNodeId());
            return;
        }

        processStpNode(node, snmpcoll, dbConn, scanTime, vlan, snmpVlanColl);

        if (snmpVlanColl.hasDot1dBasePortTable()) {
            processDot1DBasePortTable(node, snmpcoll, dbConn, scanTime, vlan, snmpVlanColl);
        }

        if (snmpVlanColl.hasDot1dStpPortTable()) {
            processDot1StpPortTable(node, snmpcoll, dbConn, scanTime, vlan, snmpVlanColl);
        }

        if (snmpVlanColl.hasDot1dTpFdbTable()) {
            processDot1DTpFdbTable(node, vlan, snmpVlanColl, scanTime);
        }

        if (snmpVlanColl.hasQBridgeDot1dTpFdbTable()) {
            processQBridgeDot1dTpFdbTable(node, vlan, snmpVlanColl);
        }

        for (final String physaddr : getPhysAddrs(node.getNodeId(), d, dbConn)) {
            node.addBridgeIdentifier(physaddr);
        }

    }

    protected void processQBridgeDot1dTpFdbTable(final LinkableNode node, final OnmsVlan vlan, final SnmpVlanCollection snmpVlanColl) {
        if (LogUtils.isDebugEnabled(this)) {
            if (snmpVlanColl.getQBridgeDot1dFdbTable().size() > 0) {
                LogUtils.debugf(this, "processQBridgeDot1dTpFdbTable: Starting Q-BRIDGE-MIB dot1dTpFdb table processing for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            } else {
                LogUtils.debugf(this, "processQBridgeDot1dTpFdbTable: Zero Q-BRIDGE-MIB dot1dTpFdb table entries for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            }
        }

        for (final QBridgeDot1dTpFdbTableEntry dot1dfdbentry : snmpVlanColl.getQBridgeDot1dFdbTable()) {
            final String curMacAddress = dot1dfdbentry.getQBridgeDot1dTpFdbAddress();

            if (curMacAddress == null || curMacAddress.equals("000000000000")) {
                LogUtils.infof(this, "processQBridgeDot1DTpFdbTable: Invalid MAC addres %s on node %d. Skipping.", curMacAddress, node.getNodeId());
                continue;
            }

            LogUtils.debugf(this, "processQBridgeDot1DTpFdbTable: Found MAC address %s on node %d", curMacAddress, node.getNodeId());

            final int fdbport = dot1dfdbentry.getQBridgeDot1dTpFdbPort();

            if (fdbport == 0 || fdbport == -1) {
                LogUtils.debugf(this, "processQBridgeDot1DTpFdbTable: Invalid FDB port (%d) for MAC address %s on node %d. Skipping.", fdbport, curMacAddress, node.getNodeId());
                continue;
            }

            LogUtils.debugf(this, "processQBridgeDot1DTpFdbTable: Found bridge port %d on node %d.", fdbport, node.getNodeId());

            final int curfdbstatus = dot1dfdbentry.getQBridgeDot1dTpFdbStatus();

            if (curfdbstatus == SNMP_DOT1D_FDB_STATUS_LEARNED) {
                node.addMacAddress(fdbport, curMacAddress, Integer.toString((int) vlan.getVlanId()));
                LogUtils.debugf(this, "processQBridgeDot1DTpFdbTable: Found learned status on bridge port.");
            } else if (curfdbstatus == SNMP_DOT1D_FDB_STATUS_SELF) {
                node.addBridgeIdentifier(curMacAddress);
                LogUtils.debugf(this, "processQBridgeDot1DTpFdbTable: MAC address (%s) is used as bridge identifier.", curMacAddress);
            } else if (curfdbstatus == SNMP_DOT1D_FDB_STATUS_INVALID) {
                LogUtils.debugf(this, "processQBridgeDot1DTpFdbTable: Found 'INVALID' status. Skipping.");
            } else if (curfdbstatus == SNMP_DOT1D_FDB_STATUS_MGMT) {
                LogUtils.debugf(this, "processQBridgeDot1DTpFdbTable: Found 'MGMT' status. Skipping.");
            } else if (curfdbstatus == SNMP_DOT1D_FDB_STATUS_OTHER) {
                LogUtils.debugf(this, "processQBridgeDot1DTpFdbTable: Found 'OTHER' status. Skipping.");
            } else if (curfdbstatus == -1) {
                LogUtils.warnf(this, "processQBridgeDot1DTpFdbTable: Unable to determine status. Skipping.");
            }
        }
    }

    protected void processDot1DTpFdbTable(LinkableNode node, final OnmsVlan vlan, final SnmpVlanCollection snmpVlanColl, Timestamp scanTime) {
        if (LogUtils.isDebugEnabled(this)) {
            if (snmpVlanColl.getDot1dFdbTable().size() > 0) {
                LogUtils.debugf(this, "processDot1DTpFdbTable: Starting dot1dTpFdb table processing for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            } else {
                LogUtils.debugf(this, "processDot1DTpFdbTable: Zero dot1dTpFdb table entries for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            }
        }

        for (final Dot1dTpFdbTableEntry dot1dfdbentry : snmpVlanColl.getDot1dFdbTable()) {
            final String curMacAddress = dot1dfdbentry.getDot1dTpFdbAddress();
            final int fdbport = dot1dfdbentry.getDot1dTpFdbPort();
            final int curfdbstatus = dot1dfdbentry.getDot1dTpFdbStatus();

            if (curMacAddress == null || curMacAddress.equals("000000000000")) {
                LogUtils.infof(this, "processDot1DTpFdbTable: Invalid MAC address %s on node %d. Skipping.", curMacAddress, node.getNodeId());
                continue;
            }

            LogUtils.debugf(this, "processDot1DTpFdbTable: Found valid MAC address %s on node %d", curMacAddress, node.getNodeId());

            if (fdbport == 0 || fdbport == -1) {
                LogUtils.debugf(this, "processDot1DTpFdbTable: Invalid FDB port (%d) for MAC address %s on node %d. Skipping.", fdbport, curMacAddress, node.getNodeId());
                continue;
            }

            LogUtils.debugf(this, "processDot1DTpFdbTable: MAC address (%s) found on bridge port %d on node %d", curMacAddress, fdbport, node.getNodeId());

            if (curfdbstatus == SNMP_DOT1D_FDB_STATUS_LEARNED && vlan.getVlanId() != null) {
                node.addMacAddress(fdbport, curMacAddress, vlan.getVlanId().toString());
                LogUtils.debugf(this, "processDot1DTpFdbTable: Found learned status on bridge port.");
            } else if (curfdbstatus == SNMP_DOT1D_FDB_STATUS_SELF) {
                node.addBridgeIdentifier(curMacAddress);
                LogUtils.debugf(this, "processDot1DTpFdbTable: MAC address (%s) is used as bridge identifier.", curMacAddress);
            } else if (curfdbstatus == SNMP_DOT1D_FDB_STATUS_INVALID) {
                LogUtils.debugf(this, "processDot1DTpFdbTable: Found 'INVALID' status. Skipping.");
            } else if (curfdbstatus == SNMP_DOT1D_FDB_STATUS_MGMT) {
                LogUtils.debugf(this, "processDot1DTpFdbTable: Found 'MGMT' status. Skipping.");
            } else if (curfdbstatus == SNMP_DOT1D_FDB_STATUS_OTHER) {
                LogUtils.debugf(this, "processDot1DTpFdbTable: Found 'OTHER' status. Skipping.");
            } else if (curfdbstatus == -1) {
                LogUtils.warnf(this, "processDot1DTpFdbTable: Unable to determine status. Skipping.");
            }
        }
    }

    protected void processDot1StpPortTable(final LinkableNode node, final SnmpCollection snmpcoll, final Connection dbConn, final Timestamp scanTime, final OnmsVlan vlan, final SnmpVlanCollection snmpVlanColl) throws SQLException {
        if (LogUtils.isDebugEnabled(this)) {
            if (snmpVlanColl.getDot1dStpPortTable().size() > 0) {
                LogUtils.debugf(this, "processDot1StpPortTable: Starting dot1StpPort table processing for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            } else {
                LogUtils.debugf(this, "processDot1StpPortTable: Zero dot1StpPort table entries for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            }
        }

        for (final Dot1dStpPortTableEntry dot1dstpptentry : snmpVlanColl.getDot1dStpPortTable()) {

            final int stpport = dot1dstpptentry.getDot1dStpPort();

            if (stpport == -1) {
                LogUtils.infof(this, "processDot1StpPortTable: Found invalid STP port. Skipping.");
                continue;
            }

            final OnmsNode onmsNode = getNode(node.getNodeId());
            if (onmsNode == null) {
                LogUtils.debugf(this, "no node found!");
                continue;
            }

            final OnmsStpInterface stpInterface = new OnmsStpInterface(onmsNode, stpport, vlan.getVlanId());
            stpInterface.setStatus(DbStpNodeEntry.STATUS_ACTIVE);
            stpInterface.setLastPollTime(scanTime);

            String stpPortDesignatedBridge = dot1dstpptentry.getDot1dStpPortDesignatedBridge();
            String stpPortDesignatedPort = dot1dstpptentry.getDot1dStpPortDesignatedPort();

            if (stpPortDesignatedBridge == null || stpPortDesignatedBridge.equals("0000000000000000")) {
                LogUtils.infof(this, "processDot1StpPortTable: Designated bridge (%s) is invalid on node %d. Skipping.", stpPortDesignatedBridge, node.getNodeId());
                stpPortDesignatedBridge = "0000000000000000";
            } else if (stpPortDesignatedPort == null || stpPortDesignatedPort.equals("0000")) {
                LogUtils.infof(this, "processDot1StpPortTable: Designated port (%s) is invalid on node %d. Skipping.", stpPortDesignatedPort, node.getNodeId());
                stpPortDesignatedPort = "0000";
            } else {
                stpInterface.setStpPortState(dot1dstpptentry.getDot1dStpPortState());
                stpInterface.setStpPortPathCost(dot1dstpptentry.getDot1dStpPortPathCost());
                stpInterface.setStpPortDesignatedBridge(stpPortDesignatedBridge);
                stpInterface.setStpPortDesignatedRoot(dot1dstpptentry.getDot1dStpPortDesignatedRoot());
                stpInterface.setStpPortDesignatedCost(dot1dstpptentry.getDot1dStpPortDesignatedCost());
                stpInterface.setStpPortDesignatedPort(stpPortDesignatedPort);
                node.addStpInterface(stpInterface);
            }

            if (snmpcoll.getSaveStpInterfaceTable()) {
                saveStpInterface(dbConn, stpInterface);
            }
        }
    }

    protected void processDot1DBasePortTable(final LinkableNode node, final SnmpCollection snmpcoll, final Connection dbConn, final Timestamp scanTime, final OnmsVlan vlan, final SnmpVlanCollection snmpVlanColl) throws SQLException {
        if (LogUtils.isDebugEnabled(this)) {
            if (snmpVlanColl.getDot1dBasePortTable().size() > 0) {
                LogUtils.debugf(this, "processDot1DBasePortTable: Starting dot1BasePort table processing for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            } else {
                LogUtils.debugf(this, "processDot1DBasePortTable: Zero dot1BasePort table entries for %d/%s", node.getNodeId(), str(node.getSnmpPrimaryIpAddr()));
            }
        }

        for (final Dot1dBasePortTableEntry dot1dbaseptentry : snmpVlanColl.getDot1dBasePortTable()) {
            int baseport = dot1dbaseptentry.getBaseBridgePort();
            int ifindex = dot1dbaseptentry.getBaseBridgePortIfindex();

            if (baseport == -1 || ifindex == -1) {
                LogUtils.infof(this, "processDot1DBasePortTable: Invalid base port (%d) or ifIndex (%d). Skipping.", baseport, ifindex);
                continue;
            }

            node.setIfIndexBridgePort(ifindex, baseport);

            
            
            if (snmpcoll.getSaveStpInterfaceTable()) {
                final OnmsNode onmsNode = getNode(node.getNodeId());
                if (onmsNode == null) {
                    LogUtils.debugf(this, "no node found!");
                    continue;
                }
                final OnmsStpInterface stpInterface = new OnmsStpInterface(onmsNode, baseport, vlan.getVlanId());
                stpInterface.setBridgePort(baseport);
                stpInterface.setVlan(vlan.getVlanId());
                stpInterface.setIfIndex(ifindex);
                stpInterface.setStatus(DbStpNodeEntry.STATUS_ACTIVE);
                stpInterface.setLastPollTime(scanTime);

                saveStpInterface(dbConn, stpInterface);
            }
        }
    }

    protected void processStpNode(final LinkableNode node, final SnmpCollection snmpcoll, final Connection dbConn, final Timestamp scanTime, final OnmsVlan vlan, final SnmpVlanCollection snmpVlanColl) throws SQLException {
        LogUtils.debugf(this, "processStpNode: Starting STP node processing.");

        final Dot1dBaseGroup dod1db = snmpVlanColl.getDot1dBase();
        final String baseBridgeAddress = dod1db.getBridgeAddress();

        if (vlan.getVlanId() != null) {
            node.addBridgeIdentifier(baseBridgeAddress, vlan.getVlanId().toString());
        }

        final OnmsNode onmsNode = getNode(node.getNodeId());
        final OnmsStpNode stpNode = new OnmsStpNode(onmsNode, vlan.getVlanId());
        stpNode.setLastPollTime(scanTime);
        stpNode.setStatus(DbStpNodeEntry.STATUS_ACTIVE);

        if (snmpcoll.getSaveStpNodeTable()) {
            LogUtils.debugf(this, "baseBridgeAddress = %s", baseBridgeAddress);
            stpNode.setBaseBridgeAddress(baseBridgeAddress);
            stpNode.setBaseNumPorts(dod1db.getNumberOfPorts());
            stpNode.setBaseType(dod1db.getBridgeType());
            stpNode.setBaseVlanName(vlan.getVlanName());
        }

        if (snmpVlanColl.hasDot1dStp()) {
            LogUtils.debugf(this, "store: adding Dot1dStpGroup in stpnode table");

            final Dot1dStpGroup dod1stp = snmpVlanColl.getDot1dStp();

            stpNode.setStpProtocolSpecification(dod1stp.getStpProtocolSpecification());
            stpNode.setStpPriority(dod1stp.getStpPriority());
            stpNode.setStpRootCost(dod1stp.getStpRootCost());
            stpNode.setStpRootPort(dod1stp.getStpRootPort());

            String stpDesignatedRoot = dod1stp.getStpDesignatedRoot();

            if (stpDesignatedRoot == null || stpDesignatedRoot == "0000000000000000") {
                LogUtils.debugf(this, "store: Dot1dStpGroup found stpDesignatedRoot " + stpDesignatedRoot + ", not adding to Linkable node");
                stpDesignatedRoot = "0000000000000000";
            } else {
                if (stpNode.getBaseVlan() != null) {
                    node.setVlanStpRoot(vlan.getVlanId().toString(), stpDesignatedRoot);
                }
            }
            stpNode.setStpDesignatedRoot(stpDesignatedRoot);
        }
        // store object in database
        if (snmpcoll.getSaveStpNodeTable()) {
            saveStpNode(dbConn, stpNode);
        }
    }

}
