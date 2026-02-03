package pl.marta.ludo.userservice.auth.dto;

public record UserTokenDetails(String refreshToken, String accessToken) {
}
