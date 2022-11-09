package com.example.scraping;

import com.example.util.ArticleScrap;
import com.example.util.EnregistrementBDD;
import com.example.util.EnvoiMail;
import com.example.util.Scrap;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import java.awt.Desktop;

/**
 * The type Vinyle controller.
 */
public class VinyleController {
    private static ArrayList<ArticleScrap> listeArticles = new ArrayList<ArticleScrap>();

    /**
     * Gets the ArrayList.
     *
     * @return the liste articles
     */
    public static ArrayList<ArticleScrap> getListeArticles() {
        return listeArticles;
    }

    /**
     *Displays every article contained in the ArrayList.
     *
     * @return the string
     */
    public String affichageListe(){
        String res="";
        for (ArticleScrap a: listeArticles){
            res+=a.toString();
        }
        return res;
    }

    /**
     * The MenuItem used to save a file.
     */
    @FXML
    MenuItem enregistrerFichier;

    /**
     * The MenuItem used to send a mail.
     */
    @FXML
    MenuItem envoyerMail;

    /**
     * The MenuItem used to send data to the database.
     */
    @FXML
    MenuItem enregistrerBDD;

    /**
     * The MenuItem used to close the program.
     */
    @FXML
    MenuItem quitter;

    /**
     * The MenuItem used to set the database's parameters.
     */
    @FXML
    MenuItem bdd;

    /**
     * The MenuItem used to open the user guide .
     */
    @FXML
    MenuItem modeEmploi;

    /**
     * The ComboBox in which the user has to select a genre.
     */
    @FXML
    ComboBox<String> genre;

    /**
     * The TextField in which the user enters the title they want to research.
     */
    @FXML
    TextField titre;

    /**
     * The DatePicker used to select a date for the research.
     */
    @FXML
    DatePicker date;

    /**
     * The minimum price the user is ready to pay for the articles they research.
     */
    @FXML
    TextField prixMin;

    /**
     * The maximum price the user is ready to pay for the articles they research.
     */
    @FXML
    TextField prixMax;

    /**
     * The checkBox the user has to tick if they want to search for articles on the Discogs' website.
     */
    @FXML
    CheckBox discogs;

    /**
     * The checkBox the user has to tick if they want to search for articles on the Fnac's website.
     */
    @FXML
    CheckBox fnac;

    /**
     * The checkBox the user has to tick if they want to search for articles on the VinylCorner's website.
     */
    @FXML
    CheckBox vinylCorner;

    /**
     * The checkBox the user has to tick if they want to search for articles on Le Bon Coin's website.
     */
    @FXML
    CheckBox leBonCoin;

    /**
     * The checkBox the user has to tick if they want to search for articles on the MesVinyles' website.
     */
    @FXML
    CheckBox mesVinyles;

    /**
     * The checkBox the user has to tick if they want to search for articles on the Culture Factory's website.
     */
    @FXML
    CheckBox cultureFactory;

    /**
     * The search button.
     */
    @FXML
    Button rechercher;

    /**
     * The clear button.
     */
    @FXML
    Button effacer;

    /**
     * The TextArea used to display the results of the search.
     */
    @FXML
    TextArea resultats;

    /**
     * The Info label used to transmit informations to the user.
     */
    @FXML
    Label info;

    /**
     * The progressBar used to show the user the progress of the current scraping.
     */
    @FXML
    ProgressBar barreProgres;

    /**
     * The clear method used to clear every field in the form.
     */
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

    /**
     * Method used to refresh the value of the ComboBox.
     */
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

    /**
     * Method used to close the main window, and therefore the program.
     */
    public void quitter(){                                                                                              //Ferme la fenêtre principale
        Platform.exit();
    }

    /**
     * Method called when the user clicks on the search button.
     *
     * @throws IOException the io exception
     */
    public void onRechercherClick() throws IOException {                                                                //Méthode appelée quand on clique sur le bouton Rechercher
        if (masqueSaisie()) {
            doTheScrap();
    }}

