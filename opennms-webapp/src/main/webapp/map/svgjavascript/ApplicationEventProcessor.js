function onMouseOverMapElement(evt) {
	myMapApp.enableTooltips();
	var id = evt.target.parentNode.getAttributeNS(null,"id");
	var mapElement = map.mapElements[id];
	var toolTipLabel = "";

	if (mapElement.isNode()) {
		toolTipLabel=nodeidSortAss[mapElement.getNodeId()].getLabel();
	} else {
		toolTipLabel="Map: "+mapidSortAss[mapElement.getMapId()];
	}

	myMapApp.addTooltip(id,toolTipLabel,false,false,"currentTarget",undefined);
}

function onMouseOverLink(evt) {
	myMapApp.enableTooltips();
	var id = evt.target.parentNode.getAttributeNS(null,"id");

	
	var link = map.mapLinks[id]
		var toolTipLabel = "";

    var statusMap = link.getStatusMap();
    for (var statusString in statusMap) {
    	toolTipLabel = toolTipLabel+" "+ statusString + "("+statusMap[statusString]+")";
	}
	toolTipLabel = toolTipLabel+" total("+link.getNumberOfLinks()+")";
	
	myMapApp.addTooltip(id,toolTipLabel,false,false,"currentTarget",undefined);
}

function onMouseOutMapElement(evt) {
	myMapApp.disableTooltips();
}

function onMouseOutLink(evt) {
	myMapApp.disableTooltips();
}

function onMouseDownOnSLink(evt) {
	resetSelectedObjects();
	if ((typeof map) == "object")
	{
		// close other menus
		windowsClean();

		clearDownInfo();
		clearActionsStarted();
		var id = evt.target.parentNode.getAttributeNS(null,"id");
		var slink = map.mapLinks[id];
		writeTopInfoText(getInfoOnSLink(slink));
		
		if (evt.detail == 2)
		{
			var x=evt.clientX + 2;
			var y=evt.clientY + 4;
			var height = cmheight+cmdelta * (slink.getNumberOfMultiLinks()+1);
			var cm =  new ContextMenuSimulator(winSvgElement,slink.id,"Summary Link",x,y,cmwidth,cmsubwidth,height,mapWidth-x,mapHeight-y,cmmenuStyle,cmmenuElementStyle,cmmenuElementTextStyle,cmmenuBarStyle,cmmenuElementMouseStyle,cmdelta);
			cm.addItem(slink.id,LINK_TEXT[slink.getTypology()],execLinkCMAction,true);
			for ( var linkid in slink.getLinks() ) {
			    var link = slink.getLinks()[linkid];
				cm.addItem(linkid,LINK_TEXT[link.getTypology()],execLinkCMAction,false);	
			}
			var addcmbar=true;			
			for(var index in CM_COMMANDS) {
				if(CM_COMMANDS[index]=="-") {
					addcmbar=true;
				} else {
					var commandLabel = unescape(CM_COMMANDS[index]);
					var nodeids = slink.getNodeIds();
					cm.addSubMenu(index,commandLabel,execSelectedCMActionFromSubMenu,addcmbar,nodeids.length);
					for (var k in nodeids ) {
					     var nodeid = nodeids[k];
						cm.addSubMenuItem(index,nodeid,nodeidSortAss[nodeid].getLabel(),false);
					}
					addcmbar=false;
				}
			}
		}		
	}
}

