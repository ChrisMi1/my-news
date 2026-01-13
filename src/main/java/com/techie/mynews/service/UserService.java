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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public List<UserSourceResponse> saveSources(UserSourceRequest userSourceRequest) {
        String email = getEmail();

        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            throw new MyNewsException(HttpStatus.BAD_REQUEST,"User with email " + email + " not found.");
        }

        return updateUserSources(user,userSourceRequest);
    }

    private List<UserSourceResponse> updateUserSources(User user, UserSourceRequest userSourceRequest) {
        List<UserSourceResponse> userSourceResponses = new ArrayList<>();

        Map<Integer,Source> updatedSources = mapSourceIdsToSources(userSourceRequest.getSourcesIds())
                .stream()
                .collect(Collectors.toMap(Source::getId, Function.identity()));

        Map<Integer,Source> existedSources = user.getSources()
                .stream()
                .collect(Collectors.toMap(Source::getId, Function.identity()));

        List<Integer> removedSources = new ArrayList<>();
        //Check if the source id that we found in db exists also in the updated list. If not then we have to remove it from db.
        //If we found it then we can delete it from the updated list as it is already exists in db
        for (Integer exitedId : existedSources.keySet()) {
            if (!updatedSources.containsKey(exitedId)) {
                removedSources.add(exitedId);
            } else {
                updatedSources.remove(exitedId);
            }
        }
        List<Source> addedSources = updatedSources.values().stream().toList();

        if (!removedSources.isEmpty()) {
            log.info("User with email: " + user.getEmail() + " remove " + removedSources.size() + " sources.");
            List<UserSource> removedUserSources = getDeletedUserSources(user,removedSources);
            userSourceRepository.deleteAll(removedUserSources);
            userSourceResponses.addAll(userSourceMapper.toUserSourceResponseList(removedUserSources,"DELETED"));
        }

        if (!addedSources.isEmpty()) {
            log.info("User with email: " + user.getEmail() + " add " + addedSources.size() + " sources.");
            List<UserSource> addedUserSources = createUserSources(user,addedSources);
            userSourceResponses.addAll(userSourceMapper.toUserSourceResponseList(userSourceRepository.saveAll(addedUserSources),"ADDED"));
        }

        return userSourceResponses;
    }

    private List<UserSource> createUserSources(User user, List<Source> sources) {
        return sources.stream().map(source ->
        {
            UserSource userSource = new UserSource();

            userSource.setUser(user);
            userSource.setSource(source);
            return userSource;
        })
        .collect(Collectors.toList());
    }

    private List<UserSource> getDeletedUserSources(User user, List<Integer> sources) {
        return userSourceRepository.findAllByUserIdAndSourceIds(user.getId(),sources);
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
        return sourceMapper.toSourceResponseList(user.getSources());
    }
}
