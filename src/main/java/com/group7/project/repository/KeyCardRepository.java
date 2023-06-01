package com.group7.project.repository;

import com.group7.project.model.Keycard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface KeyCardRepository extends JpaRepository<Keycard, Long> {

    // key_management.keycards

    Optional<Keycard> findKeycardById(Long id);

    Set<Keycard> findKeycardsByEndtag(Date tag);

    Set<Keycard> findKeycardsByAnfangstag(Date tag);

    @Query(value = "SELECT max(keycard_id) FROM key_management.keycards", nativeQuery = true)
    Integer hoechsteID();

    // höchste ID um wert für neue zugang_id festzulegen
    @Query(value = "SELECT max(zugang_id) FROM key_management.hat_zugang;", nativeQuery = true)
    Integer hoechsteIDzugang();

    @Query(value = "SELECT benutzername FROM key_management.keycards WHERE keycard_id = ?1", nativeQuery = true)
    String findOwner(int keycard_id);

    @Query(value = "SELECT email FROM key_management.keycards k join personalabteilung.konten p on k.benutzername = p.benutzername where k.keycard_id = ?1", nativeQuery = true)
    String findEmailByKeycard(int keycard_id);

    @Query(value = "SELECT keycard_id FROM key_management.keycards k join personalabteilung.konten p on k.benutzername = p.benutzername where p.rolle = ?1", nativeQuery = true)
    List<Integer> findKeycardsByRole(String rolle);

    @Query(value = "SELECT anfangstag FROM key_management.keycards WHERE keycard_id = ?1", nativeQuery = true)
    String findanfangstag(int keycard_id);

    @Query(value = "SELECT endtag FROM key_management.keycards WHERE keycard_id = ?1", nativeQuery = true)
    String findendtag(int keycard_id);

    @Query(value = "SELECT status FROM key_management.keycards WHERE keycard_id = ?1", nativeQuery = true)
    String findstatus(int keycard_id);

    @Query(value = "SELECT distinct h.raum_id FROM key_management.keycards k JOIN key_management.hat_zugang h ON k.keycard_id = h.keycard_id WHERE k.keycard_id = ?1 and h.raum_id IS NOT NULL", nativeQuery = true)
    List berechtigungenRaum(int keycard_id);

    @Query(value = "SELECT distinct h.bereich_id FROM key_management.keycards k JOIN key_management.hat_zugang h ON k.keycard_id = h.keycard_id WHERE k.keycard_id = ?1 and h.bereich_id IS NOT NULL", nativeQuery = true)
    List berechtigungenBereich(int keycard_id);

    // hier bereiche und räume auf türen runterbrechen für testzentrum
    @Query(value = "(select distinct t1.tür_id from key_management.hat_zugang z1 join verwaltungsabteilung.türen t1 on z1.raum_id = t1.raum_id where z1.keycard_id = 1) UNION (select distinct  t2.tür_id from key_management.hat_zugang z2 join verwaltungsabteilung.bereiche b on z2.bereich_id = b.bereich_id join verwaltungsabteilung.besteht_aus a on b.bereich_id = a.bereich_id join verwaltungsabteilung.türen t2 on a.raum_id = t2.raum_id where z2.keycard_id = 1)", nativeQuery = true)
    List berechtigungenTüren(int keycard_id);

    @Query(value = "select distinct r.gebäudename, r.raumname, r.raum_id from verwaltungsabteilung.räume r join key_management.hat_zugang z on r.raum_id = z.raum_id where z.keycard_id = ?1", nativeQuery = true)
    List raumBerechtigungen(int keycard_id);

    @Query(value = "select distinct r.raumname from verwaltungsabteilung.räume r join key_management.hat_zugang z on r.raum_id = z.raum_id where z.keycard_id = ?1", nativeQuery = true)
    List raumNameBerechtigungen(int keycard_id);

    @Query(value = "select distinct r.raumname from verwaltungsabteilung.räume r join key_management.hat_zugang z on r.raum_id = z.raum_id where z.keycard_id = ?1 and r.ist_sensitiv = 'Nein';", nativeQuery = true)
    List raumUnsensitiveBerechtigungen(int keycard_id);

    @Query(value = "select distinct b.bereichsname, b.bereich_id from verwaltungsabteilung.bereiche b join key_management.hat_zugang z on b.bereich_id = z.bereich_id where z.keycard_id = ?1", nativeQuery = true)
    List bereichBerechtigungen(int keycard_id);

    @Query(value = "select distinct b.bereichsname from verwaltungsabteilung.bereiche b join key_management.hat_zugang z on b.bereich_id = z.bereich_id where z.keycard_id = ?1", nativeQuery = true)
    List bereichNameBerechtigungen(int keycard_id);

    @Query(value = "select * from (select distinct b.bereichsname from verwaltungsabteilung.bereiche b join key_management.hat_zugang z on b.bereich_id = z.bereich_id where z.keycard_id = ?1) as ali where ali.bereichsname in (select bereichsname from verwaltungsabteilung.bereiche where bereich_id not in (SELECT distinct bereich_id FROM verwaltungsabteilung.besteht_aus ba join verwaltungsabteilung.räume r on ba.raum_id = r.raum_id where r.ist_sensitiv = 'Ja'))", nativeQuery = true)
    List bereichUnsensitiveBerechtigungen(int keycard_id);

    @Query(value = "SELECT tür_id FROM key_management.keycards k JOIN key_management.hat_zugang hz ON k.keycard_id = hz.keycard_id JOIN verwaltungsabteilung.türen t ON hz.raum_id=t.raum_id WHERE k.keycard_id = ?1 UNION SELECT tür_id FROM key_management.keycards k JOIN key_management.hat_zugang hz ON k.keycard_id = hz.keycard_id JOIN verwaltungsabteilung.bereiche b ON hz.bereich_id = b.bereich_id JOIN verwaltungsabteilung.besteht_aus ba ON b.bereich_id = ba.bereich_id JOIN verwaltungsabteilung.türen t ON t.raum_id = ba.raum_id WHERE k.keycard_id = ?1", nativeQuery = true)
    List berechtigungen(int keycard_id);

    @Query(value = "SELECT email, keycard_id FROM key_management.keycards kc JOIN personalabteilung.konten ko ON kc.benutzername = ko.benutzername WHERE datediff(endtag, curdate()) <=7", nativeQuery = true)
    List<String> akteureAblaufenderAdressen();

    @Modifying
    @Transactional
    @Query(value = "UPDATE key_management.keycards SET benutzername = ?1, anfangstag = ?2, endtag = ?3, status = ?4 WHERE keycard_id = ?5", nativeQuery = true)
    public void update(String benutzername, Date anfangstag, Date endtag, String status, int keycard_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE key_management.keycards SET anfangstag = ?1 WHERE keycard_id = ?2", nativeQuery = true)
    public void updateAnfangstag(Date anfangstag, int keycard_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE key_management.keycards SET endtag = ?1 WHERE keycard_id = ?2", nativeQuery = true)
    public void updateEndtag(Date endtag, int keycard_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE key_management.keycards SET status = ?1 WHERE keycard_id = ?2", nativeQuery = true)
    public void updateStatus(String status, int keycard_id);

    @Query(value = "SELECT keycard_id, status, anfangstag, endtag FROM key_management.keycards WHERE benutzername = ?1", nativeQuery = true)
    List listeKeycards(String username);

    @Query(value = "SELECT keycard_id, status FROM key_management.keycards where benutzername = ?1 and status = 'Aktiviert'", nativeQuery = true)
    List listeActiveKeycards(String username);

    @Query(value = "SELECT keycard_id FROM key_management.keycards WHERE benutzername = ?1", nativeQuery = true)
    List listenurKeycards(String username);

    @Query(value = "SELECT keycard_id FROM key_management.keycards WHERE benutzername = ?1 AND endtag >= CURRENT_DATE()", nativeQuery = true)
    List listenurAktiveKeycards(String username);

    @Query(value = "SELECT keycard_id FROM key_management.keycards", nativeQuery = true)
    List listenurKeycardIds();

    @Query(value = "SELECT keycard_id FROM key_management.keycards WHERE benutzername = ?1", nativeQuery = true)
    List<String> listenurKeycardsString(String username);
    // Keycard übertragen

    @Modifying
    @Transactional
    @Query(value = "UPDATE key_management.keycards SET benutzername = ?1 WHERE keycard_id = ?2", nativeQuery = true)
    public void updateOwner(String newOwner, int id);

    // Keycard löschen

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM key_management.keycards WHERE keycard_id = ?1", nativeQuery = true)
    public void deleteKeycard(int id);

    // neue Keycard erstellen

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO key_management.keycards (keycard_id, benutzername, anfangstag, endtag, status) VALUES (?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
    void keycardHinzufuegen(int keycard_id, String benutzername, Date anfangstag, Date endtag, String Status);

    // neue Zugangsberechtigung hinzufügen

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO key_management.hat_zugang (zugang_id, keycard_id, raum_id, bereich_id) VALUES (?1, ?2, ?3, ?4)", nativeQuery = true)
    void hatZugangHinzufuegen(int zugang_id, int keycard_id, Integer raum_id, Integer bereich_id);

    // Suche

    @Transactional
    @Query(value = "SELECT * FROM key_management.keycards WHERE keycard_id = ?1", nativeQuery = true)
    List<Keycard> search(String wert);

    // abgelaufene Keycards finden

    @Transactional
    @Query(value = "SELECT * FROM key_management.keycards where date_add(endtag, interval(1) day) < curdate()", nativeQuery = true)
    List<Keycard> abgelaufeneKeycards();

    //

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM key_management.hat_zugang WHERE keycard_id = ?1", nativeQuery = true)
    public void deleteAlleZugangrechts(int keycardId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM key_management.hat_zugang WHERE keycard_id = ?1 AND ((raum_id = ?2 and raum_id IS NOT NULL) OR (bereich_id = ?3 and bereich_id IS NOT NULL", nativeQuery = true)
    public void deleteZugangrecht(int keycardId, int raum_id, int bereich_id);

    @Query(value = "SELECT h.raum_id FROM key_management.keycards k JOIN key_management.hat_zugang h ON k.keycard_id = h.keycard_id WHERE k.keycard_id = ?1 and h.raum_id IS NOT NULL", nativeQuery = true)
    List<String> berechtigungenRaumString(int keycard_id);

    @Query(value = "SELECT h.bereich_id FROM key_management.keycards k JOIN key_management.hat_zugang h ON k.keycard_id = h.keycard_id WHERE k.keycard_id = ?1 and h.bereich_id IS NOT NULL", nativeQuery = true)
    List<String> berechtigungenBereichString(int keycard_id);

    @Query(value = "SELECT COUNT(*) FROM key_management.hat_zugang WHERE keycard_id = ?1 AND raum_id = ?2", nativeQuery = true)
    int raumZugangID(int keycard_id, int raum_id);

    @Query(value = "SELECT COUNT(*) FROM key_management.hat_zugang WHERE keycard_id = ?1 AND bereich_id = ?2", nativeQuery = true)
    int bereichZugangID(int keycard_id, int bereich_id);
}
