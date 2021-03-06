package com.dke.data.agrirouter.impl.validation;

import com.dke.data.agrirouter.api.exception.ForbiddenRequestException;
import com.dke.data.agrirouter.api.exception.InvalidUrlForRequestException;
import com.dke.data.agrirouter.api.exception.UnauthorizedRequestException;
import com.dke.data.agrirouter.api.exception.UnexpectedHttpStatusException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResponseValidatorTest implements ResponseValidator {

  @Test
  void givenValidUrlAssertResponseStatusIsValidShouldThrowNoException() throws Exception {
    Response getResponse =
        Response.status(this.openConnection("https://exme.net/").getResponseCode()).build();
    this.assertResponseStatusIsValid(getResponse, HttpStatus.SC_OK);
  }

  @Test
  void
      givenValidUrlAssertResponseStatusIsValidShouldThrowUnexpectedHttpStatusExceptionIfResponseStatusDoesNotMatch()
          throws Exception {
    Response getResponse =
        Response.status(this.openConnection("https://exme.net/").getResponseCode()).build();
    Assertions.assertThrows(
        UnexpectedHttpStatusException.class,
        () -> this.assertResponseStatusIsValid(getResponse, HttpStatus.SC_CREATED));
  }

  @Test
  void givenInvalidUrlAssertResponseStatusIsValidShouldThrowUnauthorizedRequestException()
      throws Exception {
    Response getResponse =
        Response.status(this.openConnection("https://exme.net/401").getResponseCode()).build();
    Assertions.assertThrows(
        UnauthorizedRequestException.class,
        () -> this.assertResponseStatusIsValid(getResponse, HttpStatus.SC_NOT_FOUND));
  }

  @Test
  void givenInvalidUrlAssertResponseStatusIsValidShouldThrowForbiddenRequestException()
      throws Exception {
    Response getResponse =
        Response.status(this.openConnection("https://exme.net/403").getResponseCode()).build();
    Assertions.assertThrows(
        ForbiddenRequestException.class,
        () -> this.assertResponseStatusIsValid(getResponse, HttpStatus.SC_NOT_FOUND));
  }

  @Test
  void givenInvalidUrlAssertResponseStatusIsValidShouldThrowInvalidUrlForRequestException()
      throws Exception {
    Response getResponse =
        Response.status(this.openConnection("https://exme.net/404").getResponseCode()).build();
    Assertions.assertThrows(
        InvalidUrlForRequestException.class,
        () -> this.assertResponseStatusIsValid(getResponse, HttpStatus.SC_NOT_FOUND));
  }

  private HttpURLConnection openConnection(String spec) throws Exception {
    URL url = new URL(spec);
    return (HttpURLConnection) url.openConnection();
  }
}
