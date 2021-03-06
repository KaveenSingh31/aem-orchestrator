package com.shinesolutions.aemorchestrator.service;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.shinesolutions.aemorchestrator.handler.SqsMessageHandler;

/**
 * Listener for the SQS queue. Will add itself as a message listener upon startup
 * and then start the connection.
 * 
 * When a message is received, it will pass it to the @see MessageHandler
 */
@Component
public class OrchestratorMessageListener implements MessageListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private SQSConnection connection;

	@Resource
	private MessageConsumer consumer;

	@Resource
	private SqsMessageHandler messageHandler;

	@Override
	public void onMessage(Message message) {
		try {
			if (message != null) {
				logger.info("Message received " + message.getJMSMessageID());

				boolean removeMessageFromQueue = messageHandler.handleMessage(message);
				
				//Acknowledging the message with remove it from the queue
				if(removeMessageFromQueue) {
				    logger.info("Acknowledged message (removing from queue): " + message.getJMSMessageID());
				    message.acknowledge();
				} else {
				    logger.info("Leaving message " + message.getJMSMessageID() + " on the queue");
				}
			} else {
				logger.info("Null message received");
			}

		} catch (Exception e) {
			logger.error("Error while recieving message", e);
		}
	}

	public void start() throws Exception {
		logger.debug("Initialising message receiver, starting SQS connection");
		consumer.setMessageListener(this);
		connection.start();
	}

	@PreDestroy
	public void cleanUp() throws Exception {
		logger.debug("Destroying message receiver, stopping SQS connection");
		connection.stop();
	}

}
