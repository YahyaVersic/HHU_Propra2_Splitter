package com.example.splitter.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GruppeTest {

  private Person mkPerson(String githubHandle) {
    return new Person(githubHandle);
  }

  private Gruppe mkGruppe(String beschreibung, Person gruender, Person... personen) {
    Gruppe gruppe = new Gruppe(null, beschreibung, gruender);
    gruppe.addTeilnehmer(personen);
    return gruppe;
  }

  private Ausgabe mkAusgabe(Person geldgeber, String beschreibung, Set<Person> profiteure,
      Geld geld) {
    return new Ausgabe(geldgeber, beschreibung, profiteure, geld);
  }

  @Test
  @DisplayName("Teilnehmer kann hinzugefügt werden")
  void test1() {
    //Arrange
    Person p1 = mkPerson("Obi-Wan");
    Person p2 = mkPerson("Anakin");
    Gruppe gruppe = mkGruppe("Jedi Council", p1);

    //Act
    gruppe.addTeilnehmer(p2);

    //Assert
    assertThat(gruppe.teilnehmer()).contains(p1, p2);

  }


  @Test
  @DisplayName("Ausgabe kann hinzugefügt werden")
  void test_2() {
    //Arrange
    Person axler = mkPerson("Axler");
    Person valentin = mkPerson("Valentin");
    Person raetz = mkPerson("Rätz");
    Person klopsch = mkPerson("Klopsch");
    Gruppe mathclub = mkGruppe("Council of Math", axler, valentin, raetz, klopsch);
    Set<Person> profiteuere = Set.of(valentin, raetz);
    Ausgabe a1 = mkAusgabe(klopsch, "Induktionsaufgaben", profiteuere, new Geld(10_000, 69));

    //Act
    assertThat(mathclub.ausgabenliste()).isEmpty();
    mathclub.addAusgabe(a1);

    //Assert
    assertThat(mathclub.ausgabenliste()).contains(a1);
  }

  @Test
  @DisplayName("Ausgabe wird nicht hinzugefügt, wenn eine Person in der Ausgabe nicht in der Gruppe ist")
  void test_6() {
    //Arrange
    Person p1 = mkPerson("Peter Fox");
    Person p2 = mkPerson("Eminem");
    Person p3 = mkPerson("David Guetta");
    Person p4 = mkPerson("Nina Chuba");
    Gruppe gruppe = mkGruppe("Cool Festival", p1, p2, p3); //p4 nicht in Gruppe
    Ausgabe a1 = mkAusgabe(p2, "Festival Stuff 1", Set.of(p4), new Geld(12, 21));
    Ausgabe a2 = mkAusgabe(p4, "Festival Stuff 2", Set.of(p2), new Geld(21, 12));

    //Act & Assert
    assertThat(gruppe.ausgabenliste()).isEmpty();
    gruppe.addAusgabe(a1);
    assertThat(gruppe.ausgabenliste()).isEmpty();
    gruppe.addAusgabe(a2);
    assertThat(gruppe.ausgabenliste()).isEmpty();
  }

  @Test
  @DisplayName("Teilnehmer darf nicht mehr nach einer Ausgabe hinzugefügt werden")
  void test_3() {
    //Arrange
    Person homer = mkPerson("Homer Simpson");
    Person peter = mkPerson("Peter Griffin");
    Gruppe gruppe = mkGruppe(" The Simpsons Guy, Season 13, Episode 1", homer, peter);
    Ausgabe a1 = mkAusgabe(homer, "Dunkin Donuts", Set.of(peter), new Geld(234, 32));

    //Act
    assertThat(gruppe.teilnehmerDarfHinzugefuegtWerden()).isTrue();
    gruppe.addAusgabe(a1);

    //Assert
    assertThat(gruppe.teilnehmerDarfHinzugefuegtWerden()).isFalse();
  }

  @Test
  @DisplayName("Wenn Gruppe geschlossen, darf keine Ausgabe/Teilnehmer mehr hinzugefügt werden")
  void test_4() {
    //Arrange
    Person jens = mkPerson("Jens");
    Person markus = mkPerson("Markus");

    Gruppe gruppe = mkGruppe("Javaland",
        jens); //p2 nicht in der Gruppe, damit er "nicht hinzugefügt" werden kann und das richtig assertet wird
    Ausgabe a1 = mkAusgabe(jens, "Eis essen", Set.of(markus), new Geld(15, 98));

    //Act
    assertThat(gruppe.isOffen()).isTrue();
    gruppe.schliessen();
    gruppe.addAusgabe(a1);
    gruppe.addTeilnehmer(markus);

    //Assert
    assertThat(gruppe.isOffen()).isFalse();
    assertThat(gruppe.ausgabenliste()).isEmpty();
    assertThat(gruppe.teilnehmer()).hasSize(1);
  }

  @Test
  @DisplayName("Ist Teilnehmer in Gruppe gibt true zurück, wenn Teilnehmer in Gruppe ist")
  void test_5() {
    Person jens = mkPerson("Jens");

    Gruppe gruppe = mkGruppe("Javaland",
        jens);
    assertThat(gruppe.isTeilnehmerInGruppe(jens)).isTrue();
  }

  @Test
  @DisplayName("Ist Teilnehmer in Gruppe gibt false zurück, wenn Teilnehmer nicht in Gruppe ist")
  void test_7() {
    Person jens = mkPerson("Jens");
    Person markus = mkPerson("Markus");

    Gruppe gruppe = mkGruppe("Javaland",
        jens);
    assertThat(gruppe.isTeilnehmerInGruppe(markus)).isFalse();
  }


}
