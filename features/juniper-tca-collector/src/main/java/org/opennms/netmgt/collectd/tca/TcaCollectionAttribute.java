package org.opennms.netmgt.collectd.tca;

import org.opennms.netmgt.collectd.AbstractCollectionAttribute;
import org.opennms.netmgt.config.collector.CollectionAttribute;
import org.opennms.netmgt.config.collector.CollectionAttributeType;
import org.opennms.netmgt.config.collector.CollectionResource;
import org.opennms.netmgt.config.collector.ServiceParameters;

/**
 * The Class TcaCollectionAttribute.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class TcaCollectionAttribute extends AbstractCollectionAttribute implements CollectionAttribute {

	/** The Attribute Name. */
	private String m_name;

	/** The Attribute Value. */
	private String m_value;

	/** The TCA Collection Resource associated with this attribute. */
	private TcaCollectionResource m_resource;

	/** The Attribute Type. */
	private CollectionAttributeType m_attribType;

	/**
	 * Instantiates a new XML collection attribute.
	 *
	 * @param resource the resource
	 * @param attribType the attribute type
	 * @param name the attribute name
	 * @param value the attribute value
	 */
	public TcaCollectionAttribute(TcaCollectionResource resource, CollectionAttributeType attribType, String name, String value) {
		m_resource = resource;
		m_attribType = attribType;
		m_name = name;
		m_value = value;
	}

	/* (non-Javadoc)
	 * @see org.opennms.netmgt.collectd.AbstractCollectionAttribute#getAttributeType()
	 */
	public CollectionAttributeType getAttributeType() {
		return m_attribType;
	}

	/* (non-Javadoc)
	 * @see org.opennms.netmgt.collectd.AbstractCollectionAttribute#getName()
	 */
	public String getName() {
		return m_name;
	}

	/* (non-Javadoc)
	 * @see org.opennms.netmgt.collectd.AbstractCollectionAttribute#getNumericValue()
	 */
	public String getNumericValue() {
		return m_value;
	}

	/* (non-Javadoc)
	 * @see org.opennms.netmgt.collectd.AbstractCollectionAttribute#getResource()
	 */
	public CollectionResource getResource() {
		return m_resource;
	}

	/* (non-Javadoc)
	 * @see org.opennms.netmgt.collectd.AbstractCollectionAttribute#getStringValue()
	 */
	public String getStringValue() {
		return m_value;
	}

	/* (non-Javadoc)
	 * @see org.opennms.netmgt.collectd.AbstractCollectionAttribute#shouldPersist(org.opennms.netmgt.config.collector.ServiceParameters)
	 */
	public boolean shouldPersist(ServiceParameters params) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.opennms.netmgt.config.collector.CollectionAttribute#getType()
	 */
	public String getType() {
		return m_attribType.getType();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "TcaCollectionAttribute " + m_name + "=" + m_value;
	}

}
