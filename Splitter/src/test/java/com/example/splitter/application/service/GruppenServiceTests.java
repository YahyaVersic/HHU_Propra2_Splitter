package com.example.splitter.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.splitter.domain.model.Ausgabe;
import com.example.splitter.domain.model.Geld;
import com.example.splitter.domain.model.Gruppe;
import com.example.splitter.domain.model.Person;
import com.example.splitter.domain.model.berechnung.Schuld;
import com.example.splitter.domain.services.GruppenRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GruppenServiceTests {


  GruppenRepository gruppenRepository = mock(GruppenRepository.class);
  GruppenService gruppenService = new GruppenService(gruppenRepository);

  private Person mkPerson(String githubHandle) {
    return new Person(githubHandle);
  }

  private Gruppe mkGruppe(Integer id, String beschreibung, Person gruender, Person... personen) {
    Gruppe gruppe = new Gruppe(id, beschreibung, gruender);
    gruppe.addTeilnehmer(personen);
    return gruppe;
  }

  private Ausgabe mkAusgabe(Person geldgeber, String beschreibung, Set<Person> profiteure,
      Geld geld) {
    return new Ausgabe(geldgeber, beschreibung, profiteure, geld);
  }

  private Geld mkGeld(int betragInCent) {
    return new Geld(betragInCent);
  }


  @Test
  @DisplayName("Gruppe kann hinzugefügt werden")
  void test_1() {
    //Arrange
    Person p1 = mkPerson("bendisposto");
    Person p2 = mkPerson("rutenkolk");

    Gruppe sonnengruppe = mkGruppe(0, "Sonnengruppe", p1, p2);

    when(gruppenRepository.save(sonnengruppe)).thenReturn(
        new Gruppe(0, sonnengruppe.name(), Set.of(p1, p2), new ArrayList<>(), true));

    //Act
    Gruppe gruppe = gruppenService.addGruppe(sonnengruppe);

    //Assert
    verify(gruppenRepository, times(1)).save(sonnengruppe);
    assertThat(gruppenService.addGruppe(sonnengruppe)).isEqualTo(sonnengruppe);
  }

  @Test
  @DisplayName("Eine Gruppe, die null ist kann nicht hinzugefügt werden")
  void test_2() {
    //Arrange
    Gruppe nullgruppe = null;

    //Act

    //Assert
    assertThatThrownBy(() -> gruppenService.addGruppe(nullgruppe)).hasMessage(
        "Gruppe darf nicht null").isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Teilnehmer kann hinzugefügt werden")
  void test_3() {
    Gruppe gruppe = new Gruppe(0, "DIE Gruppe", new Person("Steve"));
    when(gruppenRepository.findById(0)).thenReturn(Optional.of(gruppe));
    Person person = new Person("Gertrude");

    gruppenService.addTeilnehmer(gruppe.id(), person);

    assertThat(gruppenService.getGruppe(gruppe.id()).get().teilnehmer()).contains(person);
  }

  @Test
  @DisplayName("Einer nicht vorhandenen Gruppe kann kein Teilnehmer hinzugefügt werden")
  void test_4() {
    Gruppe nullgruppe = null;
    Person person = new Person("Gertrude");

    assertThatThrownBy(() -> gruppenService.addTeilnehmer(null, person)).hasMessage(
            "Keine Gruppe mit dieser ID konnte gefunden werden.")
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("null darf nicht als Teilnehmer hinzugefügt werden")
  void test_5() {
    Gruppe gruppe = new Gruppe(0, "DIE Gruppe", new Person("Steve"));
    when(gruppenRepository.findById(0)).thenReturn(Optional.of(gruppe));
    Person nullperson = null;

    assertThatThrownBy(() -> gruppenService.addTeilnehmer(gruppe.id(), nullperson)).hasMessage(
        "Person darf nicht null sein.").isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Ausgabe kann hinzugefügt werden")
  void test_6() {
    Person p1 = new Person("Steve");
    Person p2 = new Person("Gertrude");
    Gruppe gruppe = new Gruppe(0, "DIE Gruppe", p1);
    when(gruppenRepository.findById(0)).thenReturn(Optional.of(gruppe));
    gruppenService.addTeilnehmer(gruppe.id(), p2);
    Ausgabe ausgabe = new Ausgabe(p1, "Waffel essen", Set.of(p2), new Geld(500));

    gruppenService.addAusgabe(gruppe.id(), ausgabe);

    assertThat(gruppenService.getGruppe(gruppe.id()).get().ausgabenliste()).containsExactly(
        ausgabe);
  }

  @Test
  @DisplayName("Einer nicht vorhandenen Gruppe kann keine Ausgabe hinzugefügt werden")
  void test_7() {
    Person p1 = new Person("Steve");
    Person p2 = new Person("Gertrude");
    Ausgabe ausgabe = new Ausgabe(p1, "Waffel essen", Set.of(p2), new Geld(500));

    assertThatThrownBy(() -> gruppenService.addAusgabe(null, ausgabe)).hasMessage(
            "Keine Gruppe mit dieser ID konnte gefunden werden.")
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("null darf nicht als Ausgabe hinzugefügt werden")
  void test_8() {
    Person p1 = new Person("Gertrude");
    int gruppenID = 0;
    when(gruppenRepository.findById(0)).thenReturn(
        Optional.of(new Gruppe(0, "HolzfällerGruppe", new HashSet<>(), new ArrayList<>(), true)));
    Ausgabe ausgabe = null;

    assertThatThrownBy(() -> gruppenService.addAusgabe(gruppenID, ausgabe)).hasMessage(
        "Ausgabe darf nicht null sein.").isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Gruppe kann gelöscht werden")
  void test_9() {
    Person p1 = new Person("Steve");
    Person p2 = new Person("Gertrude");
    Gruppe gruppe = new Gruppe(0, "DIE Gruppe", p1);
    when(gruppenRepository.findById(0)).thenReturn(Optional.of(gruppe));

    gruppenService.removeGruppe(gruppe.id());

    verify(gruppenRepository, times(1)).deleteById(gruppe.id());
  }

  @Test
  @DisplayName("GruppenID null kann nicht gelöscht werden")
  void test_10() {
    assertThatThrownBy(() -> gruppenService.removeGruppe(null)).hasMessage(
        "GruppenID darf nicht null sein").isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("getGruppen sucht nach allen Gruppen")
  void test_11() {
    int gruppenID1 = 1;
    int gruppenID2 = 2;
    Gruppe gruppe1 = new Gruppe(gruppenID1, "Gruppe des Spaßes", new Person("Hannibal"));
    Gruppe gruppe2 = new Gruppe(gruppenID2, "Fun Gruppe", new Person("Dracula"));
    List<Gruppe> gruppen = List.of(gruppe1, gruppe2);
    when(gruppenRepository.findAll()).thenReturn(gruppen);

    assertThat(gruppenService.getGruppen()).containsExactly(gruppe1, gruppe2);
  }

  @Test
  @DisplayName("getGruppe gibt die gesuchte Gruppe zurück")
  void test_12() {
    int gruppenID1 = 1;
    int gruppenID2 = 2;
    Gruppe gruppe1 = new Gruppe(gruppenID1, "Gruppe des Spaßes", new Person("Hannibal"));
    Gruppe gruppe2 = new Gruppe(gruppenID2, "Gruppe des Spaßes", new Person("Hannibal"));
    when(gruppenRepository.findById(gruppenID1)).thenReturn(Optional.of(gruppe1));
    when(gruppenRepository.findById(gruppenID2)).thenReturn(Optional.of(gruppe2));

    assertThat(gruppenService.getGruppe(gruppenID1).get()).isEqualTo(gruppe1);
  }

  @Test
  @DisplayName("getGruppenForPerson gibt alle Gruppen der Person zurück")
  void test_13() {
    int gruppenID1 = 1;
    int gruppenID2 = 2;
    int gruppenID3 = 3;
    Person p1 = new Person("Hanni");
    Person p2 = new Person("Nanni");
    Gruppe gruppe1 = new Gruppe(gruppenID1, "Tanzen", p1);
    Gruppe gruppe2 = new Gruppe(gruppenID2, "Reiten", p1);
    gruppe2.addTeilnehmer(p2);
    Gruppe gruppe3 = new Gruppe(gruppenID3, "Singen", p2);
    List<Gruppe> gruppen = List.of(gruppe1, gruppe2, gruppe3);
    when(gruppenRepository.findAll()).thenReturn(gruppen);

    assertThat(gruppenService.getGruppenForPerson(p1)).containsExactly(gruppe1,
        gruppe2);
  }

  @Test
  @DisplayName("getGruppenForPerson gibt alle Gruppen der Person zurück")
  void test_14() {
    int gruppenID1 = 1;
    int gruppenID2 = 2;
    int gruppenID3 = 3;
    Person p1 = new Person("Hanni");
    Person p2 = new Person("Nanni");
    Gruppe gruppe1 = new Gruppe(gruppenID1, "Tanzen", p1);
    Gruppe gruppe2 = new Gruppe(gruppenID2, "Reiten", p1);
    gruppe2.addTeilnehmer(p2);
    Gruppe gruppe3 = new Gruppe(gruppenID3, "Singen", p2);
    List<Gruppe> gruppen = List.of(gruppe1, gruppe2, gruppe3);
    when(gruppenRepository.findAll()).thenReturn(gruppen);

    assertThatThrownBy(() -> gruppenService.getGruppenForPerson(null)).hasMessage(
        "Person darf nicht null sein").isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("getOpenGruppenForPerson gibt alle offenen Gruppen der Person zurück")
  void test_15() {
    int gruppenID1 = 1;
    int gruppenID2 = 2;
    int gruppenID3 = 3;
    int gruppenID4 = 4;
    Person p1 = new Person("Hanni");
    Person p2 = new Person("Nanni");
    Gruppe gruppe1 = new Gruppe(gruppenID1, "Tanzen", p1);
    Gruppe gruppe2 = new Gruppe(gruppenID2, "Reiten", p1);
    gruppe2.addTeilnehmer(p2);
    Gruppe gruppe3 = new Gruppe(gruppenID3, "Singen", p2);
    Gruppe gruppe4 = new Gruppe(gruppenID4, "Backen", p1);
    gruppe4.schliessen();
    List<Gruppe> gruppen = List.of(gruppe1, gruppe2, gruppe3);
    when(gruppenRepository.findAll()).thenReturn(gruppen);

    assertThat(gruppenService.getOpenGruppenForPerson(p1.githubHandle())).containsExactly(gruppe1,
        gruppe2);
  }

  @Test
  @DisplayName("getGruppenForPerson wirft exception, wenn null übergeben wird")
  void test_16() {
    assertThatThrownBy(() -> gruppenService.getOpenGruppenForPerson(null)).hasMessage(
        "GithubHandle darf nicht null sein").isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("getClosedGruppenForPerson gibt alle geschlossenen Gruppen der Person zurück")
  void test_17() {
    int gruppenID1 = 1;
    int gruppenID2 = 2;
    int gruppenID3 = 3;
    int gruppenID4 = 4;
    Person p1 = new Person("Hanni");
    Person p2 = new Person("Nanni");
    Gruppe gruppe1 = new Gruppe(gruppenID1, "Tanzen", p1);
    Gruppe gruppe2 = new Gruppe(gruppenID2, "Reiten", p1);
    gruppe2.addTeilnehmer(p2);
    Gruppe gruppe3 = new Gruppe(gruppenID3, "Singen", p2);
    Gruppe gruppe4 = new Gruppe(gruppenID4, "Backen", p1);
    gruppe1.schliessen();
    gruppe2.schliessen();
    gruppe3.schliessen();
    List<Gruppe> gruppen = List.of(gruppe1, gruppe2, gruppe3);
    when(gruppenRepository.findAll()).thenReturn(gruppen);

    assertThat(gruppenService.getClosedGruppenForPerson(p1.githubHandle())).containsExactly(gruppe1,
        gruppe2);
  }

  @Test
  @DisplayName("getOpenGruppenForPerson wirft exception, wenn null übergeben wird")
  void test_18() {
    assertThatThrownBy(() -> gruppenService.getOpenGruppenForPerson(null)).hasMessage(
        "GithubHandle darf nicht null sein").isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("getClosedGruppenForPerson gibt alle geschlossenen Gruppen der Person zurück")
  void test_19() {
    int gruppenID1 = 1;
    int gruppenID2 = 2;
    int gruppenID3 = 3;
    int gruppenID4 = 4;
    Person p1 = new Person("Hanni");
    Person p2 = new Person("Nanni");
    Gruppe gruppe1 = new Gruppe(gruppenID1, "Tanzen", p1);
    Gruppe gruppe2 = new Gruppe(gruppenID2, "Reiten", p1);
    gruppe2.addTeilnehmer(p2);
    Gruppe gruppe3 = new Gruppe(gruppenID3, "Singen", p2);
    Gruppe gruppe4 = new Gruppe(gruppenID4, "Backen", p1);
    gruppe1.schliessen();
    gruppe2.schliessen();
    gruppe3.schliessen();
    List<Gruppe> gruppen = List.of(gruppe1, gruppe2, gruppe3);
    when(gruppenRepository.findAll()).thenReturn(gruppen);

    assertThat(gruppenService.getClosedGruppenForPerson(p1.githubHandle())).containsExactly(gruppe1,
        gruppe2);
  }

  @Test
  @DisplayName("getClosedGruppenForPerson wirft exception, wenn null übergeben wird")
  void test_20() {
    assertThatThrownBy(() -> gruppenService.getClosedGruppenForPerson(null)).hasMessage(
        "GithubHandle darf nicht null sein").isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("getSchuldenForGruppe gibt alle Schulden einer Gruppe zurück")
  void test_21() {
    int gruppenID1 = 1;
    int gruppenID2 = 2;
    Person p1 = new Person("Hanni");
    Person p2 = new Person("Nanni");
    Person p3 = new Person("Bond");
    Gruppe gruppe1 = new Gruppe(gruppenID1, "Tanzen", p1);
    Gruppe gruppe2 = new Gruppe(gruppenID2, "Reiten", p1);
    gruppe1.addTeilnehmer(p2);
    gruppe1.addTeilnehmer(p3);
    gruppe2.addTeilnehmer(p2);
    gruppe1.addAusgabe(new Ausgabe(p1, "Samba", Set.of(p1, p2), new Geld(10000)));
    gruppe1.addAusgabe(new Ausgabe(p1, "Tango", Set.of(p1, p2, p3), new Geld(10000)));
    gruppe2.addAusgabe(new Ausgabe(p2, "Samba", Set.of(p1, p2), new Geld(10000)));
    List<Gruppe> gruppen = List.of(gruppe1, gruppe2);
    when(gruppenRepository.findById(1)).thenReturn(Optional.of(gruppe1));
    when(gruppenRepository.findById(2)).thenReturn(Optional.of(gruppe2));
    assertThat(gruppenService.getSchuldenForGruppe(gruppenID1)).containsExactly(
        new Schuld(p2, p1, new Geld(8333), gruppe1.name()),
        new Schuld(p3, p1, new Geld(3333), gruppe2.name())
    );
  }

  @Test
  @DisplayName("getSchuldenForGruppe wirft exception, wenn null übergeben wird")
  void test_22() {
    assertThatThrownBy(() -> gruppenService.getSchuldenForGruppe(null)).hasMessage(
        "GruppenID darf nicht null sein").isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("getSchuldenZuZahlen wirft exception, wenn null übergeben wird")
  void test_24() {
    assertThatThrownBy(() -> gruppenService.getSchuldenZuZahlen(null)).hasMessage(
        "Person darf nicht null sein").isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("getSchuldenZuEmpfangen wirft exception, wenn null übergeben wird")
  void test_26() {
    assertThatThrownBy(() -> gruppenService.getSchuldenZuEmpfangen(null)).hasMessage(
        "Person darf nicht null sein").isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("darfTeilnehmerHinzugefuegtWerden gibt true zurück, wenn Gruppe noch keine Ausgaben hat und offen ist")
  void test_27() {
    int gruppenID1 = 1;
    int gruppenID2 = 2;
    int gruppenID3 = 3;
    Person p1 = new Person("JensiMausi");
    Gruppe gruppe1 = new Gruppe(gruppenID1, "Javaland", p1);
    Gruppe gruppe2 = new Gruppe(gruppenID2, "Javaland", p1);
    Gruppe gruppe3 = new Gruppe(gruppenID3, "Javaland", p1);

    gruppe2.addAusgabe(new Ausgabe(p1, "Hotdogstand", Set.of(p1), new Geld(500)));
    gruppe3.schliessen();

    when(gruppenRepository.findById(gruppenID1)).thenReturn(Optional.of(gruppe1));
    when(gruppenRepository.findById(gruppenID2)).thenReturn(Optional.of(gruppe2));
    when(gruppenRepository.findById(gruppenID3)).thenReturn(Optional.of(gruppe3));

    assertThat(gruppenService.darfTeilnehmerHinzugefuegtWerden(gruppenID1)).isTrue();
    assertThat(gruppenService.darfTeilnehmerHinzugefuegtWerden(gruppenID2)).isFalse();
  }

  @Test
  @DisplayName("darfTeilnehmerHinzugefuegtWerden wirft exception, wenn null übergeben wird")
  void test_28() {
    assertThatThrownBy(() -> gruppenService.darfTeilnehmerHinzugefuegtWerden(null)).hasMessage(
        "GruppenID darf nicht null sein").isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("personHatZugriffAufGruppe gibt true zurück, wenn Person teilnehmer der Gruppe ist")
  void test_29() {
    int gruppenID1 = 1;
    int gruppenID2 = 2;
    int gruppenID3 = 3;
    Person p1 = new Person("JensiMausi");
    Person p2 = new Person("MatziSpatzi");
    Gruppe gruppe1 = new Gruppe(gruppenID1, "Javaland", p1);
    Gruppe gruppe2 = new Gruppe(gruppenID2, "Javaland", p2);

    gruppe1.addTeilnehmer(p2);

    when(gruppenRepository.findById(gruppenID1)).thenReturn(Optional.of(gruppe1));
    when(gruppenRepository.findById(gruppenID2)).thenReturn(Optional.of(gruppe2));

    assertThat(gruppenService.personHatZugriffAufGruppe(p1.githubHandle(), gruppenID2)).isFalse();
    assertThat(gruppenService.personHatZugriffAufGruppe(p2.githubHandle(), gruppenID2)).isTrue();
  }

  @Test
  @DisplayName("personHatZugriffAufGruppe wirft exception, wenn null übergeben wird")
  void test_30() {
    int gruppenID1 = 1;
    Person p1 = new Person("JensiMausi");
    Gruppe gruppe1 = new Gruppe(gruppenID1, "Javaland", p1);
    assertThatThrownBy(
        () -> gruppenService.personHatZugriffAufGruppe(p1.githubHandle(), null)).hasMessage(
        "GruppenID darf nicht null sein").isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> gruppenService.personHatZugriffAufGruppe(null, gruppenID1)).hasMessage(
        "GitHubHandle darf nicht null sein").isInstanceOf(IllegalArgumentException.class);
  }

}
