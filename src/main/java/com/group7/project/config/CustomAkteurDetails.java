package com.group7.project.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.group7.project.model.Konto;

public class CustomAkteurDetails implements UserDetails {

    private Konto user;

    public CustomAkteurDetails(Konto user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(user.getRolle().toString());
    }

    @Override
    public String getPassword() {
        return user.getPasswort();
    }

    @Override
    public String getUsername() {
        return user.getBenutzername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setVorname(String vorname) {
        this.user.setVorname(vorname);
    }

    public void setNachanme(String nachanme) {
        this.user.setNachname(nachanme);
    }

    public String getAdresse() {
        return this.user.getAdresse();
    }

}
