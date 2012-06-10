package org.opennms.features.gwt.snmpselect.list.client.rest;

import java.util.ArrayList;
import java.util.List;

import org.opennms.features.gwt.snmpselect.list.client.view.SnmpCellListItem;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class DefaultSnmpInterfaceRestService implements SnmpInterfaceRestService{
    
    private static String DEFAULT_RESPONSE = "{" +
    "\"@totalCount\" : \"2\"," +
    "\"@count\" : \"2\"," +
    "\"snmpInterface\" : [ {" +
      "\"@poll\" : \"false\"," +
      "\"@pollFlag\" : \"N\"," +
      "\"@ifIndex\" : \"2\"," +
      "\"@id\" : \"139\"," +
      "\"@collect\" : \"true\"," +
      "\"@collectFlag\" : \"C\"," +
      "\"ifAdminStatus\" : \"1\"," +
      "\"ifAlias\" : \"\"," +
      "\"ifDescr\" : \"eth0\"," +
      "\"ifName\" : \"eth0\"," +
      "\"ifOperStatus\" : \"1\"," +
      "\"ifSpeed\" : \"10000000\"," +
      "\"ifType\" : \"6\"," +
      "\"ipInterfaces\" : \"138\"," +
      "\"netMask\" : \"255.255.255.0\"," +
      "\"nodeId\" : \"10\"," +
      "\"physAddr\" : \"00163e13f215\"" +
    "}, {" +
      "\"@poll\" : \"false\"," +
      "\"@pollFlag\" : \"N\"," +
      "\"@ifIndex\" : \"3\"," +
      "\"@id\" : \"140\"," +
      "\"@collect\" : \"true\"," +
      "\"@collectFlag\" : \"UC\"," +
      "\"ifAdminStatus\" : \"2\"," +
      "\"ifAlias\" : \"\"," +
      "\"ifDescr\" : \"sit0\"," +
      "\"ifName\" : \"sit0\"," +
      "\"ifOperStatus\" : \"2\"," +
      "\"ifSpeed\" : \"0\"," +
      "\"ifType\" : \"131\"," +
      "\"nodeId\" : \"10\"" +
    "} ]" +
"}";
    
    private SnmpInterfaceRequestHandler m_requestHandler;
    private int m_nodeId;
    
    public DefaultSnmpInterfaceRestService(int nodeId) {
        m_nodeId = nodeId;
    }
    
    @Override
    public void getInterfaceList() {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode("rest/nodes/" + m_nodeId + "/snmpinterfaces?limit=0"));
        builder.setHeader("accept", "application/json");
        
        try {
            builder.sendRequest(null, new RequestCallback() {

                @Override
                public void onResponseReceived(Request request, Response response) {
                    if(response.getStatusCode() == 200) {
                        m_requestHandler.onResponse(parseJSONData(response.getText()));
                    }else {
                        m_requestHandler.onError("An Error Occurred retreiving the SNMP Interfaces for this node.\n" +
                        		"Status Code: " + response.getStatusCode());
                        
                        m_requestHandler.onResponse(parseJSONData(DEFAULT_RESPONSE));
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    m_requestHandler.onError(exception.getMessage());
                    
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
        
    }

    protected List<SnmpCellListItem> parseJSONData(String jsonString) {
        List<SnmpCellListItem> cellList = new ArrayList<SnmpCellListItem>();
        JSONObject jsonObject = JSONParser.parseStrict(jsonString).isObject();
        
        if(jsonObject.containsKey("snmpInterface") && jsonObject.get("snmpInterface").isArray() != null) {
            JsArray<SnmpCellListItem> jsArray = createJsArray(jsonObject.get("snmpInterface").isArray().getJavaScriptObject());
            for(int i = 0; i < jsArray.length(); i++) {
                cellList.add(jsArray.get(i));
            }
        }
        
        return cellList;
    }

    private static native JsArray<SnmpCellListItem> createJsArray(JavaScriptObject jso) /*-{
        return jso;
    }-*/;

    @Override
    public void updateCollection( int ifIndex, String collectFlag ) {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.PUT, URL.encode("rest/nodes/" + m_nodeId + "/snmpinterfaces/" + ifIndex));
        builder.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        try {
            builder.sendRequest("collect=" + collectFlag, new RequestCallback() {

                @Override
                public void onResponseReceived(Request request, Response response) {
                    
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    m_requestHandler.onError("There was an error when saving the interface collection value");
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSnmpInterfaceRequestHandler(SnmpInterfaceRequestHandler handler) {
        m_requestHandler = handler;
    }
    
}