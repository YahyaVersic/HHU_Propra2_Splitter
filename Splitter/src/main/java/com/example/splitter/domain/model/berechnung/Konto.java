package com.example.splitter.domain.model.berechnung;

import com.example.splitter.domain.model.Geld;
import com.example.splitter.domain.model.Person;
import java.util.Objects;

public class Konto {

  private final Person inhaber;
  private Geld kontostand;

  public Konto(Person inhaber, Geld kontostand) {
    if (inhaber == null || kontostand == null) {
      throw new IllegalArgumentException("Argumente dürfen nicht null sein");
    }
    this.inhaber = inhaber;
    this.kontostand = kontostand;
  }

  public Person inhaber() {
    return inhaber;
  }

  public Geld kontostand() {
    return kontostand;
  }

  public void setKontostand(Geld kontostand) {
    if (kontostand == null) {
      throw new IllegalArgumentException("Kontostand darf nicht null sein");
    }
    this.kontostand = kontostand;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (Konto) obj;
    return Objects.equals(this.inhaber, that.inhaber) &&
        Objects.equals(this.kontostand, that.kontostand);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inhaber, kontostand);
  }

  @Override
  public String toString() {
    return "Konto[" +
        "inhaber=" + inhaber + ", " +
        "kontostand=" + kontostand + ']';
  }
}
