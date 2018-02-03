package com.io7m.jartifact.http;

import java.io.InputStream;
import java.net.URL;

public interface HTTPType
{
  HTTPData get(URL url)
    throws HTTPException;
}
