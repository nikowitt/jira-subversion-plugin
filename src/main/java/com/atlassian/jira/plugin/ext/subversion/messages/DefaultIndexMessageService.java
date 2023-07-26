package com.atlassian.jira.plugin.ext.subversion.messages;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import com.atlassian.cache.CachedReference;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.jira.cluster.ClusterMessagingService;
import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.plugin.ext.subversion.SubversionManager;
import com.atlassian.jira.plugin.ext.subversion.revisions.RevisionIndexer;

/**
 * @since v2.0
 */
public class DefaultIndexMessageService implements IndexMessageService {

	private static final Logger logger = LoggerFactory.getLogger(DefaultIndexMessageService.class);

	private static final String INDEX_MESSAGE_CHANNEL = "INDEX_CHANNEL";

	private final ClusterMessagingService clusterMessagingService;
	private final IndexMessageConsumer messageConsumer;
	private final RevisionIndexer revisionIndexer;
	private final CachedReference<Map<Long, SubversionManager>> cachedSvnManagers;

	public DefaultIndexMessageService(ClusterMessagingService clusterMessagingService, RevisionIndexer revisionIndexer,
			CachedReference<Map<Long, SubversionManager>> cachedSvnManagers) {
		this.clusterMessagingService = clusterMessagingService;
		this.revisionIndexer = revisionIndexer;
		messageConsumer = new IndexMessageConsumer();
		this.cachedSvnManagers = cachedSvnManagers;
	}

	private enum IndexMessageType {
		ADD_INDEX,
		REMOVE_INDEX;

		public void callIndexAction(DefaultIndexMessageService indexService, ParsedIndexMessage parsedIndexMessage)
				throws SVNException, IndexException, IOException {
			Long repositoryId = Long.valueOf(parsedIndexMessage.getMessageParam());
			switch (this) {
			case ADD_INDEX:
				indexService.invalidateCacheWhenRepositoryNotFound(repositoryId);
				indexService.revisionIndexer.addRepository(repositoryId);
				break;
			case REMOVE_INDEX:
				indexService.invalidateCacheWhenRepositoryNotFound(repositoryId);
				indexService.revisionIndexer.removeEntries(Long.valueOf(parsedIndexMessage.getMessageParam()));
				break;
			}
		}

	}

	@Override
	public void removeIndexForRepository(long repositoryId) {
		clusterMessagingService.sendRemote(INDEX_MESSAGE_CHANNEL, createMessage(IndexMessageType.REMOVE_INDEX, repositoryId));
	}

	@Override
	public void addIndexForRepository(long repositoryId) {
		clusterMessagingService.sendRemote(INDEX_MESSAGE_CHANNEL, createMessage(IndexMessageType.ADD_INDEX, repositoryId));
	}

	@Override
	public void registerListeners() {
		if (logger.isInfoEnabled()) {
			logger.info("Registered IndexMessageConsumer: " + messageConsumer);
		}
		clusterMessagingService.registerListener(INDEX_MESSAGE_CHANNEL, messageConsumer);
	}

	@Override
	public void unregisterListeners() {
		if (logger.isInfoEnabled()) {
			logger.info("Unregistered IndexMessageConsumer: " + messageConsumer);
		}
		clusterMessagingService.unregisterListener(INDEX_MESSAGE_CHANNEL, messageConsumer);
	}

	private class IndexMessageConsumer implements com.atlassian.jira.cluster.ClusterMessageConsumer {

		@Override
		public void receive(String channel, String message, String sender) {
			if (logger.isDebugEnabled()) {
				logger.debug("Received message: " + message + " in channel: " + channel + " from sender: " + sender);
			}
			if (INDEX_MESSAGE_CHANNEL.equals(channel)) {

				ParsedIndexMessage parsedIndexMessage = parseClusterMessage(message);

				try {
					parsedIndexMessage.getIndexMessageType().callIndexAction(DefaultIndexMessageService.this, parsedIndexMessage);
				} catch (Exception e) {
					throw new InfrastructureException("Could not update repository index", e);
				}
			}
		}
	}

	private class ParsedIndexMessage {

		private IndexMessageType indexMessageType;
		private String messageParam;

		private ParsedIndexMessage(String messageType, String messageParam) {
			indexMessageType = IndexMessageType.valueOf(messageType);
			this.messageParam = messageParam;
		}

		private IndexMessageType getIndexMessageType() {
			return indexMessageType;
		}

		private String getMessageParam() {
			return messageParam;
		}
	}

	private String createMessage(IndexMessageType messagesType, long repositoryId) {
		return messagesType.name() + "|" + repositoryId;
	}

	private ParsedIndexMessage parseClusterMessage(String message) {
		if (StringUtils.isBlank(message)) {
			throw new IllegalArgumentException("Indexing message can't be blank: '" + message + "'");
		}

		String[] tokens = message.split("\\|");
		if (tokens.length < 2 || StringUtils.isBlank(tokens[0]) || StringUtils.isBlank(tokens[1])) {
			throw new IllegalArgumentException("Invalid indexing message: '" + message + "'");
		}

		return new ParsedIndexMessage(tokens[0], tokens[1]);
	}

	/**
	 * This is protection against the race which may happen when cache is invalidated and message
	 * is sent to reindex repository. This may cause we will index repository using old list of
	 * repositories which will not include the one we have just added.
	 *
	 * @param repositoryId
	 */
	private void invalidateCacheWhenRepositoryNotFound(long repositoryId) {
		if (cachedSvnManagers.get() != null && cachedSvnManagers.get().get(repositoryId) == null) {
			cachedSvnManagers.reset();
		}
	}

}
