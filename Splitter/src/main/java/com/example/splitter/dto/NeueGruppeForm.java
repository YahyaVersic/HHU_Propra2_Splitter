package com.example.splitter.dto;

import jakarta.validation.constraints.NotBlank;


public record NeueGruppeForm(
    @NotBlank(message = "Darf nicht leer sein") String name
) {

}
