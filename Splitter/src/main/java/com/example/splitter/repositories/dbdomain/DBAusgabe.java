package com.example.splitter.repositories.dbdomain;

import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("ausgabe")
public record DBAusgabe(@Id Integer id,
                        String beschreibung,
                        String geldgeber,
                        Integer geld,
                        Set<DBProfiteur> profiteure
) {

}
