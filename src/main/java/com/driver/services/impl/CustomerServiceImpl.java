package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		try {
			customerRepository2.save(customer);
		}
		catch (Exception e){
			return;
		}
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		try {
			Customer customer = customerRepository2.findById(customerId).get();
			customerRepository2.delete(customer);
		}
		catch (Exception e){
			return;
		}

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		List<Driver> drivers = driverRepository2.findAll();

//		Collections.sort(drivers,(a, b) -> a.getDriverId() - b.getDriverId());
		Driver driver = null;
		for(Driver driver1: drivers){
			if(driver1.getCab().getAvailable()){
				if((driver == null) || (driver.getDriverId() > driver1.getDriverId())) {
					driver = driver1;
				}
			}
		}
		if(driver==null){
			throw new Exception("No cab available!");
		}

		TripBooking newTrip = new TripBooking();
		Customer customer = customerRepository2.findById(customerId).get();
		newTrip.setCustomer(customer);
		newTrip.setDriver(driver);
		newTrip.setFromLocation(fromLocation);
		newTrip.setToLocation(toLocation);
		newTrip.setDistanceInKm(distanceInKm);
		newTrip.setBill(driver.getCab().getPerKmRate()*distanceInKm);
		newTrip.setStatus(TripStatus.CONFIRMED);
		customer.getTripBookingList().add(newTrip);
		driver.getTripBookingList().add(newTrip);
		customerRepository2.save(customer);
		driverRepository2.save(driver);

//		tripBookingRepository2.save(newTrip);
		return newTrip;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		try {
			TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
			tripBooking.setStatus(TripStatus.CANCELED);
			tripBooking.getDriver().getCab().setAvailable(true);
			tripBooking.setBill(0);
			tripBooking.setToLocation(null);
			tripBooking.setFromLocation(null);
			tripBooking.setDistanceInKm(0);
			tripBookingRepository2.save(tripBooking);

		}
		catch (Exception e){
			return;
		}
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		try {
			TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
			tripBooking.setStatus(TripStatus.COMPLETED);
			tripBookingRepository2.save(tripBooking);
		}
		catch (Exception e){
			return;
		}

	}
}
