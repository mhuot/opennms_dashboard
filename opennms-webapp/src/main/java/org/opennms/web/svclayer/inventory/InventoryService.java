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

package org.opennms.web.svclayer.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.opennms.core.resource.Vault;
import org.opennms.netmgt.config.RWSConfig;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.rancid.ConnectionProperties;
import org.opennms.rancid.InventoryElement2;
import org.opennms.rancid.InventoryNode;
import org.opennms.rancid.RWSBucket;
import org.opennms.rancid.RWSClientApi;
import org.opennms.rancid.RWSResourceList;
import org.opennms.rancid.RancidApiException;
import org.opennms.rancid.RancidNode;
import org.opennms.rancid.RancidNodeAuthentication;
import org.opennms.rancid.RWSBucket.BucketItem;

import org.opennms.web.element.ElementUtil;
import org.opennms.web.inventory.*;
import org.springframework.beans.factory.InitializingBean;

/**
 * <p>InventoryService class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.8.1
 */
public class InventoryService implements InitializingBean {
    RWSConfig m_rwsConfig;
    NodeDao m_nodeDao;
    ConnectionProperties m_cp;
    
    
    /**
     * <p>afterPropertiesSet</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
            RWSClientApi.init();
            m_cp = m_rwsConfig.getBase();
    }
    
    
    /**
     * <p>Constructor for InventoryService.</p>
     */
    public InventoryService() {
        super();
    }
    
    /**
     * <p>getRwsConfig</p>
     *
     * @return a {@link org.opennms.netmgt.config.RWSConfig} object.
     */
    public RWSConfig getRwsConfig() {
        return m_rwsConfig;
    }
    /**
     * <p>setRwsConfig</p>
     *
     * @param rwsConfig a {@link org.opennms.netmgt.config.RWSConfig} object.
     */
    public void setRwsConfig(RWSConfig rwsConfig) {
        m_rwsConfig = rwsConfig;
    }
    /**
     * <p>getNodeDao</p>
     *
     * @return a {@link org.opennms.netmgt.dao.NodeDao} object.
     */
    public NodeDao getNodeDao() {
        return m_nodeDao;
    }
    /**
     * <p>setNodeDao</p>
     *
     * @param nodeDao a {@link org.opennms.netmgt.dao.NodeDao} object.
     */
    public void setNodeDao(NodeDao nodeDao) {
        m_nodeDao = nodeDao;
    }
    
    /**
     * <p>checkRWSAlive</p>
     *
     * @return a boolean.
     */
    public boolean checkRWSAlive(){
        try {
            RWSResourceList rl = RWSClientApi.getRWSResourceServicesList(m_cp);
            if (rl!= null){
                return true;
            }
            return false;
        }catch (Throwable e){
            return false;
        }
    }
    
