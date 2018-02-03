package com.io7m.jartifact.core;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionParser
{
  private interface ParserType
  {
    Version tryParse(String text)
      throws ParseException;
  }

  private static final class ParserMmPQ implements ParserType
  {
    private static final Pattern PATTERN =
      Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)-(.+)");

    ParserMmPQ()
    {

    }

    @Override
    public Version tryParse(final String text)
      throws ParseException
    {
      final Matcher matcher = PATTERN.matcher(text);
      if (matcher.matches()) {
        return Version.builder()
          .setMajor(Integer.parseInt(matcher.group(1)))
          .setMinor(Integer.parseInt(matcher.group(2)))
          .setPatch(Integer.parseInt(matcher.group(3)))
          .setQualifier(matcher.group(4))
          .build();
      }
      throw new ParseException(text, 0);
    }
  }

  private static final class ParserMmP implements ParserType
  {
    private static final Pattern PATTERN =
      Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)");

    ParserMmP()
    {

    }

    @Override
    public Version tryParse(final String text)
      throws ParseException
    {
      final Matcher matcher = PATTERN.matcher(text);
      if (matcher.matches()) {
        return Version.builder()
          .setMajor(Integer.parseInt(matcher.group(1)))
          .setMinor(Integer.parseInt(matcher.group(2)))
          .setPatch(Integer.parseInt(matcher.group(3)))
          .build();
      }
      throw new ParseException(text, 0);
    }
  }

  private static final class ParserMmQ implements ParserType
  {
    private static final Pattern PATTERN =
      Pattern.compile("([0-9]+)\\.([0-9]+)-(.+)");

    ParserMmQ()
    {

    }

    @Override
    public Version tryParse(final String text)
      throws ParseException
    {
      final Matcher matcher = PATTERN.matcher(text);
      if (matcher.matches()) {
        return Version.builder()
          .setMajor(Integer.parseInt(matcher.group(1)))
          .setMinor(Integer.parseInt(matcher.group(2)))
          .setQualifier(matcher.group(3))
          .build();
      }
      throw new ParseException(text, 0);
    }
  }

  private static final class ParserMm implements ParserType
  {
    private static final Pattern PATTERN =
      Pattern.compile("([0-9]+)\\.([0-9]+)");

    ParserMm()
    {

    }

    @Override
    public Version tryParse(final String text)
      throws ParseException
    {
      final Matcher matcher = PATTERN.matcher(text);
      if (matcher.matches()) {
        return Version.builder()
          .setMajor(Integer.parseInt(matcher.group(1)))
          .setMinor(Integer.parseInt(matcher.group(2)))
          .build();
      }
      throw new ParseException(text, 0);
    }
  }

  private static final class ParserMQ implements ParserType
  {
    private static final Pattern PATTERN =
      Pattern.compile("([0-9]+)-(.+)");

    ParserMQ()
    {

    }

    @Override
    public Version tryParse(final String text)
      throws ParseException
    {
      final Matcher matcher = PATTERN.matcher(text);
      if (matcher.matches()) {
        return Version.builder()
          .setMajor(Integer.parseInt(matcher.group(1)))
          .setQualifier(matcher.group(2))
          .build();
      }
      throw new ParseException(text, 0);
    }
  }

  private static final class ParserM implements ParserType
  {
    private static final Pattern PATTERN =
      Pattern.compile("([0-9]+)");

    ParserM()
    {

    }

    @Override
    public Version tryParse(final String text)
      throws ParseException
    {
      final Matcher matcher = PATTERN.matcher(text);
      if (matcher.matches()) {
        return Version.builder()
          .setMajor(Integer.parseInt(matcher.group(1)))
          .build();
      }
      throw new ParseException(text, 0);
    }
  }

  private static final List<ParserType> PARSERS = createParsers();

  private static List<ParserType> createParsers()
  {
    return List.of(
      new ParserMmPQ(),
      new ParserMmP(),
      new ParserMmQ(),
      new ParserMm(),
      new ParserMQ(),
      new ParserM());
  }

  private VersionParser()
  {

  }

  public static Version parseAny(final String text)
  {
    Objects.requireNonNull(text, "Text");

    for (final ParserType p : PARSERS) {
      try {
        return p.tryParse(text);
      } catch (final ParseException e) {
        continue;
      }
    }
    return Version.builder()
      .setQualifier(text)
      .build();
  }
}
