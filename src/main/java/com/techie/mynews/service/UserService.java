package com.techie.mynews.service;

import com.techie.mynews.dto.request.UserSourceRequest;
import com.techie.mynews.dto.response.CheckUserResponse;
import com.techie.mynews.dto.response.UserResponse;
import com.techie.mynews.dto.response.UserSourceResponse;
import com.techie.mynews.exception.MyNewsException;
import com.techie.mynews.mapper.UserMapper;
import com.techie.mynews.mapper.UserSourceMapper;
import com.techie.mynews.model.Source;
import com.techie.mynews.model.User;
import com.techie.mynews.model.UserSource;
import com.techie.mynews.repository.SourceRepository;
import com.techie.mynews.repository.UserRepository;
import com.techie.mynews.repository.UserSourceRepository;
import com.techie.mynews.utils.AuthUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final SourceRepository sourceRepository;
    private final UserSourceRepository userSourceRepository;
    private final UserSourceMapper userSourceMapper;
    private final UserMapper userMapper;
    public List<UserSourceResponse> saveSources(UserSourceRequest userSourceRequest) {
        String email = getEmail();

        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            throw new MyNewsException(HttpStatus.BAD_REQUEST,"User with email " + email + " not found.");
        }

        List<Source> sources = mapSourceIdsToSources(userSourceRequest.getSourcesIds());

        if(isTheCombinationUserAndSourceAlreadyExists(user,sources)) {
            throw new MyNewsException(HttpStatus.CONFLICT,"User already have one of the sources provided");
        }

        List<UserSource> newUserSources = createUserSources(user,sources);

        List<UserSource> userSources = userSourceRepository.saveAll(newUserSources);
        log.info("Sources saved successfully for the user :" + user.getEmail());

        return userSourceMapper.toUserSourceResponseList(userSources);
    }

    private Boolean isTheCombinationUserAndSourceAlreadyExists(User user, List<Source> sources) {
        return sources.stream().anyMatch(source -> userSourceRepository.existsByUser_IdAndSource_Id(user.getId(),source.getId()));
    }

    private List<UserSource> createUserSources(User user, List<Source> sources) {
        return sources.stream().map(source -> UserSource.builder().user(user).source(source).build()).collect(Collectors.toList());
    }

    private List<Source> mapSourceIdsToSources(List<Integer> sourcesIds)  {
        return sourcesIds.stream().map(sourceId -> sourceRepository.findById(sourceId)
                        .orElseThrow(() -> new MyNewsException(HttpStatus.BAD_REQUEST,"Source with id " + sourceId + " not found.")))
                .collect(Collectors.toList());
    }

    private User saveUser(String email) {
        User user = new User();
        user.setEmail(email);
        return userRepository.save(user);
    }

    public CheckUserResponse checkIfUserExists() {
        String email = getEmail();

        log.info("Checking if user exists with email: " + email);
        User user = userRepository.findUserByEmail(email);
        CheckUserResponse checkUserResponse = new CheckUserResponse();

        if (user == null) {
            checkUserResponse.setExists(false);
            checkUserResponse.setUserResponse(null);
            return checkUserResponse;
        }

        UserResponse userResponse = userMapper.entityToResponse(user);
        checkUserResponse.setExists(true);
        checkUserResponse.setUserResponse(userResponse);

        return checkUserResponse;
    }

    public UserResponse create() {
        String email = getEmail();

        if (userRepository.findUserByEmail(email) != null) {
            throw new MyNewsException(HttpStatus.CONFLICT,"User with email " + email + " already exists");
        }

        User user = saveUser(email);
        return userMapper.entityToResponse(user);
    }

    private String getEmail() {
        String email = AuthUtils.getActionerEmail();
        if (email == null) throw new MyNewsException(HttpStatus.NOT_FOUND,"No email found");
        return email;
    }
}
