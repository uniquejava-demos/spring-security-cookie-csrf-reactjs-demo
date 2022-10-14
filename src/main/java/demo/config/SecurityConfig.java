package demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager users() {
        return new InMemoryUserDetailsManager(
                User.withUsername("cyper")
                        .password("{noop}password")
                        .roles("admin")
                        .build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(auth -> auth
                .anyRequest().authenticated()
        );

        http.exceptionHandling((ex) -> ex
                .authenticationEntryPoint(new MyAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler())
        );

        http.formLogin().permitAll();

        return http.build();
    }

    record R(String code, String message) {
    }

    class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String message = NestedExceptionUtils.getMostSpecificCause(authException).getMessage();
            R r = new R("unauthorized", message);
            response.getWriter().println(new ObjectMapper().writeValueAsString(r));
        }
    }

    class MyAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            String message = NestedExceptionUtils.getMostSpecificCause(accessDeniedException).getMessage();
            R r = new R("forbidden", message);
            response.getWriter().println(new ObjectMapper().writeValueAsString(r));
        }
    }
}
