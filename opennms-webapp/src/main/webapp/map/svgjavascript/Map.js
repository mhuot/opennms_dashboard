// you must create an instance of a map object in onLoad event of svg node root
// Map map = new Map("#bbbbbb", "Background", 600, 400, 0, 0, 3);

Map.prototype = new SVGElement;
Map.superclass = SVGElement.prototype;

// color  == background color of the map
// image  == background image of the map
// id     == map Id
// width  == map width in pixel
// height == map height in pixel
// x      == x coord of map's "centre" 
// y      == y coord of map's "centre"
// maxLinks == the max number of links between 2 mapElement
// sLinkId == the id of the summary Link 
// mapElements  == array of MapElement Objects
// mapLinks   == array of Link Object
// linksBetweenElements == array containing the number of links existing between 2 element the key of the array 
//                         is like 'idElem1-idElem2' and the value is an integer representing the number of 
//                         links between elem1 and elem2

function Map(defaultcolor, id, width, height, x, y, maxlinks, sLink,stroke,strokewidth,strokedasharray,ignoreLinkStatus,ignoreStroke)
{
 	if ( arguments.length == 13 )
		this.init(defaultcolor, id, width, height, x, y, maxlinks,sLink,stroke,strokewidth,strokedasharray,ignoreLinkStatus,ignoreStroke);
	else
		alert("Map constructor call error");
}

Map.prototype.init = function(color, id, width, height, x, y, maxlinks, sLink,stroke,strokewidth,strokedasharray,ignoreLinkStatus,ignoreStroke)
{
	//following attributes are the starting and the ending SVGPoints of the rectangle 
	//for the selection of the nodes on the map.
	this.backgroundiscolor = true;
	this.defaultbackgroundcolor = color;
	this.bg = color;
	this.id = id;
	this.width = width;
	this.height = height;
	this.x = x;
	this.y = y;
	this.maxlinks = maxlinks;
	
	this.sLinkId=sLink;
	this.sLinkStroke=stroke;
	this.sLinkStrokeDashArray=strokedasharray;
	this.sLinkStrokeWidth=strokewidth;

	this.ignoreLinkStatus = ignoreLinkStatus;
	this.ignoreStroke=ignoreStroke;
	
	this.startSelectionRectangle = null;
	this.endSelectionRectangle = null;
	this.draggableObject = null;
	this.selectedObjects = null;
    this.offset = document.documentElement.createSVGPoint();	
	this.linksBetweenElements = new Array();
	this.mapElements = new Array();
	this.mapLinks = new Array();
	this.lastlinks = new Array();
	
	this.svgNode = document.createElementNS(svgNS,"g");
	
	this.rect = document.createElementNS(svgNS,"rect"); 
	this.rect.setAttributeNS(null,"width", width);
	this.rect.setAttributeNS(null,"height", height);
	this.rect.setAttributeNS(null,"x", x);
	this.rect.setAttributeNS(null,"y", y);
	this.rect.setAttributeNS(null,"fill", color);
	this.rect.setAttributeNS(null,"stroke-width","1");
	
	this.image = document.createElementNS(svgNS,"image");
	this.image.setAttributeNS(null,"width", width);
	this.image.setAttributeNS(null,"height", height);
	this.image.setAttributeNS(null,"x", x);
	this.image.setAttributeNS(null,"y", y);
	this.image.setAttributeNS(null,'display', 'none');	

	this.background = document.createElementNS(svgNS,"g");
	this.background.setAttributeNS(null,"id", id);
	this.background.appendChild(this.rect);
	this.background.appendChild(this.image);

	this.background.addEventListener("mousemove", this.onMouseMove, false);
	this.background.addEventListener("mouseup", this.onMouseUp, false);
	this.background.addEventListener("mousedown", this.onMouseDownOnMap, false);
	
	this.svgNode.appendChild(this.background);

}

