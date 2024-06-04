package com.example.splitter.repositories;

import com.example.splitter.domain.model.Gruppe;
import com.example.splitter.domain.model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJdbcTest
@Sql("V1__init.sql")
public class GruppenRepositoryImplTest {

  @Autowired
  DBGruppenRepo repo;

  ArrayList<Gruppe> makeGruppen() {
    Gruppe gruppe1 = new Gruppe(1, "Mondgruppe", new Person("M"));
    Gruppe gruppe2 = new Gruppe(2, "Erdengruppe", new Person("M"));
    Gruppe gruppe3 = new Gruppe(3, "Merkurgruppe", new Person("M"));
    Gruppe gruppe4 = new Gruppe(4, "Venusgruppe", new Person("M"));
    Gruppe gruppe5 = new Gruppe(5, "Marsgruppe", new Person("M"));
    Gruppe gruppe6 = new Gruppe(6, "Jupitergruppe", new Person("M"));
    ArrayList<Gruppe> gruppen = new ArrayList<>(
        List.of(gruppe1, gruppe2, gruppe3, gruppe4, gruppe5, gruppe6));
    return gruppen;
  }

//  @AfterEach
//  @Test
//  @DisplayName("Löscht den Inhalt aller Tabellen vor jedem Test")
////  @Sql("clear_tables.sql")
////  @SqlGroup({@Sql("clear_tables.sql"), @Sql("V1__init.sql")})
////  @Disabled
//  void beforeEach() {
//
//  }


  @Test
  @DisplayName("Eine Gruppe wird gespeichert")
  void test_1() {
    GruppenRepositoryImpl impl = new GruppenRepositoryImpl(repo);
    Gruppe gruppe = new Gruppe(null, "Mondgruppe", new Person("M"));

    Gruppe result = impl.save(gruppe);

    assertThat(impl.findAll()).contains(result);
  }

  @Test
  @DisplayName("Eine NULL-Gruppe wird nicht gespeichert")
  void test_2() {
    GruppenRepositoryImpl impl = new GruppenRepositoryImpl(repo);
    Gruppe gruppe = new Gruppe(null, "Mondgruppe", new Person("M"));

    assertThatThrownBy(() -> impl.save(null)).hasMessage("Gruppe darf nicht null sein")
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Eine Gruppe kann gelöscht werden")
  void test_3() {
    GruppenRepositoryImpl impl = new GruppenRepositoryImpl(repo);
    Gruppe gruppe = new Gruppe(null, "Mondgruppe", new Person("M"));
    Gruppe result = impl.save(gruppe);

    impl.delete(result);

    assertThat(impl.findAll()).isEmpty();
  }

  @Test
  @DisplayName("Eine NULL-Gruppe wird nicht gelöscht")
  void test_4() {
    GruppenRepositoryImpl impl = new GruppenRepositoryImpl(repo);

    assertThatThrownBy(() -> impl.delete(null)).hasMessage("Gruppe darf nicht null sein")
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Mehrere Gruppen werden gespeichert mit saveAll()")
  void test_5() {
    GruppenRepositoryImpl impl = new GruppenRepositoryImpl(repo);
    Gruppe gruppe1 = new Gruppe(null, "Mondgruppe", new Person("M"));
    Gruppe gruppe2 = new Gruppe(null, "Sterngruppe", new Person("M"));
    Gruppe gruppe3 = new Gruppe(null, "Regenbogengruppe", new Person("M"));
    List<Gruppe> gruppen = List.of(gruppe1, gruppe2, gruppe3);

    impl.saveAll(gruppen);

    assertThat(impl.findAll()).hasSize(3);
  }

  @Test
  @DisplayName("NULL-Liste wirft Exception")
  void test_6() {
    GruppenRepositoryImpl impl = new GruppenRepositoryImpl(repo);

    assertThatThrownBy(() -> impl.saveAll(null)).hasMessage("Liste darf nicht null sein")
        .isInstanceOf(IllegalArgumentException.class);
  }


  @Test
  @DisplayName("Wenn Gruppe vorhanden ist, findet findById() die richtige Gruppe")
  @SqlGroup({@Sql("clear_tables.sql"), @Sql("V1__init.sql"), @Sql("test_context.sql")})
  void test_7() {
    GruppenRepositoryImpl impl = new GruppenRepositoryImpl(repo);

    Gruppe gruppe = new Gruppe(1, "Mondgruppe", new Person("M"));

    assertThat(impl.findById(1)).contains(gruppe);
  }


  @Test
  @DisplayName("findAll() gibt alle vorhandenen Gruppen korrekt zurück")
  @SqlGroup({@Sql("clear_tables.sql"), @Sql("V1__init.sql"), @Sql("test_context.sql")})
//  @SqlGroup({@Sql(scripts = "V1__init.sql"), @Sql("test_context.sql")})
  void test_8() {
    GruppenRepositoryImpl impl = new GruppenRepositoryImpl(repo);

//    Gruppe gruppe = new Gruppe(1, "Mondgruppe", new Person("M"));

    assertThat(impl.findAll()).containsAll(makeGruppen());
  }


  @Test
  @DisplayName("Nicht vorhandene ID gibt ein leeres Optional zurück")
  @SqlGroup({@Sql("clear_tables.sql"), @Sql(scripts = "V1__init.sql")})
  void test_9() {
    GruppenRepositoryImpl impl = new GruppenRepositoryImpl(repo);
    assertThat(impl.findById(-69)).isEmpty();
    assertThat(impl.findById(69)).isEmpty();
  }

  @Test
  @DisplayName("Null ID in findById() wirft Exception")
  @SqlGroup({@Sql("clear_tables.sql"), @Sql(scripts = "V1__init.sql")})
  void test_10() {
    GruppenRepositoryImpl impl = new GruppenRepositoryImpl(repo);
    assertThatThrownBy(() -> impl.findById(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Null ID in deleteById() wirft Exception")
  @SqlGroup({@Sql("clear_tables.sql"), @Sql(scripts = "V1__init.sql")})
  void test_11() {
    GruppenRepositoryImpl impl = new GruppenRepositoryImpl(repo);
    assertThatThrownBy(() -> impl.deleteById(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("deleteById() löscht Inhalt entsprechende Gruppe in db")
  @SqlGroup({@Sql("clear_tables.sql"), @Sql(scripts = "V1__init.sql"), @Sql("test_context.sql")})
  void test_12() {
    GruppenRepositoryImpl impl = new GruppenRepositoryImpl(repo);

    //act
    var gruppe = impl.findById(1);
    var x = impl.findAll();
    impl.deleteById(1);

    //Assert
    assertThat(impl.findAll().size()).isEqualTo(x.size() - 1);
    assertThat(impl.findById(1)).isEmpty();
  }

}
