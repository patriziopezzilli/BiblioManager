/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.servlets;

import it.biblio.utility.DataUtil;
import it.biblio.utility.Database;
import it.biblio.utility.FreeMarker;
import it.biblio.utility.SecurityLayer;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * servlet per la lista delle categorie
 *
 * @author Biblioteca Digitale
 */
public class Categorie extends HttpServlet {

    /**
     * metodo per notificare all'utente che si è verificato un errore
     *
     * @param response
     * @throws IOException
     */
    private void action_error(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession s = SecurityLayer.checkSession(request);
        Map data = new HashMap();
        if (s != null) {
            
            int k = 0;
            try {
                k = DataUtil.getGroup((String) s.getAttribute("username"));
            } catch (SQLException ex) {
                Logger.getLogger(Bookdetail.class.getName()).log(Level.SEVERE, null, ex);
            }
            data.put("gruppo",k);
            data.put("session", s.getAttribute("username"));
        }
        FreeMarker.process("404page.html", data, response, getServletContext());
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @throws javax.naming.NamingException
     * @throws java.sql.SQLException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, NamingException, SQLException {
        Map data = new HashMap();
        HttpSession s = SecurityLayer.checkSession(request);
        Database.connect();
        //se è in sessione stampo benvenuto
        if (s != null) {
            data.put("session", s.getAttribute("username"));
            int k = DataUtil.getGroup((String) s.getAttribute("username"));
            data.put("gruppo",k);
        }
        //prendo la lista delle categorie dal database
        
        ResultSet rs = Database.selectRecord("INFORMATION_SCHEMA.COLUMNS", "TABLE_NAME = 'pubblicazioni' AND COLUMN_NAME = 'categoria'");
        String st = null;
        while (rs.next()) {
            st = rs.getString("COLUMN_TYPE");
        }
        List<String> enuml = DataUtil.parsaEnum(st);
        data.put("lista_categorie", enuml);

        Database.close();
        data.put("activecategorie", 1);

        FreeMarker.process("listacategorie.html", data, response, getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            processRequest(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Categorie.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Categorie.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            processRequest(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Categorie.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Categorie.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }

    }

}
