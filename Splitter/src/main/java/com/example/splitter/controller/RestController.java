package com.example.splitter.controller;


import com.example.splitter.application.service.GruppenService;
import com.example.splitter.controller.webdomain.WebAuslage;
import com.example.splitter.controller.webdomain.WebGruppe;
import com.example.splitter.controller.webdomain.WebSchuld;
import com.example.splitter.domain.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@org.springframework.web.bind.annotation.RestController
public class RestController {

  private final GruppenService gruppenService;

  public RestController(GruppenService gruppenService
  ) {
    this.gruppenService = gruppenService;
  }

  @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseEntity resolveException() {
    return new ResponseEntity(HttpStatusCode.valueOf(400));
  }


  @PostMapping("/api/gruppen")
  public ResponseEntity<Integer> createGruppe(
      @RequestBody WebGruppe webGruppe) {
    if (!webGruppe.isValid()) {
      return new ResponseEntity(HttpStatusCode.valueOf(400));
    }

    var g = gruppenService.addGruppe(WebGruppe.toGruppe(webGruppe));
    return new ResponseEntity(g.id(), HttpStatusCode.valueOf(201));
  }

  @GetMapping("/api/user/{login}/gruppen")
  public ResponseEntity<List<WebGruppe>> getGruppenForUser(
      @PathVariable("login") String githubLogin) {
    var a = gruppenService.getGruppenForPerson(new Person(githubLogin)).stream()
        .map(WebGruppe::fromGruppe).toList();
    return new ResponseEntity(a, HttpStatus.valueOf(200));
  }

  @GetMapping("/api/gruppen/{id}")
  public ResponseEntity<WebGruppe> getGruppenUebersicht(@PathVariable("id") String gruppenId) {
    int id;
    try {
      id = Integer.parseInt(gruppenId);
    } catch (NumberFormatException e) {
      return new ResponseEntity(HttpStatus.valueOf(404));
    }
    if (!gruppenService.getGruppe(id).isPresent()) {
      return new ResponseEntity(HttpStatus.valueOf(404));
    }
    return new ResponseEntity(WebGruppe.fromGruppe(gruppenService.getGruppe(id).get()),
        HttpStatus.valueOf(200));
  }

  @PostMapping("/api/gruppen/{id}/schliessen")
  public ResponseEntity gruppeSchliessen(@PathVariable("id") String gruppenId) {
    int id;
    try {
      id = Integer.parseInt(gruppenId);
    } catch (NumberFormatException e) {
      return new ResponseEntity(HttpStatus.valueOf(404));
    }
    if (gruppenService.getGruppe(id).isEmpty()) {
      return new ResponseEntity(HttpStatus.valueOf(404));
    }
    gruppenService.schliesseGruppe(id);
    return new ResponseEntity(HttpStatus.valueOf(200));
  }

  @PostMapping("/api/gruppen/{id}/auslagen")
  public ResponseEntity auslageEintragen(@PathVariable("id") String gruppenId,
      @RequestBody WebAuslage webAuslage) {
    int id;
    try {
      id = Integer.parseInt(gruppenId);
    } catch (NumberFormatException e) {
      return new ResponseEntity(HttpStatus.valueOf(404));
    }
    Optional<Gruppe> g = gruppenService.getGruppe(id);
    if (g.isEmpty()) {
      return new ResponseEntity(HttpStatus.valueOf(404));
    }
    Gruppe gruppe = g.get();
    if (!gruppe.isOffen()) {
      return new ResponseEntity(HttpStatus.valueOf(409));
    }
    if (!webAuslage.isValid()) {
      return new ResponseEntity(HttpStatus.valueOf(400));
    }

    gruppenService.addAusgabe(id, WebAuslage.toAusgabe(webAuslage));
    return new ResponseEntity(HttpStatus.valueOf(201));
  }

  @GetMapping("/api/gruppen/{id}/ausgleich")
  public ResponseEntity<List<WebSchuld>> ausgleichszahlungen(@PathVariable("id") String gruppenId) {
    int id;
    try {
      id = Integer.parseInt(gruppenId);
    } catch (NumberFormatException e) {
      return new ResponseEntity(HttpStatus.valueOf(404));
    }
    if (gruppenService.getGruppe(id).isEmpty()) {
      return new ResponseEntity(HttpStatus.valueOf(404));
    }

    var a = gruppenService.getSchuldenForGruppe(id).stream().map(WebSchuld::fromSchuld)
        .toList();
    return new ResponseEntity(a, HttpStatus.valueOf(200));
  }


}