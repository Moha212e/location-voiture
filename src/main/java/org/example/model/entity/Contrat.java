package org.example.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Classe représentant un contrat de location de véhicule.
 */
public class Contrat implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private String idContrat;
    private double caution;
    private String typeAssurance;
    private List<String> options;
    private boolean estSigne;
    private StatutContrat statutContrat;
    private double prixAssurance;
    private double prixTotal;
    
    @JsonIgnore
    private Reservation reservation;
    
    // Identifiant de la réservation pour la sérialisation
    private int reservationId;
    
    // Informations du client et de la voiture pour la sérialisation
    private String clientId;
    private String clientName;
    private String clientSurname;
    private String carId;
    private String carBrand;
    private String carModel;

    /**
     * Constructeur par défaut.
     */
    public Contrat() {
        this.options = new ArrayList<>();
        this.statutContrat = StatutContrat.EN_ATTENTE;
        this.prixAssurance = 0.0;
        this.prixTotal = 0.0;
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param idContrat     Identifiant unique du contrat
     * @param caution       Montant de la caution
     * @param typeAssurance Type d'assurance (ex: "Tous risques", "Tiers", etc.)
     * @param estSigne      Indique si le contrat est signé
     * @param statutContrat Statut actuel du contrat
     */
    public Contrat(String idContrat, double caution, String typeAssurance, boolean estSigne, StatutContrat statutContrat) {
        this.idContrat = idContrat;
        this.caution = caution;
        this.typeAssurance = typeAssurance;
        this.options = new ArrayList<>();
        this.estSigne = estSigne;
        this.statutContrat = statutContrat;
        this.prixAssurance = calculerPrixAssurance(typeAssurance);
        this.prixTotal = 0.0; // Sera calculé quand la réservation sera associée
    }

    /**
     * Constructeur avec paramètres, sans ID de contrat (sera généré automatiquement).
     *
     * @param caution       Montant de la caution
     * @param typeAssurance Type d'assurance (ex: "Tous risques", "Tiers", etc.)
     * @param estSigne      Indique si le contrat est signé
     * @param statutContrat Statut actuel du contrat
     */
    public Contrat(double caution, String typeAssurance, boolean estSigne, StatutContrat statutContrat) {
        this.caution = caution;
        this.typeAssurance = typeAssurance;
        this.options = new ArrayList<>();
        this.estSigne = estSigne;
        this.statutContrat = statutContrat;
        this.prixAssurance = calculerPrixAssurance(typeAssurance);
        this.prixTotal = 0.0; // Sera calculé quand la réservation sera associée
    }

    /**
     * Obtient l'identifiant du contrat.
     *
     * @return L'identifiant du contrat
     */
    public String getIdContrat() {
        return idContrat;
    }

    /**
     * Définit l'identifiant du contrat.
     *
     * @param idContrat Le nouvel identifiant du contrat
     */
    public void setIdContrat(String idContrat) {
        this.idContrat = idContrat;
    }

    /**
     * Obtient le montant de la caution.
     *
     * @return Le montant de la caution
     */
    public double getCaution() {
        return caution;
    }

    /**
     * Définit le montant de la caution.
     *
     * @param caution Le nouveau montant de la caution
     */
    public void setCaution(double caution) {
        this.caution = caution;
        calculerPrixTotal();
    }

    /**
     * Obtient le type d'assurance.
     *
     * @return Le type d'assurance
     */
    public String getTypeAssurance() {
        return typeAssurance;
    }

    /**
     * Définit le type d'assurance.
     *
     * @param typeAssurance Le nouveau type d'assurance
     */
    public void setTypeAssurance(String typeAssurance) {
        this.typeAssurance = typeAssurance;
        this.prixAssurance = calculerPrixAssurance(typeAssurance);
        calculerPrixTotal();
    }

    /**
     * Obtient la liste des options.
     *
     * @return La liste des options
     */
    public List<String> getOptions() {
        return options;
    }

    /**
     * Définit la liste des options.
     *
     * @param options La nouvelle liste des options
     */
    public void setOptions(List<String> options) {
        this.options = options;
    }

    /**
     * Ajoute une option à la liste.
     *
     * @param option L'option à ajouter
     */
    public void ajouterOption(String option) {
        this.options.add(option);
    }

    /**
     * Supprime une option de la liste.
     *
     * @param option L'option à supprimer
     * @return true si l'option a été supprimée, false sinon
     */
    public boolean supprimerOption(String option) {
        return this.options.remove(option);
    }

    /**
     * Vérifie si le contrat est signé.
     *
     * @return true si le contrat est signé, false sinon
     */
    public boolean isEstSigne() {
        return estSigne;
    }

    /**
     * Définit si le contrat est signé.
     *
     * @param estSigne true si le contrat est signé, false sinon
     */
    public void setEstSigne(boolean estSigne) {
        this.estSigne = estSigne;
        if (estSigne) {
            this.statutContrat = StatutContrat.SIGNE;
        }
    }

    /**
     * Obtient le statut du contrat.
     *
     * @return Le statut du contrat
     */
    public StatutContrat getStatutContrat() {
        return statutContrat;
    }

    /**
     * Définit le statut du contrat.
     *
     * @param statutContrat Le nouveau statut du contrat
     */
    public void setStatutContrat(StatutContrat statutContrat) {
        this.statutContrat = statutContrat;
        if (statutContrat == StatutContrat.SIGNE) {
            this.estSigne = true;
        } else if (statutContrat == StatutContrat.ANNULE || statutContrat == StatutContrat.EXPIRE) {
            this.estSigne = false;
        }
    }
    
    /**
     * Obtient le prix de l'assurance.
     *
     * @return Le prix de l'assurance
     */
    public double getPrixAssurance() {
        return prixAssurance;
    }

    /**
     * Définit le prix de l'assurance.
     *
     * @param prixAssurance Le nouveau prix de l'assurance
     */
    public void setPrixAssurance(double prixAssurance) {
        this.prixAssurance = prixAssurance;
        calculerPrixTotal();
    }

    /**
     * Obtient le prix total du contrat.
     *
     * @return Le prix total du contrat
     */
    public double getPrixTotal() {
        return prixTotal;
    }

    /**
     * Définit le prix total du contrat.
     *
     * @param prixTotal Le nouveau prix total du contrat
     */
    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }
    
    /**
     * Obtient la réservation associée au contrat.
     *
     * @return La réservation associée
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * Définit la réservation associée au contrat.
     *
     * @param reservation La nouvelle réservation associée
     */
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        if (reservation != null) {
            this.reservationId = reservation.getIdReservation();
            
            // Copier les informations du client
            if (reservation.getClient() != null) {
                this.clientId = String.valueOf(reservation.getClient().getIdClient());
                this.clientName = reservation.getClient().getName();
                this.clientSurname = reservation.getClient().getSurname();
            }
            
            // Copier les informations de la voiture
            if (reservation.getCar() != null) {
                this.carId = reservation.getCar().getIdCar();
                this.carBrand = reservation.getCar().getBrand();
                this.carModel = reservation.getCar().getModel();
            }
        }
        calculerPrixTotal();
    }
    
    /**
     * Obtient l'identifiant de la réservation associée au contrat.
     *
     * @return L'identifiant de la réservation associée
     */
    public int getReservationId() {
        return reservationId;
    }

    /**
     * Définit l'identifiant de la réservation associée au contrat.
     *
     * @param reservationId L'identifiant de la réservation associée
     */
    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }
    
    /**
     * Obtient l'identifiant du client.
     *
     * @return L'identifiant du client
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Définit l'identifiant du client.
     *
     * @param clientId L'identifiant du client
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Obtient le nom du client.
     *
     * @return Le nom du client
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Définit le nom du client.
     *
     * @param clientName Le nom du client
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * Obtient le prénom du client.
     *
     * @return Le prénom du client
     */
    public String getClientSurname() {
        return clientSurname;
    }

    /**
     * Définit le prénom du client.
     *
     * @param clientSurname Le prénom du client
     */
    public void setClientSurname(String clientSurname) {
        this.clientSurname = clientSurname;
    }

    /**
     * Obtient l'identifiant de la voiture.
     *
     * @return L'identifiant de la voiture
     */
    public String getCarId() {
        return carId;
    }

    /**
     * Définit l'identifiant de la voiture.
     *
     * @param carId L'identifiant de la voiture
     */
    public void setCarId(String carId) {
        this.carId = carId;
    }

    /**
     * Obtient la marque de la voiture.
     *
     * @return La marque de la voiture
     */
    public String getCarBrand() {
        return carBrand;
    }

    /**
     * Définit la marque de la voiture.
     *
     * @param carBrand La marque de la voiture
     */
    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    /**
     * Obtient le modèle de la voiture.
     *
     * @return Le modèle de la voiture
     */
    public String getCarModel() {
        return carModel;
    }

    /**
     * Définit le modèle de la voiture.
     *
     * @param carModel Le modèle de la voiture
     */
    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }
    
    /**
     * Calcule le prix de l'assurance en fonction du type d'assurance.
     *
     * @param typeAssurance Le type d'assurance
     * @return Le prix de l'assurance
     */
    private double calculerPrixAssurance(String typeAssurance) {
        if (typeAssurance == null) {
            return 0.0;
        }
        
        switch (typeAssurance) {
            case "Tous risques":
                return 150.0;
            case "Tiers étendu":
                return 100.0;
            case "Tiers":
                return 50.0;
            default:
                return 75.0; // Prix par défaut pour "Autre"
        }
    }
    
    /**
     * Calcule le prix total du contrat en additionnant l'assurance, la caution et le prix de la réservation.
     */
    public void calculerPrixTotal() {
        double prixReservation = (reservation != null) ? reservation.getPrice() : 0.0;
        this.prixTotal = this.prixAssurance + this.caution + prixReservation;
    }

    @Override
    public String toString() {
        return "Contrat{idContrat='" + idContrat + "', caution=" + caution + ", typeAssurance='" + typeAssurance + "', options=" + options + ", estSigne=" + estSigne + ", statutContrat=" + statutContrat + ", prixAssurance=" + prixAssurance + ", prixTotal=" + prixTotal + ", clientId='" + clientId + "', clientName='" + clientName + "', clientSurname='" + clientSurname + "', carId='" + carId + "', carBrand='" + carBrand + "', carModel='" + carModel + "'}";
    }
}
