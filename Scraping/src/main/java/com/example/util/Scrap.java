package com.example.util;

import com.example.scraping.VinyleController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Scrap.
 */
public class Scrap {
    /**
     * Method used to scrap the Le Bon Coin's website.
     *
     * @param titre the title we are searching for
     * @param min   the minimum price
     * @param max   the maximum price
     * @throws IOException the io exception
     */
    public void scrapBonCoin(String titre, double min, double max) throws IOException {                                 //Méthode qui effectue le scraping du site Le Bon Coin
        String url = "https://www.leboncoin.fr/recherche?category=26&text=" + titre + "&price=" + min + "-" + max;
        WebClient webClient = new WebClient();

        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage htmlPage = webClient.getPage(url);

        List<HtmlAnchor> article = htmlPage.getByXPath("//p");
        for (HtmlElement e : article) {
            String value = e.getTextContent();
            if (value.toLowerCase().contains(titre.toLowerCase())) {                                                    //On clique sur l'article seulement si son intitulé contient notre recherche (afin d'éviter les annonces/pub indésirables)
                HtmlPage page2 = e.click();
                List<HtmlElement> description = page2.getByXPath("//div[5]/div/div/p");
                List<HtmlElement> prix = page2.getByXPath("//div[3]/div/span/div/div[1]/div/span");
                for (HtmlElement a : prix) {
                    String valPrix = a.getTextContent();
                    Double prixFinal = stringToDouble(valPrix);
                    for (HtmlElement b : description) {
                        String descri = b.getTextContent();
                        String lien = String.valueOf(page2.getUrl());
                        ArticleScrap article1 = new ArticleScrap(value,genreStringToInt("divers"),0,prixFinal,descri,lien);
                        VinyleController.getListeArticles().add(article1);
                    }
                }
            }
        }
    }

    /**
     * Method used to scrap the Fnac's website.
     *
     * @param titre the title we are searching for
     * @param min   the minimum price
     * @param max   the maximum price
     * @param annee the year
     * @throws IOException the io exception
     */
    public void scrapFnac(String titre, double min, double max, int annee) throws IOException {                         //Méthode qui effectue le scraping du site Fnac
        String url = "https://www.fnac.com/SearchResult/ResultList.aspx?SCat=0&Search="+titre;
        WebClient webClient = new WebClient();

        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage htmlPage = webClient.getPage(url);
        System.out.println("Je suis dans le site "+htmlPage.getUrl());

        List<HtmlAnchor> article = htmlPage.getByXPath("//article/form/div[2]/div/p[1]/span/a");
        for (HtmlElement e : article) {
            String value = e.getTextContent();
            HtmlPage page2 = e.click();
            System.out.println("Je suis dans l'article "+ page2.getUrl());
            List<HtmlElement> description = page2.getByXPath("//div[2]/div/div[1]/div[2]/div[2]/div[1]/div[2]/div");
            List<HtmlElement> dateParution=page2.getByXPath("//div[2]/dl[2]/dd/p");
            List<HtmlElement> prix = page2.getByXPath(".//span[contains(@class,'userPrice')]");
            String anneeParutionStr=dateParution.get(0).getTextContent();
            System.out.println("Je suis dans la date "+anneeParutionStr);
            String anneeParutionStr1= anneeParutionStr.replaceAll("[^0-9]", "");
            System.out.println("année: "+anneeParutionStr1);
            int anneeParutionInt = Integer.parseInt(anneeParutionStr1);
            if (anneeParutionInt<=annee) {
                System.out.println("La condition de date est remplie ");
                String valPrix = prix.get(0).getTextContent().trim();
                System.out.println(valPrix);
                double prixFinal = stringToDouble(valPrix);
                if ((prixFinal <= max) && (prixFinal >= min)) {
                    System.out.println("Toutes les conditions sont remplies, on prend");
                    String descri="";
                    if (!description.isEmpty()) {
                        descri = description.get(0).getTextContent();
                    }
                    String lien = String.valueOf(page2.getUrl());
                    ArticleScrap article1= new ArticleScrap(value,genreStringToInt("divers"),anneeParutionInt,prixFinal,descri,lien);
                    VinyleController.getListeArticles().add(article1);
                }
            }
        }
    }

