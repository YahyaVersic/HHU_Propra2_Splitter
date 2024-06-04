package com.example.splitter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.example.splitter.application.service.GruppenService;
import com.example.splitter.controller.webdomain.WebAuslage;
import com.example.splitter.controller.webdomain.WebGruppe;
import com.example.splitter.domain.model.Ausgabe;
import com.example.splitter.domain.model.Geld;
import com.example.splitter.domain.model.Gruppe;
import com.example.splitter.domain.model.Person;
import com.example.splitter.helper.WithMockOAuth2User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RestController.class)
public class RestControllerTest {

  @MockBean
  GruppenService gruppenService;

  @Autowired
  private MockMvc mvc;

  @Autowired
  ObjectMapper objectMapper;

  private final String EXPECTED_MIME_TYPE = "application/json";


  // /api/gruppen/{id}
  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Get auf /api/gruppen/{id} funktioniert (mit status 200)")
  void test_1() throws Exception {
    //Arrange
    Person p = new Person("A");
    Gruppe g = new Gruppe(1, "Mondgruppe", new Person("A"));
    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));
    WebGruppe w = new WebGruppe(1, "Mondgruppe", Set.of(p), true, new ArrayList<>());

    //Act
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/gruppen/1")).andReturn();
    String actualContentType = result.getResponse().getContentType();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(EXPECTED_MIME_TYPE).isEqualTo(actualContentType);
    assertThat(result.getResponse().getContentAsString()).isEqualTo(
        objectMapper.writeValueAsString(w)
    );
  }

  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Get auf /api/gruppen/{id} funktioniert nicht bei fehlender Gruppe (mit status 404)")
  void test_2() throws Exception {
    //Act
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/gruppen/1")).andReturn();
    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
  }

  // /api/gruppen
  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Post request auf /api/gruppen funktioniert")
  void test_3() throws Exception {
    //Arrange
    WebGruppe gruppe = new WebGruppe("Mondgruppe", new String[]{"A"});
    Gruppe x = new Gruppe(any(), "Mondgruppe", new Person("A"));
    when(gruppenService.addGruppe(x)).thenReturn(new Gruppe(1, "Mondgruppe", new Person("A")));

    //Act
    MvcResult result = mvc.perform(post("/api/gruppen")
            .with(csrf()) //Warum mit csrf?
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(gruppe)))
        .andReturn();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(201);
    assertThat(result.getResponse().getContentAsString()).isEqualTo("1");
    assertThat(result.getResponse().getContentType()).isEqualTo(EXPECTED_MIME_TYPE);
  }


  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Post request auf /api/gruppen mit falschem json gibt status 400 ")
  void test_4() throws Exception {
    WebGruppe gruppe = new WebGruppe(null, null);
    MvcResult result = mvc.perform(
        post("/api/gruppen").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(gruppe))).andReturn();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(400);
  }


  // /api/user/{login}/gruppen
  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Get auf /api/user/{login}/gruppen funktioniert (mit status 200)")
  void test_5() throws Exception {
    //Arrange
    Person p = new Person("A");
    Gruppe g1 = new Gruppe(1, "Mondgruppe", new Person("A"));
    Gruppe g2 = new Gruppe(2, "Sonnengruppe", new Person("A"));
    when(gruppenService.getGruppenForPerson(p)).thenReturn(List.of(g1, g2));
    WebGruppe w1 = WebGruppe.fromGruppe(g1);
    WebGruppe w2 = WebGruppe.fromGruppe(g2);

    //Act
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/user/A/gruppen")).andReturn();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(EXPECTED_MIME_TYPE).isEqualTo(result.getResponse().getContentType());
    assertThat(result.getResponse().getContentAsString()).isEqualTo(
        objectMapper.writeValueAsString(List.of(w1, w2)));
  }

  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Get auf /api/user/{login}/gruppen ist leer, wenn der user keine Gruppen hat")
  void test_6() throws Exception {
    //Act
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/user/A/gruppen")).andReturn();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
  }


  // /api/gruppen/{id}/schliessen post
  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Post request auf /api/gruppen/{id}/schliessen funktioniert")
  void test_7() throws Exception { //Clean
    //Arrange
    Integer gruppenID = 1;
    when(gruppenService.getGruppe(gruppenID)).thenReturn(Optional.of(
        new Gruppe(gruppenID, "Mondgruppe",
            new Person("B"))));

    //Act
    MvcResult result = mvc.perform(post("/api/gruppen/" + gruppenID + "/schliessen")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(gruppenID))).andReturn();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    verify(gruppenService, times(1)).getGruppe(gruppenID);
    verify(gruppenService, times(1)).schliesseGruppe(gruppenID);
  }


  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Post request auf  /api/gruppen/{id}/schliessen mit falschem json gibt status 404 ")
  void test_8() throws Exception {
    Integer gruppenID = 1;
    MvcResult result = mvc.perform(post("/api/gruppen/" + gruppenID + "/schliessen")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(gruppenID))).andReturn();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
    verify(gruppenService, times(1)).getGruppe(gruppenID);
    verify(gruppenService, times(0)).schliesseGruppe(any());
  }


  // /api/gruppen/{id}/auslagen
  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Post request auf /api/gruppen/{id}/auslagen funktioniert")
  void test_9() throws Exception {
    //Arrange
    Integer gruppenID = 1;
    Person a = new Person("A");
    Person b = new Person("B");
    Gruppe gruppe = new Gruppe(gruppenID, "Mondgruppe", Set.of(a, b),
        List.of(new Ausgabe(a, "Stuff", Set.of(b), new Geld(1, 0))), true);
    when(gruppenService.getGruppe(gruppenID)).thenReturn(Optional.of(gruppe));
    WebAuslage auslage = new WebAuslage("Pommes", "A", 123, new String[]{"B"});

    //Act
    MvcResult result = mvc.perform(post("/api/gruppen/" + gruppenID + "/auslagen")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(auslage))).andReturn();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(201);
  }


  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Post request auf  /api/gruppen/{id}/auslagen mit falschem json gibt status 404 ")
  void test_10() throws Exception {
    //Arrange
    Integer gruppenID = 1;
    when(gruppenService.getGruppe(gruppenID)).thenReturn(
        Optional.of(new Gruppe(gruppenID, "Mondgruppe", new Person("A"))));
    WebAuslage auslage = new WebAuslage("Pommes", null, 0, null);

    //Act
    MvcResult result = mvc.perform(post("/api/gruppen/" + gruppenID + "/auslagen").with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(auslage))).andReturn();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(400);
    verify(gruppenService, times(1)).getGruppe(gruppenID);
  }


  // /api/gruppen/{id}/ausgleich get
  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Get auf /api/gruppen/{id}/ausgleich funktioniert (mit status 200)")
  void test_11() throws Exception {
    //Arrange
    Integer gruppenID = 1;
    Person p1 = new Person("A");
    Person p2 = new Person("B");
    Gruppe g = new Gruppe(1, "Mondgruppe", p1);
    g.addTeilnehmer(p2);
    Ausgabe ausgabe = new Ausgabe(p1, "Essen", Set.of(p2), new Geld(100, 0));
    g.addAusgabe(ausgabe);
    when(gruppenService.getGruppe(1)).thenReturn(Optional.of(g));

    WebAuslage auslage = WebAuslage.fromAusgabe(ausgabe);

    //Act
    MvcResult result = mvc.perform(
        MockMvcRequestBuilders.get("/api/gruppen/" + gruppenID + "/ausgleich")).andReturn();
    String actualContentType = result.getResponse().getContentType();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(EXPECTED_MIME_TYPE).isEqualTo(actualContentType);
    verify(gruppenService, times(1)).getSchuldenForGruppe(1);
  }

  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Get auf /api/gruppen/{id}/ausgleich funktioniert nicht bei fehlender Gruppe (mit status 404)")
  void test_12() throws Exception {
    Integer gruppenID = 1;
    //Act
    MvcResult result = mvc.perform(
        MockMvcRequestBuilders.get("/api/gruppen/" + gruppenID + "/ausgleich")).andReturn();
    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
    verify(gruppenService, times(1)).getGruppe(1);
  }


  // Randfaelle
  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Get auf /api/gruppen/{id}/ausgleich mit falscher ID gibt status 404")
  void test_13() throws Exception {
    String gruppenID = "Banane";
    //Act
    MvcResult result = mvc.perform(
        MockMvcRequestBuilders.get("/api/gruppen/" + gruppenID + "/ausgleich")).andReturn();
    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
  }


  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Post request auf  /api/gruppen/{id}/auslagen mit falscher ID gibt status 404 ")
  void test_14() throws Exception {
    //Arrange
    String gruppenID = "Banane";
    WebAuslage auslage = new WebAuslage("Pommes", "A", 1, new String[]{"B"});

    //Act
    MvcResult result = mvc.perform(post("/api/gruppen/" + gruppenID + "/auslagen").with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(auslage))).andReturn();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
  }


  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Post request auf /api/gruppen/{id}/auslagen mit bei nicht vorhandener Gruppe gibt status 404 ")
  void test_15() throws Exception {
    //Arrange
    Integer gruppenID = 1;
    WebAuslage auslage = new WebAuslage("Pommes", "A", 1, new String[]{"B"});
    when(gruppenService.getGruppe(gruppenID)).thenReturn(Optional.empty());

    //Act
    MvcResult result = mvc.perform(post("/api/gruppen/" + gruppenID + "/auslagen").with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(auslage))).andReturn();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
  }


  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Post request auf /api/gruppen/{id}/auslagen mit bei geschlossener Gruppe gibt status 409")
  void test_16() throws Exception {
    //Arrange
    Integer gruppenID = 1;
    WebAuslage auslage = new WebAuslage("Pommes", "A", 1, new String[]{"B"});
    Gruppe gr = new Gruppe(1, "Mondgruppe", new Person("A"));
    gr.schliessen();
    when(gruppenService.getGruppe(gruppenID))
        .thenReturn(Optional.of(gr));

    //Act
    MvcResult result = mvc.perform(post("/api/gruppen/" + gruppenID + "/auslagen").with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(auslage))).andReturn();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(409);
  }


  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Post request auf /api/gruppen/{id}/schliessen gibt bei falscher ID status 404 ")
  void test_17() throws Exception {
    //Arrange
    String gruppenID = "Banane";

    //Act
    MvcResult result = mvc.perform(post("/api/gruppen/" + gruppenID + "/schliessen").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(null)))
        .andReturn();

    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
  }

  @Test
  @WithMockOAuth2User(login = "A")
  @DisplayName("Get auf /api/gruppen/{id} funktioniert nicht bei falscher ID (mit status 404)")
  void test_18() throws Exception {
    String gruppenID = "Banane";
    //Act
    MvcResult result = mvc.perform(
        MockMvcRequestBuilders.get("/api/gruppen/" + gruppenID)).andReturn();
    //Assert
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
  }


}
