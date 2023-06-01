package com.group7.project.repository;

import com.group7.project.model.Bereich;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacilityRepository extends JpaRepository<Bereich, Integer> {

    // verwaltungsabteilung

    @Query(value = "SELECT DISTINCT gebäudename FROM verwaltungsabteilung.bereiche b JOIN verwaltungsabteilung.besteht_aus a ON b.bereich_id = a.bereich_id JOIN verwaltungsabteilung.räume r ON a.raum_id = r.raum_id JOIN verwaltungsabteilung.türen t ON r.raum_id = t.raum_id", nativeQuery = true)
    List<String> gebäude();

    @Query(value = "SELECT count(türen) FROM key_management.anträge", nativeQuery = true)
    int tuerAnzahl(String bereich);

    @Query(value = "SELECT DISTINCT gebäudename, b.bereichsname FROM verwaltungsabteilung.bereiche b JOIN verwaltungsabteilung.besteht_aus a ON b.bereich_id = a.bereich_id JOIN verwaltungsabteilung.räume r ON a.raum_id = r.raum_id JOIN verwaltungsabteilung.türen t ON r.raum_id = t.raum_id", nativeQuery = true)
    List<String> bereiche();

    @Query(value = "SELECT DISTINCT gebäudename, b.bereichsname, raumname, ist_sensitiv FROM verwaltungsabteilung.bereiche b JOIN verwaltungsabteilung.besteht_aus a ON b.bereich_id = a.bereich_id JOIN verwaltungsabteilung.räume r ON a.raum_id = r.raum_id JOIN verwaltungsabteilung.türen t ON r.raum_id = t.raum_id", nativeQuery = true)
    List<String> räume();

    @Query(value = "SELECT DISTINCT bereichsname FROM verwaltungsabteilung.bereiche", nativeQuery = true)
    List<String> nurBereichsnamen();

    @Query(value = "SELECT DISTINCT raumname FROM verwaltungsabteilung.räume", nativeQuery = true)
    List<String> nurRaumnamen();

    @Query(value = "SELECT tür_id, ist_sensitiv FROM verwaltungsabteilung.bereiche b JOIN verwaltungsabteilung.besteht_aus a ON b.bereich_id = a.bereich_id JOIN verwaltungsabteilung.räume r ON a.raum_id = r.raum_id JOIN verwaltungsabteilung.türen t ON r.raum_id = t.raum_id WHERE raumname=?1 ORDER BY (tür_id)", nativeQuery = true)
    List<String> türenZuRaum(String raumname);

    @Query(value = "SELECT tür_id, ist_sensitiv FROM verwaltungsabteilung.bereiche b JOIN verwaltungsabteilung.besteht_aus a ON b.bereich_id = a.bereich_id JOIN verwaltungsabteilung.räume r ON a.raum_id = r.raum_id JOIN verwaltungsabteilung.türen t ON r.raum_id = t.raum_id WHERE bereichsname=?1 ORDER BY (tür_id)", nativeQuery = true)
    List<String> türenZuBereich(String bereichsname);

    @Query(value = "SELECT DISTINCT tür_id FROM verwaltungsabteilung.bereiche b JOIN verwaltungsabteilung.besteht_aus a ON b.bereich_id = a.bereich_id JOIN verwaltungsabteilung.räume r ON a.raum_id = r.raum_id JOIN verwaltungsabteilung.türen t ON r.raum_id = t.raum_id ORDER BY (tür_id)", nativeQuery = true)
    List<String> alleTüren();

    @Query(value = "SELECT DISTINCT CONCAT(tür_id, ' Raum: ', raumname) FROM verwaltungsabteilung.bereiche b JOIN verwaltungsabteilung.besteht_aus a ON b.bereich_id = a.bereich_id JOIN verwaltungsabteilung.räume r ON a.raum_id = r.raum_id JOIN verwaltungsabteilung.türen t ON r.raum_id = t.raum_id;", nativeQuery = true)
    List<String> alleTürenRäume();

    @Query(value = "SELECT DISTINCT (benutzername) FROM key_management.keycards k JOIN key_management.hat_zugang z ON k.keycard_id = z.keycard_id JOIN verwaltungsabteilung.türen t ON z.tür_id = t.tür_id WHERE t.tür_id = ?1", nativeQuery = true)
    List<String> benutzernamenZuTür(int tür_id);

    @Query(value = "SELECT DISTINCT(bereichsname) FROM verwaltungsabteilung.bereiche", nativeQuery = true)
    List allBereiche();

    @Query(value = "SELECT DISTINCT(raumname) FROM verwaltungsabteilung.räume", nativeQuery = true)
    List allRaeume();

    @Query(value = "SELECT raumname FROM verwaltungsabteilung.räume where ist_sensitiv = 'Nein'", nativeQuery = true)
    List allUnsensitiveRaeume();

    @Query(value = "select bereichsname from verwaltungsabteilung.bereiche where bereich_id not in (SELECT distinct bereich_id FROM verwaltungsabteilung.besteht_aus ba join verwaltungsabteilung.räume r on ba.raum_id = r.raum_id where r.ist_sensitiv = 'Ja')", nativeQuery = true)
    List allUnsensitiveBereiche();

    @Query(value = "SELECT ist_sensitiv FROM verwaltungsabteilung.bereiche b JOIN verwaltungsabteilung.besteht_aus ba ON b.bereich_id = ba.bereich_id JOIN verwaltungsabteilung.räume r ON ba.raum_id = r.raum_id WHERE bereichsname = ?1", nativeQuery = true)
    List bereichistSensitiv(String bereichsname);

    @Query(value = "SELECT ist_sensitiv FROM verwaltungsabteilung.bereiche b JOIN verwaltungsabteilung.besteht_aus ba ON b.bereich_id = ba.bereich_id JOIN verwaltungsabteilung.räume r ON ba.raum_id = r.raum_id WHERE raumname = ?1", nativeQuery = true)
    List raumistSensitiv(String raumname);

    // Suchfunktion

    @Transactional
    @Query(value = "SELECT b.bereich_id, bereichsname, a.raum_id, raumname, gebäudename, ist_sensitiv, tür_id FROM verwaltungsabteilung.bereiche b JOIN verwaltungsabteilung.besteht_aus a ON b.bereich_id = a.bereich_id JOIN verwaltungsabteilung.räume r ON a.raum_id = r.raum_id JOIN verwaltungsabteilung.türen t ON r.raum_id = t.raum_id WHERE bereichsname = ?1 OR gebäudename = ?1 OR raumname = ?1 ORDER BY (tür_id)", nativeQuery = true)
    public List search(String keyword);

    @Query(value = "SELECT b.bereich_id, bereichsname, a.raum_id, gebäudename, raumname FROM verwaltungsabteilung.bereiche b JOIN verwaltungsabteilung.besteht_aus a ON b.bereich_id = a.bereich_id JOIN verwaltungsabteilung.räume r ON a.raum_id = r.raum_id ORDER BY (raum_id);", nativeQuery = true)
    List alleFacilityNamen();

    @Query(value = "SELECT * FROM verwaltungsabteilung.bereiche b JOIN verwaltungsabteilung.besteht_aus a ON b.bereich_id = a.bereich_id JOIN verwaltungsabteilung.räume r ON a.raum_id = r.raum_id JOIN verwaltungsabteilung.türen t ON r.raum_id = t.raum_id ORDER BY (tür_id) WHERE", nativeQuery = true)
    List<String> search();

    @Query(value = "SELECT bereichsname FROM verwaltungsabteilung.bereiche where bereichsname LIKE %?1% ", nativeQuery = true)
    List<String> searchBereiche(String bereich);

    @Query(value = "SELECT raumname FROM verwaltungsabteilung.räume where raumname LIKE %?1% ", nativeQuery = true)
    List<String> searchRaeume(String raum);

    @Query(value = "SELECT distinct gebäudename FROM verwaltungsabteilung.räume where gebäudename LIKE %?1% ", nativeQuery = true)
    List<String> searchGebaeude(String gebäude);

    @Query(value = "select distinct k.benutzername, k.rolle from personalabteilung.konten k join key_management.keycards kc on k.benutzername = kc.benutzername join key_management.hat_zugang z on kc.keycard_id = z.keycard_id join verwaltungsabteilung.räume r on z.raum_id = r.raum_id where r.raumname = ?1", nativeQuery = true)
    List findUsersbyRaum(String raum);

    @Query(value = "select distinct k.benutzername, k.rolle from personalabteilung.konten k join key_management.keycards kc on k.benutzername = kc.benutzername join key_management.hat_zugang z on kc.keycard_id = z.keycard_id join verwaltungsabteilung.bereiche b on z.bereich_id = b.bereich_id where b.bereichsname = ?1", nativeQuery = true)
    List findUsersbyBereich(String bereich);

    @Query(value = "SELECT bereich_id FROM verwaltungsabteilung.bereiche WHERE bereichsname = ?1", nativeQuery = true)
    Integer idzuBereich(String bereichsname);

    @Query(value = "SELECT raum_id FROM verwaltungsabteilung.räume WHERE raumname = ?1", nativeQuery = true)
    Integer idzuRaum(String raumname);

    @Query(value = "SELECT bereichsname FROM verwaltungsabteilung.bereiche WHERE bereich_id = ?1", nativeQuery = true)
    String getBereichsName(int bereichID);

    @Query(value = "SELECT raumname FROM verwaltungsabteilung.räume WHERE raum_id = ?1", nativeQuery = true)
    String getRaumName(int raumID);

    // noch optimieren für bereiche
    @Query(value = "select distinct k.benutzername, k.rolle from personalabteilung.konten k join key_management.keycards kc on k.benutzername = kc.benutzername join key_management.hat_zugang z on kc.keycard_id = z.keycard_id join verwaltungsabteilung.räume r on z.raum_id = r.raum_id where r.gebäudename = ?1", nativeQuery = true)
    List findUsersbyGebaeude(String gebaeude);

    // Henrik
    @Query(value = "SELECT distinct(raumname) FROM verwaltungsabteilung.räume where gebäudename = :facilityName", nativeQuery = true)
    List alleRaeumeProFacility(@Param("facilityName") String facility);

    @Query(value = "SELECT count(raum_id) FROM verwaltungsabteilung.räume where gebäudename = :facilityName", nativeQuery = true)
    List anzahlRaeumeProFacility(@Param("facilityName") String facility);

    @Query(value = "SELECT distinct(raum_id) FROM verwaltungsabteilung.räume where gebäudename = :facilityName AND ist_sensitiv = 'Ja'", nativeQuery = true)
    List sensitiveRaeumeProFacility(@Param("facilityName") String facility);

    // TODO bereiche sensitiv?
    // TODO räume sensitiv?
}
