package de.cinovo.cloudconductor.agent.helper;

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;

public class RepoTypeHelper {

	private RepoTypeHelper() {
		// prevent initialization
	}
	
	public static RepoType getRepoType() throws CloudConductorException {
		String repoType = System.getProperty(AgentVars.REPO_TYPE_PROP);
		switch (repoType) {
		case "yum":
			return RepoType.YUM;
		case "deb":
			return RepoType.DEB;
		default:
			throw new CloudConductorException("CloudConductor cannot handle this repo type");
		}
	}
}
