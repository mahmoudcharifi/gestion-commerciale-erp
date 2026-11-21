package com.gestion.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.gestion.model.Facture;
import com.gestion.model.StatutFacture;
import com.gestion.service.FactureService;

import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.scene.layout.HBox;

public class FacturesController {

    // =========================================
    // 1. SERVICES ET DONNEES
    // =========================================

    private final FactureService factureService =
            FactureService.getInstance();

    private final ObservableList<Facture> factures =
            FXCollections.observableArrayList();

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =========================================
    // 2. ELEMENTS FXML
    // =========================================

    @FXML
    private Label totalFacturesLabel;

    @FXML
    private Label facturesEmisesLabel;

    @FXML
    private Label facturesAnnuleesLabel;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private TableView<Facture> facturesTable;

    @FXML
    private TableColumn<Facture, String> referenceColumn;

    @FXML
    private TableColumn<Facture, String> venteColumn;

    @FXML
    private TableColumn<Facture, String> clientColumn;

    @FXML
    private TableColumn<Facture, String> dateColumn;

    @FXML
    private TableColumn<Facture, String> totalHTColumn;

    @FXML
    private TableColumn<Facture, String> tvaColumn;

    @FXML
    private TableColumn<Facture, String> totalTTCColumn;

    @FXML
    private TableColumn<Facture, String> statutColumn;

    @FXML
    private TableColumn<Facture, Void> actionsColumn;

    // =========================================
    // 3. INITIALISATION
    // =========================================

    @FXML
    public void initialize() {

        configurerTableau();
        configurerFiltres();

        chargerFactures();
        actualiserStatistiques();

        System.out.println("Page Factures initialisée !");
    }

    // =========================================
    // 4. CONFIGURER LE TABLEAU
    // =========================================

