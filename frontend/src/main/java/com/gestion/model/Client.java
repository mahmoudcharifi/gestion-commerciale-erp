package com.gestion.model;

import java.time.LocalDate;

public class Client {

    private Long id;
    private String codeClient;
    private TypeClient typeClient;

    // Informations communes
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String adresse;
    private String ville;

    // Informations spécifiques aux entreprises
    private String raisonSociale;
    private String ice;
    private String identifiantFiscal;
    private String registreCommerce;
    private String personneContact;

    private LocalDate dateCreation;
    private boolean actif;

    public Client(Long id, String codeClient, TypeClient typeClient,
                  String nom, String prenom, String telephone,
                  String email, String adresse, String ville,
                  String raisonSociale, String ice,
                  String identifiantFiscal, String registreCommerce,
                  String personneContact) {

        this.id = id;
        this.codeClient = codeClient;
        this.typeClient = typeClient;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.ville = ville;
        this.raisonSociale = raisonSociale;
        this.ice = ice;
        this.identifiantFiscal = identifiantFiscal;
        this.registreCommerce = registreCommerce;
        this.personneContact = personneContact;
        this.dateCreation = LocalDate.now();
        this.actif = true;
    }

    public String getNomAffichage() {
        if (typeClient == TypeClient.ENTREPRISE) {
            return raisonSociale;
        }

        return ((prenom == null ? "" : prenom) + " "
                + (nom == null ? "" : nom)).trim();
    }

    public Long getId() { return id; }
    public String getCodeClient() { return codeClient; }
    public TypeClient getTypeClient() { return typeClient; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }
    public String getAdresse() { return adresse; }
    public String getVille() { return ville; }
    public String getRaisonSociale() { return raisonSociale; }
    public String getIce() { return ice; }
    public String getIdentifiantFiscal() { return identifiantFiscal; }
    public String getRegistreCommerce() { return registreCommerce; }
    public String getPersonneContact() { return personneContact; }
    public LocalDate getDateCreation() { return dateCreation; }
    public boolean isActif() { return actif; }

    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setEmail(String email) { this.email = email; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setVille(String ville) { this.ville = ville; }
    public void setRaisonSociale(String raisonSociale) { this.raisonSociale = raisonSociale; }
    public void setIce(String ice) { this.ice = ice; }
    public void setIdentifiantFiscal(String identifiantFiscal) { this.identifiantFiscal = identifiantFiscal; }
    public void setRegistreCommerce(String registreCommerce) { this.registreCommerce = registreCommerce; }
    public void setPersonneContact(String personneContact) { this.personneContact = personneContact; }
    public void setActif(boolean actif) { this.actif = actif; }
}
