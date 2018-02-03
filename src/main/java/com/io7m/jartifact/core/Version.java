package com.io7m.jartifact.core;

import com.google.auto.value.AutoValue;

import java.util.Comparator;

@AutoValue
public abstract class Version implements Comparable<Version>
{
  Version()
  {

  }

  @Override
  public final int compareTo(final Version other)
  {
    return Comparator.comparingInt(Version::major)
      .thenComparingInt(Version::minor)
      .thenComparingInt(Version::patch)
      .thenComparing(Version::qualifier)
      .compare(this, other);
  }

  @Override
  public final String toString()
  {
    return new StringBuilder(32)
      .append(this.major())
      .append(".")
      .append(this.minor())
      .append(".")
      .append(this.patch())
      .append(!this.qualifier().isEmpty() ? "-" + this.qualifier() : "")
      .toString();
  }

  public abstract int major();

  public abstract int minor();

  public abstract int patch();

  public abstract String qualifier();

  public static Builder builder()
  {
    final Builder builder = new AutoValue_Version.Builder();
    builder.setMajor(0);
    builder.setMinor(0);
    builder.setPatch(0);
    builder.setQualifier("");
    return builder;
  }

  @AutoValue.Builder
  public static abstract class Builder
  {
    Builder()
    {

    }

    abstract Version autoBuild();

    public abstract Builder setMajor(int v);

    public abstract Builder setMinor(int v);

    public abstract Builder setPatch(int v);

    public abstract Builder setQualifier(String q);

    public final Version build()
    {
      final Version v = this.autoBuild();
      if (v.major() < 0) {
        throw new IllegalArgumentException(
          String.format(
            "Major version %d must be in the range [0, %d]",
            Integer.valueOf(v.major()),
            Integer.valueOf(Integer.MAX_VALUE)));
      }
      if (v.minor() < 0) {
        throw new IllegalArgumentException(
          String.format(
            "Minor version %d must be in the range [0, %d]",
            Integer.valueOf(v.minor()),
            Integer.valueOf(Integer.MAX_VALUE)));
      }
      if (v.patch() < 0) {
        throw new IllegalArgumentException(
          String.format(
            "Patch version %d must be in the range [0, %d]",
            Integer.valueOf(v.patch()),
            Integer.valueOf(Integer.MAX_VALUE)));
      }

      return v;
    }
  }
}
