package com.dke.data.agrirouter.impl.messaging.rest;

import com.dke.data.agrirouter.api.service.messaging.SendMessageService;
import com.dke.data.agrirouter.api.service.parameters.SendMessageParameters;
import com.dke.data.agrirouter.impl.common.MessageIdService;
import com.dke.data.agrirouter.impl.validation.ResponseValidator;
import org.apache.http.HttpStatus;

public class SendMessageServiceImpl
    implements SendMessageService, ResponseValidator, MessageSender {

  @Override
  public String send(SendMessageParameters parameters) {
    parameters.validate();

    if (parameters.getApplicationMessageID().equals("")) {
      parameters.setApplicationMessageID(MessageIdService.generateMessageId());
    }

    MessageSenderResponse response = this.sendMessage(parameters);
    this.assertResponseStatusIsValid(response.getNativeResponse(), HttpStatus.SC_OK);
    return parameters.getApplicationMessageID();
  }
}
