package com.omar.ecommerce.service;

import com.omar.ecommerce.dto.request.AddressRequest;
import com.omar.ecommerce.dto.response.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    List<AddressResponse> getAllAddresses(UUID userId);
    AddressResponse addAddresses(UUID userId,AddressRequest address);
    AddressResponse updateAddresses(UUID userId, UUID addressId, AddressRequest address);
    void deleteAddresses(UUID userId, UUID addressId);

}