    /**
     * <p>checkRancidNode</p>
     *
     * @param deviceName a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean checkRancidNode(String deviceName){
        
        log().debug("checkRancidNode start " + deviceName);
                
        // Group list 
        try {
            RWSResourceList groups = RWSClientApi.getRWSResourceGroupsList(m_cp);
            List<String> grouplist = groups.getResource();
            Iterator<String> iter1 = grouplist.iterator();
           
            if (iter1.hasNext()){
                String groupname = iter1.next();
                log().debug("checkRancidNode " + deviceName + " group " + groupname);        
                
                try {
                    RancidNode rn = RWSClientApi.getRWSRancidNodeTLO(m_cp, groupname, deviceName);
                    if (rn!=null){
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                 catch (RancidApiException e){
                     log().debug("No inventory information associated to " + deviceName);
                     return false;
                }
            }

        }catch (Throwable e){
            return false;
        }
        return true;
    }

    /*
     * getRancidNodeBase get 
     * data from nodeDao 
     */
    /**
     * <p>getRancidNodeBase</p>
     *
     * @param nodeid a int.
     * @return a java$util$Map object.
     */
    public Map<String, Object> getRancidNodeBase(int nodeid) {
        
        log().debug("getRancidNodeBase start for nodeid: " + nodeid);
        Map<String, Object> nodeModel = new TreeMap<String, Object>();

        nodeModel.put("RWSStatus","OK");
        OnmsNode node = m_nodeDao.get(nodeid);
        String rancidName = node.getLabel();
        
        log().debug("getRancidNodeBase rancid node name: " + rancidName);


        nodeModel.put("id", rancidName);
        nodeModel.put("db_id", nodeid);
        nodeModel.put("status_general", ElementUtil.getNodeStatusString(node.getType().charAt(0)));

        // TODO find a method to get root service for URL
        nodeModel.put("url", m_cp.getUrl()+m_cp.getDirectory());

        String rancidIntegrationUseOnlyRancidAdapterProperty = Vault.getProperty("opennms.rancidIntegrationUseOnlyRancidAdapter"); 
        log().debug("getRancidNodeBase opennms.rancidIntegrationUseOnlyRancidAdapter: " + rancidIntegrationUseOnlyRancidAdapterProperty);
        if (rancidIntegrationUseOnlyRancidAdapterProperty != null &&  "true".equalsIgnoreCase(rancidIntegrationUseOnlyRancidAdapterProperty.trim())) {
            log().debug("getRancidNodeBase permitModifyClogin: false");
            nodeModel.put("permitModifyClogin",false);
        } else {
            log().debug("getRancidNodeBase permitModifyClogin: true");
            nodeModel.put("permitModifyClogin",true);
        }
        
        String foreignSource = node.getForeignSource();
        if (foreignSource != null ) {
            nodeModel.put("foreignSource", foreignSource);
        } else {
            nodeModel.put("foreignSource", "");            
        }

        return nodeModel;
    }


    
    /*
     * getRancidNode will filter any exception, the page will show an empty table
     * in case of node not in DB or device name not in RWS 
     */
    /**
     * <p>getRancidNode</p>
     *
     * @param nodeid a int.
     * @return a java$util$Map object.
     */
    public Map<String, Object> getRancidNode(int nodeid) {
        
        log().debug("getRancidNode start");
        Map<String, Object> nodeModel = getRancidNodeBase(nodeid);
        String rancidName = (String)nodeModel.get("id"); 
        List<RancidNodeWrapper> ranlist = new ArrayList<RancidNodeWrapper>();
        List<BucketItem> bucketlist = new ArrayList<BucketItem>();
        // Group list 
        RWSResourceList groups;
        try {
            groups = RWSClientApi.getRWSResourceGroupsList(m_cp);
        } catch (RancidApiException e) {
            log().error(e.getLocalizedMessage());
            nodeModel.put("RWSStatus",e.getLocalizedMessage());
            return nodeModel;
        }
            
        List<String> grouplist = groups.getResource();
        Iterator<String> iter1 = grouplist.iterator();
        
      
        boolean first = true;
        while (iter1.hasNext()){
            String groupname = iter1.next();
            log().debug("getRancidNode: " + nodeid + " for group " + groupname);        
            
            try {
                if (first){
                    RancidNode rn = RWSClientApi.getRWSRancidNodeTLO(m_cp, groupname, rancidName);
                    nodeModel.put("devicename", rn.getDeviceName());
                    nodeModel.put("status", rn.getState());
                    nodeModel.put("devicetype", rn.getDeviceType());
                    nodeModel.put("comment", rn.getComment());
                    nodeModel.put("groupname", groupname);
                    first = false;
                }
                try {
                    RancidNode rn = RWSClientApi.getRWSRancidNodeInventory(m_cp ,groupname, rancidName);
                    String vs = rn.getHeadRevision();
                    InventoryNode in = (InventoryNode)rn.getNodeVersions().get(vs);

                    RancidNodeWrapper rnw = new RancidNodeWrapper(rn.getDeviceName(), groupname, rn.getDeviceType(), rn.getComment(), rn.getHeadRevision(),
                      rn.getTotalRevisions(), in.getCreationDate(), rn.getRootConfigurationUrl());

                    ranlist.add(rnw); 
                } catch (RancidApiException e) {
                    log().debug("No configuration found for nodeid:" + nodeid  + " on Group: " + groupname + " .Cause: " + e.getLocalizedMessage());                    
                }
            } catch (RancidApiException e){
                if (e.getRancidCode() == 2) {
                    log().debug("No device found in router.db for nodeid:" + nodeid  + " on Group: " + groupname + " .Cause: " + e.getLocalizedMessage());
                } else {
                    nodeModel.put("RWSStatus",e.getLocalizedMessage());
                    log().error(e.getLocalizedMessage());
                }
            }
        }
            
        //Groups invariant            
        nodeModel.put("grouptable", ranlist);

        
        try {
            RWSBucket bucket = RWSClientApi.getBucket(m_cp, rancidName);
            bucketlist.addAll(bucket.getBucketItem());
        } catch (RancidApiException e) {            
            if (e.getRancidCode() == 2) {
                log().debug("No entry in storage for nodeid:" + nodeid  + " nodeLabel: " + rancidName);
            } else {
                nodeModel.put("RWSStatus",e.getLocalizedMessage());
                log().error(e.getLocalizedMessage());
            }
        }
        
        nodeModel.put("bucketitems", bucketlist);        

        return nodeModel;
    }

