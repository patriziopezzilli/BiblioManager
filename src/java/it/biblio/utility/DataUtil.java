/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.biblio.utility;

import it.biblio.model.Utente;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import static java.util.Objects.isNull;

/**
 * classe per le utility.Notare che chi utilizza i metodi statici di questa
 * classe deve aver già effettuato la connessione al DB
 *
 * @author Biblioteca Digitale
 */
public class DataUtil {

    /**
     * metodo statico per sapere il gruppo di un utente dalla sua email
     *
     * @param email
     * @return
     * @throws java.sql.SQLException
     */
    public static int getGroup(String email) throws SQLException {
        {
            int st = 0;

            String condition = "usermail=" + "'" + email + "'";

            ResultSet rs = Database.selectRecord("utenti", condition);
            while (rs.next()) {
                st = rs.getInt("gruppi");
            }

            return st;
        }
    }

    /**
     * metodo per controllare l'identità dell'utente
     *
     * @param email
     * @param pass
     * @return
     * @throws java.sql.SQLException
     */
    public static int checkUser(String email, String pass) throws SQLException {
        int st = 0;

        if (!isNull(pass)) {
            pass = crypt(pass);
        }

        String condition = "usermail='" + email + "' AND password='" + pass + "'";

        ResultSet rs = Database.selectRecord("utenti", condition);
        while (rs.next()) {
            st = rs.getInt("id");
        }

        return st;
    }

    /**
     * metodo per ottenere un utente sapendo la sua email
     *
     * @param email
     * @return
     * @throws java.sql.SQLException
     */
    public static Utente getUser(String email) throws SQLException {
        Utente utente = null;

        ResultSet rs = Database.selectRecord("utenti", "usermail=" + email);

        while (rs.next()) {

            String usermail = rs.getString("usermail");
            String nome = rs.getString("nome");
            String cognome = rs.getString("cognome");
            String citta = rs.getString("citta");
            Date annonascita = rs.getDate("annonascita");
            int gruppi = rs.getInt("gruppi");

            utente = new Utente(usermail, nome, cognome, citta, annonascita, gruppi);
        }

        return utente;
    }

    /**
     * Cripta una stringa
     *
     * @param string stringa da criptare
     * @return stringa criptata
     */
    public static String crypt(String string) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            byte[] passBytes = string.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digested.length; i++) {
                sb.append(Integer.toHexString(0xff & digested[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }

    }

    /**
     * metodo per parsare un campo enum da un Resultset e ritornarlo come lista
     * di stringhe
     *
     * @param s
     * @return
     */
    public static List<String> parsaEnum(String s) {
        s = s.substring(6, s.length() - 2);
        String[] tokens = s.split("','");
        List<String> temp = Arrays.asList(tokens);
        return temp;
    }

}