    /**
     * Method used to scrap the Vinyl Corner's website.
     *
     * @param titre          the title we are searching for
     * @param min            the minimum price
     * @param max            the maximum price
     * @param annee          the year
     * @param genreRecherche the genre we are searching for
     * @throws IOException the io exception
     */
    public void scrapVinylCorner(String titre, double min, double max,int annee, String genreRecherche) throws IOException {        //Méthode qui effectue le scraping du site VinylCorner
        String url = "https://www.vinylcorner.fr/catalogsearch/result/?q="+titre+"&category=" + genreVinylCorner(genreRecherche);
        WebClient webClient = new WebClient();

        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage htmlPage = webClient.getPage(url);
        System.out.println("Je suis dans le site " + htmlPage.getUrl());
        List<HtmlAnchor> article = htmlPage.getByXPath("//div/div[2]/strong/a");
        for (HtmlElement e : article) {
            String value = e.getTextContent().trim();
            HtmlPage page2 = e.click();
            System.out.println("Je suis dans l'article " + value + " Lien: " + page2.getUrl());
            List<HtmlElement> description = page2.getByXPath(".//span[contains(@itemprop,'description')]");
            List<HtmlElement> dateParution = page2.getByXPath("//div[1]/div[2]/p[2]");
            List<HtmlElement> prix = page2.getByXPath("//div[4]/div/span/span/span");
            for (HtmlElement a : dateParution) {
                String anneeParutionStr = a.getTextContent();
                anneeParutionStr = anneeParutionStr.substring(anneeParutionStr.length()-4);
                System.out.println("Je suis dans la date " + anneeParutionStr);
                int anneeParutionInt = Integer.parseInt(anneeParutionStr);
                if (anneeParutionInt <= annee) {
                    System.out.println("La condition de date est remplie " + page2.getUrl());
                    for (HtmlElement p : prix) {
                        String valPrix = p.getTextContent();
                        System.out.println(valPrix);
                        double prixFinal = stringToDouble(valPrix);
                        if ((prixFinal <= max) && (prixFinal >= min)) {
                            System.out.println("Toutes les conditions sont remplies, on prend");
                            String descri = "";
                            if (!description.isEmpty())
                                descri = description.get(0).getTextContent();
                            String lien = String.valueOf(page2.getUrl());
                            ArticleScrap article1 = new ArticleScrap(value, genreStringToInt(genreRecherche), anneeParutionInt, prixFinal, descri, lien);
                            VinyleController.getListeArticles().add(article1);
                        }
                    }
                }
            }
        }
    }

