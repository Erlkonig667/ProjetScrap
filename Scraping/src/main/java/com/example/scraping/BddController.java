package com.example.scraping;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The type Bdd controller.
 */
public class BddController {

    /**
     * The server's name.
     */
    @FXML
    TextField nomServeur;

    /**
     * The database's name.
     */
    @FXML
    TextField nomBDD;

    /**
     * The port number to access the database.
     */
    @FXML
    TextField numeroBDD;

    /**
     * The Login to access the database.
     */
    @FXML
    TextField login;

    /**
     * The Password to access the database.
     */
    @FXML
    TextField password;

    /**
     * The confirm button.
     */
    @FXML
    Button valider;

    /**
     * The close button.
     */
    @FXML
    Button fermer;

    /**
     * The clear button to reset the form's fields.
     */
    @FXML
    Button effacer;

    /**
     * The Test connexion button.
     */
    @FXML
    Button testConnexion;

    /**
     * The Label 1 used to transmit informations to the user.
     */
    @FXML
    Label label1;

    /**
     * Method used to clear every field in the form.
     */
    public void effacer(){                                                                                              //Réinitialise les champs du formulaire
        nomServeur.clear();
        nomBDD.clear();
        numeroBDD.clear();
        login.clear();
        password.clear();
    }

    /**
     * Method used to close the current window.
     */
    public void fermer(){                                                                                               //Ferme la fenêtre en cours
        Stage stage = (Stage) fermer.getScene().getWindow();
        stage.close();
    }

    /**
     * Method used to test the connexion to the database.
     */
    public void testerConnexion(){                                                                                      //Méthode qui teste la connection à la base de données avec les infos renseignées par l'utilisateur
        String nomServ=nomServeur.getText();
        String nomBase=nomBDD.getText();
        String numPort=numeroBDD.getText();
        String log = login.getText();
        String mdp=password.getText();
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://"+nomServ+":"+numPort+"/"+nomBase, log, mdp);)
        {label1.setText("Vous êtes bien connecté(e) à votre base de données.");}
        catch (SQLException e) {
            label1.setText("La connexion a échoué.Veuillez réessayer.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Method used to save the database's parameters in a textFile
     *
     * @throws IOException the io exception
     */
    public void enregistrementInfosConnexion() throws IOException {
        String adresseIP=nomServeur.getText();
        String numeroPort=numeroBDD.getText();
        String nomBase=nomBDD.getText();
        String log =login.getText();
        String mdp =password.getText();
        String nomFichierSortie = "texte"+ File.separator+"infosConnexion.txt";
        PrintWriter ecrire;
        ecrire =  new PrintWriter(new BufferedWriter(new FileWriter(nomFichierSortie)));
        ecrire.println(adresseIP+"\n"+numeroPort+"\n"+nomBase+"\n"+log+"\n"+mdp);
        ecrire.close();
        Stage stage = (Stage) fermer.getScene().getWindow();
        stage.close();
    }
}
