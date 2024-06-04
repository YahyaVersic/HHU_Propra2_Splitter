package com.example.splitter.domain.model;

import java.util.Objects;


public final class Person {

  private final String githubHandle;

  public Person(String githubHandle) {
    if (githubHandle == null) {
      throw new IllegalArgumentException("Argument darf nicht null sein");
    }
    this.githubHandle = githubHandle;
  }

  @Override
  public String toString() {
    return githubHandle;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o == null || o.getClass() != this.getClass()) {
      return false;
    }
    var that = (Person) o;
    return this.githubHandle.equalsIgnoreCase(that.githubHandle);
  }

  public String githubHandle() {
    return githubHandle;
  }

  @Override
  public int hashCode() {
    return Objects.hash(githubHandle);
  }

}
