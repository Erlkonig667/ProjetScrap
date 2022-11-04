package com.example.util;

public class ArticleScrap {
    private String titre;
    private String genre;
    private int annee;
    private double prix;
    private String description;

    public ArticleScrap(String titre, String genre, int annee, double prix, String description){
        this.titre=titre;
        this.genre=genre;
        this.annee=annee;
        this.prix=prix;
        this.description=description;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return  "Titre=" + this.getTitre() +"\n" +
                "Annee=" + this.getAnnee() + "\n" +
                "Genre=" + this.getGenre() + "\n" +
                "Prix=" + this.getPrix() + "\n" +
                "Description=" + this.getDescription() + "\n"
                +"=============================================================================================================\n";
    }
}
