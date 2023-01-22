package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
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
		try {
			Customer customer = customerRepository2.findById(customerId).get();
			List<Driver> driverList = driverRepository2.findAll();
			for (Driver driver : driverList) {
				if (driver.getCab().getAvailable()) {
					TripBooking tripBooking = new TripBooking();
					tripBooking.setDriver(driver);
					tripBooking.setCustomer(customer);
					tripBooking.setFromLocation(fromLocation);
					tripBooking.setToLocation(toLocation);
					tripBooking.setDistanceInKm(distanceInKm);
					tripBooking.setStatus(TripStatus.CONFIRMED);

					driver.getCab().setAvailable(false);
//				Cab cab = driver.getCab();
//				cab.setDriver(driver);
					customer.getTripBookingList().add(tripBooking);
					driver.getTripBookingList().add(tripBooking);

					customerRepository2.save(customer);
					driverRepository2.save(driver);
					tripBookingRepository2.save(tripBooking);

					return tripBooking;
				}
			}
		}
		catch (Exception e) {
			throw new Exception("No cab available!");
		}
		return null;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		try {
			TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
			tripBooking.setStatus(TripStatus.CANCELED);
//			Customer customer = tripBooking.getCustomer();
			Driver driver = tripBooking.getDriver();
			driver.getCab().setAvailable(true);
			driverRepository2.save(driver);

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

			Customer customer = tripBooking.getCustomer();
			customer.getTripBookingList().add(tripBooking);

			Driver driver = tripBooking.getDriver();
			driver.getTripBookingList().add(tripBooking);
			driver.getCab().setAvailable(true);
			driverRepository2.save(driver);

			tripBookingRepository2.save(tripBooking);

		}
		catch (Exception e){
			return;
		}

	}
}
