import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.stream.Stream;

public class EnvVariables {
    private static HashMap<String, String> envVariables = new HashMap<>();

    public EnvVariables() {
        try {
            parseEnv();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void parseEnv() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("src/.env"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] keyValue = line.split("=");
            envVariables.put(keyValue[0], keyValue[1]);
        }
        reader.close();

    }

    public static String get(String key) {
        return envVariables.get(key);
    }
}
