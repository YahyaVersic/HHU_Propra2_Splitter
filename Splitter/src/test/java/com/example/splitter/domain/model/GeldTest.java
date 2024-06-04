package com.example.splitter.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GeldTest {

  @Test
  @DisplayName("ToString methode gibt im Format euro,cent zurück")
  void test1() {
    //AAA
    //Arrange
    Geld geld = new Geld(100, 90);
    //Act
    String s = geld.toString();
    //Assert
    assertThat(s).isEqualTo("100,90");

  }

  @Test
  @DisplayName("Geld wird richtig addiert")
  void test2() {
    //AAA
    //Arrange
    Geld geld = new Geld(111, 99);
    Geld geld2 = new Geld(222, 1);

    //Act
    Geld sum = geld.add(geld2);

    //Assert
    assertThat(sum.toString()).isEqualTo("334,00");
  }

  @Test
  @DisplayName("Geld darf nur negative oder nur positive Werte im Konstruktor haben")
  void test3() {
    //AAA
    //Arrange
    var exception = assertThrows(IllegalArgumentException.class, () -> new Geld(111, -99));
    var exception2 = assertThrows(IllegalArgumentException.class, () -> new Geld(-111, 99));
    //Act

    //Assert
    assertThat(exception.getMessage()).contains("nur negativ");
    assertThat(exception2.getMessage()).contains("nur negativ");
  }

  @Test
  @DisplayName("15€ durch drei Personen teilen gibt 5€ (pro Person) zurück")
  void test4() {
    //AAA
    //Arrange
    Geld g = new Geld(15, 0);
    //Act
    //Assert
    assertThat(g.split(3)).isEqualTo(new Geld(5, 0));

  }

  @Test
  @DisplayName("Equals gibt für zwei Wertgleiche Geldobjekte true zurück")
  void test5() {
    //AAA
    //Arrange
    Geld g1 = new Geld(15, 0);
    Geld g2 = new Geld(15, 0);
    //Act
    //Assert
    assertThat(g1.equals(g2)).isTrue();

  }

  @Test
  @DisplayName("Equals gibt für zwei wertungleiche Geldobjekte false zurück")
  void test6() {
    //AAA
    //Arrange
    Geld g1 = new Geld(15, 0);
    Geld g2 = new Geld(16, 0);
    //Act
    //Assert
    assertThat(g1.equals(g2)).isFalse();

  }

  @Test
  @DisplayName("Geld darf nicht auf eine negative Anzahl Personen verteilt werden")
  void test7() {
    //AAA
    //Arrange
    Geld g = new Geld(15, 0);
    //Act
    var exception = assertThrows(IllegalArgumentException.class, () -> g.split(-3));
    //Assert
    assertThat(exception.getMessage()).contains("muss positiv sein");

  }

  @Test
  @DisplayName("Geld kann nicht auf 0 Personen verteilt werden")
  void test8() {
    //AAA
    //Arrange
    Geld g = new Geld(15, 0);
    //Act
    var exception = assertThrows(IllegalArgumentException.class, () -> g.split(0));
    //Assert
    assertThat(exception.getMessage()).contains("muss positiv sein");
  }


}