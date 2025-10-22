package Client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Бот загадывает слово - существительное в именительном падеже
 * Загаданное слово содержит более 5 букв
 * Изначально все буквы слова неизвестны для игрока
 * Игрок вводит буквы по-одной, регистр не важен
 * У игрока 6 попыток на ввод буквы (голова, туловище, 2 руки и 2 ноги)*/
public class Main {
    private static final int MIN_LENGTH = 3;
    private static final Path dictionaryPath = Path.of("resources","singular.txt");

    public static void main(String[] args) {
        //получаем словарь слов не менее 5 букв
        //выбираем рандомно слово из словаря
        //запрашиваем букву у пользователя, пока игрок не проиграл(попытки закончились)
        //или пока есть не отгаданные буквы в слове
        //если буква есть в слове - устанавливаем ее на правильное(ые) место(а)
        //если буквы нет - увеличиваем счетчик ошибок (максимум - 6) и рисуем новое состояние виселицы
        List<String> dictionary = getDictionary(dictionaryPath);

    }

    public static List<String> getDictionary(Path dictionaryPath){
        List<String> dictionary = new ArrayList<>();

        try (Stream<String> lines = Files.lines(dictionaryPath)) {
            dictionary = lines.filter(line -> line.length() > MIN_LENGTH)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }

        return dictionary;
    }
}