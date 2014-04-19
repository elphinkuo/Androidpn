/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.server.console.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.model.User;
import org.androidpn.server.service.ServiceManager;
import org.androidpn.server.service.UserService;
import org.androidpn.server.xmpp.presence.PresenceManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class UserController extends MultiActionController {

    private UserService userService;

    public UserController() {
        userService = ServiceManager.getUserService();
    }

    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PresenceManager presenceManager = new PresenceManager();
        List<User> userList = userService.getUsers();
        for (User user : userList) {
            if (presenceManager.isAvailable(user)) {
                // Presence presence = presenceManager.getPresence(user);
                user.setOnline(true);
            } else {
                user.setOnline(false);
            }
            // logger.debug("user.online=" + user.isOnline());
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("userList", userList);
        mav.setViewName("user/list");
        return mav;
    }

}
