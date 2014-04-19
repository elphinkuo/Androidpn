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
package org.androidpn.server.dao.hibernate;

import java.util.List;

import org.androidpn.server.dao.UserDao;
import org.androidpn.server.model.User;
import org.androidpn.server.service.UserNotFoundException;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class UserDaoHibernate extends GenericDaoHibernate<User, Long> implements
        UserDao {

    public UserDaoHibernate() {
        super(User.class);
    }

    @SuppressWarnings("unchecked")
    public List<User> getUsers() {
        return getHibernateTemplate().find(
                "from User u order by upper(u.lastName), upper(u.firstName)");
    }

    public User saveUser(User user) {
        getHibernateTemplate().saveOrUpdate(user);
        getHibernateTemplate().flush();
        return user;
    }

    //    @Override
    //    public User save(User user) {
    //        return this.saveUser(user);
    //    }

    @SuppressWarnings("unchecked")
    public User getUserByUsername(String username) throws UserNotFoundException {
        List users = getHibernateTemplate().find("from User where username=?",
                username);
        if (users == null || users.isEmpty()) {
            throw new UserNotFoundException("User '" + username + "' not found");
        } else {
            return (User) users.get(0);
        }
    }

    //    @SuppressWarnings("unchecked")
    //    public User findUserByUsername(String username) {
    //        List users = getHibernateTemplate().find("from User where username=?",
    //                username);
    //        return (users == null || users.isEmpty()) ? null : (User) users.get(0);
    //    }

}
