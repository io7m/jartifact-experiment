package com.io7m.jartifact.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class HTTPDefault implements HTTPType
{
  public HTTPDefault()
  {

  }

  @Override
  public HTTPData get(final URL url)
    throws HTTPException
  {
    try {
      final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setInstanceFollowRedirects(true);
      connection.setRequestMethod("GET");
      connection.setRequestProperty("User-Agent", "Callisto");

      final int code = connection.getResponseCode();
      if (code >= 400) {
        final String message = connection.getResponseMessage();
        throw new HTTPException(
          new StringBuilder(128)
            .append("GET failed: ")
            .append(code)
            .append(" ")
            .append(message)
            .toString(), code, message);
      }

      return HTTPData.create(
        connection.getContentLengthLong(),
        connection.getContentType(),
        connection.getInputStream());
    } catch (final HTTPException e) {
      throw e;
    } catch (final IOException e) {
      throw new HTTPException(e.getCause(), -1, "");
    }
  }
}
