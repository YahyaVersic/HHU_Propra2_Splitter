package com.example.splitter.domain.services;

import com.example.splitter.domain.model.Gruppe;
import java.util.List;
import java.util.Optional;

public interface GruppenRepository {

  List<Gruppe> findAll();

  Gruppe save(Gruppe gruppe);

  void saveAll(List<Gruppe> gruppen);

  Optional<Gruppe> findById(Integer id);

  void delete(Gruppe gruppe);

  void deleteById(Integer id);

}
