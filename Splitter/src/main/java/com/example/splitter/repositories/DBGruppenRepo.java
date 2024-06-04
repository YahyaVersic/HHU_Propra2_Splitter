package com.example.splitter.repositories;

import com.example.splitter.repositories.dbdomain.DBGruppe;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;


public interface DBGruppenRepo extends CrudRepository<DBGruppe, Integer> {

  @Override
  Optional<DBGruppe> findById(Integer integer);

  @Override
  List<DBGruppe> findAll();


}
