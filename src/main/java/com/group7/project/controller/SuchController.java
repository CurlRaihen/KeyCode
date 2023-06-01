package com.group7.project.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.group7.project.model.Antrag;
import com.group7.project.model.Keycard;
import com.group7.project.model.Konto;
import com.group7.project.repository.AkteurRepository;
import com.group7.project.repository.AntraegeRepository;
import com.group7.project.repository.FacilityRepository;
import com.group7.project.repository.KeyCardRepository;
import com.group7.project.repository.LogsRepository;

@Controller
public class SuchController {

    @Autowired
    private AkteurRepository akteurRepository;

    @Autowired
    private AntraegeRepository antraegeRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private KeyCardRepository keyCardRepository;

    @Autowired
    private LogsRepository logsRepository;

    @GetMapping("/suchen")
    public String getProbePage(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);

            return "suchpage";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @PostMapping("/nutzer_verwalten")
    public String getAkteurFinderResult(Model model, @RequestParam("wert") String wert) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String rolle = akteurRepository.findRolle(username);

        if (rolle.equals("Verwaltungsmitarbeiter")) {
            List<Konto> akteursuchergebnis = akteurRepository.searchNutzer(wert);
            model.addAttribute("akteursuchergebnis", akteursuchergebnis);
            int ergebnisSize = akteursuchergebnis.size();
            model.addAttribute("rolle", rolle);
            model.addAttribute("ergebnisSize", ergebnisSize);
        } else {
            List<Konto> akteursuchergebnis = akteurRepository.search(wert);
            model.addAttribute("akteursuchergebnis", akteursuchergebnis);
            int ergebnisSize = akteursuchergebnis.size();
            model.addAttribute("rolle", rolle);
            model.addAttribute("ergebnisSize", ergebnisSize);
        }

