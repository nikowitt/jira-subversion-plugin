Notes:
I forked this to provide support for JIRA 9 for this plugin. There are really nasty hacks and it doesn't adhere to any coding
standards, I just needed a running solution.

Working
-----------
**SVN tab in issues**

Not working
-----------
**Administer repositories**: Even though @SupportedMethods is set on e.g. ViewSubversionRepositories, JIRA still refuses to open this,
claiming the method /secure/ViewSubversionRepositories!default.jspa; user:(...) /secure/ViewSubversionRepositories!
default.jspa [c.a.j.web.dispatcher.JiraWebworkActionDispatcher] Action 'com.atlassian.jira.plugin.ext.subversion.action.ViewSubversionRepositoriesAction!
execute' does not support 'GET' request method.

So in order to use the administration, you need to disable "jira.webactions.request.method.recognition.disabled" as described
in https://confluence.atlassian.com/jiracore/preparing-for-jira-9-0-1115661092.html

**Maven integration**, therefore I patched and attached the JAR file.

Support
-----------
Plugin is unsupported by Atlassian
