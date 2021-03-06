<%

/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2007-2011 The OpenNMS Group, Inc.
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

%>



<%@page language="java"
	contentType="text/html"
	session="true" 
%>

<jsp:include page="/includes/header.jsp" flush="false" >
  <jsp:param name="title" value="Configure Discovery" />
  <jsp:param name="headTitle" value="Discovery" />
  <jsp:param name="headTitle" value="Admin" />
  <jsp:param name="location" value="admin" />
  <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>Admin</a>" />
  <jsp:param name="breadcrumb" value="Discovery" />
</jsp:include>


<!-- Body -->

  <div class="TwoColLAdmin">
    <h3>Discovery</h3>
		<p>
			<a href="admin/discovery/modifyDiscoveryConfig">Modify Configuration</a>
		</p>
  </div>
      
  	<div class="TwoColRAdmin">
      <h3>Configuration</h3>
        <p>The place to configure the Discovery service.
        After you have added, removed specific IP addresses or ranges, you can save the configuration and restart
        the service.
        </p>       
  </div>
  <hr />


<jsp:include page="/includes/footer.jsp" flush="false" />

