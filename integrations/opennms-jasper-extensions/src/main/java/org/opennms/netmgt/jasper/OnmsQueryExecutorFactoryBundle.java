/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.jasper;

import org.opennms.netmgt.jasper.jrobin.JRobinQueryExecutorFactory;
import org.opennms.netmgt.jasper.rrdtool.RrdtoolQueryExecutorFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactory;
import net.sf.jasperreports.engine.query.QueryExecuterFactoryBundle;
import org.opennms.netmgt.jasper.jrobin.JRobinQueryExecutorFactory;
import org.opennms.netmgt.jasper.resource.ResourceQueryExecuterFactory;
import org.opennms.netmgt.jasper.rrdtool.RrdtoolQueryExecutorFactory;

public class OnmsQueryExecutorFactoryBundle implements QueryExecuterFactoryBundle {
    
    public String[] getLanguages() {
        return new String[] {"jrobin","rrdtool","resourceQuery"};
    }

    public JRQueryExecuterFactory getQueryExecuterFactory(String language) throws JRException {
        if("jrobin".equals(language)) {
            return new JRobinQueryExecutorFactory();
        } else if("rrdtool".equals(language)) {
            return new RrdtoolQueryExecutorFactory();
        } else if("resourceQuery".equals(language)) {
            return new ResourceQueryExecuterFactory();
        } else {
            return null;
        }
    }

}
