package com.example.scraping;

import com.example.util.EnvoiMail;
import com.example.util.Scrap;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

import static com.example.util.EnvoiMail.envoyerMail;

public class VinyleController {

    @FXML
    MenuItem enregistrerFichier;

    @FXML
    MenuItem envoyerMail;

    @FXML
    MenuItem enregistrerBDD;

    @FXML
    MenuItem quitter;

    @FXML
    MenuItem bdd;

    @FXML
    MenuItem modeEmploi;

    @FXML
    ComboBox<String> genre;

    @FXML
    TextField titre;

    @FXML
    DatePicker date;

    @FXML
    TextField prixMin;

    @FXML
    TextField prixMax;

    @FXML
    CheckBox discogs;

    @FXML
    CheckBox fnac;

    @FXML
    CheckBox vinylCorner;

    @FXML
    CheckBox leBonCoin;

    @FXML
    CheckBox mesVinyles;

    @FXML
    CheckBox cultureFactory;

    @FXML
    Button rechercher;

    @FXML
    Button effacer;

    @FXML
    TextArea resultats;

    public void clear(){                                                                                                //Méthode pour effacer les données rentrées dans le formulaire
        titre.clear();
        genre.getSelectionModel().clearSelection();
        refreshComboBox();
        date.setValue(null);
        prixMin.clear();
        prixMax.clear();
        discogs.setSelected(false);
        fnac.setSelected(false);
        vinylCorner.setSelected(false);
        leBonCoin.setSelected(false);
        mesVinyles.setSelected(false);
        cultureFactory.setSelected(false);
    }
    public void refreshComboBox() {                                                                                     //Méthode pour que la comboBox reprenne sa valeur par défaut (Promptext)
        genre.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(genre.getPromptText());
                } else {
                    setText(item);
                }
            }
        });
    }

    public void quitter(){                                                                                              //Ferme la fenêtre principale
        Platform.exit();
    }

    public void onBDDClick() throws IOException {                                                                       //Appelle la méthode qui ouvre une nouvelle fenêtre avec les paramètres de la BDD
        fenetreBDD();
    }

    public void fenetreBDD() throws IOException {                                                                       //Méthode qui ouvre la fenêtre avec les paramètres de la BDD sur laquelle on veut travailler
        FXMLLoader fxmlLoader = new FXMLLoader(VinyleApplication.class.getResource("ihmBDD.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 450);
        Stage stage= new Stage();
        stage.setTitle("Paramètres de la base de données");
        stage.setScene(scene);
        stage.show();
    }

    public void onEnregistrerClick(){                                                                                   //Appelle la méthode qui ouvre la pop up de transmission vers la BDD
        popUpTransmissionBDD();
    }

    public void popUpTransmissionBDD(){                                                                                 //Méthode qui ouvre une pop up afin que l'utilisateur puisse enregistrer des données dans la BDD
        Stage popUpWindow = new Stage ();

        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.setTitle("Transmission BDD");
        Label label1=new Label("Transmission des données à la base de données.");
        Label label2=new Label("Cliquez sur Valider pour lancer la transmission:");
        Button button1= new Button("Valider");
        Button button2= new Button("Annuler");
        button2.setOnAction(e -> popUpWindow.close());
        VBox layout= new VBox(10);
        layout.getChildren().addAll(label1,label2,button1, button2);
        layout.setAlignment(Pos.CENTER);
        Scene scene1= new Scene(layout, 320, 220);
        popUpWindow.setScene(scene1);
        popUpWindow.showAndWait();
    }
    public void onRechercherClick() throws IOException {                                                                //Méthode appelée quand on clique sur le bouton Rechercher
        double min = Double.parseDouble(prixMin.getText());
        double max = Double.parseDouble(prixMax.getText());
        String recherche= titre.getText().toLowerCase();
        String genreChoisi=genre.getValue();
        int annee=30000;
        if (date.getValue()!=null)
            annee = Integer.parseInt(String.valueOf(date.getValue().getYear()));
        String res="RESULTATS DE VOTRE RECHERCHE:\n\n";
        if (discogs.isSelected()){
            res+="DISCOGS: \n\n";
            res+=Scrap.scrapDiscogs(recherche,min,max,annee,genreChoisi);
        }
        if (fnac.isSelected()) {
            res += "FNAC: \n\n";
            res += Scrap.scrapFnac(recherche, min, max,annee);
        }
        if (vinylCorner.isSelected()){
            res+="VINYLCORNER: \n\n";
            res+=Scrap.scrapVinylCorner(recherche,min,max,annee,genreChoisi);
        }
        if (leBonCoin.isSelected()) {                                                                                   //Pour chaque site sélectionné, on appelle la méthode correspondante pour faire le scraping
            res += "LE BON COIN: \n\n";
            res += Scrap.scrapBonCoin(recherche, min, max);
        }
        if (mesVinyles.isSelected()){
            res+="MESVINYLES: \n\n";
            res+=Scrap.scrapMesVinyles(recherche,min,max,annee);
        }
        if (cultureFactory.isSelected()){
            res+="CULTURE FACTORY: \n\n";
            res+=Scrap.scrapCultureFactory(recherche,min,max);
        }
        afficherResultats(res);
    }
    public void afficherResultats(String res){                                                                          //Méthode pour afficher du texte dans la fenêtre de résultats
        resultats.setText(res);
    }

    public void onEnregistrerFichierClick(ActionEvent event) throws FileNotFoundException {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegardez votre recherche");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File fichierResultat = fileChooser.showSaveDialog(stage);
        if (fichierResultat != null) {
            PrintWriter writer = new PrintWriter(fichierResultat);
            writer.print("");
            writer.close();
            enregistrerFicher(resultats.getText(), fichierResultat);
        }
    }

    private void enregistrerFicher(String contenu, File fichier){
        try {
            PrintWriter writer = new PrintWriter(fichier);
            writer.println(contenu);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onMailClick(){                                                                                          //Appelle la méthode qui ouvre la pop up mail
        popUpMail();
    }

    public void popUpMail(){                                                                                            //Méthode qui ouvre une pop up pour l'envoi de mail
        Stage popUpWindow = new Stage ();

        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.setTitle("Envoi des résultats par mail");
        Label label1 = new Label("");
        Label label2=new Label("Veuillez saisir l'adresse mail du destinataire:");
        TextField text1=new TextField();
        text1.setPromptText("votre-mail@mail.com");
        Button button1= new Button("Envoyer");
        button1.setOnAction(e -> {
            try {
                onEnvoyerMailClick(text1.getText());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            label1.setText("Nous avons envoyé un mail à cette adresse.");});
        Button button2= new Button("Fermer");
        button2.setOnAction(e -> popUpWindow.close());
        VBox layout= new VBox(10);
        layout.getChildren().addAll(label1,label2,text1,button1, button2);
        layout.setAlignment(Pos.CENTER);
        Scene scene1= new Scene(layout, 340, 240);
        popUpWindow.setScene(scene1);
        popUpWindow.showAndWait();
    }

    private void onEnvoyerMailClick(String adresse) throws IOException {
        EnvoiMail.save(resultats.getText());
        String nomFichierSortie = "texte"+File.separator+"RechercheVinyles.txt";
        EnvoiMail.envoyerMail(adresse,nomFichierSortie);

    }




}