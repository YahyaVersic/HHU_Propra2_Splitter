package com.example.splitter.controller.webdomain;

import com.example.splitter.domain.model.Ausgabe;
import com.example.splitter.domain.model.Geld;
import com.example.splitter.domain.model.Person;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WebAuslage {

  String grund;
  String glaeubiger;
  int cent;
  String[] schuldner;


  public WebAuslage(String grund, String glaeubiger, int cent, String[] schuldner) {
    this.grund = grund;
    this.glaeubiger = glaeubiger;
    this.cent = cent;
    this.schuldner = schuldner;
  }

  public String getGrund() {
    return grund;
  }

  public String getGlaeubiger() {
    return glaeubiger;
  }

  public int getCent() {
    return cent;
  }

  public String[] getSchuldner() {
    return schuldner;
  }

  public static Ausgabe toAusgabe(WebAuslage webAuslage) {
    var a = Arrays.stream(webAuslage.schuldner).map(Person::new).collect(Collectors.toSet());
    return new Ausgabe(new Person(webAuslage.glaeubiger), webAuslage.grund, a,
        new Geld(webAuslage.cent));
  }

  public static WebAuslage fromAusgabe(Ausgabe ausgabe) {
    var a = ausgabe.profiteure().stream().map(Person::githubHandle).toArray(String[]::new);
    return new WebAuslage(ausgabe.beschreibung(), ausgabe.geldgeber().githubHandle(),
        ausgabe.geld().centbetrag(), a);
  }

  public boolean isValid() {
    return grund != null && glaeubiger != null && schuldner != null && schuldner.length != 0
        && cent != 0;
  }

  @Override
  public String toString() {
    return "WebAuslage[" +
        "grund=" + grund + ", " +
        "glaeubiger=" + glaeubiger + ", " +
        "cent=" + cent + ", " +
        "schuldner=" + Arrays.toString(schuldner) + ']';
  }
}
