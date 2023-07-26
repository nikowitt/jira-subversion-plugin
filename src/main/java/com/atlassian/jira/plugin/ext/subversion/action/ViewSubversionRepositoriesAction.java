package com.atlassian.jira.plugin.ext.subversion.action;

import java.util.Collection;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

import com.atlassian.jira.plugin.ext.subversion.MultipleSubversionRepositoryManager;
import com.atlassian.jira.plugin.ext.subversion.SubversionManager;
import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.google.common.collect.Ordering;

/**
 * Manage 1 or more repositories
 */

public class ViewSubversionRepositoriesAction extends SubversionActionSupport {

	private static Comparator<SubversionManager> svnManagerComparator =
			new Comparator<SubversionManager>() {
				@Override
				public int compare(SubversionManager left, SubversionManager right) {
					return StringUtils.defaultString(left.getDisplayName()).compareTo(
							StringUtils.defaultString(right.getDisplayName())
					);
				}
			};

	public ViewSubversionRepositoriesAction(MultipleSubversionRepositoryManager manager) {
		super(manager);
	}

	public Collection<SubversionManager> getRepositories() {
		Collection<SubversionManager> repositoryList = getMultipleRepoManager().getRepositoryList();
		return Ordering.from(svnManagerComparator).sortedCopy(repositoryList);

	}

	@Override
	@SupportedMethods(RequestMethod.GET)
	public String doDefault() {
		return super.doDefault();
	}

	@Override
	@SupportedMethods({ RequestMethod.GET, RequestMethod.POST })
	public String execute() throws Exception {
		return super.execute();
	}

	@Override
	@SupportedMethods({ RequestMethod.GET, RequestMethod.POST })
	protected String doExecute() throws Exception {
		return super.doExecute();
	}
}
