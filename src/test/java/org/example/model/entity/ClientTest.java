package org.example.model.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/* private int idClient;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private String licenseNumber;
    private String address; */

public class ClientTest {

    @Test
    public void testDefaultConstructor() {
        Client client = new Client();
        assertNotNull(client);
        assertEquals(0, client.getIdClient());
        assertEquals("", client.getName());
        assertEquals("", client.getSurname());
        assertEquals("", client.getEmail());
        assertEquals("", client.getPhoneNumber());
        assertNull(client.getBirthDate());
    }

    @Test
    public void testParameterizedConstructor() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Client client = new Client(2, "Pasch", "Mueller", "<EMAIL>", birthDate);
        assertEquals(2, client.getIdClient());
        assertEquals("Pasch", client.getName());
        assertEquals("Mueller", client.getSurname());
        assertEquals("<EMAIL>", client.getEmail());
        assertEquals(birthDate, client.getBirthDate());
    }

    @Test
    public void testFullConstructor() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Client client = new Client(3, "John", "Doe", "john@example.com", birthDate, "0612345678", "123 Main St");
        assertEquals(3, client.getIdClient());
        assertEquals("John", client.getName());
        assertEquals("Doe", client.getSurname());
        assertEquals("john@example.com", client.getEmail());
        assertEquals("0612345678", client.getPhoneNumber());
        assertEquals("123 Main St", client.getAddress());
        assertEquals(birthDate, client.getBirthDate());
    }

    @Test
    public void testSettersAndGetters() {
        Client client = new Client();
        client.setIdClient(100);
        client.setName("Pasch");
        client.setSurname("Mueller");
        client.setEmail("<EMAIL>");
        client.setPhoneNumber("0612345678");
        client.setLicenseNumber("123456789");
        client.setAddress("123 Main St");
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        client.setBirthDate(birthDate);

        assertEquals(100, client.getIdClient());
        assertEquals("Pasch", client.getName());
        assertEquals("Mueller", client.getSurname());
        assertEquals("<EMAIL>", client.getEmail());
        assertEquals("0612345678", client.getPhoneNumber());
        assertEquals("123456789", client.getLicenseNumber());
        assertEquals("123 Main St", client.getAddress());
        assertEquals(birthDate, client.getBirthDate());
    }

    @Test
    public void testToString() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Client client = new Client(2, "Pasch", "Mueller", "<EMAIL>", birthDate);
        String toString = client.toString();
        
        // Vérifier que la chaîne contient les informations importantes
        assertTrue(toString.contains("idClient=2"));
        assertTrue(toString.contains("name='Pasch'"));
        assertTrue(toString.contains("surname='Mueller'"));
        assertTrue(toString.contains("email='<EMAIL>'"));
    }
}