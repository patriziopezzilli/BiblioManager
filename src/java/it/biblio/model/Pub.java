/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.model;

import it.biblio.utility.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.NamingException;

/**
 *classe per l'oggetto Pubblicazione
 * 
 * @author Biblioteca Digitale
 */
public class Pub {
    
    private int id;
    private String nome;
    private String autore;
    private String img;
    private String descrizione;
    private String categoria;
    private double voto;
    private int keyword;
    
    //costruttori
    public Pub(){};
    
    public Pub(int id, String nome,String autore,double voto ){
    
        this.id=id;
        this.nome=nome;
        this.autore=autore;
        this.voto=voto;
        
    }
    
    public Pub(int id, String nome, String autore, String descrizione,
               String categoria, double voto, int keyword ) throws NamingException, SQLException{
        
        this.id=id;
        this.autore=autore;
        this.nome=nome;
        this.categoria=categoria;
        this.descrizione=descrizione;
        this.voto=voto;
        this.keyword=keyword;
        
        ResultSet rs= Database.selectRecord("ristampe", "pubblicazioni="+id,"data_inserimento DESC");
        if(rs.next())
        this.img= rs.getString("copertina");
        else this.img=null;
    }
    
    //metodi get  e set
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getAutore() {
        return autore;
    }

    public String getImg() {
        return img;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getVoto() {
        return voto;
    }

    public int getKeyword() {
        return keyword;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setVoto(double voto) {
        this.voto = voto;
    }

    public void setKeyword(int keyword) {
        this.keyword = keyword;
    }
    
   
    
}
