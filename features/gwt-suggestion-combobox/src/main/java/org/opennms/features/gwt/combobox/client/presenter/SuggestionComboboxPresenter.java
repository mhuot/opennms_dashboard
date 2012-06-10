package org.opennms.features.gwt.combobox.client.presenter;

import org.opennms.features.gwt.combobox.client.rest.NodeRestResponseMapper;
import org.opennms.features.gwt.combobox.client.rest.NodeService;
import org.opennms.features.gwt.combobox.client.view.NodeDetail;
import org.opennms.features.gwt.combobox.client.view.SuggestionComboboxView;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.HasWidgets;

public class SuggestionComboboxPresenter implements Presenter, SuggestionComboboxView.Presenter<NodeDetail>{
    
    private final SimpleEventBus m_eventBus;
    private final SuggestionComboboxView<NodeDetail> m_view;
    private final NodeService m_nodeService;
    
    public SuggestionComboboxPresenter(SimpleEventBus eventBus, SuggestionComboboxView<NodeDetail> view, NodeService nodeService) {
        m_eventBus = eventBus;
        m_view = view;
        m_view.setPresenter(this);
        
        m_nodeService = nodeService;
    }
    
    @Override
    public void go(final HasWidgets container) {
        container.clear();
        container.add(m_view.asWidget());
    }

    public SimpleEventBus getEventBus() {
        return m_eventBus;
    }

    public SuggestionComboboxView<NodeDetail> getDisplay() {
        return m_view;
    }

    @Override
    public void onGoButtonClicked() {
        m_nodeService.getNodeByNodeLabel(m_view.getSelectedText(), new RequestCallback() {
            
            @Override
            public void onResponseReceived(Request request, Response response) {
                if(response.getStatusCode() == 200) {
                    m_view.setData(NodeRestResponseMapper.mapNodeJSONtoNodeDetail(response.getText()));
                }else {
                    //m_view.setData(NodeRestResponseMapper.mapNodeJSONtoNodeDetail(DefaultNodeService.TEST_RESPONSE));
                    Window.alert("Error Occurred Retreiving Nodes");
                }
            }
            
            @Override
            public void onError(Request request, Throwable exception) {
                Window.alert("error in retrieving the Rest call");
            }
        });
    }

    @Override
    public void onEnterKeyEvent() {
        m_nodeService.getNodeByNodeLabel(m_view.getSelectedText(), new RequestCallback() {

            @Override
            public void onResponseReceived(Request request, Response response) {
                if(response.getStatusCode() == 200) {
                    m_view.setData(NodeRestResponseMapper.mapNodeJSONtoNodeDetail(response.getText()));
                    Window.alert("Error Occurred Retreiving Nodes");
                }else {
                    // m_view.setData(NodeRestResponseMapper.mapNodeJSONtoNodeDetail(DefaultNodeService.TEST_RESPONSE));
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                Window.alert("error in retrieving the Rest call");
                
            }
        });
    }

    public NodeService getNodeService() {
        return m_nodeService;
    }

    @Override
    public void onNodeSelected() {
        StringBuilder builder = new StringBuilder();
        builder.append(getBaseHref() + "graph/chooseresource.htm");
        builder.append("?reports=all");
        builder.append("&parentResourceType=node");
        builder.append("&parentResource=" + m_view.getSelectedNode().getId());

        Location.assign(builder.toString());
    }
    
    public native final String getBaseHref()/*-{
        try{
            return $wnd.getBaseHref();
        }catch(err){
            return "";
        }
    }-*/;

}
