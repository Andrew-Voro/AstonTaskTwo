package aston.servlets;

import aston.exception.NotFoundException;
import aston.service.UserService;
import aston.service.impl.UserServiceImpl;
import aston.servlets.dto.UserIncomingDto;
import aston.servlets.dto.UserUpdateDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class UserServletTest {
    private static UserService mockUserService;
    @InjectMocks
    private static UserServlet userServlet;
    private static UserServiceImpl oldInstance;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private static void setMock(UserService mock) {
        try {
            Field instance = UserServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (UserServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockUserService = Mockito.mock(UserService.class);
        setMock(mockUserService);
        userServlet = new UserServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = UserServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockUserService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("users").when(mockRequest).getPathInfo();

        userServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockUserService).findAll();
    }

    @Test
    void doGetById() throws IOException, NotFoundException {
        Mockito.doReturn("users/1").when(mockRequest).getPathInfo();

        userServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockUserService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFoundException() throws IOException, NotFoundException {
        Mockito.doReturn("users/100").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockUserService).findById(100L);

        userServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("users/2q").when(mockRequest).getPathInfo();

        userServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws IOException, NotFoundException {
        Mockito.doReturn("users/2").when(mockRequest).getPathInfo();

        userServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockUserService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteNotFound() throws IOException, NotFoundException {
        Mockito.doReturn("users/100").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockUserService).delete(100L);

        userServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        Mockito.verify(mockUserService).delete(100L);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("users/a100").when(mockRequest).getPathInfo();

        userServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException {
        String expectedLogin = "New login";
        String expectedName = "New name";
        String expectedEmail = "New email";
        String expectedBirthday = "1976-08-20";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"login\":\"" + expectedLogin + "\"" +
                        ",\"name\":\"" + expectedName + "\"" +
                        ", \"email\":\"" + expectedEmail + "\",\"birthday\":\"" + expectedBirthday + "\"" +
                        "}",
                null, null
        ).when(mockBufferedReader).readLine();

        userServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<UserIncomingDto> argumentCaptor = ArgumentCaptor.forClass(UserIncomingDto.class);
        Mockito.verify(mockUserService).save(argumentCaptor.capture());

        UserIncomingDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedLogin, result.getLogin());
        Assertions.assertEquals(expectedName, result.getName());
        Assertions.assertEquals(expectedEmail, result.getEmail());
        Assertions.assertEquals(expectedBirthday, result.getBirthday());
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        String expectedLogin = "New login";
        String expectedName = "New name";
        String expectedEmail = "New email";
        String expectedBirthday = "1976-08-20";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"login\":\"" + expectedLogin + "\"" +
                        ",\"name\":\"" + expectedName + "\"" +
                        ", \"email\":\"" + expectedEmail + "\",\"birthday\":\"" + expectedBirthday + "\"" +
                        "}",
                null, null
        ).when(mockBufferedReader).readLine();

        userServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<UserUpdateDto> argumentCaptor = ArgumentCaptor.forClass(UserUpdateDto.class);
        Mockito.verify(mockUserService).update(argumentCaptor.capture());

        UserUpdateDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedLogin, result.getLogin());
        Assertions.assertEquals(expectedName, result.getName());
        Assertions.assertEquals(expectedEmail, result.getEmail());
        Assertions.assertEquals(expectedBirthday, result.getBirthday());
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{Bad json:1}",
                null
        ).when(mockBufferedReader).readLine();

        userServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}
