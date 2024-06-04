package com.example.splitter.domain.model.berechnung;

import com.example.splitter.domain.model.Geld;
import com.example.splitter.domain.model.Person;
import java.util.Objects;

public final class Schuld {

  private final Person zahler;
  private final Person empfaenger;
  private Geld geld;

  private final String gruppenname;

  public Schuld(Person zahler, Person empfaenger, Geld geld, String gruppenname) {
    if (zahler == null || empfaenger == null || geld == null || gruppenname == null) {
      throw new IllegalArgumentException("Argumente dürfen nicht null sein");
    }
    this.zahler = zahler;
    this.empfaenger = empfaenger;
    this.geld = geld;
    this.gruppenname = gruppenname;
  }

  public Person zahler() {
    return zahler;
  }

  public Person empfaenger() {
    return empfaenger;
  }

  public Geld geld() {
    return geld;
  }

  public String gruppenname() {
    return gruppenname;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (Schuld) obj;
    return Objects.equals(this.zahler, that.zahler) &&
        Objects.equals(this.empfaenger, that.empfaenger) &&
        Objects.equals(this.geld, that.geld);
  }

  @Override
  public int hashCode() {
    return Objects.hash(zahler, empfaenger, geld);
  }

  @Override
  public String toString() {
    return zahler + " schuldet " + empfaenger + " " + geld + " Euro";
  }

}

