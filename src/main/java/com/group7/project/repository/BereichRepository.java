package com.group7.project.repository;

import com.group7.project.model.Bereich;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BereichRepository extends JpaRepository<Bereich, Integer> {

    @Query(value = "SELECT * FROM verwaltungsabteilung.bereiche WHERE bereich_id = ?1 OR bereichsname = ?1", nativeQuery = true)
    List<Bereich> sucheBereich(String wert);
}
