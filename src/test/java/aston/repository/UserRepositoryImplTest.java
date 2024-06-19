package aston.repository;

import aston.model.User;
import aston.repository.impl.UserRepositoryImpl;
import aston.util.PropertiesUtil;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDate;
import java.util.Optional;

class UserRepositoryImplTest {
    private static final String INIT_SQL = "sql/schema.sql";
    private static final int containerPort = 5432;
    private static final int localPort = 5432;
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("users_db")
            .withUsername(PropertiesUtil.getProperties("db.username"))
            .withPassword(PropertiesUtil.getProperties("db.password"))
            .withExposedPorts(containerPort)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(localPort), new ExposedPort(containerPort)))
            ))
            .withInitScript(INIT_SQL);
    public static UserRepository userRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
        userRepository = UserRepositoryImpl.getInstance();
        jdbcDatabaseDelegate = new JdbcDatabaseDelegate(container, "");
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @BeforeEach
    void setUp() {
        ScriptUtils.runInitScript(jdbcDatabaseDelegate, INIT_SQL);
    }

    @Test
    void save() {
        String expectedLogin = "New login";
        String expectedName = "New name";
        String expectedEmail = "New email";
        String expectedBirthday = "1976-08-20";

        User user = new User(
                expectedLogin,
                expectedName,
                expectedEmail,
                LocalDate.parse(expectedBirthday),
                null,
                null);
        user = userRepository.save(user);
        Optional<User> resultUser = userRepository.findById(user.getId());

        Assertions.assertTrue(resultUser.isPresent());
        Assertions.assertEquals(expectedLogin, resultUser.get().getLogin());
        Assertions.assertEquals(expectedName, resultUser.get().getName());
        Assertions.assertEquals(expectedEmail, resultUser.get().getEmail());
        Assertions.assertEquals(LocalDate.parse(expectedBirthday), resultUser.get().getBirthday());
    }

    @Test
    void update() {
        String expectedName = "Next name";
        User userForUpdate = userRepository.findById(1L).get();
        Assertions.assertNotEquals(expectedName, userForUpdate.getName());
        userForUpdate.setName(expectedName);
        userRepository.update(userForUpdate);
        User resultUser = userRepository.findById(3L).get();
        Assertions.assertEquals(expectedName, resultUser.getName());
    }

    @Test
    void deleteById() {
        Boolean expectedValue = true;
        int expectedSize = userRepository.findAll().size();

        User tempUser = new User(
                "User for delete login.",
                "User for delete name.",
                "login@email.com.",
                null,
                null,
                null
        );
        tempUser = userRepository.save(tempUser);

        boolean resultDelete = userRepository.deleteById(tempUser.getId());
        int roleListAfterSize = userRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, roleListAfterSize);
    }

    @DisplayName("Find by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void findById(Long expectedId, Boolean expectedValue) {
        Optional<User> user = userRepository.findById(expectedId);
        Assertions.assertEquals(expectedValue, user.isPresent());
        user.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @Test
    void findAll() {
        int expectedSize = 7;
        int resultSize = userRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Exist by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void exitsById(Long roleId, Boolean expectedValue) {
        boolean isUserExist = userRepository.exitsById(roleId);

        Assertions.assertEquals(expectedValue, isUserExist);
    }
}