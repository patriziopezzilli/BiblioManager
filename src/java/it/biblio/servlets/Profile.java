/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.servlets;

import it.biblio.model.Recensione;
import it.biblio.utility.DataUtil;
import it.biblio.utility.Database;
import it.biblio.utility.FreeMarker;
import it.biblio.utility.SecurityLayer;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
 * servlet per il profilo
 *
 * @author Biblioteca Digitale
 */
public class Profile extends HttpServlet {

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
        if (s != null) {
            Recensione rec;
            String nomeutente = (String) s.getAttribute("username");
            data.put("session", nomeutente);
            List<Recensione> listarec = new ArrayList<Recensione>();

            Database.connect();
            
            int k = DataUtil.getGroup((String) s.getAttribute("username"));
            data.put("gruppo",k);
            /*     RECUPERO INFORMAZIONI UTENTE   */
            ResultSet rs = Database.selectRecord("utenti", "usermail='" + data.get("session") + "'");

            while (rs.next()) {

                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String citta = rs.getString("citta");
                Date annonascita = rs.getDate("annonascita");
                int ruolo = rs.getInt("gruppi");
                data.put("nome", nome);
                data.put("cognome", cognome);
                data.put("citta", citta);
                data.put("annonascita", annonascita);
                data.put("gruppi", ruolo);

            }
             /*     RECUPERO RECENSIONI IN ATTESA DI APPROVAZIONE   */
            ResultSet cs = Database.selectRecord("recensioni", "user=" + s.getAttribute("userid") + " && validato=FALSE");
            while (cs.next()) {

                int id_rec = cs.getInt("id");
                String titolo_rec = cs.getString("titolo_rec");
                String descrizione_rec = cs.getString("descrizione_rec");
                boolean validato = cs.getBoolean("validato");
                int voto = cs.getInt("voto");
                int id_pub = cs.getInt("id_pub");
                String user = cs.getString("user");

                rec = new Recensione(id_rec, titolo_rec, descrizione_rec, validato, voto, id_pub, user);
                listarec.add(rec);

            }
            data.put("listarecensioni", listarec);
            FreeMarker.process("profile.html", data, response, getServletContext());

        } else {
            /* NOT IN SESSION */
            action_error(request, response);
        }
        Database.close();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Profile.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Profile.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Profile.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Profile.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }
    }

}
