package com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The type Enregistrement bdd.
 */
public class EnregistrementBDD {
    private static final String SQL_INSERT = "INSERT INTO recherche (Titre,Description,Prix,id_genre,Annee) VALUES (?,?,?,?,?)";                        //La requête SQL pour insérer des données dans la base

    /**
     * Method used to send data in the database.
     *
     * @param titre       the article's title
     * @param description the article's description
     * @param prix        the article's price
     * @param genre       the article's genre
     * @param annee       the article's year
     */
    public static void saveBDD(String titre,String description, double prix, int genre, int annee){                                                     //Méthode qui se connecte à la BDD et fait la requête SQL
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://109.234.161.28:3306/gretaxao_thibaut-langlais", "gretaxao_thibaut-langlais", "TLanglais2022!");
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, titre);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, prix);
            preparedStatement.setInt(4, genre);
            preparedStatement.setInt(5, annee);
            int row = preparedStatement.executeUpdate();
            System.out.println(row);
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
