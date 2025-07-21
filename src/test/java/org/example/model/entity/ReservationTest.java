package org.example.model.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/*
    private int idReservation;
    private LocalDate startDate;
    private LocalDate endDate;
    private String responsable;
    private String notes;
    private float price;
    private String carId;
    private int clientId;
    private String contratId;
 */

public class ReservationTest {

    @Test
    public void testDefaultConstructor() {
        Reservation reservation = new Reservation();
        assertNotNull(reservation);
        assertEquals(0, reservation.getIdReservation());
        assertNull(reservation.getStartDate());
        assertNull(reservation.getEndDate());
        assertEquals("", reservation.getResponsable());
        assertEquals("", reservation.getNotes());
        assertEquals(0, reservation.getPrice(), 0.001);
        assertNull(reservation.getCar());
        assertNull(reservation.getClient());
        assertNull(reservation.getContrat());
        assertNull(reservation.getCarId());
        assertEquals(0, reservation.getClientId());
        assertNull(reservation.getContratId());
    }
    
    @Test
    public void testParameterizedConstructor() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(5);
        Reservation reservation = new Reservation(2, startDate, endDate, "John Doe", "Test notes", 100.0f);
        assertEquals(2, reservation.getIdReservation());
        assertEquals(startDate, reservation.getStartDate());
        assertEquals(endDate, reservation.getEndDate());
        assertEquals("John Doe", reservation.getResponsable());
        assertEquals("Test notes", reservation.getNotes());
        assertEquals(100.0f, reservation.getPrice(), 0.001);
        assertNull(reservation.getCar());
        assertNull(reservation.getClient());
        assertNull(reservation.getContrat());
    }
    
    @Test
    public void testFullConstructor() {
        Car car = new Car("1-ABC-123", "Toyota", "Corolla", 2020, 25);
        Client client = new Client(1, "John", "Doe", "john@example.com", LocalDate.of(1990, 1, 1));
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 10);
        Contrat contrat = new Contrat("C123", 1000.0, "Tous risques", true, StatutContrat.SIGNE);
        
        Reservation reservation = new Reservation(3, car, client, startDate, endDate, "Jane Doe", 250.0f, "Full test notes", contrat);
        
        assertEquals(3, reservation.getIdReservation());
        assertNotNull(reservation.getStartDate());
        assertNotNull(reservation.getEndDate());
        assertEquals("Jane Doe", reservation.getResponsable());
        assertEquals("Full test notes", reservation.getNotes());
        assertEquals(250.0f, reservation.getPrice(), 0.001);
        assertEquals(car, reservation.getCar());
        assertEquals(client, reservation.getClient());
        assertEquals(contrat, reservation.getContrat());
        assertEquals("1-ABC-123", reservation.getCarId());
        assertEquals(1, reservation.getClientId());
        assertEquals("C123", reservation.getContratId());
    }

    @Test
    public void testSettersAndGetters() {
        Reservation reservation = new Reservation();
        
        reservation.setIdReservation(5);
        assertEquals(5, reservation.getIdReservation());
        
        LocalDate startDate = LocalDate.now();
        reservation.setStartDate(startDate);
        assertEquals(startDate, reservation.getStartDate());
        
        LocalDate endDate = LocalDate.now().plusDays(3);
        reservation.setEndDate(endDate);
        assertEquals(endDate, reservation.getEndDate());
        
        reservation.setResponsable("Alice Johnson");
        assertEquals("Alice Johnson", reservation.getResponsable());
        
        reservation.setNotes("Important notes");
        assertEquals("Important notes", reservation.getNotes());
        
        reservation.setPrice(150.75f);
        assertEquals(150.75f, reservation.getPrice(), 0.001);
        
        Car car = new Car("1-DEF-456", "Honda", "Civic", 2021, 30);
        reservation.setCar(car);
        assertEquals(car, reservation.getCar());
        assertEquals(car.getIdCar(), reservation.getCarId());
        
        Client client = new Client(2, "Bob", "Smith", "alice@example.com", LocalDate.of(1985, 5, 15));
        reservation.setClient(client);
        assertEquals(client, reservation.getClient());
        assertEquals(client.getIdClient(), reservation.getClientId());
        
        Contrat contrat = new Contrat("C456", 1500.0, "Tiers", false, StatutContrat.EN_ATTENTE);
        reservation.setContrat(contrat);
        assertEquals(contrat, reservation.getContrat());
        assertEquals(contrat.getIdContrat(), reservation.getContratId());
        
        // Tester la définition directe des IDs
        reservation = new Reservation();
        reservation.setCarId("1-GHI-789");
        reservation.setClientId(3);
        reservation.setContratId("C789");
        
        assertEquals("1-GHI-789", reservation.getCarId());
        assertEquals(3, reservation.getClientId());
        assertEquals("C789", reservation.getContratId());
    }

    @Test
    public void testToString() {
        Reservation reservation = new Reservation(2, null, null, "John Doe", "Test notes", 100.0f);
        String expected = "Reservation{idReservation=2, startDate=null, endDate=null, responsable='John Doe', notes='Test notes', price=100.0}";  
        assertEquals(expected, reservation.toString());
    }
}