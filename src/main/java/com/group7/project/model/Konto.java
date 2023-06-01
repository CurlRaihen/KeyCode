package com.group7.project.model;

import lombok.Data;

import javax.persistence.*;

import java.sql.Date;
import java.util.Objects;

@Data
@Entity
@Table(schema = "personalabteilung", name = "konten")
public class Konto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "konto_id")
    Integer konto_id;

    @Column(name = "benutzername")
    String benutzername;

    @Column(name = "passwort")
    String passwort;

    @Column(name = "rolle")
    String rolle;

    @Column(name = "vorname")
    String vorname;

    @Column(name = "nachname")
    String nachname;

    @Column(name = "geburtsdatum")
    Date geburtsdatum;

    @Column(name = "telefonnummer")
    String telefonnummer;

    @Column(name = "email")
    String email;

    @Column(name = "adresse")
    String adresse;

    @Column(name = "arbeitsvertragsende")
    Date arbeitsvertragsende;

    @Column(name = "erstellungszeitpunkt")
    Date erstellungszeitpunkt;

    @Column(name = "erlaubt")
    int erlaubt;

    public Konto() {

    }

    @Override
    public String toString() {
        return "Konto{" +
                "konto_id=" + konto_id +
                ", benutzername='" + benutzername + '\'' +
                ", passwort='" + passwort + '\'' +
                ", rolle='" + rolle + '\'' +
                ", erstellungszeitpunkt='" + erstellungszeitpunkt + '\'' +
                ", vorname='" + vorname + '\'' +
                ", nachname='" + nachname + '\'' +
                ", geburtsdatum='" + geburtsdatum + '\'' +
                ", telefnonnummer='" + telefonnummer + '\'' +
                ", email='" + email + '\'' +
                ", adresse='" + adresse + '\'' +
                ", arbeitsvertragsende='" + arbeitsvertragsende + '\'' +
                ", erlaubt='" + erlaubt + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Konto that = (Konto) o;
        return Objects.equals(konto_id, that.konto_id) && Objects.equals(benutzername, that.benutzername)
                && Objects.equals(passwort, that.passwort) && Objects.equals(email, that.email)
                && Objects.equals(rolle, that.rolle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(konto_id, benutzername, passwort, email);
    }

    public int getErlaubt() {
        return this.erlaubt;
    }

    public void setErlaubt(int erlaubt) {
        this.erlaubt = erlaubt;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public Date getGeburtsdatum() {
        return geburtsdatum;
    }

    public void setGeburtsdatum(Date geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Date getArbeitsvertragsende() {
        return arbeitsvertragsende;
    }

    public void setArbeitsvertragsende(Date arbeitsvertragsende) {
        this.arbeitsvertragsende = arbeitsvertragsende;
    }

    public Integer getId() {
        return konto_id;
    }

    public void setId(Integer konto_id) {
        this.konto_id = konto_id;
    }

    public String getBenutzername() {
        return benutzername;
    }

    public void setRolle(String rolle) {
        this.rolle = rolle;
    }

    public String getRolle() {
        return rolle;
    }

    public Date getErstellungszeitpunkt() {
        return erstellungszeitpunkt;
    }

    public void setErstellungszeitpunkt(Date erstellungszeitpunkt) {
        this.erstellungszeitpunkt = erstellungszeitpunkt;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}