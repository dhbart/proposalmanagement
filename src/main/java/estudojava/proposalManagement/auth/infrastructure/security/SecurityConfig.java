package estudojava.proposalManagement.auth.infrastructure.security;

import estudojava.proposalManagement.auth.domain.UserRole;
import estudojava.proposalManagement.auth.infrastructure.persistency.entity.User;
import estudojava.proposalManagement.auth.infrastructure.persistency.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            RestUsernamePasswordAuthenticationFilter restUsernamePasswordAuthenticationFilter) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .securityContext(context -> context.requireExplicitSave(false))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .addFilterAt(restUsernamePasswordAuthenticationFilter,  UsernamePasswordAuthenticationFilter.class);

    //        .formLogin(Customizer.withDefaults());

        return http.build();
    }

    /* Esse Bean não é mais necessário após a implementação do JpaUserDetailsService
    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

        UserDetails influencer = User.withUsername("influencer")
                .password(passwordEncoder.encode("password"))
                .roles("INFLUENCER")
                .build();

        UserDetails brand = User.withUsername("brand")
                .password(passwordEncoder.encode("password"))
                .roles("BRAND")
                .build();

        return new InMemoryUserDetailsManager(influencer, brand);
    }
    */

    @Bean
    CommandLineRunner InitDatabase(UserRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.count() == 0) {
                User fitnessInfluencer = new User();
                fitnessInfluencer.setUsername("fitness_vibe");
                fitnessInfluencer.setPassword(passwordEncoder.encode("password"));
                fitnessInfluencer.setRole(UserRole.ROLE_INFLUENCER);

                User techInfluencer = new User();
                techInfluencer.setUsername("tech_guru");
                techInfluencer.setPassword(passwordEncoder.encode("password"));
                techInfluencer.setRole(UserRole.ROLE_INFLUENCER);

                User brand = new User();
                brand.setUsername("logistics");
                brand.setPassword(passwordEncoder.encode("password"));
                brand.setRole(UserRole.ROLE_BRAND);

                repository.saveAll(List.of(fitnessInfluencer, techInfluencer, brand));
            }
        };
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
