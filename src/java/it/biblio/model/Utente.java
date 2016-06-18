/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.model;

import java.sql.Date;

/**
 *  Classe per l'oggetto Utente
 * 
 * @author Biblioteca Digitale
 */
public class Utente {
    private String usermail;
    private String nome;
    private String cognome;
    private String citta;
    private Date datanascita;
    private int gruppo;
    //costruttori
        public Utente(String usermail, String nome, String cognome,  int gruppi) {
        this.usermail = usermail;
        this.nome = nome;
        this.cognome = cognome;
        this.gruppo= gruppi;
    }

    public Utente(String usermail, String nome, String cognome, String citta, Date datanascita, int gruppi) {
        this.usermail = usermail;
        this.nome = nome;
        this.cognome = cognome;
        this.citta = citta;
        this.datanascita = datanascita;
        this.gruppo= gruppi;
    }
    //metodi get e set
    public String getUsermail() {
        return usermail;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getCitta() {
        return citta;
    }

    public Date getDatanascita() {
        return datanascita;
    }

    public int getGruppo() {
        return gruppo;
    }

    public void setUsermail(String usermail) {
        this.usermail = usermail;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public void setDatanascita(Date datanascita) {
        this.datanascita = datanascita;
    }

    public void setGruppo(int gruppo) {
        this.gruppo = gruppo;
    }

}
