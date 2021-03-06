/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2011 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.config.jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.opennms.core.utils.ConfigFileConstants;
import org.opennms.core.utils.ThreadCategory;

public class JdbcDataCollectionConfigFactory {
    private JdbcDataCollectionConfig m_jdbcDataCollectionConfig = null;
    
    public JdbcDataCollectionConfigFactory() {
        try {
            File cfgFile = ConfigFileConstants.getFile(ConfigFileConstants.JDBC_COLLECTION_CONFIG_FILE_NAME);
            log().debug("init: config file path: " + cfgFile.getPath());
            InputStream reader = new FileInputStream(cfgFile);
            unmarshall(reader);
            reader.close();
        } catch(IOException e) {
            // TODO rethrow.
        }
    }
    
    public JdbcDataCollectionConfig unmarshall(InputStream configFile) {
        try {
            InputStream jdccStream = configFile;
            JAXBContext context = JAXBContext.newInstance(JdbcDataCollectionConfig.class);
            Unmarshaller um = context.createUnmarshaller();
            um.setSchema(null);
            JdbcDataCollectionConfig jdcc = (JdbcDataCollectionConfig) um.unmarshal(jdccStream);
            m_jdbcDataCollectionConfig = jdcc;
            return jdcc;
        } catch (Throwable e) {
            // TODO!!
            //throw new ForeignSourceRepositoryException("unable to access default foreign source resource", e);
        }
        return m_jdbcDataCollectionConfig;
    }
    
    protected static ThreadCategory log() {
        return ThreadCategory.getInstance(JdbcDataCollectionConfig.class);
    }

}
