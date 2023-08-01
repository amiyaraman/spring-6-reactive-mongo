package com.amiya.reactivemongo.mapper;

import com.amiya.reactivemongo.domain.Customer;
import com.amiya.reactivemongo.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    CustomerDTO customerToCustomerDto(Customer customer);

    Customer customerDtoToCustomer(CustomerDTO customerDTO);
}