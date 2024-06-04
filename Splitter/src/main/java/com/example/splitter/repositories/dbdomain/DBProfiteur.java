package com.example.splitter.repositories.dbdomain;

import org.springframework.data.relational.core.mapping.Table;

@Table("profiteure")
public record DBProfiteur(String name) {

}
