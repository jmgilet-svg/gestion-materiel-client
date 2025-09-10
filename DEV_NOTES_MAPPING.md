# DEV Notes Mapping

## Devis / Quote
- **Models**
  - `com.materiel.client.model.Quote` extends `BaseDocument` (fields: `id`, `number`, `date`, `customerId`, `customerName`, `lines`, `totalHT`, `totalTVA`, `totalTTC`, `status`, `notes`).
  - `com.materiel.client.model.Devis` (legacy model) with fields: `id`, `numero`, `dateCreation`, `dateValidite`, `client`, `statut`, `version`, `montantHT`, `montantTVA`, `montantTTC`.
- **Services**
  - Interface `com.materiel.client.service.DevisService`.
  - Mock implementation `com.materiel.client.service.impl.MockDevisService`.
  - API client `com.materiel.client.service.impl.ApiDevisService`.
- **UI**
  - List panel `com.materiel.client.view.devis.DevisListPanel`.
  - Editor dialog `com.materiel.client.view.devis.DevisEditDialog`.

## Commande / Order
- **Models**
  - `com.materiel.client.model.Order` extends `BaseDocument` with field `quoteId`.
  - `com.materiel.client.model.Commande` (legacy) with fields: `id`, `numero`, `dateCreation`, `dateLivraisonPrevue`, `dateLivraisonEffective`, `client`, `devisOrigine`, `statut`, `montantHT`, `montantTVA`, `montantTTC`, `adresseLivraison`, `commentaires`, `responsablePreparation`.
- **Services**
  - Interface `com.materiel.client.service.OrderService` + mock `com.materiel.client.mock.OrderServiceMock`.
  - Legacy interface `com.materiel.client.service.CommandeService` with `service.impl.MockCommandeService` & `service.impl.ApiCommandeService`.
- **UI**
  - Legacy list panel `com.materiel.client.view.commandes.CommandeListPanel` and editor `com.materiel.client.view.commandes.CommandeEditDialog`.
  - Stub panel `com.materiel.client.view.commande.OrdersPanel`.

## Bon de livraison / DeliveryNote
- **Models**
  - `com.materiel.client.model.DeliveryNote` extends `BaseDocument` with fields `orderId`, `List<UUID> interventionIds`.
  - `com.materiel.client.model.BonLivraison` (legacy) with many logistics fields (client, commandeOrigine, dates, adresse, chauffeur, vehicule, etc.).
- **Services**
  - Interface `com.materiel.client.service.DeliveryNoteService` + mock `com.materiel.client.mock.DeliveryNoteServiceMock`.
  - Legacy interface `com.materiel.client.service.BonLivraisonService` with `service.impl.MockBonLivraisonService`.
- **UI**
  - Legacy list panel `com.materiel.client.view.livraisons.BonLivraisonListPanel` and editor `com.materiel.client.view.livraisons.BonLivraisonEditDialog`.
  - Stub panel `com.materiel.client.view.bl.DeliveryNotesPanel`.

## Facture / Invoice
- **Models**
  - `com.materiel.client.model.Invoice` extends `BaseDocument` with fields `quoteId`, `List<UUID> deliveryNoteIds`.
  - No separate `Facture` class found.
- **Services**
  - Interface `com.materiel.client.service.InvoiceService` + mock `com.materiel.client.mock.InvoiceServiceMock`.
- **UI**
  - Stub list panel `com.materiel.client.view.facture.InvoicesPanel` (no dedicated editor yet).

## Shared Utility
- `com.materiel.client.model.DocumentLine` describes lines: `UUID id`, `designation`, `unite`, `quantite`, `prixUnitaireHT`, `remisePct`, `tvaPct`.
- `com.materiel.client.util.DocumentTotalsCalculator` computes totals for a list of `DocumentLine`.
- Reusable Swing components for lines & totals exist under `com.materiel.client.view.doc` (`DocumentLineTable`, `DocumentLineTableModel`, `DocumentTotalsPanel`).
