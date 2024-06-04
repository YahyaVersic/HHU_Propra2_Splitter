package com.example.splitter.repositories.dbdomain;

import org.springframework.data.relational.core.mapping.Table;

@Table("teilnehmer")
public record DBTeilnehmer(String name) {

}
