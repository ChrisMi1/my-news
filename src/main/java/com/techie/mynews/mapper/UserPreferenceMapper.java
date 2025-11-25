package com.techie.mynews.mapper;

import com.techie.mynews.dto.request.UserRequest;
import com.techie.mynews.dto.response.UserResponse;
import com.techie.mynews.model.UserPreference;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserPreferenceMapper {
    UserPreferenceMapper INSTANCE = Mappers.getMapper(UserPreferenceMapper.class);

    UserResponse entityToResponse(UserPreference userPreference);
    UserPreference requestToEntity(UserRequest userRequest);
}
