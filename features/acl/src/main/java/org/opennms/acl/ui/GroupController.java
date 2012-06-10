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

package org.opennms.acl.ui;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.opennms.acl.domain.Group;
import org.opennms.acl.exception.AuthorityNotFoundException;
import org.opennms.acl.model.Pager;
import org.opennms.acl.service.GroupService;
import org.opennms.acl.ui.util.WebUtils;
import org.opennms.acl.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Group Controller
 *
 * @author Massimiliano Dess&igrave; (desmax74@yahoo.it)
 * @since jdk 1.5.0
 * @version $Id: $
 */
@Controller
public class GroupController {

    /**
     * <p>list</p>
     *
     * @param req a {@link javax.servlet.http.HttpServletRequest} object.
     * @return a {@link org.springframework.web.servlet.ModelAndView} object.
     */
    @RequestMapping("/group.list.page")
    public ModelAndView list(HttpServletRequest req) {
        Pager pager = WebUtils.getPager(req, groupService.getTotalItemsNumber(), 15);
        ModelAndView mav = new ModelAndView("group/list");
        mav.addObject(Constants.GROUPS, groupService.getGroups(pager));
        mav.addObject(Constants.PAGER, pager);
        return mav;
    }

    /**
     * <p>detail</p>
     *
     * @param req a {@link javax.servlet.http.HttpServletRequest} object.
     * @return a {@link org.springframework.web.servlet.ModelAndView} object.
     */
    @RequestMapping("/group.detail.page")
    public ModelAndView detail(HttpServletRequest req) {
        Group group = WebUtils.getGroup(req);
        return new ModelAndView("group/detail", Constants.GROUP, group.getGroupView());
    }

    /**
     * <p>delete</p>
     *
     * @param req a {@link javax.servlet.http.HttpServletRequest} object.
     * @return a {@link org.springframework.web.servlet.ModelAndView} object.
     */
    @RequestMapping("/group.delete.page")
    public ModelAndView delete(HttpServletRequest req) {
        Group group = WebUtils.getGroup(req);
        ModelAndView mav = new ModelAndView(Constants.REDIRECT_GROUP_LIST);
        mav.addObject(Constants.MESSAGE, group.remove() ? Constants.MSG_AUTHORITY_DELETE_SUCCESS : Constants.MSG_AUTHORITY_DELETE_FAILURE);
        return mav;
    }

    /**
     * <p>confirmDelete</p>
     *
     * @param req a {@link javax.servlet.http.HttpServletRequest} object.
     * @return a {@link org.springframework.web.servlet.ModelAndView} object.
     */
    @RequestMapping("/group.confirm.page")
    public ModelAndView confirmDelete(HttpServletRequest req) {
        Group group = WebUtils.getGroup(req);
        ModelAndView mav = new ModelAndView("group/detail");
        mav.addObject(Constants.GROUP, group.getGroupView());
        mav.addObject(Constants.UI_MODE, Constants.DELETE);
        return mav;
    }

    /**
     * <p>items</p>
     *
     * @param req a {@link javax.servlet.http.HttpServletRequest} object.
     * @return a {@link org.springframework.web.servlet.ModelAndView} object.
     */
    @RequestMapping("/group.items.page")
    public ModelAndView items(HttpServletRequest req) {
        Group group = WebUtils.getGroup(req);
        if (group != null) {
            ModelAndView mav = new ModelAndView("group/items");
            mav.addObject(Constants.GROUP, group.getGroupView());
            mav.addObject(Constants.UI_ITEMS, group.getFreeAuthorities());
            mav.addObject(Constants.GROUP_AUTHORITIES, group.getAuthorities());
            return mav;
        } else {
            throw new AuthorityNotFoundException("id not found");
        }
    }

    /**
     * <p>selection</p>
     *
     * @param ids a {@link java.lang.String} object.
     * @param req a {@link javax.servlet.http.HttpServletRequest} object.
     * @return a {@link org.springframework.web.servlet.ModelAndView} object.
     */
    @RequestMapping("/group.selection.page")
    public ModelAndView selection(@RequestParam("includedHidden") String ids, HttpServletRequest req) {
        Group group = WebUtils.getGroup(req);
        if (group != null && ids.length() > 0) {
            group.setNewAuthorities(WebUtils.extractIdGrantedAuthorityFromString(ids, Constants.COMMA));
        } else {
            group.setNewAuthorities(new ArrayList<Integer>());
        }
        group.save();
        return new ModelAndView(new StringBuilder(Constants.REDIRECT_GROUP_LIST).append("?").append(Constants.GROUP_ID).append("=").append(group.getId()).toString());
    }

    /**
     * <p>Constructor for GroupController.</p>
     *
     * @param groupService a {@link org.opennms.acl.service.GroupService} object.
     */
    @Autowired
    public GroupController(@Qualifier("groupService") GroupService groupService) {
        this.groupService = groupService;
    }

    private final GroupService groupService;
}
