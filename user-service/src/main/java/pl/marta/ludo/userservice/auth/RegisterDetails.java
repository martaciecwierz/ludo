package pl.marta.ludo.userservice.auth;

import jakarta.validation.constraints.NotBlank;

public record RegisterDetails (
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String confirmPassword) {
}