    /**
     * <p>getBuckets</p>
     *
     * @param nodeid a int.
     * @return a java$util$Map object.
     */
    public Map<String, Object> getBuckets(int nodeid) {
        log().debug("getBuckets start: nodeid: " + nodeid);
        Map<String, Object> nodeModel = getRancidNodeBase(nodeid);
        String rancidName = (String)nodeModel.get("id"); 

        List<BucketItem> bucketlist = new ArrayList<BucketItem>();
        try {
            RWSBucket bucket = RWSClientApi.getBucket(m_cp, rancidName);
            nodeModel.put("bucketexist", true);
            bucketlist.addAll(bucket.getBucketItem());
        } catch (RancidApiException e) {
            if (e.getRancidCode() == 2) {
                nodeModel.put("bucketexist", false);
                log().debug("No entry in storage for nodeid:" + nodeid  + " nodeLabel: " + rancidName);
            } else {
                nodeModel.put("RWSStatus",e.getLocalizedMessage());
                log().error(e.getLocalizedMessage());
            }
        }            
        nodeModel.put("bucketlistsize", bucketlist.size());
        nodeModel.put("bucketitems", bucketlist);        
        return nodeModel;        
    }
    
    /**
     * <p>getRancidNodeList</p>
     *
     * @param nodeid a int.
     * @return a java$util$Map object.
     */
    public Map<String, Object> getRancidNodeList(int nodeid) {
        log().debug("getRancidNodelist start: nodeid: " + nodeid);
        Map<String, Object> nodeModel = getRancidNodeBase(nodeid);
        String rancidName = (String)nodeModel.get("id"); 

                       
        RWSResourceList groups;
        try {
            groups = RWSClientApi.getRWSResourceGroupsList(m_cp);
        } catch (RancidApiException e) {
            nodeModel.put("RWSStatus",e.getLocalizedMessage());
            log().error(e.getLocalizedMessage());
            return nodeModel;
        }
        
        List<InventoryWrapper> ranlist = new ArrayList<InventoryWrapper>();
        
        List<String> grouplist = groups.getResource();
        Iterator<String> iter2 = grouplist.iterator();
        
        boolean first = true;
        String groupname;
        while (iter2.hasNext()) {
            groupname = iter2.next();
        
            RancidNode rn;
            try {
                rn = RWSClientApi.getRWSRancidNodeInventory(m_cp, groupname, rancidName);
                if (first){
                    nodeModel.put("devicename", rn.getDeviceName());
                    first = false;
                }
                RWSResourceList versionList = RWSClientApi.getRWSResourceConfigList(m_cp, groupname, rancidName);
                
                List<String> versionListStr= versionList.getResource();
                
                Iterator<String> iter1 = versionListStr.iterator();
                
                String vs;
                
                while (iter1.hasNext()) {
                    vs = iter1.next();
                    InventoryNode in = (InventoryNode)rn.getNodeVersions().get(vs);
                    InventoryWrapper inwr = new InventoryWrapper(in.getVersionId(), in.getCreationDate(), groupname, in.getConfigurationUrl());
                    ranlist.add(inwr);
                }
            } catch (RancidApiException e) {
                if (e.getRancidCode() == 2) {
                    log().debug("No Inventory found in CVS repository for nodeid:" + nodeid  + " nodeLabel: " + rancidName);
                } else {
                    nodeModel.put("RWSStatus",e.getLocalizedMessage());
                    log().error(e.getLocalizedMessage());
                }
            }
        }
            
        nodeModel.put("grouptable", ranlist);
            
            
        return nodeModel;
        
    }

