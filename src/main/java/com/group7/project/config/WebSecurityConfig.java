package com.group7.project.config;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.group7.project.model.Konto;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private DataSource dataSource;

    @Bean
    public UserDetailsService uService() {
        return new AkteurDetailsService();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .dataSource(dataSource)
                .usersByUsernameQuery(
                        "select benutzername, passwort, erlaubt from personalabteilung.konten where benutzername=?")
                .authoritiesByUsernameQuery(
                        "select benutzername, rolle from personalabteilung.konten where benutzername=?");
    }

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        PortMapperImpl portMapper = new PortMapperImpl();
        portMapper.setPortMappings(Collections.singletonMap("8080", "8080"));
        PortResolverImpl portResolver = new PortResolverImpl();
        portResolver.setPortMapper(portMapper);
        LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint(
                "/login");
        entryPoint.setPortMapper(portMapper);
        entryPoint.setPortResolver(portResolver);

        http.exceptionHandling()
                .authenticationEntryPoint(entryPoint)
                .and()
                .requiresChannel(channel -> channel.anyRequest().requiresSecure())
                .authorizeRequests()
                .antMatchers("/css/**", "/images/**", "/").permitAll()
                .antMatchers("/dashboard_admin", "/diagramme")
                .hasAnyAuthority("Administrator")
                .antMatchers("/dashboard_verwaltungsleitung")
                .hasAnyAuthority("Verwaltungsleitung")
                .antMatchers("/dashboard_verwaltungsmitarbeiter")
                .hasAnyAuthority("Verwaltungsmitarbeiter")
                .antMatchers("/dashboard_nutzer")
                .hasAnyAuthority("Nutzer")
                .antMatchers("/VMantragbearbeiten")
                .hasAnyAuthority("Verwaltungsmitarbeiter")
                .antMatchers("/VLantragbearbeiten", "/kartenbearbeiten")
                .hasAnyAuthority("Verwaltungsleitung")
                .antMatchers("/archiv")
                .hasAnyAuthority("Admin", "Verwaltungsleitung")
                .antMatchers("/suchen")
                .hasAnyAuthority("Admin", "Verwaltungsleitung", "Verwaltungsmitarbeiter")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler)
                .permitAll()
                .and()
                .logout()
                .logoutSuccessHandler(new LogoutSuccessHandler() {

                    @Override
                    public void onLogoutSuccess(HttpServletRequest request,
                            HttpServletResponse response, Authentication authentication)
                            throws IOException, ServletException {
                        Konto userDetails = (Konto) authentication.getPrincipal();
                        String username = userDetails.getBenutzername();

                        System.out.println("The user " + username + " has logged out.");

                        response.sendRedirect(request.getContextPath());
                    }
                })
                .permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/error");
    }

}