package com.group7.project.repository;

import com.group7.project.model.Konto;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LogsRepository extends JpaRepository<Konto, Integer> {

        // key_management.log_keycards
        // key_management.log_türen

        @Query(value = "SELECT * FROM key_management.log_türen", nativeQuery = true)
        List tuerlogs();

        @Query(value = "SELECT * FROM key_management.log_türen WHERE erstellungszeitpunkt BETWEEN ?1 AND ?2", nativeQuery = true)
        List tuerlogsSearch(Timestamp start, Timestamp ende);

        @Query(value = "SELECT max(log_id) FROM key_management.log_türen;", nativeQuery = true)
        Integer hoechsteTürID();

        // Einfügen

        @Transactional
        @Modifying
        @Query(value = "INSERT INTO key_management.log_türen (log_id, keycard_id, tür_id, ergebnis, erstellungszeitpunkt) VALUES (?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
        void tuerlogHinzufuegen(int log_id, int keycard_id, int tür_id, String ergebnis,
                        Timestamp erstellungszeitpunkt);

        @Query(value = "SELECT * FROM key_management.log_keycards", nativeQuery = true)
        List keycardlogs();

        @Query(value = "SELECT * FROM key_management.log_keycards WHERE erstellungszeitpunkt BETWEEN ?1 AND ?2", nativeQuery = true)
        List keycardlogsSearch(Timestamp startdatum, Timestamp enddatum);

        @Query(value = "SELECT k.keycard_id, benutzername, anfangstag, endtag, status, log_id, tür_id, ergebnis, erstellungszeitpunkt FROM key_management.keycards k JOIN key_management.log_türen t ON k.keycard_id = t.keycard_id WHERE benutzername = ?1", nativeQuery = true)
        List persoenlicheTuerLogs(String benutzername);

        @Query(value = "SELECT k.keycard_id, benutzername, anfangstag, endtag, status, log_id, bearbeitername, typ, erstellungszeitpunkt FROM key_management.keycards k JOIN key_management.log_keycards lk ON k.keycard_id = lk.keycard_id WHERE benutzername = ?1", nativeQuery = true)
        List persoenlicheKeycardLogs(String benutzername);

        // Einfügen

        @Transactional
        @Modifying
        @Query(value = "INSERT INTO key_management.log_keycards (log_id, keycard_id, bearbeitername, typ, erstellungszeitpunkt) VALUES (?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
        void keycardlogHinzufuegen(int konto_id, int keycard_id, String bearbeitername, String typ,
                        Timestamp erstellungszeitpunkt);

        @Query(value = "Select MONTH(erstellungszeitpunkt) from key_management.log_türen WHERE ergebnis = 'Akzeptiert'", nativeQuery = true)
        List erstellungszeitpunkte();

        @Query(value = "Select MONTH(erstellungszeitpunkt) from key_management.log_türen", nativeQuery = true)
        List versuchteZugaenge();

        // archiv

        @Query(value = "SELECT * FROM key_management.archiv_anträge", nativeQuery = true)
        List antraegeArchiv();

        @Query(value = "SELECT * FROM key_management.archiv_akteure", nativeQuery = true)
        List akteureArchiv();

        //

        @Query(value = "SELECT max(log_id) FROM key_management.log_keycards", nativeQuery = true)
        Integer hoechsteKeycardLogID();

        @Query(value = "SELECT log_id, keycard_id, t.tür_id, ergebnis, erstellungszeitpunkt, r.raum_id, gebäudename, raumname, ist_sensitiv FROM key_management.log_türen lt JOIN verwaltungsabteilung.türen t ON lt.tür_id=t.tür_id JOIN verwaltungsabteilung.räume r ON t.raum_id=r.raum_id WHERE raumname = ?1 AND erstellungszeitpunkt BETWEEN ?2 AND ?3", nativeQuery = true)
        List tuerlogSuche(String raumname, Timestamp start, Timestamp ende);

        @Query(value = "SELECT log_id, keycard_id, t.tür_id, ergebnis, erstellungszeitpunkt, r.raum_id, gebäudename, raumname, ist_sensitiv FROM key_management.log_türen lt JOIN verwaltungsabteilung.türen t ON lt.tür_id=t.tür_id JOIN verwaltungsabteilung.räume r ON t.raum_id=r.raum_id WHERE gebäudename = ?1 AND erstellungszeitpunkt BETWEEN ?2 AND ?3", nativeQuery = true)
        List facilitySuche(String facility, Timestamp start, Timestamp ende);
}
