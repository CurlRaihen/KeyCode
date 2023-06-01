package com.group7.project.controller;

import com.group7.project.mailserver.Email;
import com.group7.project.mailserver.EmailSenderService;
import com.group7.project.model.Antrag;
import com.group7.project.model.Keycard;
import com.group7.project.model.Konto;
import com.group7.project.repository.*;
import com.group7.project.service.AkteurService;
import com.group7.project.service.AntraegeService;
import com.group7.project.service.KeyCardService;

import java.sql.Date;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class AkteurController {

    @Autowired
    private AkteurRepository akteurRepository;

    @Autowired
    private AntraegeRepository antraegeRepository;

    @Autowired
    private KeyCardRepository keyCardRepository;

    @Autowired
    private LogsRepository logsRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private AkteurService akteurService;

    @Autowired
    private AntraegeService antraegeService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private KeyCardService keyCardService;
    private final TuerRepository tuerRepository;

    public AkteurController(AkteurRepository akteurRepository, AntraegeRepository antraegeRepository,
            KeyCardRepository keyCardRepository, LogsRepository logsRepository, FacilityRepository faccilityRepository,
            AkteurService akteurService,
            AntraegeService antraegeService,
            EmailSenderService emailSenderService, KeyCardService keyCardService,
            TuerRepository tuerRepository) {
        this.akteurRepository = akteurRepository;
        this.antraegeRepository = antraegeRepository;
        this.keyCardRepository = keyCardRepository;
        this.logsRepository = logsRepository;
        this.facilityRepository = faccilityRepository;

        this.akteurService = akteurService;
        this.antraegeService = antraegeService;
        this.emailSenderService = emailSenderService;
        this.keyCardService = keyCardService;
        this.tuerRepository = tuerRepository;
    }
    // FINAL START

    @RequestMapping("/")
    public String startingPage() {

        return "redirect:/home";
    }

    @RequestMapping("/about")
    public String getAboutPage(Model model) {
        return "about";
    }

    @RequestMapping("/contact")
    public String getContactPage(Model model) {
        return "contact";
    }

    @PostMapping("/kontaktformular")
    public String sendContactForm(Model model) {

        return "contact";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        try {
            model.addAttribute("loginRequest", new Konto());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
                return "login";
            }

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            String dashboardlink = "/dashboard_" + rolle.toLowerCase();

            return "redirect:/home";
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }
    }

    @PostMapping("/login")
    public String login(@ModelAttribute Konto usersModel, Model model) {
        try {
            System.out.println("login request: " + usersModel);
            Konto authenticated = akteurService.authenticate(usersModel.getBenutzername(), usersModel.getPasswort());
            if (authenticated != null) {
                model.addAttribute("userBenutzername", authenticated.getBenutzername());
                model.addAttribute("userVorname", authenticated.getVorname());
                model.addAttribute("userNachname", authenticated.getNachname());
                model.addAttribute("userEmail", authenticated.getEmail());
                model.addAttribute("userRole", authenticated.getRolle());

                return "dashboard";
            } else {

                return "error_page";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }

            return "redirect:/login";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }
    }

    @GetMapping("/home")
    public String get_home_Page(Model model) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Nutzer")) {
                return "dash_nutzer";
            } else if (rolle.equals("Verwaltungsmitarbeiter") || rolle.equals("Verwaltungsleitung")) {
                model.addAttribute("rolle", rolle);
                return "dash_verwaltung";
            } else if (rolle.equals("Administrator")) {
                List keycardlogs = logsRepository.keycardlogs();
                model.addAttribute("keycardlogs", keycardlogs);

                int nutzerAnzahl = akteurRepository.anzahlAkteure("Nutzer");
                model.addAttribute("nutzerAnzahl", nutzerAnzahl);
                int mitarbeiterAnzahl = akteurRepository.anzahlAkteure("Verwaltungsmitarbeiter");
                model.addAttribute("mitarbeiterAnzahl", mitarbeiterAnzahl);
                int leiterAnzahl = akteurRepository.anzahlAkteure("Verwaltungsleitung");
                model.addAttribute("leiterAnzahl", leiterAnzahl);
                int adminAnzahl = akteurRepository.anzahlAkteure("Administrator");
                model.addAttribute("adminAnzahl", adminAnzahl);
                int gesamtAnzahl = nutzerAnzahl + mitarbeiterAnzahl + leiterAnzahl + adminAnzahl;
                model.addAttribute("gesamtAnzahl", gesamtAnzahl);
                List akteurAntraege = antraegeRepository.listAlleAntraege();
                model.addAttribute("akteurAntraege", akteurAntraege);
                List daten = akteurRepository.daten();
                model.addAttribute("daten", daten);
                List tuerzugaenge = logsRepository.erstellungszeitpunkte();
                model.addAttribute("tuerzugaenge", tuerzugaenge);
                List versuchtezugaenge = logsRepository.versuchteZugaenge();
                model.addAttribute("versuchtezugaenge", versuchtezugaenge);
                return "dash_admin";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @GetMapping("/profil")
    public String get_h_profilPage(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            model.addAttribute("benutzername", username);
            String name = akteurRepository.findVorname(username) + ' ' + akteurRepository.findNachname(username);
            model.addAttribute("name", name);
            String adresse = akteurRepository.findAdresse(username);
            model.addAttribute("adresse", adresse);
            String rolle = akteurRepository.findRolle(username);
            model.addAttribute("rolle", rolle);
            String geburtsdatum = akteurRepository.findGeburtsdatum(username);
            model.addAttribute("geburtsdatum", geburtsdatum);
            String email = akteurRepository.findEmail(username);
            model.addAttribute("email", email);
            String telefonnummer = akteurRepository.findTelefonnummer(username);
            model.addAttribute("telefonnummer", telefonnummer);
            String arbeitsvertragsende = akteurRepository.findArbeitsvertragsende(username);
            model.addAttribute("arbeitsvertragsende", arbeitsvertragsende);
            String dashboardlink = "/dashboard" + rolle.toLowerCase();
            model.addAttribute("dashboardlink", dashboardlink);
            List keycards = keyCardRepository.listenurKeycards(username);
            model.addAttribute("keycards", keycards);
            return "profil";
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @GetMapping("/antrag_stellen")
    public String get_antrag_stellen_Page(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Nutzer") || rolle.equals("Verwaltungsmitarbeiter")) {
                return "antrag_stellen";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @GetMapping("/antrag_neue_keycard")
    public String get_antrag_neue_keycard_Page(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Nutzer") || rolle.equals("Verwaltungsmitarbeiter")) {
                return "antrag";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @RequestMapping("/user")
    public String get_user_Page(Model model,
            @RequestParam(value = "benutzername", required = false) String benutzername,
            @RequestParam(value = "chosenKeycard", required = false) Integer chosenKeycard,
            @RequestParam(value = "redirectFrom", required = false) String redirectFrom) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);

            // ACCESS CHECK for benutzername Parameter
            if (rolle.equals("Verwaltungsmitarbeiter")) {
                if (benutzername != null) {
                    List nutzerUsernames = akteurRepository.findUsernameByRole("Nutzer");
                    boolean flag = false;
                    for (int i = 0; i < nutzerUsernames.size(); i++) {
                        if (benutzername.equals(nutzerUsernames.get(i))) {
                            flag = true;
                        }
                    }
                    if (flag == false) {
                        return "denied_access";
                    }
                }
            }

            // ACCESS CHECK for Keycard Parameter
            if (benutzername != null && chosenKeycard != null) {
                List userKeycards = keyCardRepository.listenurKeycards(benutzername);
                boolean flag = false;
                for (int i = 0; i < userKeycards.size(); i++) {
                    if (chosenKeycard.equals(userKeycards.get(i))) {
                        flag = true;
                    }
                }
                if (flag == false) {
                    return "denied_access";
                }
            }

            // ACCESS CHECK ROLLE
            if (rolle.equals("Verwaltungsmitarbeiter") || rolle.equals("Verwaltungsleitung")) {
                // my Email
                String fromEmail = akteurRepository.findEmail(username);
                model.addAttribute("fromEmail", fromEmail);

                // DETAILS Client
                String benutzerName = benutzername;
                model.addAttribute("benutzername", benutzerName);
                String name = akteurRepository.findVorname(benutzerName) + ' '
                        + akteurRepository.findNachname(benutzerName);
                model.addAttribute("name", name);
                String adresse = akteurRepository.findAdresse(benutzerName);
                model.addAttribute("adresse", adresse);
                String rolleYou = akteurRepository.findRolle(benutzerName);
                model.addAttribute("rolle", rolleYou);
                String geburtsdatum = akteurRepository.findGeburtsdatum(benutzerName);
                model.addAttribute("geburtsdatum", geburtsdatum);
                String email = akteurRepository.findEmail(benutzerName);
                model.addAttribute("email", email);

                // ZUTRITTSBERECHTIGUNGEN
                List keycards = keyCardRepository.listeKeycards(benutzerName);
                model.addAttribute("keycards", keycards);
                int anzahlKeycards = keycards.size();
                model.addAttribute("anzahlKeycards", anzahlKeycards);
                if (chosenKeycard != null) {
                    int keycard_id = chosenKeycard;
                    model.addAttribute("keycard_id", keycard_id);
                    String anfangstag = keyCardRepository.findanfangstag(chosenKeycard);
                    model.addAttribute("anfangstag", anfangstag);
                    String endtag = keyCardRepository.findendtag(chosenKeycard);
                    model.addAttribute("endtag", endtag);
                    String status = keyCardRepository.findstatus(chosenKeycard);
                    model.addAttribute("status", status);
                }
                // INPUT CHECK FOR KEYCARD UBERTRAGEN

                if (rolle.equalsIgnoreCase("Verwaltungsmitarbeiter")) {
                    List users = akteurRepository.findUsernameByRole("Nutzer");
                    System.out.println(users);
                    model.addAttribute("users", users);
                } else {
                    List users = akteurRepository.findUsernames();
                    model.addAttribute("users", users);
                }

                return "user";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @RequestMapping("/keycards_verwalten")
    public String get_h_keycard_verwalten_Page(Model model,
            @RequestParam(value = "chosenKeycard", required = false) Integer chosenKeycard) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);

            // ACCESS ROLLE
            if (!rolle.equals("Administrator")) {
                List keycards = keyCardRepository.listeKeycards(username);

                model.addAttribute("keycards", keycards);
                int anzahlKeycards = keycards.size();
                model.addAttribute("anzahlKeycards", anzahlKeycards);
                if (rolle.equals("Verwaltungsmitarbeiter") || rolle.equals("Nutzer")) {
                    String name = akteurRepository.findVorname(username) + ' '
                            + akteurRepository.findNachname(username);
                    model.addAttribute("name", name);

                    List keycard_without_first = keyCardRepository.listeKeycards(username);
                    // wird benötigt, weil class active des carousels probleme macht
                    if (anzahlKeycards > 1) {
                        keycard_without_first.remove(0);
                    }
                    model.addAttribute("keycards_without_first", keycard_without_first);
                    return "keycard_verwalten";
                } else {
                    if (chosenKeycard != null) {
                        boolean flag = false;
                        List userKeycards = keyCardRepository.listenurKeycards(username);
                        for (int i = 0; i < userKeycards.size(); i++) {
                            if (chosenKeycard.equals(userKeycards.get(i))) {
                                flag = true;
                            }
                        }
                        if (flag == false) {
                            return "denied_access";
                        }

                        int keycard_id = chosenKeycard;
                        model.addAttribute("keycard_id", keycard_id);
                        String anfangstag = keyCardRepository.findanfangstag(chosenKeycard);
                        model.addAttribute("anfangstag", anfangstag);
                        String endtag = keyCardRepository.findendtag(chosenKeycard);
                        model.addAttribute("endtag", endtag);
                        String status = keyCardRepository.findstatus(chosenKeycard);
                        model.addAttribute("status", status);
                    }

                    return "vl_keycards_verwalten";
                }
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @RequestMapping("/zutritt_bearbeiten")
    public String get_zutritte_bearbeiten_Page(Model model,
            @RequestParam(value = "keycard_id", required = false) Integer keycard_id) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            model.addAttribute("rolle", rolle);

            // ACCESS CHECK for Keycard Parameter
            if (keycard_id != null) {
                if (rolle.equals("Verwaltungsmitarbeiter")) {
                    boolean flag = false;
                    List userKeycards = keyCardRepository.findKeycardsByRole("Nutzer");
                    for (int i = 0; i < userKeycards.size(); i++) {
                        if (keycard_id.equals(userKeycards.get(i))) {
                            flag = true;
                        }
                    }
                    if (flag == false) {
                        return "denied_access";
                    }
                }
                if (rolle.equalsIgnoreCase("Verwaltungsleitung")) {
                    boolean flag = false;
                    List userKeycards = keyCardRepository.listenurKeycardIds();
                    for (int i = 0; i < userKeycards.size(); i++) {
                        if (keycard_id.equals(userKeycards.get(i))) {
                            flag = true;
                        }
                    }
                    if (flag == false) {
                        return "exception";
                    }
                }
            }

            if (rolle.equals("Verwaltungsleitung") || rolle.equals("Verwaltungsmitarbeiter")) {
                // ALL ROOMS AND ARES FOR CHECKING THE FORM
                if (rolle.equalsIgnoreCase("Verwaltungsmitarbeiter")) {
                    // ALLE UNSENSTIVEN RÄUME/BEREICHE ZUM HINZUFÜGEN
                    List allRooms = facilityRepository.allUnsensitiveRaeume();
                    model.addAttribute("allRooms", allRooms);
                    List allAreas = facilityRepository.allUnsensitiveBereiche();
                    model.addAttribute("allAreas", allAreas);
                    // ALLE UNSENSITIVEN RÄUME/BEREICHE ZUM ENTFERNEN
                    List raumBerechtigunen = keyCardRepository.raumUnsensitiveBerechtigungen(keycard_id);
                    int anzahlRaumBerechtigungen = raumBerechtigunen.size();
                    model.addAttribute("anzahlRaumBerechtigungen", anzahlRaumBerechtigungen);
                    model.addAttribute("raumBerechtigunen", raumBerechtigunen);
                    // BEREICHE
                    List bereichBerechtigungen = keyCardRepository.bereichUnsensitiveBerechtigungen(keycard_id);
                    int anzahlBereichBerechtigungen = bereichBerechtigungen.size();
                    model.addAttribute("bereichBerechtigungen", bereichBerechtigungen);
                    model.addAttribute("anzahlBereichBerechtigungen", anzahlBereichBerechtigungen);
                } else {
                    // ALLE RÄUME/BEREICHE ZUM HINZUFÜGEN
                    List allRooms = facilityRepository.allRaeume();
                    model.addAttribute("allRooms", allRooms);
                    List allAreas = facilityRepository.allBereiche();
                    model.addAttribute("allAreas", allAreas);

                    // ALLE RÄUME/BEREICHE ZUM ENTFERNEN
                    List raumBerechtigunen = keyCardRepository.raumNameBerechtigungen(keycard_id);
                    int anzahlRaumBerechtigungen = raumBerechtigunen.size();
                    model.addAttribute("anzahlRaumBerechtigungen", anzahlRaumBerechtigungen);
                    model.addAttribute("raumBerechtigunen", raumBerechtigunen);
                    // BEREICHE
                    List bereichBerechtigungen = keyCardRepository.bereichNameBerechtigungen(keycard_id);
                    int anzahlBereichBerechtigungen = bereichBerechtigungen.size();
                    model.addAttribute("bereichBerechtigungen", bereichBerechtigungen);
                    model.addAttribute("anzahlBereichBerechtigungen", anzahlBereichBerechtigungen);
                }
                String anfangstag = keyCardRepository.findanfangstag(keycard_id);
                String endtag = keyCardRepository.findendtag(keycard_id);
                model.addAttribute("anfangstag", anfangstag);
                model.addAttribute("endtag", endtag);
                model.addAttribute("keycard_id", keycard_id);
                return "zutritt_bearbeiten";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @PostMapping("/rechte_bearbeitet")
    public String rechteBearbeiten(Model model,
            @RequestParam(value = "keycardId", required = false) int keycardId,
            @RequestParam(value = "anfangstag", required = false) Date anfangstag,
            @RequestParam(value = "endtag", required = false) Date endtag,
            @RequestParam(value = "bereich", required = false) String bereiche,
            @RequestParam(value = "raum", required = false) String räume) {

        System.out.println(bereiche);
        System.out.println(räume);

        try {
            if (bereiche == null && räume == null) {
                return "redirect:/zutritt_bearbeiten" + "?keycard_id=" + keycardId;
            }

            // Date Ändern
            keyCardRepository.updateAnfangstag(anfangstag, keycardId);
            keyCardRepository.updateEndtag(endtag, keycardId);

            // Input der Räume als Liste abspeichern.
            List<String> raumListeFinal = new ArrayList<String>();
            if (!räume.isEmpty() && räume != null) {
                List<String> raumListeInput = new ArrayList<String>(Arrays.asList(räume.split(",")));
                for (int i = 0; i < raumListeInput.size(); i++) {
                    if (!raumListeFinal.contains(raumListeInput.get(i))) {
                        raumListeFinal.add(raumListeInput.get(i));
                    }
                }
            }
            // Input der Bereiche als Liste abspeichern.
            List<String> bereichListeFinal = new ArrayList<String>();
            if (!bereiche.isEmpty() && bereiche != null) {
                List<String> bereichListeInput = new ArrayList<String>(Arrays.asList(bereiche.split(",")));
                for (int i = 0; i < bereichListeInput.size(); i++) {
                    if (!bereichListeFinal.contains(bereichListeInput.get(i))) {
                        bereichListeFinal.add(bereichListeInput.get(i));
                    }
                }
            }

            // Liste der Räume und Bereiche in der DB speichern
            keyCardRepository.deleteAlleZugangrechts(keycardId); // Erst alle Zugangsrechte löschen
            if (raumListeFinal.size() > 0) {
                for (int i = 0; i < raumListeFinal.size(); i++) {
                    int zugangId;
                    if (keyCardRepository.hoechsteIDzugang() == null) {
                        zugangId = 1;
                    } else {
                        zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                    }
                    // Alle Input Zugangsrechte hinzufügen
                    int raumBereitsGebucht = keyCardRepository.raumZugangID(keycardId,
                            facilityRepository.idzuRaum(raumListeFinal.get(i)));
                    if (raumBereitsGebucht == 0) {
                        keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId,
                                facilityRepository.idzuRaum(raumListeFinal.get(i)), null);
                    }
                }
            }
            if (bereichListeFinal.size() > 0) {
                for (int j = 0; j < bereichListeFinal.size(); j++) {
                    int zugangId;
                    if (keyCardRepository.hoechsteIDzugang() == null) {
                        zugangId = 1;
                    } else {
                        zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                    }

                    int bereichBereitsGebucht = keyCardRepository.bereichZugangID(keycardId,
                            facilityRepository.idzuBereich(bereichListeFinal.get(j)));
                    if (bereichBereitsGebucht == 0) {
                        keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId, null,
                                facilityRepository.idzuBereich(bereichListeFinal.get(j)));
                    }
                }
            }

            // LOGS ERSTELLEN
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            int keycardLogId;
            if (logsRepository.hoechsteKeycardLogID() == null) {
                keycardLogId = 1;
            } else {
                keycardLogId = logsRepository.hoechsteKeycardLogID() + 1;
            }
            logsRepository.keycardlogHinzufuegen(keycardLogId, keycardId, username, "Rechte_Änderung",
                    Timestamp.valueOf(LocalDateTime.now()));

            String antragStellerEmail = keyCardRepository.findEmailByKeycard(keycardId);
            String senderEmail = akteurRepository.findEmail(username);

            String nachricht = "Guten Tag, ihre Keycard #" + keycardId
                    + " wurde von der Verwaltung bearbeitet. Bitte informieren Sie sich hierfür im Portal unter 'Keycard verwalten'.";
            emailSenderService.sendSimpleMail(antragStellerEmail, senderEmail,
                    "Nachricht zu Keycard " + keycardId, nachricht);

            return "redirect:/zutritt_bearbeiten" + "?keycard_id=" + keycardId;
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @RequestMapping("/verlust_melden")
    public String verlust_Melden(@RequestParam("keycardid") int keycardid) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            keyCardRepository.updateStatus("Deaktiviert", keycardid);

            String toMail = akteurRepository.findEmail(keyCardRepository.findOwner(keycardid));
            String fromMail = akteurRepository.findEmail(username);
            String subject = "Verlust der Karte " + keycardid;
            String body = "Die Karte " + keycardid + " wurde deaktiviert";

            Email verlustmail = new Email(toMail, fromMail, subject, body);
            emailSenderService.sendSimpleMail(verlustmail.getToEmail(),
                    verlustmail.getFromEmail(),
                    verlustmail.getSubject(),
                    verlustmail.getBody());

            return "redirect:/keycards_verwalten";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @GetMapping("/antrag_auf_neue_keycard")
    public String get_neue_keycard_Page(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            List<String> nurBereichsnamen = facilityRepository.nurBereichsnamen();
            model.addAttribute("nurBereichsnamen", nurBereichsnamen);
            List<String> nurRaumnamen = facilityRepository.nurRaumnamen();
            model.addAttribute("nurRaumnamen", nurRaumnamen);
            // ALL ROOMS AND ARES FOR CHECKING THE FORM
            List allRooms = facilityRepository.allRaeume();
            model.addAttribute("allRooms", allRooms);
            List allAreas = facilityRepository.allBereiche();
            model.addAttribute("allAreas", allAreas);
            if (rolle.equals("Nutzer") || rolle.equals("Verwaltungsmitarbeiter")) {
                return "antrag_neu";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @GetMapping("/test")
    public String get_Test_Page(Model model) {
        return "test";

    }

    @GetMapping("/antrag_auf_erweiterung")
    public String get_keycard_erweitern_Page(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Nutzer") || rolle.equals("Verwaltungsmitarbeiter")) {
                // USER KEYCARDS
                List allKeycards = keyCardRepository.listeKeycards(username);
                int anzahlAllKeycards = allKeycards.size();
                model.addAttribute("anzahlKeycards", anzahlAllKeycards);

                // USER ACTIVE KEYCARDS
                List activeKeycards = keyCardRepository.listeActiveKeycards(username);
                model.addAttribute("keycards", activeKeycards);
                int anzahlActiveKeycards = activeKeycards.size();
                model.addAttribute("anzahlActiveKeycards", anzahlActiveKeycards);

                // ALL ROOMS AND ARES FOR CHECKING THE FORM
                List allRooms = facilityRepository.allRaeume();
                model.addAttribute("allRooms", allRooms);
                List allAreas = facilityRepository.allBereiche();
                model.addAttribute("allAreas", allAreas);
                return "keycard_erweitern";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }
    }

    @GetMapping("/antraege_bearbeiten")
    public String get_antraege_bearbeiten_Page(Model model,
            @RequestParam(value = "message", required = false) String message) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Verwaltungsmitarbeiter") || rolle.equals("Verwaltungsleitung")) {

                model.addAttribute("benutzername", username);
                List akteurAntraege = antraegeRepository.listBearbeitendeAntraege(rolle);
                int anzahlAntraege = akteurAntraege.size();
                model.addAttribute("anzahlAntraege", anzahlAntraege);
                model.addAttribute("akteurAntraege", akteurAntraege);

                return "antraege_bearbeiten";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @RequestMapping("/antrag_bearbeiten")
    public String get_antrag_bearbeiten_Page(Model model, @RequestParam("antrag_id") int antrag_id) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);

            // ACCESS CHECK antrags_id Parameter
            List meineBearbeiterAntraege = antraegeRepository.listAntragIdBearbeitendeAntraege(rolle);
            System.out.println(meineBearbeiterAntraege);
            boolean flag = false;
            for (int i = 0; i < meineBearbeiterAntraege.size(); i++) {
                if (meineBearbeiterAntraege.get(i).equals(antrag_id)) {
                    flag = true;
                }
            }
            if (flag == false) {
                return "denied_access";
            }

            // ACCESS CHECK ROLLE
            if (rolle.equals("Verwaltungsmitarbeiter") || rolle.equals("Verwaltungsleitung")) {
                String art = antraegeRepository.getAntragart(antrag_id);

                List raeume = antraegeRepository.getBeantragtFuerRäume(antrag_id);
                int anzahlRaeume = raeume.size();
                model.addAttribute("anzahlRaeume", anzahlRaeume);
                model.addAttribute("raeume", raeume);
                List bereiche = antraegeRepository.getBeantragtFuerBereiche(antrag_id);
                int anzahlBereiche = bereiche.size();
                model.addAttribute("anzahlBereiche", anzahlBereiche);
                model.addAttribute("bereiche", bereiche);
                Antrag answer = antraegeRepository.finden(antrag_id);
                if (answer != null) {
                    model.addAttribute("answer", answer);
                }

                if (art.equals("NEU")) {
                    Date anfangstag = antraegeRepository.antragsAnfangstag(antrag_id);
                    Date endtag = antraegeRepository.antragsEndstag(antrag_id);
                    model.addAttribute("anfangstag", anfangstag);
                    model.addAttribute("endtag", endtag);
                    return "neue_bearbeiten";
                } else {
                    int keycard_id = antraegeRepository.getKeycardID(antrag_id);
                    model.addAttribute("keycard_id", keycard_id);

                    String anfangstag = keyCardRepository.findanfangstag(keycard_id);
                    String endtag = keyCardRepository.findendtag(keycard_id);
                    model.addAttribute("anfangstag", anfangstag);
                    model.addAttribute("endtag", endtag);
                    return "erweitern_bearbeiten";
                }
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @PostMapping("/antrag_bearbeitet")
    public String get_antrag_bearbeitet(Model model,
            @RequestParam(value = "antrag_id", required = false) int antrag_id,
            @RequestParam(value = "bereich", required = false) String bereiche,
            @RequestParam(value = "raum", required = false) String räume,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "bearbeiterMessage", required = false) String bearbeiterMessage) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            Date anfangstag = antraegeRepository.antragsAnfangstag(antrag_id);
            Date endtag = antraegeRepository.antragsEndstag(antrag_id);

            if (status.equals("akzeptiert")) {
                Antrag antrag = antraegeRepository.finden(antrag_id);
                String antragsArt = antraegeRepository.getAntragart(antrag_id);

                String bereicheAntwort = "";
                String räumeAntwort = "";

                if (antragsArt.equalsIgnoreCase("NEU")) {
                    int keycardId;
                    if (keyCardRepository.hoechsteID() == null) {
                        keycardId = 1;
                    } else {
                        keycardId = keyCardRepository.hoechsteID() + 1;
                    }

                    keyCardRepository.keycardHinzufuegen(keycardId,
                            antrag.getBenutzername(), anfangstag, endtag,
                            "Aktiviert");

                    int keycardLogId;
                    if (logsRepository.hoechsteKeycardLogID() == null) {
                        keycardLogId = 1;
                    } else {
                        keycardLogId = logsRepository.hoechsteKeycardLogID() + 1;
                    }
                    logsRepository.keycardlogHinzufuegen(keycardLogId, keycardId, username, "Erstellung",
                            Timestamp.valueOf(LocalDateTime.now()));

                    List<String> räumeFinal = new ArrayList<String>();
                    List<String> bereicheFinal = new ArrayList<String>();

                    if (räume != null && !räume.isEmpty()) {
                        List<String> raumListe = new ArrayList<String>(Arrays.asList(räume.split(",")));
                        for (int x = 0; x < raumListe.size(); x++) {

                            if (!räumeFinal.contains(facilityRepository.getRaumName(Integer.valueOf(raumListe.get(x))))
                                    && facilityRepository.getRaumName(Integer.valueOf(raumListe.get(x))) != null) {

                                räumeFinal.add(facilityRepository.getRaumName(Integer.valueOf(raumListe.get(x))));
                            }

                        }
                    }

                    if (bereiche != null && !bereiche.isEmpty()) {
                        List<String> bereichListe = new ArrayList<String>(Arrays.asList(bereiche.split(",")));
                        for (int y = 0; y < bereichListe.size(); y++) {

                            if (!bereicheFinal
                                    .contains(facilityRepository.getBereichsName(Integer.valueOf(bereichListe.get(y))))
                                    &&
                                    facilityRepository.getBereichsName(Integer.valueOf(bereichListe.get(y))) != null) {

                                bereicheFinal.add(facilityRepository
                                        .getBereichsName(Integer.valueOf(bereichListe.get(y))));
                            }

                        }
                    }

                    if (räumeFinal.size() > 0) {
                        for (int i = 0; i < räumeFinal.size(); i++) {
                            int zugangId;
                            if (keyCardRepository.hoechsteIDzugang() == null) {
                                zugangId = 1;
                            } else {
                                zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                            }

                            int raumBereitsGebucht = keyCardRepository.raumZugangID(keycardId,
                                    facilityRepository.idzuRaum(räumeFinal.get(i)));
                            if (raumBereitsGebucht == 0) {
                                keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId,
                                        facilityRepository.idzuRaum(räumeFinal.get(i)), null);
                            }

                            räumeAntwort += räumeFinal.get(i) + " ";
                        }
                    }

                    if (bereicheFinal.size() > 0) {
                        for (int j = 0; j < bereicheFinal.size(); j++) {
                            int zugangId;
                            if (keyCardRepository.hoechsteIDzugang() == null) {
                                zugangId = 1;
                            } else {
                                zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                            }

                            int bereichBereitsGebucht = keyCardRepository.bereichZugangID(keycardId,
                                    facilityRepository.idzuBereich(bereicheFinal.get(j)));
                            if (bereichBereitsGebucht == 0) {
                                keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId, null,
                                        facilityRepository.idzuBereich(bereicheFinal.get(j)));
                            }

                            bereicheAntwort += bereicheFinal.get(j) + " ";
                        }
                    }

                    String antragStellerEmail = antraegeRepository.emailzuAntrag(antrag_id);
                    String senderEmail = akteurRepository.findEmail(username);

                    String body = "";
                    if (bereicheAntwort.equals("")) {
                        body = "Guten Tag, folgende Räume wurden angenommen:" + räumeAntwort;
                    } else if (räumeAntwort.equals("")) {
                        body = "Guten Tag, folgende Bereiche wurden angenommen:" + bereicheAntwort;
                    } else {
                        body = "Guten Tag, folgende Bereiche " + bereicheAntwort + " und folgende Räume " + räumeAntwort
                                + " wurden für Sie akzeptiert";

                    }

                    emailSenderService.sendSimpleMail(antragStellerEmail, senderEmail,
                            "Nachricht zu Antrag " + antrag_id, body);

                    antraegeRepository.updateStatus("Angenommen", antrag_id);
                    archivAntrag(antrag_id, "Angenommen");
                    antraegeRepository.deleteAntrag(antrag_id);
                    antraegeRepository.deleteBeantragtFuer(antrag_id);

                } else if (antragsArt.equalsIgnoreCase("BESTEHEND")) {
                    int keycardId = antraegeRepository.getKeycardID(antrag_id);

                    int keycardLogId;
                    if (logsRepository.hoechsteKeycardLogID() == null) {
                        keycardLogId = 1;
                    } else {
                        keycardLogId = logsRepository.hoechsteKeycardLogID() + 1;
                    }
                    logsRepository.keycardlogHinzufuegen(keycardLogId, keycardId, username, "Rechte_Änderung",
                            Timestamp.valueOf(LocalDateTime.now()));

                    List<String> räumeFinal = new ArrayList<String>();
                    List<String> bereicheFinal = new ArrayList<String>();

                    if (räume != null && !räume.isEmpty()) {
                        List<String> raumListe = new ArrayList<String>(Arrays.asList(räume.split(",")));
                        for (int x = 0; x < raumListe.size(); x++) {

                            if (!räumeFinal.contains(facilityRepository.getRaumName(Integer.valueOf(raumListe.get(x))))
                                    && facilityRepository.getRaumName(Integer.valueOf(raumListe.get(x))) != null) {

                                räumeFinal.add(facilityRepository.getRaumName(Integer.valueOf(raumListe.get(x))));
                            }

                        }
                    }

                    if (bereiche != null && !bereiche.isEmpty()) {
                        List<String> bereichListe = new ArrayList<String>(Arrays.asList(bereiche.split(",")));
                        for (int y = 0; y < bereichListe.size(); y++) {

                            if (!bereicheFinal
                                    .contains(facilityRepository.getBereichsName(Integer.valueOf(bereichListe.get(y))))
                                    &&
                                    facilityRepository.getBereichsName(Integer.valueOf(bereichListe.get(y))) != null) {

                                bereicheFinal.add(facilityRepository
                                        .getBereichsName(Integer.valueOf(bereichListe.get(y))));
                            }

                        }
                    }

                    if (räumeFinal.size() > 0) {
                        for (int i = 0; i < räumeFinal.size(); i++) {
                            int zugangId;
                            if (keyCardRepository.hoechsteIDzugang() == null) {
                                zugangId = 1;
                            } else {
                                zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                            }

                            int raumBereitsGebucht = keyCardRepository.raumZugangID(keycardId,
                                    facilityRepository.idzuRaum(räumeFinal.get(i)));
                            if (raumBereitsGebucht == 0) {
                                keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId,
                                        facilityRepository.idzuRaum(räumeFinal.get(i)), null);
                            }

                            räumeAntwort += räumeFinal.get(i) + " ";

                        }
                    }

                    if (bereicheFinal.size() > 0) {
                        for (int j = 0; j < bereicheFinal.size(); j++) {
                            int zugangId;
                            if (keyCardRepository.hoechsteIDzugang() == null) {
                                zugangId = 1;
                            } else {
                                zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                            }

                            int bereichBereitsGebucht = keyCardRepository.bereichZugangID(keycardId,
                                    facilityRepository.idzuBereich(bereicheFinal.get(j)));
                            if (bereichBereitsGebucht == 0) {
                                keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId, null,
                                        facilityRepository.idzuBereich(bereicheFinal.get(j)));
                            }

                            bereicheAntwort += bereicheFinal.get(j) + " ";

                        }
                    }

                    String antragStellerEmail = antraegeRepository.emailzuAntrag(antrag_id);
                    String senderEmail = akteurRepository.findEmail(username);

                    String body = "";
                    if (bereicheAntwort.equals("")) {
                        body = "Guten Tag, folgende Räume wurden angenommen:" + räumeAntwort;
                    } else if (räumeAntwort.equals("")) {
                        body = "Guten Tag, folgende Bereiche wurden angenommen:" + bereicheAntwort;
                    } else {
                        body = "Guten Tag, folgende Bereiche " + bereicheAntwort + " und folgende Räume " + räumeAntwort
                                + " wurden für Sie akzeptiert";

                    }

                    emailSenderService.sendSimpleMail(antragStellerEmail, senderEmail,
                            "Nachricht zu Antrag " + antrag_id, body);

                    antraegeRepository.updateStatus("Angenommen", antrag_id);
                    archivAntrag(antrag_id, "Angenommen");
                    antraegeRepository.deleteAntrag(antrag_id);
                    antraegeRepository.deleteBeantragtFuer(antrag_id);
                }
            } else {
                String antragStellerEmail = antraegeRepository.emailzuAntrag(antrag_id);
                String senderEmail = akteurRepository.findEmail(username);

                emailSenderService.sendSimpleMail(antragStellerEmail, senderEmail, "Nachricht zu Antrag " + antrag_id,
                        "Guten Tag, Ihr Antrag " + antrag_id
                                + " wurde abgelehnt. Die Bearbeitungsstelle gibt hierfür folgende Erklärung ab: "
                                + bearbeiterMessage);

                antraegeRepository.updateStatus("Abgelehnt", antrag_id);
                archivAntrag(antrag_id, "Abgelehnt");
                antraegeRepository.deleteAntrag(antrag_id);
                antraegeRepository.deleteBeantragtFuer(antrag_id);

            }
            return "redirect:/antraege_bearbeiten";
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @PostMapping("/antrag_verwalten")
    public String getAkteurFinderResult(Model model, @RequestParam("wert") String wert) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);

            if (rolle.equals("Verwaltungsmitarbeiter") || rolle.equals("Verwaltungsleitung")) {
                List<Konto> akteursuchergebnis = akteurRepository.searchNutzer(wert);
                model.addAttribute("akteursuchergebnis", akteursuchergebnis);
                int ergebnisSize = akteursuchergebnis.size();
                model.addAttribute("ergebnisSize", ergebnisSize);
            } else {
                List<Konto> akteursuchergebnis = akteurRepository.search(wert);
                model.addAttribute("akteursuchergebnis", akteursuchergebnis);
                System.out.println(akteursuchergebnis);
                int ergebnisSize = akteursuchergebnis.size();
                model.addAttribute("ergebnisSize", ergebnisSize);
            }

            return "nutzerverwaltung";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @RequestMapping("/zutrittsuebersicht_einsehen")
    public String get_h_zutritte_Page(Model model,
            @RequestParam(value = "keycard_id", required = false) Integer keycard_id) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Nutzer") ||
                    rolle.equals("Verwaltungsmitarbeiter") ||
                    rolle.equals("Verwaltungsleitung")) {
                List raumBerechtigungen = keyCardRepository.raumBerechtigungen(keycard_id);
                int anzahlRaumBerechtigungen = raumBerechtigungen.size();
                model.addAttribute("anzahlRaumBerechtigungen", anzahlRaumBerechtigungen);
                model.addAttribute("raumBerechtigungen", raumBerechtigungen);

                List bereichBerechtigungen = keyCardRepository.bereichBerechtigungen(keycard_id);
                int anzhalBereichBerechtigungen = bereichBerechtigungen.size();
                model.addAttribute("anzhalBereichBerechtigungen", anzhalBereichBerechtigungen);
                model.addAttribute("bereichBerechtigungen", bereichBerechtigungen);

                return "zutritte";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }
    }

    @RequestMapping("/zutrittsrechte_inhaber")
    public String getRechteInhaberResult(Model model,
            @RequestParam(value = "facilityName", required = false) String facilityName,
            @RequestParam(value = "facilityType", required = false) String facilityType) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Verwaltungsmitarbeiter") || rolle.equals("Verwaltungsleitung")) {

                if (facilityType.equals("Raum")) {

                    List berechtigtePersonen = facilityRepository.findUsersbyRaum(facilityName);
                    model.addAttribute("berechtigtePersonen", berechtigtePersonen);
                    int anzahlBerechtigungen = berechtigtePersonen.size();
                    model.addAttribute("anzahlBerechtigungen", anzahlBerechtigungen);
                } else if (facilityType.equals("Gebaeude")) {
                    List berechtigtePersonen = facilityRepository.findUsersbyGebaeude(facilityName);
                    model.addAttribute("berechtigtePersonen", berechtigtePersonen);
                    int anzahlBerechtigungen = berechtigtePersonen.size();
                    model.addAttribute("anzahlBerechtigungen", anzahlBerechtigungen);
                } else if (facilityType.equals("Bereich")) {
                    List berechtigtePersonen = facilityRepository.findUsersbyBereich(facilityName);
                    model.addAttribute("berechtigtePersonen", berechtigtePersonen);
                    int anzahlBerechtigungen = berechtigtePersonen.size();
                    model.addAttribute("anzahlBerechtigungen", anzahlBerechtigungen);
                }
                return "rechtsinhaber";

            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @GetMapping("/gebaeude_uebersicht")
    public String get_uebersicht_geb_Page(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Verwaltungsmitarbeiter") || rolle.equals("Verwaltungsleitung")) {
                return "geb_ubersicht";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @RequestMapping("/gebaeude")
    public String get_gebaeude_Page(Model model,
            @RequestParam(value = "gebaeudeName", required = false) String gebaeudeName) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Verwaltungsmitarbeiter") || rolle.equals("Verwaltungsleitung")) {
                String facility = gebaeudeName;
                model.addAttribute("facility", facility);
                List raeume = facilityRepository.alleRaeumeProFacility(facility);
                model.addAttribute("raeume", raeume);
                int anzahlRaeume = raeume.size();
                model.addAttribute("anzahlRaeume", anzahlRaeume);
                List sensitiveRäume = facilityRepository.sensitiveRaeumeProFacility(facility);
                int anzahlSense = sensitiveRäume.size();
                model.addAttribute("anzahlSense", anzahlSense);

                return "building";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }
    }

    @GetMapping("/zutrittsrechte_einsehen")
    public String get_zutrittsrechte_einsehen_Page(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Verwaltungsmitarbeiter") || rolle.equals("Verwaltungsleitung")) {
                return "zutrittsrechte_einsehen";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @RequestMapping("/zutrittsprotokoll_download")
    public String zutrittsProtokollDownload(Model model,
            @RequestParam(value = "raum", required = false) String raum,
            @RequestParam(value = "startdatum", required = false) String startdatum,
            @RequestParam(value = "enddatum", required = false) String enddatum) {

        try {
            startdatum += " 00:00:00";
            enddatum += " 23:59:59";
            model.addAttribute("tstartdatum", startdatum);
            model.addAttribute("tenddatum", enddatum);

            Timestamp startTimestamp = Timestamp.valueOf(startdatum);
            Timestamp endTimestamp = Timestamp.valueOf(enddatum);

            List zutrittsprotokoll = logsRepository.tuerlogSuche(raum, startTimestamp, endTimestamp);
            model.addAttribute("zutrittsprotokoll", zutrittsprotokoll);
            model.addAttribute("raum", raum);

            return "building";
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }
    }

    @PostMapping("/zutrittsrechte_einsehen")
    public String getFacilityFinderResult(Model model,
            @RequestParam(value = "wert", required = false) String wert,
            @RequestParam(value = "searchOption", required = false) String searchOption) {

        try {

            if (searchOption.equals("Bereich")) {
                List ergebnisList = facilityRepository.searchBereiche(wert);
                model.addAttribute("ergebnisList", ergebnisList);
                int ergebnisSize = ergebnisList.size();
                model.addAttribute("ergebnisSize", ergebnisSize);
            } else if (searchOption.equals("Raum")) {
                List ergebnisList = facilityRepository.searchRaeume(wert);
                model.addAttribute("ergebnisList", ergebnisList);
                int ergebnisSize = ergebnisList.size();
                model.addAttribute("ergebnisSize", ergebnisSize);
            } else {
                List ergebnisList = facilityRepository.searchGebaeude(wert);
                model.addAttribute("ergebnisList", ergebnisList);
                int ergebnisSize = ergebnisList.size();
                model.addAttribute("ergebnisSize", ergebnisSize);
            }
            model.addAttribute("searchOption", searchOption);

            return "zutrittsrechte_einsehen";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }
    }

    @GetMapping("/nutzer_verwalten")
    public String get_nutzerverwaltung_Page(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Verwaltungsmitarbeiter") || rolle.equals("Verwaltungsleitung")) {
                model.addAttribute("rolle", rolle);
                return "nutzerverwaltung";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @GetMapping("/neue_keycard_erstellen")
    public String get_vl_erstellen_keycard_Page(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Verwaltungsleitung")) {
                // ALL ROOMS AND ARES FOR CHECKING THE FORM
                List allRooms = facilityRepository.allRaeume();
                model.addAttribute("allRooms", allRooms);
                List allAreas = facilityRepository.allBereiche();
                model.addAttribute("allAreas", allAreas);
                return "vl_erstellen_keycard";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @PostMapping("/neue_keycard_erstellt")
    public String vl_neue_keycard_erstelllt(Model model,
            @RequestParam(value = "anfangstag", required = false) Date anfangstag,
            @RequestParam(value = "endtag", required = false) Date endtag,
            @RequestParam(value = "raum", required = false) String räume,
            @RequestParam(value = "bereich", required = false) String bereiche) {
        try {
            // Keycard_id generieren
            int keycardId;
            if (keyCardRepository.hoechsteID() == null) {
                keycardId = 1;
            } else {
                keycardId = keyCardRepository.hoechsteID() + 1;
            }
            // Keycard der DB hinzufügen
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            keyCardRepository.keycardHinzufuegen(
                    keycardId,
                    username,
                    anfangstag,
                    endtag,
                    "Aktiviert");
            // Input der Räume als Liste abspeichern.
            List<String> raumListeFinal = new ArrayList<String>();
            if (!räume.isEmpty() && räume != null) {
                List<String> raumListeInput = new ArrayList<String>(Arrays.asList(räume.split(",")));
                for (int i = 0; i < raumListeInput.size(); i++) {
                    if (!raumListeFinal.contains(raumListeInput.get(i))) {
                        raumListeFinal.add(raumListeInput.get(i));
                    }
                }
            }
            // Input der Bereiche als Liste abspeichern.
            List<String> bereichListeFinal = new ArrayList<String>();
            if (!bereiche.isEmpty() && bereiche != null) {
                List<String> bereichListeInput = new ArrayList<String>(Arrays.asList(bereiche.split(",")));
                for (int i = 0; i < bereichListeInput.size(); i++) {
                    if (!bereichListeFinal.contains(bereichListeInput.get(i))) {
                        bereichListeFinal.add(bereichListeInput.get(i));
                    }
                }
            }
            // Liste der Räume und Bereiche in der DB speichern
            keyCardRepository.deleteAlleZugangrechts(keycardId); // Erst alle Zugangsrechte löschen
            for (int i = 0; i < raumListeFinal.size(); i++) {
                int zugangId;
                if (keyCardRepository.hoechsteIDzugang() == null) {
                    zugangId = 1;
                } else {
                    zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                }
                // Alle Input Zugangsrechte hinzufügen
                keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId,
                        facilityRepository.idzuRaum(raumListeFinal.get(i)), null);
            }
            if (bereichListeFinal.size() > 0) {
                for (int j = 0; j < bereichListeFinal.size(); j++) {
                    int zugangId;
                    if (keyCardRepository.hoechsteIDzugang() == null) {
                        zugangId = 1;
                    } else {
                        zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                    }
                    keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId, null,
                            facilityRepository.idzuBereich(bereichListeFinal.get(j)));
                }
            }
            // Keycard_Log erstellen für neue Karte
            int keycardLogId;
            if (logsRepository.hoechsteKeycardLogID() == null) {
                keycardLogId = 1;
            } else {
                keycardLogId = logsRepository.hoechsteKeycardLogID() + 1;
            }
            logsRepository.keycardlogHinzufuegen(keycardLogId, keycardId, username, "Erstellung",
                    Timestamp.valueOf(LocalDateTime.now()));

            return "redirect:/keycards_verwalten";
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @PostMapping("/status_aendern")
    public String changeStatus(Model model,
            @RequestParam(value = "chosenStatus", required = false) String status,
            @RequestParam(value = "keycard_id", required = false) int keycard_id,
            @RequestParam(value = "redirectPage", required = false) String redirectPage) {
        try {
            int logId = logsRepository.hoechsteKeycardLogID() + 1;
            String benutzername = SecurityContextHolder.getContext().getAuthentication().getName();
            String emailNachricht = ""; // EMAIL NACHRICHT
            if (status.equalsIgnoreCase("aktiviert")) {
                keyCardRepository.updateStatus("Aktiviert", keycard_id);
                logsRepository.keycardlogHinzufuegen(logId, keycard_id, benutzername, "Aktivierung",
                        Timestamp.valueOf(LocalDateTime.now()));
                emailNachricht = "Die Karte #" + keycard_id + " wurde aktiviert";
            } else if (status.equalsIgnoreCase("gesperrt")) {
                keyCardRepository.updateStatus("Gesperrt", keycard_id);
                logsRepository.keycardlogHinzufuegen(logId, keycard_id, benutzername, "Sperrung",
                        Timestamp.valueOf(LocalDateTime.now()));
                emailNachricht = "Die Karte #" + keycard_id + " wurde gesperrt";
            }

            // SEND MAIL
            String toMail = akteurRepository.findEmail(keyCardRepository.findOwner(keycard_id));
            String fromMail = akteurRepository.findEmail(benutzername);
            String subject = "Statusänderung Keycard #" + keycard_id;
            emailSenderService.sendSimpleMail(toMail, fromMail, subject, emailNachricht);

            if (redirectPage.equalsIgnoreCase("user")) {
                String username = keyCardRepository.findOwner(keycard_id);
                return "redirect:/user" + "?benutzername=" + username + "&chosenKeycard=" + keycard_id;
            } else {
                return "redirect:/keycards_verwalten" + "?chosenKeycard=" + keycard_id;
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @PostMapping("/erinnerung_gesendet")
    public String sendErinnerungsEmail(Model model,
            @RequestParam("fromEmail") String fromEmail,
            @RequestParam("toEmail") String toEmail,
            @RequestParam("subject") String subject,
            @RequestParam("body") String body) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Verwaltungsmitarbeiter") || rolle.equals("Verwaltungsleitung")) {
                // emailSenderService.sendSimpleMail(toEmail, fromEmail, subject, body);
                return "erfolg";
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }

    @PostMapping("/updateOwner")
    public String updateOwner(Model model, @RequestParam("keycard_id") int keycardid,
            @RequestParam("neuerOwner") String neuerOwner) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            if (rolle.equals("Verwaltungsmitarbeiter") || rolle.equals("Verwaltungsleitung")) {
                String benutzername = keyCardRepository.findOwner(keycardid);
                keyCardRepository.updateOwner(neuerOwner, keycardid);
                String redirectFrom = "updateOwner";

                // SEND EMAIL
                String toMail = akteurRepository.findEmail(keyCardRepository.findOwner(keycardid));
                String fromMail = akteurRepository.findEmail(benutzername);
                String subject = "Änderung Keycard #" + keycardid;
                String emailNachricht = "Ihre Keycard #" + keycardid
                        + " wurde einem anderen Nutzer übertragen. Bitte geben Sie die Karte bei nächster Gelegenheit bei der Verwaltung ab, falls dies noch nicht passiert ist";
                emailSenderService.sendSimpleMail(toMail, fromMail, subject, emailNachricht);
                return "redirect:/user" + "?benutzername=" + benutzername + "&redirectFrom=" + redirectFrom;
            } else {
                return "denied_access";
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "exception";
        }

    }
    // FINAL ENDE

    @GetMapping("/antrag")
    public String getAntragPage(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String rolle = akteurRepository.findRolle(username);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            String dashboardlink = "/dashboard_" + rolle.toLowerCase();
            model.addAttribute("dashboardlink", dashboardlink);
            List keycards = keyCardRepository.listenurKeycards(username);
            model.addAttribute("keycards", keycards);
            List<String> gebaeude = facilityRepository.gebäude();
            model.addAttribute("gebaeude", gebaeude);
            List<String> bereiche = facilityRepository.bereiche();
            model.addAttribute("bereiche", bereiche);
            List<String> raeume = facilityRepository.räume();
            model.addAttribute("raeume", raeume);

            return "antrag";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @RequestMapping("/VMantragbearbeiten")
    public String getVMAntragBearbeitenPage(Model model,
            @RequestParam("antragID") int antragID) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String rolle = akteurRepository.findRolle(username);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            model.addAttribute("antragID", antragID);
            List informationen = antraegeRepository.antragsInformationen(antragID);
            model.addAttribute("informationen", informationen);
            List vollerName = antraegeRepository.vollerName(antragID);
            model.addAttribute("vollerName", vollerName);
            String akteurName = antraegeRepository.benutzername(antragID);
            List akteurKeycards = keyCardRepository.listenurKeycards(akteurName);
            model.addAttribute("akteurKeycards", akteurKeycards);
            String akteurMail = akteurRepository.findEmail(akteurName);
            model.addAttribute("akteurMail", akteurMail);
            String eigeneMail = akteurRepository.findEmail(username);
            model.addAttribute("eigeneMail", eigeneMail);
            antraegeRepository.updateStatus("In_Bearbeitung", antragID);
            antraegeRepository.updateBearbeiter(vorname, antragID);
            Antrag answer = antraegeRepository.finden(antragID);
            if (answer != null) {
                model.addAttribute("answer", answer);
            }
            List beantragteRäume = antraegeRepository.getBeantragtFuerRäume(antragID);
            if (!beantragteRäume.isEmpty()) {
                model.addAttribute("beantragteRäume", beantragteRäume);
            }
            List beantragteBereiche = antraegeRepository.getBeantragtFuerBereiche(antragID);
            if (!beantragteBereiche.isEmpty()) {
                model.addAttribute("beantragteBereiche", beantragteBereiche);
            }

            return "VMantragbearbeiten";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @RequestMapping("/VLantragbearbeiten")
    public String getVLAntragBearbeitenPage(Model model,
            @RequestParam("antragID") int antragID) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String rolle = akteurRepository.findRolle(username);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            model.addAttribute("antragID", antragID);
            List vollerName = antraegeRepository.vollerName(antragID);
            model.addAttribute("vollerName", vollerName);
            String akteurName = antraegeRepository.benutzername(antragID);
            List akteurKeycards = keyCardRepository.listenurKeycards(akteurName);
            model.addAttribute("akteurKeycards", akteurKeycards);
            String akteurMail = akteurRepository.findEmail(akteurName);
            model.addAttribute("akteurMail", akteurMail);
            String eigeneMail = akteurRepository.findEmail(username);
            model.addAttribute("eigeneMail", eigeneMail);
            antraegeRepository.updateStatus("In_Bearbeitung", antragID);
            antraegeRepository.updateBearbeiter(vorname, antragID);
            Antrag answer = antraegeRepository.finden(antragID);
            if (answer != null) {
                model.addAttribute("answer", answer);
            }
            List beantragteRäume = antraegeRepository.getBeantragtFuerRäume(antragID);
            if (!beantragteRäume.isEmpty()) {
                model.addAttribute("beantragteRäume", beantragteRäume);
            }
            List beantragteBereiche = antraegeRepository.getBeantragtFuerBereiche(antragID);
            if (!beantragteBereiche.isEmpty()) {
                model.addAttribute("beantragteBereiche", beantragteBereiche);
            }

            return "VLantragbearbeiten";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @RequestMapping("/antragEinsicht")
    public String getantragEinsicht(Model model,
            @RequestParam("antragID") int antragID) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String rolle = akteurRepository.findRolle(username);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            model.addAttribute("antragID", antragID);
            List vollerName = antraegeRepository.vollerName(antragID);
            model.addAttribute("vollerName", vollerName);
            String akteurName = antraegeRepository.benutzername(antragID);
            List akteurKeycards = keyCardRepository.listenurKeycards(akteurName);
            model.addAttribute("akteurKeycards", akteurKeycards);
            String akteurMail = akteurRepository.findEmail(akteurName);
            model.addAttribute("akteurMail", akteurMail);
            String eigeneMail = akteurRepository.findEmail(username);
            model.addAttribute("eigeneMail", eigeneMail);
            Antrag answer = antraegeRepository.finden(antragID);
            if (answer != null) {
                model.addAttribute("answer", answer);
            }
            List beantragteRäume = antraegeRepository.getBeantragtFuerRäume(antragID);
            if (!beantragteRäume.isEmpty()) {
                model.addAttribute("beantragteRäume", beantragteRäume);
            }
            List beantragteBereiche = antraegeRepository.getBeantragtFuerBereiche(antragID);
            if (!beantragteBereiche.isEmpty()) {
                model.addAttribute("beantragteBereiche", beantragteBereiche);
            }

            return "antragEinsicht";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @RequestMapping("/updateSTATUS")
    public String updateSTATUS(Model model, @RequestParam("status") String status,
            @RequestParam("antragID") int antragID) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            String dashboardlink = "/dashboard_" + rolle.toLowerCase();
            antraegeRepository.updateStatus(status, antragID);

            return "redirect:" + dashboardlink;

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @RequestMapping("/finishAntrag")
    public String finalStatus(Model model, @RequestParam("status") String status,
            @RequestParam("antragID") int antragID, @RequestParam("toEmail") String toEmail) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            String dashboardlink = "/dashboard_" + rolle.toLowerCase();
            String fromEmail = akteurRepository.findEmail(username);

            Antrag bearbeitet = antraegeRepository.finden(antragID);

            List<Integer> beantragtFuersRaum = antraegeRepository.getBeantragtFuersRaum(antragID).stream()
                    .map(Integer::parseInt).toList();
            List<Integer> beantragtFuersBereich = antraegeRepository.getBeantragtFuersBereich(antragID).stream()
                    .map(Integer::parseInt).toList();

            if (bearbeitet.getAntragsart().equalsIgnoreCase("BESTEHEND")) {
                int keycardId = bearbeitet.getKeycard_id();

                if (status.equalsIgnoreCase("Abgelehnt")) {

                    antraegeRepository.updateStatus("Abgelehnt", antragID);
                    archivAntrag(antragID, "Abgelehnt");
                    antraegeRepository.deleteAntrag(antragID);
                    antraegeRepository.deleteBeantragtFuer(antragID);

                } else if (status.equalsIgnoreCase("Angenommen")) {

                    List<Integer> zugangrechtsRaum = keyCardRepository.berechtigungenRaumString(keycardId).stream()
                            .map(Integer::parseInt).collect(Collectors.toList());
                    List<Integer> differencesRaum = antraegeRepository.getBeantragtFuersRaum(antragID).stream()
                            .map(Integer::parseInt).collect(Collectors.toList());
                    differencesRaum.removeAll(zugangrechtsRaum);
                    List<Integer> zugangrechtsBereich = keyCardRepository.berechtigungenRaumString(keycardId).stream()
                            .map(Integer::parseInt).collect(Collectors.toList());
                    List<Integer> differencesBereich = antraegeRepository.getBeantragtFuersRaum(antragID).stream()
                            .map(Integer::parseInt).collect(Collectors.toList());
                    differencesBereich.removeAll(zugangrechtsBereich);
                    for (int raumId : differencesRaum) {
                        int zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                        keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId, raumId, null);
                    }
                    for (int bereichId : differencesBereich) {
                        int zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                        keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId, null, bereichId);

                    }
                    int logId = logsRepository.hoechsteKeycardLogID();
                    logsRepository.keycardlogHinzufuegen(logId + 1, keycardId, username,
                            "Rechte_Änderung",
                            Timestamp.valueOf(LocalDateTime.now()));
                    antraegeRepository.updateStatus("Angenommen", antragID);
                    archivAntrag(antragID, "Angenommen");
                    antraegeRepository.deleteAntrag(antragID);
                    antraegeRepository.deleteBeantragtFuer(antragID);

                }
            } else if (bearbeitet.getAntragsart().equalsIgnoreCase("NEU")) {

                if (status.equalsIgnoreCase("Abgelehnt")) {

                    antraegeRepository.updateStatus("Abgelehnt", antragID);
                    archivAntrag(antragID, "Abgelehnt");
                    antraegeRepository.deleteAntrag(antragID);
                    antraegeRepository.deleteBeantragtFuer(antragID);

                } else if (status.equalsIgnoreCase("Angenommen")) {

                    int keycardId = keyCardRepository.hoechsteID() + 1;
                    keyCardRepository.keycardHinzufuegen(keycardId, bearbeitet.getBenutzername(),
                            bearbeitet.getAnfangstag(), bearbeitet.getEndtag(), "Aktiviert");

                    int keycardLogId = logsRepository.hoechsteKeycardLogID() + 1;
                    logsRepository.keycardlogHinzufuegen(keycardLogId, keycardId, username,
                            "Erstellung", Timestamp.valueOf(LocalDateTime.now()));
                    for (int raumId : beantragtFuersRaum) {
                        int zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                        keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId, raumId, null);
                    }
                    for (int bereichId : beantragtFuersBereich) {
                        int zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                        keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId, null, bereichId);

                    }
                    antraegeRepository.updateStatus("Angenommen", antragID);
                    archivAntrag(antragID, "Angenommen");
                    antraegeRepository.deleteAntrag(antragID);
                    antraegeRepository.deleteBeantragtFuer(antragID);

                }
            }

            if (status.equals("Angenommen")) {
                Email angenommenMail = new Email(toEmail, fromEmail, "ANTRAG " + antragID +
                        " angenommen", "Ihr Antrag wurde angenommen");
                emailSenderService.sendSimpleMail(angenommenMail.getToEmail(),
                        angenommenMail.getFromEmail(),
                        angenommenMail.getSubject(), angenommenMail.getBody());
            }

            if (status.equals("Abgelehnt")) {
                Email abgelehntMail = new Email(toEmail, fromEmail, "ANTRAG " + antragID +
                        " abgelehnt", "Ihr Antrag wurde abgelehnt");
                emailSenderService.sendSimpleMail(abgelehntMail.getToEmail(),
                        abgelehntMail.getFromEmail(),
                        abgelehntMail.getSubject(), abgelehntMail.getBody());
            }

            if (status.equals("Nicht_bearbeitet")) {
                antraegeRepository.updateStatus(status, antragID);
                antraegeRepository.updateBearbeiter("---", antragID);
            }

            return "redirect:" + dashboardlink;

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @RequestMapping("/finishBerechtigungen")
    public String finishBerechtigungen(Model model, @RequestParam("antragID") int antragID,
            @RequestParam("berechtigungen") String berechtigungen) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            String dashboardlink = "/dashboard_" + rolle.toLowerCase();

            List<String> berechtiungsListe = new ArrayList<String>(Arrays.asList(berechtigungen.split(",")));
            List<Integer> beantragtFuersRaum = berechtiungsListe.stream().map(Integer::parseInt).toList();
            List<Integer> beantragtFuersBereich = berechtiungsListe.stream().map(Integer::parseInt).toList();
            Antrag bearbeitet = antraegeRepository.finden(antragID);

            if (antraegeRepository.getAntragart(antragID).equalsIgnoreCase("NEU")) {
                int keycardId = keyCardRepository.hoechsteID() + 1;
                keyCardRepository.keycardHinzufuegen(keycardId, bearbeitet.getBenutzername(),
                        bearbeitet.getAnfangstag(), bearbeitet.getEndtag(), "Aktiviert");

                int keycardLogId = logsRepository.hoechsteKeycardLogID();
                logsRepository.keycardlogHinzufuegen(keycardLogId + 1, keycardId, username,
                        "Erstellung", Timestamp.valueOf(LocalDateTime.now()));

                for (int raumId : beantragtFuersRaum) {
                    int zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                    keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId, raumId, null);
                }
                for (int bereichId : beantragtFuersBereich) {
                    int zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                    keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId, null, bereichId);
                }

                antraegeRepository.updateStatus("Angenommen", antragID);
                archivAntrag(antragID, "Angenommen");
                antraegeRepository.deleteAntrag(antragID);
                antraegeRepository.deleteBeantragtFuer(antragID);

            } else if (antraegeRepository.getAntragart(antragID).equalsIgnoreCase("BESTEHEND")) {

                int keycardId = bearbeitet.getKeycard_id();
                List<Integer> zugangrechtsRaum = keyCardRepository.berechtigungenRaumString(keycardId).stream()
                        .map(Integer::parseInt).collect(Collectors.toList());
                List<Integer> differencesRaum = antraegeRepository.getBeantragtFuersRaum(antragID).stream()
                        .map(Integer::parseInt).collect(Collectors.toList());
                differencesRaum.removeAll(zugangrechtsRaum);
                List<Integer> zugangrechtsBereich = keyCardRepository.berechtigungenRaumString(keycardId).stream()
                        .map(Integer::parseInt).collect(Collectors.toList());
                List<Integer> differencesBereich = antraegeRepository.getBeantragtFuersRaum(antragID).stream()
                        .map(Integer::parseInt).collect(Collectors.toList());
                differencesBereich.removeAll(zugangrechtsBereich);
                for (int raumId : differencesRaum) {
                    int zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                    keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId, raumId, null);
                }
                for (int bereichId : differencesBereich) {
                    int zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                    keyCardRepository.hatZugangHinzufuegen(zugangId, keycardId, null, bereichId);

                }
                int logId = logsRepository.hoechsteKeycardLogID();
                logsRepository.keycardlogHinzufuegen(logId + 1, keycardId, username,
                        "Rechte_Änderung",
                        Timestamp.valueOf(LocalDateTime.now()));

                antraegeRepository.updateStatus("Angenommen", antragID);
                archivAntrag(antragID, "Angenommen");
                antraegeRepository.deleteAntrag(antragID);
                antraegeRepository.deleteBeantragtFuer(antragID);
            }

            String toEmail = akteurRepository.findEmail(antraegeRepository.benutzername(antragID));
            String fromEmail = akteurRepository.findEmail(username);
            emailSenderService.sendSimpleMail(toEmail, fromEmail, "Antrag " + antragID,
                    berechtigungen + " wurden akzeptiert");

            return "redirect:" + dashboardlink;

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    public void archivAntrag(int antragId, String status) {
        Antrag antrag = antraegeRepository.finden(antragId);
        Integer keycardId = 0;
        String bearbeiter = SecurityContextHolder.getContext().getAuthentication().getName();
        // String dateiname = antraegeRepository.getDateiname(antragId);
        byte[] datei = antraegeRepository.getDatei(antragId);
        String raeume = "";
        String bereiche = "";
        List<String> beantragtFuersRaum = antraegeRepository.getBeantragtFuersRaum(antragId);
        for (String s : beantragtFuersRaum) {
            raeume += tuerRepository.getRaumname(Integer.parseInt(s)) + ";";
        }
        List<String> beantragtFuersBereich = antraegeRepository.getBeantragtFuersBereich(antragId);
        for (String s : beantragtFuersBereich) {
            bereiche += tuerRepository.getBereichname(Integer.parseInt(s)) + ";";
        }

        if (antrag.getAntragsart().equalsIgnoreCase("BESTEHEND")) {
            keycardId = antrag.getKeycard_id();
        }

        antraegeRepository.insertArchivAntrag(antragId, antrag.getBenutzername(), keycardId,
                antrag.getBearbeitungsstelle(),
                bearbeiter,
                antrag.getErstellungszeitpunkt(), status, datei,
                antrag.getAnfangstag(), antrag.getEndtag(), bereiche, raeume);

        // warum wird hier id nicht gespeichert im archiv?
        if (antrag.getAntragsart().equalsIgnoreCase("NEU")) {
            antraegeRepository.setNullKeycardIdArchivAntrag(antragId);
        }

        if (datei == null) {
            // antraegeRepository.setNullDateinameArchivAntrag(antragId);
            antraegeRepository.setNullDateiArchivAntrag(antragId);
        }
    }

    @RequestMapping("/nachrichtsenden")
    public String getNachrichtSendenPage(Model model,
            @RequestParam("toEmail") String toEmail,
            @RequestParam("antragID") int antragID) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String fromEmail = akteurRepository.findEmail(username);
            model.addAttribute("fromEmail", fromEmail);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            model.addAttribute("toEmail", toEmail);
            model.addAttribute("antragID", antragID);

            return "nachrichtsenden";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @PostMapping("/nachrichtsenden")
    public String sendEmail(Model model, Email email) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String rolle = akteurRepository.findRolle(username);
            String dashboardlink = "/dashboard_" + rolle.toLowerCase();

            emailSenderService.sendSimpleMail(email.getToEmail(), email.getFromEmail(), email.getSubject(),
                    email.getBody());

            return "redirect:" + dashboardlink;

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/archiv")
    public String getArchivPage(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String rolle = akteurRepository.findRolle(username);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            String dashboardlink = "/dashboard_" + rolle.toLowerCase();
            model.addAttribute("dashboardlink", dashboardlink);
            List antraegeArchiv = logsRepository.antraegeArchiv();
            model.addAttribute("antraegeArchiv", antraegeArchiv);
            List akteureArchiv = logsRepository.akteureArchiv();
            model.addAttribute("akteureArchiv", akteureArchiv);

            return "archiv";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/kartenbearbeiten")
    public String getKArteAusstellenPage(Model model,
            @RequestParam(name = "keycardid", required = false) Integer keycardid) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            model.addAttribute("username", username);
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String rolle = akteurRepository.findRolle(username);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            String dashboardlink = "/dashboard_" + rolle.toLowerCase();
            model.addAttribute("dashboardlink", dashboardlink);
            int konto_id = akteurRepository.findKontoID(username);
            model.addAttribute("konto_id", konto_id);
            List keycards = keyCardRepository.listenurKeycards(username);
            model.addAttribute("keycards", keycards);
            List alleTüren = facilityRepository.alleTüren();
            model.addAttribute("alleTueren", alleTüren);

            if (keycardid != null) {
                String anfangstag = keyCardRepository.findanfangstag(keycardid);
                model.addAttribute("anfangstag", anfangstag);
                String endtag = keyCardRepository.findendtag(keycardid);
                model.addAttribute("endtag", endtag);
                String status = keyCardRepository.findstatus(keycardid);
                model.addAttribute("status", status);
                List berechtigungenRaum = keyCardRepository.berechtigungenRaum(keycardid);
                List berechtigungenBereich = keyCardRepository.berechtigungenBereich(keycardid);
                model.addAttribute("berechtigungenRaum", berechtigungenRaum);
                model.addAttribute("berechtigungenBereich", berechtigungenBereich);
            }

            return "kartenbearbeiten";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @RequestMapping("/neueKarte")
    public String neueKarte(@RequestParam("benutzername") String benutzername,
            @RequestParam("anfangstag") Date anfangstag, @RequestParam("endtag") Date endtag,
            @RequestParam("status") String status) {

        try {
            int keycardID = keyCardRepository.hoechsteID();
            int logId = logsRepository.hoechsteKeycardLogID();
            keyCardRepository.keycardHinzufuegen(keycardID + 1, benutzername, anfangstag, endtag, status);

            String bearbeiter = SecurityContextHolder.getContext().getAuthentication().getName();
            logsRepository.keycardlogHinzufuegen(logId + 1, keycardID + 1, bearbeiter, "Erstellung",
                    Timestamp.valueOf(LocalDateTime.now()));

            return "redirect:/kartenbearbeiten";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @RequestMapping("/bestehendeKarte")
    public String bestehendeKarte(@RequestParam("keycardid") int keycardid,
            @RequestParam("anfangstag") Date anfangstag,
            @RequestParam("endtag") Date endtag,
            @RequestParam("status") String status,
            @RequestParam("berechtigungenRaum") List<String> berechtigungenRaum,
            @RequestParam("berechtigungenBereich") List<String> berechtigungenBereich) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            keyCardRepository.update(username, anfangstag, endtag, status, keycardid);
            int logId = logsRepository.hoechsteKeycardLogID();
            String bearbeiter = SecurityContextHolder.getContext().getAuthentication().getName();
            String logStatus = "";
            if (status.equalsIgnoreCase("Aktiviert")) {
                logStatus = "Aktivierung";
            } else if (status.equalsIgnoreCase("Deaktiviert")) {
                logStatus = "Deaktivierung";
            } else {
                logStatus = "Sperrung";
            }
            logsRepository.keycardlogHinzufuegen(logId + 1, keycardid, bearbeiter, logStatus,
                    Timestamp.valueOf(LocalDateTime.now()));

            List<Integer> neueBerechtigungenRaum = berechtigungenRaum.stream().map(Integer::parseInt).toList();
            List<Integer> alteBerechtigungenRaum = keyCardRepository.berechtigungenRaum(keycardid);
            List<Integer> differencesRaum = new ArrayList<>(neueBerechtigungenRaum);
            differencesRaum.removeAll(alteBerechtigungenRaum);
            if (differencesRaum.size() != 0) {
                keyCardRepository.deleteAlleZugangrechts(keycardid);
                for (String r : berechtigungenRaum) {
                    int raumId = Integer.parseInt(r);
                    int zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                    keyCardRepository.hatZugangHinzufuegen(zugangId, keycardid, raumId, null);
                }
            }
            List<Integer> neueBerechtigungenBereich = berechtigungenBereich.stream().map(Integer::parseInt).toList();
            List<Integer> alteBerechtigungenBereich = keyCardRepository.berechtigungenBereich(keycardid);
            List<Integer> differencesBereich = new ArrayList<>(neueBerechtigungenBereich);
            differencesBereich.removeAll(alteBerechtigungenBereich);
            if (differencesBereich.size() != 0) {
                keyCardRepository.deleteAlleZugangrechts(keycardid);
                for (String b : berechtigungenBereich) {
                    int bereichId = Integer.parseInt(b);
                    int zugangId = keyCardRepository.hoechsteIDzugang() + 1;
                    keyCardRepository.hatZugangHinzufuegen(zugangId, keycardid, null, bereichId);
                }
                int logID = logsRepository.hoechsteKeycardLogID();
                logsRepository.keycardlogHinzufuegen(logID + 1, keycardid, username, "Rechte_Änderung",
                        Timestamp.valueOf(LocalDateTime.now()));
            }

            return "redirect:/kartenbearbeiten";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/verlustmelden")
    public String verlustMelden(@RequestParam("keycardid") int keycardid) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            keyCardRepository.updateStatus("Verloren", keycardid);
            int logId = logsRepository.hoechsteKeycardLogID();
            logsRepository.keycardlogHinzufuegen(logId + 1, keycardid, username, "Sperrung",
                    Timestamp.valueOf(LocalDateTime.now()));

            String toMail = akteurRepository.findEmail(keyCardRepository.findOwner(keycardid));
            String fromMail = akteurRepository.findEmail(username);
            String subject = "Verlust der Karte " + keycardid;
            String body = "Die Karte " + keycardid + " wurde gesperrt";

            Email verlustmail = new Email(toMail, fromMail, subject, body);

            emailSenderService.sendSimpleMail(verlustmail.getToEmail(),
                    verlustmail.getFromEmail(),
                    verlustmail.getSubject(),
                    verlustmail.getBody());

            return "redirect:/cards";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/diagramme")
    public String getChartsPage(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String rolle = akteurRepository.findRolle(username);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            String dashboardlink = "/dashboard_" + rolle.toLowerCase();
            model.addAttribute("dashboardlink", dashboardlink);

            // machen

            return "diagramme";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/gebaeudeuebersicht")
    public String getMapsPage(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String rolle = akteurRepository.findRolle(username);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            String dashboardlink = "/dashboard_" + rolle.toLowerCase();
            model.addAttribute("dashboardlink", dashboardlink);
            List<String> gebaeude = facilityRepository.gebäude();
            model.addAttribute("gebaeude", gebaeude);
            List<String> raeume = facilityRepository.räume();
            model.addAttribute("raeume", raeume);

            return "gebaeudeuebersicht";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/cards")
    public String get3DkeyPage(Model model, @RequestParam(name = "keycardid", required = false) Integer keycardid) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List keycards = keyCardRepository.listeKeycards(username);
            model.addAttribute("keycards", keycards);
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String rolle = akteurRepository.findRolle(username);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            String dashboardlink = "/dashboard_" + rolle.toLowerCase();
            model.addAttribute("dashboardlink", dashboardlink);
            if (keycardid != null) {
                // TODO
                // List berechtigungen = keyCardRepository.berechtigungen(keycardid);
                // model.addAttribute("berechtigungen", berechtigungen);
            }
            return "cards";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/logs")
    public String getLogsPage(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            model.addAttribute("username", username);
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String rolle = akteurRepository.findRolle(username);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            String dashboardlink = "/dashboard_" + rolle.toLowerCase();
            model.addAttribute("dashboardlink", dashboardlink);
            List tuerlogs = logsRepository.tuerlogs();
            model.addAttribute("tuerlogs", tuerlogs);
            List keycardlogs = logsRepository.keycardlogs();
            model.addAttribute("keycardlogs", keycardlogs);
            List persoenlicheTuerlogs = logsRepository.persoenlicheTuerLogs(username);
            model.addAttribute("persoenlicheTuerlogs", persoenlicheTuerlogs);
            List persoenlicheKeycardLogs = logsRepository.persoenlicheKeycardLogs(username);
            model.addAttribute("persoenlicheKeycardLogs", persoenlicheKeycardLogs);

            return "logs";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/testzentrum")
    public String getSimulationPage(Model model,
            @RequestParam(name = "keycardid", required = false) Integer keycardid) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List keycards = keyCardRepository.listenurKeycards(username);
            model.addAttribute("keycards", keycards);
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String rolle = akteurRepository.findRolle(username);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            if (keycardid != null) {
                String status = keyCardRepository.findstatus(keycardid);
                model.addAttribute("status", status);
                List berechtigungen = keyCardRepository.berechtigungen(keycardid);
                model.addAttribute("berechtigungen", berechtigungen);
                String anfangstag = keyCardRepository.findanfangstag(keycardid);
                model.addAttribute("anfangstag", anfangstag);
                String endtag = keyCardRepository.findendtag(keycardid);
                model.addAttribute("endtag", endtag);
            }
            model.addAttribute("keycardid", keycardid);

            String dashboardlink = "/dashboard_" + rolle.toLowerCase();
            model.addAttribute("dashboardlink", dashboardlink);
            int kontoid = akteurRepository.findKontoID(username);
            model.addAttribute("kontoid", kontoid);
            List<String> tueren = facilityRepository.alleTürenRäume();
            model.addAttribute("tueren", tueren);

            return "testzentrum";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/game")
    public String getGamePage(Model model) {
        return "game";
    }

    @GetMapping("/kontouebersicht")
    public String getKontoUebersichtPage(Model model, @RequestParam("kontoid") int konto_id) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);

            Konto akteur = akteurRepository.findeKonto(konto_id);
            model.addAttribute("akteur", akteur);
            List akteurkeycards = keyCardRepository.listenurKeycards(akteur.getBenutzername());
            model.addAttribute("akteurkeycards", akteurkeycards);
            String akteurlink = "profilePictures/" + akteur.getBenutzername() + ".jpg";
            model.addAttribute("akteurlink", akteurlink);
            List akteurNamen = akteurRepository.namenAuflisten();
            model.addAttribute("akteurNamen", akteurNamen);
            List<String> status = new ArrayList();
            List<List<String>> listOfLists = new ArrayList<>();
            if (!akteurkeycards.isEmpty()) {
                for (int i = 0; i < akteurkeycards.size(); i++) {
                    // TODO
                    // listOfLists.add(keyCardRepository.berechtigungen(Integer.parseInt(akteurkeycards.get(i).toString())));
                    // status.add(keyCardRepository.findstatus((Integer) akteurkeycards.get(i)));
                }
            }
            model.addAttribute("listOfLists", listOfLists);
            model.addAttribute("status", status);

            List<String> alleTueren = facilityRepository.alleTüren();
            model.addAttribute("alleTueren", alleTueren);

            return "kontouebersicht";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/zugriffsuebersicht")
    public String getZugriffsUebersichtPage(Model model, @RequestParam("tuerid") int tuerid) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            model.addAttribute("tuerid", tuerid);

            List zugriffsnamen = facilityRepository.benutzernamenZuTür(tuerid);
            model.addAttribute("zugriffsnamen", zugriffsnamen);

            return "zugriffsuebersicht";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @RequestMapping("/updateOwner")
    public String neuerOwner(Model model, @RequestParam("keycardid") int keycardid,
            @RequestParam("neuerOwner") String neuerOwner) {

        try {
            keyCardRepository.updateOwner(neuerOwner, keycardid);

            return "redirect:/suchen";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @RequestMapping("/updateRights")
    public String updateRights(Model model,
            @RequestParam(value = "keycardID", required = false) String keycardID,
            @RequestParam(value = "rechte", required = false) String rechte,
            @RequestParam(value = "status", required = false) String status) {

        try {
            int keycardId = Integer.parseInt(keycardID);
            int tuerId = Integer.parseInt(rechte);
            int logId = logsRepository.hoechsteKeycardLogID();
            String benutzername = SecurityContextHolder.getContext().getAuthentication().getName();
            if (rechte.isEmpty()) {
                if (status.equalsIgnoreCase("aktiviert")) {
                    keyCardRepository.updateStatus("Aktiviert", keycardId);
                    logsRepository.keycardlogHinzufuegen(logId + 1, keycardId, benutzername, "Aktivierung",
                            Timestamp.valueOf(LocalDateTime.now()));
                } else if (status.equalsIgnoreCase("deaktiviert")) {
                    keyCardRepository.updateStatus("Deaktiviert", keycardId);
                    logsRepository.keycardlogHinzufuegen(logId + 1, keycardId, benutzername, "Deaktivierung",
                            Timestamp.valueOf(LocalDateTime.now()));
                } else if (status.equalsIgnoreCase("gesperrt")) {
                    keyCardRepository.updateStatus("Gesperrt", keycardId);
                    logsRepository.keycardlogHinzufuegen(logId + 1, keycardId, benutzername, "Sperrung",
                            Timestamp.valueOf(LocalDateTime.now()));
                }
            } else {
                /*
                 * List<Integer> zugangsrechts =
                 * keyCardRepository.berechtigungenString(keycardId).stream().map(Integer::
                 * parseInt).collect(Collectors.toList());
                 * if (zugangsrechts.contains(tuerId)) {
                 * keyCardRepository.deleteZugangrecht(keycardId, tuerId);
                 * logsRepository.keycardlogHinzufuegen(logId + 1, keycardId, benutzername,
                 * "Rechte_Änderung",
                 * Timestamp.valueOf(LocalDateTime.now()));
                 * 
                 * } else {
                 * keyCardRepository.hatZugangHinzufuegen(keycardId, tuerId);
                 * logsRepository.keycardlogHinzufuegen(logId + 1, keycardId, benutzername,
                 * "Rechte_Änderung",
                 * Timestamp.valueOf(LocalDateTime.now()));
                 * }
                 */
            }

            return "redirect:/suchen";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @RequestMapping("/deleteCard")
    public String deleteCard(Model model, @RequestParam(value = "keycardID", required = false) String keycardID) {

        try {
            // machen
            // SQL Abfrage
            int keycardId = Integer.parseInt(keycardID);
            keyCardRepository.deleteKeycard(keycardId);
            keyCardRepository.deleteAlleZugangrechts(keycardId);
            // String benutzername =
            // SecurityContextHolder.getContext().getAuthentication().getName();
            // logsRepository.keycardlogHinzufuegen(logsRepository.hoechsteKeycardLogID() +
            // 1,
            // keycardId, benutzername, "Deletierung",
            // Timestamp.valueOf(LocalDateTime.now()));

            return "redirect:/suchen";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @RequestMapping("/deleteOwner")
    public String deleteOwner(Model model, @RequestParam("kontoid") int kontoid) {

        try {
            String benutzername = akteurRepository.findByBenutzername(kontoid);
            List<Integer> keycards = keyCardRepository.listenurKeycardsString(benutzername)
                    .stream().map(Integer::parseInt).toList();

            for (Integer k : keycards) {
                int keycardId = k.intValue();
                keyCardRepository.deleteAlleZugangrechts(k);
                keyCardRepository.deleteKeycard(keycardId);
            }
            akteurRepository.akteurInsArchiv(kontoid);
            akteurRepository.deleteAkteur(kontoid);

            return "redirect:/suchen";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @RequestMapping("/tuerlog")
    public void logger(@RequestParam("konto_id") int konto_id, @RequestParam("keycard_id") int keycard_id,
            @RequestParam("tür_id") int tür_id, @RequestParam("ergebnis") String ergebnis) {

        try {
            Date date = new Date(System.currentTimeMillis());
            Timestamp ts = new Timestamp(date.getTime());
            logsRepository.tuerlogHinzufuegen(konto_id, keycard_id, tür_id, ergebnis, ts);
        } catch (Exception error) {
            System.out.println(error.getMessage());
        }
    }

    @GetMapping("/settings")
    public String getSettingsPage(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String rolle = akteurRepository.findRolle(username);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            String dashboardlink = "/dashboard_" + rolle.toLowerCase();
            model.addAttribute("dashboardlink", dashboardlink);

            return "settings";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/dashboard_nutzer")
    public String getNutzerDashboardPage(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            model.addAttribute("benutzername", username);
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            String rolle = akteurRepository.findRolle(username);
            model.addAttribute("rolle", rolle);
            int nutzerAnzahl = akteurRepository.anzahlAkteure("Nutzer");
            model.addAttribute("nutzerAnzahl", nutzerAnzahl);
            int mitarbeiterAnzahl = akteurRepository.anzahlAkteure("Verwaltungsmitarbeiter");
            model.addAttribute("mitarbeiterAnzahl", mitarbeiterAnzahl);
            int leiterAnzahl = akteurRepository.anzahlAkteure("Verwaltungsleitung");
            model.addAttribute("leiterAnzahl", leiterAnzahl);
            int adminAnzahl = akteurRepository.anzahlAkteure("Administrator");
            model.addAttribute("adminAnzahl", adminAnzahl);
            int gesamtAnzahl = nutzerAnzahl + mitarbeiterAnzahl + leiterAnzahl + adminAnzahl;
            model.addAttribute("gesamtAnzahl", gesamtAnzahl);
            List antraege = antraegeRepository.listAntraege(username);
            model.addAttribute("antraege", antraege);

            return "dashboard_nutzer";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/dashboard_verwaltungsmitarbeiter")
    public String getMitarbeiterDashboardPage(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            model.addAttribute("benutzername", username);
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            String rolle = akteurRepository.findRolle(username);
            model.addAttribute("rolle", rolle);
            String email = akteurRepository.findEmail(username);
            model.addAttribute("email", email);
            int nutzerAnzahl = akteurRepository.anzahlAkteure("Nutzer");
            model.addAttribute("nutzerAnzahl", nutzerAnzahl);
            int mitarbeiterAnzahl = akteurRepository.anzahlAkteure("Verwaltungsmitarbeiter");
            model.addAttribute("mitarbeiterAnzahl", mitarbeiterAnzahl);
            int leiterAnzahl = akteurRepository.anzahlAkteure("Verwaltungsleitung");
            model.addAttribute("leiterAnzahl", leiterAnzahl);
            int adminAnzahl = akteurRepository.anzahlAkteure("Administrator");
            model.addAttribute("adminAnzahl", adminAnzahl);
            int gesamtAnzahl = nutzerAnzahl + mitarbeiterAnzahl + leiterAnzahl + adminAnzahl;
            model.addAttribute("gesamtAnzahl", gesamtAnzahl);
            List antraege = antraegeRepository.listAntraege(username);
            model.addAttribute("antraege", antraege);
            List nutzerAntraege = antraegeRepository.listBearbeitendeAntraege("Verwaltungsmitarbeiter");
            model.addAttribute("nutzerAntraege", nutzerAntraege);

            return "dashboard_verwaltungsmitarbeiter";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/dashboard_verwaltungsleitung")
    public String getLeitungDashboardPage(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            model.addAttribute("benutzername", username);
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            String rolle = akteurRepository.findRolle(username);
            model.addAttribute("rolle", rolle);
            String email = akteurRepository.findEmail(username);
            model.addAttribute("email", email);
            int nutzerAnzahl = akteurRepository.anzahlAkteure("Nutzer");
            model.addAttribute("nutzerAnzahl", nutzerAnzahl);
            int mitarbeiterAnzahl = akteurRepository.anzahlAkteure("Verwaltungsmitarbeiter");
            model.addAttribute("mitarbeiterAnzahl", mitarbeiterAnzahl);
            int leiterAnzahl = akteurRepository.anzahlAkteure("Verwaltungsleitung");
            model.addAttribute("leiterAnzahl", leiterAnzahl);
            int adminAnzahl = akteurRepository.anzahlAkteure("Administrator");
            model.addAttribute("adminAnzahl", adminAnzahl);
            int gesamtAnzahl = nutzerAnzahl + mitarbeiterAnzahl + leiterAnzahl + adminAnzahl;
            model.addAttribute("gesamtAnzahl", gesamtAnzahl);
            List<String[]> akteurAntraege = antraegeRepository.listBearbeitendeAntraege("Verwaltungsleitung");
            model.addAttribute("akteurAntraege", akteurAntraege);
            List<String> ganzeNamen = new ArrayList<String>();

            for (int i = 0; i < akteurAntraege.size(); i++) {
                String[] wert = akteurAntraege.get(i);
                ganzeNamen.add(akteurRepository.findName(wert[1]));
            }

            return "dashboard_verwaltungsleitung";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @GetMapping("/dashboard_admin")
    public String getAdminDashboardPage(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            model.addAttribute("benutzername", username);
            String vorname = akteurRepository.findVorname(username);
            model.addAttribute("vorname", vorname);
            String link = "profilePictures/" + username + ".jpg";
            model.addAttribute("link", link);
            String rolle = akteurRepository.findRolle(username);
            model.addAttribute("rolle", rolle);
            int nutzerAnzahl = akteurRepository.anzahlAkteure("Nutzer");
            model.addAttribute("nutzerAnzahl", nutzerAnzahl);
            int mitarbeiterAnzahl = akteurRepository.anzahlAkteure("Verwaltungsmitarbeiter");
            model.addAttribute("mitarbeiterAnzahl", mitarbeiterAnzahl);
            int leiterAnzahl = akteurRepository.anzahlAkteure("Verwaltungsleitung");
            model.addAttribute("leiterAnzahl", leiterAnzahl);
            int adminAnzahl = akteurRepository.anzahlAkteure("Administrator");
            model.addAttribute("adminAnzahl", adminAnzahl);
            int gesamtAnzahl = nutzerAnzahl + mitarbeiterAnzahl + leiterAnzahl + adminAnzahl;
            model.addAttribute("gesamtAnzahl", gesamtAnzahl);
            List akteurAntraege = antraegeRepository.listAlleAntraege();
            model.addAttribute("akteurAntraege", akteurAntraege);
            List daten = akteurRepository.daten();
            model.addAttribute("daten", daten);
            List tuerzugaenge = logsRepository.erstellungszeitpunkte();
            model.addAttribute("tuerzugaenge", tuerzugaenge);

            return "dashboard_admin";

        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Paris") // immer um Mitternacht
    public void sendeAblaufBenachrichtigung() {

        try {
            // Keycards, die innerhalb der nächsten Woche ablaufen
            List<String> ablaufendeKartenOwner = keyCardRepository.akteureAblaufenderAdressen();
            String akteurEmail = "";
            String keycardID = "";

            for (String item : ablaufendeKartenOwner) {
                String[] trennen = item.split(",");
                for (int i = 0; i < trennen.length; i++) {
                    if (i == 0) {
                        akteurEmail = trennen[0];
                    }
                    if (i == 1) {
                        keycardID = trennen[1];
                    }
                }

                Email email = new Email(akteurEmail, "group7",
                        "Karte " + keycardID + " läuft in einer Woche ab", "Bitte erneuern Sie Ihre Karte");
                emailSenderService.sendSimpleMail(email.getToEmail(), email.getFromEmail(), email.getSubject(),
                        email.getBody());
            }

            // Keycards, die am aktuellen Tag ablaufen
            List<Keycard> abgelaufeneKeycards = keyCardRepository.abgelaufeneKeycards();
            for (Keycard item : abgelaufeneKeycards) {
                String email = akteurRepository.findEmail(item.getBenutzername());
                Integer abgelaufeneID = item.getId();

                Email beanchrichtigung = new Email(email, "group7",
                        "Karte " + abgelaufeneID + " ist heute abgelaufen", "Bitte erneuern Sie Ihre Karte");
                emailSenderService.sendSimpleMail(beanchrichtigung.getToEmail(), beanchrichtigung.getFromEmail(),
                        beanchrichtigung.getSubject(),
                        beanchrichtigung.getBody());

                keyCardRepository.updateStatus("Gesperrt", abgelaufeneID);
            }

        } catch (Exception error) {
            System.out.println(error.getMessage());
        }
    }

    @GetMapping("/karte")
    public String getGeäudeKarte(Model model) {
        try {
            List audimax = facilityRepository.alleRaeumeProFacility("Audimax");
            model.addAttribute("audimax", audimax);
            List fim = facilityRepository.alleRaeumeProFacility("FIM");
            model.addAttribute("fim", fim);
            List itz = facilityRepository.alleRaeumeProFacility("ITZ");
            model.addAttribute("itz", itz);
            List juridicum = facilityRepository.alleRaeumeProFacility("Juridicum");
            model.addAttribute("juridicum", juridicum);
            List philosophicum = facilityRepository.alleRaeumeProFacility("Philosophicum");
            model.addAttribute("philosophicum", philosophicum);
            List sportzentrum = facilityRepository.alleRaeumeProFacility("Sportzentrum");
            model.addAttribute("sportzentrum", sportzentrum);
            List wiwi = facilityRepository.alleRaeumeProFacility("WIWI");
            model.addAttribute("wiwi", wiwi);

            return "karte";
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }
}
