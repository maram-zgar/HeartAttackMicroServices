package dev.maram.gateway;

import dev.maram.gateway.user.Role;
import dev.maram.gateway.user.User;
import dev.maram.gateway.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedAdmin(UserRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.findByEmail("admin@admin.com").isEmpty()) {
                repository.save(User.builder()
                        .firstName("Admin")
                        .lastName("Admin")
                        .email("admin@admin.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build());
            }
        };
    }

}
