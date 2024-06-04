package com.example.splitter.controller.webdomain;

import com.example.splitter.domain.model.berechnung.Schuld;

import java.util.Objects;

public final class WebSchuld {

  private final String von;
  private final String an;
  private final int cents;

  public WebSchuld(String von, String an, int cent) {
    this.von = von;
    this.an = an;
    this.cents = cent;
  }

  public static WebSchuld fromSchuld(Schuld schuld) {
    return new WebSchuld(schuld.zahler().githubHandle(), schuld.empfaenger().githubHandle(),
        schuld.geld().centbetrag());
  }

  public String getVon() {
    return von;
  }

  public String getAn() {
    return an;
  }

  public int getCents() {
    return cents;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (WebSchuld) obj;
    return Objects.equals(this.von, that.von) &&
        Objects.equals(this.an, that.an) &&
        this.cents == that.cents;
  }

  @Override
  public int hashCode() {
    return Objects.hash(von, an, cents);
  }

  @Override
  public String toString() {
    return "WebSchuld[" +
        "von=" + von + ", " +
        "an=" + an + ", " +
        "cent=" + cents + ']';
  }


}
