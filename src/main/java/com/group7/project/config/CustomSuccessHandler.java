package com.group7.project.config;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Authentication authentication) throws IOException, ServletException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        httpServletResponse.sendRedirect("/home");
        /* 
        if (roles.contains("Nutzer")) {
            httpServletResponse.sendRedirect("/dashboard_nutzer");
        } else if (roles.contains("Verwaltungsmitarbeiter")) {
            httpServletResponse.sendRedirect("/dashboard_verwaltungsmitarbeiter");
        } else if (roles.contains("Verwaltungsleitung")) {
            httpServletResponse.sendRedirect("/dashboard_verwaltungsleitung");
        } else if (roles.contains("Admin")) {
            httpServletResponse.sendRedirect("/dashboard_admin");
        } else {
            httpServletResponse.sendRedirect("/error");
        }
        */
    }
}