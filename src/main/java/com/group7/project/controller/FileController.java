package com.group7.project.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.group7.project.model.Antrag;
import com.group7.project.repository.AkteurRepository;
import com.group7.project.repository.AntraegeRepository;
import com.group7.project.repository.FacilityRepository;
import com.group7.project.repository.KeyCardRepository;
import com.group7.project.repository.LogsRepository;

@Controller
public class FileController {

    @Autowired
    AntraegeRepository antraegeRepository;

    @Autowired
    FacilityRepository facilityRepository;

    @Autowired
    AkteurRepository akteurRepository;

    @Autowired
    LogsRepository logsRepository;

    @Autowired
    KeyCardRepository keyCardRepository;

    @RequestMapping(path = "/testzentrum/{tür}/{keycard}")
    public String türlog(@PathVariable(name = "tür") String tür, @PathVariable(name = "keycard") String keycard)
            throws SQLException {

        try {
            Integer log_id = logsRepository.hoechsteTürID();
            if (log_id == null) {
                log_id = 1;
            } else {
                log_id = logsRepository.hoechsteTürID() + 1;
            }
            boolean zugang = false;
            boolean istImZeitlichenRahmen = false;
            String ergebnis = "";

            if (Integer.parseInt(keycard) != 0) {

                String status = keyCardRepository.findstatus(Integer.valueOf(keycard));

                List berechtigungen = keyCardRepository.berechtigungen(Integer.parseInt(keycard));

                String anfangstag = keyCardRepository.findanfangstag(Integer.parseInt(keycard));
                String endtag = keyCardRepository.findendtag(Integer.parseInt(keycard));
                Date startdate = java.sql.Date.valueOf(anfangstag);
                Date enddate = java.sql.Date.valueOf(endtag);
                Date newEnddate = java.sql.Date.valueOf((LocalDate.parse(endtag).plusDays(1)));
                Date today = new java.sql.Date(Calendar.getInstance().getTime().getTime());

                if (today.after(startdate) && newEnddate.after(today)) {
                    istImZeitlichenRahmen = true;
                    System.out.println("HURRA");
                }

                for (int i = 0; i < berechtigungen.size(); i++) {
                    if (((Number) berechtigungen.get(i)).intValue() == Integer.parseInt(tür)
                            && status.equals("Aktiviert") && istImZeitlichenRahmen) {
                        zugang = true;
                    }
                }

                if (zugang) {
                    ergebnis = "Akzeptiert";
                } else {
                    ergebnis = "Nicht_akzeptiert";
                }

                Date date = new Date(System.currentTimeMillis());
                Timestamp erstellungszeitpunkt = new Timestamp(date.getTime());

                logsRepository.tuerlogHinzufuegen(log_id, Integer.parseInt(keycard), Integer.parseInt(tür), ergebnis,
                        erstellungszeitpunkt);
            }

            return "redirect:/testzentrum";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @PostMapping("/antragfertig")
    public String saveAntrag(Model model,
            @RequestParam(value = "art", required = false) String art,
            @RequestParam(value = "berechtigung", required = false) MultipartFile berechtigung,
            @RequestParam(value = "anfangstag", required = false) String anfangstag,
            @RequestParam(value = "endtag", required = false) String endtag,
            @RequestParam(value = "kommentar", required = false) String kommentar,
            @RequestParam(value = "bereich", required = false) String bereiche,
            @RequestParam(value = "raum", required = false) String räume) {

        try {
            String benutzername = SecurityContextHolder.getContext().getAuthentication().getName();
            saveAntragToDatabase(benutzername, berechtigung, art, kommentar, bereiche, räume, anfangstag, endtag);

            return "redirect:/antrag_stellen";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    public void saveAntragToDatabase(String benutzername, MultipartFile berechtigung, String antragsart,
            String kommentar, String bereiche, String räume, String anfangstag, String endtag) {

        try {
            boolean ist_sensitiv = false;

            Antrag newAntrag = new Antrag();

            Integer antrag_id = antraegeRepository.hoechsteID();

            if (antrag_id == null) {
                antrag_id = 1;
            } else {
                antrag_id = antraegeRepository.hoechsteID() + 1;
            }
            newAntrag.setAntrag_id(antrag_id);
            newAntrag.setBenutzername(benutzername);
            Date date = new Date(System.currentTimeMillis());
            Timestamp erstellungszeitpunkt = new Timestamp(date.getTime());
            newAntrag.setErstellungszeitpunkt(erstellungszeitpunkt);
            String status = "Nicht_bearbeitet";
            newAntrag.setStatus(status);

            String extension = "";

            int b = berechtigung.getOriginalFilename().lastIndexOf('.');
            if (b >= 0) {
                extension = berechtigung.getOriginalFilename().substring(b + 1);
            }

            if (extension.equals("pdf")) {
                try {
                    newAntrag.setDatei(berechtigung.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                newAntrag.setDatei(null);
            }

            if (antragsart.equals("neue Karte")) {
                newAntrag.setAntragsart("NEU");
                newAntrag.setKeycard_id(null);
            } else {
                newAntrag.setAntragsart("BESTEHEND");
                newAntrag.setKeycard_id(Integer.parseInt(antragsart));
            }
            newAntrag.setKommentar(kommentar);
            newAntrag.setBearbeiter(null);

            List<String> bereichsliste = new ArrayList<String>(Arrays.asList(bereiche.split(",")));
            for (int i = 0; i < bereichsliste.size(); i++) {
                List bereichSensitiv = facilityRepository.bereichistSensitiv(bereichsliste.get(i));
                if (bereichSensitiv.contains("Ja")) {
                    ist_sensitiv = true;
                }
            }

            List<String> raumliste = new ArrayList<String>(Arrays.asList(räume.split(",")));
            for (int j = 0; j < raumliste.size(); j++) {
                List räumeSensitiv = facilityRepository.raumistSensitiv(raumliste.get(j));
                if (räumeSensitiv.contains("Ja")) {
                    ist_sensitiv = true;
                }
            }

            String rolle = akteurRepository.findRolle(benutzername);

            if (rolle.equals("Nutzer") && !ist_sensitiv) {
                newAntrag.setBearbeitungsstelle("Verwaltungsmitarbeiter");
            } else if (ist_sensitiv || rolle.equals("Verwaltungsmitarbeiter")) {
                newAntrag.setBearbeitungsstelle("Verwaltungsleitung");
            }

            if (ist_sensitiv) {
                newAntrag.setIst_sensitiv("Ja");
            } else {
                newAntrag.setIst_sensitiv("Nein");
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date adatum;
            java.util.Date edatum;
            try {
                if (!anfangstag.isEmpty() && !endtag.isEmpty()) {
                    adatum = format.parse(anfangstag);
                    edatum = format.parse(endtag);
                    java.sql.Date startdatum = new java.sql.Date(adatum.getTime());
                    java.sql.Date enddatum = new java.sql.Date(edatum.getTime());

                    newAntrag.setAnfangstag(startdatum);
                    newAntrag.setEndtag(enddatum);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ArrayList<Integer> bereicheFinal = new ArrayList<Integer>();
            ArrayList<Integer> raeumeFinal = new ArrayList<Integer>();

            boolean bereicheNotEmpy = false;
            boolean raeumeNotEmpy = false;

            if (bereiche != null && !bereiche.isEmpty()) {
                List<String> bereichListe = new ArrayList<String>(Arrays.asList(bereiche.split(",")));
                for (int x = 0; x < bereichListe.size(); x++) {

                    if (!bereicheFinal.contains(facilityRepository.idzuBereich(bereichListe.get(x)))
                            && facilityRepository.idzuBereich(bereichListe.get(x)) != null) {

                        bereicheNotEmpy = true;

                        bereicheFinal.add(facilityRepository.idzuBereich(bereichListe.get(x)));

                        Integer beantragt_id = antraegeRepository.gethighestbeantragtID();
                        if (beantragt_id == null) {
                            beantragt_id = 1;
                        } else {
                            beantragt_id = antraegeRepository.gethighestbeantragtID() + 1;
                        }
                        antraegeRepository.insertBeantragtFür(beantragt_id, antrag_id, null,
                                facilityRepository.idzuBereich(bereichListe.get(x)));
                    }

                }
            }

            if (räume != null && !räume.isEmpty()) {
                List<String> raumListe = new ArrayList<String>(Arrays.asList(räume.split(",")));
                for (int y = 0; y < raumListe.size(); y++) {
                    if (!raeumeFinal.contains(facilityRepository.idzuRaum(raumListe.get(y)))
                            && facilityRepository.idzuRaum(raumListe.get(y)) != null) {

                        raeumeNotEmpy = true;

                        raeumeFinal.add(facilityRepository.idzuRaum(raumListe.get(y)));

                        Integer beantragt_id = antraegeRepository.gethighestbeantragtID();
                        if (beantragt_id == null) {
                            beantragt_id = 1;
                        } else {
                            beantragt_id = antraegeRepository.gethighestbeantragtID() + 1;
                        }
                        antraegeRepository.insertBeantragtFür(beantragt_id, antrag_id,
                                facilityRepository.idzuRaum(raumListe.get(y)),
                                null);
                    }
                }
            }

            if (bereicheNotEmpy || raeumeNotEmpy) {
                antraegeRepository.save(newAntrag);
            }

        } catch (Exception error) {
            System.out.println(error.getMessage());
        }
    }
}
