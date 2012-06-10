/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2008-2011 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.vmmgr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.management.MBeanServer;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.opennms.core.utils.LogUtils;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.config.service.Service;
import org.opennms.netmgt.config.service.types.InvokeAtType;

/**
 * <p>
 * The Manager is reponsible for launching/starting all services in the VM
 * that it is started for. The Manager operates in two modes, normal and
 * server
 * </p>
 * <p>
 * normal mode: In the normal mode, the Manager starts all services configured
 * for its VM in the service-configuration.xml and starts listening for
 * control events on the 'control-broadcast' JMS topic for stop control
 * messages for itself
 * </p>
 * <p>
 * server mode: In the server mode, the Manager starts up and listens on the
 * 'control-broadcast' JMS topic for 'start' control messages for services in
 * its VM and a stop control messge for itself. When a start for a service is
 * received, it launches only that service and sends a successful 'running' or
 * an 'error' response to the Controller
 * </p>
 * <p>
 * <strong>Note: </strong>The Manager is NOT intelligent - if it receives a
 * stop control event, it will exit - does not check to see if the services
 * its started are all stopped
 * <p>
 *
 * @author <a href="mailto:weave@oculan.com">Brian Weaver</a>
 * @author <a href="mailto:sowmya@opennms.org">Sowmya Nataraj</a>
 * @author <a href="http://www.opennms.org">OpenNMS.org</a>
 * @author <a href="mailto:weave@oculan.com">Brian Weaver</a>
 * @author <a href="mailto:sowmya@opennms.org">Sowmya Nataraj</a>
 * @author <a href="http://www.opennms.org">OpenNMS.org</a>
 * @author <a href="mailto:weave@oculan.com">Brian Weaver</a>
 * @author <a href="mailto:sowmya@opennms.org">Sowmya Nataraj</a>
 * @author <a href="http://www.opennms.org">OpenNMS.org</a>
 * @version $Id: $
 */
public class Starter {
    /**
     * The log4j category used to log debug messsages and statements.
     */
    private static final String LOG4J_CATEGORY = "OpenNMS.Manager";

    private void setLogPrefix() {
        ThreadCategory.setPrefix(LOG4J_CATEGORY);
    }

    private ThreadCategory log() {
        return ThreadCategory.getInstance(getClass());
    }
    
    /**
     * <p>startDaemon</p>
     */
    public void startDaemon() {
        configureLog4j();
        
        setLogPrefix();
        
        setupMx4jLogger();
        
        loadGlobalProperties();
        
        setDefaultProperties();

        start();
    }

    private void setupMx4jLogger() {
        mx4j.log.Log.redirectTo(new mx4j.log.Log4JLogger());
    }
    
    private void configureLog4j() {
        File homeDir = new File(System.getProperty("opennms.home"));
        File etcDir = new File(homeDir, "etc");
        
        File xmlFile = new File(etcDir, "log4j.xml");
        if (xmlFile.exists()) {
            DOMConfigurator.configureAndWatch(xmlFile.getAbsolutePath());
        } else {
            File propertiesFile = new File(etcDir, "log4j.properties");
            if (propertiesFile.exists()) {
                PropertyConfigurator.configureAndWatch(propertiesFile.getAbsolutePath());
            } else {
                die("Could not find a Log4j configuration file at "
                        + xmlFile.getAbsolutePath() + " or "
                        + propertiesFile.getAbsolutePath() + ".  Exiting.");
            }
        }

	/*
	 * This is causing infinite recursion on exit
	 * CaptchaStds.captchaStdOut();
	 */
    }
    
    
    private void setDefaultProperties() {
        setupFileResourceProperty("opennms.library.jicmp", System.mapLibraryName("jicmp"), "Initialization of ICMP socket will likely fail.");
        setupFileResourceProperty("opennms.library.jrrd", System.mapLibraryName("jrrd"), "Initialization of RRD code will likely fail if the JniRrdStrategy is used.");
        setupFileResourceProperty("jcifs.properties", "jcifs.properties", "Initialization of JCIFS will likely fail or may be improperly configured.");
    }

