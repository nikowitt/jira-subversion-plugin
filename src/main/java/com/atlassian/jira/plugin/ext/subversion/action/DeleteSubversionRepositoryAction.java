package com.atlassian.jira.plugin.ext.subversion.action;

import com.atlassian.jira.plugin.ext.subversion.MultipleSubversionRepositoryManager;
import com.atlassian.jira.plugin.ext.subversion.SubversionManager;
import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;

public class DeleteSubversionRepositoryAction extends SubversionActionSupport {
	private long repoId;
	private SubversionManager subversionManager;

	public DeleteSubversionRepositoryAction(MultipleSubversionRepositoryManager manager) {
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
	public String doDefault() {
		if (!hasPermissions()) {
			return PERMISSION_VIOLATION_RESULT;
		}

		subversionManager = getMultipleRepoManager().getRepository(repoId);
		return INPUT;
	}

	@Override
	@SupportedMethods({ RequestMethod.GET, RequestMethod.POST })
	public String doExecute() {
		if (!hasPermissions()) {
			return PERMISSION_VIOLATION_RESULT;
		}

		getMultipleRepoManager().removeRepository(repoId);
		return getRedirect("ViewSubversionRepositories.jspa");
	}

	public SubversionManager getSubversionManager() {
		return subversionManager;
	}
}
