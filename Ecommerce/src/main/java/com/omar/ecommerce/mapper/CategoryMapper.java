package com.omar.ecommerce.mapper;

import com.omar.ecommerce.dto.request.CategoryRequest;
import com.omar.ecommerce.dto.response.CategoryResponse;
import com.omar.ecommerce.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequest request);
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category category);
}
