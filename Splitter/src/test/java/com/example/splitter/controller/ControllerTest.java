package com.example.splitter.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.example.splitter.application.service.GruppenService;
import com.example.splitter.domain.model.Ausgabe;
import com.example.splitter.domain.model.Geld;
import com.example.splitter.domain.model.Gruppe;
import com.example.splitter.domain.model.Person;
import com.example.splitter.helper.WithMockOAuth2User;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@WebMvcTest
public class ControllerTest {

  @MockBean
  GruppenService gruppenService;

  @Autowired
  MockMvc mmvc;


  //Testen der Startseite
  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Get Mapping auf Startseite liefert startseite.html")
  public void test_1() throws Exception {
    mmvc.perform(MockMvcRequestBuilders.get("/"))
        .andExpect(MockMvcResultMatchers.view().name("index"));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Get Mapping auf Startseite ist kein Bad Request")
  public void test_2() throws Exception {
    mmvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isOk());
  }


  //Testen der Gruppe-Erstellen Seite
  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Get Mapping auf Gruppenersteller liefert neueGruppe.html")
  public void test_3() throws Exception {
    mmvc.perform(MockMvcRequestBuilders.get("/createGroup"))
        .andExpect(MockMvcResultMatchers.view().name("neueGruppe"));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Get Mapping auf Gruppenersteller ist kein Bad Request")
  public void test_4() throws Exception {
    mmvc.perform(MockMvcRequestBuilders.get("/createGroup"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Redirect nach legaler Gruppe-Erstellen Anfrage funktioniert")
  public void test_5() throws Exception {
    //Arrange
    Person p = new Person("Klaus");
    Gruppe g1 = new Gruppe(any(), "Mondgruppe", p);
    Gruppe g2 = new Gruppe(1, "Mondgruppe", p);
    when(gruppenService.addGruppe(g1)).thenReturn(g2);
    //Act & Assert
    mmvc.perform(
            MockMvcRequestBuilders.post("/createGroup").param("name", "Mondgruppe").with(csrf()))
        .andExpect(MockMvcResultMatchers.redirectedUrl("/gruppenansicht/1"));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei leerem Gruppennamen im Post (illegale Anfrage) wird wieder das Formular angezeigt und keine Gruppe erstellt")
  public void test_6() throws Exception {
    //Arrange
    Person p = new Person("Klaus");
    Gruppe g1 = new Gruppe(any(), "Mondgruppe", p);
    Gruppe g2 = new Gruppe(1, "Mondgruppe", p);
    when(gruppenService.addGruppe(g1)).thenReturn(g2);
    //Act & Assert
    mmvc.perform(
            MockMvcRequestBuilders.post("/createGroup").param("name", "").with(csrf()))
        .andExpect(MockMvcResultMatchers.redirectedUrl("/createGroup"));
  }


  //Testen der Gruppenuebersicht
  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Zugriff auf fremde Gruppenuebersicht wird die Error-Seite zurückgegeben")
  public void test_8() throws Exception {
    Person p = new Person("Otto");
    Gruppe g = new Gruppe(1, "Testgruppe", p);
    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));

    mmvc.perform(MockMvcRequestBuilders.get("/gruppenansicht/1"))
        .andExpect(MockMvcResultMatchers.view().name("error"));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Zugriff auf eigene Gruppe wird die gruppenuebersicht.html zurückgegeben")
  public void test_9() throws Exception {
    Person p = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p);
    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);
    mmvc.perform(MockMvcRequestBuilders.get("/gruppenansicht/1"))
        .andExpect(MockMvcResultMatchers.view().name("gruppenuebersicht"));
  }

  //Testen der Ausgabe-Erstellen Seite
  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Get-Anfrage an Ausgabe erstellen wird ausgabe.html zurückgegeben")
  public void test_10() throws Exception {
    Person p = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p);
    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);

    mmvc.perform(MockMvcRequestBuilders.get("/gruppenansicht/1/ausgabeHinzufuegen"))
        .andExpect(MockMvcResultMatchers.view().name("ausgabe"));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Get-Anfrage an Ausgabe erstellen wird Fehlerseite zurückgegeben, wenn User nicht in der Gruppe ist")
  public void test_11() throws Exception {
    Person p = new Person("Otto");
    Gruppe g = new Gruppe(1, "Testgruppe", p);
    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    mmvc.perform(MockMvcRequestBuilders.get("/gruppenansicht/1/ausgabeHinzufuegen"))
        .andExpect(MockMvcResultMatchers.view().name("error"));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei legaler Post-Anfrage an Ausgabe erstellen wird Gruppenuebersicht zurückgegeben und Ausgabe wird hinzugefügt")
  public void test_12() throws Exception {
    Person p1 = new Person("Otto");
    Person p2 = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p1);
    g.addTeilnehmer(p2);
    Ausgabe ausgabe = new Ausgabe(p2, "Einkauf", Set.of(p1), new Geld(33));

    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);

    mmvc.perform(MockMvcRequestBuilders.post("/gruppenansicht/1/ausgabeHinzufuegen")
            .param("name", "Einkauf")
            .param("preis", "0.33")
            .param("bezahler", "Klaus")
            .param("profiteure", "Otto")
            .with(csrf()))
        .andExpect(MockMvcResultMatchers.view().name("redirect:/gruppenansicht/1"));
    verify(gruppenService, times(1)).addAusgabe(1, ausgabe);
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei unberechtigter Post Anfrage an Ausgabe hinzufügen wird die Fehlerseite zurückgegeben und keine Ausgabe hinzugefuegt")
  public void test_13() throws Exception {
    Person p1 = new Person("Otto");
    Person p2 = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p1);
    g.addTeilnehmer(p2);

    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(false);

    mmvc.perform(MockMvcRequestBuilders.post("/gruppenansicht/1/ausgabeHinzufuegen")
            .param("name", "Einkauf")
            .param("preis", "0.33")
            .param("bezahler", "Klaus")
            .param("profiteure", "Otto")
            .with(csrf()))
        .andExpect(MockMvcResultMatchers.view().name("error"));
    verify(gruppenService, times(0)).addAusgabe(any(), any());
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Post Anfrage an Ausgabe hinzufuegen mit negativem Geldbetrag gelangt man wieder auf das Formular und keine Ausgabe wird hinzugefuegt")
  public void test_14() throws Exception {
    Person p1 = new Person("Otto");
    Person p2 = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p1);
    g.addTeilnehmer(p2);

    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);

