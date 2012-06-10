package org.opennms.features.gwt.snmpselect.list.client;


import org.opennms.features.gwt.snmpselect.list.client.rest.DefaultSnmpInterfaceRestService;
import org.opennms.features.gwt.snmpselect.list.client.view.SnmpCellListItem;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.RootPanel;

public class SnmpSelectListEntry implements EntryPoint {

    @Override
    public void onModuleLoad() {
        
        if(Navigator.getUserAgent().contains("MSIE")) {
            NodeList<Element> divs = RootPanel.getBodyElement().getElementsByTagName("div");
            for(int j = 0; j < divs.getLength(); j++) {
                Element element = divs.getItem(j);
                if(element.hasAttribute("name") && element.getAttribute("name").equals("opennms-snmpSelectList")) {
                    createView(element);
                }
            }
        }else {
        
            NodeList<Element> nodes = RootPanel.getBodyElement().getElementsByTagName("opennms:snmpSelectList");
            if(nodes.getLength() > 0) {
                for(int i = 0; i < nodes.getLength(); i++) {
                    Element elem = nodes.getItem(i);
                    createView(elem);
                }
                
            }
        }
    }

    private void createView(Element elem) {
        AppController appController = new AppController(new DefaultSnmpInterfaceRestService(getNodeId()));
        appController.go(RootPanel.get(elem.getId()));
    }
    
    private int getNodeId() {
        if(Location.getParameter("node") != null) {
            return Integer.valueOf(Location.getParameter("node"));
        }else {
            return -1;
        }
    }

    public native static JsArray<SnmpCellListItem> getTestDataList()/*-{
        return $wnd.testData.snmpInterface;
    }-*/;

}
