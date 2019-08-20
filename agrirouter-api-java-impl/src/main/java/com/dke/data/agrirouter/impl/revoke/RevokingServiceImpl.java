package com.dke.data.agrirouter.impl.revoke;

import com.dke.data.agrirouter.api.dto.revoke.RevokeRequest;
import com.dke.data.agrirouter.api.dto.revoke.RevokingError;
import com.dke.data.agrirouter.api.enums.RevokeResponse;
import com.dke.data.agrirouter.api.env.Environment;
import com.dke.data.agrirouter.api.exception.UnexpectedHttpStatusException;
import com.dke.data.agrirouter.api.service.onboard.RevokingService;
import com.dke.data.agrirouter.api.service.parameters.RevokeParameters;
import com.dke.data.agrirouter.impl.EnvironmentalService;
import com.dke.data.agrirouter.impl.RequestFactory;
import com.dke.data.agrirouter.impl.SignatureService;
import com.dke.data.agrirouter.impl.common.UtcTimeService;
import com.google.gson.Gson;
import java.util.Optional;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Hex;

public class RevokingServiceImpl extends EnvironmentalService
    implements RevokingService, SignatureService {

  private String lastError;

  public RevokingServiceImpl(Environment environment) {
    super(environment);
  }

  @Override
  public RevokeResponse revoke(RevokeParameters revokeParameters) {
    revokeParameters.validate();
    this.lastError = "";
    Response response = null;
    RevokeRequest revokeRequest = createRevokeRequestBody(revokeParameters);
    Gson gson = new Gson();
    String jsonBody = gson.toJson(revokeRequest).replace("\n", "");
    String encodedSignature = this.createSignature(revokeParameters, jsonBody);
    this.verifySignature(jsonBody, decodeHex(encodedSignature), revokeParameters.getPublicKey());

    try {
      response =
          RequestFactory.signedDeleteRequest(
                  environment.getRevokeUrl(), revokeParameters.getApplicationId(), encodedSignature)
              .build("DELETE", Entity.entity(jsonBody, MediaType.APPLICATION_JSON_TYPE))
              .invoke();

      response.bufferEntity();
      this.lastError = response.readEntity(String.class);

      RevokeResponse result = RevokeResponse.Filter.valueOf(response.getStatus());
      if (result.getKey() == RevokeResponse.SUCCESS.getKey()) {
        this.lastError = "";
        return result;
      } else {
        throw new UnexpectedHttpStatusException(
            response.getStatus(), RevokeResponse.SUCCESS.getKey());
      }
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  private String createSignature(RevokeParameters revokeParameters, String jsonBody) {
    byte[] signature = this.createSignature(jsonBody, revokeParameters.getPrivateKey());
    String encodedSignature = Hex.encodeHexString(signature);
    return encodedSignature;
  }

  private RevokeRequest createRevokeRequestBody(RevokeParameters parameters) {
    this.getNativeLogger().info("BEGIN | Create revoking request. | '{}'.", parameters);
    RevokeRequest revokeRequest = new RevokeRequest();
    revokeRequest.setAccountId(parameters.getAccountId());
    revokeRequest.setEndpointIds(parameters.getEndpointIds().toArray(new String[] {}));
    revokeRequest.setUTCTimestamp(UtcTimeService.inThePast(10).toString());
    revokeRequest.setTimeZone(UtcTimeService.offset());
    this.getNativeLogger().info("END | Create revoking request. | '{}'.", parameters);
    return revokeRequest;
  }

  public String getLastErrorAsString() {
    return this.lastError;
  }

  public Optional<RevokingError> getLastError() {
    Gson gson = new Gson();
    if (this.lastError.equals("")) {
      return Optional.empty();
    } else {
      return Optional.of(gson.fromJson(this.lastError, RevokingError.class));
    }
  }
}
