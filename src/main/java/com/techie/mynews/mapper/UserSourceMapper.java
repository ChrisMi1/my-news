package com.techie.mynews.mapper;

import com.techie.mynews.dto.response.UserSourceResponse;
import com.techie.mynews.model.UserSource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",uses = {UserMapper.class,SourceMapper.class})
public interface UserSourceMapper {
    UserSourceMapper INSTANCE = Mappers.getMapper(UserSourceMapper.class);

    @Mapping(target = "user", source = "userSource.user")
    @Mapping(target = "source", source = "userSource.source")
    @Mapping(target = "action", expression = "java(action)")
    UserSourceResponse entityToResponse(UserSource userSource,String action);

    default List<UserSourceResponse> toUserSourceResponseList(List<UserSource> userSources, String action) {
        return userSources.stream()
                .map(s -> entityToResponse(s, action))
                .toList();
    }
}
