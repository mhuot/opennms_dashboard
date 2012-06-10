<%--
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

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<jsp:include page="/includes/header.jsp" flush="false">
	<jsp:param name="title" value="Category" />
	<jsp:param name="headTitle" value="Category" />
	<jsp:param name="breadcrumb"
               value="<a href='admin/index.jsp'>Admin</a>" />
	<jsp:param name="breadcrumb"
	           value="<a href='admin/categories.htm'>Category</a>" />
	<jsp:param name="breadcrumb" value="Show" />
</jsp:include>

<h3>Edit surveillance categories on ${model.node.label}</h3>

<p>
Node <a href="<c:url value='element/node.jsp?node=${model.node.id}'/>">${model.node.label}</a> (node ID: ${model.node.id}) has ${fn:length(model.node.categories)} categories 
</p>

<form action="admin/categories.htm" method="get">
  <input type="hidden" name="node" value="${model.node.id}"/>
  <input type="hidden" name="edit" value=""/>
  
  <table class="normal">
    <tr>
      <td class="normal" align="center">
		Available categories
      </td>
      
      <td class="normal">  
      </td>

      <td class="normal" align="center">
      	Categories on node
      </td>
    </tr>
      
    <tr>
      <td class="normal">  
    <select name="toAdd" size="20" multiple>
	  <c:forEach items="${model.categories}" var="category">
	    <option value="${category.id}">${category.name}</option>
	  </c:forEach>
    </select>
      </td>
      
      <td class="normal" style="text-align:center; vertical-align:middle;">  
        <input type="submit" name="action" value="Add &#155;&#155;"/>
        <br/>
        <br/>
        <input type="submit" name="action" value="&#139;&#139; Remove"/>
      </td>
    
      <td class="normal">  
    <select name="toDelete" size="20" multiple>
	  <c:forEach items="${model.sortedCategories}" var="category">
	    <option value="${category.id}">${category.name}</option>
	  </c:forEach>
    </select>
      </td>
    </tr>
  </table>
</form>

<jsp:include page="/includes/footer.jsp" flush="false"/>
