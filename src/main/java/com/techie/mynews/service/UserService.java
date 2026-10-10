package com.techie.mynews.service;

import com.techie.mynews.dto.request.UserSourceRequest;
import com.techie.mynews.dto.response.CheckUserResponse;
import com.techie.mynews.dto.response.SourceResponse;
import com.techie.mynews.dto.response.UserResponse;
import com.techie.mynews.dto.response.UserSourceResponse;
import com.techie.mynews.exception.MyNewsException;
import com.techie.mynews.mapper.SourceMapper;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
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
    private final SourceMapper sourceMapper;

    @Transactional
    public List<UserSourceResponse> saveSources(UserSourceRequest userSourceRequest) {
        String email = getEmail();

        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            throw new MyNewsException(HttpStatus.BAD_REQUEST,"User with email " + email + " not found.");
        }

        return updateUserSources(user,userSourceRequest);
    }

    private List<UserSourceResponse> updateUserSources(User user, UserSourceRequest userSourceRequest) {
        Set<Integer> targetSourceIds = new HashSet<>(
                userSourceRequest.getSourcesIds() != null ? userSourceRequest.getSourcesIds() : List.of()
        );

        //Identify which UserSource entities must be removed
        List<UserSource> toRemove = user.getUserSources().stream()
                .filter(us -> !targetSourceIds.contains(us.getSource().getId()))
                .toList();

        //Identify which source IDs need to be added
        Set<Integer> currentSourceIds = user.getUserSources().stream()
                .map(us -> us.getSource().getId())
                .collect(Collectors.toSet());

        Set<Integer> idsToAdd = targetSourceIds.stream()
                .filter(id -> !currentSourceIds.contains(id))
                .collect(Collectors.toSet());

        //Apply Removals
        for (UserSource us : toRemove) {
            user.getUserSources().remove(us);
        }

        //Apply Additions
        List<UserSource> toAdd = new ArrayList<>();
        if (!idsToAdd.isEmpty()) {
            List<Source> sourcesToAttach = sourceRepository.findAllById(idsToAdd);
            for (Source source : sourcesToAttach) {
                UserSource newLink = new UserSource();
                newLink.setUser(user);
                newLink.setSource(source);
                user.getUserSources().add(newLink);
                toAdd.add(newLink);
            }
        }

        //Build DTO Responses
        List<UserSourceResponse> responses = new ArrayList<>();
        if (!toRemove.isEmpty()) {
            log.info("User {} removed {} sources.", user.getEmail(), toRemove.size());
            responses.addAll(userSourceMapper.toUserSourceResponseList(toRemove, "DELETED"));
        }
        if (!toAdd.isEmpty()) {
            log.info("User {} added {} sources.", user.getEmail(), toAdd.size());
            responses.addAll(userSourceMapper.toUserSourceResponseList(toAdd, "ADDED"));
        }

        return responses;
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
        log.info("User with email: " + user.getEmail() + " saved successfully!");
        return userMapper.entityToResponse(user);
    }

    private String getEmail() {
        String email = AuthUtils.getActionerEmail();
        if (email == null) throw new MyNewsException(HttpStatus.NOT_FOUND,"No email found");
        return email;
    }

    public List<SourceResponse> getSelectedSourcesByUser() {
        User user = userRepository.findUserByEmail(getEmail());

        List<Source> sources = user.getUserSources().stream()
                .map(UserSource::getSource)
                .toList();

        return sourceMapper.toSourceResponseList(sources);
    }
}
