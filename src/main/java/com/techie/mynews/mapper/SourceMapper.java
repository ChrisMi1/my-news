package com.techie.mynews.mapper;

import com.techie.mynews.dto.response.SourceResponse;
import com.techie.mynews.dto.response.UserResponse;
import com.techie.mynews.model.Source;
import com.techie.mynews.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper(componentModel = "spring")
public interface SourceMapper {
    SourceMapper INSTANCE = Mappers.getMapper(SourceMapper.class);

    SourceResponse entityToResponse(Source source);

    List<SourceResponse> toSourceResponseList(List<Source> sources);
}
