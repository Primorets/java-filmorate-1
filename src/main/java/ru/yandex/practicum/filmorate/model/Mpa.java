package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Mpa {

    private int id;

    private String name;
}
