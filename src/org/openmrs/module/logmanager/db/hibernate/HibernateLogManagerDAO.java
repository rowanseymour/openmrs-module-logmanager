/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.logmanager.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.logmanager.Preset;
import org.openmrs.module.logmanager.db.LogManagerDAO;

public class HibernateLogManagerDAO implements LogManagerDAO {
	
	protected static final Log log = LogFactory.getLog(HibernateLogManagerDAO.class);
	
	protected SessionFactory sessionFactory;
	
	/**
	 * Set session factory
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) { 
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see LogManagerDAO#getMySQLVersion()
	 */
	public String getMySQLVersion() throws DAOException {
		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session.createSQLQuery("SELECT VERSION();");
		return query.uniqueResult().toString();
	}

	/**
	 * @see LogManagerDAO#getPreset(int)
	 */
	public Preset getPreset(int presetId) throws DAOException {
		Session session = sessionFactory.getCurrentSession();
		return (Preset)session.load(Preset.class, presetId);
	}

	/**
	 * @see LogManagerDAO#getPresets()
	 */
	@SuppressWarnings("unchecked")
	public List<Preset> getPresets() throws DAOException {
		Session session = sessionFactory.getCurrentSession();
		return session.createCriteria(Preset.class).list();
	}

	/**
	 * @see LogManagerDAO#savePreset(Preset)
	 */
	public void savePreset(Preset preset) throws DAOException {
		Session session = sessionFactory.getCurrentSession();	
		session.saveOrUpdate(preset);
	}

	/**
	 * @see LogManagerDAO#deletePreset(Preset)
	 */
	public void deletePreset(Preset preset) throws DAOException {
		Session session = sessionFactory.getCurrentSession();	
		session.delete(preset);
	}
}
