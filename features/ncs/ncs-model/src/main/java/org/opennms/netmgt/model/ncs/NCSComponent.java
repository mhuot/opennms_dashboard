package org.opennms.netmgt.model.ncs;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;


@Entity
@Table(name="ncscomponent")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@XmlRootElement(name="component")
@XmlAccessorType(XmlAccessType.FIELD)
public class NCSComponent {
	
	public enum DependencyRequirements { ANY, ALL };
	
	@XmlRootElement(name="node")
	@XmlAccessorType(XmlAccessType.FIELD)
	@Embeddable
	public static class NodeIdentification {
		@XmlAttribute(name="foreignSource", required=true)
		private String m_foreignSource;
		
	    @XmlAttribute(name="foreignId", required=true)
	    private String m_foreignId;
	    
	    public NodeIdentification() {
		}
	    
	    public NodeIdentification(String nodeForeignSource, String nodeForeignId) {
	    	m_foreignSource = nodeForeignSource;
	    	m_foreignId = nodeForeignId;
	    }
	    
		@Column(name = "nodeForeignSource")
		public String getForeignSource() {
			return m_foreignSource;
		}

		public void setForeignSource(String foreignSource) {
			m_foreignSource = foreignSource;
		}

		@Column(name = "nodeForeignId")
		public String getForeignId() {
			return m_foreignId;
		}

		public void setForeignId(String foreignId) {
			m_foreignId = foreignId;
		}

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((m_foreignId == null) ? 0 : m_foreignId.hashCode());
            result = prime
                    * result
                    + ((m_foreignSource == null) ? 0 : m_foreignSource
                            .hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            NodeIdentification other = (NodeIdentification) obj;
            if (m_foreignId == null) {
                if (other.m_foreignId != null)
                    return false;
            } else if (!m_foreignId.equals(other.m_foreignId))
                return false;
            if (m_foreignSource == null) {
                if (other.m_foreignSource != null)
                    return false;
            } else if (!m_foreignSource.equals(other.m_foreignSource))
                return false;
            return true;
        }

		

	    	    
	}
	
    @XmlElement(name="id")
    private Long m_id;

    @XmlTransient
    private Integer m_version;
    
    @XmlAttribute(name="foreignSource", required=true)
    private String m_foreignSource;
    
    @XmlAttribute(name="foreignId", required=true)
    private String m_foreignId;
    
    @XmlAttribute(name="type", required=true)
    private String m_type;
    
    @XmlElement(name="name")
    private String m_name;
    
    @XmlElement(name="node")
    private NodeIdentification m_nodeIdentification;

    @XmlElement(name="upEventUei")
    private String m_upEventUei;
    
    @XmlElement(name="downEventUei")
    private String m_downEventUei;
    
    @XmlElement(name="dependenciesRequired", required=false, defaultValue="ALL")
    private DependencyRequirements m_dependenciesRequired;
    
    @XmlElement(name = "attributes", required = false)
    @XmlJavaTypeAdapter(JAXBMapAdapter.class)
    private Map<String, String> m_attributes = new LinkedHashMap<String, String>();

    @XmlElement(name="component")
    private Set<NCSComponent> m_subcomponents = new LinkedHashSet<NCSComponent>();

    @XmlTransient
	private Set<NCSComponent> m_parents = new LinkedHashSet<NCSComponent>();
    
    public NCSComponent(final String type, final String foreignSource, final String foreignId) {
    	this();
    	m_type = type;
    	m_foreignSource = foreignSource;
    	m_foreignId = foreignId;
    }
    
    public NCSComponent() {
    }

    @Id
    @Column(name="id", nullable=false)
    @SequenceGenerator(name="opennmsSequence", sequenceName="opennmsNxtId")
    @GeneratedValue(generator="opennmsSequence")
    public Long getId() {
        return m_id;
    }

	public void setId(Long id) {
        m_id = id;
    }

    @Version
    public Integer getVersion() {
        return m_version;
    }

    public void setVersion(Integer version) {
        m_version = version;
    }

	public String getForeignSource() {
		return m_foreignSource;
	}

	public void setForeignSource(String foreignSource) {
		m_foreignSource = foreignSource;
	}

	public String getForeignId() {
		return m_foreignId;
	}

	public void setForeignId(String foreignId) {
		m_foreignId = foreignId;
	}

	public String getType() {
		return m_type;
	}

	public void setType(String type) {
		m_type = type;
	}
	
	public NodeIdentification getNodeIdentification() {
		return m_nodeIdentification;
	}
	
	public void setNodeIdentification(NodeIdentification nodeIdentification) {
		m_nodeIdentification = nodeIdentification;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}
	
	public String getUpEventUei() {
		return m_upEventUei;
	}
	
	public void setUpEventUei(String upEventUei) {
		m_upEventUei = upEventUei;
	}
	
	public String getDownEventUei() {
		return m_downEventUei;
	}
	
	public void setDownEventUei(String downEventUei) {
		m_downEventUei = downEventUei;
	}
	
    @Enumerated(EnumType.STRING)
    @Column(name = "depsRequired")
	public DependencyRequirements getDependenciesRequired() {
		return m_dependenciesRequired;
	}

	public void setDependenciesRequired(DependencyRequirements dependenciesRequired) {
		m_dependenciesRequired = dependenciesRequired;
	}

	@ManyToMany
	@JoinTable(name="subcomponents", joinColumns = { @JoinColumn(name="subcomponent_id") }, inverseJoinColumns = { @JoinColumn(name="component_id") })
	public Set<NCSComponent> getParentcomponents() {
		return m_parents ;
	}

	public void setParentcomponents(final Set<NCSComponent> parents) {
		m_parents = parents;
	}

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="subcomponents", joinColumns = { @JoinColumn(name="component_id") }, inverseJoinColumns = { @JoinColumn(name="subcomponent_id") })
	public Set<NCSComponent> getSubcomponents() {
        return m_subcomponents;
	}

	public void setSubcomponents(Set<NCSComponent> subComponents) {
		m_subcomponents = subComponents;
	}
	
	public void addSubcomponent(NCSComponent subComponent) {
		getSubcomponents().add(subComponent);
	}
	
	public void removeSubcomponent(NCSComponent subComponent) {
		getSubcomponents().remove(subComponent);
	}
	
	public NCSComponent getSubcomponent(String foreignSource, String foreignId) {
		for(NCSComponent subcomponent : getSubcomponents()) {
			if (subcomponent.hasIdentity(foreignSource, foreignId)) {
				return subcomponent;
			}
		}
		return null;
	}
	
	public boolean hasIdentity(String foreignSource, String foreignId) {
		return m_foreignSource.equals(foreignSource) && m_foreignId.equals(foreignId);
	}

    @CollectionOfElements
    @JoinTable(name="ncs_attributes")
    @MapKey(columns=@Column(name="key"))
    @Column(name="value", nullable=false)
	public Map<String, String> getAttributes() {
		return m_attributes;
	}
    
    public void setAttributes(Map<String, String> attributes) {
    	m_attributes = attributes;
    }
	
	public void setAttribute(String key, String value) {
		m_attributes.put(key, value);
	}
	
	public String removeAttribute(String key) {
		return m_attributes.remove(key);
	}
	
	public void visit(NCSComponentVisitor visitor) {
	    // visit this component
	    visitor.visitComponent(this);
	    
	    // visit subcomponents
	    for(NCSComponent subcomponent : getSubcomponents()) {
	        subcomponent.visit(visitor);
	    }
	    
	    // complete visiting this component
	    visitor.completeComponent(this);
	}
}
