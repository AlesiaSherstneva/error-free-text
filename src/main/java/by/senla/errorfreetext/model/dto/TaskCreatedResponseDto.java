package by.senla.errorfreetext.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TaskCreatedResponseDto {
    private UUID id;
}