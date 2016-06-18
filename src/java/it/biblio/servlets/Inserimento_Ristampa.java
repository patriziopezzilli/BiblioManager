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
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
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
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * servlet per l'inserimento della ristampa
 *
 *
 * @author Biblioteca Digitale
 */
public class Inserimento_Ristampa extends HttpServlet {

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
     * metodo per gestire l'upload di file
     *
     * @param request
     * @param response
     * @param k
     * @return
     * @throws IOException
     */
    private boolean upload(HttpServletRequest request) throws IOException, Exception {

        Map<String, Object> ristampe = new HashMap<String, Object>();
        int idopera = Integer.parseInt(request.getParameter("id"));

        if (ServletFileUpload.isMultipartContent(request)) {

            FileItemFactory fif = new DiskFileItemFactory();
            ServletFileUpload sfo = new ServletFileUpload(fif);
            List<FileItem> items = sfo.parseRequest(request);
            for (FileItem item : items) {
                String fname = item.getFieldName();
                if (item.isFormField() && fname.equals("ISBN") && !item.getString().isEmpty()) {
                    ristampe.put("isbn", item.getString());
                } else if (item.isFormField() && fname.equals("numero_pagine") && !item.getString().isEmpty()) {
                    ristampe.put("numpagine", Integer.parseInt(item.getString()));
                } else if (item.isFormField() && fname.equals("anno_pub") && !item.getString().isEmpty()) {
                    ristampe.put("datapub", item.getString());
                } else if (item.isFormField() && fname.equals("editore") && !item.getString().isEmpty()) {
                    ristampe.put("editore", item.getString());
                } else if (item.isFormField() && fname.equals("lingua") && !item.getString().isEmpty()) {
                    ristampe.put("lingua", item.getString());
                } else if (item.isFormField() && fname.equals("indice") && !item.getString().isEmpty()) {
                    ristampe.put("indice", item.getString());
                    //se è stato inserito un pdf salvo il file e inserisco nella mappa i dati
                } else if (!item.isFormField() && fname.equals("PDF")) {
                    String name = item.getName();
                    long size = item.getSize();
                    if (size > 0 && !name.isEmpty()) {
                        File target = new File(getServletContext().getRealPath("") + File.separatorChar + "PDF" + File.separatorChar + name);
                        item.write(target);
                        ristampe.put("download", "PDF" + File.separatorChar + name);
                    }
                    //salvo l'immagine e inserisco i dati nella mappa
                } else if (!item.isFormField() && fname.equals("copertina")) {
                    String name = item.getName();
                    long size = item.getSize();
                    if (size > 0 && !name.isEmpty()) {
                        File target = new File(getServletContext().getRealPath("") + File.separatorChar + "Copertine" + File.separatorChar + name);
                        item.write(target);
                        ristampe.put("copertina", "Copertine" + File.separatorChar + name);
                    }
                }
            }
            ristampe.put("pubblicazioni", idopera);
            return Database.insertRecord("ristampe", ristampe);

        }
        return false;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws java.text.ParseException
     * @throws IOException if an I/O error occurs
     * @throws java.sql.SQLException
     * @throws javax.naming.NamingException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, ServletException, SQLException, NamingException, Exception {

        Map<String, Object> data = new HashMap<String, Object>();
        Database.connect();
        HttpSession s = SecurityLayer.checkSession(request);
        
        int k = DataUtil.getGroup((String) s.getAttribute("username"));
        data.put("gruppo",k);
            
        //controllo sessione e permessi
        if (!isNull(request.getParameter("id"))
                && k != 3) {
            
            int idopera = Integer.parseInt(request.getParameter("id"));

            if (upload(request)) {
                data.put("session", s.getAttribute("username"));
                response.sendRedirect("book-detail?id=" + idopera);
            } else {
                data.put("session", s.getAttribute("username"));
                data.put("idopera", idopera);
                FreeMarker.process("inserimento_ristampa.html", data, response, getServletContext());

            }

        } else {
            action_error(request, response);
        }
        Database.close();
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
        } catch (ParseException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Inserimento_Ristampa.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (ParseException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Inserimento_Ristampa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
