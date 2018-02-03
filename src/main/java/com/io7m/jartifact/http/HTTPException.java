package com.io7m.jartifact.http;

import java.io.IOException;
import java.util.Objects;

public final class HTTPException extends IOException
{
  private final int code;
  private final String response;

  public int responseCode()
  {
    return this.code;
  }

  public String responseMessage()
  {
    return this.response;
  }

  public HTTPException(
    final String message,
    final int code,
    final String response)
  {
    super(message);
    this.code = code;
    this.response = Objects.requireNonNull(response, "response");
  }

  public HTTPException(
    final String message,
    final Throwable cause,
    final int code,
    final String response)
  {
    super(message, cause);
    this.code = code;
    this.response = Objects.requireNonNull(response, "response");
  }

  public HTTPException(
    final Throwable cause,
    final int code,
    final String response)
  {
    super(cause);
    this.code = code;
    this.response = Objects.requireNonNull(response, "response");
  }
}
