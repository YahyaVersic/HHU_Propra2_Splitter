package com.example.splitter.domain.model;

import com.example.splitter.dto.AusgabenForm;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotNull.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class Ausgabe {

  private final Integer id;
  private final Person geldgeber;
  private final String beschreibung;
  private final Set<Person> profiteure;
  private final Geld geld;

  public Ausgabe(Person geldgeber, String beschreibung, Set<Person> profiteure, Geld geld) {
    if (geldgeber == null || beschreibung == null || profiteure == null || geld == null) {
      throw new IllegalArgumentException("Argumente dürfen nicht null sein");
    }
    this.id = null;
    this.geldgeber = geldgeber;
    this.beschreibung = beschreibung;
    this.profiteure = profiteure;
    this.geld = geld;
  }

  public Ausgabe(Integer id, Person geldgeber, String beschreibung, Set<Person> profiteure,
      Geld geld) {
    if (geldgeber == null || beschreibung == null || profiteure == null || geld == null) {
      throw new IllegalArgumentException("Argumente dürfen nicht null sein");
    }
    this.id = id;
    this.geldgeber = geldgeber;
    this.beschreibung = beschreibung;
    this.profiteure = profiteure;
    this.geld = geld;
  }

  public Ausgabe(@NotNull AusgabenForm ausgabenForm) {
    this(
        new Person(ausgabenForm.bezahler()),
        ausgabenForm.name(),
        Arrays.stream(ausgabenForm.profiteure()).map(Person::new).collect(Collectors.toSet()),
        new Geld((int) (ausgabenForm.preis() * 100)));
  }

  public Person geldgeber() {
    return geldgeber;
  }

  public String beschreibung() {
    return beschreibung;
  }

  public Set<Person> profiteure() {
    return Collections.unmodifiableSet(profiteure);
  }

  public Geld geld() {
    return geld;
  }

  public Integer id() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (Ausgabe) obj;
    return Objects.equals(this.id, that.id());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Ausgabe[" +
        "geldgeber=" + geldgeber + ", " +
        "beschreibung=" + beschreibung + ", " +
        "profiteure=" + profiteure + ", " +
        "geld=" + geld + ']';
  }

}