    /**
     * <p>getRancidNodeList</p>
     *
     * @param nodeid a int.
     * @param group a {@link java.lang.String} object.
     * @return a java$util$Map object.
     */
    public Map<String, Object> getRancidNodeList(int nodeid, String group) {
        log().debug("getRancidlist start: nodeid: " + nodeid + " group: " + group);
        Map<String, Object> nodeModel = getRancidNodeBase(nodeid);
        String rancidName = (String)nodeModel.get("id"); 

        List<InventoryWrapper> ranlist = new ArrayList<InventoryWrapper>();
            
        RancidNode rn;
        try {
            rn = RWSClientApi.getRWSRancidNodeInventory(m_cp, group, rancidName);
            nodeModel.put("devicename", rn.getDeviceName());
        } catch (RancidApiException e) {
            if (e.getRancidCode() == 2) {
                log().debug("No Inventory found in CVS repository for nodeid:" + nodeid  + " nodeLabel: " + rancidName);
            } else {
                nodeModel.put("RWSStatus",e.getLocalizedMessage());
                log().error(e.getLocalizedMessage());
            }
            return nodeModel;
        }

        
        RWSResourceList versionList;
        
        try {
            versionList = RWSClientApi.getRWSResourceConfigList(m_cp, group, rancidName);
        } catch (RancidApiException e) {
            nodeModel.put("RWSStatus",e.getLocalizedMessage());
            log().error(e.getLocalizedMessage());
            return nodeModel;
        }
        
        List<String> versionListStr= versionList.getResource();
        
        Iterator<String> iter1 = versionListStr.iterator();
        
        String vs;
        
        while (iter1.hasNext()) {
            vs = iter1.next();
            InventoryNode in = (InventoryNode)rn.getNodeVersions().get(vs);
            InventoryWrapper inwr = new InventoryWrapper(in.getVersionId(), in.getCreationDate(), group, in.getConfigurationUrl());
            ranlist.add(inwr);
        }
        
        nodeModel.put("grouptable", ranlist);

        return nodeModel;
        
    }

    /**
     * <p>getInventory</p>
     *
     * @param nodeid a int.
     * @param group a {@link java.lang.String} object.
     * @param version a {@link java.lang.String} object.
     * @return a java$util$Map object.
     */
    public Map<String, Object> getInventory(int nodeid,
                                                String group, String version) {
        log().debug("getInventoryNode start: nodeid: " + nodeid + " group: " + group + " version: " + version);
        Map<String, Object> nodeModel = getRancidNodeBase(nodeid);
        String rancidName = (String)nodeModel.get("id"); 

        try {
            RancidNode rn = RWSClientApi.getRWSRancidNodeInventory(m_cp, group, rancidName);
    
            InventoryNode in = (InventoryNode)rn.getNodeVersions().get(version);
            
            nodeModel.put("devicename", rancidName);
            nodeModel.put("groupname", group);
            nodeModel.put("version", version);
            nodeModel.put("status", in.getParent().getState());
            nodeModel.put("creationdate", in.getCreationDate());
            nodeModel.put("swconfigurationurl", in.getSoftwareImageUrl());
            nodeModel.put("configurationurl", in.getConfigurationUrl());

            log().debug("getInventoryNode date: " + in.getCreationDate());

            List<InventoryElement2> ie = RWSClientApi.getRWSRancidNodeInventoryElement2(m_cp, rn, version);
            
            
            Iterator<InventoryElement2> iter1 = ie.iterator();
                                       
            while (iter1.hasNext()) {
                InventoryElement2 ietmp = iter1.next();
                
                log().debug("Adding inventory: " + ietmp.expand());
            }
                               
            nodeModel.put("inventory",ie);
            
        } catch (RancidApiException e) {
            if (e.getRancidCode() == 2) {
                log().debug("No Inventory found in CVS repository for nodeid:" + nodeid  + " nodeLabel: " + rancidName);
            } else {
                nodeModel.put("RWSStatus",e.getLocalizedMessage());
                log().error(e.getLocalizedMessage());
            }
        }
        return nodeModel;
    }


