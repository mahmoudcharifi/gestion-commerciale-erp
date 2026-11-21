
package com.gestion.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.gestion.model.Achat;
import com.gestion.model.StatutAchat;
import com.gestion.service.AchatService;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AchatsController {

    @FXML private Label totalAchatsLabel;
    @FXML private Label nombreAchatsLabel;
    @FXML private Label achatsValidesLabel;
    @FXML private Label achatsAnnulesLabel;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    @FXML private TableView<Achat> achatsTable;

    @FXML private TableColumn<Achat, String> referenceColumn;
    @FXML private TableColumn<Achat, String> fournisseurColumn;
    @FXML private TableColumn<Achat, String> dateColumn;
    @FXML private TableColumn<Achat, String> totalColumn;
    @FXML private TableColumn<Achat, String> statusColumn;
    @FXML private TableColumn<Achat, Void> actionsColumn;

    private final AchatService achatService = AchatService.getInstance();

    private final ObservableList<Achat> achats =
            FXCollections.observableArrayList();

    private FilteredList<Achat> achatsFiltres;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final NumberFormat currencyFormatter =
            NumberFormat.getNumberInstance(Locale.FRANCE);

    @FXML
    public void initialize() {

        currencyFormatter.setMinimumFractionDigits(2);
        currencyFormatter.setMaximumFractionDigits(2);

        configurerFiltres();
        configurerTableau();
        actualiserDonnees();
    }

    // Configuration des filtres
    private void configurerFiltres() {

        statusFilter.setItems(FXCollections.observableArrayList(
                "Tous les statuts",
                "Brouillon",
                "Validé",
                "Annulé"
        ));

        statusFilter.setValue("Tous les statuts");

        achatsFiltres = new FilteredList<>(achats, achat -> true);

        searchField.textProperty().addListener(
                (observable, ancienneValeur, nouvelleValeur) ->
                        appliquerFiltres()
        );

        statusFilter.valueProperty().addListener(
                (observable, ancienneValeur, nouvelleValeur) ->
                        appliquerFiltres()
        );

        achatsTable.setItems(achatsFiltres);
    }

    // Configuration du tableau
    private void configurerTableau() {

        referenceColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getReference()
                )
        );

        fournisseurColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getFournisseur().getNom()
                )
        );

        dateColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getDateAchat().format(dateFormatter)
                )
        );

        totalColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        formaterMontant(cell.getValue().getMontantTotalTTC())
                )
        );

        statusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        formaterStatut(cell.getValue().getStatut())
                )
        );

        configurerActions();
    }

    // Boutons d'action
    private void configurerActions() {

        actionsColumn.setCellFactory(column ->
                new TableCell<>() {

                    private final Button detailsButton =
                            new Button("Détails");

                    private final Button validateButton =
                            new Button("Valider");

                    private final Button cancelButton =
                            new Button("Annuler");

                    {
                        detailsButton.getStyleClass().addAll(
                            "action-button", "action-details"
                        );

                        validateButton.getStyleClass().addAll(
                            "action-button", "action-success"
                        );

                        cancelButton.getStyleClass().addAll(
                            "action-button", "action-danger"
                        );
                    }

                    private final HBox container =
                            new HBox(6, detailsButton, validateButton, cancelButton);

                    {
                        detailsButton.setOnAction(event -> {
                            Achat achat = getTableView()
                                    .getItems().get(getIndex());
                            afficherDetails(achat);
                        });

                        validateButton.setOnAction(event -> {
                            Achat achat = getTableView()
                                    .getItems().get(getIndex());
                            validerAchat(achat);
                        });

                        cancelButton.setOnAction(event -> {
                            Achat achat = getTableView()
                                    .getItems().get(getIndex());
                            annulerAchat(achat);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || getIndex() >= getTableView()
                                .getItems().size()) {
                            setGraphic(null);
                        } else {
                            Achat achat = getTableView()
                                    .getItems().get(getIndex());

                            boolean brouillon =
                                    achat.getStatut() == StatutAchat.BROUILLON;

                            validateButton.setVisible(brouillon);
                            validateButton.setManaged(brouillon);

                            cancelButton.setVisible(
                                    achat.getStatut() != StatutAchat.ANNULE
                            );

                            cancelButton.setManaged(
                                    achat.getStatut() != StatutAchat.ANNULE
                            );

                            setGraphic(container);
                        }
                    }
                }
        );
    }

    // Actualiser les données
    private void actualiserDonnees() {

        achats.setAll(achatService.getAllAchats());

        actualiserStatistiques();
        appliquerFiltres();
    }

    // Statistiques
    private void actualiserStatistiques() {

        BigDecimal total = achatService.calculerTotalAchats();

        totalAchatsLabel.setText(formaterMontant(total) + " DH");

        nombreAchatsLabel.setText(
                String.valueOf(achatService.compterAchats())
        );

        achatsValidesLabel.setText(
                String.valueOf(achatService.compterAchatsValides())
        );

        achatsAnnulesLabel.setText(
                String.valueOf(achatService.compterAchatsAnnules())
        );
    }

    // Recherche et filtrage
    private void appliquerFiltres() {

        String recherche = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        String statut = statusFilter.getValue();

        achatsFiltres.setPredicate(achat -> {

            boolean correspondRecherche =
                    achat.getReference().toLowerCase().contains(recherche)
                    || achat.getFournisseur().getNom()
                            .toLowerCase().contains(recherche);

            boolean correspondStatut =
                    statut == null
                    || statut.equals("Tous les statuts")
                    || formaterStatut(achat.getStatut()).equals(statut);

            return correspondRecherche && correspondStatut;
        });
    }

    // Valider un achat
    private void validerAchat(Achat achat) {

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Validation de l'achat");
        confirmation.setHeaderText("Confirmer la validation ?");
        confirmation.setContentText(
                "Les quantités achetées seront ajoutées au stock."
        );

        confirmation.showAndWait().ifPresent(response -> {

            if (response == ButtonType.OK) {
                try {
                    achatService.validerAchat(achat);
                    actualiserDonnees();

                    afficherMessage(
                            Alert.AlertType.INFORMATION,
                            "Succès",
                            "L'achat a été validé et le stock mis à jour."
                    );

                } catch (Exception e) {
                    afficherMessage(
                            Alert.AlertType.ERROR,
                            "Erreur",
                            e.getMessage()
                    );
                }
            }
        });
    }

    // Annuler un achat
    private void annulerAchat(Achat achat) {

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Annulation de l'achat");
        confirmation.setHeaderText("Confirmer l'annulation ?");
        confirmation.setContentText(
                "Cette opération peut entraîner une sortie du stock."
        );

        confirmation.showAndWait().ifPresent(response -> {

            if (response == ButtonType.OK) {
                try {
                    achatService.annulerAchat(achat);
                    actualiserDonnees();

                    afficherMessage(
                            Alert.AlertType.INFORMATION,
                            "Succès",
                            "L'achat a été annulé."
                    );

                } catch (Exception e) {
                    afficherMessage(
                            Alert.AlertType.ERROR,
                            "Erreur",
                            e.getMessage()
                    );
                }
            }
        });
    }

    // Afficher les détails
    private void afficherDetails(Achat achat) {

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                    "/com/gestion/views/AchatDetails.fxml"
                )
            );

            Parent root = loader.load();

            AchatDetailsController controller = loader.getController();
            controller.setAchat(achat);

            Stage stage = new Stage();

            stage.setTitle(
                "Détails de l'achat " + achat.getReference()
            );

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(achatsTable.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setResizable(true);

            stage.showAndWait();

        } catch (Exception e) {

            e.printStackTrace();

            afficherMessage(
                Alert.AlertType.ERROR,
                "Erreur",
                "Impossible d'ouvrir les détails de l'achat : "
                        + e.getMessage()
            );
        }
    }

    @FXML
    private void handleNouvelAchat() {

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gestion/views/AchatForm.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nouvel achat");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(achatsTable.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setResizable(true);

            stage.setOnHidden(event -> actualiserDonnees());

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();

            afficherMessage(
                Alert.AlertType.ERROR,
                "Erreur",
                "Impossible d'ouvrir le formulaire d'achat : " + e.getMessage()
            );
        }
    }

    // Bouton actualiser
    @FXML
    private void handleActualiser() {
        actualiserDonnees();
    }

    private String formaterStatut(StatutAchat statut) {

        return switch (statut) {
            case BROUILLON -> "Brouillon";
            case VALIDE -> "Validé";
            case ANNULE -> "Annulé";
        };
    }

    private String formaterMontant(BigDecimal montant) {
        return currencyFormatter.format(montant);
    }

    private void afficherMessage(
            Alert.AlertType type,
            String titre,
            String message) {

        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
