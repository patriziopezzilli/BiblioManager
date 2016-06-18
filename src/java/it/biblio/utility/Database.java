package it.biblio.utility;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * classe per la gestione del DB
 *
 * @author Biblioteca Digitale
 */
public class Database {

    private static Connection db;

    /**
     * Connessione al database
     *
     * @throws javax.naming.NamingException
     * @throws java.sql.SQLException
     */
    public static void connect() throws NamingException, SQLException {
        InitialContext ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("java:comp/env/BiblioManager");
        Database.db = ds.getConnection();
    }

    /**
     * Chiusura connessione al database
     *
     * @throws java.sql.SQLException
     */
    public static void close() throws SQLException {
        Database.db.close();
    }

    /**
     * Select record con condizione
     *
     * @param table tabella da cui prelevare i dati
     * @param condition condizione per il filtro dei dati
     * @return dati prelevati
     * @throws java.sql.SQLException
     */
    public static ResultSet selectRecord(String table, String condition) throws SQLException {
        // Generazione query
        String query = "SELECT * FROM " + table + " WHERE " + condition;
        // Esecuzione query
        return Database.executeQuery(query);
    }

    /**
     * Select record con condizione e ordinamento
     *
     * @param table tabella da cui prelevare i dati
     * @param condition condizione per il filtro dei dati
     * @param order ordinamento dei dati
     * @return dati prelevati
     * @throws java.sql.SQLException
     */
    public static ResultSet selectRecord(String table, String condition, String order) throws SQLException {
        // Generazione query
        String query = "SELECT * FROM " + table + " WHERE " + condition + " ORDER BY " + order;
        // Esecuzione query
        return Database.executeQuery(query);
    }

    /**
     * select record con condizione,ordinamento, indice di partenza e quantità
     * di record nel risultato
     *
     * @param table
     * @param condition
     * @param order
     * @param start
     * @param quantity
     * @return
     * @throws SQLException
     */
    public static ResultSet selectRecordLimitxy(String table, String condition, String order, int start, int quantity) throws SQLException {
        // Generazione query
        String query = "SELECT * FROM " + table + " WHERE " + condition + " ORDER BY " + order + " LIMIT " + start + "," + quantity;
        // Esecuzione query
        return Database.executeQuery(query);
    }

    /**
     * Select record con join tra due tabelle
     *
     * @param table_1 nome della prima tabella
     * @param table_2 nome della seconda tabella
     * @param join_condition condizione del join tra la tabelle
     * @param where_condition condizione per il filtro dei dati
     * @return dati prelevati
     * @throws java.sql.SQLException
     */
    public static ResultSet selectJoin(String table_1, String table_2, String join_condition, String where_condition) throws SQLException {
        // Generazione query
        String query = "SELECT * FROM " + table_1 + " JOIN " + table_2 + " ON " + join_condition + " WHERE " + where_condition;
        // Esecuzione query
        return Database.executeQuery(query);
    }

    /**
     * Select record con join tra due tabelle e ordinamento
     *
     * @param table_1 nome della prima tabella
     * @param table_2 nome della seconda tabella
     * @param join_condition condizione del join tra la tabelle
     * @param where_condition condizione per il filtro dei dati
     * @param order ordinamento dei dati
     * @return dati prelevati
     * @throws java.sql.SQLException
     */
    public static ResultSet selectJoin(String table_1, String table_2, String join_condition, String where_condition, String order) throws SQLException {
        // Generazione query
        String query = "SELECT * FROM " + table_1 + " JOIN " + table_2 + " ON " + join_condition + " WHERE " + where_condition + "ORDER BY" + order;
        // Esecuzione query
        return Database.executeQuery(query);
    }

    /**
     * Insert record
     *
     * @param table tabella in cui inserire i dati
     * @param data dati da inserire
     * @return dati prelevati
     * @throws java.sql.SQLException
     */
    public static boolean insertRecord(String table, Map<String, Object> data) throws SQLException {
        // Generazione query
        String query = "INSERT INTO " + table + " SET ";
        Object value;
        String attr;

        for (Map.Entry<String, Object> e : data.entrySet()) {
            attr = e.getKey();
            value = e.getValue();
            if (value instanceof Integer) {
                query = query + attr + " = " + value + ", ";
            } else {
                value = value.toString().replace("\'", "\\'");
                query = query + attr + " = '" + value + "', ";
            }
        }
        query = query.substring(0, query.length() - 2);
        // Esecuzione query
        return Database.updateQuery(query);
    }

