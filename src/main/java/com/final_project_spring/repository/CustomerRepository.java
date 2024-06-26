package com.final_project_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.final_project_spring.entity.Customer;

/**
 * @author Rajesh_Kawali
 *
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
