/*
 * Copyright (C) 2010 The Androidpn Team
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
                "from User u order by u.createdDate desc");
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
