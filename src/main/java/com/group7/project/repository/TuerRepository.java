package com.group7.project.repository;

import com.group7.project.model.Tuer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TuerRepository extends JpaRepository<Tuer, Integer> {

    @Query(value = "SELECT * FROM verwaltungsabteilung.türen WHERE tür_id = ?1", nativeQuery = true)
    List<Tuer> sucheTuer(String wert);

    @Query(value = "SELECT raumname FROM verwaltungsabteilung.räume r JOIN verwaltungsabteilung.türen t ON r.raum_id = t.raum_id WHERE tür_id = ?1", nativeQuery = true)
    String getRaumname(int tuerId);

    @Query(value = "SELECT distinct bereichsname FROM verwaltungsabteilung.bereiche b JOIN verwaltungsabteilung.besteht_aus a ON b.bereich_id = a.bereich_id " +
            "JOIN verwaltungsabteilung.räume r ON r.raum_id = a.raum_id JOIN verwaltungsabteilung.türen t ON t.raum_id = r.raum_id  WHERE tür_id = ?1", nativeQuery = true)
    String getBereichname(int tuerId);
}
