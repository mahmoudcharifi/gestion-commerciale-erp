package com.gestion.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gestion.model.Client;
import com.gestion.model.TypeClient;

public class ClientService {

    private final List<Client> clients = new ArrayList<>();

    private long prochainId = 1;
    private long prochainCode = 1;
    private Client clientComptoir;
    public Client getClientComptoir() {

        if (clientComptoir == null) {

            clientComptoir = new Client(
                    0L,
                    "CLI-COMPT",
                    TypeClient.PARTICULIER,
                    "Comptoir",
                    "Client",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

        }

        return clientComptoir;
    }

    private static final ClientService INSTANCE = new ClientService();

    private ClientService() {
    }

    public static ClientService getInstance() {
        return INSTANCE;
    }

    public Client ajouterClient(Client client) {
        verifierClient(client);

        client.setActif(true);
        clients.add(client);

        return client;
    }

    public void modifierClient(Client client) {
        verifierClient(client);

        Client clientExistant = trouverParId(client.getId());

        if (clientExistant == null) {
            throw new IllegalArgumentException("Client introuvable.");
        }

        clientExistant.setNom(client.getNom());
        clientExistant.setPrenom(client.getPrenom());
        clientExistant.setTelephone(client.getTelephone());
        clientExistant.setEmail(client.getEmail());
        clientExistant.setAdresse(client.getAdresse());
        clientExistant.setVille(client.getVille());
        clientExistant.setRaisonSociale(client.getRaisonSociale());
        clientExistant.setIce(client.getIce());
        clientExistant.setIdentifiantFiscal(client.getIdentifiantFiscal());
        clientExistant.setRegistreCommerce(client.getRegistreCommerce());
        clientExistant.setPersonneContact(client.getPersonneContact());
    }

    public List<Client> getAllClients() {
        return Collections.unmodifiableList(new ArrayList<>(clients));
    }

    public List<Client> rechercherClients(String recherche) {
        if (recherche == null || recherche.isBlank()) {
            return getAllClients();
        }

        String terme = recherche.trim().toLowerCase();

        return clients.stream()
                .filter(Client::isActif)
                .filter(client ->
                        contient(client.getCodeClient(), terme)
                        || contient(client.getNomAffichage(), terme)
                        || contient(client.getTelephone(), terme)
                        || contient(client.getEmail(), terme)
                        || contient(client.getIce(), terme)
                )
                .toList();
    }

    public void desactiverClient(Long id) {
        Client client = trouverParId(id);

        if (client == null) {
            throw new IllegalArgumentException("Client introuvable.");
        }

        client.setActif(false);
    }

    public String genererCodeClient() {
        return String.format("CLI-%03d", prochainCode++);
    }

    public Long genererId() {
        return prochainId++;
    }

    public Client trouverParId(Long id) {
        return clients.stream()
                .filter(client -> client.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void verifierClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client est obligatoire.");
        }

        if (client.getTypeClient() == null) {
            throw new IllegalArgumentException("Le type du client est obligatoire.");
        }

        if (client.getTypeClient() == TypeClient.PARTICULIER) {
            if (client.getNom() == null || client.getNom().isBlank()
                    || client.getPrenom() == null || client.getPrenom().isBlank()) {
                throw new IllegalArgumentException(
                        "Le nom et le prénom du particulier sont obligatoires."
                );
            }
        }

        if (client.getTypeClient() == TypeClient.ENTREPRISE) {
            if (client.getRaisonSociale() == null
                    || client.getRaisonSociale().isBlank()) {
                throw new IllegalArgumentException(
                        "La raison sociale de l'entreprise est obligatoire."
                );
            }
        }
    }

    private boolean contient(String valeur, String terme) {
        return valeur != null && valeur.toLowerCase().contains(terme);
    }
}
