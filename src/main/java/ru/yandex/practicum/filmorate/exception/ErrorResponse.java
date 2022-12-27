package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    String massage;

    public ErrorResponse(String massage) {
        this.massage = massage;
    }
}
