package cairocraft.utils;

import com.exaroton.api.ExarotonClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GenerateClient {

    public static ExarotonClient Initialize() {
        String API_TOKEN;
        try {
            File envObj = new File(".env");
            Scanner reader = new Scanner(envObj);
            String apiString = reader.nextLine();
            reader.close();

            String[] separatedString = apiString.split("=");
            API_TOKEN = separatedString[1];

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Now we can use the API token to do our work
        return new ExarotonClient(API_TOKEN);
    }
}
