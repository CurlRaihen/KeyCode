package com.group7.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.group7.project.model.Konto;
import com.group7.project.repository.AkteurRepository;

@Service
public class AkteurDetailsService implements UserDetailsService {

    @Autowired
    private AkteurRepository akteurRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Konto user = akteurRepository.findByBenutzername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomAkteurDetails(user);
    }
}