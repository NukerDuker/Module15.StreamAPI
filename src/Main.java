import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    //Статичная константа с паттерном для даты
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("ddMMyy");

    public static void main(String[] args) {
        //локальные переменные
        final Path filesource = Paths.get("src/balance.txt");
        final LocalDate now = LocalDate.now();
        final String formattedDate = SIMPLE_DATE_FORMAT.format(now.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
        final Path fileToWrite = Paths.get("src/balance_total_" + formattedDate + ".txt");

        try {
            final List<String> strings = Files.readAllLines(filesource, Charset.defaultCharset());
            final List<String[]> sortedNames = strings.stream()
                    .map(s -> {
                        final String[] split = s.split(",");
                        return new String[]{
                                split[0],
                                split.length > 1 ? split[1].trim() : ""
                        };
                    })
                    .sorted((x, y) -> Integer.parseInt(y[1]) - Integer.parseInt(x[1]))
                    .collect(Collectors.toList());

            try (final BufferedWriter writer = Files.newBufferedWriter(fileToWrite, Charset.defaultCharset())) {
                for (String[] split : sortedNames) {
                    writer.write(String.format("%s\n", String.join(", ", split)));
                }
                writer.append(String.format(
                        "===\nИтого: %d руб. (%s)",
                        sortedNames.stream().mapToInt(split -> Integer.parseInt(split[1])).sum(),
                        now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
