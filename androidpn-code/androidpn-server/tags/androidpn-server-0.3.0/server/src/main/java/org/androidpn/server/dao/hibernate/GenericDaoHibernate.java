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

import java.io.Serializable;
import java.util.List;

import org.androidpn.server.dao.GenericDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/** 
 * Class desciption here.
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
