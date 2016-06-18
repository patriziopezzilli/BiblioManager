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
 * servlet per la ricerca
 *
 * @author Biblioteca Digitale
 */
public class Ricerca extends HttpServlet {

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
     * @throws java.sql.SQLException
     * @throws javax.naming.NamingException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, NamingException {
        
        Map data = new HashMap();
        HttpSession s = SecurityLayer.checkSession(request);
        if (s != null) {
            Database.connect();
            int k = DataUtil.getGroup((String) s.getAttribute("username"));
            data.put("gruppo", k);

            data.put("session", s.getAttribute("username"));
            List<Pub> lista;
            int numberPage = 0;
            int numeropagine;
            int[] pagine;
            String ordine;
            
            if (isNull(request.getParameter("ordine"))) {
                    if (!isNull(request.getParameter("voto"))) {
                        ordine = "voto DESC";
                    } else if (!isNull(request.getParameter("titolo"))) {
                        ordine = "titolo";
                    } else {
                        ordine = "id";
                    }
                } else {
                    ordine = request.getParameter("ordine");
                }
                
                data.put("ordine", ordine);
            // 
            //isbn,titolo,autore,editore,parole chiave, lingua
            if (isNull(request.getParameter("numberpage")) && isNull(request.getParameter("totPage"))
                    && isNull(request.getParameter("numeropagine"))) {


                int totPage = Database.countRicerca(request.getParameter("parametro"));
                numeropagine = (int) Math.ceil((float) totPage / (float) 6);

                pagine = new int[numeropagine];
                ResultSet rs = Database.selectRicercaLimitxy(request.getParameter("parametro"), ordine, numberPage, 6);

                lista = new ArrayList<Pub>();

                while (rs.next()) {

                    int id = rs.getInt("id");
                    String titolo = rs.getString("titolo");
                    String autore = rs.getString("autore");
                    String descrizione = rs.getString("descrizione");
                    String categoria = rs.getString("categoria");
                    double voto = rs.getDouble("voto");
                    int keyword = rs.getInt("keyword");

                    Pub pubtemp = new Pub(id, titolo, autore, descrizione, categoria,
                            voto, keyword);
                    lista.add(pubtemp);
                }
            } else {

                numberPage = Integer.parseInt(request.getParameter("numberpage"));
                numeropagine = Integer.parseInt(request.getParameter("numeropagine"));
                pagine = new int[numeropagine];
                ResultSet rs = Database.selectRicercaLimitxy(request.getParameter("parametro"), ordine, numberPage * 6, 6);

                lista = new ArrayList<Pub>();

                while (rs.next()) {

                    int id = rs.getInt("id");
                    String titolo = rs.getString("titolo");
                    String autore = rs.getString("autore");
                    String descrizione = rs.getString("descrizione");
                    String categoria = rs.getString("categoria");
                    double voto = rs.getDouble("voto");
                    int keyword = rs.getInt("keyword");

                    Pub pubtemp = new Pub(id, titolo, autore, descrizione, categoria,
                            voto, keyword);
                    lista.add(pubtemp);
                }
            }
            Database.close();

            data.put("parametro", request.getParameter("parametro"));
            data.put("numberpage", numberPage);
            data.put("numeropagine", numeropagine);
            data.put("pagine", pagine);
            data.put("lista", lista);
            FreeMarker.process("listview.html", data, response, getServletContext());
        } else {
            response.sendRedirect("login");
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
        } catch (SQLException ex) {
            Logger.getLogger(Ricerca.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Ricerca.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (SQLException ex) {
            Logger.getLogger(Ricerca.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Ricerca.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }
    }

}
