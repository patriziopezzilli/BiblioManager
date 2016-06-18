/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.model;


/**
 *
 * @author Biblioteca Digitale
 */

public class Ristampa {
    
    private long  isbn;
    private int numpagine;
    private String datapub;
    private String editore;
    private String lingua;
    private String download;
    private int pubblicazioni;
    private String indice;
    private String data_inserimento;
    private String copertina;
    //costruttori
    public Ristampa(long isbn, int numpagine, String datapub, String editore, String lingua, int pubblicazioni, String indice) {
        this.isbn = isbn;
        this.numpagine = numpagine;
        this.datapub = datapub;
        this.editore = editore;
        this.lingua = lingua;
        this.pubblicazioni = pubblicazioni;
        this.indice = indice;
    }
        public Ristampa(long isbn, int numpagine, String datapub, String editore, String lingua,String download, int pubblicazioni, String indice, String datainserimento, String copertina) {
        this.isbn = isbn;
        this.numpagine = numpagine;
        this.datapub = datapub;
        this.editore = editore;
        this.download=download;
        this.copertina=copertina;
        this.data_inserimento=datainserimento;
        this.lingua = lingua;
        this.pubblicazioni = pubblicazioni;
        this.indice = indice;
    }
    //metodi get e set
    public long getIsbn() {
        return isbn;
    }

    public int getNumpagine() {
        return numpagine;
    }

    public String getDatapub() {
        return datapub;
    }

    public String getEditore() {
        return editore;
    }

    public String getLingua() {
        return lingua;
    }

    public String getDownload() {
        return download;
    }

    public int getPubblicazioni() {
        return pubblicazioni;
    }

    public String getIndice() {
        return indice;
    }

    public String getData_inserimento() {
        return data_inserimento;
    }

    public String getCopertina() {
        return copertina;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    public void setNumpagine(int numpagine) {
        this.numpagine = numpagine;
    }

    public void setDatapub(String datapub) {
        this.datapub = datapub;
    }

    public void setEditore(String editore) {
        this.editore = editore;
    }

    public void setLingua(String lingua) {
        this.lingua = lingua;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public void setPubblicazioni(int pubblicazioni) {
        this.pubblicazioni = pubblicazioni;
    }

    public void setIndice(String indice) {
        this.indice = indice;
    }

    public void setData_inserimento(String data_inserimento) {
        this.data_inserimento = data_inserimento;
    }

    public void setCopertina(String copertina) {
        this.copertina = copertina;
    }
}
