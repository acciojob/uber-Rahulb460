package com.driver.services.impl;

import java.util.ArrayList;
import java.util.List;

import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Admin;
import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.AdminRepository;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	AdminRepository adminRepository1;

	@Autowired
	DriverRepository driverRepository1;

	@Autowired
	CustomerRepository customerRepository1;

	@Override
	public void adminRegister(Admin admin) {
		//Save the admin in the database
		try {
			adminRepository1.save(admin);
		}
		catch (Exception e){
			return;
		}

	}

	@Override
	public Admin updatePassword(Integer adminId, String password) {
		//Update the password of admin with given id
		try {
			Admin admin = adminRepository1.findById(adminId).get();
			admin.setPassword(password);
			adminRepository1.save(admin);
			return admin;
		}
		catch (Exception e){
			return null;
		}
	}

	@Override
	public void deleteAdmin(int adminId){
		// Delete admin without using deleteById function
		try {
			Admin admin = adminRepository1.findById(adminId).get();
			adminRepository1.delete(admin);
		}
		catch (Exception e){
			return;
		}

	}

	@Override
	public List<Driver> getListOfDrivers() {
		//Find the list of all drivers
		try {
			List<Driver> listOfDrivers = driverRepository1.findAll();
			return listOfDrivers;
		}
		catch (Exception e){
			return new ArrayList<>();
		}
	}

	@Override
	public List<Customer> getListOfCustomers() {
		//Find the list of all customers
		try {
			List<Customer> listOfCustomers = customerRepository1.findAll();
			return listOfCustomers;
		}
		catch (Exception e){
			return new ArrayList<>();
		}
	}

}
