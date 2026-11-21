
package com.gestion.controller;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.gestion.model.StatutPaiement;
import com.gestion.model.StatutVente;
import com.gestion.model.Vente;
import com.gestion.service.VenteService;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class SalesController {

    @FXML private Label totalSalesLabel;
    @FXML private Label revenueLabel;
    @FXML private Label todaySalesLabel;
    @FXML private Label cancelledSalesLabel;

    @FXML private TextField searchField;
    @FXML private ComboBox<StatutVente> statusFilter;

    @FXML private TableView<Vente> salesTable;

    @FXML private TableColumn<Vente, String> referenceColumn;
    @FXML private TableColumn<Vente, String> clientColumn;
    @FXML private TableColumn<Vente, String> dateColumn;
    @FXML private TableColumn<Vente, String> totalColumn;
    @FXML private TableColumn<Vente, String> paymentStatusColumn;
    @FXML private TableColumn<Vente, String> saleStatusColumn;
    @FXML private TableColumn<Vente, Void> actionsColumn;

    private final VenteService venteService =
            VenteService.getInstance();

    private ObservableList<Vente> ventes;
    private FilteredList<Vente> ventesFiltrees;

    private static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {

        initialiserDonnees();
        configurerTableau();
        configurerFiltres();
        actualiserStatistiques();
    }

    // =========================================
    // INITIALISATION
    // =========================================

    private void initialiserDonnees() {

        ventes = FXCollections.observableArrayList(
                venteService.getAllVentes()
        );

        ventesFiltrees = new FilteredList<>(
                ventes,
                vente -> true
        );

        salesTable.setItems(ventesFiltrees);
    }

    // =========================================
    // CONFIGURATION DU TABLEAU
    // =========================================

    private void configurerTableau() {

        salesTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        referenceColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getReference()
                )
        );

        clientColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getClient().getNomAffichage()
                )
        );

        dateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDateVente().format(FORMAT_DATE)
                )
        );

        totalColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getMontantTotalTTC()
                                .setScale(2, RoundingMode.HALF_UP)
                                + " DH"
                )
        );

        paymentStatusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        formaterStatutPaiement(
                                data.getValue().getStatutPaiement()
                        )
                )
        );

        saleStatusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        formaterStatutVente(
                                data.getValue().getStatutVente()
                        )
                )
        );

        configurerStatuts();
        configurerActions();
    }

    // =========================================
    // FORMATER LES STATUTS
    // =========================================

    private String formaterStatutPaiement(
            StatutPaiement statut) {

        return switch (statut) {
            case NON_PAYE -> "Non payé";
            case PARTIELLEMENT_PAYE -> "Partiellement payé";
            case PAYE -> "Payé";
        };
    }

    private String formaterStatutVente(StatutVente statut) {

        return switch (statut) {
            case BROUILLON -> "Brouillon";
            case VALIDEE -> "Validée";
            case ANNULEE -> "Annulée";
        };
    }

    private void configurerStatuts() {

        saleStatusColumn.setCellFactory(column ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String statut, boolean empty) {

                        super.updateItem(statut, empty);

                        if (empty || statut == null) {
                            setText(null);
                            setStyle("");
                            return;
                        }

                        setText(statut);

                        switch (statut) {
                            case "Validée" ->
                                    setStyle(
                                            "-fx-text-fill: #23845B;"
                                            + "-fx-font-weight: bold;"
                                    );

                            case "Annulée" ->
                                    setStyle(
                                            "-fx-text-fill: #C74747;"
                                            + "-fx-font-weight: bold;"
                                    );

                            default ->
                                    setStyle(
                                            "-fx-text-fill: #D99132;"
                                            + "-fx-font-weight: bold;"
                                    );
                        }
                    }
                }
        );

        paymentStatusColumn.setCellFactory(column ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String statut, boolean empty) {

                        super.updateItem(statut, empty);

                        if (empty || statut == null) {
                            setText(null);
                            setStyle("");
                            return;
                        }

                        setText(statut);

                        switch (statut) {
                            case "Payé" ->
                                    setStyle(
                                            "-fx-text-fill: #23845B;"
                                            + "-fx-font-weight: bold;"
                                    );

                            case "Non payé" ->
                                    setStyle(
                                            "-fx-text-fill: #C74747;"
                                            + "-fx-font-weight: bold;"
                                    );

                            default ->
                                    setStyle(
                                            "-fx-text-fill: #D99132;"
                                            + "-fx-font-weight: bold;"
                                    );
                        }
                    }
                }
        );
    }

    // =========================================
    // ACTIONS
    // =========================================

    private void configurerActions() {

        actionsColumn.setCellFactory(column ->
                new TableCell<>() {

                    private final Button detailsButton =
                            new Button("Détails");

                    {
                        detailsButton.getStyleClass().add(
                                "secondary-button"
                        );

                        detailsButton.setOnAction(event -> {
                            Vente vente = getTableView().getItems().get(getIndex());
                            ouvrirDetailsVente(vente);
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item, boolean empty) {

                        super.updateItem(item, empty);

                        setGraphic(
                                empty ? null : detailsButton
                        );
                    }
                }
        );
    }

    // =========================================
    // RECHERCHE ET FILTRES
    // =========================================

    private void configurerFiltres() {

        statusFilter.setItems(
                FXCollections.observableArrayList(
                        StatutVente.values()
                )
        );

        statusFilter.setConverter(new StringConverter<>() {

            @Override
            public String toString(StatutVente statut) {

                return statut == null
                        ? ""
                        : formaterStatutVente(statut);
            }

            @Override
            public StatutVente fromString(String texte) {
                return null;
            }
        });

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        appliquerFiltres()
        );

        statusFilter.valueProperty().addListener(
                (observable, oldValue, newValue) ->
                        appliquerFiltres()
        );
    }

    private void appliquerFiltres() {

        String recherche = searchField.getText() == null
                ? ""
                : searchField.getText().toLowerCase().trim();

        StatutVente statutSelectionne =
                statusFilter.getValue();

        ventesFiltrees.setPredicate(vente -> {

            boolean correspondRecherche =
                    recherche.isEmpty()
                    || vente.getReference().toLowerCase()
                            .contains(recherche)
                    || vente.getClient().getNomAffichage()
                            .toLowerCase().contains(recherche);

            boolean correspondStatut =
                    statutSelectionne == null
                    || vente.getStatutVente() == statutSelectionne;

            return correspondRecherche && correspondStatut;
        });
    }

    // =========================================
    // STATISTIQUES
    // =========================================

    private void actualiserStatistiques() {

        totalSalesLabel.setText(
                String.valueOf(venteService.compterVentes())
        );

        revenueLabel.setText(
                venteService.calculerChiffreAffaires()
                        .setScale(2, RoundingMode.HALF_UP)
                        + " DH"
        );

        LocalDate aujourdHui = LocalDate.now();

        long ventesDuJour = ventes.stream()
                .filter(vente ->
                        vente.getStatutVente() == StatutVente.VALIDEE
                        && vente.getDateVente().toLocalDate()
                                .equals(aujourdHui)
                )
                .count();

        todaySalesLabel.setText(
                String.valueOf(ventesDuJour)
        );

        cancelledSalesLabel.setText(
                String.valueOf(venteService.compterVentesAnnulees())
        );
    }

    // =========================================
    // ACTUALISATION
    // =========================================

    @FXML
    private void handleRefresh() {

        ventes.setAll(venteService.getAllVentes());

        appliquerFiltres();
        actualiserStatistiques();

        salesTable.refresh();
    }

    // =========================================
    // NOUVELLE VENTE
    // =========================================


    @FXML
    private void handleAddSale() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/gestion/views/SaleForm.fxml"
                    )
            );

            Scene scene = new Scene(loader.load());

            Stage saleStage = new Stage();

            saleStage.setTitle("Nouvelle vente");
            saleStage.setScene(scene);

            saleStage.initModality(Modality.APPLICATION_MODAL);

            saleStage.setResizable(true);

            saleStage.setOnHidden(event -> {
                handleRefresh();
            });

            saleStage.showAndWait();

        } catch (Exception e) {

            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText("Impossible d'ouvrir le formulaire");
            alert.setContentText(e.getClass().getSimpleName()
                    + " : " + e.getMessage());

            alert.showAndWait();
        }
    }


    // =========================================
    // MESSAGE
    // =========================================

    private void afficherInformation(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Gestion des ventes");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
    private void ouvrirDetailsVente(Vente vente) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gestion/views/SaleDetails.fxml")
            );

            Parent root = loader.load();

            SaleDetailsController controller = loader.getController();
            controller.setVente(vente);

            Stage stage = new Stage();
            stage.setTitle("Détails de la vente " + vente.getReference());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            afficherInformation(
                "Impossible d'ouvrir les détails de cette vente : " + e.getMessage()
            );
        }
    }
}
