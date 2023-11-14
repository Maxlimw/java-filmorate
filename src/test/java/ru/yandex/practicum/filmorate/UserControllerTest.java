package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.ErrorHandler;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureWebMvc
@ContextConfiguration(classes = {UserController.class, UserService.class, InMemoryUserStorage.class, ErrorHandler.class})
public class UserControllerTest {
    UserController userController;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUserEmptyRequestTest() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString("{}"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserInvalidLoginTest() throws Exception {
        User user = new User("cringe@mail.ru", "Max limw", "Maxim", LocalDate.of(2000, 07, 22));

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserNullLoginTest() throws Exception {
        User user = new User("cringe@mail.ru", null, "Maxim", LocalDate.of(2000, 07, 22));

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserInvalidEmailTest() throws Exception {
        User user = new User("cringe.mail.ru", "Maxlimw", "Maxim", LocalDate.of(2000, 07, 22));

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserInvalidBirthdayTest() throws Exception {
        User user = new User("cringe.mail.ru", "Maxlimw", "Maxim", LocalDate.now().plusMonths(5));

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void createUserEmptyNameTest() throws Exception {
        User user = new User("cringe@mail.ru", "Maxlimw", "", LocalDate.of(2000, 07, 22));

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}