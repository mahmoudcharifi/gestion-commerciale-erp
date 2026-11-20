
package com.gestion.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.IdentityHashMap;
import java.util.Map;

import com.gestion.model.Client;
import com.gestion.model.LigneVente;
import com.gestion.model.Produit;
import com.gestion.model.StatutVente;
import com.gestion.model.TypeClient;
import com.gestion.model.Vente;
import com.gestion.service.ClientService;
import com.gestion.service.VenteService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class SaleFormController {

    @FXML private Label saleReferenceLabel;
    @FXML private Label clientTypeLabel;

    @FXML private ComboBox<Client> clientComboBox;
    @FXML private ComboBox<Produit> productComboBox;

    @FXML private TextField quantityField;
    @FXML private TextField discountField;

    @FXML private TableView<LigneVente> saleLinesTable;

    @FXML private TableColumn<LigneVente, String> productReferenceColumn;
    @FXML private TableColumn<LigneVente, String> productNameColumn;
    @FXML private TableColumn<LigneVente, String> productPriceColumn;
    @FXML private TableColumn<LigneVente, String> productQuantityColumn;
    @FXML private TableColumn<LigneVente, String> productTotalColumn;
    @FXML private TableColumn<LigneVente, Void> removeProductColumn;

    @FXML private Label totalHTLabel;
    @FXML private Label discountLabel;
    @FXML private Label vatLabel;
    @FXML private Label totalTTCLabel;
    @FXML private Label errorLabel;

    private final ClientService clientService =
            ClientService.getInstance();

    private final VenteService venteService =
            VenteService.getInstance();

    private final ObservableList<LigneVente> lignes =
            FXCollections.observableArrayList();

    private long prochainIdLigne = 1;

    @FXML
    public void initialize() {

        saleLinesTable.setItems(lignes);

        chargerClients();
        chargerProduits();
        configurerTableau();
        configurerEcouteurs();

        actualiserTotaux();
    }

    // =========================================
    // CLIENTS
    // =========================================

    private void chargerClients() {

        ObservableList<Client> clients =
                FXCollections.observableArrayList();

        clientService.getAllClients()
                .stream()
                .filter(Client::isActif)
                .forEach(clients::add);

        Client clientComptoir =
                clientService.getClientComptoir();

        clients.add(0, clientComptoir);

        clientComboBox.setItems(clients);

        clientComboBox.setConverter(new StringConverter<>() {

            @Override
            public String toString(Client client) {

                return client == null
                        ? ""
                        : client.getNomAffichage();
            }

            @Override
            public Client fromString(String value) {
                return null;
            }
        });

        clientComboBox.getSelectionModel()
                .selectFirst();

        actualiserTypeClient();
    }

    private void actualiserTypeClient() {

        Client client = clientComboBox.getValue();

        if (client == null) {
            clientTypeLabel.setText("Non sélectionné");
            return;
        }

        if ("CLI-COMPT".equals(client.getCodeClient())) {
            clientTypeLabel.setText("Client comptoir");
        } else if (client.getTypeClient() == TypeClient.ENTREPRISE) {
            clientTypeLabel.setText("Entreprise");
        } else {
            clientTypeLabel.setText("Particulier");
        }
    }

    // =========================================
    // PRODUITS
    // =========================================

    private void chargerProduits() {

        productComboBox.setItems(
                FXCollections.observableArrayList(
                        ProductsController.getProducts()
                                .stream()
                                .filter(produit ->
                                        produit.getQuantite() > 0)
                                .toList()
                )
        );

        productComboBox.setConverter(new StringConverter<>() {

            @Override
            public String toString(Produit produit) {

                if (produit == null) {
                    return "";
                }

                return produit.getReference()
                        + " - "
                        + produit.getNom()
                        + " (Stock : "
                        + produit.getQuantite()
                        + ")";
            }

            @Override
            public Produit fromString(String value) {
                return null;
            }
        });
    }

    // =========================================
    // TABLEAU DES LIGNES
    // =========================================

    private void configurerTableau() {

        productReferenceColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getReferenceProduit()
                )
        );

        productNameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNomProduit()
                )
        );

        productPriceColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        formatMontant(
                                data.getValue().getPrixUnitaire()
                        )
                )
        );

        productQuantityColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue().getQuantite()
                        )
                )
        );

        productTotalColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        formatMontant(
                                data.getValue().getSousTotalHT()
                        )
                )
        );

        configurerBoutonSuppression();
    }

    private void configurerBoutonSuppression() {

        removeProductColumn.setCellFactory(column ->
                new TableCell<>() {

                    private final Button supprimerButton =
                            new Button("Supprimer");

                    {
                        supprimerButton.getStyleClass()
                                .add("danger-button");

                        supprimerButton.setOnAction(event -> {

                            LigneVente ligne =
                                    getTableView().getItems()
                                            .get(getIndex());

                            lignes.remove(ligne);

                            actualiserTotaux();
                            effacerErreur();
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item, boolean empty) {

                        super.updateItem(item, empty);

                        setGraphic(
                                empty ? null : supprimerButton
                        );
                    }
                }
        );
    }

    // =========================================
    // ÉCOUTEURS
    // =========================================

    private void configurerEcouteurs() {

        clientComboBox.valueProperty().addListener(
                (observable, oldValue, newValue) ->
                        actualiserTypeClient()
        );

        discountField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        actualiserTotaux()
        );
    }

    // =========================================
    // AJOUTER UN PRODUIT
    // =========================================

    @FXML
    private void handleAddProduct() {

        try {

            Produit produit = productComboBox.getValue();

            if (produit == null) {
                throw new IllegalArgumentException(
                        "Veuillez sélectionner un produit."
                );
            }

            int quantite = Integer.parseInt(
                    quantityField.getText().trim()
            );

            if (quantite <= 0) {
                throw new IllegalArgumentException(
                        "La quantité doit être supérieure à zéro."
                );
            }

            if (quantite > produit.getQuantite()) {
                throw new IllegalArgumentException(
                        "Stock insuffisant. Disponible : "
                                + produit.getQuantite()
                );
            }

            BigDecimal tauxTVA = produit.getTauxTVA();

            if (tauxTVA == null) {
                throw new IllegalArgumentException(
                        "Veuillez renseigner la TVA de ce produit."
                );
            }

            LigneVente ligne = new LigneVente(
                    prochainIdLigne++,
                    produit,
                    quantite,
                    produit.getPrixVente()
            );

            lignes.add(ligne);

            productComboBox.getSelectionModel()
                    .clearSelection();

            quantityField.setText("1");

            actualiserTotaux();
            effacerErreur();

        } catch (NumberFormatException e) {

            afficherErreur(
                    "Veuillez saisir une quantité entière valide."
            );

        } catch (IllegalArgumentException e) {

            afficherErreur(e.getMessage());
        }
    }

    // =========================================
    // CALCUL DES TOTAUX
    // =========================================

    private void actualiserTotaux() {

        BigDecimal totalHT = BigDecimal.ZERO;
        BigDecimal totalTVA = BigDecimal.ZERO;

        for (LigneVente ligne : lignes) {

            totalHT = totalHT.add(
                    ligne.getSousTotalHT()
            );

            totalTVA = totalTVA.add(
                    ligne.getMontantTVA()
            );
        }

        BigDecimal remise;

        try {

            String texte = discountField.getText();

            remise = texte == null || texte.isBlank()
                    ? BigDecimal.ZERO
                    : new BigDecimal(
                            texte.trim().replace(',', '.')
                    );

        } catch (NumberFormatException e) {

            afficherErreur(
                    "Le montant de la remise est invalide."
            );
            return;
        }

        if (remise.compareTo(BigDecimal.ZERO) < 0
                || remise.compareTo(totalHT) > 0) {

            afficherErreur(
                    "La remise doit être comprise entre 0 "
                            + "et le total HT."
            );
            return;
        }

        BigDecimal tauxRemise =
                totalHT.compareTo(BigDecimal.ZERO) == 0
                        ? BigDecimal.ZERO
                        : remise.divide(
                                totalHT,
                                10,
                                RoundingMode.HALF_UP
                        );

        BigDecimal reductionTVA =
                totalTVA.multiply(tauxRemise)
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalHTApresRemise =
                totalHT.subtract(remise);

        BigDecimal totalTVAApresRemise =
                totalTVA.subtract(reductionTVA);

        BigDecimal totalTTC =
                totalHTApresRemise.add(totalTVAApresRemise);

        totalHTLabel.setText(
                formatMontant(totalHTApresRemise)
        );

        discountLabel.setText(
                formatMontant(remise)
        );

        vatLabel.setText(
                formatMontant(totalTVAApresRemise)
        );

        totalTTCLabel.setText(
                formatMontant(totalTTC)
        );
    }

    private BigDecimal lireRemise() {

        String texte = discountField.getText();

        if (texte == null || texte.isBlank()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(
                texte.trim().replace(',', '.')
        );
    }

    // =========================================
    // ENREGISTRER UN BROUILLON
    // =========================================

    @FXML
    private void handleSaveDraft() {

        try {

            Client client = verifierClient();

            verifierLignes();
            verifierRemise();

            BigDecimal remise = lireRemise();


            Vente vente = venteService.creerVente(client);

            ajouterLignesEtRemise(vente, remise);

            afficherConfirmation(
                    "Le brouillon "
                            + vente.getReference()
                            + " a été enregistré."
            );

            fermerFormulaire();

        } catch (RuntimeException e) {

            afficherErreur(e.getMessage());
        }
    }

    // =========================================
    // VALIDER UNE VENTE
    // =========================================

    @FXML
    private void handleValidateSale() {

        Vente vente = null;

        try {

            Client client = verifierClient();

            verifierLignes();
            verifierRemise();

            BigDecimal remise = lireRemise();

            // Création après toutes les vérifications du formulaire
            vente = venteService.creerVente(client);

            ajouterLignesEtRemise(vente, remise);

            // Vérification finale et sortie du stock
            venteService.validerVente(vente);

            afficherConfirmation(
                    "La vente "
                            + vente.getReference()
                            + " a été validée avec succès."
            );

            fermerFormulaire();

        } catch (RuntimeException e) {

            // Nettoyage si une erreur survient après la création
            if (vente != null
                    && vente.getStatutVente() == StatutVente.BROUILLON) {

                venteService.supprimerBrouillon(vente);
            }

            afficherErreur(e.getMessage());
        }
    }


    private void ajouterLignesEtRemise(
            Vente vente,
            BigDecimal remise) {

        for (LigneVente ligne : lignes) {
            venteService.ajouterLigne(vente, ligne);
        }

        venteService.appliquerRemise(vente, remise);
    }

    // =========================================
    // VALIDATIONS
    // =========================================

    private Client verifierClient() {

        Client client = clientComboBox.getValue();

        if (client == null) {
            throw new IllegalArgumentException(
                    "Veuillez sélectionner un client."
            );
        }

        return client;
    }


    private void verifierLignes() {

        if (lignes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ajoutez au moins un produit à la vente."
            );
        }

        Map<Produit, Integer> quantitesDemandees =
                new IdentityHashMap<>();

        for (LigneVente ligne : lignes) {

            Produit produit = ligne.getProduit();

            if (produit == null) {
                throw new IllegalArgumentException(
                        "Un produit de la vente est invalide."
                );
            }

            quantitesDemandees.merge(
                    produit,
                    ligne.getQuantite(),
                    Integer::sum
            );
        }

        for (Map.Entry<Produit, Integer> entree
                : quantitesDemandees.entrySet()) {

            Produit produit = entree.getKey();
            int quantiteDemandee = entree.getValue();

            if (quantiteDemandee > produit.getQuantite()) {
                throw new IllegalArgumentException(
                        "Stock insuffisant pour "
                                + produit.getNom()
                                + ". Disponible : "
                                + produit.getQuantite()
                                + ", demandé : "
                                + quantiteDemandee
                );
            }
        }
    }


    // =========================================
    // ANNULER
    // =========================================

    @FXML
    private void handleCancel() {
        fermerFormulaire();
    }

    private void fermerFormulaire() {

        Stage currentStage =
                (Stage) errorLabel.getScene().getWindow();

        currentStage.close();
    }

    // =========================================
    // UTILITAIRES
    // =========================================

    private String formatMontant(BigDecimal montant) {

        return montant.setScale(
                2,
                RoundingMode.HALF_UP
        ) + " DH";
    }

    private void afficherErreur(String message) {

        errorLabel.setText(
                message == null
                        ? "Une erreur est survenue."
                        : message
        );
    }

    private void effacerErreur() {
        errorLabel.setText("");
    }

    private void afficherConfirmation(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Gestion des ventes");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void verifierRemise() {

        BigDecimal remise = lireRemise();

        BigDecimal totalHT = lignes.stream()
                .map(LigneVente::getSousTotalHT)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (remise.compareTo(BigDecimal.ZERO) < 0
                || remise.compareTo(totalHT) > 0) {

            throw new IllegalArgumentException(
                    "La remise doit être comprise entre 0 et le total HT."
            );
        }
    }

}
