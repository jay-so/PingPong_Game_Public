package org.prography.spring.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.User;
import org.prography.spring.domain.enums.UserStatus;
import org.prography.spring.dto.request.InitializationRequest;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.service.validation.ValidateInitService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.prography.spring.common.ApiResponseCode.SEVER_ERROR;
import static org.prography.spring.domain.enums.UserStatus.*;

@Service
@RequiredArgsConstructor
public class InitializationService {

    private final HttpClient httpClient;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ValidateInitService validateInitService;

    @Transactional
    public void init(InitializationRequest initializationRequest) {
        validateInitService.validateInitializationRequest(initializationRequest);

        userRepository.deleteAll();
        roomRepository.deleteAll();

        try {
            List<User> userList = callFakerApi(initializationRequest);
            userRepository.saveAll(userList);
        } catch (RuntimeException e) {
            throw new BussinessException(SEVER_ERROR);
        }
    }

    private List<User> callFakerApi(InitializationRequest initializationRequest) {
        String fakerApiUrl = String.format("https://fakerapi.it/api/v1/users?_seed=%d&_quantity=%d&_locale=ko_KR",
                initializationRequest.getSeed(),
                initializationRequest.getQuantity());

        try {
            HttpResponse<String> fakerApiResponse = httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create(fakerApiUrl))
                    .build(), HttpResponse.BodyHandlers.ofString());

            List<User> userList = convertResponseToUsers(fakerApiResponse.body());
            userList.sort(Comparator.comparing(User::getFakerId));
            return userList;

        } catch (IOException e) {
            throw new BussinessException(SEVER_ERROR);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BussinessException(SEVER_ERROR);
        }
    }

    private List<User> convertResponseToUsers(String body) {
        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            return IntStream.range(0, dataArray.length())
                    .mapToObj(dataArray::getJSONObject)
                    .map(this::jsonToUser)
                    .collect(Collectors.toCollection(ArrayList::new));

        } catch (JSONException e) {
            throw new BussinessException(SEVER_ERROR);
        }
    }

    private User jsonToUser(JSONObject userJson) {
        Long fakerId = userJson.getLong("id");
        String name = userJson.getString("username");
        String email = userJson.getString("email");
        UserStatus status = userStatusBasedOnFakeId(fakerId);

        return User.builder()
                .fakerId(fakerId)
                .name(name)
                .email(email)
                .status(status)
                .build();
    }

    private UserStatus userStatusBasedOnFakeId(Long fakerId) {
        if (fakerId <= 30) {
            return ACTIVE;
        }

        if (fakerId <= 60) {
            return WAIT;
        }
        return NON_ACTIVE;
    }
}