    /**
     * <p>getRancidNodeWithCLoginForGroup</p>
     *
     * @param nodeid a int.
     * @param group a {@link java.lang.String} object.
     * @param adminRole a boolean.
     * @return a java$util$Map object.
     */
    public Map<String, Object> getRancidNodeWithCLoginForGroup(int nodeid,String group, boolean adminRole) {
        log().debug("getRancidNodeWithCloginFroGroup start: group: " + group);
        Map<String, Object> nodeModel = getRancidNodeBase(nodeid);
        String rancidName = (String)nodeModel.get("id"); 
        
        // Group list 
        RWSResourceList groups;
        try {
            groups = RWSClientApi.getRWSResourceGroupsList(m_cp);
        } catch (RancidApiException e) {
            nodeModel.put("RWSStatus",e.getLocalizedMessage());
            log().error(e.getLocalizedMessage());
            return nodeModel;
        }

        List<String> grouplist = groups.getResource();
        nodeModel.put("grouplist",grouplist);

        // DeviceType list 
        RWSResourceList devicetypes;
        try {
            devicetypes = RWSClientApi.getRWSResourceDeviceTypesPatternList(m_cp);
        } catch (RancidApiException e) {
            nodeModel.put("RWSStatus",e.getLocalizedMessage());
            log().error(e.getLocalizedMessage());
            return nodeModel;
        }

        List<String> devicetypelist = devicetypes.getResource();
        nodeModel.put("devicetypelist",devicetypelist);
        
        nodeModel.put("groupname", group);
        
        try {
            RancidNode rn = RWSClientApi.getRWSRancidNodeTLO(m_cp, group, rancidName);
            nodeModel.put("devicename", rn.getDeviceName());
            nodeModel.put("status", rn.getState());
            nodeModel.put("devicetype", rn.getDeviceType());
            nodeModel.put("comment", rn.getComment());
            nodeModel.put("deviceexist", true);
        }
        catch (RancidApiException e){
            if (e.getRancidCode() == 2) {
            nodeModel.put("deviceexist", false);
                log().debug("No device found in router.db for:" + rancidName + "on Group: " + group);
            } else {
                nodeModel.put("RWSStatus",e.getLocalizedMessage());
                log().error(e.getLocalizedMessage());               
                return nodeModel;
            }
        }

        if (adminRole) {
            log().debug("getRancidNode: getting clogin info for: " + rancidName);        
            RancidNodeAuthentication rn5;
            try {
                rn5 = RWSClientApi.getRWSAuthNode(m_cp,rancidName);
                nodeModel.put("isadmin", "true");
                nodeModel.put("cloginuser", rn5.getUser());
                nodeModel.put("cloginpassword", rn5.getPassword());
                nodeModel.put("cloginconnmethod", rn5.getConnectionMethodString());
                nodeModel.put("cloginenablepass", rn5.getEnablePass());
                String autoen = "0";
                if (rn5.isAutoEnable()){
                    autoen = "1";
                }
                nodeModel.put("cloginautoenable", autoen);
            }catch (RancidApiException e){
                nodeModel.put("RWSStatus",e.getLocalizedMessage());
                log().error("getRancidNode: clogin get failed with reason: " + e.getLocalizedMessage());
            }
        }
        return nodeModel;
    }

