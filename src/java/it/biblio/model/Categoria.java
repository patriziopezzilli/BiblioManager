/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.model;

/**
 * classe per l'oggetto categoria
 * 
 * @author Biblioteca Digitale
 */
public class Categoria {
    
   private int id;
   private String nome;
   
   //costruttore
    public Categoria(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }
    //metodi get e set
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
}
