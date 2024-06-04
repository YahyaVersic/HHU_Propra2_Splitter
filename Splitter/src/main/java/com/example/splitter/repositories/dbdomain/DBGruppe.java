package com.example.splitter.repositories.dbdomain;

import java.util.List;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.w3c.dom.stylesheets.LinkStyle;

@Table("gruppe")
public record DBGruppe(
    @Id Integer id,
    String name,
    boolean isOpen,
    Set<DBTeilnehmer> teilnehmer,
    List<DBAusgabe> ausgaben
) {

}