    mmvc.perform(MockMvcRequestBuilders.post("/gruppenansicht/1/ausgabeHinzufuegen")
            .param("name", "Einkauf")
            .param("preis", "-0.33")
            .param("bezahler", "Klaus")
            .param("profiteure", "Otto")
            .with(csrf()))
        .andExpect(
            MockMvcResultMatchers.view().name("redirect:/gruppenansicht/1/ausgabeHinzufuegen"));
    verify(gruppenService, times(0)).addAusgabe(any(), any());
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Post Anfrage an Ausgabe hinzufuegen mit 0 als Geldbetrag gelangt man wieder auf das Formular und keine Ausgabe wird hinzugefuegt")
  public void test_15() throws Exception {
    Person p1 = new Person("Otto");
    Person p2 = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p1);
    g.addTeilnehmer(p2);

    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);

    mmvc.perform(MockMvcRequestBuilders.post("/gruppenansicht/1/ausgabeHinzufuegen")
            .param("name", "Einkauf")
            .param("preis", "0")
            .param("bezahler", "Klaus")
            .param("profiteure", "Otto")
            .with(csrf()))
        .andExpect(
            MockMvcResultMatchers.view().name("redirect:/gruppenansicht/1/ausgabeHinzufuegen"));
    verify(gruppenService, times(0)).addAusgabe(any(), any());
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Post Anfrage an Ausgabe hinzufuegen mit leerem String als Name gelangt man wieder auf das Formular und keine Ausgabe wird hinzugefuegt")
  public void test_16() throws Exception {
    Person p1 = new Person("Otto");
    Person p2 = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p1);
    g.addTeilnehmer(p2);

    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);

    mmvc.perform(MockMvcRequestBuilders.post("/gruppenansicht/1/ausgabeHinzufuegen")
            .param("name", "")
            .param("preis", "0")
            .param("bezahler", "Klaus")
            .param("profiteure", "Otto")
            .with(csrf()))
        .andExpect(
            MockMvcResultMatchers.view().name("redirect:/gruppenansicht/1/ausgabeHinzufuegen"));
    verify(gruppenService, times(0)).addAusgabe(any(), any());
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Post Anfrage an Ausgabe hinzufuegen mit leerem String als Bezahler gelangt man wieder auf das Formular und keine Ausgabe wird hinzugefuegt")
  public void test_17() throws Exception {
    Person p1 = new Person("Otto");
    Person p2 = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p1);
    g.addTeilnehmer(p2);

    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);

    mmvc.perform(MockMvcRequestBuilders.post("/gruppenansicht/1/ausgabeHinzufuegen")
            .param("name", "")
            .param("preis", "0")
            .param("bezahler", "")
            .param("profiteure", "Otto")
            .with(csrf()))
        .andExpect(
            MockMvcResultMatchers.view().name("redirect:/gruppenansicht/1/ausgabeHinzufuegen"));
    verify(gruppenService, times(0)).addAusgabe(any(), any());
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Post Anfrage an Ausgabe hinzufuegen ohne Profiteure gelangt man wieder auf das Formular und keine Ausgabe wird hinzugefuegt")
  public void test_18() throws Exception {
    Person p1 = new Person("Otto");
    Person p2 = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p1);
    g.addTeilnehmer(p2);

    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);

    mmvc.perform(MockMvcRequestBuilders.post("/gruppenansicht/1/ausgabeHinzufuegen")
            .param("name", "")
            .param("preis", "0")
            .param("bezahler", "")
            .param("profiteure", "")
            .with(csrf()))
        .andExpect(
            MockMvcResultMatchers.view().name("redirect:/gruppenansicht/1/ausgabeHinzufuegen"));
    verify(gruppenService, times(0)).addAusgabe(any(), any());
  }


  //Testen der Mitglied hinzufuegen Seite
  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Get-Anfrage an Mitglied hinzufuegen wird neuesMitglied.html zurückgegeben")
  public void test_19() throws Exception {
    Person p = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p);
    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    when(gruppenService.darfTeilnehmerHinzugefuegtWerden(1)).thenReturn(true);
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);

    mmvc.perform(MockMvcRequestBuilders.get("/gruppenansicht/1/mitgliedHinzufuegen"))
        .andExpect(MockMvcResultMatchers.view().name("neuesMitglied"));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Get-Anfrage an Mitglied hinzufuegen wird error.html zurückgegeben, wenn User nicht in der Gruppe ist")
  public void test_20() throws Exception {
    Person p = new Person("Otto");
    Gruppe g = new Gruppe(1, "Testgruppe", p);
    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(false);
    when(gruppenService.darfTeilnehmerHinzugefuegtWerden(1)).thenReturn(true);

    mmvc.perform(MockMvcRequestBuilders.get("/gruppenansicht/1/mitgliedHinzufuegen"))
        .andExpect(MockMvcResultMatchers.view().name("error"));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Wenn GetAnfrage an Mitglied hinzufuegen, aber bereits eine Ausgabe hinzugefügt, wird error.html zurückgegeben")
  public void test_21() throws Exception {
    Person p = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p);
    g.addAusgabe(new Ausgabe(p, "Nices Ding gekauft", Set.of(p), new Geld(23)));
    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);
    when(gruppenService.darfTeilnehmerHinzugefuegtWerden(1)).thenReturn(false);
    mmvc.perform(MockMvcRequestBuilders.get("/gruppenansicht/1/mitgliedHinzufuegen"))
        .andExpect(MockMvcResultMatchers.view().name("error"));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Valider Post-Anfrage an Mitglied hinzufuegen wird die Gruppenansicht zurueckgegeben und Mitglied hinzugefuegt")
  public void test_22() throws Exception {
    //Arrange

    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);
    //Act & Assert
    mmvc.perform(
            MockMvcRequestBuilders.post("/gruppenansicht/1/mitgliedHinzufuegen")
                .param("name", "Otto").with(csrf()))
        .andExpect(MockMvcResultMatchers.redirectedUrl("/gruppenansicht/1"));
    verify(gruppenService, times(1)).addTeilnehmer(1, new Person("Otto"));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Post-Anfrage an Mitglied hinzufuegen fuer eine Fremde Gruppe wird die Fehlerseite zurueckgegeben und kein Mitglied hinzugefuegt")
  public void test_23() throws Exception {
    //Arrange
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(false);
    //Act & Assert
    mmvc.perform(
            MockMvcRequestBuilders.post("/gruppenansicht/1/mitgliedHinzufuegen")
                .param("name", "Otto").with(csrf()))
        .andExpect(MockMvcResultMatchers.view().name("error"));
    verify(gruppenService, times(0)).addTeilnehmer(any(), any());
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei leerem Mitgliedsnamen im Post wird wieder das Formular angezeigt und kein Mitglied hinzugefuegt")
  public void test_24() throws Exception {
    //Arrange
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);
    //Act & Assert
    mmvc.perform(
            MockMvcRequestBuilders.post("/gruppenansicht/1/mitgliedHinzufuegen")
                .param("name", "").with(csrf()))
        .andExpect(
            MockMvcResultMatchers.view().name("redirect:/gruppenansicht/1/mitgliedHinzufuegen"));
    verify(gruppenService, times(0)).addTeilnehmer(any(), any());
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei inkorrektem GitHubHandle (Sonderzeichen) im Post wird wieder das Formular angezeigt und kein Mitglied hinzugefuegt")
  public void test_25() throws Exception {
    //Arrange
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);
    //Act & Assert
    mmvc.perform(
            MockMvcRequestBuilders.post("/gruppenansicht/1/mitgliedHinzufuegen")
                .param("name", "---").with(csrf()))
        .andExpect(
            MockMvcResultMatchers.view().name("redirect:/gruppenansicht/1/mitgliedHinzufuegen"));
    verify(gruppenService, times(0)).addTeilnehmer(any(), any());
  }


  //Testen der Schulduebersicht
  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Bei Get-Anfrage an Schuldenübersicht wird schulduebersicht.html zurückgegeben")
  public void test_26() throws Exception {
    Person p = new Person("Klaus");

    mmvc.perform(MockMvcRequestBuilders.get("/schulduebersicht"))
        .andExpect(MockMvcResultMatchers.view().name("schulduebersicht"));
  }

  //Testen der Persoenlichen Uebersicht
  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Eine Person, die in keiner Gruppe Mitglied ist bekommt auf der persönlichen Übersicht keine Gruppen angezeigt")
  public void test_27() throws Exception {
    Person p = new Person("Klaus");
    when(gruppenService.getGruppenForPerson(p)).thenReturn(List.of());

    mmvc.perform(MockMvcRequestBuilders.get("/persoenlicheUebersicht"))
        .andExpect(MockMvcResultMatchers.model().attribute("open_group", List.of()))
        .andExpect(MockMvcResultMatchers.model().attribute("closed_group", List.of()));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Eine Person, die in einer offenen Gruppe Mitglied ist bekommt auf der persönlichen Übersicht nur diese offene Gruppe angezeigt")
  public void test_28() throws Exception {
    Person p = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p);
    when(gruppenService.getOpenGruppenForPerson("Klaus")).thenReturn(List.of(g));
    when(gruppenService.getClosedGruppenForPerson("Klaus")).thenReturn(List.of());
    mmvc.perform(MockMvcRequestBuilders.get("/persoenlicheUebersicht"))
        .andExpect(MockMvcResultMatchers.model().attribute("open_group", List.of(g)))
        .andExpect(MockMvcResultMatchers.model().attribute("closed_group", List.of()));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Eine Person, die in einer geschlossenen Gruppe Mitglied ist bekommt auf der persönlichen Übersicht nur diese geschlossenen Gruppe angezeigt")
  public void test_29() throws Exception {
    Person p = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p);
    g.schliessen();
    when(gruppenService.getOpenGruppenForPerson("Klaus")).thenReturn(List.of());
    when(gruppenService.getClosedGruppenForPerson("Klaus")).thenReturn(List.of(g));
    mmvc.perform(MockMvcRequestBuilders.get("/persoenlicheUebersicht"))
        .andExpect(MockMvcResultMatchers.model().attribute("open_group", List.of()))
        .andExpect(MockMvcResultMatchers.model().attribute("closed_group", List.of(g)));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("In der Übersicht einer Gruppe wird die Gruppe in das Model eingefügt")
  public void test_30() throws Exception {
    Person p = new Person("Klaus");
    Gruppe g = new Gruppe(1, "Testgruppe", p);

    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);

    mmvc.perform(MockMvcRequestBuilders.get("/gruppenansicht/1"))
        .andExpect(MockMvcResultMatchers.model().attribute("gruppe", g));

  }

  //Gruppe schliessen
  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Get auf Gruppe schließen liefert korrekte Ansicht bei eigener offener Gruppe")
  public void test_31() throws Exception {
    Person person = new Person("Klaus");
    Gruppe gruppe = new Gruppe(1, "Mondgruppe", person);
    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(gruppe));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);
    mmvc.perform(MockMvcRequestBuilders.get("/gruppenansicht/1/gruppeSchliessen"))
        .andExpect(MockMvcResultMatchers.view().name("gruppeSchliessen"));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Get auf Gruppe schließen liefert Fehler-Ansicht bei fremde offener Gruppe")
  public void test_32() throws Exception {
    Person person = new Person("Klaus");
    Gruppe gruppe = new Gruppe(1, "Mondgruppe", person);
    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(gruppe));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(false);
    mmvc.perform(MockMvcRequestBuilders.get("/gruppenansicht/1/gruppeSchliessen"))
        .andExpect(MockMvcResultMatchers.view().name("error"));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Get auf Gruppe schließen liefert Fehler-Ansicht bei eigener geschlossen Gruppe")
  public void test_33() throws Exception {
    Person person = new Person("Klaus");
    Gruppe gruppe = new Gruppe(1, "Mondgruppe", person);
    gruppe.schliessen();
    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(gruppe));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);
    mmvc.perform(MockMvcRequestBuilders.get("/gruppenansicht/1/gruppeSchliessen"))
        .andExpect(MockMvcResultMatchers.view().name("error"));
  }

  @Test
  @WithMockOAuth2User(login = "Klaus")
  @DisplayName("Post auf Gruppe schliessen schliesst die Gruppe")
  public void test_34() throws Exception {
    Person person = new Person("Klaus");
    Gruppe gruppe = new Gruppe(1, "Mondgruppe", person);
    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(gruppe));
    when(gruppenService.personHatZugriffAufGruppe("Klaus", 1)).thenReturn(true);
    mmvc.perform(MockMvcRequestBuilders.post("/gruppenansicht/1/gruppeSchliessen").with(csrf()));
    verify(gruppenService, times(1)).schliesseGruppe(1);
  }

}