        return "nutzerverwaltung";
    }

    @PostMapping("/antragfinder")
    public String getAntragFinderResult(Model model, @RequestParam("wert") String wert) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);

            List<Antrag> antraegesuchergebnis = antraegeRepository.search(wert);
            model.addAttribute("antraegesuchergebnis", antraegesuchergebnis);

            return "suchpage";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @PostMapping("/keycardfinder")
    public String getKeycardFinderResult(Model model, @RequestParam("wert") String wert) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);

            List<Keycard> keycardsuchergebnis = keyCardRepository.search(wert);
            model.addAttribute("keycardsuchergebnis", keycardsuchergebnis);

            return "suchpage";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @PostMapping("/tuerlogfinder")
    public String getTuerlogFinderResult(Model model, @RequestParam("startdatum") String startdatum,
            @RequestParam("enddatum") String enddatum) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);

            startdatum += " 00:00:00";
            enddatum += " 23:59:59";
            model.addAttribute("tstartdatum", startdatum);
            model.addAttribute("tenddatum", enddatum);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            Date parsedStartdatum = dateFormat.parse(startdatum);
            Timestamp StartdatumTimestamp = new java.sql.Timestamp(parsedStartdatum.getTime());

            Date parsedEnddatum = dateFormat.parse(enddatum);
            Timestamp EnddatumTimestamp = new java.sql.Timestamp(parsedEnddatum.getTime());

            List tuerlogsuchergebnis = logsRepository.tuerlogsSearch(StartdatumTimestamp, EnddatumTimestamp);
            model.addAttribute("tuerlogsuchergebnis", tuerlogsuchergebnis);

        } catch (Exception error) {
            System.out.println(error.getMessage());
        }

        return "suchpage";
    }

    @PostMapping("/keycardlogfinder")
    public String getKeycardlogFinderResult(Model model, @RequestParam("startdatum") String startdatum,
            @RequestParam("enddatum") String enddatum) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);

            startdatum += " 00:00:00";
            enddatum += " 23:59:59";
            model.addAttribute("kstartdatum", startdatum);
            model.addAttribute("kenddatum", enddatum);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            Date parsedStartdatum = dateFormat.parse(startdatum);
            Timestamp StartdatumTimestamp = new java.sql.Timestamp(parsedStartdatum.getTime());

            Date parsedEnddatum = dateFormat.parse(enddatum);
            Timestamp EnddatumTimestamp = new java.sql.Timestamp(parsedEnddatum.getTime());

            List keycardlogsuchergebnis = logsRepository.keycardlogsSearch(StartdatumTimestamp, EnddatumTimestamp);
            model.addAttribute("keycardlogsuchergebnis", keycardlogsuchergebnis);

        } catch (Exception error) {
            System.out.println(error.getMessage());
        }

        return "suchpage";
    }

    //

    @RequestMapping("/tuerlogSuche")
    public RedirectView gettuerlogSuche(Model model, HttpServletRequest request,
            @RequestParam("facility") String facility,
            @RequestParam("raum") String raum,
            @RequestParam("startdatum") String startdatum,
            @RequestParam("enddatum") String enddatum, RedirectAttributes redir) {
        try {

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);

            startdatum += " 00:00:00";
            enddatum += " 23:59:59";
            model.addAttribute("tstartdatum", startdatum);
            model.addAttribute("tenddatum", enddatum);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            Date parsedStartdatum = dateFormat.parse(startdatum);
            Timestamp StartdatumTimestamp = new java.sql.Timestamp(parsedStartdatum.getTime());

            Date parsedEnddatum = dateFormat.parse(enddatum);
            Timestamp EnddatumTimestamp = new java.sql.Timestamp(parsedEnddatum.getTime());

            List zutrittsprotokoll = new ArrayList<>();
            if (raum.equalsIgnoreCase("Gesamt")) {
                zutrittsprotokoll = logsRepository.facilitySuche(facility,
                        StartdatumTimestamp, EnddatumTimestamp);
                model.addAttribute("zutrittsprotokoll", zutrittsprotokoll);
            } else {
                zutrittsprotokoll = logsRepository.tuerlogSuche(raum,
                        StartdatumTimestamp, EnddatumTimestamp);
                model.addAttribute("zutrittsprotokoll", zutrittsprotokoll);
            }

            RedirectView redirectView = new RedirectView("/gebaeude?gebaeudeName=" + facility, true);
            redir.addFlashAttribute("zutrittsprotokoll", zutrittsprotokoll);
            redir.addFlashAttribute("raum", raum);
            return redirectView;

        } catch (Exception error) {
            System.out.println(error.getMessage());
        }

        RedirectView redirectView = new RedirectView("/exception", true);
        return redirectView;
    }

    @RequestMapping("/doorlogSuche")
    public RedirectView getAllTuerLogs(Model model, HttpServletRequest request,
            @RequestParam("startdatum") String startdatum,
            @RequestParam("enddatum") String enddatum, RedirectAttributes redir) {
        try {

            startdatum += " 00:00:00";
            enddatum += " 23:59:59";

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            Date parsedStartdatum = dateFormat.parse(startdatum);
            Timestamp StartdatumTimestamp = new java.sql.Timestamp(parsedStartdatum.getTime());

            Date parsedEnddatum = dateFormat.parse(enddatum);
            Timestamp EnddatumTimestamp = new java.sql.Timestamp(parsedEnddatum.getTime());

            List zutrittsprotokoll = logsRepository.tuerlogsSearch(
                    StartdatumTimestamp, EnddatumTimestamp);

            RedirectView redirectView = new RedirectView("/home", true);
            redir.addFlashAttribute("zutrittsprotokoll", zutrittsprotokoll);
            return redirectView;

        } catch (Exception error) {
            System.out.println(error.getMessage());
            RedirectView redirectView = new RedirectView("/exception", true);
            return redirectView;
        }
    }

    @RequestMapping("/keycardlogSuche")
    public RedirectView getAllKeycardLogs(Model model, HttpServletRequest request,
            @RequestParam("startdatum") String startdatum,
            @RequestParam("enddatum") String enddatum, RedirectAttributes redir) {
        try {

            startdatum += " 00:00:00";
            enddatum += " 23:59:59";

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            Date parsedStartdatum = dateFormat.parse(startdatum);
            Timestamp StartdatumTimestamp = new java.sql.Timestamp(parsedStartdatum.getTime());

            Date parsedEnddatum = dateFormat.parse(enddatum);
            Timestamp EnddatumTimestamp = new java.sql.Timestamp(parsedEnddatum.getTime());

            List keycardprotokoll = logsRepository.keycardlogsSearch(
                    StartdatumTimestamp, EnddatumTimestamp);

            System.out.println(keycardprotokoll.size());

            RedirectView redirectView = new RedirectView("/home", true);
            redir.addFlashAttribute("keycardprotokoll", keycardprotokoll);
            return redirectView;

        } catch (Exception error) {
            System.out.println(error.getMessage());
            RedirectView redirectView = new RedirectView("/exception", true);
            return redirectView;
        }
    }
}
