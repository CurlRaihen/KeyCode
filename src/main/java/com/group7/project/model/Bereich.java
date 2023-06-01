package com.group7.project.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "verwaltungsabteilung", name = "bereiche")
public class Bereich {

    @Id
    @Column(name = "bereich_id")
    Integer bereich_id;

    public Integer getBereich_id() {
        return this.bereich_id;
    }

    public void setBereich_id(Integer bereich_id) {
        this.bereich_id = bereich_id;
    }

    @Column(name = "bereichsname")
    String bereichsname;

    public String getBereichsname() {
        return this.bereichsname;
    }

    public void setBereichsname(String bereichsname) {
        this.bereichsname = bereichsname;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Konto that = (Konto) o;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bereich_id, bereichsname);
    }

}
