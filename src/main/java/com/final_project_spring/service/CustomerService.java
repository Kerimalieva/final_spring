package com.final_project_spring.service;

import java.util.List;

import com.final_project_spring.dto.CustomerDTO;

/**
 * @author Rajesh_Kawali
 *
 */
public interface CustomerService {

	public List<CustomerDTO> getAllCustomers();
	
	public CustomerDTO addCustomer(CustomerDTO customerDto);

	public CustomerDTO customerById(Long id);

	public String deleteCustomer(Long id);

	public CustomerDTO updateCustomer(Long id, CustomerDTO user);

}
