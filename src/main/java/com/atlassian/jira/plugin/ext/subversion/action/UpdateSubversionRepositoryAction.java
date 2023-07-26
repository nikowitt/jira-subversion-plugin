package com.atlassian.jira.plugin.ext.subversion.action;

import com.atlassian.jira.plugin.ext.subversion.MultipleSubversionRepositoryManager;
import com.atlassian.jira.plugin.ext.subversion.SubversionManager;
import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;

public class UpdateSubversionRepositoryAction extends AddSubversionRepositoryAction {
	private long repoId = -1;

	public UpdateSubversionRepositoryAction(MultipleSubversionRepositoryManager multipleRepoManager) {
		super(multipleRepoManager);
	}

	@Override
	@SupportedMethods(RequestMethod.GET)
	public String doDefault() {
		if (ERROR.equals(super.doDefault())) {
			return ERROR;
		}

		if (!hasPermissions()) {
			return PERMISSION_VIOLATION_RESULT;
		}

		if (repoId == -1) {
			addErrorMessage(getText("subversion.repository.id.missing"));
			return ERROR;
		}

		// Retrieve the cvs repository
		SubversionManager repository = getMultipleRepoManager().getRepository(repoId);
		if (repository == null) {
			addErrorMessage(getText("subversion.repository.does.not.exist", Long.toString(repoId)));
			return ERROR;
		}

		setDisplayName(repository.getDisplayName());
		setRoot(repository.getRoot());
		if (repository.getViewLinkFormat() != null) {
			setWebLinkType(repository.getViewLinkFormat().getType());
			setChangesetFormat(repository.getViewLinkFormat().getChangesetFormat());
			setViewFormat(repository.getViewLinkFormat().getViewFormat());
			setFileAddedFormat(repository.getViewLinkFormat().getFileAddedFormat());
			setFileDeletedFormat(repository.getViewLinkFormat().getFileDeletedFormat());
			setFileModifiedFormat(repository.getViewLinkFormat().getFileModifiedFormat());
			setFileReplacedFormat(repository.getViewLinkFormat().getFileReplacedFormat());
		}
		setUsername(repository.getUsername());
		setPassword(repository.getPassword());
		setPrivateKeyFile(repository.getPrivateKeyFile());
		setRevisionCacheSize(new Integer(repository.getRevisioningCacheSize()));
		setRevisionIndexing(true);

		return INPUT;
	}

	@Override
	@SupportedMethods({ RequestMethod.GET, RequestMethod.POST })
	public String doExecute() {
		if (!hasPermissions()) {
			addErrorMessage(getText("subversion.admin.privilege.required"));
			return ERROR;
		}

		if (repoId == -1) {
			return getRedirect("ViewSubversionRepositories.jspa");
		}

		SubversionManager subversionManager = getMultipleRepoManager().updateRepository(repoId, this);
		if (!subversionManager.isActive()) {
			repoId = subversionManager.getId();
			addErrorMessage(subversionManager.getInactiveMessage());
			addErrorMessage(getText("admin.errors.occured.when.updating"));
			return ERROR;
		}
		return getRedirect("ViewSubversionRepositories.jspa");
	}

	public long getRepoId() {
		return repoId;
	}

	public void setRepoId(long repoId) {
		this.repoId = repoId;
	}

}