Map.prototype.setBGvalue = function(bg) {
	var imagetype= bg.split(".")[1];
	if(imagetype != null) {
		this.bg = bg;
	} else {
		if (bg.length == 6) {
			this.bg = "#"+bg;
		} else if (bg.length == 7 && bg.substring(0,1) == '#') {
			this.bg = bg;
		} else {
			alert('SetBGValue: background color wrong:'+bg);
		}						
	}
}

Map.prototype.setBG = function() {
	var imagetype= this.bg.split(".")[1];
	if(imagetype != null) {
		this.setBackgroundImage(this.bg)
	} else {
		this.setBackgroundColor(this.bg);
	}
}

Map.prototype.resetBackground = function() {
	this.rect.setAttributeNS(null,"fill", this.defaultbackgroundcolor);
	this.image.setAttributeNS(null,"display", "none");	
	this.rect.setAttributeNS(null,"display", "inline");
	this.backgroundiscolor = true;
}

Map.prototype.setBackgroundColor = function(color){
	this.rect.setAttributeNS(null,"fill", color);
	if (!this.backgroundiscolor) {
		this.image.setAttributeNS(null,"display", "none");	
		this.rect.setAttributeNS(null,"display", "inline");
		this.backgroundiscolor = true;
	}
}

Map.prototype.setBackgroundImage = function(image){
	this.image.setAttributeNS(xlinkNS, 'xlink:href', image);
	if (this.backgroundiscolor) {
		this.image.setAttributeNS(null,"display", "inline");	
		this.rect.setAttributeNS(null,"display", "none");
		this.backgroundiscolor = false;
	}
}

//get the width of the map
Map.prototype.getWidth = function(){
	return this.width;
}

//get the height of the map
Map.prototype.getHeight= function(){
	return this.height;
}

// clean map and set the array of map element and render
Map.prototype.setMapElements = function(nodes){

	if ( nodes == undefined ) return null;

	this.clean();

	this.mapElements = nodes;
}

Map.prototype.addMapElement = function(mapElement) {

	if (mapElement == null || mapElement.id == undefined) {
		alert("Error adding mapElement to map: mapElement is null");
		return false;
	}
	// if exist first save link then remove element (of course with links)
	if (this.mapElements[mapElement.id] != undefined) { 
		alert("Error adding mapElement to map: mapElement exists");
		return false;
	}
	// now add map element
	this.mapElements[mapElement.id] = mapElement;
	return true;
}

// delete the node (and all its links) with the id in input
Map.prototype.deleteMapElement = function(elemId)
{
	var elementDeleted = false;
	if (elemId == undefined) {
		return elementDeleted;
	}

	//remove the links of the element
	this.deleteLinksOnElement(elemId);
		
	//remove the element from the svg view	

	if(this.mapElements !=null) {
		var i = 0;
		var tempNodes = new Array();
		var curnode = null;
		for (currNode in this.mapElements){
			if (currNode == elemId ) {
				var elem = this.mapElements[currNode].getSvgNode();
				if(elem.parentNode==this.svgNode){
					this.svgNode.removeChild(elem);
					elementDeleted = true;
					//alert("removed"+currNode);
				}
			} else {
				i++;
				tempNodes[currNode]=this.mapElements[currNode];
			}
		}
		this.mapElements=tempNodes;
	}
	return elementDeleted;
}

Map.prototype.getLinkId = function(id1,id2,typology) {

	var a = id1.toString();
	var b = id2.toString();
	var id = a + "-" + b;
	var na = a.substr(0,id1.length-1);
	var nb = b.substr(0,id2.length-1);
	
	if (na > nb) {
		id = b + "-" + a;
	}
	
	if (na == nb && id2.indexOf(MAP_TYPE)== -1) {
		id = b + "-" + a;
	}
	id=id+"-"+typology;
	//alert(id);
	return id;
} 

//return the result of matching by links' ids, ignoring link type
Map.prototype.matchLink = function(id,idlink) {
	var idLinkSplitted = idlink.split("-");
	return ( idLinkSplitted[0]==id || idLinkSplitted[1]==id );
} 

