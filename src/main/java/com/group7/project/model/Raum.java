package com.group7.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "verwaltungsabteilung", name = "räume")
public class Raum {

    @Id
    @Column(name = "raum_id")
    Integer raum_id;

    public Integer getRaum_id() {
        return this.raum_id;
    }

    public void setRaum_id(Integer raum_id) {
        this.raum_id = raum_id;
    }

    @Column(name = "gebäudename")
    String gebäudename;

    public String getGebäudename() {
        return this.gebäudename;
    }

    public void setGebäudename(String gebäudename) {
        this.gebäudename = gebäudename;
    }

    @Column(name = "raumname")
    String raumname;

    public String getRaumname() {
        return this.raumname;
    }

    public void setRaumname(String raumname) {
        this.raumname = raumname;
    }

    @Column(name = "ist_sensitiv")
    String ist_sensitiv;

    public String getIst_sensitiv() {
        return this.ist_sensitiv;
    }

    public void setIst_sensitiv(String ist_sensitiv) {
        this.ist_sensitiv = ist_sensitiv;
    }

}