//if double-click on an element (map) open the map 
function onClickMapElement(evt)
{
	if ((typeof map) == "object")
	{	
		map.startSelectionRectangle = null;
		map.endSelectionRectangle = null;
						
	}
	
	//select the element
	resetSelectedObjects();
	map.selectedObjects=new Array();
	map.selectedObjects.push( evt.target.parentNode );
	evt.target.parentNode.setAttributeNS(null,"opacity", "0.5");
	
	var id = evt.target.parentNode.getAttributeNS(null,"id");
	var mapElement = map.mapElements[id];
	// close other menus
	windowsClean();
	
	// view info node
	clearDownInfo();			
	writeTopInfoText(getInfoOnMapElement(mapElement));

	if (evt.detail == 2)
	{
		myMapApp.disableTooltips()
		var label = mapElement.getLabel();
		var x = mapElement.getX() + mapElemDimension;
		var y = mapElement.getY() ;
		if(mapElement.isNode())
		{
			var nodeid = mapElement.getNodeId();
			
			var cm =  new ContextMenuSimulator(winSvgElement,nodeid,label,x,y,cmwidth,cmsubwidth,cmheight,mapWidth-x,mapHeight-y,cmmenuStyle,cmmenuElementStyle,cmmenuElementTextStyle,cmmenuBarStyle,cmmenuElementMouseStyle,cmdelta);
			var addcmbar=true;
			for(var index in CM_COMMANDS){
				if(CM_COMMANDS[index]=="-"){
					addcmbar=true;
				}else{
					var commandLabel = unescape(CM_COMMANDS[index]);
					cm.addItem(index,commandLabel,execSelectedCMAction,addcmbar);
					addcmbar=false;
				}
			}
		}
	
		if(mapElement.isMap())
		{
			var mapid=mapElement.getMapId();
			var mapname=mapidSortAss[mapid];
			var topmaplist = new Array();
			for (var elemlabel in nodeLabelMap) {
				if (mapname == elemlabel ) {
				    var mapLbl = nodeLabelMap[elemlabel];
				    if (mapLbl!=undefined) {
						for (var j=0; j<mapLbl.length;j++ ){
							var mapnamecursor = mapLbl[j];
							if (mapnamecursor != currentMapName)
								topmaplist.push(mapnamecursor);
						}
					}
					break;
				}  
			}
			if (topmaplist.length > 0 ) {
				var cm =  new ContextMenuSimulator(winSvgElement,mapid,label,x,y,cmwidth,cmsubwidth,3*cmdelta,mapWidth-x,mapHeight-y,cmmenuStyle,cmmenuElementStyle,cmmenuElementTextStyle,cmmenuBarStyle,cmmenuElementMouseStyle,cmdelta);
				cm.addItem(mapid,"Open",execOpenMapCMAction,true);
				cm.addSubMenu("Top"+mapid,"Up",execOpenMapCMActionFromSubMenu,false,topmaplist.length);
				for ( var alfa in topmaplist ) {
					var mapnamecursor=topmaplist[alfa];
					cm.addSubMenuItem("Top"+mapid,mapSortAss[mapnamecursor].id,mapnamecursor,false);					
				}
			} else {
				openMapSetUp(mapid,true);
			}
		}
			
	}
}

function execOpenMapCMAction(mapid) {
	openMapSetUp(mapid,true);
}

function execOpenMapCMActionFromSubMenu(index,id,menulabel,mapid) {
	openMapSetUp(mapid,true);
}
function onMouseDownOnMapElement(evt)
{	
	if ((typeof map) == "object")
	{ 
  		var mapElement = map.mapElements[evt.target.parentNode.getAttributeNS(null,"id")];		

		var matrix;
		// track the origin
		map.draggableObject = null;
		if(map.selectedObjects==null){	
			//add the element to the selection
			map.selectedObjects=new Array();
			map.selectedObjects.push( evt.target.parentNode );
			evt.target.parentNode.setAttributeNS(null,"opacity", "0.5");
		}else{
			var found=false;
			for(selObj in  map.selectedObjects){
				if(map.selectedObjects[selObj] == evt.target.parentNode){
					found=true;
					break;
				}
			}
			if(!found){
				resetSelectedObjects();
				resetDraggableObject();
				map.selectedObjects=new Array();
				map.selectedObjects.push( evt.target.parentNode );					
			}
						
		}
		
		if(map.selectedObjects.length==1){
			// view info node
			clearDownInfo();			
			writeTopInfoText(getInfoOnMapElement(mapElement));
		}
		map.draggableObject =  evt.target.parentNode;
		// get the relative position
		var matrix = evt.target.parentNode.getCTM();
  		var mouse = getMouse(evt);

  		map.offset.x = (matrix.e - mouse.x); //document.getFirstChild().currentScale;
  		map.offset.y = (matrix.f - mouse.y); //document.getFirstChild().currentScale;
		
	
		
		//delete the element if flag 'deletingMapElem' is true
		if(deletingMapElem==true){
			deleteMapElement(mapElement);
			deleteMapElementSetUp();
			myMapApp.disableTooltips();
		}
		
		//add the element neighbors if flag 'addingMapElemNeighbors' is true
		if(addingMapElemNeighbors==true){
			var elemMap = mapElement;
			if(elemMap.isMap()){
				writeDownInfo("Cannot add neighbors to a map");
			} else {
				addMapElemNeighSetUp(elemMap.getNodeId());
			}
		}

		//set the icon selected into the relative selection list to the selected element
		if(settingMapElemIcon==true){
			mapElement.setIcon(new Icon(selectedMEIconInList,MEIconsSortAss[selectedMEIconInList]));
			map.render();
			setIconSetUp();
		}
	}
}


