/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.model;

/**
 *classse per l'oggetto keyword, ogni pubblicazione ha 3 keyword associate, 
 * rappresentate dai tag 1,2,3
 * 
 * @author Biblioteca Digitale
 */
public class Keyword {
    
    private int id;
    private String tag1;
    private String tag2;
    private String tag3;
    
    //costruttore
    public Keyword(int id, String tag1, String tag2, String tag3) {
        this.id = id;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
    }
    //metodi get e set
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }
    
    
}
