package com.example.splitter.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public final class Gruppe {

  private final Integer id;
  private final String name;
  private final Set<Person> teilnehmer;
  private final List<Ausgabe> ausgabenliste;
  private boolean isOpen;


  public Gruppe(Integer id, String name,
      Person gruender) {
    if (name == null || gruender == null) {
      throw new IllegalArgumentException("Name und Gruender dürfen nicht null sein");
    }
    this.id = id;
    this.name = name;
    this.teilnehmer = new HashSet<>();
    teilnehmer.add(gruender);
    this.ausgabenliste = new ArrayList<>();
    this.isOpen = true;
  }

  public Gruppe(Integer id, String name, Set<Person> teilnehmer, List<Ausgabe> ausgabenliste,
      boolean isOpen) {
    if (name == null || teilnehmer == null || ausgabenliste == null) {
      throw new IllegalArgumentException("Argumente dürfen nicht null sein");
    }
    this.id = id;
    this.name = name;
    this.teilnehmer = new HashSet<>(teilnehmer);
    this.ausgabenliste = new ArrayList<>(ausgabenliste);
    this.isOpen = isOpen;
  }

  public boolean isOffen() {
    return isOpen;
  }

  public boolean isGeschlossen() {
    return !isOpen;
  }

  public void schliessen() {
    isOpen = false;
  }

  public void addTeilnehmer(Person person) {
    if (person == null) {
      throw new IllegalArgumentException("Person darf nicht null sein");
    }
    if (!teilnehmerDarfHinzugefuegtWerden() || isTeilnehmerInGruppe(person)) {
      return;
    }
    teilnehmer.add(person);
  }

  public void addTeilnehmer(Person... p) {
    for (Person person : p) {
      addTeilnehmer(person);
    }
  }

  public boolean isTeilnehmerInGruppe(Person person) {
    if (person == null) {
      throw new IllegalArgumentException("Person darf nicht null sein");
    }
    for (Person p : teilnehmer) {
      if (p.equals(person)) {
        return true;
      }
    }
    return false;
  }

  public void addAusgabe(
      Ausgabe ausgabe
  ) {
    if (ausgabe == null) {
      throw new IllegalArgumentException("Ausgabe darf nicht null sein");
    }
    if (isGeschlossen()) {
      return;
    }
    if (!teilnehmer.containsAll(ausgabe.profiteure())) {
      return; // Wenn einer der Profiteure nicht in der Gruppe ist soll die Ausgabe nicht hinzugefügt werden.
    }
    if (!teilnehmer.contains(ausgabe.geldgeber())) {
      return; //Wenn der Geldgeber nicht in der Gruppe ist soll die Ausgabe nicht hinzugefügt werden
    }
    this.ausgabenliste.add(ausgabe);
  }

  public String name() {
    return name;
  }

  public Set<Person> teilnehmer() {
    return Collections.unmodifiableSet(teilnehmer);
  }

  public List<Ausgabe> ausgabenliste() {
    return Collections.unmodifiableList(ausgabenliste);
  }

  public boolean teilnehmerDarfHinzugefuegtWerden() {
    return ausgabenliste().isEmpty() && isOffen();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (Gruppe) obj;
    return this.id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return name;
  }

  public Integer id() {
    return id;
  }

  public boolean containsTeilnehmer(Person p) {
    if (p == null) {
      throw new IllegalArgumentException("Person darf nicht null sein");
    }
    return this.teilnehmer.contains(p);
  }


}
