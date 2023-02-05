package ru.yandex.practicum.filmorate.model;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class Genre {
    private int id;
    private String name;
}
