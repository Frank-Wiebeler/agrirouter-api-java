package com.dke.data.agrirouter.api.service.messaging.encoding;

import com.dke.data.agrirouter.api.service.parameters.MessageHeaderParameters;
import com.dke.data.agrirouter.api.service.parameters.PayloadParameters;

/** Encoding of messages. */
public interface EncodeMessageService {

  /**
   * Encode a given message using the internal protobuf encoding mechanism.
   *
   * @param messageHeaderParameters -
   * @param payloadParameters -
   * @return -
   */
  String encode(
      MessageHeaderParameters messageHeaderParameters, PayloadParameters payloadParameters);
}
