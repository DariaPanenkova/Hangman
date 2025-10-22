package Client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
    private static final int MAX_TRIES = 6;
    private static final Path dictionaryPath = Path.of("src", "main","resources","singular.txt");

    private static final Random random = new Random();
    private static final Scanner scanner = new Scanner(System.in);

    private static final String GAME_STATE_WON = "Слово угадано!";
    private static final String GAME_STATE_OVER = "Вы проиграли!";
    private static final String GAME_STATE_GAME_NOT_OVER = "Игра не закончена";

    public static void main(String[] args) {
        //получаем словарь слов не менее 5 букв
        //выбираем рандомно слово из словаря
        //запрашиваем букву у пользователя, пока игрок не проиграл(попытки закончились)
        //или пока есть не отгаданные буквы в слове
        //если буква есть в слове - устанавливаем ее на правильное(ые) место(а)
        //если буквы нет - увеличиваем счетчик ошибок (максимум - 6) и рисуем новое состояние виселицы

        startGame();
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

    public static String getRandomWord(List<String> dictionary){
        int index = random.nextInt(dictionary.size());
        return dictionary.get(index);
    }

    public static String getCurrentWordState(String searchWord, Set<Character> rightCharsSet) {
        StringBuilder result = new StringBuilder();

        for (char c : searchWord.toCharArray()) {
            if (rightCharsSet.contains(c)) {
                result.append(c); // Оставляем символ как есть
            } else if (Character.isLetter(c)) {
                result.append('_'); // Заменяем букву на _
            }
        }
        return result.toString();
    }

    public static char inputChar(Set<Character> correctCharsSet, Set<Character> incorrectCharsSet){
        System.out.println("Введите букву для проверки: ");

        do {
            String input = scanner.nextLine().toLowerCase();

            if (input.isEmpty()) {
                System.out.println("Пустой ввод. Введите букву: ");
                continue;
            }

            if (input.length() > 1) {
                System.out.println("Введено больше одного символа.Введите букву: ");
                continue;
            }

            char character = input.charAt(0);

            if (!input.matches("[а-яё]")) {
                System.out.println("Введите русскую букву: ");
                continue;
            }

            if (correctCharsSet.contains(character)
                || incorrectCharsSet.contains(character)){
                System.out.println("Вы уже проверяли букву " + input +".Введите русскую букву: ");
                continue;
            }

            return character;
        } while (true);
    }

    public static void startGame(){
        List<String> dictionary = getDictionary(dictionaryPath);
        while(true){
            System.out.println("Хотите начать игру? 1 - да 0 - нет");
            String input = scanner.nextLine();

            if (Objects.equals(input, "1")){
                String searchWord = getRandomWord(dictionary);
                System.out.println(searchWord);
                startGameLoop(searchWord);
            } else {
                break;
            }
        }
    }

    public static void startGameLoop(String searchWord){
        Set<Character> correctCharSet = new HashSet<>();
        Set<Character> incorrectCharSet = new HashSet<>();

        do{
            char charTry = inputChar(correctCharSet, incorrectCharSet);

            if (searchWord.contains(String.valueOf(charTry))){
                correctCharSet.add(charTry);
            }else{
                incorrectCharSet.add(charTry);
            }

            String currentWordState = getCurrentWordState(searchWord, correctCharSet);
            System.out.println(currentWordState);
            String gameState = checkGameState(searchWord, currentWordState, incorrectCharSet.size());
            System.out.println("Ошибки (" + incorrectCharSet.size() + "):" + incorrectCharSet);
            if (!Objects.equals(gameState, GAME_STATE_GAME_NOT_OVER)){
                System.out.println(gameState);
                System.out.println("Загаданное слово: " + searchWord);
                return;
            }
        }while(true);
    }

    public static String checkGameState(String searchWord, String currentWordState, int incorrectTries){
        if (Objects.equals(searchWord, currentWordState)){
            return GAME_STATE_WON;
        } else if (currentWordState.contains("_")) {
            if (incorrectTries >= MAX_TRIES) {
                return GAME_STATE_OVER;
            }
        }

        return GAME_STATE_GAME_NOT_OVER;
    }
}