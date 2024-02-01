package org.prography.spring.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.prography.spring.domain.User;
import org.prography.spring.domain.enums.UserStatus;
import org.prography.spring.dto.requestDto.InitializationRequest;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.prography.spring.domain.enums.UserStatus.*;

@Service
@RequiredArgsConstructor
public class InitializationService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final HttpClient httpClient;

    public void init(InitializationRequest initializationRequest) {
        userRepository.deleteAll();
        roomRepository.deleteAll();

        String fakerApiUrl = String.format("https://fakerapi.it/api/v1/users?_seed=%d&_quantity=%d&_locale=ko_KR",
                initializationRequest.getSeed(),
                initializationRequest.getQuantity());

        try {
            HttpResponse<String> faksrApiResponse = httpClient.send(HttpRequest.newBuilder()
                            .uri(URI.create(fakerApiUrl))
                            .build(),
                    HttpResponse.BodyHandlers.ofString());

            List<User> userList = convertResponseToUsers(faksrApiResponse.body());
            userList.sort(Comparator.comparing(User::getFakerId));
            userRepository.saveAll(userList);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Faker API 호출 중 오류가 발생하였습니다.");
        }
    }

    private List<User> convertResponseToUsers(String body) {
        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            return IntStream.range(0, dataArray.length())
                    .mapToObj(dataArray::getJSONObject)
                    .map(this::jsonToUser)
                    .collect(Collectors.toList());
        } catch (JSONException e) {
            throw new RuntimeException("Faker API 응답을 변환하는 중 오류가 발생하였습니다.");
        }
    }

    private User jsonToUser(JSONObject userJson) {
        Integer fakerId = userJson.getInt("id");
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

    private UserStatus userStatusBasedOnFakeId(Integer fakerId) {
        if (fakerId <= 30) {
            return ACTIVE;
        }

        if (fakerId <= 60) {
            return WAIT;
        }
        return NON_ACTIVE;
    }
}