    /**
     * Method used to scrap the Mes Vinyles' website.
     *
     * @param titre the title we are searching for
     * @param min   the minimum price
     * @param max   the maximum price
     * @param annee the year
     * @throws IOException the io exception
     */
    public void scrapMesVinyles(String titre, double min, double max,int annee) throws IOException {                    //Méthode qui effectue le scraping du site MesVinyles
        String url = "https://mesvinyles.fr/fr/recherche?controller=search&s=" + titre;
        WebClient webClient = new WebClient();

        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage htmlPage = webClient.getPage(url);
        System.out.println("Je suis dans le site " + htmlPage.getUrl());
        List<HtmlAnchor> article = htmlPage.getByXPath("//div/div[2]/h3/a");
        for (HtmlElement a : article){
            String value= a.getTextContent();
            if (value.toLowerCase().contains(titre.toLowerCase())) {
                HtmlPage page2 = a.click();
                System.out.println("Je suis dans l'article " + value+"  "+page2.getUrl());
                List<HtmlElement> dateParution = page2.getByXPath("//div[1]/div[2]/div[2]/div[1]/p[1]");
                List<HtmlElement> prix = page2.getByXPath("//div[1]/div[2]/div[2]/div[2]/form/div[2]/div[1]/div/span");
                for (HtmlElement d : dateParution) {
                    String anneeParutionStr = d.getTextContent();
                    System.out.println("Je suis dans la date " + anneeParutionStr);
                    anneeParutionStr = anneeParutionStr.replaceAll("[^0-9]", "");
                    int anneeParutionInt;
                    if (anneeParutionStr.equals(""))
                        anneeParutionInt=0;
                    else
                       anneeParutionInt = Integer.parseInt(anneeParutionStr);
                    if (anneeParutionInt <= annee) {
                        System.out.println("La condition de date est remplie " + page2.getUrl());
                        for (HtmlElement p : prix) {
                            String valPrix = p.getTextContent();
                            System.out.println(valPrix);
                            double prixFinal = stringToDouble(valPrix);
                            if ((prixFinal <= max) && (prixFinal >= min)) {
                                System.out.println("Toutes les conditions sont remplies, on prend");
                                String lien = String.valueOf(page2.getUrl());
                                ArticleScrap article1 = new ArticleScrap(value,genreStringToInt("divers"),anneeParutionInt,prixFinal,"",lien);
                                VinyleController.getListeArticles().add(article1);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Method used to scrap the Culture Factory's website.
     *
     * @param titre the title we are searching for
     * @param min   the minimum price
     * @param max   the maximum price
     * @throws IOException the io exception
     */
    public void scrapCultureFactory(String titre, double min, double max) throws IOException {                          //Méthode qui effectue le scraping du site Culture Factory
        String url = "https://culturefactory.fr/recherche?controller=search&s=" + titre;
        WebClient webClient = new WebClient();

        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage htmlPage = webClient.getPage(url);
        System.out.println("Je suis dans le site " + htmlPage.getUrl());
        List<HtmlElement> article=htmlPage.getByXPath("//article/div[2]/h4/a");

        for (HtmlElement a : article){
            String value= a.getTextContent();
            if (value.toLowerCase().contains(titre.toLowerCase()) && value.toLowerCase().contains("vinyle")) {
                HtmlPage page2 = a.click();
                System.out.println("Je suis dans l'article " + value+"  "+page2.getUrl());
                List<HtmlElement> description = page2.getByXPath(".//div[contains(@class,'product-desc')]");
                List<HtmlElement> prix = page2.getByXPath("//div[1]/div[2]/div/div/section/div[1]/div[2]/div[1]/div[1]/div/span");
                        for (HtmlElement p : prix) {
                            String valPrix = p.getTextContent();
                            System.out.println(valPrix);
                            double prixFinal = stringToDouble(valPrix);
                            if ((prixFinal <= max) && (prixFinal >= min)) {
                                System.out.println("Toutes les conditions sont remplies, on prend");
                                String descri="";
                                if (!description.isEmpty())
                                    descri=description.get(0).getTextContent();
                                String lien = String.valueOf(page2.getUrl());
                                ArticleScrap article1 = new ArticleScrap(value,genreStringToInt("divers"),null,prixFinal,descri,lien);
                                VinyleController.getListeArticles().add(article1);
                            }
                        }
                }
            }
    }

    /**
     * Method used to scrap the Discogs' website.
     *
     * @param titre          the title we are searching for
     * @param min            the minimum price
     * @param max            the maximum price
     * @param annee          the year
     * @param genreRecherche the genre we are searching for
     * @throws IOException the io exception
     */
    public void scrapDiscogs(String titre, double min, double max,int annee, String genreRecherche) throws IOException {                //Méthode qui effectue le scraping du site Discogs
        String url = "https://www.discogs.com/fr/search/?q=" + titre + "&type=master&genre_exact=" + genreDiscogs(genreRecherche);
        WebClient webClient = new WebClient();

        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage htmlPage = webClient.getPage(url);
        System.out.println("Je suis dans le site " + htmlPage.getUrl());

        List<HtmlAnchor> article = htmlPage.getByXPath("//div[1]/a");
        for (HtmlElement e : article) {
            String value = e.getTextContent();
            HtmlPage page2 = e.click();
            System.out.println("Je suis dans l'article " +value+ "  lien: " + page2.getUrl());
            List<HtmlElement> description = page2.getByXPath("//div[2]/div[1]/div[5]/section/div/div");
            List<HtmlElement> dateParution = page2.getByXPath("//table/tbody/tr[3]/td/a/time");
            List<HtmlElement> prix = page2.getByXPath("//div[2]/div[1]/div[11]/section/header/div/span/span");
                for (HtmlElement a : dateParution) {
                    String anneeParutionStr = a.getTextContent();
                    System.out.println("Je suis dans la date " + anneeParutionStr);
                    int anneeParutionInt = Integer.parseInt(anneeParutionStr);
                    if (anneeParutionInt <= annee) {
                        System.out.println("La condition de date est remplie ");
                        for (HtmlElement p : prix) {
                            String valPrix = p.getTextContent();
                            System.out.println(valPrix);
                            double prixFinal = stringToDouble(valPrix);
                            if ((prixFinal <= max) && (prixFinal >= min)) {
                                System.out.println("Toutes les conditions sont remplies, on prend");
                                String descri ="";
                                if (!description.isEmpty())
                                    descri= description.get(0).getTextContent() + "\n";
                                String lien = String.valueOf(page2.getUrl());
                                ArticleScrap article1=new ArticleScrap(value,genreStringToInt(genreRecherche),anneeParutionInt,prixFinal,descri,lien);
                                VinyleController.getListeArticles().add(article1);
                            }
                        }
                    }
                }
            }
        }

    /**
     * Method used to cast a String into a double in order to get the price of the article.
     *
     * @param prixInitial the price we got from the scraping
     * @return a double
     */
    public double stringToDouble (String prixInitial){                                                                  //Méthode pour convertir une chaîne de caractère en double afin de pouvoir faire des opérations sur le prix
        String prixTemp= prixInitial.replaceAll("[^0-9,]", "");
        prixTemp=prixTemp.replace(",",".");
        double prixFinal = Double.parseDouble(prixTemp);
        return prixFinal;
    }


    /**
     * Method used to get the right genre for the Discogs' scrap.
     *
     * @param genre the genre that was selected for the search
     * @return the string containing the appropriate genre for the Discogs' website
     */
    public String genreDiscogs(String genre){                                                                           //Méthode qui renvoie un genre valide pour le site Discogs, en fonction du genre choisi pour la recherche
        switch (genre){
            case "Funk":
            case "Soul":
                return "Funk+%2F+Soul";
            case "Electro":
                return "Electronic";
            case "DubStep":
                return "";
            default:
                return genre;
        }
    }

    /**
     * Method used to get the right genre for the VinylCorner's scrap.
     *
     * @param genre the genre that was selected for the search
     * @return the appropriate int used by the VinyleCorner's website to order a search by genre
     */
    public int genreVinylCorner(String genre){                                                                           //Méthode qui renvoie un genre valide pour le site Vinyl Corner, en fonction du genre choisi pour la recherche
        switch (genre){
            case "Rock":
                return 5;
            case "Blues":
            case "Jazz":
                return 11;
            case "Electro":
                return 7;
            case "Reggae":
                return 10;
            case "Soul":
            case "Funk":
                return 9;
            default:
                return 3;
        }
    }


    /**
     * Method to convert the genre into an int for the database
     *
     * @param genre the genre that was selected for the search
     * @return the corresponding int
     */
    public int genreStringToInt(String genre){                                                                          //Méthode qui convertit le genre choisi en entier afin de pouvoir par la suite envoyer les données dans la BDD
        switch (genre){
            case "Rock":
                return 1;
            case "Blues":
                return 2;
            case "Jazz":
                return 3;
            case "Reggae":
                return 4;
            case "Funk":
                return 5;
            case "Electro":
                return 6;
            case  "DubStep":
                return 7;
            case "Soul":
                return 8;
            default:
                return 9;
        }
    }
}
