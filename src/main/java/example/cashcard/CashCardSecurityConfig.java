package example.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class CashCardSecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/cashcards/**")
                        .hasRole("CARD-OWNER"))
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .build();
    }

    @Bean
    UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        var userBuilder = User.builder();
        var sarah = userBuilder
                .username("sarah1")
                .password(passwordEncoder.encode("1sarah"))
                .roles("CARD-OWNER")
                .build();

        var hankWithNoCards = userBuilder
                .username("hank0")
                .password(passwordEncoder.encode("0hank"))
                .roles("NON-OWNER")
                .build();

        var kumar = userBuilder
                .username("kumar2")
                .password(passwordEncoder.encode("2kumar"))
                .roles("CARD-OWNER")
                .build();

        return new InMemoryUserDetailsManager(sarah, hankWithNoCards, kumar);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}