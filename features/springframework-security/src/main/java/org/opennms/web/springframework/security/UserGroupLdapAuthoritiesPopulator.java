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

package org.opennms.web.springframework.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

/**
 * This class adds the ability to provide a concrete map of associations between specific
 * group values and roles. These associations can be provided by setting the <code>groupToRoleMap</code>
 * property either in a Spring context file or by calling {@link #setGroupToRoleMap(Map)}.
 */
public class UserGroupLdapAuthoritiesPopulator extends DefaultLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

	private final Log logger = LogFactory.getLog(UserGroupLdapAuthoritiesPopulator.class);

	private final SearchControls searchControls = new SearchControls();

	private final SpringSecurityLdapTemplate ldapTemplate;

	/**
	 * Default value is <code>cn</code>.
	 */
	private String groupRoleAttribute = "cn";

	/**
	 * Default value is <code>(member={0})</code>.
	 */
	private String groupSearchFilter = "(member={0})";

	/**
	 * Map is empty by default.
	 */
	private Map<String, List<String>> groupToRoleMap = new HashMap<String, List<String>>();

	public UserGroupLdapAuthoritiesPopulator(ContextSource contextSource, String groupSearchBase) {
		super(contextSource, groupSearchBase);
		this.ldapTemplate = new SpringSecurityLdapTemplate(contextSource);
		this.ldapTemplate.setSearchControls(searchControls);
	}

	/**
	 *
	 * This function returns a list of roles from the given set of groups
	 * based on the value of the <code>groupToRoleMap</code> property.
	 * 
	 * @return a {@link java.util.Set} object.
	 */
	@Override
	protected Set<GrantedAuthority> getAdditionalRoles(final DirContextOperations user, final String username) {
		final String userDn = user.getNameInNamespace();
		final Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

		if (super.getGroupSearchBase() == null) {
			return authorities;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Searching for roles for user '" + username + "', DN = " + "'" + userDn + "', with filter "
					+ this.groupSearchFilter + " in search base '" + super.getGroupSearchBase() + "'");
		}

		final Set<String> userRoles = ldapTemplate.searchForSingleAttributeValues(
				super.getGroupSearchBase(), 
				this.groupSearchFilter,
				new String[]{userDn, username}, 
				this.groupRoleAttribute
		);

		for(String group : userRoles) {
			final List<String> rolesForGroup = this.groupToRoleMap.get(group);
			logger.debug("Checking " + group + " for an associated role");
			if (rolesForGroup != null) {
				for(String role : rolesForGroup) {
					authorities.add(new SimpleGrantedAuthority(role));
					logger.debug("Added role: " + role + " based on group " + group);
				}
			}
		}

		return authorities;
	}

	@Override
	public void setGroupRoleAttribute(final String groupRoleAttribute) {
		super.setGroupRoleAttribute(groupRoleAttribute);
		this.groupRoleAttribute = groupRoleAttribute;
	}

	@Override
	public void setGroupSearchFilter(final String groupSearchFilter) {
		super.setGroupSearchFilter(groupSearchFilter);
		this.groupSearchFilter = groupSearchFilter;
	}

	/**
	 * <p>This property contains a set of group to role mappings. Both values are specified
	 * as string values.</p>
	 * 
	 * <p>An example Spring context that sets this property could be:</p>
	 * 
	 * <pre>
	 * <code>
	 * &lt;property xmlns="http://www.springframework.org/schema/beans" name="groupToRoleMap"&gt;
	 *   &lt;map&gt;
	 *     &lt;entry&gt;
	 *       &lt;key&gt;&lt;value&gt;CompanyX_OpenNMS_User_Group&lt;/value&gt;&lt;/key&gt;
	 *       &lt;list&gt;
	 *         &lt;value&gt;ROLE_USER&lt;/value&gt;
	 *       &lt;/list&gt;
	 *     &lt;/entry&gt;
	 *   &lt;/map&gt; 
	 * &lt;/property&gt;
	 * </code>
	 * </pre>
	 */
	public void setGroupToRoleMap(final Map<String, List<String>> groupToRoleMap) {
		this.groupToRoleMap = groupToRoleMap;
	}

	@Override
	public void setSearchSubtree(boolean searchSubtree) {
		super.setSearchSubtree(searchSubtree);
		int searchScope = searchSubtree ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE;
		this.searchControls.setSearchScope(searchScope);
	}
}