    /*
     * getRancidNodeWithClogin will filter any exception, the page will show an empty table
     * in case of node not in DB or device name not in RWS 
     */
    /**
     * <p>getRancidNodeWithCLogin</p>
     *
     * @param nodeid a int.
     * @param adminRole a boolean.
     * @return a java$util$Map object.
     */
    public Map<String, Object> getRancidNodeWithCLogin(int nodeid, boolean adminRole) {
        
        log().debug("getRancidNodeWithClogin start");
        Map<String, Object> nodeModel = getRancidNodeBase(nodeid);
        String rancidName = (String)nodeModel.get("id"); 
        
        // Group list 
        RWSResourceList groups;
        try {
            groups = RWSClientApi.getRWSResourceGroupsList(m_cp);
        } catch (RancidApiException e) {
            nodeModel.put("RWSStatus",e.getLocalizedMessage());
            log().error(e.getLocalizedMessage());
            return nodeModel;
        }
            
        List<String> grouplist = groups.getResource();
        nodeModel.put("grouplist",grouplist);
        Iterator<String> iter1 = grouplist.iterator();        
      
        String groupname;
        while (iter1.hasNext()){
            groupname = iter1.next();
            nodeModel.put("groupname", groupname);
            log().debug("getRancidNodeWithClogin " + rancidName + " group " + groupname);        
            
            try {
                RancidNode rn = RWSClientApi.getRWSRancidNodeTLO(m_cp, groupname, rancidName);
                nodeModel.put("devicename", rn.getDeviceName());
                nodeModel.put("status", rn.getState());
                nodeModel.put("devicetype", rn.getDeviceType());
                nodeModel.put("comment", rn.getComment());
                nodeModel.put("deviceexist", true);
                break;
            } catch (RancidApiException e){
                if (e.getRancidCode() == 2) {
                    nodeModel.put("deviceexist", false);
                    log().debug("No device found in router.db for:" + rancidName + "on Group: " + groupname);
                } else {
                    nodeModel.put("RWSStatus",e.getLocalizedMessage());
                    log().error(e.getLocalizedMessage());               
                    return nodeModel;
                }
            }
        }
                   
        // DeviceType list 
        RWSResourceList devicetypes;
        try {
            devicetypes = RWSClientApi.getRWSResourceDeviceTypesPatternList(m_cp);
        } catch (RancidApiException e) {
            nodeModel.put("RWSStatus",e.getLocalizedMessage());
            log().error(e.getLocalizedMessage());
            return nodeModel;
        }

        List<String> devicetypelist = devicetypes.getResource();
        nodeModel.put("devicetypelist",devicetypelist);

        //CLOGIN
        if (adminRole) {
            log().debug("getRancidNode: getting clogin info for: " + rancidName);        
            RancidNodeAuthentication rn5;
            try {
                rn5 = RWSClientApi.getRWSAuthNode(m_cp,rancidName);
                nodeModel.put("isadmin", "true");
                nodeModel.put("cloginuser", rn5.getUser());
                nodeModel.put("cloginpassword", rn5.getPassword());
                nodeModel.put("cloginconnmethod", rn5.getConnectionMethodString());
                nodeModel.put("cloginenablepass", rn5.getEnablePass());
                String autoen = "0";
                if (rn5.isAutoEnable()){
                    autoen = "1";
                }
                nodeModel.put("cloginautoenable", autoen);
            }catch (RancidApiException e){
                nodeModel.put("RWSStatus",e.getLocalizedMessage());
                log().error("getRancidNode: clogin get failed with reason: " + e.getLocalizedMessage());
            }
        }
        return nodeModel;
    }

