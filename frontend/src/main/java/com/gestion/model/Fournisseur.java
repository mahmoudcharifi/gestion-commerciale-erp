
package com.gestion.model;

import java.time.LocalDate;

public class Fournisseur {

    private Long id;
    private String codeFournisseur;
    private String nom;
    private String telephone;
    private String email;
    private String adresse;
    private String ville;
    private String description;
    private LocalDate dateCreation;
    private boolean actif;

    public Fournisseur(Long id, String codeFournisseur, String nom,
                       String telephone, String email, String adresse,
                       String ville, String description) {

        this.id = id;
        this.codeFournisseur = codeFournisseur;
        this.nom = nom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.ville = ville;
        this.description = description;
        this.dateCreation = LocalDate.now();
        this.actif = true;
    }

    public Long getId() { return id; }
    public String getCodeFournisseur() { return codeFournisseur; }
    public String getNom() { return nom; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }
    public String getAdresse() { return adresse; }
    public String getVille() { return ville; }
    public String getDescription() { return description; }
    public LocalDate getDateCreation() { return dateCreation; }
    public boolean isActif() { return actif; }

    public void setNom(String nom) { this.nom = nom; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setEmail(String email) { this.email = email; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setVille(String ville) { this.ville = ville; }
    public void setDescription(String description) { this.description = description; }
    public void setActif(boolean actif) { this.actif = actif; }
}
