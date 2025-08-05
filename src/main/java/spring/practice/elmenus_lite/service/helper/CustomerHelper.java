package spring.practice.elmenus_lite.service.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.exception.InvalidOrderException;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.model.Address;
import spring.practice.elmenus_lite.model.Customer;
import spring.practice.elmenus_lite.model.PreferredPaymentSetting;
import spring.practice.elmenus_lite.repostory.AddressRepository;
import spring.practice.elmenus_lite.repostory.CustomerRepository;
import spring.practice.elmenus_lite.repostory.PreferredPaymentSettingRepository;

@Component
@RequiredArgsConstructor
public class CustomerHelper {
    private final CustomerRepository customerRepository;
    private final PreferredPaymentSettingRepository preferredPaymentSettingRepository;
    private final AddressRepository addressRepository;

    public Customer fetchCustomer(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CUSTOMER_NOT_FOUND.getFinalMessage(customerId)));
    }

    public Customer fetchCustomerByUserId(Integer userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.CUSTOMER_NOT_FOUND.getFinalMessage(userId)));
    }
    public Address fetchAndValidateAddress(Integer addressId, Integer customerId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));

        if (!address.getCustomer().getId().equals(customerId)) {
            throw new InvalidOrderException(ErrorMessage.ENTITY_OWNERSHIP_VIOLATION.getFinalMessage("Address"));
        }
        return address;
    }

    public PreferredPaymentSetting fetchAndValidatePaymentSetting(Integer paymentSettingId, Integer customerId) {
        PreferredPaymentSetting paymentSetting = preferredPaymentSettingRepository.findById(paymentSettingId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.PAYMENT_SETTING_NOT_FOUND.getFinalMessage(paymentSettingId)));

        if (!paymentSetting.getCustomer().getId().equals(customerId)) {
            throw new InvalidOrderException(ErrorMessage.ENTITY_OWNERSHIP_VIOLATION.getFinalMessage("Payment setting"));
        }
        return paymentSetting;
    }
}
