package com.group7.project.repository;

import com.group7.project.model.Raum;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RaumRepository extends JpaRepository<Raum, Integer> {

    @Query(value = "SELECT * FROM verwaltungsabteilung.räume WHERE raum_id = ?1 OR raumname = ?1", nativeQuery = true)
    List<Raum> sucheRaum(String wert);

}
