/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.servlets;

import it.biblio.model.Modifica;
import it.biblio.model.Recensione;
import it.biblio.model.Ristampa;
import it.biblio.utility.DataUtil;
import it.biblio.utility.Database;
import it.biblio.utility.FreeMarker;
import it.biblio.utility.SecurityLayer;
import java.io.IOException;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
 * servlet per i dettagli del libro
 *
 * @author Biblioteca Digitale
 */
public class Bookdetail extends HttpServlet {

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
     * metodo da eseguire se è stata inserita una recensione
     *
     * @param request
     * @throws SQLException
     */
    private void action_recensione(HttpServletRequest request) throws SQLException {
        HttpSession s = SecurityLayer.checkSession(request);
        Map<String, Object> map = new HashMap<String, Object>();
        //Recupera titolo recensione
        String titolo = request.getParameter("titolo");
        //Recupera voto recensione
        int voto = Integer.parseInt(request.getParameter("voto"));

        //Recupera contenuto recensione
        int id_pub = Integer.parseInt(request.getParameter("id"));
        String descrizione = request.getParameter("descrizione");

        map.put("titolo_rec", titolo);
        map.put("descrizione_rec", descrizione);
        map.put("validato", 0);
        map.put("voto", voto);
        map.put("id_pub", id_pub);
        map.put("user", s.getAttribute("userid"));

        Database.insertRecord("recensioni", map);
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
        Database.connect();
        if (s != null && !isNull(request.getParameter("id"))) {
           
            if (!isNull(request.getParameter("titolo")) && !isNull(request.getParameter("voto"))) {
                action_recensione(request);
            }

            ArrayList<Ristampa> lista;
            lista = new ArrayList<Ristampa>();
            Recensione rec;
            List<Recensione> listarec = new ArrayList<Recensione>();
            List<Modifica> listamod = new ArrayList<Modifica>();
            int id = Integer.parseInt(request.getParameter("id"));

            /*     RECUPERO INFORMAZIONI SUL LIBRO    */
            ResultSet rs = Database.selectRecord("pubblicazioni", "id=" + id);

            while (rs.next()) {
                String titolo = rs.getString("titolo");
                String descrizione = rs.getString("descrizione");
                String categoria = rs.getString("categoria");
                double voto = rs.getDouble("voto");
                data.put("titolo", titolo);
                data.put("descrizione", descrizione);
                data.put("categoria", categoria);
                data.put("voto", voto);
            }
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            ResultSet ms = Database.selectRecord("ristampe", "pubblicazioni=" + id, "datapub");
            while (ms.next()) {
                lista.add(new Ristampa(
                        ms.getLong("isbn"),
                        ms.getInt("numpagine"),
                        dateFormat1.format(ms.getDate("datapub")),
                        ms.getString("editore"),
                        ms.getString("lingua"),
                        ms.getString("download"),
                        ms.getInt("pubblicazioni"),
                        ms.getString("indice"),
                        dateFormat2.format(ms.getTimestamp("data_inserimento")),
                        ms.getString("copertina")
                ));

            }

            /* CONTROLLO SE DEVO CAMBIARE LA RISTAMPA IN QUESTIONE
                ALTRIMENTI CARICO L'ULTIMA
             */
            Ristampa temp = null;

            if (!isNull(request.getParameter("isbn"))) {

                for (Ristampa d : lista) {

                    if (d.getIsbn() == Long.parseLong(request.getParameter("isbn"))) {
                        temp = d;

                    }
                }
                lista.remove(temp);
                lista.add(lista.size(), temp);
            }
            /*     RECUPERO MODIFICHE DA METTERE IN LISTA STORYBOARD  */

            ResultSet es = Database.selectRecord("storyboard", "id_pub=" + id);

            while (es.next()) {

                Timestamp timestamp;
                Date date;
                timestamp = es.getTimestamp("time");
                date = new java.util.Date(timestamp.getTime());
                listamod.add(new Modifica(
                        es.getInt("id"),
                        es.getInt("id_utente"),
                        es.getString("descrizione_modifica"),
                        es.getInt("id_pub"),
                        date
                ));
            }

            /*     RECUPERO RECENSIONI DA METTERE IN LISTA    */
            ResultSet cs = Database.selectRecord("recensioni", "id_pub=" + id + " && validato=TRUE");

            while (cs.next()) {

                int id_rec = cs.getInt("id");
                String titolo_rec = cs.getString("titolo_rec");
                String descrizione_rec = cs.getString("descrizione_rec");
                boolean validato = true;
                int voto = cs.getInt("voto");
                int id_pub = id;
                String user = cs.getString("user");

                rec = new Recensione(id_rec, titolo_rec, descrizione_rec, validato, voto, id_pub, user);

                listarec.add(rec);

            }

            /*     RECUPERO CATEGORIE DA METTERE IN LISTA    */
            ResultSet Ws = Database.selectRecord("INFORMATION_SCHEMA.COLUMNS", "TABLE_NAME = 'pubblicazioni' AND COLUMN_NAME = 'categoria'");
            String st = null;
            while (Ws.next()) {
                st = Ws.getString("COLUMN_TYPE");
            }
            List<String> enuml = DataUtil.parsaEnum(st);
            
            
            int k = DataUtil.getGroup((String) s.getAttribute("username"));
            data.put("gruppo",k);
            data.put("lista_categorie", enuml);
            data.put("session", s.getAttribute("username"));
            data.put("listaristampe", lista);
            data.put("listamodifiche", listamod);
            data.put("listarecensioni", listarec);
            FreeMarker.process("book-detail.html", data, response, getServletContext());
            Database.close();

        } else {
            response.sendRedirect("login");
        }
    }

    /* GET */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            processRequest(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Bookdetail.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Bookdetail.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }
    }

    /*POST */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            processRequest(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(Bookdetail.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Bookdetail.class.getName()).log(Level.SEVERE, null, ex);
            action_error(request, response);
        }

    }
}
