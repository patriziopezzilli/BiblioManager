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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
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
 * servlet per l'eliminazione di una ristampa, e se la ristampa eliminata è
 * l'ultima, cancella anche la pubblicazione con le recensioni e storyboard
 *
 * @author Biblioteca Digitale
 */
public class Eliminaristampa extends HttpServlet {

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
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws javax.naming.NamingException
     * @throws java.sql.SQLException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, NamingException, SQLException {
        Map data = new HashMap();
        HttpSession s = SecurityLayer.checkSession(request);
        Database.connect();
        //controllo sessione parametri e autorizzazione
        if (s != null && !isNull(request.getParameter("id"))
                && !isNull(request.getParameter("idr"))
                && DataUtil.getGroup((String) s.getAttribute("username")) != 3) {
            int id = Integer.parseInt(request.getParameter("id"));
            long idr = Long.parseLong(request.getParameter("idr"));

            //DA RICONTROLLARE//
            //se l'eliminazione dell'ultima ristampa va a buon fine(non vuota)
            ResultSet is;
            is = Database.selectRecord("ristampe", "isbn=" + idr);

            if (is.next()) {
                Path path = Paths.get(request.getServletContext().getRealPath("") + (is.getString("copertina")));
                //provo l'eliminazione del file nel filesystem e dell'immagine nel DB
                Files.delete(path);
                if (Database.deleteRecord("ristampe", "isbn=" + idr)) {
                    //controllo se sia l'ultima ristampa, se si procedo
                    ResultSet rs = Database.selectRecord("ristampe", "pubblicazioni=" + id);
                    if (!rs.next()) {
                        //elimino le modifiche e recensioni associate
                        if (Database.deleteRecord("storyboard", "id_pub=" + id)) {
                            if (Database.deleteRecord("recensioni", "id_pub=" + id)) //elimino la pubblicazione vuota
                            {
                                Database.deleteRecord("pubblicazioni", "id=" + id);
                            }
                        }
                    }
                }
                data.put("session", s.getAttribute("username"));
                response.sendRedirect("listacategorie");
            }
        } else {
            action_error(request, response);
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Eliminaristampa.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Eliminaristampa.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Eliminaristampa.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Eliminaristampa.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }
    }
// </editor-fold>

}
