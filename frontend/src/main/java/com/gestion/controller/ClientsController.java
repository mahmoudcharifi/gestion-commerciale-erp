package com.gestion.controller;

import java.io.IOException;
import java.util.Optional;

import com.gestion.model.Client;
import com.gestion.model.TypeClient;
import com.gestion.service.ClientService;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ClientsController {

    @FXML private Label totalClientsLabel;
    @FXML private Label particuliersLabel;
    @FXML private Label entreprisesLabel;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilterComboBox;

    @FXML private TableView<Client> clientsTable;

    @FXML private TableColumn<Client, String> codeColumn;
    @FXML private TableColumn<Client, String> nameColumn;
    @FXML private TableColumn<Client, String> typeColumn;
    @FXML private TableColumn<Client, String> phoneColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TableColumn<Client, String> cityColumn;
    @FXML private TableColumn<Client, Void> actionsColumn;

    private final ClientService clientService = ClientService.getInstance();

    private final ObservableList<Client> clients =
            FXCollections.observableArrayList();

    private FilteredList<Client> filteredClients;

    @FXML
    public void initialize() {
        configurerFiltres();
        configurerTableau();
        chargerClients();
        configurerRecherche();
    }

    private void configurerFiltres() {
        typeFilterComboBox.setItems(FXCollections.observableArrayList(
                "Tous les types",
                "Particuliers",
                "Entreprises"
        ));

        typeFilterComboBox.setValue("Tous les types");

        typeFilterComboBox.setOnAction(event -> appliquerFiltres());
    }

    private void configurerTableau() {
        codeColumn.setCellValueFactory(
                new PropertyValueFactory<>("codeClient")
        );

        nameColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getNomAffichage()
                )
        );

        typeColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTypeClient().name()
                                .equals("PARTICULIER")
                                ? "Particulier" : "Entreprise"
                )
        );

        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>("telephone")
        );

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );

        cityColumn.setCellValueFactory(
                new PropertyValueFactory<>("ville")
        );

        configurerActions();
    }

    private void configurerActions() {
        actionsColumn.setCellFactory(column -> new TableCell<>() {

            private final Button deactivateButton =
                    new Button("Désactiver");

            {
                deactivateButton.getStyleClass().addAll(
                        "button", "danger-button"
                );

                deactivateButton.setOnAction(event -> {
                    Client client = getTableView().getItems().get(getIndex());
                    desactiverClient(client);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                setGraphic(empty ? null : new HBox(5, deactivateButton));
            }
        });
    }

    private void chargerClients() {
        clients.setAll(clientService.getAllClients());

        filteredClients = new FilteredList<>(clients, client -> true);

        clientsTable.setItems(filteredClients);

        actualiserStatistiques();
        appliquerFiltres();
    }

    private void configurerRecherche() {
        searchField.textProperty().addListener(
                (observable, ancienneValeur, nouvelleValeur) ->
                        appliquerFiltres()
        );
    }

    private void appliquerFiltres() {
        if (filteredClients == null) {
            return;
        }

        String recherche = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        String typeSelectionne = typeFilterComboBox.getValue();

        filteredClients.setPredicate(client -> {

            if (!client.isActif()) {
                return false;
            }

            boolean correspondRecherche =
                    recherche.isEmpty()
                    || contient(client.getCodeClient(), recherche)
                    || contient(client.getNomAffichage(), recherche)
                    || contient(client.getTelephone(), recherche)
                    || contient(client.getEmail(), recherche)
                    || contient(client.getIce(), recherche);

            boolean correspondType =
                    "Tous les types".equals(typeSelectionne)
                    || ("Particuliers".equals(typeSelectionne)
                    && client.getTypeClient() == TypeClient.PARTICULIER)
                    || ("Entreprises".equals(typeSelectionne)
                    && client.getTypeClient() == TypeClient.ENTREPRISE);

            return correspondRecherche && correspondType;
        });
    }

    private boolean contient(String valeur, String recherche) {
        return valeur != null
                && valeur.toLowerCase().contains(recherche);
    }

    private void actualiserStatistiques() {
        long total = clients.stream()
                .filter(Client::isActif)
                .count();

        long particuliers = clients.stream()
                .filter(Client::isActif)
                .filter(client ->
                        client.getTypeClient() == TypeClient.PARTICULIER)
                .count();

        long entreprises = clients.stream()
                .filter(Client::isActif)
                .filter(client ->
                        client.getTypeClient() == TypeClient.ENTREPRISE)
                .count();

        totalClientsLabel.setText(String.valueOf(total));
        particuliersLabel.setText(String.valueOf(particuliers));
        entreprisesLabel.setText(String.valueOf(entreprises));
    }

    private void desactiverClient(Client client) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Désactivation du client");
        confirmation.setHeaderText("Désactiver ce client ?");
        confirmation.setContentText(
                "Le client " + client.getNomAffichage()
                + " ne sera plus disponible dans la liste active."
        );

        Optional<ButtonType> resultat = confirmation.showAndWait();

        if (resultat.isPresent()
                && resultat.get() == ButtonType.OK) {

            clientService.desactiverClient(client.getId());

            chargerClients();
        }
    }

@FXML
private void openClientForm() {
    ouvrirFormulaire(null);
}

private void ouvrirFormulaire(Client clientAModifier) {
    try {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gestion/views/ClientForm.fxml")
        );

        Parent root = loader.load();

        ClientFormController controller = loader.getController();

        Stage stage = new Stage();
        stage.setTitle(
                clientAModifier == null
                        ? "Nouveau client"
                        : "Modifier le client"
        );

        controller.setStage(stage);
        controller.setOnClientSaved(client -> {
            try {
                if (clientAModifier == null) {
                    client = new Client(
                            clientService.genererId(),
                            clientService.genererCodeClient(),
                            client.getTypeClient(),
                            client.getNom(),
                            client.getPrenom(),
                            client.getTelephone(),
                            client.getEmail(),
                            client.getAdresse(),
                            client.getVille(),
                            client.getRaisonSociale(),
                            client.getIce(),
                            client.getIdentifiantFiscal(),
                            client.getRegistreCommerce(),
                            client.getPersonneContact()
                    );

                    clientService.ajouterClient(client);
                } else {
                    clientService.modifierClient(client);
                }

                chargerClients();

            } catch (IllegalArgumentException e) {
                afficherErreur(e.getMessage());
            }
        });

        if (clientAModifier != null) {
            controller.setClientToEdit(clientAModifier);
        }

        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.showAndWait();

    } catch (IOException e) {
        e.printStackTrace();
        afficherErreur("Impossible d'ouvrir le formulaire client.");
    }
}

private void afficherErreur(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Erreur");
    alert.setHeaderText("Opération impossible");
    alert.setContentText(message);
    alert.showAndWait();
}

}
