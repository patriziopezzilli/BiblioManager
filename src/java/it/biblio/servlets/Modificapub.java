/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.servlets;

import it.biblio.model.Keyword;
import it.biblio.model.Pub;
import it.biblio.model.Ristampa;
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
 * servlet per la modifica della pubblicazione
 *
 * @author Biblioteca Digitale
 */
public class Modificapub extends HttpServlet {

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
     * metodo per gestire l'upload di file e inserimento dati
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean upload(HttpServletRequest request) throws IOException, SQLException, Exception {
        HttpSession s = SecurityLayer.checkSession(request);

        Map<String, Object> pub = new HashMap<String, Object>();
        Map<String, Object> ristampe = new HashMap<String, Object>();
        Map<String, Object> keyword = new HashMap<String, Object>();
        Map<String, Object> storyboard = new HashMap<String, Object>();

        if (ServletFileUpload.isMultipartContent(request)) {
            FileItemFactory fif = new DiskFileItemFactory();
            ServletFileUpload sfo = new ServletFileUpload(fif);
            List<FileItem> items = sfo.parseRequest(request);
            for (FileItem item : items) {
                String fname = item.getFieldName();
                if (item.isFormField() && fname.equals("titolo") && !item.getString().isEmpty()) {
                    pub.put("titolo", item.getString());
                } else if (item.isFormField() && fname.equals("autore") && !item.getString().isEmpty()) {
                    pub.put("Autore", item.getString());
                } else if (item.isFormField() && fname.equals("descrizione") && !item.getString().isEmpty()) {
                    pub.put("descrizione", item.getString());
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
                } else if (item.isFormField() && fname.equals("idkey") && !item.getString().isEmpty()) {
                    keyword.put("id", item.getString());
                } else if (item.isFormField() && fname.equals("idpub") && !item.getString().isEmpty()) {
                    pub.put("id", item.getString());
                } else if (item.isFormField() && fname.equals("idris") && !item.getString().isEmpty()) {
                    ristampe.put("isbn", item.getString());
                } else if (item.isFormField() && fname.equals("modifica") && !item.getString().isEmpty()) {
                    storyboard.put("descrizione_modifica", item.getString());
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

            storyboard.put("id_utente", s.getAttribute("userid"));
            storyboard.put("id_pub", pub.get("id"));

            if (Database.updateRecord("keyword", keyword, "id=" + keyword.get("id"))) {

                Database.updateRecord("pubblicazioni", pub, "id=" + pub.get("id"));
                Database.insertRecord("storyboard", storyboard);
                Database.updateRecord("ristampe", ristampe, "isbn=" + ristampe.get("isbn"));

                return true;
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
        int pub;
        Ristampa ristampa;
        Pub pubblicazione;
        Keyword keyw;
        int keyword = 0;
        Map data = new HashMap();
        Database.connect();
        HttpSession s = SecurityLayer.checkSession(request);
        
        int k = DataUtil.getGroup((String) s.getAttribute("username"));
        data.put("gruppo",k);
            
        if (k != 3) {
            if (upload(request)) {
                data.put("session", s.getAttribute("username"));
                System.out.println(request.getParameter(("id")));
                response.sendRedirect("book-detail?id=" + request.getParameter("id"));
            } else {

                /* GET dati from DB */
                pub = Integer.parseInt(request.getParameter("id"));
                ResultSet risultato_pub = Database.selectRecord("pubblicazioni", "id=" + pub);

                int id = 0;
                String titolo = null;
                String descrizione = null;
                String categoria = null;
                String autore = null;
                int isbn = 0;
                int num_pagine = 0;
                String datapub = null;
                String editore = null;
                String lingua = null;
                String indice = null;
                int pubblicazioni = 0;
                String key1 = null;
                String key2 = null;
                String key3 = null;
                //riempio i dati dell'oggetto pubblicazione
                while (risultato_pub.next()) {

                    id = risultato_pub.getInt("id");
                    titolo = risultato_pub.getString("titolo");
                    autore = risultato_pub.getString("autore");

                    descrizione = risultato_pub.getString("descrizione");
                    categoria = risultato_pub.getString("categoria");
                    keyword = risultato_pub.getInt("keyword");

                }

                pubblicazione = new Pub(id, titolo, autore, descrizione, categoria, 0, keyword);
                data.put("pubblicazione", pubblicazione);

                ResultSet risultato_ris = Database.selectRecord("ristampe", "pubblicazioni=" + pub);
                //riempio i dati dell'oggetto ristampa
                while (risultato_ris.next()) {

                    isbn = (int) risultato_ris.getLong("isbn");
                    num_pagine = risultato_ris.getInt("numpagine");
                    datapub = risultato_ris.getString("datapub");
                    editore = risultato_ris.getString("editore");
                    lingua = risultato_ris.getString("lingua");
                    indice = risultato_ris.getString("indice");
                    pubblicazioni = pub;

                }

                ristampa = new Ristampa(isbn, num_pagine, datapub, editore, lingua, pubblicazioni, indice);
                data.put("ristampa", ristampa);

                ResultSet risultato_key = Database.selectRecord("keyword", "id=" + keyword);
                //riempio i dati dell'oggetto keyword
                while (risultato_key.next()) {

                    key1 = risultato_key.getString("tag1");
                    key2 = risultato_key.getString("tag2");
                    key3 = risultato_key.getString("tag3");

                }

                keyw = new Keyword(keyword, key1, key2, key3);
                data.put("keyword", keyw);

                ResultSet rs = Database.selectRecord("INFORMATION_SCHEMA.COLUMNS", "TABLE_NAME = 'pubblicazioni' AND COLUMN_NAME = 'categoria'");
                String st = null;
                while (rs.next()) {
                    st = rs.getString("COLUMN_TYPE");
                }
                List<String> enuml = DataUtil.parsaEnum(st);

                data.put("lista_categorie", enuml);

                data.put("session", s.getAttribute("username"));
                FreeMarker.process("modificapub.html", data, response, getServletContext());
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
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Inserimento_pub.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (Exception ex) {
            Logger.getLogger(Modificapub.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Modificapub.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }
    }

}
