package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;


@WebMvcTest
public class FilmControllerTest {

    FilmController filmController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private int counter = 1;

    @Test
    public void createFilmPositive() {
        Film film = new Film("Смешарики в стране ОЗ", "Спин-офф", LocalDate.of(2000, 11, 11), 100);
        try {
            mockMvc.perform(
                            post("/films")
                                    .content(objectMapper.writeValueAsString(film))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createFilmNegativeTest() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString("{}"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmInvalidDescriptionLengthTest() throws Exception {
        Film film = new Film("Пипины дочки", "a".repeat(201), LocalDate.of(2000, 11, 11), 90);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFilmNegativeInvalidReleaseDateTest() throws Exception {
        Film film = new Film("Сырок Александров", "Вкусно", LocalDate.of(1890, 1, 1), 90);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void createFilmNegativeNegativeDurationTest() throws Exception {
        Film film = new Film("Ernest Merkel", "Pi-pi", LocalDate.of(2022, 1, 1), -30);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFilmNegativeEmptyName() throws Exception {
        Film film = new Film("", "Billy Herrington RIP", LocalDate.of(2022, 1, 1), 90);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}