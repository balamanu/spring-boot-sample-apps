package com.elan.sample.app.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.container.AsyncResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elan.sample.app.api.ChatMessage;
import com.elan.sample.app.rest.context.GetMessagesRequestContext;
import com.elan.sample.app.rest.handler.ResponseHandler;
import com.elan.sample.app.store.mongo.model.Message;
import com.elan.sample.app.store.mongo.repository.messages.MessageRepository;

@Service
public class MessageService {

	@Autowired
	private MessageRepository messageRepo;

	@Autowired
	private ResponseHandler responseHandler;

	/**
	 * Transforms the chat message (API model) into the DB model and stores in Mongo
	 * 
	 * @param message The chat message payload
	 */
	public void saveMessage(ChatMessage message) {
		Message dbMessage = new Message();
		dbMessage.setSenderUserId(message.getSenderUserId());
		dbMessage.setText(message.getMessage());
		dbMessage.setReceiverUserId(message.getReceiverUserId());
		dbMessage.setMessageSentDate(LocalDate.now());
		dbMessage.setRead(false);
		messageRepo.save(dbMessage);
	}
	
	/**
	 * Retrieves messages from the data store for the user and with the given filter params.
	 * 
	 * @param requestContext Context holding details related to message retrival
	 * @param response Jersey provided ASYNC response object
	 */
	public void getMessages(GetMessagesRequestContext requestContext, AsyncResponse response) {

		List<Message> chatMessage = new ArrayList<>();

		switch (requestContext.getFilterType()) {
		case ALL:

			chatMessage = messageRepo.findByReceiverUserIdOrSenderUserId(requestContext.getUserId());

			break;

		case SENT:
			chatMessage = messageRepo.findBySenderUserId(requestContext.getUserId());

			break;

		case RECEIVED:

			chatMessage = messageRepo.findByReceiverUserId(requestContext.getUserId());

			break;
		}

		responseHandler.handleSuccessResponse(chatMessage, response);
		return;

	}

}
