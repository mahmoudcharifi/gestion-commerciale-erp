package com.gestion.controller;

import java.io.IOException;

import com.gestion.model.Fournisseur;
import com.gestion.service.FournisseurService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SuppliersController {

    @FXML private Label totalSuppliersLabel;
    @FXML private Label activeSuppliersLabel;
    @FXML private Label inactiveSuppliersLabel;

    @FXML private TextField searchField;

    @FXML private TableView<Fournisseur> suppliersTable;

    @FXML private TableColumn<Fournisseur, String> codeColumn;
    @FXML private TableColumn<Fournisseur, String> nameColumn;
    @FXML private TableColumn<Fournisseur, String> phoneColumn;
    @FXML private TableColumn<Fournisseur, String> emailColumn;
    @FXML private TableColumn<Fournisseur, String> cityColumn;
    @FXML private TableColumn<Fournisseur, String> statusColumn;
    @FXML private TableColumn<Fournisseur, Void> actionsColumn;

    private final FournisseurService fournisseurService =
            FournisseurService.getInstance();

    private ObservableList<Fournisseur> fournisseurs;
    private FilteredList<Fournisseur> fournisseursFiltres;

    @FXML
    public void initialize() {
        initialiserDonnees();
        configurerTableau();
        configurerRecherche();
        actualiserStatistiques();
    }

    private void initialiserDonnees() {
        fournisseurs = FXCollections.observableArrayList(
                fournisseurService.getAllFournisseurs()
        );

        fournisseursFiltres = new FilteredList<>(
                fournisseurs, fournisseur -> true
        );

        suppliersTable.setItems(fournisseursFiltres);
    }

    private void configurerTableau() {

        codeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCodeFournisseur()
                )
        );
        suppliersTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNom())
        );

        phoneColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTelephone())
        );

        emailColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail())
        );

        cityColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getVille())
        );

        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().isActif() ? "Actif" : "Inactif"
                )
        );

        configurerColonneActions();
    }

    private void configurerColonneActions() {

        actionsColumn.setCellFactory(column -> new TableCell<>() {

            private final Button modifierButton = new Button("Modifier");
            private final Button statutButton = new Button();

            private final HBox container =
                    new HBox(6, modifierButton, statutButton);

            {
                    container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    modifierButton.setMinWidth(76);
                    modifierButton.setPrefWidth(76);

                    statutButton.setMinWidth(82);
                    statutButton.setPrefWidth(82);

                modifierButton.getStyleClass().addAll(
                    "supplier-action-button",
                    "supplier-edit-button"
                );

                statutButton.getStyleClass().addAll(
                    "supplier-action-button"
                );

                modifierButton.setOnAction(event -> {
                    Fournisseur fournisseur = getTableView()
                            .getItems().get(getIndex());

                    ouvrirFormulaire(fournisseur);
                });

                statutButton.setOnAction(event -> {
                    Fournisseur fournisseur = getTableView()
                            .getItems().get(getIndex());

                    if (fournisseur.isActif()) {
                        fournisseurService.desactiverFournisseur(
                                fournisseur.getId()
                        );
                    } else {
                        fournisseurService.activerFournisseur(
                                fournisseur.getId()
                        );
                    }

                    actualiserDonnees();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 ||
                        getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Fournisseur fournisseur = getTableView()
                            .getItems().get(getIndex());

                    statutButton.setText(
                            fournisseur.isActif() ? "Désactiver" : "Activer"
                    );
                    statutButton.getStyleClass().removeAll(
                        "supplier-activate-button",
                        "supplier-deactivate-button"
                    );

                    statutButton.getStyleClass().add(
                        fournisseur.isActif()
                            ? "supplier-deactivate-button"
                            : "supplier-activate-button"
                    );

                    setGraphic(container);
                }
            }
        });
    }

    private void configurerRecherche() {

        searchField.textProperty().addListener(
                (observable, ancienneValeur, nouvelleValeur) -> {

                    String recherche = nouvelleValeur == null
                            ? ""
                            : nouvelleValeur.toLowerCase().trim();

                    fournisseursFiltres.setPredicate(fournisseur ->
                            fournisseur.getNom().toLowerCase().contains(recherche)
                            || fournisseur.getCodeFournisseur()
                                    .toLowerCase().contains(recherche)
                            || fournisseur.getTelephone()
                                    .toLowerCase().contains(recherche)
                            || fournisseur.getEmail()
                                    .toLowerCase().contains(recherche)
                            || fournisseur.getVille().toLowerCase()
                                    .contains(recherche)
                    );
                }
        );
    }

    @FXML
    private void handleAddSupplier() {
        ouvrirFormulaire(null);
    }

    @FXML
    private void handleRefresh() {
        actualiserDonnees();
    }

    private void ouvrirFormulaire(Fournisseur fournisseurAModifier) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/gestion/views/SupplierForm.fxml"
                    )
            );

            Parent root = loader.load();

            SupplierFormController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(
                    fournisseurAModifier == null
                            ? "Nouveau fournisseur"
                            : "Modifier fournisseur"
            );

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            controller.setStage(stage);
            controller.setOnSupplierSaved(this::actualiserDonnees);

            if (fournisseurAModifier != null) {
                controller.setSupplierToEdit(fournisseurAModifier);
            }

            stage.showAndWait();

        } catch (IOException e) {
            afficherErreur(
                    "Impossible d'ouvrir le formulaire fournisseur."
            );
            e.printStackTrace();
        }
    }

    private void actualiserDonnees() {
        fournisseurs.setAll(fournisseurService.getAllFournisseurs());
        actualiserStatistiques();
        suppliersTable.refresh();
    }

    private void actualiserStatistiques() {

        int total = fournisseurs.size();

        long actifs = fournisseurs.stream()
                .filter(Fournisseur::isActif)
                .count();

        totalSuppliersLabel.setText(String.valueOf(total));
        activeSuppliersLabel.setText(String.valueOf(actifs));
        inactiveSuppliersLabel.setText(String.valueOf(total - actifs));
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
