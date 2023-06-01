package com.group7.project.repository;

import com.group7.project.model.Konto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface AkteurRepository extends JpaRepository<Konto, Integer> {

        Optional<Konto> findByBenutzernameAndPasswort(String benutzername, String passwort);

        // personalabteilung.konto

        Konto findByBenutzername(String benutzername);

        @Query(value = "SELECT benutzername FROM personalabteilung.konten WHERE konto_id= ?1", nativeQuery = true)
        String findByBenutzername(int kontoId);

        @Query(value = "SELECT vorname, nachname FROM personalabteilung.konten WHERE benutzername = ?1", nativeQuery = true)
        String findName(String benutzername);

        @Query(value = "SELECT * FROM personalabteilung.konten WHERE rolle = ?1", nativeQuery = true)
        List<Konto> findByRole(String rolle);

        

        @Query(value = "SELECT benutzername FROM personalabteilung.konten WHERE rolle = ?1", nativeQuery = true)
        List<String> findUsernameByRole(String rolle);

        @Query(value = "SELECT benutzername FROM personalabteilung.konten", nativeQuery = true)
        List<String> findUsernames();

        @Query(value = "SELECT adresse FROM personalabteilung.konten WHERE benutzername = ?1", nativeQuery = true)
        String findAdresse(String benutzername);

        @Query(value = "SELECT vorname FROM personalabteilung.konten WHERE benutzername = ?1", nativeQuery = true)
        String findVorname(String benutzername);

        @Query(value = "SELECT nachname FROM personalabteilung.konten WHERE benutzername = ?1", nativeQuery = true)
        String findNachname(String benutzername);

        @Query(value = "SELECT rolle FROM personalabteilung.konten WHERE benutzername = ?1", nativeQuery = true)
        String findRolle(String benutzername);

        @Query(value = "SELECT geburtsdatum FROM personalabteilung.konten WHERE benutzername = ?1", nativeQuery = true)
        String findGeburtsdatum(String benutzername);

        @Query(value = "SELECT email FROM personalabteilung.konten WHERE benutzername = ?1", nativeQuery = true)
        String findEmail(String benutzername);

        @Query(value = "SELECT telefonnummer FROM personalabteilung.konten WHERE benutzername = ?1", nativeQuery = true)
        String findTelefonnummer(String benutzername);

        @Query(value = "SELECT arbeitsvertragsende FROM personalabteilung.konten WHERE benutzername = ?1", nativeQuery = true)
        String findArbeitsvertragsende(String benutzername);

        // @Query(value = "SELECT guthaben FROM personalabteilung.konten WHERE
        // benutzername = ?1", nativeQuery = true)
        // int getGuthaben(String benutzername);

        @Query(value = "SELECT count(benutzername) FROM personalabteilung.konten WHERE rolle = ?1", nativeQuery = true)
        int anzahlAkteure(String rolle);

        @Query(value = "SELECT YEAR(erstellungszeitpunkt) AS startjahr, MONTH(erstellungszeitpunkt) AS startmonat, DAY(erstellungszeitpunkt) AS startag FROM personalabteilung.konten", nativeQuery = true)
        List daten();

        @Query(value = "SELECT konto_id FROM personalabteilung.konten WHERE benutzername = ?1", nativeQuery = true)
        int findKontoID(String benutzername);

        @Query(value = "SELECT max(konto_id) FROM personalabteilung.konten", nativeQuery = true)
        int hoechsteID();

        @Query(value = "SELECT * FROM personalabteilung.konten WHERE konto_id = ?1", nativeQuery = true)
        Konto findeKonto(int konto_id);

        // alle Akteure auflisten

        @Query(value = "SELECT benutzername FROM personalabteilung.konten", nativeQuery = true)
        List namenAuflisten();

        @Query(value = "SELECT * FROM personalabteilung.konten WHERE benutzername LIKE %?1% OR vorname LIKE %?1% OR nachname LIKE %?1%", nativeQuery = true)
        List suchen(String suchWert);

        // Suchfunktion

        @Transactional
        @Query(value = "SELECT * FROM personalabteilung.konten WHERE (benutzername LIKE %?1% OR vorname LIKE %?1% OR nachname LIKE %?1%) AND (rolle = 'Verwaltungsmitarbeiter' or rolle = 'Nutzer')", nativeQuery = true)
        public List<Konto> search(String keyword);

        @Transactional
        @Query(value = "SELECT * FROM personalabteilung.konten WHERE (benutzername LIKE %?1% OR vorname LIKE %?1% OR nachname LIKE %?1%) AND rolle = 'Nutzer'", nativeQuery = true)
        public List<Konto> searchNutzer(String keyword);

        // Löschfunktion

        @Modifying
        @Transactional
        @Query(value = "DELETE FROM personalabteilung.konten WHERE konto_id = ?1", nativeQuery = true)
        public void deleteAkteur(int konto_id);

        // Einfügen

        @Transactional
        @Modifying
        @Query(value = "INSERT INTO personalabteilung.konten (konto_id, benutzername, passwort, rolle, erstellungszeitpunkt, vorname, nachname, geburtsdatum, telefonnummer, email, adresse, arbeitsvertragsende, erlaubt) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13)", nativeQuery = true)
        void akteurHinzufuegen(int konto_id, String benutzername, String passwort, String rolle,
                        Timestamp erstellungszeitpunkt, String vorname, String nachname, Date geburtsdatum,
                        String telefonnummer,
                        String email, String adresse, Date arbeitsvertragsende, int erlaubt);

        // ins Archiv verschieben

        @Transactional
        @Modifying
        @Query(value = "INSERT INTO key_management.archiv_akteure SELECT * FROM personalabteilung.konten WHERE konto_id=?1", nativeQuery = true)
        void akteurInsArchiv(int konto_id);

        // andere

        Boolean existsByBenutzername(String benutzername);

        Boolean existsByEmail(String email);

}
