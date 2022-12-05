package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    private int id;
    @NotEmpty
    private String name;
    private String description;
    @Past
    private LocalDate releaseDate;
    @Min(value = 0, message = "min =0")
    private int duration;
}
