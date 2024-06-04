package com.example.splitter.controller.webdomain;

import com.example.splitter.domain.model.Ausgabe;
import com.example.splitter.domain.model.Gruppe;
import com.example.splitter.domain.model.Person;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;
import java.util.stream.Collectors;

public final class WebGruppe {

  private final String gruppe;
  private final String name;
  private final String[] personen;

  private final boolean geschlossen;
  private final WebAuslage[] ausgaben;


  public WebGruppe(Integer id, String name, Set<Person> personen, boolean isOpen,
      List<Ausgabe> ausgabenliste) {
    this.gruppe = String.valueOf(id);
    this.name = name;
    this.personen = personen.stream().map(Person::githubHandle).toArray(String[]::new);

    this.ausgaben = ausgabenliste.stream().map(WebAuslage::fromAusgabe).toArray(WebAuslage[]::new);
    this.geschlossen = !isOpen;
  }

  public WebGruppe(@JsonProperty("name") String name, @JsonProperty("personen") String[] personen) {
    this.gruppe = null;
    this.name = name;
    this.ausgaben = null;
    this.geschlossen = false;
    if (personen == null) {
      this.personen = null;
      return;
    }
    this.personen = Arrays.copyOf(personen, personen.length);
  }

  public static WebGruppe fromGruppe(Gruppe gruppe) {
    return new WebGruppe(gruppe.id(),
        gruppe.name(),
        new HashSet<>(gruppe.teilnehmer()),
        gruppe.isOffen(),
        new ArrayList<>(gruppe.ausgabenliste()));
  }

  public static Gruppe toGruppe(WebGruppe webGruppe) {
    Set<Person> personenSet = Arrays.stream(webGruppe.personen).map(Person::new).collect(
        Collectors.toSet());
    return new Gruppe(null, webGruppe.getName(), personenSet,
        new ArrayList<>(), !webGruppe.isGeschlossen());
  }

  public boolean isValid() {
    return name != null && personen != null && personen.length != 0;
  }

  public String getGruppe() {
    return gruppe;
  }

  public String getName() {
    return name;
  }

  public String[] getPersonen() {
    return personen;
  }

  public WebAuslage[] getAusgaben() {
    return ausgaben;
  }

  public boolean isGeschlossen() {
    return geschlossen;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (WebGruppe) obj;
    return Objects.equals(this.gruppe, that.gruppe) &&
        Objects.equals(this.name, that.name) &&
        Arrays.equals(this.personen, that.personen) &&
        Arrays.equals(this.ausgaben, that.ausgaben) &&
        this.geschlossen == that.geschlossen;
  }

  @Override
  public int hashCode() {
    return Objects.hash(gruppe, name, personen, ausgaben, geschlossen);
  }

  @Override
  public String toString() {
    return name;
  }

}