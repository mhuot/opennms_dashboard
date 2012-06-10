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

package org.opennms.netmgt.dao.support;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import java.io.IOException;

import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.dao.ResourceDao;
import org.opennms.netmgt.dao.support.DefaultResourceDao;
import org.opennms.netmgt.dao.support.ResponseTimeResourceType;
import org.opennms.netmgt.model.OnmsNode;

import junit.framework.TestCase;

public class ResponseTimeResourceTypeTest extends TestCase {
    public void testGetResourcesUsingDao() throws IOException {
        ResourceDao resourceDao = new DefaultResourceDao();
        NodeDao nodeDao = createMock(NodeDao.class);
        
        expect(nodeDao.get(1)).andReturn(new OnmsNode());
        
        ResponseTimeResourceType r = new ResponseTimeResourceType(resourceDao, nodeDao);
        
        replay(nodeDao);
        r.getResourcesForNode(1);
        verify(nodeDao);
    }
}