Map.prototype.getLinksOnElement = function(id)
{
	var elements = new Array();
	var elemToRender = null;
	for (elemToRender in this.mapLinks) {
	if(this.matchLink(id,elemToRender))
		elements[elemToRender] = this.mapLinks[elemToRender];
	}
	return elements;
}

Map.prototype.deleteLinksOnElement = function(id)
{
	var linksDeleted = false;
	if(this.mapLinks!=null){
		var elemToRender = null;
		var  i = 0;
		for (elemToRender in this.mapLinks) {
			if(this.matchLink(id,elemToRender)) {
				var ids=elemToRender.split('-');
				this.deleteLink(ids[0],ids[1],ids[2]);
				linksDeleted = true;
			} 
		}
	}
	return linksDeleted;
}

// add a new link to map
Map.prototype.addLink = function(id1, id2, typology, numberOfLinks, statusMap, status, stroke, stroke_width, dash_array, flash, deltaLink,nodeids)
{
	var id = this.getLinkId(id1,id2,typology);
	if(this.mapLinks[id]!= undefined) {
		for (var l in nodeids)
		id+="-"+nodeids[l];
	}
	// check parameter

	var statuscount=0;
	for (var status in statusMap) {
	 statuscount++;
	}
	if (this.ignoreLinkStatus && statuscount > 1 ) 
		stroke=this.ignoreStroke
	
	var first = null;
	var second = null;

	var idSplitted = id.split("-");
	var idWithoutTypology = idSplitted[0]+"-"+idSplitted[1];

	//remove the element from the svg view	
	if(this.mapElements !=null && id1 != null && id2 != null )
	{
		first = this.mapElements[id1];
		second = this.mapElements[id2];
	} else {
		return false;
	}

	if (first == undefined || second == undefined) {
		return false;
	}
	
	//calculate and mantains the number of links between the elements
	if(this.linksBetweenElements[idWithoutTypology]==undefined){
		this.linksBetweenElements[idWithoutTypology]=0;
	}

	var link;
	var sid =  this.getLinkId(id1,id2,this.sLinkId);
    
    if (this.linksBetweenElements[idWithoutTypology] <= this.maxlinks-1) {
		link = new Link(id, typology, status, numberOfLinks, statusMap, first, second, stroke, stroke_width, dash_array, flash,this.linksBetweenElements[idWithoutTypology], deltaLink,nodeids);		
		this.mapLinkSize++;

		this.mapLinks[id] = link;
		if (this.linksBetweenElements[idWithoutTypology] == this.maxlinks-1 ) {
			this.lastlinks[sid] = link;
		}
		this.linksBetweenElements[idWithoutTypology]++;    	
	} else {		
		link = new Link(id, typology, status, numberOfLinks, statusMap, first, second, stroke, stroke_width, dash_array, flash,this.maxlinks-1, deltaLink,nodeids);
		var summaryLink;
		
		if (this.mapLinks[sid]==undefined) {
			summaryLink = new SLink(sid, this.sLinkId, first, second, this.sLinkStroke, this.sLinkStrokeWidth, this.sLinkStrokeDashArray, this.maxlinks-1, deltaLink);
			var lastlink =this.lastlinks[sid];
			summaryLink.addLink(lastlink);
			var tempMapLinks=new Array();
			for ( var linkid in this.mapLinks ) {
			    if ( linkid != lastlink.id ) {
					tempMapLinks[linkid]=this.mapLinks[linkid];
				}
			}
			this.mapLinks=tempMapLinks;
		} else {
			summaryLink = this.mapLinks[sid];
		}
		
		summaryLink.addLink(link);
		this.mapLinks[sid] = summaryLink;
	}
	return true;
}

