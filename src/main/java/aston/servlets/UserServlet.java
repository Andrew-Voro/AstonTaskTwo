package aston.servlets;

import aston.exception.NotFoundException;
import aston.service.UserService;
import aston.service.impl.UserServiceImpl;
import aston.servlets.dto.UserIncomingDto;
import aston.servlets.dto.UserOutGoingDto;
import aston.servlets.dto.UserUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

/**
 *The program begin to implement a topic of java course of YaPracticum using servlet technology instead of Spring MVC;
 * All implemented endpoints have test of Postman, see postman directory.
 * make functions: create user, create friends, find all users with friends, find users by id with friends,
 * find common friends for any two users.
 */

@WebServlet(urlPatterns = {"/users/*"})
public class UserServlet extends HttpServlet {
    private final transient UserService userService = UserServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public UserServlet() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private static void setJsonHeader(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private static String getJson(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader postData = req.getReader();
        String line;
        while ((line = postData.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer = null;
        Optional<UserIncomingDto> userResponse;
        try {

            userResponse = Optional.ofNullable(objectMapper.readValue(json, UserIncomingDto.class));
            UserIncomingDto user = userResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(userService.save(user));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect user Object.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);

        String responseAnswer = "";
        try {
            String[] pathPart = req.getPathInfo().split("/");
            if (pathPart.length == 1/*||"".equals(pathPart[1])*/) {
                List<UserOutGoingDto> userDtoList = userService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(userDtoList);
            } else if (pathPart.length == 2) {
                Long userId = Long.parseLong(pathPart[1]);
                UserOutGoingDto userDto = userService.findById(userId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(userDto);
            } else if (!"".equals(pathPart[1]) && "friends".equals(pathPart[2]) && "common".equals(pathPart[3]) && !"".equals(pathPart[4])) {
                Long userId = Long.parseLong(pathPart[1]);
                Long otherUserId = Long.parseLong(pathPart[4]);
                List<UserOutGoingDto> friends = userService.findCommonFriendsForTwoUsers(userId, otherUserId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(friends);
            }

        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String responseAnswer = "";
        try {
            String[] pathPart = req.getPathInfo().split("/");
            Long userId = Long.parseLong(pathPart[1]);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            userService.delete(userId);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer = "";
        Optional<UserUpdateDto> userResponse;
        try {
            if (req.getPathInfo() == null) {
                userResponse = Optional.ofNullable(objectMapper.readValue(json, UserUpdateDto.class));
                UserUpdateDto userUpdateDto = userResponse.orElseThrow(IllegalArgumentException::new);
                responseAnswer = objectMapper.writeValueAsString(userService.update(userUpdateDto));
            } else {
                String[] pathPart = req.getPathInfo().split("/");
                if (!"".equals(pathPart[1]) && "friends".equals(pathPart[2]) && !"".equals(pathPart[3])) {
                    {
                        Long userId = Long.parseLong(pathPart[1]);
                        Long friendId = Long.parseLong(pathPart[1]);
                        responseAnswer = objectMapper.writeValueAsString(userService.addFriend(userId, friendId));
                    }
                }
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect user Object.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }


}
