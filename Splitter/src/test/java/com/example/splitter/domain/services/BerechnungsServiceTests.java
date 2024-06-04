package com.example.splitter.domain.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.splitter.domain.model.Ausgabe;
import com.example.splitter.domain.model.Geld;
import com.example.splitter.domain.model.Gruppe;
import com.example.splitter.domain.model.Person;
import com.example.splitter.domain.model.berechnung.Schuld;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BerechnungsServiceTests {

  Gruppe createGroupContext() {
    Person person1 = new Person("");
    Person person2 = new Person("");
    Gruppe gruppe = new Gruppe(null, "Ausflug zum Mond", person1);
    gruppe.addTeilnehmer(person2);
    Set<Person> profiteure = new HashSet<>();
    profiteure.add(person2);
    gruppe.addAusgabe(new Ausgabe(person1, "Crepe essen", profiteure, new Geld(5, 80)));
    return gruppe;
  }

  @Test
  @DisplayName("Eine Person ohne Schulden hat keine Schulden")
  void test_5() {
    Person person1 = new Person("b");
    Person person2 = new Person("a");
    Gruppe gruppe = new Gruppe(null, "Ausflug zum Mond", person1);
    gruppe.addTeilnehmer(person2);
    Set<Person> profiteure = new HashSet<>();
    profiteure.add(person2);
    gruppe.addAusgabe(new Ausgabe(person1, "Crepe essen", profiteure, new Geld(5, 80)));

    int money = BerechnungsService.berechneKontostandEinerPerson(gruppe, person1).get(person1)
        .centbetrag();

    //assertThat(schulden).isEmpty();
    assertThat(money).isGreaterThanOrEqualTo(0);
  }

  @Test
  @DisplayName("Eine Person mit einer Schuld hat eine Schuld")
  void test_6() {
    Person person1 = new Person("b");
    Person person2 = new Person("a");
    Gruppe gruppe = new Gruppe(null, "Ausflug zum Mond", person1);
    gruppe.addTeilnehmer(person2);
    Set<Person> profiteure = new HashSet<>();
    profiteure.add(person2);
    gruppe.addAusgabe(new Ausgabe(person1, "Crepe essen", profiteure, new Geld(5, 80)));

    assertThat(BerechnungsService.berechneGesamtKontostaende(gruppe)).containsEntry(person2,
        new Geld(-5, -80)).containsEntry(person1, new Geld(5, 80));
  }

  @Test
  @DisplayName("Eine Person hat keine Schuld bei sich selbst Schuld")
  void test_7() {
    Person person1 = new Person("b");
    Person person2 = new Person("a");
    Gruppe gruppe = new Gruppe(null, "Ausflug zum Mond", person1);
    gruppe.addTeilnehmer(person2);
    Set<Person> profiteure = new HashSet<>();
    profiteure.add(person1);
    gruppe.addAusgabe(new Ausgabe(person1, "Crepe essen", profiteure, new Geld(5, 80)));

    assertThat(BerechnungsService.berechneGesamtKontostaende(gruppe)).containsEntry(person1,
        new Geld(0, 0));
  }

  @Test
  @DisplayName("Kosten werden zwischen 2 Personen geteilt")
  void test_8() {
    Person person1 = new Person("b");
    Person person2 = new Person("a");
    Gruppe gruppe = new Gruppe(null, "Ausflug zum Mond", person1);
    gruppe.addTeilnehmer(person2);
    Set<Person> profiteure = new HashSet<>();
    profiteure.add(person1);
    profiteure.add(person2);
    gruppe.addAusgabe(new Ausgabe(person1, "Crepe essen", profiteure, new Geld(5, 80)));

    assertThat(BerechnungsService.berechneGesamtKontostaende(gruppe)).containsEntry(person1,
        new Geld(2, 90)).containsEntry(person2, new Geld(-2, -90));
  }

  @Test
  @DisplayName("Wenn es keine Ausgaben gab, gibt es keine Schulden")
  void test_9() {
    Person person1 = new Person("b");
    Person person2 = new Person("a");
    Gruppe gruppe = new Gruppe(null, "Ausflug zum Mond", person1);
    gruppe.addTeilnehmer(person2);
    assertThat(BerechnungsService.berechneSchuldenEinerGruppe(gruppe)).isEmpty();
  }

  @Test
  @DisplayName("Wenn sich Ausgaben zwischen zwei Personen aufheben, gibt es keine Schuld")
  void test_10() {
    Person person1 = new Person("b");
    Person person2 = new Person("a");
    Gruppe gruppe = new Gruppe(null, "Ausflug zum Mond", person1);
    gruppe.addTeilnehmer(person2);
    Set<Person> profiteure = new HashSet<>();
    profiteure.add(person1);
    profiteure.add(person2);
    gruppe.addAusgabe(new Ausgabe(person1, "Crepe essen", profiteure, new Geld(5, 80)));
    gruppe.addAusgabe(new Ausgabe(person2, "Eis essen", profiteure, new Geld(5, 80)));

    assertThat(BerechnungsService.berechneSchuldenEinerGruppe(gruppe)).isEmpty();
  }

  @Test
  @DisplayName("Bei zwei Ausgaben zwischen zwei Personen gibt es hinterher nur eine Schuld")
  void test_11() {
    Person person1 = new Person("b");
    Person person2 = new Person("a");
    Gruppe gruppe = new Gruppe(null, "Ausflug zum Mond", person1);
    gruppe.addTeilnehmer(person2);
    Set<Person> profiteure = new HashSet<>();
    profiteure.add(person1);
    profiteure.add(person2);
    gruppe.addAusgabe(new Ausgabe(person1, "Crepe essen", profiteure, new Geld(5, 80)));
    gruppe.addAusgabe(new Ausgabe(person2, "Eis essen", profiteure, new Geld(7, 20)));

    assertThat(BerechnungsService.berechneSchuldenEinerGruppe(gruppe)).hasSize(1);
  }

  @Test
  @DisplayName("Bei zwei Ausgaben zwischen zwei Personen, werden Schulden korrekt verrechnet")
  void test_12() {
    Person person1 = new Person("b");
    Person person2 = new Person("a");
    Gruppe gruppe = new Gruppe(null, "Ausflug zum Mond", person1);
    gruppe.addTeilnehmer(person2);
    Set<Person> profiteure = new HashSet<>();
    profiteure.add(person1);
    profiteure.add(person2);
    gruppe.addAusgabe(new Ausgabe(person1, "Crepe essen", profiteure, new Geld(6, 0)));
    gruppe.addAusgabe(new Ausgabe(person2, "Eis essen", profiteure, new Geld(4, 0)));

    assertThat(BerechnungsService.berechneSchuldenEinerGruppe(gruppe)).contains(
        new Schuld(person2, person1, new Geld(1, 0), gruppe.name()));
  }

  @Test
  @DisplayName("Eine Person gibt für mehrere Personen aus und alle schulden ihr")
  void test_13() {
    Person person1 = new Person("b");
    Person person2 = new Person("a");
    Person person3 = new Person("c");
    Gruppe gruppe = new Gruppe(null, "Ausflug zum Mond", person1);
    gruppe.addTeilnehmer(person2);
    gruppe.addTeilnehmer(person3);
    Set<Person> profiteure = new HashSet<>();
    profiteure.add(person1);
    profiteure.add(person2);
    profiteure.add(person3);

    gruppe.addAusgabe(new Ausgabe(person1, "Crepe essen", profiteure, new Geld(6, 0)));
    gruppe.addAusgabe(new Ausgabe(person1, "Eis essen", profiteure, new Geld(3, 0)));

    assertThat(BerechnungsService.berechneSchuldenEinerGruppe(gruppe).stream()
        .filter(x -> !x.empfaenger().equals(person1)).toList()).isEmpty();
  }

  //----------------------------------------------------------------------------------------------
  //Vorgegebene Test-Szenarien vom 1.März.2023

  @Test
  @DisplayName("Szenario 1: Summieren von Auslagen")
  void test_14() {
    //Arrange
    Person a = new Person("A");
    Person b = new Person("B");

    Ausgabe ausgabe1 = new Ausgabe(a, "Döner-Eis (Ein Gericht)",
        Set.of(a, b), new Geld(10, 0));
    Ausgabe ausgabe2 = new Ausgabe(a, "Eis-Döner (Ein anderes Gericht)",
        Set.of(a, b), new Geld(20, 0));

    Gruppe gruppe = new Gruppe(null, "Testgruppe (Dönergruppe)", a);
    gruppe.addTeilnehmer(b);
    gruppe.addAusgabe(ausgabe1);
    gruppe.addAusgabe(ausgabe2);

    //Act
    List<Schuld> schulds = BerechnungsService.berechneSchuldenEinerGruppe(gruppe);

    //Assert
    assertThat(schulds).containsExactly(new Schuld(b, a, new Geld(15, 0), gruppe.name()));

  }

  @Test
  @DisplayName("Szenario 2: Ausgleich")
  void test_15() {
    //Arrange
    Person a = new Person("A");
    Person b = new Person("B");

    Ausgabe ausgabe1 = new Ausgabe(a, "Döner-Eis (Ein Gericht)",
        Set.of(a, b), new Geld(10, 0));
    Ausgabe ausgabe2 = new Ausgabe(b, "Eis-Döner (Ein anderes Gericht)",
        Set.of(a, b), new Geld(20, 0));

    Gruppe gruppe = new Gruppe(null, "Testgruppe", a);
    gruppe.addTeilnehmer(b);
    gruppe.addAusgabe(ausgabe1);
    gruppe.addAusgabe(ausgabe2);

    //Act
    List<Schuld> schulds = BerechnungsService.berechneSchuldenEinerGruppe(gruppe);

    //Assert
    assertThat(schulds).containsExactly(new Schuld(a, b, new Geld(5, 0), gruppe.name()));

  }

  @Test
  @DisplayName("Szenario 3: Zahlung ohne eigene Beteiligung\n")
  void test_16() {
    //Arrange
    Person a = new Person("A");
    Person b = new Person("B");

    Ausgabe ausgabe1 = new Ausgabe(a, "Döner-Eis (Ein Gericht)",
        Set.of(b), new Geld(10, 0));
    Ausgabe ausgabe2 = new Ausgabe(a, "Eis-Döner (Ein anderes Gericht)",
        Set.of(a, b), new Geld(20, 0));

    Gruppe gruppe = new Gruppe(null, "Testgruppe", a);
    gruppe.addTeilnehmer(b);
    gruppe.addAusgabe(ausgabe1);
    gruppe.addAusgabe(ausgabe2);

    //Act
    List<Schuld> schulds = BerechnungsService.berechneSchuldenEinerGruppe(gruppe);

    //Assert
    assertThat(schulds).containsExactly(new Schuld(b, a, new Geld(20, 0), gruppe.name()));
  }

  @Test
  @DisplayName("Szenario 4: Ringausgleich")
  void test_17() {
    //Arrange
    Person a = new Person("A");
    Person b = new Person("B");
    Person c = new Person("C");

    Ausgabe ausgabe1 = new Ausgabe(a, "Döner-Eis (Ein Gericht)",
        Set.of(a, b), new Geld(10, 0));
    Ausgabe ausgabe2 = new Ausgabe(b, "Eis-Döner (Ein anderes Gericht)",
        Set.of(b, c), new Geld(10, 0));
    Ausgabe ausgabe3 = new Ausgabe(c, "Eis-Döner (Ein anderes Gericht)",
        Set.of(c, a), new Geld(10, 0));

    Gruppe gruppe = new Gruppe(null, "Testgruppe", a);
    gruppe.addTeilnehmer(b);
    gruppe.addTeilnehmer(c);
    gruppe.addAusgabe(ausgabe1);
    gruppe.addAusgabe(ausgabe2);
    gruppe.addAusgabe(ausgabe3);

    //Act
    List<Schuld> schulds = BerechnungsService.berechneSchuldenEinerGruppe(gruppe);

    //Assert
    assertThat(schulds).hasSize(0);
  }

  @Test
  @DisplayName("Szenario 5: ABC Beispiel aus der Einführung")
  void test_18() {
    //Arrange
    Person a = new Person("Anton");
    Person b = new Person("Berta");
    Person c = new Person("Christian");

    Ausgabe ausgabe1 = new Ausgabe(a, "Döner-Eis (Ein Gericht)",
        Set.of(a, b, c), new Geld(60, 0));
    Ausgabe ausgabe2 = new Ausgabe(b, "Eis-Döner (Ein anderes Gericht)",
        Set.of(a, b, c), new Geld(30, 0));
    Ausgabe ausgabe3 = new Ausgabe(c, "Eis-Döner (Ein anderes Gericht)",
        Set.of(b, c), new Geld(100, 0));

    Gruppe gruppe = new Gruppe(null, "Testgruppe", a);
    gruppe.addTeilnehmer(b);
    gruppe.addTeilnehmer(c);
    gruppe.addAusgabe(ausgabe1);
    gruppe.addAusgabe(ausgabe2);
    gruppe.addAusgabe(ausgabe3);

    //Act
    List<Schuld> schulds = BerechnungsService.berechneSchuldenEinerGruppe(gruppe);

    //Assert
    assertThat(schulds).hasSize(2);
    assertThat(schulds).contains(
        new Schuld(b, a, new Geld(30, 0), gruppe.name()),
        new Schuld(b, c, new Geld(20, 0), gruppe.name())
    );
  }

  @Test
  @DisplayName("Szenario 6: Beispiel aus der Aufgabenstellung")
  void test_19() {
    //Arrange
    Person a = new Person("A");
    Person b = new Person("B");
    Person c = new Person("C");
    Person d = new Person("D");
    Person e = new Person("E");
    Person f = new Person("F");

    Ausgabe ausgabe1 = new Ausgabe(a, "Hotelzimmer",
        Set.of(a, b, c, d, e, f), new Geld(564, 0));
    Ausgabe ausgabe2 = new Ausgabe(b, "Benzin, hinweg",
        Set.of(b, a), new Geld(38, 58));
    Ausgabe ausgabe3 = new Ausgabe(b, "Benzin, rückweg", Set.of(b, a, d), new Geld(38, 58));
    Ausgabe ausgabe4 = new Ausgabe(c, "Benzin", Set.of(c, e, f), new Geld(82, 11));
    Ausgabe ausgabe5 = new Ausgabe(d, "Städtetour", Set.of(a, b, c, d, e, f), new Geld(96, 0));
    Ausgabe ausgabe6 = new Ausgabe(f, "Theatervorstellung", Set.of(b, e, f), new Geld(95, 37));

    Gruppe gruppe = new Gruppe(null, "Testgruppe", a);
    gruppe.addTeilnehmer(b);
    gruppe.addTeilnehmer(c);
    gruppe.addTeilnehmer(d);
    gruppe.addTeilnehmer(e);
    gruppe.addTeilnehmer(f);
    gruppe.addAusgabe(ausgabe1);
    gruppe.addAusgabe(ausgabe2);
    gruppe.addAusgabe(ausgabe3);
    gruppe.addAusgabe(ausgabe4);
    gruppe.addAusgabe(ausgabe5);
    gruppe.addAusgabe(ausgabe6);

    //Act
    List<Schuld> schulds = BerechnungsService.berechneSchuldenEinerGruppe(gruppe);

    //Assert
    assertThat(schulds).hasSize(5);
    assertThat(schulds).contains(
        new Schuld(b, a, new Geld(96, 78), gruppe.name()),
        new Schuld(c, a, new Geld(55, 26), gruppe.name()),
        new Schuld(d, a, new Geld(26, 86), gruppe.name()),
        new Schuld(e, a, new Geld(169, 16), gruppe.name()),
        new Schuld(f, a, new Geld(73, 79), gruppe.name())
    );
  }

  @Test
  @DisplayName("Szenario 7: Minimierung")
  @Disabled("Noch nicht minimal")
  void test_20() {
    //Arrange
    Person a = new Person("A");
    Person b = new Person("B");
    Person c = new Person("C");
    Person d = new Person("D");
    Person e = new Person("E");
    Person f = new Person("F");
    Person g = new Person("G");

    Ausgabe ausgabe1 = new Ausgabe(d, "", Set.of(d, f), new Geld(20, 0));
    Ausgabe ausgabe2 = new Ausgabe(g, "", Set.of(b), new Geld(10, 0));
    Ausgabe ausgabe3 = new Ausgabe(e, "", Set.of(a, c, e), new Geld(75, 0));
    Ausgabe ausgabe4 = new Ausgabe(f, "", Set.of(a, f), new Geld(50, 0));
    Ausgabe ausgabe5 = new Ausgabe(e, "", Set.of(d), new Geld(40, 0));
    Ausgabe ausgabe6 = new Ausgabe(f, "", Set.of(b, f), new Geld(40, 0));
    Ausgabe ausgabe7 = new Ausgabe(f, "", Set.of(c), new Geld(5, 0));
    Ausgabe ausgabe8 = new Ausgabe(g, "", Set.of(a), new Geld(30, 0));

    Gruppe gruppe = new Gruppe(null, "Testgruppe", a);
    gruppe.addTeilnehmer(b);
    gruppe.addTeilnehmer(c);
    gruppe.addTeilnehmer(d);
    gruppe.addTeilnehmer(e);
    gruppe.addTeilnehmer(f);
    gruppe.addTeilnehmer(g);

    gruppe.addAusgabe(ausgabe1);
    gruppe.addAusgabe(ausgabe2);
    gruppe.addAusgabe(ausgabe3);
    gruppe.addAusgabe(ausgabe4);
    gruppe.addAusgabe(ausgabe5);
    gruppe.addAusgabe(ausgabe6);
    gruppe.addAusgabe(ausgabe7);
    gruppe.addAusgabe(ausgabe8);

    //Act
    List<Schuld> schulds = BerechnungsService.berechneSchuldenEinerGruppe(gruppe);

    //Assert
    assertThat(schulds).hasSize(5);
    assertThat(schulds).contains(
        new Schuld(a, f, new Geld(40, 0), gruppe.name()),
        new Schuld(a, g, new Geld(40, 0), gruppe.name()),
        new Schuld(b, e, new Geld(30, 0), gruppe.name()),
        new Schuld(c, e, new Geld(30, 0), gruppe.name()),
        new Schuld(d, e, new Geld(30, 0), gruppe.name())
    );
  }

}
