package com.group7.project.model;

import java.sql.Timestamp;
import java.sql.Date;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(schema = "key_management", name = "anträge")
public class Antrag {

    @Id
    @Column(name = "antrag_id")
    Integer antrag_id;

    @Column(name = "benutzername")
    String benutzername;

    @Column(name = "bearbeitungsstelle")
    String bearbeitungsstelle;

    @Column(name = "erstellungszeitpunkt")
    Timestamp erstellungszeitpunkt;

    @Column(name = "status")
    String status;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    byte[] datei;

    @Column(name = "antragsart")
    String antragsart;

    @Column(name = "keycard_id")
    Integer keycard_id;

    @Column(name = "kommentar")
    String kommentar;

    @Column(name = "ist_sensitiv")
    String ist_sensitiv;

    @Column(name = "bearbeiter")
    String bearbeiter;

    @Column(name = "anfangstag")
    Date anfangstag;

    public Date getAnfangstag() {
        return this.anfangstag;
    }

    public void setAnfangstag(Date anfangstag) {
        this.anfangstag = anfangstag;
    }

    @Column(name = "endtag")
    Date endtag;

    public Date getEndtag() {
        return this.endtag;
    }

    public void setEndtag(Date endtag) {
        this.endtag = endtag;
    }

    public Integer getAntrag_id() {
        return this.antrag_id;
    }

    public void setAntrag_id(Integer antrag_id) {
        this.antrag_id = antrag_id;
    }

    public String getBenutzername() {
        return this.benutzername;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    public String getBearbeitungsstelle() {
        return this.bearbeitungsstelle;
    }

    public void setBearbeitungsstelle(String bearbeitungsstelle) {
        this.bearbeitungsstelle = bearbeitungsstelle;
    }

    public Timestamp getErstellungszeitpunkt() {
        return this.erstellungszeitpunkt;
    }

    public void setErstellungszeitpunkt(Timestamp erstellungszeitpunkt) {
        this.erstellungszeitpunkt = erstellungszeitpunkt;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getDatei() {
        return this.datei;
    }

    public void setDatei(byte[] datei) {
        this.datei = datei;
    }

    @Override
    public String toString() {
        return "";
    }

}
