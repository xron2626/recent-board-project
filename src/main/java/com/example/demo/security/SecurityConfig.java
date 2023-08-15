package com.example.demo.security;


import com.example.demo.cookie.CookieManager;
import com.example.demo.security.cors.CrossOriginConfig;
import com.example.demo.security.jwt.JwtManager;
import com.example.demo.security.service.CustomUserDetailsService;
import com.example.demo.security.service.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
@Slf4j
public class SecurityConfig {
    @Value("${front.domain.url}")
    private String frontDomainUrl;

    @Autowired
    ObjectPostProcessor objectPostProcessor;
    @Autowired
    PrincipalOauth2UserService principalOauth2UserService;
    @Autowired
    CookieManager cookieManager;
    @Autowired
    CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @Autowired
    JwtManager jwtManager;
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        };
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("http.sessionManagement() = " + http.sessionManagement());
        http
                .authorizeRequests()
                // .mvcMatchers("/user2").hasAnyRole("ROLE_USER")
                .mvcMatchers("/asdfasdf").hasRole("USER")
                // .mvcMatchers("/user2").hasRole("USER")
                .mvcMatchers("/*","/","/**","/first/oauth/join/**","/facebook/check/**", "/test/**","/add/session/*","/add/session/**","/login/**","/login_proc/**","/members/**", "/item/**",  "/users/**","/facebook/api/**","/**").permitAll()
                // /**하면 다 허용되니까 /*으로 뒤에 파라미터 들어가는 것만 허용함
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest()
                // .authenticated();
                .permitAll();
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //jwt사용을 위해 session해제
        http
                .oauth2Login()
                // .successHandler(new OauthSuccessHandler())
                .loginPage("/login")
                .userInfoEndpoint()
                .userService(principalOauth2UserService)
                .and()
                .successHandler(new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                Cookie accessTokenCookie = new Cookie("accessToken", jwtManager.getAccessToken(request));
                                Cookie refreshTokenCookie = new Cookie("refreshToken", jwtManager.getRefreshToken(request));
                                response.addCookie(accessTokenCookie);
                                response.addCookie(refreshTokenCookie);
                                response.sendRedirect(frontDomainUrl);
                            }
                        })
                .failureUrl("/first/oauth/join");;


        http
                .formLogin()
                .loginPage("/login")
                // .defaultSuccessUrl("/home")
                .loginProcessingUrl("/login_proc")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.sendRedirect("/principal");
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        System.out.println("exception = " + exception);
                        response.sendRedirect(frontDomainUrl+"/login");
                    }
                })
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/");
            http.cors();
            http.headers().addHeaderWriter((request, response) -> {
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Origin", frontDomainUrl);
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me, Origin,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
                response.setHeader("Access-Control-Allow-Credentials",  "true");
                response.setHeader("Set-Cookie", "SameSite=None; Secure"); // Adjust to your needs

            });

        return http.build();
    }

}