    private void configurerTableau() {

        referenceColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getReference()
                )
        );

        venteColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getVente().getReference()
                )
        );

        clientColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getVente()
                                .getClient().getNomAffichage()
                )
        );

        dateColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getDateEmission()
                                .format(dateFormatter)
                )
        );

        totalHTColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        formaterMontant(
                                cell.getValue().getMontantTotalHT()
                        )
                )
        );

        tvaColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        formaterMontant(
                                cell.getValue().getMontantTVA()
                        )
                )
        );

        totalTTCColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        formaterMontant(
                                cell.getValue().getMontantTotalTTC()
                        )
                )
        );

        statutColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getStatut().name()
                )
        );

        statutColumn.setCellFactory(column ->
            new TableCell<Facture, String>() {

                @Override
                protected void updateItem(String statut, boolean empty) {
                    super.updateItem(statut, empty);

                    if (empty || statut == null) {
                        setGraphic(null);
                        setText(null);
                        return;
                    }

                    Label badge = new Label(statut);
                    badge.getStyleClass().add("status-badge");

                    if (statut.equals("EMISE")) {
                        badge.getStyleClass().add("status-emise");
                    } else if (statut.equals("ANNULEE")) {
                        badge.getStyleClass().add("status-annulee");
                    }

                    setGraphic(badge);
                    setText(null);
                }
            }
    );

        configurerActionsColumn();
    }

    // =========================================
    // 5. CONFIGURER LES ACTIONS
    // =========================================

    private void configurerActionsColumn() {

        actionsColumn.setCellFactory(column ->
                new TableCell<>() {

                    private final Button detailsButton =
                            new Button("Détails");

                    private final Button cancelButton =
                            new Button("Annuler");

                    private final HBox container =
                            new HBox(5, detailsButton, cancelButton);

                    {
                        detailsButton.getStyleClass().addAll(
                                "action-button",
                                "action-details"
                        );

                        cancelButton.getStyleClass().addAll(
                                "action-button",
                                "action-danger"
                        );

                        detailsButton.setOnAction(event -> {
                            Facture facture = getTableView()
                                    .getItems().get(getIndex());

                            afficherDetails(facture);
                        });

                        cancelButton.setOnAction(event -> {
                            Facture facture = getTableView()
                                    .getItems().get(getIndex());

                            annulerFacture(facture);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || getIndex() >= getTableView()
                                .getItems().size()) {
                            setGraphic(null);
                        } else {
                            Facture facture = getTableView()
                                    .getItems().get(getIndex());

                            boolean annulee =
                                    facture.getStatut()
                                            == StatutFacture.ANNULEE;

                            cancelButton.setDisable(annulee);

                            setGraphic(container);
                        }
                    }
                }
        );
    }

    // =========================================
    // 6. CONFIGURER LES FILTRES
    // =========================================

    private void configurerFiltres() {

        statusFilter.setItems(
                FXCollections.observableArrayList(
                        "Tous les statuts",
                        "EMISE",
                        "ANNULEE"
                )
        );

        statusFilter.setValue("Tous les statuts");

        searchField.textProperty().addListener(
                (observable, ancienneValeur, nouvelleValeur) ->
                        appliquerFiltres()
        );

        statusFilter.valueProperty().addListener(
                (observable, ancienneValeur, nouvelleValeur) ->
                        appliquerFiltres()
        );
    }

    // =========================================
    // 7. CHARGER LES FACTURES
    // =========================================

    private void chargerFactures() {

        factures.setAll(factureService.getAllFactures());

        appliquerFiltres();
    }

    // =========================================
    // 8. APPLIQUER LES FILTRES
    // =========================================

    private void appliquerFiltres() {

        String recherche = searchField.getText();

        List<Facture> resultats =
                factureService.rechercherFactures(recherche);

        String statutSelectionne = statusFilter.getValue();

        if (statutSelectionne != null
                && !statutSelectionne.equals("Tous les statuts")) {

            resultats = resultats.stream()
                    .filter(facture ->
                            facture.getStatut().name()
                                    .equals(statutSelectionne)
                    )
                    .toList();
        }

        factures.setAll(resultats);

        facturesTable.setItems(factures);
    }

    // =========================================
    // 9. ACTUALISER LES STATISTIQUES
    // =========================================

    private void actualiserStatistiques() {

        totalFacturesLabel.setText(
                String.valueOf(factureService.compterFactures())
        );

        facturesEmisesLabel.setText(
                String.valueOf(factureService.compterFacturesEmises())
        );

        facturesAnnuleesLabel.setText(
                String.valueOf(factureService.compterFacturesAnnulees())
        );
    }

    // =========================================
    // 10. AFFICHER LES DETAILS
    // =========================================

    private void afficherDetails(Facture facture) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Détails de la facture");
        alert.setHeaderText(
                "Facture " + facture.getReference()
        );

        String details =
                "Vente : " + facture.getVente().getReference()
                + "\nClient : "
                + facture.getVente().getClient().getNomAffichage()
                + "\nDate : "
                + facture.getDateEmission().format(dateFormatter)
                + "\n\nTotal HT : "
                + formaterMontant(facture.getMontantTotalHT())
                + " DH\nTVA : "
                + formaterMontant(facture.getMontantTVA())
                + " DH\nTotal TTC : "
                + formaterMontant(facture.getMontantTotalTTC())
                + " DH\n\nStatut : "
                + facture.getStatut();

        alert.setContentText(details);
        alert.showAndWait();
    }

    // =========================================
    // 11. ANNULER UNE FACTURE
    // =========================================

    private void annulerFacture(Facture facture) {

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Annulation de facture");
        confirmation.setHeaderText(
                "Annuler la facture " + facture.getReference() + " ?"
        );

        confirmation.setContentText(
                "Voulez-vous vraiment annuler cette facture ?"
        );

        confirmation.showAndWait().ifPresent(reponse -> {

            if (reponse == javafx.scene.control.ButtonType.OK) {

                try {
                    factureService.annulerFacture(facture);

                    chargerFactures();
                    actualiserStatistiques();

                    afficherMessage(
                            Alert.AlertType.INFORMATION,
                            "Succès",
                            "La facture a été annulée."
                    );

                } catch (RuntimeException e) {

                    afficherMessage(
                            Alert.AlertType.ERROR,
                            "Erreur",
                            e.getMessage()
                    );
                }
            }
        });
    }

    // =========================================
    // 12. ACTUALISER LA PAGE
    // =========================================

    @FXML
    private void handleRefresh() {

        chargerFactures();
        actualiserStatistiques();

        searchField.clear();
        statusFilter.setValue("Tous les statuts");
    }

    // =========================================
    // 13. FORMATER LES MONTANTS
    // =========================================

    private String formaterMontant(BigDecimal montant) {

        return String.format(
                java.util.Locale.FRANCE,
                "%,.2f",
                montant
        );
    }

    // =========================================
    // 14. AFFICHER UN MESSAGE
    // =========================================

    private void afficherMessage(
            Alert.AlertType type,
            String titre,
            String message
    ) {

        Alert alert = new Alert(type);

        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
