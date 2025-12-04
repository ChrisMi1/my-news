package com.techie.mynews.mapper;

import com.techie.mynews.dto.response.UserSourceResponse;
import com.techie.mynews.model.UserSource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",uses = {UserMapper.class,SourceMapper.class})
public interface UserSourceMapper {
    UserSource INSTANCE = Mappers.getMapper(UserSource.class);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "source", source = "source")
    UserSourceResponse entityToResponse(UserSource userSource);

    List<UserSourceResponse> toUserSourceResponseList(List<UserSource> userSources);
}
