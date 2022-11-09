package com.example.util;

/**
 * The type Article scrap.
 */
public class ArticleScrap {
    private String titre;
    private int genre;
    private int annee;
    private double prix;
    private String description;
    private String url;

    /**
     * Instantiates a new Article scrap.
     *
     * @param titre       the article's title
     * @param genre       the article's genre
     * @param annee       the article's year of release
     * @param prix        the article's price
     * @param description the article's description
     * @param url         the article's url
     */
    public ArticleScrap(String titre, int genre, Integer annee, double prix, String description, String url){
        this.titre=titre;
        this.genre=genre;
        this.annee=annee;
        this.prix=prix;
        this.description=description;
        this.url=url;

    }

    /**
     * Gets title.
     *
     * @return the article's title
     */
    public String getTitre() {
        return titre;
    }

    /**
     * Sets title.
     *
     * @param titre the article's title
     */
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /**
     * Gets genre.
     *
     * @return the article's genre
     */
    public int getGenre() {
        return genre;
    }

    /**
     * Sets genre.
     *
     * @param genre the article's genre
     */
    public void setGenre(int genre) {
        this.genre = genre;
    }

    /**
     * Gets year.
     *
     * @return the article's year of release
     */
    public int getAnnee() {
        return annee;
    }

    /**
     * Sets year.
     *
     * @param annee the article's year of release
     */
    public void setAnnee(int annee) {
        this.annee = annee;
    }

    /**
     * Gets price.
     *
     * @return the article's price
     */
    public double getPrix() {
        return prix;
    }

    /**
     * Sets price.
     *
     * @param prix the article's price
     */
    public void setPrix(double prix) {
        this.prix = prix;
    }

    /**
     * Gets description.
     *
     * @return the article's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the article's description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets url.
     *
     * @return the article's url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the article's url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Method used to display the article's informations.
     *
     * @return a String containing every information on the article
     */
    @Override
    public String toString() {
        return  "Titre=" + this.getTitre() +"\n" +
                "Annee=" + this.getAnnee() + "\n" +
                "Genre=" + this.getGenre() + "\n" +
                "Prix=" + this.getPrix() + "€\n" +
                "Description=" + this.getDescription() + "\n" +
                "URL=" + this.getUrl() + "\n"
                +"=============================================================================================================\n";
    }
}
