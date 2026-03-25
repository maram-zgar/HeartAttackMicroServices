package dev.maram.gateway.config;


import dev.maram.gateway.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;


// Whole flow in simple words :

// When a user tries to log in :
// 1. the app receives email + password
// 2. AuthenticationManager starts authentication
// 3. AuthenticationProvider handles it
// 4. UserDetailsService loads the user from DB
// 5. PasswordEncoder checks the password
// if correct, the user is authenticated


@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository; // To fetch the user from the db. So when someone tries to log in with an email, the app can search for that user.

    // This bean is the bridge between Spring Security and the database.
    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> Mono.justOrEmpty(userRepository.findByEmail(username))
                .cast(UserDetails.class);
    }

    // Is the Data Access Object (DAO) responsible for fetching user details and encode passwords ..
//    So when a user logs in, this provider is basically doing:
//    1. load the user by email
//    2. get the stored hashed password
//    3. compare it with the password the user typed
//    4. authenticate if they match


//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }

    // 'BCryptPasswordEncoder' is used to hash passwords when saving them and verify raw passwords during login.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Auth manager is the higher-level object that starts authentication.
    // Has many methods one of which is authenticating a user based on username and password.
    // So this bean is like the main entry point for authentication requests.
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
        manager.setPasswordEncoder(passwordEncoder());
        return manager;
    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }

}


//@Configuration
//@RequiredArgsConstructor
//public class ApplicationConfig {
//
//    private final UserRepository userRepository; // Ensure this is a ReactiveRepository
//
//    @Bean
//    public ReactiveUserDetailsService userDetailsService() {
//        return username -> userRepository.findByEmail(username)
//                .cast(UserDetails.class) // This tells Reactor to treat the User as UserDetails
//                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
//    }
//
//    @Bean
//    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService,
//                                                               PasswordEncoder passwordEncoder) {
//        UserDetailsRepositoryReactiveAuthenticationManager authManager =
//                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
//        authManager.setPasswordEncoder(passwordEncoder);
//        return authManager;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}