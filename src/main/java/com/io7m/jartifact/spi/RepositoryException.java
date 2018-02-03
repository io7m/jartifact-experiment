package com.io7m.jartifact.spi;

public abstract class RepositoryException extends Exception
{
  public RepositoryException(final String message)
  {
    super(message);
  }

  public RepositoryException(
    final String message,
    final Throwable cause)
  {
    super(message, cause);
  }

  public RepositoryException(final Throwable cause)
  {
    super(cause);
  }
}
