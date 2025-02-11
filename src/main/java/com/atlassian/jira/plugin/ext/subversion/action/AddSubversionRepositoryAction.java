package com.atlassian.jira.plugin.ext.subversion.action;

import com.atlassian.jira.plugin.ext.subversion.MultipleSubversionRepositoryManager;
import com.atlassian.jira.plugin.ext.subversion.SubversionManager;
import com.atlassian.jira.plugin.ext.subversion.SvnProperties;
import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.opensymphony.util.TextUtils;

@SupportedMethods({ RequestMethod.GET, RequestMethod.POST })
public class AddSubversionRepositoryAction extends SubversionActionSupport implements SvnProperties {
	private String root;
	private String displayName;
	private String username;
	private String password;
	private String privateKeyFile;
	private Boolean revisionIndexing = Boolean.TRUE;
	private Integer revisionCacheSize = new Integer(10000);
	private String webLinkType;
	private String viewFormat;
	private String changesetFormat;
	private String fileAddedFormat;
	private String fileModifiedFormat;
	private String fileReplacedFormat;
	private String fileDeletedFormat;

	public AddSubversionRepositoryAction(MultipleSubversionRepositoryManager manager) {
		super(manager);
	}

	@Override
	public void doValidation() {
		if (!TextUtils.stringSet(getDisplayName())) {
			addError("dipalyName", getText("subversion.errors.you.must.specify.a.name.for.the.repository"));
		}

		validateRepositoryParameters();
	}

	@Override
	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root != null ? root.trim() : root;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if (TextUtils.stringSet(username)) {
			this.username = username;
		} else {
			this.username = null;
		}
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (TextUtils.stringSet(password)) {
			this.password = password;
		} else {
			this.password = null;
		}
	}

	@Override
	public Boolean getRevisionIndexing() {
		return revisionIndexing;
	}

	public void setRevisionIndexing(Boolean revisionIndexing) {
		this.revisionIndexing = revisionIndexing;
	}

	@Override
	public Integer getRevisionCacheSize() {
		return revisionCacheSize;
	}

	public void setRevisionCacheSize(Integer revisionCacheSize) {
		this.revisionCacheSize = revisionCacheSize;
	}

	@Override
	public String getPrivateKeyFile() {
		return privateKeyFile;
	}

	public void setPrivateKeyFile(String privateKeyFile) {
		if (TextUtils.stringSet(privateKeyFile)) {
			this.privateKeyFile = privateKeyFile;
		} else {
			this.privateKeyFile = null;
		}
	}

	@Override
	public String getWebLinkType() {
		return webLinkType;
	}

	public void setWebLinkType(String webLinkType) {
		this.webLinkType = webLinkType;
	}

	@Override
	public String getChangesetFormat() {
		return changesetFormat;
	}

	public void setChangesetFormat(String changesetFormat) {
		if (TextUtils.stringSet(changesetFormat)) {
			this.changesetFormat = changesetFormat;
		} else {
			this.changesetFormat = null;
		}
	}

	@Override
	public String getFileAddedFormat() {
		return fileAddedFormat;
	}

	public void setFileAddedFormat(String fileAddedFormat) {
		if (TextUtils.stringSet(fileAddedFormat)) {
			this.fileAddedFormat = fileAddedFormat;
		} else {
			this.fileAddedFormat = null;
		}
	}

	@Override
	public String getViewFormat() {
		return viewFormat;
	}

	public void setViewFormat(String viewFormat) {
		if (TextUtils.stringSet(viewFormat)) {
			this.viewFormat = viewFormat;
		} else {
			this.viewFormat = null;
		}
	}

	@Override
	public String getFileModifiedFormat() {
		return fileModifiedFormat;
	}

	public void setFileModifiedFormat(String fileModifiedFormat) {
		if (TextUtils.stringSet(fileModifiedFormat)) {
			this.fileModifiedFormat = fileModifiedFormat;
		} else {
			this.fileModifiedFormat = null;
		}
	}

	@Override
	public String getFileReplacedFormat() {
		return fileReplacedFormat;
	}

	public void setFileReplacedFormat(String fileReplacedFormat) {
		if (TextUtils.stringSet(fileReplacedFormat)) {
			this.fileReplacedFormat = fileReplacedFormat;
		} else {
			this.fileReplacedFormat = null;
		}
	}

	@Override
	public String getFileDeletedFormat() {
		return fileDeletedFormat;
	}

	public void setFileDeletedFormat(String fileDeletedFormat) {
		if (TextUtils.stringSet(fileDeletedFormat)) {
			this.fileDeletedFormat = fileDeletedFormat;
		} else {
			this.fileDeletedFormat = null;
		}
	}

	@Override
	@SupportedMethods(RequestMethod.GET)
	public String doExecute() throws Exception {
		if (!hasPermissions()) {
			return PERMISSION_VIOLATION_RESULT;
		}

		SubversionManager subversionManager = getMultipleRepoManager().createRepository(this);
		if (!subversionManager.isActive()) {
			addErrorMessage(subversionManager.getInactiveMessage());
			addErrorMessage(getText("admin.errors.occured.when.creating"));
			getMultipleRepoManager().removeRepository(subversionManager.getId());
			return ERROR;
		}

		return getRedirect("ViewSubversionRepositories.jspa");
	}

	// This is public for testing purposes
	public void validateRepositoryParameters() {
		if (!TextUtils.stringSet(getDisplayName())) {
			addError("displayName", getText("subversion.errors.you.must.specify.a.name.for.the.repository"));
		}
		if (!TextUtils.stringSet(getRoot())) {
			addError("root", getText("subversion.errors.you.must.specify.the.root.of.the.repository"));
		}
	}

}
