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
package org.openmrs.module.logmanager.db;

import java.util.List;

import org.openmrs.api.db.DAOException;
import org.openmrs.module.logmanager.Preset;

public interface LogManagerDAO {

	/**
	 * Gets the version of MySQL server being used by OpenMRS
	 * @return the version string
	 * @throws DAOException
	 */
	public String getMySQLVersion() throws DAOException;
	
	/**
	 * Gets the preset with the given id
	 * @param presetId the preset id
	 * @return the preset
	 * @throws DAOException
	 */
	public Preset getPreset(int presetId) throws DAOException;
	
	/**
	 * Gets all presets
	 * @return the list of presets
	 * @throws DAOException
	 */
	public List<Preset> getPresets() throws DAOException;
	
	/**
	 * Updates the given preset in the database
	 * @param preset the preset to save
	 * @throws DAOException
	 */
	public void savePreset(Preset preset) throws DAOException;

	/**
	 * Deletes the given preset from the database
	 * @param preset the preset to delete
	 * @throws DAOException
	 */
	public void deletePreset(Preset preset) throws DAOException;
}
