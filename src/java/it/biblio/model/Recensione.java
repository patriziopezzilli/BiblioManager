/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.model;

/**
 *classe per l'oggetto Recensione
 * 
 * @author Biblioteca Digitale
 */
public class Recensione {
    
    private int id;
    private String titolo_rec;
    private String descrizione_rec;
    private boolean validato;
    private int voto;
    private int id_pub;
    private String user;
    //costruttore
    public Recensione(int id, String titolo_rec, String descrizione_rec, boolean validato, int voto, int id_pub, String user) {
        this.id = id;
        this.titolo_rec = titolo_rec;
        this.descrizione_rec = descrizione_rec;
        this.validato = validato;
        this.voto = voto;
        this.id_pub = id_pub;
        this.user = user;
    }
    //metodi get e set
    public int getId() {
        return id;
    }

    public String getTitolo_rec() {
        return titolo_rec;
    }

    public String getDescrizione_rec() {
        return descrizione_rec;
    }

    public boolean isValidato() {
        return validato;
    }

    public int getVoto() {
        return voto;
    }

    public int getId_pub() {
        return id_pub;
    }

    public String getUser() {
        return user;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitolo_rec(String titolo_rec) {
        this.titolo_rec = titolo_rec;
    }

    public void setDescrizione_rec(String descrizione_rec) {
        this.descrizione_rec = descrizione_rec;
    }

    public void setValidato(boolean validato) {
        this.validato = validato;
    }

    public void setVoto(int voto) {
        this.voto = voto;
    }

    public void setId_pub(int id_pub) {
        this.id_pub = id_pub;
    }

    public void setUser(String user) {
        this.user = user;
    }

    
}