function removeSelectionRect(){
		var selectionRectangle = document.getElementById("selectionRect");
		if(selectionRectangle!=undefined)
			selectionRectangle.parentNode.removeChild(selectionRectangle);
}


function createSelectionRect(evt, x, y, width, height){
	//security check
	
	if(map.startSelectionRectangle!=null){
		var selectionRectangle = document.getElementById("selectionRect");
		if(selectionRectangle!=undefined)
			selectionRectangle.parentNode.removeChild(selectionRectangle);
		selectionRectangle = document.createElementNS("http://www.w3.org/2000/svg","rect");
		selectionRectangle.setAttributeNS(null, "onmouseup", "onMouseUp(evt);" );
		selectionRectangle.setAttributeNS(null, "onmousemove", "onMouseMove(evt);" );
		selectionRectangle.setAttributeNS(null,"x", x);
		selectionRectangle.setAttributeNS(null,"y", y);
		selectionRectangle.setAttributeNS(null,"width", width);
		selectionRectangle.setAttributeNS(null,"height", height);
		selectionRectangle.setAttributeNS(null,"id", "selectionRect");
		selectionRectangle.setAttributeNS(null,"style","fill:white; fill-opacity:0; stroke:black;stroke-width:1;stroke-opacity:0.5;stroke-dasharray:1");		
		map.getSvgNode().appendChild(selectionRectangle);	
	}else{
		removeSelectionRect();
	}
	
}

function onMouseDownOnMap(evt)
{

	if ((typeof map) == "object" && currentMapId != MAP_NOT_OPENED){
		map.startSelectionRectangle = getMouse(evt);
		
	}
	windowsClean();
	
	// remove node information
	clearTopInfo();
	clearDownInfo();
	
	//close the menu
	closeAllMenu();
	
	//clear the actions started
	clearActionsStarted();	

	resetSelectedObjects();
	resetDraggableObject();
	

}

function resetSelectedObjects(){
	
	if ((typeof map) == "object")
	{	
		if(map.selectedObjects != null){
			for(selObj in map.selectedObjects)
				map.selectedObjects[selObj].setAttributeNS(null,"opacity", "0.9");		
		}

		map.selectedObjects = null;
	}
}


function resetDraggableObject(){
	
	if ((typeof map) == "object")
	{
		map.draggableObject = null;
	}
}		
		
function onMouseDownOnLink(evt) {
	resetSelectedObjects();
	if ((typeof map) == "object") {
		// close other menus
		windowsClean();

		clearDownInfo();
		clearActionsStarted();
		var id = evt.target.parentNode.getAttributeNS(null,"id");
		var mapLink = map.mapLinks[id];
		writeTopInfoText(getInfoOnLink(mapLink));
		
		if (evt.detail == 2) {			
			var label = new String();

			label += mapLink.getMapElement1().getLabel();
			label+=" - ";
			label+= mapLink.getMapElement2().getLabel();

			var x=evt.clientX + 2;
			var y=evt.clientY + 4;
			var cm=new ContextMenuSimulator(winSvgElement,id,label,x,y,cmwidth,cmsubwidth,cmheight,mapWidth-x,mapHeight-y,cmmenuStyle,cmmenuElementStyle,cmmenuElementTextStyle,cmmenuBarStyle,cmmenuElementMouseStyle,cmdelta);

			var addcmbar=true;			
			for(var index in CM_COMMANDS) {
				if(CM_COMMANDS[index]=="-") {
					addcmbar=true;
				} else {
					var commandLabel = unescape(CM_COMMANDS[index]);
					var nodeids = mapLink.getNodeIds();
					cm.addSubMenu(index,commandLabel,execSelectedCMActionFromSubMenu,addcmbar,nodeids.length);
					for (var k in nodeids ) {
					     var nodeid = nodeids[k];
						cm.addSubMenuItem(index,nodeid,nodeidSortAss[nodeid].getLabel(),false);
					}
					addcmbar=false;
				}
			}
		}
	}		
}

