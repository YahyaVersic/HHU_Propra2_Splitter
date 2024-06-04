package com.example.splitter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NeuesMitgliedForm(
    @NotBlank(message = "Darf nicht leer sein")
    @Pattern(regexp = "(?:[a-zA-Z\\d]|(?:[a-zA-Z\\d]|-(?=[a-zA-Z\\d])){0,38}[a-zA-Z\\d])$") String name
) {

}
