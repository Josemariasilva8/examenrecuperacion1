package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class PublicacionDTO {
    private String texto;
    private String autorUsername;
    private LocalDateTime fechaCreacion;
}
