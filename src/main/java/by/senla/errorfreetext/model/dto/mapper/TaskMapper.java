package by.senla.errorfreetext.model.dto.mapper;

import by.senla.errorfreetext.model.dto.TaskCreatedResponseDto;
import by.senla.errorfreetext.model.dto.TaskRequestDto;
import by.senla.errorfreetext.model.dto.YandexSpellerRequestDto;
import by.senla.errorfreetext.model.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    @Mapping(source = "text", target = "originalText")
    Task toEntity(TaskRequestDto dto);

    TaskCreatedResponseDto toCreatedResponseDto(Task task);

    @Mapping(source = "originalText", target = "text")
    @Mapping(source = "language", target = "lang")
    YandexSpellerRequestDto toYandexRequestDto(Task task);
}