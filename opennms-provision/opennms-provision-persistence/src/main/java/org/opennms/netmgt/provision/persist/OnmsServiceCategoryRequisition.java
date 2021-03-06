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

package org.opennms.netmgt.provision.persist;

import org.opennms.netmgt.provision.persist.requisition.RequisitionCategory;

/**
 * OnmsCategoryRequisition
 *
 * @author brozow
 * @version $Id: $
 */
public class OnmsServiceCategoryRequisition {

    private RequisitionCategory m_category;

    /**
     * <p>Constructor for OnmsServiceCategoryRequisition.</p>
     *
     * @param category a {@link org.opennms.netmgt.provision.persist.requisition.RequisitionCategory} object.
     */
    public OnmsServiceCategoryRequisition(RequisitionCategory category) {
        m_category = category;
    }

    /**
     * @return the category
     */
    RequisitionCategory getCategory() {
        return m_category;
    }

    /**
     * <p>visit</p>
     *
     * @param visitor a {@link org.opennms.netmgt.provision.persist.RequisitionVisitor} object.
     */
    public void visit(RequisitionVisitor visitor) {
        visitor.visitServiceCategory(this);
        visitor.completeServiceCategory(this);
    }

    /**
     * <p>getName</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return m_category.getName();
    }
    
    

}
