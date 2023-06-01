package com.group7.project.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(schema = "verwaltungsabteilung", name = "türen")
public class Tuer {

    @Id
    @Column(name = "tür_id")
    Integer tür_id;

    public Integer getTüR_id() {
        return this.tür_id;
    }

    public void setTüR_id(Integer tür_id) {
        this.tür_id = tür_id;
    }

    @Column(name = "raum_id")
    Integer raum_id;

    public Integer getRaum_id() {
        return this.raum_id;
    }

    public void setRaum_id(Integer raum_id) {
        this.raum_id = raum_id;
    }

}
