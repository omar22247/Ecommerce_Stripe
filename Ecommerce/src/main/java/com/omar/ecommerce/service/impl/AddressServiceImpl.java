package com.omar.ecommerce.service.impl;

import com.omar.ecommerce.dto.request.AddressRequest;
import com.omar.ecommerce.dto.response.AddressResponse;
import com.omar.ecommerce.entity.Address;
import com.omar.ecommerce.entity.User;
import com.omar.ecommerce.exception.ResourceNotFoundException;
import com.omar.ecommerce.mapper.AddressMapper;
import com.omar.ecommerce.repository.AddressRepository;
import com.omar.ecommerce.repository.UserRepository;
import com.omar.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;

    @Override
    public List<AddressResponse> getAllAddresses(UUID userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream().map(addressMapper::toAddressResponse).toList();
    }

    @Transactional
    @Override
    public AddressResponse addAddresses(UUID userId, AddressRequest request) {
        User user = userRepository.getReferenceById(userId);

        Address addressEntity = addressMapper.toAddress(request);
        addressEntity.setUser(user);

        handleDefaultAddress(userId, addressEntity, request.isDefault());

        addressRepository.save(addressEntity);
        return addressMapper.toAddressResponse(addressEntity);
    }

    @Transactional
    @Override
    public AddressResponse updateAddresses(UUID userId, UUID addressId, AddressRequest request) {
        Address addressEntity = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        assertOwnership(addressEntity, userId, "update");

        addressMapper.updateAddressFromRequest(request, addressEntity);

        handleDefaultAddress(userId, addressEntity, request.isDefault());

        addressRepository.save(addressEntity);
        return addressMapper.toAddressResponse(addressEntity);
    }

    @Transactional
    @Override
    public void deleteAddresses(UUID userId, UUID addressId) {
        Address addressEntity = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        assertOwnership(addressEntity, userId, "delete");

        if (addressEntity.isDefault()) {
            throw new IllegalStateException("Cannot delete the default address. Set another address as default first.");
        }

        addressRepository.delete(addressEntity);
    }

    private void assertOwnership(Address address, UUID userId, String action) {
        if (!address.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User does not have permission to " + action + " this address");
        }
    }

    private void handleDefaultAddress(UUID userId, Address addressEntity, boolean requestedDefault) {
        Optional<Address> currentDefault = addressRepository.findByUserIdAndIsDefaultTrue(userId);

        if (currentDefault.isEmpty()) {
            addressEntity.setDefault(true);
            return;
        }

        boolean isAlreadyTheDefault = currentDefault.get().getId().equals(addressEntity.getId());

        if (requestedDefault && !isAlreadyTheDefault) {
            currentDefault.get().setDefault(false);
            addressEntity.setDefault(true);
        } else if (!requestedDefault && isAlreadyTheDefault) {
            addressEntity.setDefault(true);
        } else {
            addressEntity.setDefault(requestedDefault);
        }
    }
}