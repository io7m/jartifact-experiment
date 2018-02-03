package com.io7m.jartifact.core;

import com.google.auto.value.AutoValue;

import java.util.Objects;
import java.util.Optional;

@AutoValue
public abstract class VersionRange
{
  VersionRange()
  {

  }

  public abstract Optional<Version> lower();

  public abstract boolean lowerInclusive();

  public abstract Optional<Version> upper();

  public abstract boolean upperInclusive();

  @Override
  public final String toString()
  {
    return new StringBuilder(32)
      .append(this.lowerInclusive() ? "[" : "(")
      .append(this.lower().map(Version::toString).orElse(""))
      .append(",")
      .append(this.upper().map(Version::toString).orElse(""))
      .append(this.upperInclusive() ? "]" : ")")
      .toString();
  }

  public static Builder builder()
  {
    final Builder builder = new AutoValue_VersionRange.Builder();
    builder.setLower(Version.builder().build());
    builder.setLowerInclusive(true);
    builder.setUpper(Version.builder().build());
    builder.setUpperInclusive(true);
    return builder;
  }

  public boolean contains(final Version version)
  {
    Objects.requireNonNull(version, "version");

    final Boolean lower_ok = this.lower().map(lower -> {
      if (this.lowerInclusive()) {
        return Boolean.valueOf(lower.compareTo(version) >= 0);
      }
      return Boolean.valueOf(lower.compareTo(version) > 0);
    }).orElse(Boolean.TRUE);

    final Boolean upper_ok = this.upper().map(upper -> {
      if (this.upperInclusive()) {
        return Boolean.valueOf(upper.compareTo(version) <= 0);
      }
      return Boolean.valueOf(upper.compareTo(version) < 0);
    }).orElse(Boolean.TRUE);

    return lower_ok.booleanValue() && upper_ok.booleanValue();
  }

  @AutoValue.Builder
  public static abstract class Builder
  {
    Builder()
    {

    }

    public abstract Builder setLower(Version v);

    public abstract Builder setLower(Optional<Version> v);

    public abstract Builder setLowerInclusive(boolean i);

    public abstract Builder setUpper(Version v);

    public abstract Builder setUpper(Optional<Version> v);

    public abstract Builder setUpperInclusive(boolean i);

    public abstract VersionRange build();
  }
}
