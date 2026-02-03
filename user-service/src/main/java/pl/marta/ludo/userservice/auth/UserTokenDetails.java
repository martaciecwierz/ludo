package pl.marta.ludo.userservice.auth;

public record UserTokenDetails(String refreshToken, String accessToken) {
}
