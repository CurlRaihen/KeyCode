package com.group7.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.project.model.Konto;
import com.group7.project.repository.AkteurRepository;

@RestController
public class ProjectRestController {

    @Autowired
    AkteurRepository akteurRepository;

    @GetMapping("/admins")
    public List<Konto> listAdmins() {
        return akteurRepository.findByRole("Admin");
    }

    @GetMapping("/leiter")
    public List<Konto> listLeiter() {
        return akteurRepository.findByRole("Verwaltungsleitung");
    }

    @GetMapping("/mitarbeiter")
    public List<Konto> listMitarbeiter() {
        return akteurRepository.findByRole("Verwaltungsmitarbeiter");
    }

    @GetMapping("/nutzer")
    public List<Konto> listNutzer() {
        return akteurRepository.findByRole("Nutzer");
    }
}
