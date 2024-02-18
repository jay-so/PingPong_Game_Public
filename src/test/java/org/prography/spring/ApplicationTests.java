package org.prography.spring;

import org.junit.jupiter.api.Test;
import org.prography.spring.controller.*;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.prography.spring.service.*;
import org.prography.spring.service.validation.ValidateInitService;
import org.prography.spring.service.validation.ValidateRoomService;
import org.prography.spring.service.validation.ValidateServerStatusService;
import org.prography.spring.service.validation.ValidateTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private ValidateInitService validateInitService;

    @Autowired
    private ValidateRoomService validateRoomService;

    @Autowired
    private ValidateServerStatusService validateServerStatusService;

    @Autowired
    private ValidateTeamService validateTeamService;

    @Autowired
    private InitializationService initializationService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ServerStatusService serverStatusService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoomRepository userRoomRepository;

    @Autowired
    private InitializationController initializationController;

    @Autowired
    private RoomController roomController;

    @Autowired
    private ServerStatusController serverStatusController;

    @Autowired
    private TeamController teamController;

    @Autowired
    private UserController userController;

    @Test
    void contextLoads() {
        assertNotNull(validateInitService);
        assertNotNull(validateRoomService);
        assertNotNull(validateServerStatusService);
        assertNotNull(validateTeamService);
        assertNotNull(initializationService);
        assertNotNull(roomService);
        assertNotNull(serverStatusService);
        assertNotNull(teamService);
        assertNotNull(userService);
        assertNotNull(roomRepository);
        assertNotNull(userRepository);
        assertNotNull(userRoomRepository);
        assertNotNull(initializationController);
        assertNotNull(roomController);
        assertNotNull(serverStatusController);
        assertNotNull(teamController);
        assertNotNull(userController);
    }
}
