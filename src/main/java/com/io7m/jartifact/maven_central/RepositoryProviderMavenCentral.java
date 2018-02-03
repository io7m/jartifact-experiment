package com.io7m.jartifact.maven_central;

import com.io7m.jartifact.core.Artifact;
import com.io7m.jartifact.core.VersionParser;
import com.io7m.jartifact.http.HTTPData;
import com.io7m.jartifact.http.HTTPType;
import com.io7m.jartifact.spi.RepositoryException;
import com.io7m.jartifact.spi.RepositoryExceptionIO;
import com.io7m.jartifact.spi.RepositoryProviderType;
import com.io7m.jartifact.spi.RepositoryRemoteArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import static javax.xml.xpath.XPathConstants.NODESET;

public final class RepositoryProviderMavenCentral implements
  RepositoryProviderType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(RepositoryProviderMavenCentral.class);

  private final XPath xpath;
  private final XPathExpression xpath_documents_expr;
  private final XPathExpression xpath_artifact_expr;
  private final XPathExpression xpath_group_expr;
  private final XPathExpression xpath_version_expr;
  private final XPathExpression xpath_package_expr;
  private final HTTPType http;

  private RepositoryProviderMavenCentral(final HTTPType in_http)
  {
    this.http = Objects.requireNonNull(in_http, "http");

    try {
      this.xpath =
        XPathFactory.newInstance().newXPath();
      this.xpath_documents_expr =
        this.xpath.compile("/response/result/doc");
      this.xpath_artifact_expr =
        this.xpath.compile("str[@name='a']/text()");
      this.xpath_group_expr =
        this.xpath.compile("str[@name='g']/text()");
      this.xpath_version_expr =
        this.xpath.compile("str[@name='v']/text()");
      this.xpath_package_expr =
        this.xpath.compile("str[@name='p']/text()");
    } catch (final XPathExpressionException e) {
      throw new IllegalStateException(e);
    }
  }

  public static RepositoryProviderType provider()
  {
    return new RepositoryProviderMavenCentral(
      ServiceLoader.load(HTTPType.class)
        .findFirst()
        .orElseThrow(() -> {
          throw new ServiceConfigurationError("No provider available for " + HTTPType.class);
        }));
  }

  public static RepositoryProviderType create(final HTTPType http)
  {
    return new RepositoryProviderMavenCentral(http);
  }

  @Override
  public List<Artifact> artifactVersionsAvailable(
    final String group,
    final String artifact)
    throws RepositoryException
  {
    Objects.requireNonNull(group, "group");
    Objects.requireNonNull(artifact, "artifact");

    try {
      final URL url = new URL(
        new StringBuilder(128)
          .append("http://search.maven.org/solrsearch/select?wt=xml&")
          .append("q=")
          .append("g:")
          .append("%22")
          .append(group)
          .append("%22+AND+a:%22")
          .append(artifact)
          .append("%22&core=gav")
          .toString());

      LOG.debug("GET {}", url);

      final HTTPData data = this.http.get(url);
      try (InputStream stream = data.stream()) {
        return this.parseArtifactsFromStream(stream);
      }
    } catch (final IOException e) {
      throw new RepositoryExceptionIO(e);
    } catch (final SAXException | ParserConfigurationException | XPathExpressionException e) {
      throw new RepositoryExceptionIO(new IOException(e));
    }
  }

  @Override
  public RepositoryRemoteArtifact artifactFile(final Artifact artifact)
    throws RepositoryException
  {
    Objects.requireNonNull(artifact, "artifact");

    try {
      final URL url = new URL(
        new StringBuilder(128)
          .append("http://search.maven.org/remotecontent?filepath=")
          .append(artifact.group().replace('.', '/'))
          .append("/")
          .append(artifact.artifact())
          .append("/")
          .append(artifact.versionRaw())
          .append("/")
          .append(artifact.artifact())
          .append("-")
          .append(artifact.versionRaw())
          .append(".")
          .append(typeSuffix(artifact.type()))
          .toString());

      LOG.debug("GET {}", url);

      final HTTPData data = this.http.get(url);
      return RepositoryRemoteArtifact.create(
        data.size(), data.contentType(), data.stream());

    } catch (final IOException e) {
      throw new RepositoryExceptionIO(e);
    }
  }

  private static String typeSuffix(final String type)
  {
    switch (type) {
      case "bundle": return "jar";
      default: return type;
    }
  }

  private List<Artifact> parseArtifactsFromStream(final InputStream stream)
    throws
    ParserConfigurationException,
    SAXException,
    IOException,
    XPathExpressionException
  {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    final DocumentBuilder builder = factory.newDocumentBuilder();
    final Document doc = builder.parse(stream);

    final Object result =
      this.xpath_documents_expr.evaluate(doc, NODESET);
    final NodeList doc_nodes = (NodeList) result;

    final ArrayList<Artifact> artifacts =
      new ArrayList<>(doc_nodes.getLength());

    for (int i = 0; i < doc_nodes.getLength(); i++) {
      final Node doc_node = doc_nodes.item(i);
      final NodeList a_result =
        (NodeList) this.xpath_artifact_expr.evaluate(doc_node, NODESET);
      final NodeList g_result =
        (NodeList) this.xpath_group_expr.evaluate(doc_node, NODESET);
      final NodeList v_result =
        (NodeList) this.xpath_version_expr.evaluate(doc_node, NODESET);
      final NodeList p_result =
        (NodeList) this.xpath_package_expr.evaluate(doc_node, NODESET);

      final String a = a_result.item(0).getNodeValue();
      final String g = g_result.item(0).getNodeValue();
      final String v = v_result.item(0).getNodeValue();
      final String p = p_result.item(0).getNodeValue();

      artifacts.add(
        Artifact.builder()
          .setArtifact(a)
          .setGroup(g)
          .setType(p)
          .setVersion(VersionParser.parseAny(v))
          .setVersionRaw(v)
          .build());
    }

    return artifacts;
  }
}
