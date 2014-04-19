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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.console.vo.SessionVO;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.androidpn.server.xmpp.session.SessionManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.xmpp.packet.Presence;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class SessionController extends MultiActionController {

    //private UserService userService;

    public SessionController() {
        //userService = ServiceManager.getUserService();
    }

    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ClientSession[] sessions = new ClientSession[0];
        sessions = SessionManager.getInstance().getSessions().toArray(sessions);

        List<SessionVO> voList = new ArrayList<SessionVO>();
        for (ClientSession sess : sessions) {
            SessionVO vo = new SessionVO();
            vo.setUsername(sess.getUsername());
            vo.setResource(sess.getAddress().getResource());
            // Status
            if (sess.getStatus() == Session.STATUS_CONNECTED) {
                vo.setStatus("CONNECTED");
            } else if (sess.getStatus() == Session.STATUS_AUTHENTICATED) {
                vo.setStatus("AUTHENTICATED");
            } else if (sess.getStatus() == Session.STATUS_CLOSED) {
                vo.setStatus("CLOSED");
            } else {
                vo.setStatus("UNKNOWN");
            }
            // Presence
            if (!sess.getPresence().isAvailable()) {
                vo.setPresence("Offline");
            } else {
                Presence.Show show = sess.getPresence().getShow();
                if (show == null) {
                    vo.setPresence("Online");
                } else if (show == Presence.Show.away) {
                    vo.setPresence("Away");
                } else if (show == Presence.Show.chat) {
                    vo.setPresence("Chat");
                } else if (show == Presence.Show.dnd) {
                    vo.setPresence("Do Not Disturb");
                } else if (show == Presence.Show.xa) {
                    vo.setPresence("eXtended Away");
                } else {
                    vo.setPresence("Unknown");
                }
            }
            vo.setClientIP(sess.getHostAddress());
            vo.setCreatedDate(sess.getCreationDate());
            voList.add(vo);
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("sessionList", voList);
        mav.setViewName("session/list");
        return mav;
    }

}
