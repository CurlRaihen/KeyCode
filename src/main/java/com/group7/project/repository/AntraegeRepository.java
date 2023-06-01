package com.group7.project.repository;

import com.group7.project.model.Antrag;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AntraegeRepository extends JpaRepository<Antrag, Integer> {

    // key_management.anträge

    @Query(value = "SELECT * FROM key_management.anträge WHERE antrag_id = ?1", nativeQuery = true)
    Antrag finden(int antrag_id);

    @Query(value = "SELECT count(benutzername) FROM key_management.anträge", nativeQuery = true)
    int anzahlAntraege(String rolle);

    @Query(value = "SELECT * FROM key_management.anträge where benutzername = ?1 ORDER BY antrag_id", nativeQuery = true)
    List listAntraege(String benutzername);

    @Query(value = "SELECT antrag_id, a.benutzername, a.erstellungszeitpunkt, status, email, rolle, antragsart, bearbeiter FROM key_management.anträge a JOIN personalabteilung.konten k ON a.benutzername = k.benutzername WHERE bearbeitungsstelle = ?1 AND status = 'Nicht_bearbeitet' ORDER by antrag_id", nativeQuery = true)
    List<String[]> listBearbeitendeAntraege(String bearbeitungsStelle);

    @Query(value = "SELECT antrag_id FROM key_management.anträge a JOIN personalabteilung.konten k ON a.benutzername = k.benutzername WHERE status = 'Nicht_bearbeitet' AND bearbeitungsstelle = ?1 ORDER by antrag_id", nativeQuery = true)
    List<Integer> listAntragIdBearbeitendeAntraege(String bearbeitungsStelle);

    @Query(value = "SELECT antrag_id, status, a.benutzername, bearbeitungsstelle, a.erstellungszeitpunkt, rolle, antragsart FROM key_management.anträge a JOIN personalabteilung.konten k ON a.benutzername = k.benutzername ORDER by antrag_id", nativeQuery = true)
    List listAlleAntraege();

    @Query(value = "SELECT * FROM key_management.anträge where antrag_id = ?1", nativeQuery = true)
    List antragsInformationen(int antrag_id);

    @Query(value = "SELECT anfangstag FROM key_management.anträge where antrag_id = ?1", nativeQuery = true)
    Date antragsAnfangstag(int antrag_id);

    @Query(value = "SELECT endtag FROM key_management.anträge where antrag_id = ?1", nativeQuery = true)
    Date antragsEndstag(int antrag_id);

    @Query(value = "SELECT vorname, nachname FROM key_management.anträge a JOIN personalabteilung.konten k ON a.benutzername = k.benutzername WHERE antrag_id = ?1", nativeQuery = true)
    List vollerName(int antrag_id);

    @Query(value = "SELECT email FROM key_management.anträge a JOIN personalabteilung.konten k ON a.benutzername = k.benutzername WHERE antrag_id = ?1", nativeQuery = true)
    String emailzuAntrag(int antrag_id);

    @Query(value = "SELECT benutzername FROM key_management.anträge WHERE antrag_id = ?1", nativeQuery = true)
    String benutzername(int antrag_id);

    @Query(value = "SELECT MAX(antrag_id) FROM (SELECT antrag_id FROM key_management.anträge UNION ALL SELECT antrag_id FROM key_management.archiv_anträge) as subQuery", nativeQuery = true)
    Integer hoechsteID();

    @Transactional
    @Query(value = "SELECT * FROM key_management.anträge WHERE antrag_id = ?1 OR benutzername = ?1 OR bearbeitungsstelle = ?1", nativeQuery = true)
    List<Antrag> search(String wert);

    @Transactional
    @Modifying
    @Query(value = "UPDATE key_management.anträge SET status = ?1 WHERE antrag_id = ?2", nativeQuery = true)
    void updateStatus(String status, int antrag_id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE key_management.anträge SET bearbeiter = ?1 WHERE antrag_id = ?2", nativeQuery = true)
    void updateBearbeiter(String bearbeiter, int antrag_id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO key_management.anträge (antrag_id, String benutzername, erstellungszeitpunkt, status, dateilink, antragsart, keycard_id, kommentar, ist_sensitiv, bearbeiter) VALUES ()", nativeQuery = true)
    void antragHinzufuegen(int antrag_id);

    // key_management.beantragt_für

    // höchste ID um wert für neue beantragt_id festzulegen
    @Query(value = "SELECT MAX(beantragt_id) FROM (SELECT beantragt_id FROM key_management.beantragt_für", nativeQuery = true)
    Integer hoechsteIDbeantragt();

    @Query(value = "SELECT raum_id FROM key_management.anträge a JOIN key_management.beantragt_für b ON a.antrag_id=b.antrag_id where b.raum_id IS NOT NULL", nativeQuery = true)
    List räume(int antrag_id);

    @Query(value = "SELECT bereich_id FROM key_management.anträge a JOIN key_management.beantragt_für b ON a.antrag_id=b.antrag_id where b.bereich_id IS NOT NULL", nativeQuery = true)
    List bereiche(int antrag_id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO key_management.beantragt_für (beantragt_id, antrag_id, raum_id, bereich_id) VALUES (?1, ?2, ?3, ?4)", nativeQuery = true)
    void insertBeantragtFür(int beantragt_id, int antrag_id, Integer raum_id, Integer bereich_id);

    @Query(value = "SELECT raumname, r.raum_id FROM verwaltungsabteilung.räume r join (select raum_id from key_management.beantragt_für b WHERE antrag_id = ?1 and raum_id IS NOT NULL) b on r.raum_id = b.raum_id;", nativeQuery = true)
    List getBeantragtFuerRäume(int antrag_id);

    @Query(value = "SELECT bereichsname, b.bereich_id  from verwaltungsabteilung.bereiche b join (select bereich_id FROM key_management.beantragt_für WHERE antrag_id = ?1 and bereich_id IS NOT NULL) f on b.bereich_id = f.bereich_id", nativeQuery = true)
    List getBeantragtFuerBereiche(int antrag_id);

    @Query(value = "SELECT keycard_id FROM key_management.anträge where antrag_id = ?1", nativeQuery = true)
    Integer getKeycardID(int antrag_id);

    @Query(value = "SELECT MAX(beantragt_id) FROM key_management.beantragt_für", nativeQuery = true)
    Integer gethighestbeantragtID();

    //

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM key_management.anträge WHERE antrag_id = ?1", nativeQuery = true)
    void deleteAntrag(int id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM key_management.beantragt_für WHERE antrag_id = ?1", nativeQuery = true)
    void deleteBeantragtFuer(int antragId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO key_management.archiv_anträge (antrag_id, benutzername, keycard_id, bearbeitungsstelle, bearbeitername, erstellungszeitpunkt, status, datei, anfangstag, endtag, bereiche, raeume) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12)", nativeQuery = true)
    void insertArchivAntrag(int antragId, String benutzername, int keycardId, String bearbeitungsstelle,
            String bearbeitername,
            Timestamp erstellungszeitpunkt, String status, byte[] datei, Date anfangstag,
            Date endtag, String raeume, String bereiche);

    @Transactional
    @Modifying
    @Query(value = "UPDATE key_management.archiv_anträge SET keycard_id = NULL WHERE antrag_id = ?1", nativeQuery = true)
    void setNullKeycardIdArchivAntrag(int antrag_id);

    @Query(value = "SELECT dateiname FROM key_management.anträge WHERE antrag_id = ?1", nativeQuery = true)
    String getDateiname(int antrag_id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE key_management.archiv_anträge SET dateiname = NULL WHERE antrag_id = ?1", nativeQuery = true)
    void setNullDateinameArchivAntrag(int antrag_id);

    @Query(value = "SELECT datei FROM key_management.anträge WHERE antrag_id = ?1", nativeQuery = true)
    byte[] getDatei(int antrag_id);

    @Query(value = "SELECT antragsart FROM key_management.anträge WHERE antrag_id = ?1", nativeQuery = true)
    String getAntragart(int antrag_id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE key_management.archiv_anträge SET datei = NULL WHERE antrag_id = ?1", nativeQuery = true)
    void setNullDateiArchivAntrag(int antrag_id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM key_management.hat_zugang WHERE keycard_id = ?1", nativeQuery = true)
    public void deleteAlleZugangrechts(int keycardId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM key_management.hat_zugang WHERE keycard_id = ?1 AND raum_id = ?2", nativeQuery = true)
    public void deleteZugangrechtRaum(int keycardId, int raum_id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM key_management.hat_zugang WHERE keycard_id = ?1 AND bereich_id = ?2", nativeQuery = true)
    public void deleteZugangrechtBereich(int keycardId, int bereich_id);

    @Query(value = "SELECT raum_id FROM key_management.beantragt_für WHERE antrag_id = ?1 and raum_id IS NOT NULL", nativeQuery = true)
    List<String> getBeantragtFuersRaum(int antrag_id);

    @Query(value = "SELECT bereich_id FROM key_management.beantragt_für WHERE antrag_id = ?1 and bereich_id IS NOT NULL", nativeQuery = true)
    List<String> getBeantragtFuersBereich(int antrag_id);

}
