package com.io7m.jartifact.http;

import com.google.auto.value.AutoValue;

import java.io.InputStream;

@AutoValue
public abstract class HTTPData
{
  HTTPData()
  {

  }

  public abstract long size();

  public abstract String contentType();

  public abstract InputStream stream();

  public static HTTPData create(
    final long size,
    final String type,
    final InputStream stream)
  {
    return new AutoValue_HTTPData(size, type, stream);
  }
}