function onMouseMove(evt)
{
	var mapsvgRoot = document.documentElement;
	var pan = mapsvgRoot.currentTranslate;
	var zoom = mapsvgRoot.currentScale;
	var mouse = getMouse(evt);
	if((typeof map) == "object" && map.startSelectionRectangle!=null ){
		var minX = map.startSelectionRectangle.x;
		var maxX = mouse.x;
		if(map.startSelectionRectangle.x > mouse.x){
			minX = mouse.x;
			maxX = map.startSelectionRectangle.x;
		}
		var minY = map.startSelectionRectangle.y;
		var maxY = mouse.y;
		if(map.startSelectionRectangle.y > mouse.y){
			minY = mouse.y;
			maxY = map.startSelectionRectangle.y;
		}		
		minX = (minX- pan.x)/zoom;
		maxX = (maxX- pan.x)/zoom;
		minY = (minY- pan.y)/zoom;
		maxY = (maxY- pan.y)/zoom;
		
		
		createSelectionRect(evt, minX, minY, maxX-minX, maxY-minY)
		

	}
	
	if ((typeof map) == "object" && map.selectedObjects != null && map.draggableObject!=null)
	{
	var mapElements = new Array();
	if(map.selectedObjects != null){
		for(selObj in map.selectedObjects){
			map.selectedObjects[selObj].setAttributeNS(null,"opacity", "0.5");
			mapElements.push( map.mapElements[map.selectedObjects[selObj].getAttributeNS(null,"id")] );
		}
	}
	var transScaledPoint = mapsvgRoot.createSVGPoint();
	//alert(evt.target.parentNode+" "+evt.target.parentNode.getAttributeNS(null,"id"));
	

	var movingElement = map.mapElements[map.draggableObject.getAttributeNS(null,"id")];
	
	var dX = mouse.x - movingElement.x*zoom;	
	var dY = mouse.y - movingElement.y*zoom;
		
  	if(mapElements.length!=0)
  		for(drObj in mapElements){
  			
			transScaledPoint.x = parseInt(((mapElements[drObj].x*zoom+dX+map.offset.x) - pan.x) / zoom);
			transScaledPoint.y = parseInt(((mapElements[drObj].y*zoom+dY+map.offset.y) - pan.y) / zoom);
			
			if(transScaledPoint.x<=0){
				transScaledPoint.x=0;
			}
			if(transScaledPoint.x>=(map.getWidth()-(mapElemDimension))){
				transScaledPoint.x=map.getWidth()-(mapElemDimension);
			}			
			if(transScaledPoint.y<=0){
				transScaledPoint.y=0;
			}	
			if(transScaledPoint.y>=(map.getHeight()-(mapElemDimension))){
				transScaledPoint.y=map.getHeight()-(mapElemDimension);
			}			
			
			mapElements[drObj].move(transScaledPoint.x, transScaledPoint.y);
			map.redrawLinkOnElement(mapElements[drObj].id);
		}
	}

}

