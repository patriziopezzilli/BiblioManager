/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.servlets;

import it.biblio.model.Utente;
import it.biblio.utility.DataUtil;
import it.biblio.utility.Database;
import it.biblio.utility.FreeMarker;
import it.biblio.utility.SecurityLayer;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.isNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * servlet per la gestione della promozione/degradazione dello staff
 *
 * @author Biblioteca Digitale
 */
public class GestioneUtenza extends HttpServlet {

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
     * metodo per la ricerca dell'utenza tramite stringa di parametro
     *
     * @param parametro
     * @return
     * @throws SQLException
     * @throws NamingException
     */
    private List<Utente> listautenti(String parametro) throws SQLException, NamingException {
        List<Utente> utenti = new ArrayList<Utente>();

        String condition1 = "usermail LIKE \"%" + parametro + "%\"";
        String condition2 = "nome LIKE \"%" + parametro + "%\"";
        String condition3 = "cognome LIKE \"%" + parametro + "%\"";
        String condizione = "(" + condition1 + " || " + condition2 + " || " + condition3 + ") AND gruppi!=1";
        //ricerca gli utenti tramite parametro e li ordina per gruppo
        ResultSet rs = Database.selectRecord("utenti", condizione, "gruppi");

        while (rs.next()) {
            utenti.add(
                    new Utente(rs.getString("usermail"), rs.getString("nome"), rs.getString("cognome"), rs.getInt("gruppi")
                    )
            );
        }
        return utenti;
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

        List<Utente> utenti;
        Map data = new HashMap();
        Database.connect();
        HttpSession s = SecurityLayer.checkSession(request);
        //controllo sessione e permessi amministratore
        if (s != null && DataUtil.getGroup((String) s.getAttribute("username")) == 1) {
            
            int k = DataUtil.getGroup((String) s.getAttribute("username"));
            data.put("gruppo",k);
            
            data.put("session", s.getAttribute("username"));
            //se ho richiesto la promozione/degradazione di un utente
            if (!isNull(request.getParameter("utente"))) {

                Map update = new HashMap();
                int gruppout;
                gruppout = DataUtil.getGroup(request.getParameter("utente"));
                if (gruppout == 2) {
                    update.put("gruppi", 3);
                    Database.updateRecord("utenti", update, "usermail='" + request.getParameter("utente") + "'");
                } else {
                    update.put("gruppi", 2);
                    Database.updateRecord("utenti", update, "usermail='" + request.getParameter("utente") + "'");
                }

            }
            //se ho eseguito la ricerca di un utente
            if (!isNull(request.getParameter("parametro"))) {
                String parametro = request.getParameter("parametro");
                data.put("parametro", parametro);
                utenti = listautenti(parametro);
                data.put("listautenti", utenti);

            }

            FreeMarker.process("gestioneutenza.html", data, response, getServletContext());
            Database.close();

        } else {
            action_error(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            processRequest(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(GestioneSistema.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(GestioneSistema.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(GestioneUtenza.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(GestioneUtenza.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }
    }

}
