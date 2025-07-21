package org.example.model.entity;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ContratTest {

    @Test
    public void testDefaultConstructor() {
        Contrat contrat = new Contrat();
        assertNotNull(contrat);
        assertNull(contrat.getIdContrat());
        assertEquals(0.0, contrat.getCaution(), 0.001);
        assertNull(contrat.getTypeAssurance());
        assertNotNull(contrat.getOptions());
        assertTrue(contrat.getOptions().isEmpty());
        assertFalse(contrat.isEstSigne());
        assertEquals(StatutContrat.EN_ATTENTE, contrat.getStatutContrat());
        assertEquals(0.0, contrat.getPrixAssurance(), 0.001);
        assertEquals(0.0, contrat.getPrixTotal(), 0.001);
    }

    @Test
    public void testParameterizedConstructorWithoutId() {
        // Test du nouveau constructeur qui ne prend pas d'ID en paramètre
        Contrat contrat = new Contrat(1000.0, "Tous risques", true, StatutContrat.SIGNE);
        assertNull(contrat.getIdContrat()); // L'ID sera généré par la DAO
        assertEquals(1000.0, contrat.getCaution(), 0.001);
        assertEquals("Tous risques", contrat.getTypeAssurance());
        assertNotNull(contrat.getOptions());
        assertTrue(contrat.getOptions().isEmpty());
        assertTrue(contrat.isEstSigne());
        assertEquals(StatutContrat.SIGNE, contrat.getStatutContrat());
        assertEquals(150.0, contrat.getPrixAssurance(), 0.001); // Prix calculé pour "Tous risques"
        assertEquals(0.0, contrat.getPrixTotal(), 0.001); // Sera calculé quand la réservation sera associée
    }

    @Test
    public void testParameterizedConstructorWithId() {
        // Test du constructeur qui prend un ID en paramètre
        Contrat contrat = new Contrat("C123", 1000.0, "Tiers", false, StatutContrat.EN_ATTENTE);
        assertEquals("C123", contrat.getIdContrat());
        assertEquals(1000.0, contrat.getCaution(), 0.001);
        assertEquals("Tiers", contrat.getTypeAssurance());
        assertNotNull(contrat.getOptions());
        assertTrue(contrat.getOptions().isEmpty());
        assertFalse(contrat.isEstSigne());
        assertEquals(StatutContrat.EN_ATTENTE, contrat.getStatutContrat());
        assertEquals(50.0, contrat.getPrixAssurance(), 0.001); // Prix calculé pour "Tiers"
        assertEquals(0.0, contrat.getPrixTotal(), 0.001); // Sera calculé quand la réservation sera associée
    }

    @Test
    public void testSettersAndGetters() {
        Contrat contrat = new Contrat();
        contrat.setIdContrat("C456");
        contrat.setCaution(1500.0);
        contrat.setTypeAssurance("Tiers étendu");
        contrat.setEstSigne(true);
        contrat.setStatutContrat(StatutContrat.SIGNE);
        
        List<String> options = new ArrayList<>();
        options.add("Conducteur supplémentaire");
        options.add("GPS");
        contrat.setOptions(options);
        
        assertEquals("C456", contrat.getIdContrat());
        assertEquals(1500.0, contrat.getCaution(), 0.001);
        assertEquals("Tiers étendu", contrat.getTypeAssurance());
        assertTrue(contrat.isEstSigne());
        assertEquals(StatutContrat.SIGNE, contrat.getStatutContrat());
        assertEquals(2, contrat.getOptions().size());
        assertTrue(contrat.getOptions().contains("Conducteur supplémentaire"));
        assertTrue(contrat.getOptions().contains("GPS"));
        assertEquals(100.0, contrat.getPrixAssurance(), 0.001); // Prix calculé pour "Tiers étendu"
    }

    @Test
    public void testAddAndRemoveOptions() {
        Contrat contrat = new Contrat();
        
        // Ajouter des options
        contrat.ajouterOption("Conducteur supplémentaire");
        contrat.ajouterOption("GPS");
        contrat.ajouterOption("Siège bébé");
        
        assertEquals(3, contrat.getOptions().size());
        assertTrue(contrat.getOptions().contains("Conducteur supplémentaire"));
        assertTrue(contrat.getOptions().contains("GPS"));
        assertTrue(contrat.getOptions().contains("Siège bébé"));
        
        // Supprimer une option
        boolean removed = contrat.supprimerOption("GPS");
        assertTrue(removed);
        assertEquals(2, contrat.getOptions().size());
        assertFalse(contrat.getOptions().contains("GPS"));
        
        // Essayer de supprimer une option qui n'existe pas
        removed = contrat.supprimerOption("Option inexistante");
        assertFalse(removed);
        assertEquals(2, contrat.getOptions().size());
    }
    
    @Test
    public void testSignatureAndStatut() {
        Contrat contrat = new Contrat();
        
        // Par défaut
        assertFalse(contrat.isEstSigne());
        assertEquals(StatutContrat.EN_ATTENTE, contrat.getStatutContrat());
        
        // Changer le statut à SIGNE doit aussi changer estSigne à true
        contrat.setStatutContrat(StatutContrat.SIGNE);
        assertTrue(contrat.isEstSigne());
        assertEquals(StatutContrat.SIGNE, contrat.getStatutContrat());
        
        // Réinitialiser
        contrat = new Contrat();
        
        // Changer estSigne à true doit aussi changer le statut à SIGNE
        contrat.setEstSigne(true);
        assertTrue(contrat.isEstSigne());
        assertEquals(StatutContrat.SIGNE, contrat.getStatutContrat());
    }
}
