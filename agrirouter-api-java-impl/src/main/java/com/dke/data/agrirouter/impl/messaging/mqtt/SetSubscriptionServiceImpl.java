package com.dke.data.agrirouter.impl.messaging.mqtt;

import com.dke.data.agrirouter.api.dto.encoding.EncodedMessage;
import com.dke.data.agrirouter.api.exception.CouldNotSendMqttMessageException;
import com.dke.data.agrirouter.api.service.LoggingEnabledService;
import com.dke.data.agrirouter.api.service.messaging.SetSubscriptionService;
import com.dke.data.agrirouter.api.service.messaging.encoding.EncodeMessageService;
import com.dke.data.agrirouter.api.service.parameters.SendMessageParameters;
import com.dke.data.agrirouter.api.service.parameters.SetSubscriptionParameters;
import com.dke.data.agrirouter.impl.messaging.MessageEncoder;
import com.dke.data.agrirouter.impl.messaging.MqttService;
import com.dke.data.agrirouter.impl.messaging.encoding.EncodeMessageServiceImpl;
import com.dke.data.agrirouter.impl.messaging.rest.MessageSender;
import com.dke.data.agrirouter.impl.validation.ResponseValidator;
import java.util.Collections;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SetSubscriptionServiceImpl extends MqttService
    implements SetSubscriptionService,
        MessageSender,
        MessageEncoder,
        ResponseValidator,
        LoggingEnabledService {
  private final EncodeMessageService encodeMessageService;

  public SetSubscriptionServiceImpl(IMqttClient mqttClient) {
    super(mqttClient);
    this.encodeMessageService = new EncodeMessageServiceImpl();
  }

  @Override
  public String send(SetSubscriptionParameters parameters) {
    parameters.validate();
    try {
      EncodedMessage encodedMessage = this.encodeMessage(parameters);
      SendMessageParameters sendMessageParameters = new SendMessageParameters();
      sendMessageParameters.setOnboardingResponse(parameters.getOnboardingResponse());
      sendMessageParameters.setEncodedMessages(
          Collections.singletonList(encodedMessage.getEncodedMessage()));
      String messageAsJson = this.createMessageBody(sendMessageParameters);
      this.getNativeLogger().info("SetSubscriptionServiceMessage: " + messageAsJson);
      byte[] payload = messageAsJson.getBytes();
      this.getMqttClient()
          .publish(
              parameters.getOnboardingResponse().getConnectionCriteria().getMeasures(),
              new MqttMessage(payload));
      return encodedMessage.getApplicationMessageID();
    } catch (MqttException e) {
      throw new CouldNotSendMqttMessageException(e);
    }
  }

  @Override
  public EncodeMessageService getEncodeMessageService() {
    return encodeMessageService;
  }
}