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
 *******************************************************************************/

package org.opennms.web.filter;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

/**
 * OrFilter
 *
 * @author brozow
 * @version $Id: $
 * @since 1.8.1
 */
public class OrFilter extends ConditionalFilter {
    
    /**
     * <p>Constructor for OrFilter.</p>
     *
     * @param filters a {@link org.opennms.web.filter.Filter} object.
     */
    public OrFilter(Filter... filters) {
        super("OR", filters);
    }

    /** {@inheritDoc} */
    @Override
    public Criterion getCriterion() {
        Disjunction disjunction = Restrictions.disjunction();
        
        for(Filter filter : getFilters()) {
            disjunction.add(filter.getCriterion());
        }
        
        return disjunction;
    }

}
