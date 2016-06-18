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
import java.sql.ResultSet;
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
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * servlet per l'inserimento della pubblicazione
 *
 * @author Biblioteca Digitale
 */
@MultipartConfig(maxFileSize = 50177215)    // upload file's size up to 50MB

public class Inserimento_pub extends HttpServlet {

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
     * metodo per gestire l'upload di file e inserimento pubblicazione con prima
     * ristampa
     *
     * @param request
     * @param response
     * @param k
     * @return
     * @throws IOException
     */
    private boolean upload(HttpServletRequest request) throws IOException, Exception {

        Map<String, Object> pub = new HashMap<String, Object>();
        Map<String, Object> ristampe = new HashMap<String, Object>();
        Map<String, Object> keyword = new HashMap<String, Object>();
        HttpSession s = SecurityLayer.checkSession(request);
        if (ServletFileUpload.isMultipartContent(request)) {
            FileItemFactory fif = new DiskFileItemFactory();
            ServletFileUpload sfo = new ServletFileUpload(fif);
            List<FileItem> items = sfo.parseRequest(request);
            for (FileItem item : items) {
                String fname = item.getFieldName();
                if (item.isFormField() && fname.equals("titolo") && !item.getString().isEmpty()) {
                    pub.put("titolo", item.getString());
                    pub.put("utente", s.getAttribute("userid"));
                } else if (item.isFormField() && fname.equals("descrizione") && !item.getString().isEmpty()) {
                    pub.put("descrizione", item.getString());
                } else if (item.isFormField() && fname.equals("autore") && !item.getString().isEmpty()) {
                    pub.put("autore", item.getString());

                } else if (item.isFormField() && fname.equals("categoria") && !item.getString().isEmpty()) {
                    pub.put("categoria", item.getString());

                } else if (item.isFormField() && fname.equals("ISBN") && !item.getString().isEmpty()) {
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
                } else if (item.isFormField() && fname.equals("keyword") && !item.getString().isEmpty()) {
                    keyword.put("tag1", item.getString());
                } else if (item.isFormField() && fname.equals("keyword2") && !item.getString().isEmpty()) {
                    keyword.put("tag2", item.getString());
                } else if (item.isFormField() && fname.equals("keyword3") && !item.getString().isEmpty()) {
                    keyword.put("tag3", item.getString());
                } else if (!item.isFormField() && fname.equals("PDF")) {
                    String name = item.getName();
                    long size = item.getSize();
                    if (size > 0 && !name.isEmpty()) {
                        File target = new File(getServletContext().getRealPath("") + File.separatorChar + "PDF" + File.separatorChar + name);
                        item.write(target);
                        ristampe.put("download", "PDF" + File.separatorChar + name);
                    }
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

            Database.insertRecord("keyword", keyword);
            ResultSet ss = Database.selectRecord("keyword", "tag1='" + keyword.get("tag1") + "' && " + "tag2='" + keyword.get("tag2") + "' && tag3='" + keyword.get("tag3") + "'");
            if (!isNull(ss)) {
                int indicek = 0;
                while (ss.next()) {
                    indicek = ss.getInt("id");
                }
                pub.put("keyword", indicek);
                Database.insertRecord("pubblicazioni", pub);
                ResultSet rs = Database.selectRecord("pubblicazioni", "titolo='" + pub.get("titolo") + "'");
                while (rs.next()) {
                    ristampe.put("pubblicazioni", rs.getInt("id"));
                }
                return Database.insertRecord("ristampe", ristampe);
            } else {
                return false;
            }

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
        Map data = new HashMap();
        Database.connect();
        HttpSession s = SecurityLayer.checkSession(request);
        if (s != null && DataUtil.getGroup((String) s.getAttribute("username")) != 3) {
            
            int k = DataUtil.getGroup((String) s.getAttribute("username"));
            data.put("gruppo",k);
            
            ResultSet rs = Database.selectRecord("INFORMATION_SCHEMA.COLUMNS", "TABLE_NAME = 'pubblicazioni' AND COLUMN_NAME = 'categoria'");
            String st = null;
            while (rs.next()) {
                st = rs.getString("COLUMN_TYPE");
            }
            List<String> enuml = DataUtil.parsaEnum(st);
            data.put("lista_categorie", enuml);

            if (upload(request)) {
                data.put("session", s.getAttribute("username"));
                FreeMarker.process("gestionesistema.html", data, response, getServletContext());

            } else {
                data.put("session", s.getAttribute("username"));
                FreeMarker.process("inserimento_pub.html", data, response, getServletContext());

                Database.close();
            }

        } else {
            action_error(request, response);
            Database.close();
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
        } catch (ParseException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (Exception ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (ParseException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (Exception ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }
    }

}
