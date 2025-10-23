package Client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final int MIN_LENGTH = 3;
    private static final int MAX_TRIES = 6;
    private static final Path DICTIONARY_PATH = Path.of("src", "main", "resources", "singular.txt");

    private static final Random random = new Random();
    private static final Scanner scanner = new Scanner(System.in);

    private final static String START = "1";
    private final static String QUIT = "0";
    private final static char UNKNOWN_LETTER = '_';
    private static final Pattern RUSSIAN_LETTER_PATTERN = Pattern.compile("[а-яё]");
    private static final String[] PICTURES = {
            """
                    _____
                    |  |
                    |
                    |
                    |
                    """,
            """
                    _____
                    |  |
                    |  O
                    |
                    |
                    """,
            """
                    _____
                    |  |
                    |  O
                    |  |
                    |
                    """,
            """
                    _____
                    |  |
                    |  O
                    | /|
                    |
                    """,
            """
                    _____
                    |  |
                    |  O
                    | /|\\
                    |
                    """,
            """
                    _____
                    |  |
                    |  O
                    | /|\\
                    | /
                    """,
            """
                    _____
                    |  |
                    |  O
                    | /|\\
                    | / \\
                    """};

    public static void main(String[] args) {
        startGame();
    }

    private static List<String> getDictionary(Path dictionaryPath) throws IOException {
        List<String> dictionary;

        try (Stream<String> lines = Files.lines(dictionaryPath)) {
            dictionary = lines.filter(line -> line.length() > MIN_LENGTH)
                    .collect(Collectors.toList());

        } catch (NoSuchFileException e) {
            throw new IOException("Файл " + dictionaryPath.toAbsolutePath() + " не найден", e);
        } catch (IOException e) {
            throw new IOException("Ошибка при чтении файла " + dictionaryPath.toAbsolutePath() + "по причине: " + e.getMessage());
        }

        return dictionary;
    }

    private static String getRandomWord(List<String> dictionary) {
        int index = random.nextInt(dictionary.size());
        return dictionary.get(index);
    }

    private static String getCurrentWordState(String searchWord, Set<Character> rightCharsSet) {
        StringBuilder result = new StringBuilder();

        for (char c : searchWord.toCharArray()) {
            if (rightCharsSet.contains(c)
                    || !Character.isLetter(c)) {
                result.append(c);
            } else {
                result.append(UNKNOWN_LETTER);
            }
        }
        return result.toString();
    }

    private static char inputLetter(Set<Character> correctCharsSet, Set<Character> incorrectCharsSet) {
        System.out.println("Введите букву для проверки: ");

        while (true) {
            String input = scanner.nextLine().toLowerCase();

            if (input.isEmpty()) {
                System.out.println("Пустой ввод. Введите букву: ");
                continue;
            }

            if (input.length() > 1) {
                System.out.println("Введено больше одного символа.Введите букву: ");
                continue;
            }

            char symbol = input.charAt(0);

            if (!RUSSIAN_LETTER_PATTERN.matcher(input).matches()) {
                System.out.println("Введите русскую букву: ");
                continue;
            }

            if (correctCharsSet.contains(symbol)
                    || incorrectCharsSet.contains(symbol)) {
                System.out.printf("Вы уже проверяли букву %s.Введите русскую букву: ", input);
                continue;
            }

            return symbol;
        }
    }

    private static void startGame() {
        try {
            List<String> dictionary = getDictionary(DICTIONARY_PATH);

            if (dictionary.isEmpty()) {
                throw new IOException("Словарь пуст");
            }

            while (true) {
                System.out.printf("Хотите начать игру? %s - да %s - нет \n", START, QUIT);
                String input = scanner.nextLine();

                if (Objects.equals(input, START)) {
                    String searchWord = getRandomWord(dictionary);
                    startGameLoop(searchWord);
                }

                if (Objects.equals(input, QUIT)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при запуске:");
            System.err.println(e.getMessage());
            System.err.println("Программа будет завершена.");
            System.exit(1);
        }
    }

    private static void startGameLoop(String searchWord) {
        Set<Character> wordLetters = new LinkedHashSet<>();
        Set<Character> incorrectLetters = new LinkedHashSet<>();

        while (!isLose(incorrectLetters.size())) {
            char letter = inputLetter(wordLetters, incorrectLetters);

            if (isWordContainLetter(searchWord, letter)) {
                wordLetters.add(letter);
            } else {
                incorrectLetters.add(letter);
            }

            String currentWordState = getCurrentWordState(searchWord, wordLetters);
            System.out.println(currentWordState);
            printHangman(incorrectLetters.size());
            System.out.printf("Ошибки (%d): %s \n", incorrectLetters.size(), incorrectLetters);

            if (isWin(searchWord, currentWordState)) {
                System.out.println("Слово угадано!");
                return;
            }
        }
        System.out.println("Попытки закончились, вы проиграли!");
        System.out.printf("Загаданное слово: %s \n", searchWord);
    }

    private static boolean isLose(int incorrectTries) {
        return incorrectTries >= MAX_TRIES;
    }

    private static boolean isWin(String searchWord, String currentWordState) {
        return Objects.equals(searchWord, currentWordState);
    }

    private static boolean isWordContainLetter(String searchWord, char letter) {
        return searchWord.contains(String.valueOf(letter));
    }

    private static void printHangman(int numPicture) {
        if (numPicture > MAX_TRIES) {
            throw new IllegalArgumentException(String.format("Картинки виселицы для количества ошибок = %d не существует", numPicture));
        }
        System.out.println(PICTURES[numPicture]);
    }
}