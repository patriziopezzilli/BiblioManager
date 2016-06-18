/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.model;

import java.util.Date;

/**
 *classe per l'oggetto Modifica, necessario a contenere i dati della modifica
 * di una pubblicazione
 * 
 * @author Biblioteca Digitale
 */
public class Modifica {
   
    private int id;
    private int id_utente;
    private String descrizionemodifica;
    private int id_pub;
    private Date time;
    
    //costruttore
    public Modifica(int id, int id_utente, String descrizionemodifica, int id_pub, Date time) {
        this.id = id;
        this.id_utente = id_utente;
        this.descrizionemodifica = descrizionemodifica;
        this.id_pub = id_pub;
        this.time = time;
    }
    //metodi get e set
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_utente() {
        return id_utente;
    }

    public void setId_utente(int id_utente) {
        this.id_utente = id_utente;
    }

    public String getDescrizionemodifica() {
        return descrizionemodifica;
    }

    public void setDescrizionemodifica(String descrizionemodifica) {
        this.descrizionemodifica = descrizionemodifica;
    }

    public int getId_pub() {
        return id_pub;
    }

    public void setId_pub(int id_pub) {
        this.id_pub = id_pub;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    
    
    
}
