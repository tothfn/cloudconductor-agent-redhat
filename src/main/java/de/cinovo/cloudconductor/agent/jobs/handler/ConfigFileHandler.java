package de.cinovo.cloudconductor.agent.jobs.handler;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cinovo.cloudconductor.agent.exceptions.ExecutionError;
import de.cinovo.cloudconductor.agent.executors.FileExecutor;
import de.cinovo.cloudconductor.agent.executors.IExecutor;
import de.cinovo.cloudconductor.agent.executors.ScriptExecutor;
import de.cinovo.cloudconductor.agent.helper.ServerCom;
import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.model.ConfigFile;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
public class ConfigFileHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceHandler.class);


	/**
	 * @throws ExecutionError an error occurred during execution
	 */
	public void run() throws ExecutionError {
		Set<ConfigFile> configFiles;
		try {
			configFiles = ServerCom.getFiles();
		} catch (CloudConductorException e) {
			throw new ExecutionError(e);
		}

		// handle files
		IExecutor<Set<String>> files = new FileExecutor(configFiles);
		try {
			files.execute();
		} catch (ExecutionError e) {
			// just log the error but go on with execution
			ConfigFileHandler.LOGGER.error(e.getMessage());
		}

		Set<String> servicesToRestart = files.getResult();
		if ((servicesToRestart != null) && !servicesToRestart.isEmpty()) {
			// handle restart of services
			ScriptExecutor serviceHandler = ScriptExecutor.generateServiceStateHandler(servicesToRestart, null, null);
			try {
				serviceHandler.execute();
			} catch (ExecutionError e) {
				// just log the error but go on with execution
				ConfigFileHandler.LOGGER.error(e.getMessage());
			}
		}
	}
}