function onMouseUp(evt)
{
	//alert("mouse up!");
	var mapsvgRoot = document.documentElement;
	var zoom = mapsvgRoot.currentScale;
	var pan = mapsvgRoot.currentTranslate;
	removeSelectionRect();
	//reset the selection rectangle

	
	if ((typeof map) == "object")
	{
		//resetSelectedObjects();
		resetDraggableObject();
		
		//
		if(map.startSelectionRectangle!=null){
			map.endSelectionRectangle = getMouse(evt);
			
			var minX = (map.startSelectionRectangle.x < map.endSelectionRectangle.x)?map.startSelectionRectangle.x:map.endSelectionRectangle.x;
			var maxX = (map.startSelectionRectangle.x > map.endSelectionRectangle.x)?map.startSelectionRectangle.x:map.endSelectionRectangle.x;
			var minY = (map.startSelectionRectangle.y < map.endSelectionRectangle.y)?map.startSelectionRectangle.y:map.endSelectionRectangle.y;
			var maxY = (map.startSelectionRectangle.y > map.endSelectionRectangle.y)?map.startSelectionRectangle.y:map.endSelectionRectangle.y;
			
			map.selectedObjects = new Array();
			
			var count=0;
			for(mEl in map.mapElements){
				
				if(( ((map.mapElements[mEl].x+mapElemDimension/2)*zoom) +pan.x )>=minX && ( ((map.mapElements[mEl].x+mapElemDimension/2)*zoom) +pan.x)<=maxX && ( ((map.mapElements[mEl].y+mapElemDimension/2)*zoom) +pan.y)>=minY && ( ((map.mapElements[mEl].y+mapElemDimension/2)*zoom) +pan.y)<=maxY){
					map.mapElements[mEl].getSvgNode().setAttributeNS(null,"opacity", "0.5");
					map.selectedObjects.push(map.mapElements[mEl].getSvgNode());
					count++;
				}
			}
			if(count>0)
			writeDownInfo(count+" element/s selected.")
			map.startSelectionRectangle=null;
			map.endSelectionRectangle=null;	
		}			
		
	}
}

function ciao() {
	return;
}

function execSelectedCMActionFromSubMenu(index,id,menulabel,nodeid) {
	if(CM_COMMANDS[index]){
		var link = CM_LINKS[index];
		var params = CM_PARAMS[index];				
		link = link.replace("ELEMENT_ID",""+nodeid);
		link = link.replace("ELEMENT_LABEL",nodeidSortAss[nodeid].getLabel());
		link = link.replace("ELEMENT_HOSTNAME",nodeidSortAss[nodeid].getLabel());
		link = link.replace("ELEMENT_IP",nodeidSortAss[nodeid].getIpAddr());
		openLink(escape(link),params);
	} else {
		alert("Windows Menu Command Error");
	}
}


function execSelectedCMAction(index,nodeid,nodelabel) {
	if(CM_COMMANDS[index]){
		var link = CM_LINKS[index];
		var params = CM_PARAMS[index];				
		link = link.replace("ELEMENT_ID",""+nodeid);
		link = link.replace("ELEMENT_LABEL",nodelabel);
		link = link.replace("ELEMENT_HOSTNAME",nodeidSortAss[nodeid].getLabel());
		link = link.replace("ELEMENT_IP",nodeidSortAss[nodeid].getIpAddr());
		openLink(escape(link),params);
	} else {
		alert("Windows Menu Command Error");
	}
}

function execLinkCMAction(linkid,sid,label) {
	var sLink=map.mapLinks[sid];
	sLink.switchLink(linkid);
}

function onClickTab(evt) {
	var tab = evt.target;
	var id = tab.getAttributeNS(null,"id");
	var idArray = id.split("__");
	var index = parseInt(idArray[1]);
	mapTabGroup.activateTabByIndex(index);
	var tabTitle = mapTabGroup.getTabTitleByIndex(index);
	if ( tabTitle == MAP_NOT_OPENED_NAME ) return;
	if ( tabTitle == NEW_MAP_NAME ) {
		newMapSetUp();
	} else {
		openMapSetUp(mapSortAss[tabTitle].id,false);
	}
}

function onCloseTab(evt) {
	var tab = evt.target;
	var id = tab.getAttributeNS(null,"id");
	var idArray = id.split("__");
	var index = parseInt(idArray[1]);
	var tabTitle = mapTabGroup.getTabTitleByIndex(index);
	if ( tabTitle == MAP_NOT_OPENED_NAME ) return;
	if (mapTabGroup.getActiveTabIndex() == index ) {
		closeSetUp();
	} else {
		mapTabClose(tabTitle);
	}
}

function onClosingActiveTab() {
	var tabTitle = mapTabGroup.getActiveTabTitle();
	if ( tabTitle == MAP_NOT_OPENED_NAME ) return;
	if ( tabTitle == NEW_MAP_NAME ) {
		newMapSetUp();
	} else {
		openMapSetUp(mapSortAss[tabTitle].id,false);
	}
}