package pl.marta.ludo.userservice.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.marta.ludo.userservice.domain.User;
import pl.marta.ludo.userservice.domain.UserRole;
import pl.marta.ludo.userservice.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public UserTokenDetails login(UserCredentialsDetails details) {
        User user = userRepository.findByUsername(details.username())
                .orElseThrow(() -> new RuntimeException("Bad credentials"));

        if (!passwordEncoder.matches(details.password(), user.getHashedPassword())) {
            throw new RuntimeException("Bad credentials");
        }

        String accessToken = jwtProvider.generateToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        return new UserTokenDetails(refreshToken, accessToken);
    }

    public String refreshToken(String refreshToken) {
        if (!jwtProvider.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String username = jwtProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username not found"));
        return jwtProvider.generateToken(user);
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

    private void validateRegisterDetails(RegisterDetails registerDetails) {
        if(!registerDetails.password().equals(registerDetails.confirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        if(userRepository.existsByUsername(registerDetails.username())) {
            throw new RuntimeException("Username already exists");
        }
    }
}
