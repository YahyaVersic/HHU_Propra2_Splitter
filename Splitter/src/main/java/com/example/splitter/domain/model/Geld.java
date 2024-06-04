package com.example.splitter.domain.model;

public class Geld {

  private final int centbetrag;

  public Geld(int euro, int cent) {
    if (euro < 0 && cent > 0 || euro > 0 && cent < 0) {
      throw new IllegalArgumentException("Konstuktor darf nur negativ oder nur positiv sein!");
    }
    this.centbetrag = euro * 100 + cent;
  }

  public Geld(int centbetrag) {
    this(0, centbetrag);
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(centbetrag);
  }

  @Override
  public String toString() {
    int euro = centbetrag / 100;
    int cent = centbetrag % 100;
    if (cent < 10) {
      return "" + euro + ",0" + cent;
    } else {
      return "" + euro + "," + cent;
    }
  }

  public Geld add(Geld g) {
    if (g == null) {
      throw new IllegalArgumentException("Geld darf nicht null sein!");
    }

    return new Geld(this.centbetrag + g.centbetrag);
  }

  public int centbetrag() {
    return centbetrag;
  }

  public Geld split(int personenanzahl) {
    if (personenanzahl <= 0) {
      throw new IllegalArgumentException("Personenzahl muss positiv sein");
    }
    return new Geld(this.centbetrag / personenanzahl);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (Geld) obj;
    return this.centbetrag == that.centbetrag;
  }
}