    /**
     * query personalizzata per la ricerca, vede se una stringa è contenuta
     * all'inizio, all'interno, o alla fine dei campi isbn,titolo,lingua,
     * editore,tag1,tag2,tag3
     *
     * @param parameter
     * @return
     * @throws SQLException
     */
    public static ResultSet selectRicerca(String parameter) throws SQLException {
        // Generazione query
        String query = "SELECT pubblicazioni.id,titolo,autore,descrizione,categoria,voto,pubblicazioni.keyword FROM "
                + "ristampe JOIN pubblicazioni ON ristampe.pubblicazioni=pubblicazioni.id "
                + "JOIN keyword ON pubblicazioni.keyword=keyword.id "
                + "WHERE isbn LIKE '%" + parameter + "%' || titolo LIKE '%" + parameter + "%' || lingua LIKE '%" + parameter + "%' "
                + "|| editore LIKE '%" + parameter + "%' || tag1 LIKE '%" + parameter + "%' || tag2 LIKE '%" + parameter + "%' || tag3 LIKE '%" + parameter + "%' "
                + "GROUP BY pubblicazioni.id";
        // Esecuzione query
        return Database.executeQuery(query);
    }

    /**
     * query personalizzata per la ricerca, vede se una stringa è contenuta
     * all'inizio, all'interno, o alla fine dei campi isbn,titolo,lingua,
     * editore,tag1,tag2,tag3 limitando il risultato a partire dall 'indice
     * start e per la quantità quantity
     *
     * @param parameter
     * @param start
     * @param quantity
     * @return
     * @throws SQLException
     */
        public static ResultSet selectRicercaLimitxy(String parameter,String ordine, int start, int quantity) throws SQLException{
        // Generazione query
         String query = "SELECT pubblicazioni.id,titolo,autore,descrizione,categoria,voto,pubblicazioni.keyword FROM "
                 + "ristampe JOIN pubblicazioni ON ristampe.pubblicazioni=pubblicazioni.id "
                 +"JOIN keyword ON pubblicazioni.keyword=keyword.id "
                + "WHERE isbn LIKE '%"+parameter+"%' || titolo LIKE '%"+parameter+"%' || lingua LIKE '%"+parameter+"%' "
                 + "|| editore LIKE '%"+parameter+"%' || tag1 LIKE '%"+parameter+"%' || tag2 LIKE '%"+parameter+"%' || tag3 LIKE '%"+parameter+"%' "
                 + "GROUP BY pubblicazioni.id ORDER BY "+ordine+ " LIMIT "+start+","+quantity;
        // Esecuzione query
        return Database.executeQuery(query);
    }

    /**
     * join a due tabelle con ordinamento e limite
     *
     * @param table_1
     * @param table_2
     * @param join_condition
     * @param order
     * @param limit
     * @return
     * @throws SQLException
     */
    public static ResultSet selectJoinLimit(String table_1, String table_2, String join_condition, String order, int limit) throws SQLException {
        // Generazione query
        String query = "SELECT * FROM " + table_1 + " JOIN " + table_2 + " ON " + join_condition + " ORDER BY " + order + " LIMIT " + limit;
        // Esecuzione query
        return Database.executeQuery(query);
    }

    /**
     * join a due tabelle con ordinamento, raggruppamento e limite
     *
     * @param table_1
     * @param table_2
     * @param join_condition
     * @param groupby
     * @param order
     * @param limit
     * @return
     * @throws SQLException
     */
    public static ResultSet selectJoinGroupbyLimit(String table_1, String table_2, String join_condition, String groupby, String order, int limit) throws SQLException {
        // Generazione query
        String query = "SELECT * FROM " + table_1 + " JOIN " + table_2 + " ON " + join_condition + " GROUP BY " + groupby + " ORDER BY " + order + " LIMIT " + limit;
        // Esecuzione query
        return Database.executeQuery(query);
    }

