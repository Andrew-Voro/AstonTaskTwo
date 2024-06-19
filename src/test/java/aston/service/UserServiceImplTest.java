package aston.service;

import aston.exception.NotFoundException;
import aston.model.User;
import aston.repository.UserRepository;
import aston.repository.impl.UserRepositoryImpl;
import aston.service.impl.UserServiceImpl;
import aston.servlets.dto.UserIncomingDto;
import aston.servlets.dto.UserOutGoingDto;
import aston.servlets.dto.UserUpdateDto;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

class UserServiceImplTest {
    private static UserService userService;
    private static UserRepository mockUserRepository;
    private static UserRepositoryImpl oldInstance;
    private String expectedLogin = "New login";
    private String expectedName = "New name";
    private String expectedEmail = "New email";
    private String expectedBirthday = "1976-08-20";

    private static void setMock(UserRepository mock) {
        try {
            Field instance = UserRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (UserRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockUserRepository = Mockito.mock(UserRepository.class);
        setMock(mockUserRepository);
        userService = UserServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = UserRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockUserRepository);
    }

    @Test
    void save() {
        Long expectedId = 1L;

        UserIncomingDto dto = new UserIncomingDto(expectedLogin, expectedName, expectedEmail, expectedBirthday);
        User user = new User(expectedLogin, expectedName, expectedEmail, LocalDate.parse(expectedBirthday), expectedId, Set.of());

        Mockito.doReturn(user).when(mockUserRepository).save(Mockito.any(User.class));

        UserOutGoingDto result = userService.save(dto);

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;

        UserUpdateDto dto = new UserUpdateDto(expectedLogin, expectedName, expectedEmail, expectedBirthday, expectedId);

        Optional<User> user = Optional.of(new User(expectedLogin, expectedName, expectedEmail, LocalDate.parse(expectedBirthday), expectedId, Set.of()));
        Mockito.doReturn(true).when(mockUserRepository).exitsById(Mockito.any());
        Mockito.doReturn(user).when(mockUserRepository).findById(Mockito.anyLong());
        userService.update(dto);

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(mockUserRepository).update(argumentCaptor.capture());

        User result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void updateNotFound() {
        Long expectedId = 1L;
        UserUpdateDto dto = new UserUpdateDto(expectedLogin, expectedName, expectedEmail, expectedBirthday, expectedId);

        Mockito.doReturn(false).when(mockUserRepository).exitsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    userService.update(dto);
                }, "Not found."
        );
        Assertions.assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;

        Optional<User> user = Optional.of(new User(expectedLogin, expectedName, expectedEmail, LocalDate.parse(expectedBirthday), expectedId, Set.of()));

        Mockito.doReturn(true).when(mockUserRepository).exitsById(Mockito.any());
        Mockito.doReturn(user).when(mockUserRepository).findById(Mockito.anyLong());

        UserOutGoingDto dto = userService.findById(expectedId);

        Assertions.assertEquals(expectedId, dto.getId());
    }

    @Test
    void findByIdNotFound() {
        Optional<User> user = Optional.empty();

        Mockito.doReturn(false).when(mockUserRepository).exitsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    userService.findById(1L);
                }, "Not found."
        );
        Assertions.assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void findAll() {
        userService.findAll();
        Mockito.verify(mockUserRepository).findAll();
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 100L;

        Mockito.doReturn(true).when(mockUserRepository).exitsById(Mockito.any());
        userService.delete(expectedId);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockUserRepository).deleteById(argumentCaptor.capture());

        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result);
    }
}