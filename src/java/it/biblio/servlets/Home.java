/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.servlets;

import it.biblio.model.Pub;
import it.biblio.utility.DataUtil;
import it.biblio.utility.Database;
import it.biblio.utility.FreeMarker;
import it.biblio.utility.SecurityLayer;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
 * servlet per la homepage
 *
 * @author Biblioteca Digitale
 */
public class Home extends HttpServlet {

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
     * metodo per richiedere la lista delle ultime pubblicazioni modificate
     *
     * @param data
     * @throws SQLException
     */
    private void ultimeModificate(Map data) throws SQLException {

        ArrayList<Pub> ultime_modificate = new ArrayList<Pub>();
        ArrayList<String> copertine_modificate = new ArrayList();
        ResultSet rs = Database.selectJoinGroupbyLimit("storyboard", "ristampe",
                "id_pub=pubblicazioni",
                "id_pub",
                "time", 8);
        while (rs.next()) {
            Pub temp = new Pub();
            temp.setId(rs.getInt("pubblicazioni"));
            ultime_modificate.add(temp);
            copertine_modificate.add(rs.getString("copertina"));
        }
        data.put("ultime_modificate", ultime_modificate);
        data.put("copertine_modificate", copertine_modificate);
    }

    /**
     * metodo per richiedere la lista delle ultime pubblicazioni inserite
     *
     * @param data
     * @throws SQLException
     */
    private void ultimeInserite(Map data) throws SQLException {

        ArrayList<Pub> ultime_inserite = new ArrayList<Pub>();
        ArrayList<String> copertine = new ArrayList();
        ArrayList<Boolean> download = new ArrayList();
        ResultSet rs = Database.selectJoinGroupbyLimit("pubblicazioni", "ristampe",
                "pubblicazioni.id=ristampe.pubblicazioni",
                "pubblicazioni.titolo", "data_inserimento", 10);
        while (rs.next()) {
            ultime_inserite.add(new Pub(
                    rs.getInt("pubblicazioni.id"),
                    rs.getString("titolo"),
                    rs.getString("autore"),
                    rs.getDouble("voto")
            ));
            copertine.add(rs.getString("copertina"));
            if (!isNull(rs.getString("download"))) {
                download.add(true);
            } else {
                download.add(false);
            }
        }
        data.put("ultime_pubblicazioni", ultime_inserite);
        data.put("copertine", copertine);
        data.put("download", download);
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
        int k;
        if (s != null) {
            data.put("session", s.getAttribute("username"));
            k= DataUtil.getGroup((String) s.getAttribute("username"));
            data.put("gruppo",k);
            
        }
        ultimeInserite(data);
        ultimeModificate(data);
        data.put("activehome", 1);
        Database.close();
        FreeMarker.process("index.html", data, response, getServletContext());
    }

    /**
     * Caricamento pagina di Home get
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }
    }

    /**
     * Caricamento pagina di Home post
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }
    }

}
