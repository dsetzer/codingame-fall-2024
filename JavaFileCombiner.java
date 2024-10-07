import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaFileCombiner {
    public static void main(String[] args) throws IOException {
        String sourceDir = "./src";
        String outputFile = "./out/Codingame.java";
        Set<String> processedClasses = new HashSet<>();

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("import java.util.*;");
            writer.println("import java.util.stream.Collectors;");
            writer.println("import java.util.concurrent.atomic.AtomicInteger;");
            writer.println();

            Files.walk(Paths.get(sourceDir))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> {
                    try {
                        String content = new String(Files.readAllBytes(path));
                        String className = extractClassName(content);

                        if (!processedClasses.contains(className)) {
                            processedClasses.add(className);
                            content = removePackageAndImports(content);
                            content = replacePublicClassesWithRegularClasses(content);
                            writer.println(content);
                            writer.println();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        }
        System.out.println("Combined file created: " + outputFile);
    }

    private static String extractClassName(String content) {
        Pattern pattern = Pattern.compile("class\\s+(\\w+)");
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1) : "";
    }

    private static String removePackageAndImports(String content) {
        return content.replaceAll("package.*?;", "") .replaceAll("import.*?;", "") .trim();
    }

    private static String replacePublicClassesWithRegularClasses(String content) {
        return content.replaceAll("(?s)\\b(public\\s+)?(\\w+\\s+)?class\\s+", "class ");
    }
}
