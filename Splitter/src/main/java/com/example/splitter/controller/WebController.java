package com.example.splitter.controller;

import com.example.splitter.application.service.GruppenService;
import com.example.splitter.domain.model.Ausgabe;
import com.example.splitter.domain.model.Gruppe;
import com.example.splitter.domain.model.Person;
import com.example.splitter.dto.AusgabenForm;
import com.example.splitter.dto.NeueGruppeForm;
import com.example.splitter.dto.NeuesMitgliedForm;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class WebController {

  private final GruppenService gruppenService;

  public WebController(GruppenService gruppenService
  ) {
    this.gruppenService = gruppenService;
  }

  @GetMapping("/")
  public String getMainPage() {
    return "index";
  }

  @GetMapping("/persoenlicheUebersicht")
  public String getPersoenlicheUebersicht(Model model,
      @AuthenticationPrincipal OAuth2User loggedUser
  ) {
    String githubHandle = loggedUser.getAttribute("login");

    model.addAttribute("gitHandle", githubHandle);
    model.addAttribute("open_group", gruppenService.getOpenGruppenForPerson(githubHandle));
    model.addAttribute("closed_group",
        gruppenService.getClosedGruppenForPerson(githubHandle));
    return "persoenlicheUebersicht";
  }

  @GetMapping("/schulduebersicht")
  public String getSchulduebersicht(Model model, @AuthenticationPrincipal OAuth2User loggedUser) {
    Person p = new Person(loggedUser.getAttribute("login"));
    model.addAttribute("schuldenZuZahlen", gruppenService.getSchuldenZuZahlen(p));
    model.addAttribute("schuldenZuEmpfangen", gruppenService.getSchuldenZuEmpfangen(p));
    return "schulduebersicht";
  }

  @GetMapping("/createGroup")
  public String createGroup() {
    return "neueGruppe";
  }

  @PostMapping("/createGroup")
  public String newGroup(Model model,
      @Valid NeueGruppeForm neueGruppeForm, BindingResult bindingResult,
      @AuthenticationPrincipal OAuth2User loggedUser
  ) {
    if (bindingResult.hasErrors()) {
      return "redirect:/createGroup";
    }
    Person p = new Person(loggedUser.getAttribute("login"));
    Gruppe gruppe = gruppenService.addGruppe(new Gruppe(null, neueGruppeForm.name(), p));
    return "redirect:/gruppenansicht/" + gruppe.id();
  }

  @GetMapping("/gruppenansicht/{gruppenID}")
  public String gruppenansicht(@PathVariable("gruppenID") Integer gruppenID, Model model,
      @AuthenticationPrincipal OAuth2User loggedUser) {
    if (!gruppenService.personHatZugriffAufGruppe(loggedUser.getAttribute("login"), gruppenID)) {
      return "error";
    }
    Person p = new Person(loggedUser.getAttribute("login"));
    Gruppe gruppe = gruppenService.getGruppe(gruppenID).get();
    model.addAttribute("gruppe", gruppe);
    model.addAttribute("schuldstaende", gruppenService.getSchuldenForGruppe(gruppenID));
    model.addAttribute("person", p);
    return "gruppenuebersicht";
  }

  @GetMapping("/gruppenansicht/{gruppenID}/ausgabeHinzufuegen")
  public String ausgabeHinzufuegen(Model model, @PathVariable("gruppenID") Integer gruppenID,
      @AuthenticationPrincipal OAuth2User loggedUser) {
    if (!gruppenService.personHatZugriffAufGruppe(loggedUser.getAttribute("login"), gruppenID)) {
      return "error";
    }
    model.addAttribute("gruppe", gruppenService.getGruppe(gruppenID).get());
    return "ausgabe";
  }

  @PostMapping("/gruppenansicht/{gruppenID}/ausgabeHinzufuegen")
  public String addAusgabe(@Valid AusgabenForm ausgabenForm, BindingResult bindingResult,
      @PathVariable("gruppenID") Integer gruppenID,
      @AuthenticationPrincipal OAuth2User loggedUser) {
    if (!gruppenService.personHatZugriffAufGruppe(loggedUser.getAttribute("login"), gruppenID)) {
      return "error";
    } else if (bindingResult.hasErrors()) {
      return "redirect:/gruppenansicht/" + gruppenID + "/ausgabeHinzufuegen";
    }
    gruppenService.addAusgabe(gruppenID, new Ausgabe(ausgabenForm));
    return "redirect:/gruppenansicht/" + gruppenID;
  }


  @GetMapping("gruppenansicht/{gruppenId}/gruppeSchliessen")
  public String gruppeSchliessen(Model model, @PathVariable("gruppenId") Integer gruppenId,
      @AuthenticationPrincipal OAuth2User loggedUser) {
    if (!gruppenService.personHatZugriffAufGruppe(loggedUser.getAttribute("login"), gruppenId)
        || !gruppenService.getGruppe(gruppenId).get().isOffen()) {
      return "error";
    }
    model.addAttribute("gruppenname", gruppenService.getGruppe(gruppenId).get().name());
    model.addAttribute("gruppenID", gruppenId);
    return "gruppeSchliessen";
  }

  @PostMapping("gruppenansicht/{gruppenId}/gruppeSchliessen")
  public String gruppeSchliessenPost(Model model, @PathVariable("gruppenId") Integer gruppenId,
      @AuthenticationPrincipal OAuth2User loggedUser) {
    if (!gruppenService.personHatZugriffAufGruppe(loggedUser.getAttribute("login"), gruppenId)
        || !gruppenService.getGruppe(gruppenId).get().isOffen()) {
      return "error";

    }
    gruppenService.schliesseGruppe(gruppenId);
    return "redirect:/persoenlicheUebersicht";
  }


  @GetMapping("/gruppenansicht/{gruppenID}/mitgliedHinzufuegen")
  public String mitgliedHinzufuegen(Model model, @PathVariable("gruppenID") Integer gruppenID,
      @AuthenticationPrincipal OAuth2User loggedUser) {
    if (!gruppenService.personHatZugriffAufGruppe(loggedUser.getAttribute("login"), gruppenID)
        || !gruppenService.darfTeilnehmerHinzugefuegtWerden(gruppenID)) {
      return "error";
    }
    model.addAttribute("gruppenname", gruppenService.getGruppe(gruppenID).get().name());
    return "neuesMitglied";
  }

  @PostMapping("/gruppenansicht/{gruppenID}/mitgliedHinzufuegen")
  public String postMitgliedHinzufuegen(Model model, @PathVariable("gruppenID") Integer gruppenID,
      @Valid NeuesMitgliedForm neuesMitgliedForm, BindingResult bindingResult,
      @AuthenticationPrincipal OAuth2User loggedUser
  ) {
    if (!gruppenService.personHatZugriffAufGruppe(loggedUser.getAttribute("login"), gruppenID)) {
      return "error";
    } else if (bindingResult.hasErrors()) {
      return "redirect:/gruppenansicht/" + gruppenID + "/mitgliedHinzufuegen";
    }
    gruppenService.addTeilnehmer(gruppenID, new Person(neuesMitgliedForm.name()));
    return "redirect:/gruppenansicht/" + gruppenID;
  }


}