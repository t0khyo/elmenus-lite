package spring.practice.elmenus_lite.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.model.Customer;
import spring.practice.elmenus_lite.repostory.CustomerRepository;

@Service
@RequiredArgsConstructor
public class CustomerUtils {
    private final CustomerRepository customerRepository;

    public Customer fetchCustomer(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CUSTOMER_NOT_FOUND.getFinalMessage(customerId)));
    }
}
