package com.techie.mynews.mapper;

import com.techie.mynews.dto.request.UserRequest;
import com.techie.mynews.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.techie.mynews.model.User;



@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponse entityToResponse(User user);
    //User requestToEntity(UserRequest userRequest);
}
