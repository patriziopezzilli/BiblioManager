/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.servlets;

import it.biblio.utility.DataUtil;
import static it.biblio.utility.DataUtil.crypt;
import it.biblio.utility.Database;
import it.biblio.utility.FreeMarker;
import it.biblio.utility.SecurityLayer;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
 * servlet per la registrazione
 *
 * @author Biblioteca Digitale
 */
public class Register extends HttpServlet {

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
     * @throws java.sql.SQLException
     * @throws javax.naming.NamingException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, NamingException {
        Map data = new HashMap();
        Database.connect();
        HttpSession s = SecurityLayer.checkSession(request);
        if (s != null) {
            response.sendRedirect("index");
        } else if (request.getMethod().equals("POST")) {
            //Recupera l'email dell'utente
            String email = request.getParameter("u");
            //Recupera la password dell'utente
            String password = request.getParameter("p");
            //Recupera nome utente
            String nome = request.getParameter("n");
            //Recupera cognome utente
            String cognome = request.getParameter("co");
            //Recupera città
            String citta = request.getParameter("ci");
            //Recupera data di nascita
            String nascita = request.getParameter("da");

            Database.connect();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("usermail", email);
            map.put("password", crypt(password));
            map.put("nome", nome);
            map.put("cognome", cognome);
            map.put("citta", citta);
            map.put("annonascita", nascita);
            map.put("gruppi", 3);

            Database.insertRecord("utenti", map);
            ResultSet rs = Database.selectRecord("utenti", "usermail='" + email + "'");
            int k = 0;
            while (rs.next()) {
                k = rs.getInt("id");
            }
            SecurityLayer.createSession(request, email, k);

            Database.close();

            response.sendRedirect("index");
        } else {
            FreeMarker.process("register.html", data, response, getServletContext());
        }
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
