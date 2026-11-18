package com.gestion.controller;

import java.util.function.Consumer;

import com.gestion.model.Client;
import com.gestion.model.TypeClient;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientFormController {

    @FXML private Label formTitle;
    @FXML private ComboBox<TypeClient> typeClientComboBox;

    @FXML private VBox particulierSection;
    @FXML private VBox entrepriseSection;

    @FXML private TextField nomField;
    @FXML private TextField prenomField;

    @FXML private TextField raisonSocialeField;
    @FXML private TextField iceField;
    @FXML private TextField identifiantFiscalField;
    @FXML private TextField registreCommerceField;
    @FXML private TextField personneContactField;

    @FXML private TextField telephoneField;
    @FXML private TextField emailField;
    @FXML private TextField adresseField;
    @FXML private TextField villeField;

    @FXML private Label errorLabel;

    private Stage stage;
    private Consumer<Client> onClientSaved;
    private Client clientToEdit;

    @FXML
    public void initialize() {
        typeClientComboBox.setItems(
                FXCollections.observableArrayList(TypeClient.values())
        );

        typeClientComboBox.setOnAction(event -> actualiserSections());

        typeClientComboBox.setValue(TypeClient.PARTICULIER);
        actualiserSections();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnClientSaved(Consumer<Client> callback) {
        this.onClientSaved = callback;
    }

    private void actualiserSections() {
        TypeClient type = typeClientComboBox.getValue();

        boolean particulier = type == TypeClient.PARTICULIER;

        particulierSection.setVisible(particulier);
        particulierSection.setManaged(particulier);

        entrepriseSection.setVisible(!particulier);
        entrepriseSection.setManaged(!particulier);
    }

    @FXML
    private void saveClient() {
        try {
            TypeClient type = typeClientComboBox.getValue();

            if (type == null) {
                afficherErreur("Veuillez choisir le type du client.");
                return;
            }

            String nom = texte(nomField);
            String prenom = texte(prenomField);
            String raisonSociale = texte(raisonSocialeField);

            if (type == TypeClient.PARTICULIER
                    && (nom.isBlank() || prenom.isBlank())) {
                afficherErreur("Le nom et le prénom sont obligatoires.");
                return;
            }

            if (type == TypeClient.ENTREPRISE && raisonSociale.isBlank()) {
                afficherErreur("La raison sociale est obligatoire.");
                return;
            }

            String email = texte(emailField);

            if (!email.isBlank()
                    && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                afficherErreur("Veuillez saisir une adresse email valide.");
                return;
            }

            Client client;

            if (clientToEdit != null) {
                client = clientToEdit;
            } else {
                client = new Client(
                        null, null, type,
                        nom, prenom,
                        texte(telephoneField),
                        email,
                        texte(adresseField),
                        texte(villeField),
                        raisonSociale,
                        texte(iceField),
                        texte(identifiantFiscalField),
                        texte(registreCommerceField),
                        texte(personneContactField)
                );
            }

            client.setNom(type == TypeClient.PARTICULIER ? nom : null);
            client.setPrenom(type == TypeClient.PARTICULIER ? prenom : null);
            client.setRaisonSociale(
                    type == TypeClient.ENTREPRISE ? raisonSociale : null
            );

            client.setTelephone(texte(telephoneField));
            client.setEmail(email);
            client.setAdresse(texte(adresseField));
            client.setVille(texte(villeField));

            client.setIce(
                    type == TypeClient.ENTREPRISE ? texte(iceField) : null
            );
            client.setIdentifiantFiscal(
                    type == TypeClient.ENTREPRISE
                            ? texte(identifiantFiscalField) : null
            );
            client.setRegistreCommerce(
                    type == TypeClient.ENTREPRISE
                            ? texte(registreCommerceField) : null
            );
            client.setPersonneContact(
                    type == TypeClient.ENTREPRISE
                            ? texte(personneContactField) : null
            );

            onClientSaved.accept(client);
            stage.close();

        } catch (Exception e) {
            afficherErreur("Une erreur est survenue : " + e.getMessage());
        }
    }

    public void setClientToEdit(Client client) {
        this.clientToEdit = client;

        formTitle.setText("Modifier le client");
        typeClientComboBox.setValue(client.getTypeClient());

        nomField.setText(valeur(client.getNom()));
        prenomField.setText(valeur(client.getPrenom()));

        raisonSocialeField.setText(valeur(client.getRaisonSociale()));
        iceField.setText(valeur(client.getIce()));
        identifiantFiscalField.setText(valeur(client.getIdentifiantFiscal()));
        registreCommerceField.setText(valeur(client.getRegistreCommerce()));
        personneContactField.setText(valeur(client.getPersonneContact()));

        telephoneField.setText(valeur(client.getTelephone()));
        emailField.setText(valeur(client.getEmail()));
        adresseField.setText(valeur(client.getAdresse()));
        villeField.setText(valeur(client.getVille()));

        actualiserSections();
    }

    @FXML
    private void cancelForm() {
        stage.close();
    }

    private String texte(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private String valeur(String valeur) {
        return valeur == null ? "" : valeur;
    }

    private void afficherErreur(String message) {
        errorLabel.setText(message);
    }
}
