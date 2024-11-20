package cairocraft.utils;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.account.Account;

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

        ExarotonClient client = null;
        client = Initialize(API_TOKEN); // Let the caller handle any exceptions that are thrown

        // Now we can use the API token to do our work
        return client;
    }

    public static ExarotonClient Initialize(String API_TOKEN) {

        ExarotonClient client = null;
        try {
            client = new ExarotonClient(API_TOKEN);
            Account testAccount = client.getAccount();
        } catch (Exception e) {
            throw new RuntimeException("API Token is invalid. Session terminating...", e.getCause());
        }
        return client;
    }


}
