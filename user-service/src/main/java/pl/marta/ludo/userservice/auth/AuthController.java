package pl.marta.ludo.userservice.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.marta.ludo.userservice.auth.dto.RegisterDetails;
import pl.marta.ludo.userservice.auth.dto.UserCredentialsDetails;
import pl.marta.ludo.userservice.auth.dto.UserTokenDetails;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserCredentialsDetails request) {
        UserTokenDetails tokenDetails = userAuthService.login(
                new UserCredentialsDetails(request.username(), request.password())
        );

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenDetails.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(7)) // TODO from config
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(tokenDetails.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue("refreshToken") String refreshToken) {
        UserTokenDetails tokenDetails = userAuthService.refreshToken(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenDetails.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(7)) // TODO from config
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(tokenDetails.accessToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterDetails request) {
        return ResponseEntity.ok(new RegisterResponse(userAuthService.registerUser(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue("refreshToken") String refreshToken) {
        userAuthService.revokeRefreshToken(refreshToken);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    public record AuthResponse(String token) {
    }

    public record RegisterResponse(UUID userId) {
    }
}