    /**
     * <p>switchStatus</p>
     *
     * @param groupName a {@link java.lang.String} object.
     * @param deviceName a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean switchStatus(String groupName, String deviceName){
        
      log().debug("InventoryService switchStatus " + groupName+"/"+deviceName);

      try {  
          RancidNode rn = RWSClientApi.getRWSRancidNodeTLO(m_cp, groupName, deviceName);
          if (rn.isStateUp()){
              log().debug("InventoryService switchStatus :down");
              rn.setStateUp(false);
          }else {
              log().debug("InventoryService switchStatus :up");
              rn.setStateUp(true);
          }
          RWSClientApi.updateRWSRancidNode(m_cp, rn);
      }
      catch (Throwable e){
          log().debug("switchStatus has given exception on node "  + groupName+"/"+deviceName + " "+ e.getMessage() );
          return false;
      }
      return true;
    }

    /**
     * <p>deleteNodeOnRouterDb</p>
     *
     * @param groupName a {@link java.lang.String} object.
     * @param deviceName a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean deleteNodeOnRouterDb(String groupName, String deviceName){
        
        log().debug("InventoryService deleteNodeOnRouterDb: " + groupName+"/"+deviceName);

        try {  
            RancidNode rn = RWSClientApi.getRWSRancidNodeTLO(m_cp, groupName, deviceName);
            RWSClientApi.deleteRWSRancidNode(m_cp, rn);
        }
        catch (Throwable e){
            log().debug("deleteNodeOnRouterDb has given exception on node "  + groupName+"/"+deviceName + " "+ e.getMessage() );
            return false;
        }
        return true;
      }

    /**
     * <p>updateNodeOnRouterDb</p>
     *
     * @param groupName a {@link java.lang.String} object.
     * @param deviceName a {@link java.lang.String} object.
     * @param deviceType a {@link java.lang.String} object.
     * @param status a {@link java.lang.String} object.
     * @param comment a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean updateNodeOnRouterDb(String groupName, String deviceName, String deviceType, String status, String comment ){
        
        log().debug("InventoryService updateNodeOnRouterDb: " + groupName+"->"+deviceName+":"+ deviceType+":"+ status + ":" + comment);

        try {  
            RancidNode rn = RWSClientApi.getRWSRancidNodeTLO(m_cp, groupName, deviceName);
            rn.setDeviceType(deviceType);
            if (comment != null) rn.setComment(comment);
            if ("up".equalsIgnoreCase(status)) {
                rn.setStateUp(true);
            } else if ("down".equalsIgnoreCase(status)) {
                rn.setStateUp(false);
            }
            RWSClientApi.updateRWSRancidNode(m_cp, rn);
        }
        catch (Throwable e){
            log().debug("updateNodeOnRouterDb has given exception on node "  + groupName+"/"+deviceName + " "+ e.getMessage() );
            return false;
        }
        return true;
    }

    /**
     * <p>createNodeOnRouterDb</p>
     *
     * @param groupName a {@link java.lang.String} object.
     * @param deviceName a {@link java.lang.String} object.
     * @param deviceType a {@link java.lang.String} object.
     * @param status a {@link java.lang.String} object.
     * @param comment a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean createNodeOnRouterDb(String groupName, String deviceName, String deviceType, String status,String comment ){
        
        log().debug("InventoryService createNodeOnRouterDb: " + groupName+"->"+deviceName+":"+ deviceType+":"+ status + ":" + comment);

        try {  
            RancidNode rn = new RancidNode(groupName,deviceName);
            rn.setDeviceType(deviceType);
            if (comment != null) rn.setComment(comment);
            if ("up".equalsIgnoreCase(status)) {
                rn.setStateUp(true);
            } else if ("down".equalsIgnoreCase(status)) {
                rn.setStateUp(false);
            }
            RWSClientApi.createRWSRancidNode(m_cp, rn);
        }
        catch (Throwable e){
            log().debug("createNodeOnRouterDb has given exception on node "  + groupName+"/"+deviceName + " "+ e.getMessage() );
            return false;
        }
        return true;
   }

    /**
     * <p>updateClogin</p>
     *
     * @param deviceName a {@link java.lang.String} object.
     * @param groupName a {@link java.lang.String} object.
     * @param userID a {@link java.lang.String} object.
     * @param pass a {@link java.lang.String} object.
     * @param enPass a {@link java.lang.String} object.
     * @param loginM a {@link java.lang.String} object.
     * @param autoE a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean updateClogin(String deviceName, String groupName, String userID, String pass, String enPass, String loginM, String autoE){
        log().debug("InventoryService updateClogin for following changes"+
                    "userID ["+ userID +"] "+
                    "pass [" + pass +"] "+
                    "enpass [" + enPass+"] "+
                    "loginM [" + loginM+"] "+
                    "autoE [" + autoE+"] "+
                    "groupName (ignored) [" + groupName+"] "+
                    "deviceName [" + deviceName + "] "); 
        try {
          RancidNodeAuthentication rna = RWSClientApi.getRWSAuthNode(m_cp, deviceName);
          rna.setUser(userID);
          rna.setPassword(pass);
          rna.setConnectionMethod(loginM);
          rna.setEnablePass(enPass);
          boolean autoeb = false;
          if (autoE.compareTo("1")==0) {
              autoeb = true;
          }
          rna.setAutoEnable(autoeb);
          RWSClientApi.createOrUpdateRWSAuthNode(m_cp,rna);
          log().debug("InventoryService ModelAndView updateClogin changes submitted");
        }
        catch (Throwable e){
            log().debug("updateClogin has given exception on node "  + deviceName + " "+ e.getMessage() );
            return false;
        }
        return true;


    }

    /**
     * <p>deleteClogin</p>
     *
     * @param deviceName a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean deleteClogin(String deviceName){
        log().debug("InventoryService deleteClogin deviceName [" + deviceName + "] "); 
        try {
          RancidNodeAuthentication rna = RWSClientApi.getRWSAuthNode(m_cp, deviceName);
          RWSClientApi.deleteRWSAuthNode(m_cp,rna);
          log().debug("InventoryService ModelAndView updateClogin changes submitted");
        }
        catch (Throwable e){
            log().debug("updateClogin has given exception on node "  + deviceName + " "+ e.getMessage() );
            return false;
        }
        return true;


    }

    /**
     * <p>deleteBucketItem</p>
     *
     * @param bucket a {@link java.lang.String} object.
     * @param filename a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean deleteBucketItem(String bucket, String filename ){
        log().debug("InventoryService deleteBucketItem for bucket/filename [" + bucket + "]/ " + "[" + filename + "]"); 
        try {
          RWSClientApi.deleteBucketItem(m_cp, bucket, filename);
          log().debug("InventoryService ModelAndView deleteBucketItem changes submitted");
        }
        catch (Throwable e){
            log().debug("deleteBucketItem has given exception on node "  + bucket + " "+ e.getMessage() );
            return false;
        }
        return true;
    }

    /**
     * <p>deleteBucket</p>
     *
     * @param bucket a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean deleteBucket(String bucket){
        log().debug("InventoryService deleteBucket for bucket [" + bucket + "]/ "); 
        try {
          RWSClientApi.deleteBucket(m_cp, bucket);
          log().debug("InventoryService ModelAndView deleteBucket changes submitted");
        }
        catch (Throwable e){
            log().debug("deleteBucket has given exception on node "  + bucket + " "+ e.getMessage() );
            return false;
        }
        return true;
    }

    /**
     * <p>createBucket</p>
     *
     * @param bucket a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean createBucket(String bucket){
        log().debug("InventoryService createBucket for bucket [" + bucket + "]/ "); 
        try {
          RWSClientApi.createBucket(m_cp, bucket);
          log().debug("InventoryService ModelAndView createBucket changes submitted");
        }
        catch (Throwable e){
            log().debug("createBucket has given exception on node "  + bucket + " "+ e.getMessage() );
            return false;
        }
        return true;
    }

    private static Logger log() {
        return Logger.getLogger("Rancid");
    }


}
