package com.omar.ecommerce.mapper;

import com.omar.ecommerce.dto.request.AddressRequest;
import com.omar.ecommerce.dto.response.AddressResponse;
import com.omar.ecommerce.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "isDefault", source = "default")
    AddressResponse toAddressResponse(Address address);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "default", ignore = true)
    Address toAddress(AddressRequest address);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "default", ignore = true)
    Address updateAddressFromRequest(AddressRequest addressRequest, @MappingTarget Address address);
}