package com.example.splitter.repositories;

import com.example.splitter.domain.model.Ausgabe;
import com.example.splitter.domain.model.Geld;
import com.example.splitter.domain.model.Gruppe;
import com.example.splitter.domain.model.Person;
import com.example.splitter.domain.services.GruppenRepository;
import com.example.splitter.repositories.dbdomain.DBAusgabe;
import com.example.splitter.repositories.dbdomain.DBGruppe;
import com.example.splitter.repositories.dbdomain.DBProfiteur;
import com.example.splitter.repositories.dbdomain.DBTeilnehmer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class GruppenRepositoryImpl implements GruppenRepository {

  private DBGruppenRepo db;

  public GruppenRepositoryImpl(DBGruppenRepo gruppeList) {
    this.db = gruppeList;
  }

  @Override
  public List<Gruppe> findAll() {
    List<DBGruppe> all = db.findAll();
    return all.stream().map(this::toGruppe).toList();
  }

  @Override
  public Gruppe save(Gruppe gruppe) {
    if (gruppe == null) {
      throw new IllegalArgumentException("Gruppe darf nicht null sein");
    }
    DBGruppe dto = fromGruppe(gruppe);
    DBGruppe saved = db.save(dto);
    return toGruppe(saved);
  }

  @Override
  public void saveAll(List<Gruppe> gruppen) {
    if (gruppen == null) {
      throw new IllegalArgumentException("Liste darf nicht null sein");
    }
    db.saveAll(gruppen.stream().map(this::fromGruppe).toList());
  }

  @Override
  public Optional<Gruppe> findById(Integer id) {
    if (id == null) {
      throw new IllegalArgumentException("Id darf nicht null sein");
    }
    var g = db.findById(id);
    return g.map(this::toGruppe);
  }

  @Override
  public void delete(Gruppe gruppe) {
    if (gruppe == null) {
      throw new IllegalArgumentException("Gruppe darf nicht null sein");
    }
    db.delete(fromGruppe(gruppe));
  }

  @Override
  public void deleteById(Integer id) {
    if (id == null) {
      throw new IllegalArgumentException("Id darf nicht null sein");
    }
    db.deleteById(id);
  }


  /*  to Methoden */

  private Gruppe toGruppe(DBGruppe dbGruppe) {
    if (dbGruppe == null) {
      throw new IllegalArgumentException("Argument darf nicht null sein");
    }
    List<Ausgabe> ausgaben = dbGruppe.ausgaben().stream().map(this::toAusgabe).toList();
    Gruppe g = new Gruppe(
        dbGruppe.id(),
        dbGruppe.name(),
        dbGruppe.teilnehmer().stream().map(this::toTeilnehmer).collect(Collectors.toSet()),
        ausgaben,
        dbGruppe.isOpen()
    );
    return g;
  }

  private Ausgabe toAusgabe(DBAusgabe dbAusgabe) {
    Ausgabe a = new Ausgabe(
        dbAusgabe.id(),
        new Person(dbAusgabe.geldgeber()),
        dbAusgabe.beschreibung(),
        dbAusgabe.profiteure().stream().map(this::toProfiteur).collect(Collectors.toSet()),
        new Geld(dbAusgabe.geld())
    );
    return a;
  }

  private Person toProfiteur(DBProfiteur dbProfiteur) {
    return new Person(dbProfiteur.name());
  }

  private Person toTeilnehmer(DBTeilnehmer dbTeilnehmer) {
    return new Person(dbTeilnehmer.name());
  }

  /* From Methoden */

  private DBGruppe fromGruppe(Gruppe gruppe) {
    return new DBGruppe(
        gruppe.id(),
        gruppe.name(),
        gruppe.isOffen(),
        gruppe.teilnehmer().stream().map(this::fromTeilnehmer).collect(Collectors.toSet()),
        gruppe.ausgabenliste().stream().map(this::fromAusgabe).toList()
    );
  }

  private DBAusgabe fromAusgabe(Ausgabe ausgabe) {
    return new DBAusgabe(ausgabe.id(),
        ausgabe.beschreibung(),
        ausgabe.geldgeber().githubHandle(),
        ausgabe.geld().centbetrag(),
        ausgabe.profiteure().stream().map(this::fromProfiteur).collect(Collectors.toSet())
    );
  }

  private DBProfiteur fromProfiteur(Person person) {
    return new DBProfiteur(person.githubHandle());
  }

  private DBTeilnehmer fromTeilnehmer(Person person) {
    return new DBTeilnehmer(person.githubHandle());
  }

}
