package com.group7.project.model;

import lombok.Data;

import javax.persistence.*;

import java.sql.Date;

@Data
@Entity
@Table(schema = "key_management", name = "keycards")
public class Keycard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keycard_id")
    int id;

    @Column(name = "benutzername")
    String benutzername;

    @Column(name = "anfangstag")
    Date anfangstag;

    @Column(name = "endtag")
    Date endtag;

    @Column(name = "status")
    String status;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBenutzername() {
        return this.benutzername;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    public Date getAnfangstag() {
        return this.anfangstag;
    }

    public void setAnfangstag(Date anfangstag) {
        this.anfangstag = anfangstag;
    }

    public Date getEndtag() {
        return this.endtag;
    }

    public void setEndtag(Date endtag) {
        this.endtag = endtag;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "";
    }
}
