package com.example.util;

import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.sql.Date;


public class EnvoiMail {

    public static void save(String res) throws IOException {
        PrintWriter ecrire;
        File rep = new File("texte");
        rep.mkdir();
        String nomFichierSortie = "texte"+File.separator+"RechercheVinyles.txt";
        ecrire =  new PrintWriter(new BufferedWriter(new FileWriter(nomFichierSortie)));
        ecrire.println(res);
        ecrire.close();
    }
    public static void envoyerMail(String adresseMail,String nomFichier){
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        // Configure API key authorization: api-key
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey("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");

        try {
            TransactionalEmailsApi api = new TransactionalEmailsApi();
            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail("thibaut.langlais2@gmail.com");
            sender.setName("Thibaut Langlais");
            List<SendSmtpEmailTo> toList = new ArrayList<SendSmtpEmailTo>();
            SendSmtpEmailTo to = new SendSmtpEmailTo();
            to.setEmail(adresseMail);
            to.setName("Monsieur, Madame");
            toList.add(to);
            SendSmtpEmailReplyTo replyTo = new SendSmtpEmailReplyTo();
            replyTo.setEmail("thibaut.langlais2@gmail.com");
            replyTo.setName("Thibaut Langlais");
            SendSmtpEmailAttachment attachment = new SendSmtpEmailAttachment();
            attachment.setName(nomFichier);
            byte[] encode = Files.readAllBytes(Paths.get(nomFichier));
            attachment.setContent(encode);
            List<SendSmtpEmailAttachment> attachmentList = new ArrayList<SendSmtpEmailAttachment>();
            attachmentList.add(attachment);
            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
            sendSmtpEmail.setSender(sender);
            sendSmtpEmail.setTo(toList);
            sendSmtpEmail.setHtmlContent("<html><body><p>Bonjour,<br><br> Vous trouverez ci-joint les résultats de votre recherche de vinyles.<br><br> Toute notre équipe vous souhaite une bonne journée! </p></body></html>");
            sendSmtpEmail.setSubject("Votre recherche de vinyles");
            sendSmtpEmail.setReplyTo(replyTo);
            sendSmtpEmail.setAttachment(attachmentList);
            CreateSmtpEmail response = api.sendTransacEmail(sendSmtpEmail);
            System.out.println(response.toString());
        } catch (Exception e) {
            System.out.println("Exception occurred:- " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        envoyerMail("thibaut.langlais2@gmail.com","images/perruche.png");
    }
}