    /**
     * Method used to do the scraping in a thread.
     */
    @FXML
    public void doTheScrap() {                                                                                          //Méthode qui effectue la recherche dans un thread afin de mettre à jour la barre de progression au fur et à mesure
        Task<Void> task = new Task() {
            @Override
            public Void call() {
                updateProgress(0,1);
                double min = Double.parseDouble(prixMin.getText());
                double max = Double.parseDouble(prixMax.getText());
                String recherche = titre.getText().toLowerCase();
                String genreChoisi = genre.getValue();
                Scrap scrap = new Scrap();
                int nbScrapTotal=nbScrap();
                int nbScrapEffectue=0;
                int annee = 30000;
                if (date.getValue() != null)
                    annee = Integer.parseInt(String.valueOf(date.getValue().getYear()));
                String res = "RESULTATS DE VOTRE RECHERCHE:\n\n";
                if (discogs.isSelected()) {
                    try {
                        scrap.scrapDiscogs(recherche, min, max, annee, genreChoisi);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    nbScrapEffectue++;
                    updateProgress(nbScrapEffectue,nbScrapTotal);
                }
                if (fnac.isSelected()) {
                    try {
                        scrap.scrapFnac(recherche, min, max, annee);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    nbScrapEffectue++;
                    updateProgress(nbScrapEffectue,nbScrapTotal);
                }
                if (vinylCorner.isSelected()) {
                    try {
                        scrap.scrapVinylCorner(recherche, min, max, annee, genreChoisi);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    nbScrapEffectue++;
                    updateProgress(nbScrapEffectue,nbScrapTotal);
                }
                if (leBonCoin.isSelected()) {
                    try {
                        scrap.scrapBonCoin(recherche, min, max);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    nbScrapEffectue++;
                    updateProgress(nbScrapEffectue,nbScrapTotal);
                }
                if (mesVinyles.isSelected()) {
                    try {
                        scrap.scrapMesVinyles(recherche, min, max, annee);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    nbScrapEffectue++;
                    updateProgress(nbScrapEffectue,nbScrapTotal);
                }
                if (cultureFactory.isSelected()) {
                    try {
                        scrap.scrapCultureFactory(recherche, min, max);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    nbScrapEffectue++;
                    updateProgress(nbScrapEffectue,nbScrapTotal);
                }
                afficherResultats(res += affichageListe());
                return null ;
            }
        };
        barreProgres.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }

    /**
     * Method used to count the number of different websites the search scraps.
     *
     * @return an int
     */
    public int nbScrap(){                                                                                               //Méthode qui renvoie le nombre de sites sélectionnés, et donc le nombre de scraping à effectuer
        int nbScrap=0;
        if (discogs.isSelected())
            nbScrap++;
        if (fnac.isSelected())
            nbScrap++;
        if (vinylCorner.isSelected())
            nbScrap++;
        if (leBonCoin.isSelected())
            nbScrap++;
        if (mesVinyles.isSelected())
            nbScrap++;
        if (cultureFactory.isSelected())
            nbScrap++;
        return nbScrap;
    }

    /**
     * Method used to check if the input is of the right type.
     *
     * @return a boolean (yes if the input is correct, false if it's not)
     */
    public boolean masqueSaisie(){                                                                                      //Masque de saisie pour les contenus des champs de prix
        boolean isSaisieOK = true;
        if ((!isNombre(prixMin.getText())) || (!isNombre(prixMax.getText()))){
            info.setText("Veuillez entrer des nombres valides dans les champs de prix.");
            isSaisieOK=false;
        }
            return isSaisieOK;
    }

    /**
     * Method used to check whether a String is a number or not.
     *
     * @param texte the String we want to test
     * @return a boolean (yes if the String is a number, no if it's not)
     */
    public boolean isNombre(String texte){                                                                              //Méthode qui vérifie si le String entré est bien un nombre
        boolean res=true;
        for (int i=0;i<texte.length();i++){
            if (!Character.isDigit(texte.charAt(i)))
                res=false;
        }
        return res;
    }

    /**
     * Method used to display the results of the search in the TextArea.
     *
     * @param res the String we want to display
     */
    public void afficherResultats(String res){                                                                          //Méthode pour afficher du texte dans la fenêtre de résultats
        resultats.setText(res);
    }

    /**
     * Method called when the user clicks on the "save file" button.
     *
     * @param event the event
     * @throws FileNotFoundException the file not found exception
     */
    public void onEnregistrerFichierClick(ActionEvent event) throws FileNotFoundException {                             //Méthode pour enregistrer les résultats sous forme d'un fichier texte
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegardez votre recherche");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File fichierResultat = fileChooser.showSaveDialog(stage);
        if (fichierResultat != null) {
            try {
                PrintWriter writer = new PrintWriter(fichierResultat);
                writer.println(resultats.getText());
                writer.close();
                Desktop d = Desktop.getDesktop();
                d.open(fichierResultat);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method called when the user clicks on the "send mail" button.
     */
    public void onMailClick(){                                                                                          //Appelle la méthode qui ouvre la pop up mail
        popUpMail();
    }

    /**
     * Method that opens the Pop-up window to send a mail.
     */
    public void popUpMail(){                                                                                            //Méthode qui ouvre une pop up pour l'envoi de mail
        Stage popUpWindow = new Stage ();

        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.setTitle("Envoi des résultats par mail");
        Label label1 = new Label("");
        Label label2=new Label("Veuillez saisir l'adresse mail du destinataire:");
        TextField text1=new TextField();
        text1.setPromptText("votre-mail@mail.com");
        Button button1= new Button("Envoyer");
        button1.setOnAction(e -> {                                                                                      // Si l'utilisateur clique sur envoyer, on appelle la méthode qui envoie le mail
            try {
                onEnvoyerMailClick(text1.getText());
            } catch (Exception ex) {
                label1.setText("L'envoi a échoué. Veuillez réessayer.");
                throw new RuntimeException(ex);
            }
            label1.setText("Nous avons envoyé un mail à cette adresse.");});                                            //Message de confirmation si le mail a bien été envoyé
        Button button2= new Button("Fermer");
        button2.setOnAction(e -> popUpWindow.close());
        VBox layout= new VBox(10);
        layout.getChildren().addAll(label1,label2,text1,button1, button2);
        layout.setAlignment(Pos.CENTER);
        Scene scene1= new Scene(layout, 340, 240);
        popUpWindow.setScene(scene1);
        popUpWindow.showAndWait();
    }

    /**
     * Method that calls the methods to save a file and send that file in a mail .
     */
    private void onEnvoyerMailClick(String adresse) throws Exception {                                                  //Méthode qui envoie un mail avec les résultats de la recherche
        EnvoiMail.save(resultats.getText());
        String nomFichierSortie = "texte"+File.separator+"RechercheVinyles.txt";
        EnvoiMail.envoyerMail(adresse,nomFichierSortie);
    }

    /**
     * Method called when the user clicks on the "BDD" MenuItem.
     *
     * @throws IOException the io exception
     */
    public void onBDDClick() throws IOException {                                                                       //Appelle la méthode qui ouvre une nouvelle fenêtre avec les paramètres de la BDD
        fenetreBDD();
    }

    /**
     * Method that opens the window in which the user can set their own database's parameters.
     *
     * @throws IOException the io exception
     */
    public void fenetreBDD() throws IOException {                                                                       //Méthode qui ouvre la fenêtre avec les paramètres de la BDD sur laquelle on veut travailler
        FXMLLoader fxmlLoader = new FXMLLoader(VinyleApplication.class.getResource("ihmBDD.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 450);
        Stage stage= new Stage();
        stage.setTitle("Paramètres de la base de données");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Method called when the user clicks on the "save in BDD" MenuItem.
     */
    public void onEnregistrerClick(){                                                                                   //Appelle la méthode qui ouvre la pop up de transmission vers la BDD
        popUpTransmissionBDD();
    }

    /**
     * Method that opens a Pop-up window to send data in the database.
     */
    public void popUpTransmissionBDD(){                                                                                 //Méthode qui ouvre une pop up afin que l'utilisateur puisse enregistrer des données dans la BDD
        Stage popUpWindow = new Stage ();

        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.setTitle("Transmission BDD");
        Label label1=new Label("Transmission des données à la base de données.");
        Label label2=new Label("Cliquez sur Valider pour lancer la transmission:");
        Button button1= new Button("Valider");
        button1.setOnAction(e -> enregistrerBDD());
        Button button2= new Button("Annuler");
        button2.setOnAction(e -> popUpWindow.close());
        VBox layout= new VBox(10);
        layout.getChildren().addAll(label1,label2,button1, button2);
        layout.setAlignment(Pos.CENTER);
        Scene scene1= new Scene(layout, 320, 220);
        popUpWindow.setScene(scene1);
        popUpWindow.showAndWait();
    }

    /**
     * Method that sends the results of the search to the database.
     */
    @FXML
    private void enregistrerBDD(){                                                                                      //Méthode qui enregistre les résultats de la recherche dans la base de données
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://109.234.161.28:3306/gretaxao_thibaut-langlais", "gretaxao_thibaut-langlais", "TLanglais2022!");)
        { for (ArticleScrap a: this.listeArticles){
            EnregistrementBDD.saveBDD(a.getTitre(),a.getDescription(),a.getPrix(),a.getGenre(),a.getAnnee());
        }
        info.setText("Les données ont bien été transmises à la base.");}
        catch (SQLException e) {
            info.setText("La transmission des données a échoué.Veuillez réessayer.");
            throw new RuntimeException(e);
        }
    }
}