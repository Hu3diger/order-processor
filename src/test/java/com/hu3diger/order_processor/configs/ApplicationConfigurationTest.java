package com.hu3diger.order_processor.configs;

import com.hu3diger.order_processor.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ApplicationConfigurationTest {

    @Autowired
    private ApplicationContext context;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUserDetailsServiceBean() {
        UserDetailsService userDetailsService = context.getBean(UserDetailsService.class);
        assertThat(userDetailsService).isNotNull();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("user@example.com"));
    }

    @Test
    void testPasswordEncoderBean() {
        BCryptPasswordEncoder passwordEncoder = context.getBean(BCryptPasswordEncoder.class);
        assertThat(passwordEncoder).isNotNull();

        String rawPassword = "password";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    void testAuthenticationManagerBean() {
        AuthenticationManager authManager = context.getBean(AuthenticationManager.class);
        assertThat(authManager).isNotNull();
    }

    @Test
    void testAuthenticationProviderBean() {
        AuthenticationProvider authProvider = context.getBean(AuthenticationProvider.class);
        assertThat(authProvider).isNotNull();
    }
}
