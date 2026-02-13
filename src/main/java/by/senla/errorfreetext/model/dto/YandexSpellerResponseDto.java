package by.senla.errorfreetext.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class YandexSpellerResponseDto {
    private int code;
    private int pos;
    private int row;
    private int col;
    private int len;
    private String word;
    private List<String> s;
}