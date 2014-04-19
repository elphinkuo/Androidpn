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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.util.Config;
import org.androidpn.server.xmpp.push.NotificationManager;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class NotificationController extends MultiActionController {

    //private UserService userService;

    private NotificationManager notificationManager;

    public NotificationController() {
        //userService = ServiceManager.getUserService();
        notificationManager = new NotificationManager();
    }

    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView();
        // mav.addObject("list", null);
        mav.setViewName("notif/form");
        return mav;
    }

    public ModelAndView send(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String broadcast = ServletRequestUtils.getStringParameter(request,
                "broadcast", "Y");
        String username = ServletRequestUtils.getStringParameter(request,
                "username");
        String message = ServletRequestUtils.getStringParameter(request,
                "message");
        String title = ServletRequestUtils.getStringParameter(request, "title");
        String ticker = ServletRequestUtils.getStringParameter(request,
                "ticker");
        String url = ServletRequestUtils.getStringParameter(request, "url");

        //        logger.debug("broadcast=" + broadcast);
        //        logger.debug("username=" + username);
        //        logger.debug("title=" + title);
        //        logger.debug("message=" + message);
        //        logger.debug("ticker=" + ticker);
        //        logger.debug("url=" + url);

        String apiKey = Config.getString("apiKey", "");
        logger.debug("apiKey=" + apiKey);

        if (broadcast.equalsIgnoreCase("Y")) {
            notificationManager.sendBroadcast(apiKey, title, message, ticker,
                    url);
        } else {
            notificationManager.sendNotifcationToUser(apiKey, username, title,
                    message, ticker, url);
        }

        ModelAndView mav = new ModelAndView();
        // mav.addObject("list", null);
        mav.setViewName("redirect:notif.do");
        return mav;
    }

}
