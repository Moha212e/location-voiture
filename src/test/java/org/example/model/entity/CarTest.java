package org.example.model.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CarTest {

    @Test
    public void testDefaultConstructor() {
        Car car = new Car();
        assertNotNull(car);
        assertEquals("", car.getIdCar());
        assertEquals("", car.getBrand());
        assertEquals("", car.getModel());
        assertEquals(0, car.getYear());
        assertEquals(0, car.getPriceday(), 0.0001);
        assertTrue(car.isAvailable());
        assertEquals("", car.getImage());
    }

    @Test
    public void testParameterizedConstructor() {
        Car car = new Car("1-ABC-123", "Toyota", "Corolla", 2020, 25);
        assertEquals("1-ABC-123", car.getIdCar());
        assertEquals("Toyota", car.getBrand());
        assertEquals("Corolla", car.getModel());
        assertEquals(2020, car.getYear());
        assertEquals(25, car.getPriceday(), 0.0001);
        assertTrue(car.isAvailable());
        assertEquals("", car.getImage());
    }

    @Test
    public void testFullConstructor() {
        Car car = new Car("1-ABC-123", "Toyota", "Corolla", 2020, 25, 
                          10000, "Essence", "Automatique", 5, 
                          true, "car.jpg");
        assertEquals("1-ABC-123", car.getIdCar());
        assertEquals("Toyota", car.getBrand());
        assertEquals("Corolla", car.getModel());
        assertEquals(2020, car.getYear());
        assertEquals(25, car.getPriceday(), 0.0001);
        assertEquals(10000, car.getMileage());
        assertEquals("Essence", car.getFuelType());
        assertEquals("Automatique", car.getTransmission());
        assertEquals(5, car.getSeats());
        assertTrue(car.isAvailable());
        assertEquals("car.jpg", car.getImage());
    }

    @Test
    public void testSettersAndGetters() {
        Car car = new Car();
        car.setIdCar("1-XYZ-789");
        car.setBrand("Honda");
        car.setModel("Civic");
        car.setYear(2019);
        car.setPriceday(25);
        car.setMileage(15000);
        car.setFuelType("Diesel");
        car.setTransmission("Manuelle");
        car.setSeats(4);
        car.setAvailable(false);
        car.setImage("civic.jpg");

        assertEquals("1-XYZ-789", car.getIdCar());
        assertEquals("Honda", car.getBrand());
        assertEquals("Civic", car.getModel());
        assertEquals(2019, car.getYear());
        assertEquals(25, car.getPriceday(), 0.0001);
        assertEquals(15000, car.getMileage());
        assertEquals("Diesel", car.getFuelType());
        assertEquals("Manuelle", car.getTransmission());
        assertEquals(4, car.getSeats());
        assertFalse(car.isAvailable());
        assertEquals("civic.jpg", car.getImage());
    }

    @Test
    public void testToString() {
        Car car = new Car("1-ABC-123", "BMW", "X5", 2022, 25);
        String expected = "Car{idCar='1-ABC-123', brand='BMW', model='X5', year=2022, priceday=25.0, mileage=0, fuelType='null', transmission='null', seats=0, available=true, image=''}";  
        assertEquals(expected, car.toString());
    }
}