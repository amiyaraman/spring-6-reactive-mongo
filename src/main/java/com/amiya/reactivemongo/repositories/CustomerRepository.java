package com.amiya.reactivemongo.repositories;

import com.amiya.reactivemongo.domain.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CustomerRepository extends ReactiveMongoRepository<Customer,String> {
}
