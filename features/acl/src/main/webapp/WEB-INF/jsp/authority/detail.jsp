<%--
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
 *
 * From the original copyright headers:
 *
 * Copyright (c) 2009+ desmax74
 * Copyright (c) 2009+ The OpenNMS Group, Inc.
 *
 * This program was developed and is maintained by Rocco RIONERO
 * ("the author") and is subject to dual-copyright according to
 * the terms set in "The OpenNMS Project Contributor Agreement".
 *
 * The author can be contacted at the following email address:
 *
 *     Massimiliano Dess&igrave;
 *     desmax74@yahoo.it
 *******************************************************************************/

--%>
<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>
<c:if test="${mode == 'delete'}"><font color="red"><spring:message code="authority.delete.confirm"/></font></c:if>
<form action="authority.list.page" method="post">
<p class="delimitato"><span><spring:message code="ui.authority.name"/>:</span>${authority.name}</p>
<p class="delimitato"><span><spring:message code="authority.description"/>:</span>${authority.description}</p>
<input type="Submit" value="<spring:message code="authority.list"/>" class="push"/>
<input type="button" onclick="location.href = 'authority.edit.page?aid=${authority.id}'" value="<spring:message code="ui.action.edit"/>"/>
<input type="button" onclick="location.href = 'authority.items.page?aid=${authority.id}'" value="<spring:message code="ui.action.edit.groups"/>"/>
<c:if test="${mode == 'delete'}"><input type="button" onclick="location.href = 'authority.delete.page?aid=${authority.id}'" value="<spring:message code="authority.delete"/>"/></c:if><br/>
</form>