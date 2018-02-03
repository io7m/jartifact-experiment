package com.io7m.jartifact.spi;

import java.io.IOException;

public final class RepositoryExceptionIO extends RepositoryException
{
  public RepositoryExceptionIO(final IOException cause)
  {
    super(cause);
  }
}
