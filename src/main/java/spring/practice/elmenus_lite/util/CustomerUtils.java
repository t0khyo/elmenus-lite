package spring.practice.elmenus_lite.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.model.Address;
import spring.practice.elmenus_lite.model.Customer;
import spring.practice.elmenus_lite.repostory.AddressRepository;
import spring.practice.elmenus_lite.repostory.CustomerRepository;

@Service
@RequiredArgsConstructor
public class CustomerUtils {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final OwnershipValidator ownershipValidator;

    public Customer fetchCustomer(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CUSTOMER_NOT_FOUND.getFinalMessage(customerId)));
    }

    public Address fetchAndValidateAddress(Integer addressId, Integer customerId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));

        ownershipValidator.validateAddressOwnership(address, customerId);
        return address;
    }
}
