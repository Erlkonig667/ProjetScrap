package com.example.util;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.*;
import java.util.List;

public class Scrap {
    public static String scrapBonCoin(String titre, double min, double max ) throws IOException {
        String res ="";
        String url = "https://www.leboncoin.fr/recherche?category=26&text=" + titre + "&price=" + min + "-" + max;
        WebClient webClient = new WebClient();

        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage htmlPage = webClient.getPage(url);

        List<HtmlAnchor> article = htmlPage.getByXPath("//p");
        for (HtmlElement e : article) {
            String value = e.getTextContent();
            if (value.toLowerCase().contains(titre.toLowerCase())) {
                HtmlPage page2 = e.click();
                List<HtmlElement> description = page2.getByXPath("//div[5]/div/div/p");
                List<HtmlElement> prix = page2.getByXPath("//div[3]/div/span/div/div[1]/div/span");
                for (HtmlElement a : prix) {
                    String valPrix = a.getTextContent();
                    res+=value+"\n";
                    res+=valPrix+"\n";
                    for (HtmlElement b : description) {
                        String descri = b.getTextContent();
                        res+=descri+"\n";
                        res+=page2.getUrl() + "\n\n";
                        res+="=============================================================================================================\n";
                    }
                }
            }
        }
        if (res.equals(""))
            res="Nous n'avons trouvé aucun résultat correspondant à votre recherche.";
        return res;
    }

    public static String scrapFnac(String titre, double min, double max, int annee) throws IOException {
        String res ="";
        String url = "https://www.fnac.com/SearchResult/ResultList.aspx?SCat=0&Search="+titre;//+"&SFilt=CD!attributes.support_audio_label%2cVinyle!attributes.support_audio_label&sft=1";
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
                String valPrix = prix.get(0).getTextContent();
                System.out.println(valPrix);
                double prixFinal = stringToDouble(valPrix);
                if ((prixFinal <= max) && (prixFinal >= min)) {
                    System.out.println("Toutes les conditions sont remplies, on prend");
                    res += value + " / Année de parution: " + anneeParutionInt + "\n";
                    res += valPrix + "\n";
                    if (!description.isEmpty()) {
                        String descri = description.get(0).getTextContent();
                        res += descri + "\n";
                    }
                    res += page2.getUrl() + "\n\n";
                    res += "=============================================================================================================\n";
                }
            }
        }
        if (res.equals(""))
            res="Nous n'avons trouvé aucun résultat correspondant à votre recherche.";
        return res;
    }

    public static String scrapVinylCorner(String titre, double min, double max,int annee, String genreRecherche) throws IOException {
        String res ="";
        String url = "https://www.vinylcorner.fr/recherche?controller=search&s=" + titre;
        WebClient webClient = new WebClient();

        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage htmlPage = webClient.getPage(url);
        System.out.println("Je suis dans le site "+htmlPage.getUrl());

        List<HtmlAnchor> article = htmlPage.getByXPath("//div/div[2]/div[1]/h3/a");
        for (HtmlElement e : article) {
            String value = e.getTextContent();
            HtmlPage page2 = e.click();
            System.out.println("Je suis dans l'article "+ value + " Lien: "+page2.getUrl());
            List<HtmlElement> description = page2.getByXPath(".//div[contains(@class,'product-description')]");
            List<HtmlElement> dateParution=page2.getByXPath("//div[2]/div/div/div/section/div[1]/div[2]/div[1]/p[2]/strong");
            List<HtmlElement> prix = page2.getByXPath("//div[1]/div[2]/div[2]/div[1]/div/span");
            List<HtmlElement> genre = page2.getByXPath("//div[2]/div/div/div/section/div[1]/div[2]/div[1]/p[4]");

            for (HtmlElement a : dateParution) {
                String anneeParutionStr=a.getTextContent();
                System.out.println("Je suis dans la date "+anneeParutionStr);
                anneeParutionStr= anneeParutionStr.substring(anneeParutionStr.length()-4);
                int anneeParutionInt = Integer.parseInt(anneeParutionStr);
                if (anneeParutionInt<=annee) {
                    System.out.println("La condition de date est remplie " + page2.getUrl());
                    if (genre.get(0).getTextContent().toLowerCase().contains(genreRecherche.toLowerCase())) {
                        for (HtmlElement p : prix) {
                            String valPrix = p.getTextContent();
                            System.out.println(valPrix);
                            double prixFinal = stringToDouble(valPrix);
                            if ((prixFinal <= max) && (prixFinal >= min)) {
                                System.out.println("Toutes les conditions sont remplies, on prend");
                                res += value + "\n";
                                res += valPrix + "\n";
                                if (!description.isEmpty())
                                    res += description.get(0).getTextContent() + "\n";
                                res += page2.getUrl() + "\n\n";
                                res += "=============================================================================================================\n";
                            }
                        }
                    }
                }
            }
        }
        if (res.equals(""))
            res="Nous n'avons trouvé aucun résultat correspondant à votre recherche.";
        return res;
    }

    public static String scrapMesVinyles(String titre, double min, double max,int annee) throws IOException {
        String res = "";
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
                                res += value + " / Année de parution: "+anneeParutionInt+ "\n";
                                res += valPrix + "\n";
                                res += page2.getUrl() + "\n\n";
                                res += "=============================================================================================================\n";
                            }
                        }
                    }
                }
            }
        }
        if (res.equals(""))
            res="Nous n'avons trouvé aucun résultat correspondant à votre recherche.";
        return res;
    }
    public static String scrapCultureFactory(String titre, double min, double max) throws IOException {
        String res = "";
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
                                res += value + "\n";
                                res += valPrix + "\n";
                                if (!description.isEmpty())
                                    res+=description.get(0).getTextContent()+"\n";
                                res += page2.getUrl() + "\n\n";
                                res += "=============================================================================================================\n";
                            }
                        }
                }
            }
        if (res.equals(""))
            res="Nous n'avons trouvé aucun résultat correspondant à votre recherche.";
        return res;
    }

    public static String scrapDiscogs(String titre, double min, double max,int annee, String genreRecherche) throws IOException {
        String res = "";
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
                                res += value + "\n";
                                res += valPrix + "\n";
                                if (!description.isEmpty())
                                    res += description.get(0).getTextContent() + "\n";
                                res += page2.getUrl() + "\n\n";
                                res += "=============================================================================================================\n";
                            }
                        }
                    }
                }
            }
        if (res.equals(""))
            res="Nous n'avons trouvé aucun résultat correspondant à votre recherche.";
        return res;
        }


    public static double stringToDouble (String prixInitial){
        String prixTemp= prixInitial.replaceAll("[^0-9,]", "");
        prixTemp=prixTemp.replace(",",".");
        double prixFinal = Double.parseDouble(prixTemp);
        return prixFinal;
    }


    public static String genreDiscogs(String genre){
        switch (genre){
            case "Funk":
            case "Soul":
                return "Funk+%2F+Soul";
            case "Electro":
                return "Electronic";
            case "Dubstep":
                return "";
            default:
                return genre;
        }
    }
}
