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
			int minId = Integer.MAX_VALUE;

			List<Driver> driverList = driverRepository2.findAll();
			for (Driver driver : driverList) {
				if (driver.getCab().getAvailable() && driver.getDriverId() < minId) {
					minId = driver.getDriverId();
				}
			}
			if(minId == Integer.MAX_VALUE){
				throw new Exception("No cab available!");
			}
			Driver driver = driverRepository2.findById(minId).get();

			Customer customer = customerRepository2.findById(customerId).get();

			TripBooking tripBooking = new TripBooking();
			int bill = driver.getCab().getPerKmRate() * distanceInKm;
			tripBooking.setDriver(driver);
			tripBooking.setCustomer(customer);
			tripBooking.setFromLocation(fromLocation);
			tripBooking.setToLocation(toLocation);
			tripBooking.setDistanceInKm(distanceInKm);
			tripBooking.setStatus(TripStatus.CONFIRMED);
			tripBooking.setBill(bill);

			driver.getCab().setAvailable(false);
			customer.getTripBookingList().add(tripBooking);
			driver.getTripBookingList().add(tripBooking);

//			customerRepository2.save(customer);
//			driverRepository2.save(driver);
			tripBookingRepository2.save(tripBooking);
			return tripBooking;
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