    private void setupFileResourceProperty(String propertyName, String file, String notFoundWarning) {
        if (System.getProperty(propertyName) == null) {
            log().debug("System property '" + propertyName + "' not set.  Searching for file '" + file + "' in the class path.");
            URL url = getClass().getClassLoader().getResource(file);
            if (url != null) {
                log().info("Found file '" + file + "' at '" + url.getPath() + "'.  Setting '" + propertyName + "' to this path.");
                System.setProperty(propertyName, url.getPath());
            } else {
                log().warn("Did not find file '" + file + "' in the class path.  " + notFoundWarning + "  Set the property '" + propertyName + "' to the location of the file.");
            }
        } else {
            log().debug("System property '" + propertyName + "' already set to '" + System.getProperty(propertyName) + "'.");
        }
    }

    private void loadGlobalProperties() {
        // Log system properties, sorted by property name
        TreeMap<Object, Object> sortedProps = new TreeMap<Object, Object>(System.getProperties());
        for (Entry<Object, Object> entry : sortedProps.entrySet()) {
            log().debug("System property '" + entry.getKey() + "' already set to value '" + entry.getValue() + "'.");
        }
        
        File propertiesFile = getPropertiesFile();
        if (!propertiesFile.exists()) {
            // don't require the file
            return;
        }
        
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(propertiesFile);
            props.load(in);
        } catch (IOException e) {
            die("Error trying to read properties file '" + propertiesFile + "': " + e, e);
        } finally {
            IOUtils.closeQuietly(in);
        }

        for (Entry<Object, Object> entry : props.entrySet()) {
            String systemValue = System.getProperty(entry.getKey().toString());
            if (systemValue != null) {
                log().debug("Property '" + entry.getKey() + "' from " + propertiesFile + " already exists as a system property (with value '" + systemValue + "').  Not overridding existing system property.");
            } else {
                log().debug("Setting system property '" + entry.getKey() + "' to '" + entry.getValue() + "' from " + propertiesFile + ".");
                System.setProperty(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        
        if (props.containsKey("networkaddress.cache.ttl")) {
            java.security.Security.setProperty("networkaddress.cache.ttl", props.getProperty("networkaddress.cache.ttl"));
        } else {
            java.security.Security.setProperty("networkaddress.cache.ttl", "120");
        }
    }

    /**
     * Print out a message and stack trace and then exit.
     * This method does not return.
     * 
     * @param message message to print to System.err
     * @param t Throwable for which to print a stack trace
     */
    private void die(String message, Throwable  t) {
        LogUtils.errorf(this, t, message);
        System.exit(1);
    }

    private void die(String message) {
        die(message, null);
    }

    private File getPropertiesFile() {
        String homeDir = System.getProperty("opennms.home");
        File etcDir = new File(homeDir, "etc");
        File propertiesFile = new File(etcDir, "opennms.properties");
        return propertiesFile;
    }

    private void start() {
        log().debug("Beginning startup");

        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        
        Invoker invoker = new Invoker();
        invoker.setServer(server);
        invoker.setAtType(InvokeAtType.START);
        List<InvokerService> services = InvokerService.createServiceList(Invoker.getDefaultServiceConfigFactory().getServices());
        invoker.setServices(services);
        invoker.instantiateClasses();

        List<InvokerResult> resultInfo = invoker.invokeMethods();

        for (InvokerResult result : resultInfo) {
            if (result != null && result.getThrowable() != null) {
                Service service = result.getService();
                String name = service.getName();
                String className = service.getClassName();

                String message =
                    "An error occurred while attempting to start the \"" +
                    name + "\" service (class " + className + ").  "
                    + "Shutting down and exiting.";
                log().fatal(message, result.getThrowable());
                System.err.println(message);
                result.getThrowable().printStackTrace();

                Manager manager = new Manager();
                manager.stop();
                manager.doSystemExit();

                // Shouldn't get here
                return;
            }
        }
        
        log().debug("Startup complete");
    }
}
