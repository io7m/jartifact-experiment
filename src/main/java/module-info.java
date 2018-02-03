import com.io7m.jartifact.http.HTTPDefault;
import com.io7m.jartifact.maven_central.RepositoryProviderMavenCentral;

module com.io7m.jartifact
{
  requires auto.value;
  requires org.slf4j;
  requires java.xml;

  exports com.io7m.jartifact.spi;
  exports com.io7m.jartifact.core;

  provides com.io7m.jartifact.spi.RepositoryProviderType
    with RepositoryProviderMavenCentral;

  provides com.io7m.jartifact.http.HTTPType
    with HTTPDefault;

  uses com.io7m.jartifact.http.HTTPType;
}