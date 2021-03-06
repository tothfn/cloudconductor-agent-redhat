package de.cinovo.cloudconductor.agent.jobs.handler;

import de.cinovo.cloudconductor.agent.AgentState;
import de.cinovo.cloudconductor.agent.jobs.AgentJob;
import de.cinovo.cloudconductor.agent.jobs.AuhtorizedKeysJob;
import de.cinovo.cloudconductor.agent.jobs.DefaultJob;
import de.cinovo.cloudconductor.agent.jobs.FilesJob;
import de.cinovo.cloudconductor.agent.jobs.HeartBeatJob;
import de.cinovo.cloudconductor.api.lib.helper.SchedulerService;
import de.cinovo.cloudconductor.api.model.AgentOptions;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
public class OptionHandler {

	/** existing jobs */
	@SuppressWarnings("unchecked")
	public static final Class<AgentJob>[] jobRegistry = new Class[] {DefaultJob.class, AuhtorizedKeysJob.class, FilesJob.class, HeartBeatJob.class};

	private AgentOptions newOptions;
	
	
	/**
	 * @param newOptions the new options to use
	 */
	public OptionHandler(AgentOptions newOptions) {
		this.newOptions = newOptions;
	}

	/**
	 */
	public void run() {
		AgentOptions oldOptions = AgentState.getOptions();
		AgentState.setOptions(this.newOptions);

		// option timer
		if ((oldOptions == null) || (this.newOptions.getAliveTimer() != oldOptions.getAliveTimer()) || (this.newOptions.getAliveTimerUnit() != oldOptions.getAliveTimerUnit())) {
			SchedulerService.instance.resetTask(HeartBeatJob.JOB_NAME, this.newOptions.getAliveTimer(), this.newOptions.getAliveTimerUnit());
		}

		// SSH KEYS
		switch (this.newOptions.getDoSshKeys()) {
		case OFF:
			SchedulerService.instance.stop(AuhtorizedKeysJob.JOB_NAME);
			break;
		case ONCE:
			SchedulerService.instance.stop(AuhtorizedKeysJob.JOB_NAME);
			SchedulerService.instance.executeOnce(AuhtorizedKeysJob.JOB_NAME);
			break;
		case REPEAT:
			if ((oldOptions == null) || (this.newOptions.getSshKeysTimer() != oldOptions.getSshKeysTimer()) || (this.newOptions.getSshKeysTimerUnit() != oldOptions.getSshKeysTimerUnit())) {
				SchedulerService.instance.resetTask(AuhtorizedKeysJob.JOB_NAME, this.newOptions.getSshKeysTimer(), this.newOptions.getSshKeysTimerUnit());
			}
			break;
		}

		// FILE MANAGEMENT
		switch (this.newOptions.getDoFileManagement()) {
		case OFF:
			SchedulerService.instance.stop(FilesJob.JOB_NAME);
			break;
		case ONCE:
			SchedulerService.instance.stop(FilesJob.JOB_NAME);
			SchedulerService.instance.executeOnce(FilesJob.JOB_NAME);
			break;
		case REPEAT:
			if ((oldOptions == null) || (this.newOptions.getFileManagementTimer() != oldOptions.getFileManagementTimer()) || (this.newOptions.getFileManagementTimerUnit() != oldOptions.getFileManagementTimerUnit())) {
				SchedulerService.instance.resetTask(FilesJob.JOB_NAME, this.newOptions.getFileManagementTimer(), this.newOptions.getFileManagementTimerUnit());
			}
			break;
		}

		// PACKAGE MANAGEMENT
		switch (this.newOptions.getDoPackageManagement()) {
		case OFF:
			SchedulerService.instance.stop(DefaultJob.JOB_NAME);
			break;
		case ONCE:
			SchedulerService.instance.stop(DefaultJob.JOB_NAME);
			SchedulerService.instance.executeOnce(DefaultJob.JOB_NAME);
			break;
		case REPEAT:
			if ((oldOptions == null) || (this.newOptions.getPackageManagementTimer() != oldOptions.getPackageManagementTimer()) || (this.newOptions.getPackageManagementTimerUnit() != oldOptions.getPackageManagementTimerUnit())) {
				SchedulerService.instance.resetTask(DefaultJob.JOB_NAME, this.newOptions.getPackageManagementTimer(), this.newOptions.getPackageManagementTimerUnit());
			}
			break;
		}
	}
}
