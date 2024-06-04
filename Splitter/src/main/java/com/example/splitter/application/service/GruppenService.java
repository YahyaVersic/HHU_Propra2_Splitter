package com.example.splitter.application.service;

import com.example.splitter.domain.model.Ausgabe;
import com.example.splitter.domain.model.Gruppe;
import com.example.splitter.domain.model.Person;
import com.example.splitter.domain.model.berechnung.Schuld;
import com.example.splitter.domain.services.BerechnungsService;
import com.example.splitter.domain.services.GruppenRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class GruppenService {

  GruppenRepository gruppenRepository;

  public GruppenService(GruppenRepository gruppenRepository) {
    if (gruppenRepository == null) {
      throw new IllegalArgumentException("GruppenRepository darf nicht null sein");
    }
    this.gruppenRepository = gruppenRepository;
  }

  public Gruppe addGruppe(Gruppe gruppe) {
    if (gruppe == null) {
      throw new IllegalArgumentException("Gruppe darf nicht null");
    }
    return gruppenRepository.save(gruppe);
  }

  public List<Gruppe> getGruppen() {
    return gruppenRepository.findAll();
  }

  public Optional<Gruppe> getGruppe(Integer gruppenID) {
    if (gruppenID == null) {
      throw new IllegalArgumentException("GruppenID darf nicht null sein");
    }
    return gruppenRepository.findById(gruppenID);
  }

  public void removeGruppe(Integer gruppenID) {
    if (gruppenID == null) {
      throw new IllegalArgumentException("GruppenID darf nicht null sein");
    }
    gruppenRepository.deleteById(gruppenID);
  }

  public void schliesseGruppe(Integer gruppenID) {
    if (gruppenID == null) {
      throw new IllegalArgumentException("GruppenID darf nicht null sein");
    }

    var g = gruppenRepository.findById(gruppenID);
    if (g.isPresent()) {
      g.get().schliessen();
      gruppenRepository.save(g.get());
    } else {
      throw new IllegalArgumentException("Keine Gruppe mit dieser ID konnte gefunden werden.");
    }
  }

  public void addTeilnehmer(Integer gruppenID, Person person) {
    var g = gruppenRepository.findById(gruppenID);

    if (g.isEmpty()) {
      throw new IllegalArgumentException("Keine Gruppe mit dieser ID konnte gefunden werden.");
    } else if (person == null) {
      throw new IllegalArgumentException("Person darf nicht null sein.");
    }
    Gruppe gruppe = g.get();
    gruppe.addTeilnehmer(person);
    gruppenRepository.save(gruppe);
  }

  public void addAusgabe(Integer gruppenID, Ausgabe ausgabe) {
    var g = gruppenRepository.findById(gruppenID);
    if (g.isEmpty()) {
      throw new IllegalArgumentException("Keine Gruppe mit dieser ID konnte gefunden werden.");
    } else if (ausgabe == null) {
      throw new IllegalArgumentException("Ausgabe darf nicht null sein.");
    }
    Gruppe gruppe = g.get();

    gruppe.addAusgabe(ausgabe);
    gruppenRepository.save(gruppe);
  }

  public List<Gruppe> getOpenGruppenForPerson(String githubHandle) {
    if (githubHandle == null) {
      throw new IllegalArgumentException("GithubHandle darf nicht null sein");
    }
    return getGruppenForPerson(new Person(githubHandle)).stream().filter(Gruppe::isOffen).toList();
  }

  public List<Gruppe> getClosedGruppenForPerson(String githubHandle) {
    if (githubHandle == null) {
      throw new IllegalArgumentException("GithubHandle darf nicht null sein");
    }
    return getGruppenForPerson(new Person(githubHandle)).stream()
        .filter(gruppe -> !gruppe.isOffen()).toList();
  }

  public List<Schuld> getSchuldenForGruppe(Integer gruppenID) {
    if (gruppenID == null) {
      throw new IllegalArgumentException("GruppenID darf nicht null sein");
    }
    var x = getGruppe(gruppenID);
    if (x.isPresent()) {
      return BerechnungsService.berechneSchuldenEinerGruppe(x.get());
    } else {
      throw new IllegalArgumentException("Gruppe nicht vorhanden");
    }
  }

  public List<Gruppe> getGruppenForPerson(Person person) {
    if (person == null) {
      throw new IllegalArgumentException("Person darf nicht null sein");
    }
    return gruppenRepository.findAll().stream().filter(x -> x.teilnehmer().contains(person))
        .toList();
  }

  public List<Schuld> getSchuldenZuZahlen(Person person) {
    if (person == null) {
      throw new IllegalArgumentException("Person darf nicht null sein");
    }
    return getGruppenForPerson(person).stream().map(x -> getSchuldenForGruppe(x.id()))
        .flatMap(Collection::stream)
        .filter(x -> x.zahler().equals(person))
        .toList();
  }

  public List<Schuld> getSchuldenZuEmpfangen(Person person) {
    if (person == null) {
      throw new IllegalArgumentException("Person darf nicht null sein");
    }
    return getGruppenForPerson(person).stream().map(x -> getSchuldenForGruppe(x.id()))
        .flatMap(Collection::stream).filter(x -> x.empfaenger().equals(person)).toList();
  }

  public boolean darfTeilnehmerHinzugefuegtWerden(Integer gruppenID) {
    if (gruppenID == null) {
      throw new IllegalArgumentException("GruppenID darf nicht null sein");
    }

    Optional<Gruppe> gruppe = getGruppe(gruppenID);
    if (gruppe.isPresent()) {
      return gruppe.get().teilnehmerDarfHinzugefuegtWerden();
    } else {
      throw new IllegalArgumentException("Gruppe nicht vorhanden");
    }
  }

  public boolean personHatZugriffAufGruppe(String gitHubHandle, Integer gruppenID) {
    if (gruppenID == null) {
      throw new IllegalArgumentException("GruppenID darf nicht null sein");
    } else if (gitHubHandle == null) {
      throw new IllegalArgumentException("GitHubHandle darf nicht null sein");
    }
    Person user = new Person(gitHubHandle);
    Optional<Gruppe> gruppe = getGruppe(gruppenID);
    if (gruppe.isPresent()) {
      return gruppe.get().containsTeilnehmer(user);
    } else {
      throw new IllegalArgumentException("Gruppe nicht vorhanden");
    }
  }


}
