package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
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
    private final Set<Long> likes = new HashSet<>();

    private final Set<Genre> genres = new HashSet<>();
    @NotNull
    private final Mpa mpa;

}
