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
package org.androidpn.server.service.impl;

import java.util.List;

import javax.persistence.EntityExistsException;

import org.androidpn.server.dao.UserDao;
import org.androidpn.server.model.User;
import org.androidpn.server.service.UserExistsException;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class UserServiceImpl implements UserService {

    protected final Log log = LogFactory.getLog(getClass());

    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public User getUser(String userId) {
        return userDao.get(new Long(userId));
    }

    public List<User> getUsers() {
        return userDao.getUsers();
    }

    public User saveUser(User user) throws UserExistsException {
        try {
            return userDao.saveUser(user);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername()
                    + "' already exists!");
        } catch (EntityExistsException e) { // needed for JPA
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername()
                    + "' already exists!");
        }
    }

    public User getUserByUsername(String username) throws UserNotFoundException {
        return (User) userDao.getUserByUsername(username);
    }

    public void removeUser(Long userId) {
        log.debug("removing user: " + userId);
        userDao.remove(userId);
    }

}
