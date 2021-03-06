package de.cinovo.cloudconductor.agent.jobs;

/*
 * #%L
 * Node Agent for cloudconductor framework
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
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
 * #L%
 */

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cinovo.cloudconductor.agent.helper.FileHelper;
import de.cinovo.cloudconductor.agent.helper.ServerCom;
import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.model.SSHKey;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
public class AuhtorizedKeysJob implements AgentJob {
	
	/** the job name, used by scheduler */
	public static final String JOB_NAME = "AUTHORIZED_KEYS";

	private static final Logger LOGGER = LoggerFactory.getLogger(AuhtorizedKeysJob.class);
	
	
	@Override
	public void run() {
		Set<SSHKey> sshKeys;
		try {
			sshKeys = ServerCom.getSSHKeys();
		} catch (CloudConductorException e) {
			AuhtorizedKeysJob.LOGGER.error("Couldn't retrieve ssh keys from server.", e);
			return;
		}
		
		try {
			FileHelper.writeRootAuthorizedKeys(sshKeys);
		} catch (IOException e) {
			AuhtorizedKeysJob.LOGGER.error("Couldn't write auhtorized keys for root.", e);
			return;
		}
	}
	
	@Override
	public String getJobIdentifier() {
		return AuhtorizedKeysJob.JOB_NAME;
	}
	
	@Override
	public boolean isDefaultStart() {
		return false;
	}
	
	@Override
	public long defaultStartTimer() {
		return 0;
	}
	
	@Override
	public TimeUnit defaultStartTimerUnit() {
		return null;
	}
}
