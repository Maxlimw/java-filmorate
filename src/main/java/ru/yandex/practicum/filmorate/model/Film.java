package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Data
public class Film {

    private Long id;
    @NotBlank
    @NotNull
    private String name;
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private int duration;
    @PositiveOrZero
    private int rate;

    public Film(String name, String description, LocalDate releaseDate, int duration, int rate) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
    }

}
