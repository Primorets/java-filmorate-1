package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Film {

    private int id;
    @NotEmpty
    private String name;
    private String description;
    @Past
    private LocalDate releaseDate;
    @Min(value = 0, message = "min =0")
    private int duration;

    @JsonIgnore
    private Set<Integer> filmsLike = new HashSet<>();
    @NonNull
    private Mpa mpa;
    @NonNull
    private List<Genre> genres = new ArrayList<>();

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
