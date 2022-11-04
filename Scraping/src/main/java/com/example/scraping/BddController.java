package com.example.scraping;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BddController {

    @FXML
    TextField nomServeur;

    @FXML
    TextField nomBDD;

    @FXML
    TextField numeroBDD;

    @FXML
    TextField login;

    @FXML
    TextField password;

    @FXML
    Button valider;

    @FXML
    Button fermer;

    @FXML
    Button effacer;

    public void effacer(){                                                                                              //Réinitialise les champs du formulaire
        nomServeur.clear();
        nomBDD.clear();
        numeroBDD.clear();
        login.clear();
        password.clear();
    }

    public void fermer(){                                                                                               //Ferme la fenêtre en cours
        Stage stage = (Stage) fermer.getScene().getWindow();
        stage.close();
    }
}
