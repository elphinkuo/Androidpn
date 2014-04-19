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

import java.io.Serializable;
import java.util.List;

import org.androidpn.server.dao.GenericDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/** 
 * This class is the Base class for all other DAOs serving common CRUD methods.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class GenericDaoHibernate<T, PK extends Serializable> extends
        HibernateDaoSupport implements GenericDao<T, PK> {

    protected final Log log = LogFactory.getLog(getClass());

    private Class<T> persistentClass;

    public GenericDaoHibernate(final Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        return super.getHibernateTemplate().loadAll(this.persistentClass);
    }

    @SuppressWarnings("unchecked")
    public T get(PK id) {
        T entity = (T) super.getHibernateTemplate().get(this.persistentClass,
                id);
        // if (entity == null) {
        //     log.warn("Uh oh, '" + this.persistentClass + "' object with id '" + id + "' not found...");
        //     throw new ObjectRetrievalFailureException(this.persistentClass, id);
        // }
        return entity;
    }

    @SuppressWarnings("unchecked")
    public boolean exists(PK id) {
        T entity = (T) super.getHibernateTemplate().get(this.persistentClass,
                id);
        return entity != null;
    }

    @SuppressWarnings("unchecked")
    public T save(T object) {
        return (T) super.getHibernateTemplate().merge(object);
    }

    public void remove(PK id) {
        super.getHibernateTemplate().delete(this.get(id));
    }
}
