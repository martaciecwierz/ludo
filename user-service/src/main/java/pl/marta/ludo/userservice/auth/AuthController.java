package pl.marta.ludo.userservice.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserCredentialsDetails request,
                                              HttpServletResponse response) {
        UserTokenDetails tokenDetails = userAuthService.login(
                new UserCredentialsDetails(request.username(), request.password())
        );

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenDetails.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(7)) // TODO from config
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new AuthResponse(tokenDetails.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue("refreshToken") String refreshToken) {
        return ResponseEntity.ok(new AuthResponse(userAuthService.refreshToken(refreshToken)));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterDetails request) {
        return ResponseEntity.ok(new RegisterResponse(userAuthService.registerUser(request)));
    }

    public record AuthResponse(String token) {
    }

    public record RegisterResponse(UUID userId) {
    }
}
