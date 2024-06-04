package com.example.splitter.dto;

import com.example.splitter.domain.model.Ausgabe;
import com.example.splitter.domain.model.Geld;
import com.example.splitter.domain.model.Person;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record AusgabenForm(
    @NotBlank(message = "Darf nicht leer sein") String name,
    @Positive(message = "Der Betrag muss größer als 0 sein") double preis,
    @NotBlank(message = "Darf nicht leer sein") String bezahler,
    @NotEmpty String[] profiteure
)
{

}