    /**
     * Update record
     *
     * @param table tabella in cui aggiornare i dati
     * @param data dati da inserire
     * @param condition condizione per il filtro dei dati
     * @return true se l'inserimento è andato a buon fine, false altrimenti
     * @throws java.sql.SQLException
     */
    public static boolean updateRecord(String table, Map<String, Object> data, String condition) throws SQLException {
        // Generazione query
        String query = "UPDATE " + table + " SET ";
        Object value;
        String attr;

        for (Map.Entry<String, Object> e : data.entrySet()) {
            attr = e.getKey();
            value = e.getValue();
            if (value instanceof String) {
                value = value.toString().replace("\'", "\\'");
                query = query + attr + " = '" + value + "', ";
            } else {
                query = query + attr + " = " + value + ", ";
            }

        }
        query = query.substring(0, query.length() - 2) + " WHERE " + condition;

        // Esecuzione query
        return Database.updateQuery(query);
    }

    /**
     * Delete record
     *
     * @param table tabella in cui eliminare i dati
     * @param condition condizione per il filtro dei dati
     * @return true se l'eliminazione è andata a buon fine, false altrimenti
     * @throws java.sql.SQLException
     */
    public static boolean deleteRecord(String table, String condition) throws SQLException {
        // Generazione query
        String query = "DELETE FROM " + table + " WHERE " + condition;
        // Esecuzione query
        return Database.updateQuery(query);
    }

    /**
     * Count record
     *
     * @param table tabella in cui contare i dati
     * @param condition condizione per il filtro dei dati
     * @param order
     * @return numero dei record se la query è stata eseguita on successo, -1
     * altrimenti
     * @throws java.sql.SQLException
     */
    public static int countRecord(String table, String condition, String order) throws SQLException {

        // Generazione query
        String query = "SELECT COUNT(*) FROM " + table + " WHERE " + condition + " ORDER BY " + order;
        // Esecuzione query
        ResultSet record = Database.executeQuery(query);
        record.next();
        // Restituzione del risultato
        return record.getInt(1);

    }

    public static int countRicerca(String parameter) throws SQLException {
        // Generazione query
        String query = "SELECT COUNT(*) FROM "
                + "ristampe JOIN pubblicazioni ON ristampe.pubblicazioni=pubblicazioni.id "
                + "JOIN keyword ON pubblicazioni.keyword=keyword.id "
                + "WHERE isbn LIKE '%" + parameter + "%' || titolo LIKE '%" + parameter + "%' || lingua LIKE '%" + parameter + "%' "
                + "|| editore LIKE '%" + parameter + "%' || tag1 LIKE '%" + parameter + "%' || tag2 LIKE '%" + parameter + "%' || tag3 LIKE '%" + parameter + "%' "
                + "GROUP BY pubblicazioni.id";
        // Esecuzione query
        ResultSet record = Database.executeQuery(query);
        int contatore = 0;
        while (record.next()) {
            contatore++;
        }
        // Restituzione del risultato
        return contatore;
    }

    /**
     * Imposta a NULL un attributo di una tabella
     *
     * @param table tabella in cui è presente l'attributo
     * @param attribute attributo da impostare a NULL
     * @param condition condizione
     * @return
     * @throws java.sql.SQLException
     */
    public static boolean resetAttribute(String table, String attribute, String condition) throws SQLException {
        String query = "UPDATE " + table + " SET " + attribute + " = NULL WHERE " + condition;
        return Database.updateQuery(query);
    }

    // <editor-fold defaultstate="collapsed" desc="Metodi ausiliari.">
    /**
     * executeQuery personalizzata
     *
     * @param query query da eseguire
     */
    private static ResultSet executeQuery(String query) throws SQLException {

        Statement s1 = Database.db.createStatement();
        ResultSet records = s1.executeQuery(query);

        return records;

    }

    /**
     * updateQuery personalizzata
     *
     * @param query query da eseguire
     */
    private static boolean updateQuery(String query) throws SQLException {

        Statement s1;

        s1 = Database.db.createStatement();
        s1.executeUpdate(query);
        s1.close();
        return true;

    }
    // </editor-fold>

}
