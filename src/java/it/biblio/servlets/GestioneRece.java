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
 * Servlet per la gestione delle recensione
 *
 * @author Biblioteca Digitale
 */
public class GestioneRece extends HttpServlet {

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
            data.put("gruppo", k);
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
        
        Database.connect();
        HttpSession s = SecurityLayer.checkSession(request);
        //controllo la sessione ed i permessi
        if (s != null && DataUtil.getGroup((String) s.getAttribute("username")) != 3) {

            int k = DataUtil.getGroup((String) s.getAttribute("username"));
            data.put("gruppo", k);
            data.put("session", s.getAttribute("username"));
            //se è stata fatta una richiesta di approvazione o cancellazione notifiche
            if (!isNull(request.getParameter("id")) && !isNull(request.getParameter("stato"))) {
                int id = Integer.parseInt(request.getParameter("id"));
                String stato = request.getParameter("stato");
                List<Recensione> listarec = new ArrayList<Recensione>();
                /*     APPROVAZIONE RECENSIONE    */
                if (stato.equals("approva")) {
                    Map<String, Object> temp = new HashMap<String, Object>();
                    temp.put("validato", 1);
                    Database.updateRecord("recensioni", temp, "id=" + id);

                    /*  Aggiorno su DB voto attuale del libro */
                    int sum = 0;
                    int cont = 0;
                    
                    ResultSet kk = Database.selectRecord("recensioni", "id_pub=" +request.getParameter("pub") +" && validato=1");
                    while (kk.next()) {
                        cont++;
                        sum += kk.getInt("voto");
                    }
                    int votodefinitivo = Math.round((float) sum / (float) cont);

                    Map<String, Object> temporanea = new HashMap<String, Object>();
                    temporanea.put("voto", votodefinitivo);
                    Database.updateRecord("pubblicazioni", temporanea, "id=" + request.getParameter("pub"));

                }
                /*     ELIMINAZIONE RECENSIONE    */
                if (stato.equals("rifiuta")) {

                    Database.deleteRecord("recensioni", "id=" + id);
                    data.put("listarecensioni", listarec);
                }
            }

            Recensione rec;
            List<Recensione> listarec = new ArrayList<Recensione>();

            /*     RECUPERO RECENSIONI DA METTERE IN LISTA    */
            ResultSet cs = Database.selectRecord("recensioni", "validato=FALSE");

            while (cs.next()) {

                int id_rec = cs.getInt("id");
                String titolo_rec = cs.getString("titolo_rec");
                String descrizione_rec = cs.getString("descrizione_rec");
                boolean validato = false;
                int voto = cs.getInt("voto");
                int id_pub = cs.getInt("id_pub");
                String user = cs.getString("user");

                rec = new Recensione(id_rec, titolo_rec, descrizione_rec, validato, voto, id_pub, user);
                listarec.add(rec);

            }

            data.put("listarecensioni", listarec);
            FreeMarker.process("gestionerecensioni.html", data, response, getServletContext());

            Database.close();

        } else {
            /* NOT IN SESSION */
            Database.close();
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
            Logger.getLogger(GestioneSistema.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(GestioneSistema.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }

    }
}