// delete the link with the id in input
Map.prototype.deleteLink = function(id1,id2,typology)
{	
	var linkDeleted = false;
	var linkId = this.getLinkId(id1,id2,typology);
	if(this.mapLinks[linkId]==undefined){
		linkId = this.getLinkId(id2,id1,typology);
		if(this.mapLinks[linkId]==undefined){
			alert("Warning: link between "+id1+" and "+id2+" not found.");
			return linkDeleted;
		}
	}
	if(this.mapLinks!=null && linkId !=null){
		var elem = this.mapLinks[linkId].getSvgNode();
		if(elem!=null){
			if(elem.parentNode==this.svgNode){
				this.svgNode.removeChild(elem);
				linkDeleted = true;
			}
		}else{
			return linkDeleted;
		}
	}

	//remove the link from the links array
	var counter=0;
	var tempLinks = new Array();
	for (currLink in this.mapLinks){
		if(linkId !=currLink){
			tempLinks[currLink]=this.mapLinks[currLink];
			counter++;
		}
			
	}
	this.mapLinks=tempLinks;
	this.mapLinkSize=counter;	
	
	
	//calculate and mantains the number of links between the elements
	var idWithoutTypology = id1+"-"+id2;
	this.linksBetweenElements[idWithoutTypology]--;

	return linkDeleted;
}

// this render graphs of nodes and links
Map.prototype.render = function()
{
	this.clean();
	
	this.setBG();
    
	for (var linkToRender in this.mapLinks) //render links
		this.svgNode.appendChild(this.mapLinks[linkToRender].getSvgNode());
	for ( var elemToRender in this.mapElements) //render mapElement
		this.svgNode.appendChild(this.mapElements[elemToRender].getSvgNode());
}

// delete all nodes and links from map view
Map.prototype.clean = function()
{
	this.resetBackground();
	this.cleanLinks();		
	this.cleanElements();
}

// delete all nodes and  from map 
Map.prototype.clear = function()
{
	this.clean();
	this.mapLinks=new Array();
	this.mapElements=new Array();
	this.linksBetweenElements=new Array();
	this.lastlinks = new Array();
	//reset the array mantaining the number of links between the elements
}

Map.prototype.cleanElements = function() {
	for (var elemToRender in this.mapElements){
		if(elemToRender!=null){
			var elem = this.mapElements[elemToRender].getSvgNode();
			if(elem.parentNode==this.svgNode){
				this.svgNode.removeChild(elem);
			}
		}
	}
}

Map.prototype.clearElements = function() {
	this.cleanElements();
	this.mapElements=new Array();
}

//delete all  links from map view
Map.prototype.cleanLinks = function() {

	for(var currLink in this.mapLinks) {
		if(this.mapLinks[currLink]!=undefined) {
			var link = this.mapLinks[currLink].getSvgNode();
			if(link.parentNode==this.svgNode) {
				this.svgNode.removeChild(link);
			}
		}
	}
}

//delete all links from map
Map.prototype.clearLinks = function(){
	this.cleanLinks();

	this.mapLinks=new Array();
	this.lastlinks = new Array();
	//reset the array mantaining the number of links between the elements
	this.linksBetweenElements=new Array();
	//alert('links cleaned');
}

Map.prototype.redrawLink = function()
{
	for(var i in this.mapLinks)
	{
		this.mapLinks[i].update();
	}	
}

Map.prototype.redrawLinkOnElement = function(id)
{
	for (var i in this.mapLinks) {
		var splittedLinkId = i.split("-");
		if(splittedLinkId[0]==id || splittedLinkId[1]==id ) 
			this.mapLinks[i].update();
	} 
}

Map.prototype.getMapElement = function(id)
{
	return this.mapElements[id];

}

Map.prototype.getMapElementsSize = function() {
	if (this.mapElements) return this.mapElements.lenght;
	return 0; 
}

Map.prototype.getMapLinksSize = function() {
	if (this.mapLinks) return this.mapLinks.lenght;
	return 0; 
}

Map.prototype.onMouseDownOnMap = onMouseDownOnMap;
Map.prototype.onMouseMove = onMouseMove;
Map.prototype.onMouseUp = onMouseUp;