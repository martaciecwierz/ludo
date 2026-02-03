package pl.marta.ludo.userservice.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.marta.ludo.userservice.auth.dto.RegisterDetails;
import pl.marta.ludo.userservice.auth.dto.UserCredentialsDetails;
import pl.marta.ludo.userservice.auth.dto.UserTokenDetails;
import pl.marta.ludo.userservice.domain.User;
import pl.marta.ludo.userservice.domain.UserRole;
import pl.marta.ludo.userservice.repository.UserRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public UserTokenDetails login(UserCredentialsDetails details) {
        User user = userRepository.findByUsername(details.username())
                .orElseThrow(() -> new RuntimeException("Bad credentials"));
        validatePassword(details, user);

        String accessToken = jwtProvider.generateToken(user);
        String refreshToken = jwtProvider.generateRefreshToken();
        RefreshToken entity = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expirationDate(getExpirationDate())
                .build();
        refreshTokenRepository.save(entity);

        return new UserTokenDetails(refreshToken, accessToken);
    }

    @Transactional
    public UserTokenDetails refreshToken(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        validateRefreshToken(tokenEntity);
        User user = tokenEntity.getUser();
        tokenEntity.setRevoked(true);
        String newRefreshToken = jwtProvider.generateRefreshToken();
        RefreshToken newTokenEntity = RefreshToken.builder()
                .token(newRefreshToken)
                .user(user)
                .expirationDate(getExpirationDate())
                .build();

        refreshTokenRepository.save(newTokenEntity);
        String accessToken = jwtProvider.generateToken(user);

        return new UserTokenDetails(newRefreshToken, accessToken);
    }

    @Transactional
    public UUID registerUser(RegisterDetails registerDetails) {
        validateRegisterDetails(registerDetails);

        String hashedPassword = passwordEncoder.encode(registerDetails.password());
        User user = User.builder()
                .username(registerDetails.username())
                .hashedPassword(hashedPassword)
                .role(UserRole.USER)
                .build();

        return userRepository.save(user).getId();
    }

    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        tokenEntity.setRevoked(true);
    }

    private void validatePassword(UserCredentialsDetails details, User user) {
        if (!passwordEncoder.matches(details.password(), user.getHashedPassword())) {
            throw new RuntimeException("Bad credentials");
        }
    }

    private void validateRegisterDetails(RegisterDetails registerDetails) {
        if(!registerDetails.password().equals(registerDetails.confirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        if(userRepository.existsByUsername(registerDetails.username())) {
            throw new RuntimeException("Username already exists");
        }
    }

    private void validateRefreshToken(RefreshToken refreshToken) {
        if(refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token is revoked");
        }
        if (refreshToken.isExpired()) {
            throw new RuntimeException("Refresh token expired");
        }
    }

    private Instant getExpirationDate() {
        return Instant.now().plusMillis(jwtProvider.getRefreshExpirationMs());
    }
}
