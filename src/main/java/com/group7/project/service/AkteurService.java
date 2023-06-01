package com.group7.project.service;

import com.group7.project.model.Konto;
import com.group7.project.repository.AkteurRepository;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AkteurService {

    @Autowired
    private final AkteurRepository akteurRepository;

    public AkteurService(AkteurRepository akteurRepository) {
        this.akteurRepository = akteurRepository;
    }

    public boolean alreadyExists(String benutzername) {
        Konto model = new Konto();
        model.setBenutzername(benutzername);

        if (akteurRepository.findByBenutzername(benutzername) == null) {
            return false;
        } else {
            return true;
        }
    }

    public Konto registerAkteur(int kontoid, String benutzername, String passwort, String rolle,
            Date erstellungszeitpunkt,
            String vorname, String nachname, Date geburtsdatum, String telefonnummer, String email,
            String adresse, Date arbeitsvertragsende, int erlaubt) {

        if (alreadyExists(benutzername)) {
            return null;
        } else {
            Konto akteurModel = new Konto();

            akteurModel.setId(kontoid);
            akteurModel.setBenutzername(benutzername);
            akteurModel.setPasswort(passwort);
            akteurModel.setRolle(rolle);
            akteurModel.setErstellungszeitpunkt(erstellungszeitpunkt);
            akteurModel.setVorname(vorname);
            akteurModel.setNachname(nachname);
            akteurModel.setGeburtsdatum(geburtsdatum);
            akteurModel.setTelefonnummer(telefonnummer);
            akteurModel.setEmail(email);
            akteurModel.setAdresse(adresse);
            akteurModel.setArbeitsvertragsende(arbeitsvertragsende);
            akteurModel.setArbeitsvertragsende(arbeitsvertragsende);

            return akteurRepository.save(akteurModel);
        }
    }

    public Konto authenticate(String benutzername, String passwort) {
        return akteurRepository.findByBenutzernameAndPasswort(benutzername, passwort).orElse(null);
    }

    public List<Konto> listAll(String keyword) {
        if (keyword != null) {
            return akteurRepository.search(keyword);
        }
        return akteurRepository.findAll();
    }

    public List<Konto> listAll() {
        return akteurRepository.findAll();
    }
}
