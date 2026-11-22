
package com.gestion.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

import com.gestion.model.Client;
import com.gestion.model.Facture;
import com.gestion.model.LigneVente;
import com.gestion.model.Vente;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class FacturePdfService {

    private static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void genererFacturePdf(Facture facture, String cheminFichier) {

        if (facture == null) {
            throw new IllegalArgumentException("La facture ne peut pas être null.");
        }

        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(cheminFichier));

            document.open();

            ajouterEntete(document, facture);

            ajouterInformationsClient(document, facture);

            ajouterTableauProduits(document, facture);
            ajouterRecapitulatif(document, facture);
            ajouterInformationsPaiement(document, facture);
            ajouterPiedDePage(document);

            document.close();

        } catch (DocumentException | IOException e) {
            throw new RuntimeException(
                    "Erreur lors de la génération de la facture PDF.", e
            );
        }
    }

    private void ajouterEntete(Document document, Facture facture)
            throws DocumentException {

        Font titreFont = new Font(Font.HELVETICA, 22, Font.BOLD);
        Font infoFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

        Paragraph titre = new Paragraph("FACTURE", titreFont);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(20);

        document.add(titre);

        Paragraph reference = new Paragraph(
                "Référence : " + facture.getReference(), infoFont
        );

        Paragraph date = new Paragraph(
                "Date d'émission : "
                        + facture.getDateEmission().format(FORMAT_DATE),
                infoFont
        );

        Paragraph statut = new Paragraph(
                "Statut : " + facture.getStatut(), infoFont
        );

        reference.setSpacingAfter(8);
        date.setSpacingAfter(8);
        statut.setSpacingAfter(20);

        document.add(reference);
        document.add(date);
        document.add(statut);
    }

    private void ajouterInformationsClient(Document document, Facture facture)
            throws DocumentException {

        Vente vente = facture.getVente();
        Client client = vente.getClient();

        Font sectionFont = new Font(Font.HELVETICA, 13, Font.BOLD);
        Font infoFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

        Paragraph titre = new Paragraph("INFORMATIONS DU CLIENT", sectionFont);
        titre.setSpacingAfter(10);

        document.add(titre);

        document.add(new Paragraph(
                "Nom / Raison sociale : " + valeur(client.getNomAffichage()),
                infoFont
        ));

        document.add(new Paragraph(
                "Adresse : " + valeur(client.getAdresse()),
                infoFont
        ));

        document.add(new Paragraph(
                "Ville : " + valeur(client.getVille()),
                infoFont
        ));

        document.add(new Paragraph(
                "Téléphone : " + valeur(client.getTelephone()),
                infoFont
        ));

        document.add(new Paragraph(
                "Email : " + valeur(client.getEmail()),
                infoFont
        ));

        if (client.getIce() != null && !client.getIce().isBlank()) {
            document.add(new Paragraph(
                    "ICE : " + client.getIce(),
                    infoFont
            ));
        }

        document.add(new Paragraph(" "));
    }
    private String valeur(String texte) {
        return texte == null || texte.isBlank() ? "Non renseigné" : texte;
    }

    private void ajouterTableauProduits(Document document, Facture facture)
            throws DocumentException {

        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        Font bodyFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        Paragraph titre = new Paragraph(
                "DÉTAILS DES PRODUITS",
                new Font(Font.HELVETICA, 13, Font.BOLD)
        );

        titre.setSpacingBefore(15);
        titre.setSpacingAfter(10);
        document.add(titre);

        PdfPTable tableau = new PdfPTable(
                new float[]{3, 1, 1.5f, 1, 1.5f}
        );

        tableau.setWidthPercentage(100);
        tableau.setSpacingAfter(15);

        String[] colonnes = {
                "Produit",
                "Qté",
                "Prix HT",
                "TVA",
                "Total HT"
        };

        for (String colonne : colonnes) {
            PdfPCell cellule = new PdfPCell(
                    new Phrase(colonne, headerFont)
            );

            cellule.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellule.setPadding(7);
            tableau.addCell(cellule);
        }

        Vente vente = facture.getVente();

        for (LigneVente ligne : vente.getLignes()) {

            ajouterCellule(tableau, ligne.getNomProduit(), bodyFont);
            ajouterCellule(tableau, String.valueOf(ligne.getQuantite()), bodyFont);

            ajouterCellule(
                    tableau,
                    formaterMontant(ligne.getPrixUnitaire()),
                    bodyFont
            );

            ajouterCellule(
                    tableau,
                    ligne.getTauxTVA().stripTrailingZeros().toPlainString() + "%",
                    bodyFont
            );

            ajouterCellule(
                    tableau,
                    formaterMontant(ligne.getSousTotalHT()),
                    bodyFont
            );
        }

        document.add(tableau);
    }

    private void ajouterCellule(
            PdfPTable tableau,
            String texte,
            Font font) {

        PdfPCell cellule = new PdfPCell(new Phrase(texte, font));
        cellule.setPadding(7);
        cellule.setVerticalAlignment(Element.ALIGN_MIDDLE);

        tableau.addCell(cellule);
    }

    private String formaterMontant(BigDecimal montant) {

        return montant.setScale(2, RoundingMode.HALF_UP)
                .toPlainString() + " DH";
    }


    private void ajouterRecapitulatif(Document document, Facture facture)
            throws DocumentException {

        Font labelFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
        Font totalFont = new Font(Font.HELVETICA, 13, Font.BOLD);

        Vente vente = facture.getVente();

        PdfPTable tableau = new PdfPTable(new float[]{2, 1});
        tableau.setWidthPercentage(50);
        tableau.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tableau.setSpacingBefore(10);

        ajouterCellule(tableau, "Total HT :", labelFont);
        ajouterCellule(
                tableau,
                formaterMontant(facture.getMontantTotalHT()),
                labelFont
        );

        ajouterCellule(tableau, "Remise globale :", labelFont);
        ajouterCellule(
                tableau,
                formaterMontant(vente.getRemiseGlobale()),
                labelFont
        );

        ajouterCellule(tableau, "TVA :", labelFont);
        ajouterCellule(
                tableau,
                formaterMontant(facture.getMontantTVA()),
                labelFont
        );

        ajouterCellule(tableau, "TOTAL TTC :", totalFont);
        ajouterCellule(
                tableau,
                formaterMontant(facture.getMontantTotalTTC()),
                totalFont
        );

        document.add(tableau);
    }

    private void ajouterInformationsPaiement(Document document, Facture facture)
            throws DocumentException {

        Vente vente = facture.getVente();

        Font titreFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font infoFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

        Paragraph titre = new Paragraph("INFORMATIONS DE PAIEMENT", titreFont);
        titre.setSpacingBefore(15);
        titre.setSpacingAfter(10);

        document.add(titre);

        document.add(new Paragraph(
                "Montant payé : " + formaterMontant(vente.getMontantPaye()),
                infoFont
        ));

        document.add(new Paragraph(
                "Reste à payer : " + formaterMontant(vente.getResteAPayer()),
                infoFont
        ));

        document.add(new Paragraph(
                "Statut du paiement : " + vente.getStatutPaiement(),
                infoFont
        ));
    }

    private void ajouterPiedDePage(Document document)
            throws DocumentException {

        Font footerFont = new Font(
                Font.HELVETICA,
                9,
                Font.ITALIC
        );

        Paragraph separator = new Paragraph(
                "____________________________________________________________"
        );

        separator.setSpacingBefore(25);
        document.add(separator);

        Paragraph message = new Paragraph(
                "Merci pour votre confiance !",
                footerFont
        );

        message.setAlignment(Element.ALIGN_CENTER);
        message.setSpacingBefore(10);

        document.add(message);

        Paragraph note = new Paragraph(
                "Document généré automatiquement par Gestion Commerciale ERP.",
                footerFont
        );

        note.setAlignment(Element.ALIGN_CENTER);
        note.setSpacingBefore(5);

        document.add(note);
    }
}
