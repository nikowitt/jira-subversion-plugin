package com.atlassian.jira.plugin.ext.subversion.action;

import com.atlassian.jira.plugin.ext.subversion.MultipleSubversionRepositoryManager;
import com.atlassian.jira.plugin.ext.subversion.SubversionManager;
import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;

public class ActivateSubversionRepositoryAction extends SubversionActionSupport {
	private long repoId;
	private SubversionManager subversionManager;

	public ActivateSubversionRepositoryAction(MultipleSubversionRepositoryManager manager) {
		super(manager);
	}

	public String getRepoId() {
		return Long.toString(repoId);
	}

	public void setRepoId(String repoId) {
		this.repoId = Long.parseLong(repoId);
	}

	@Override
	@SupportedMethods(RequestMethod.GET)
	public String doExecute() {
		if (!hasPermissions()) {
			return PERMISSION_VIOLATION_RESULT;
		}

		subversionManager = getMultipleRepoManager().getRepository(repoId);
		subversionManager.activate();
		if (!subversionManager.isActive()) {
			addErrorMessage(getText("subversion.repository.activation.failed", subversionManager.getInactiveMessage()));
		}
		return SUCCESS;
	}

	public SubversionManager getSubversionManager() {
		return subversionManager;
	}

}
