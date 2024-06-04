package com.example.splitter.domain.services;

import com.example.splitter.domain.model.Ausgabe;
import com.example.splitter.domain.model.Geld;
import com.example.splitter.domain.model.Gruppe;
import com.example.splitter.domain.model.berechnung.Konto;
import com.example.splitter.domain.model.Person;
import com.example.splitter.domain.model.berechnung.Schuld;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BerechnungsService {

  public static List<Schuld> berechneSchuldenEinerGruppe(Gruppe gruppe) {
    if (gruppe == null) {
      throw new IllegalArgumentException("Gruppe darf nicht null sein");
    }
    Map<Person, Geld> konten = berechneGesamtKontostaende(gruppe);

    //Filtere Konten in eine positive und eine negative Liste, damit man weiß wer Geld schuldet und wer Geld bekommt.
    List<Konto> positive = new ArrayList<>();
    List<Konto> negative = new ArrayList<>();
    konten.forEach((person, geld) -> {
      if (geld.centbetrag() > 0) {
        positive.add(new Konto(person, new Geld(0, geld.centbetrag())));
      } else if (geld.centbetrag() < 0) {
        negative.add(new Konto(person, new Geld(0, geld.centbetrag())));
      }
    });

    var positiveKonten = absteigendSortieren(positive);
    var negativeKonten = aufsteigendSortieren(negative); //Da negative Zahlen

    return berechneKonkreteUeberweisungenZwischenKonten(positiveKonten,
        negativeKonten, gruppe.name());
  }

  private static List<Schuld> berechneKonkreteUeberweisungenZwischenKonten(Konto[] positiveKonten,
      Konto[] negativeKonten, String gruppenname) {
    List<Schuld> schulds = new ArrayList<>();
    int i = 0; //Iteriert über negative Konten
    int j = 0; //Iteriert über positive Konten
    while (i < negativeKonten.length) {
      int sum =
          positiveKonten[j].kontostand().centbetrag() + negativeKonten[i].kontostand().centbetrag();
      if (sum > 0) {
        schulds.add(new Schuld(
            negativeKonten[i].inhaber(), positiveKonten[j].inhaber(),
            new Geld(-negativeKonten[i].kontostand().centbetrag()),
            gruppenname));
        negativeKonten[i].setKontostand(new Geld(0));
        positiveKonten[j].setKontostand(new Geld(sum));
        ++i;
      } else if (sum == 0) {
        schulds.add(new Schuld(negativeKonten[i].inhaber(), positiveKonten[j].inhaber(), new Geld(
            positiveKonten[j].kontostand().centbetrag()), gruppenname));
        positiveKonten[j].setKontostand(new Geld(sum));
        negativeKonten[i].setKontostand(new Geld(sum));
        i++;
        ++j;
      } else if (sum < 0) {
        schulds.add(new Schuld(negativeKonten[i].inhaber(), positiveKonten[j].inhaber(), new Geld(
            positiveKonten[j].kontostand().centbetrag()), gruppenname));
        positiveKonten[j].setKontostand(new Geld(0));
        negativeKonten[i].setKontostand(new Geld(sum));
        j++;
      }
    }
    return schulds;
  }

  private static Konto[] aufsteigendSortieren(List<Konto> negative) {
    return negative.stream()
        .sorted(Comparator.comparingInt(o -> o.kontostand().centbetrag()))
        .toArray(Konto[]::new);
  }

  private static Konto[] absteigendSortieren(List<Konto> positive) {
    return positive.stream()
        .sorted((o1, o2) -> o2.kontostand().centbetrag() - o1.kontostand().centbetrag())
        .toArray(Konto[]::new);
  }

  static Map<Person, Geld> berechneGesamtKontostaende(Gruppe gruppe) {
    List<Ausgabe> ausgabeList = gruppe.ausgabenliste();

    Map<Person, Geld> konten = new HashMap<>();
    for (Person teilnehmer : gruppe.teilnehmer()) {
      konten.put(teilnehmer, new Geld(0, 0));
    }

    for (Ausgabe ausgabe : ausgabeList) {
      Geld teilGeld = ausgabe.geld().split(ausgabe.profiteure().size());
      for (Person profiteur : ausgabe.profiteure()) {
        konten.put(profiteur,
            new Geld(0, konten.get(profiteur).centbetrag() - teilGeld.centbetrag()));
        konten.put(ausgabe.geldgeber(),
            new Geld(0, konten.get(ausgabe.geldgeber()).centbetrag() + teilGeld.centbetrag()));
      }
    }
    return konten;
  }

  static Map<Person, Geld> berechneKontostandEinerPerson(Gruppe gruppe, Person person) {
    Map<Person, Geld> konten = berechneGesamtKontostaende(gruppe);
    Map<Person, Geld> konto = new HashMap<>();
    konto.put(person, konten.get(person));
    return konto;
  }

}
