package cairocraft.anoobis;

import cairocraft.utils.GenerateClient;
import com.exaroton.api.ExarotonClient;

public class Main {
    public static void main(String[] args) {
        // CLI Flags
        // -d stands for Developer mode
        //      This will read in the API token from a .env file
        //      rather than taking it from the user (if they don't already
        //      have it on file) and storing it in the local directory
        boolean DevMode = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "d":
                    // Enable developer mode
                    DevMode = true;
                    break;
            }
        }
        // First initialize the client
        ExarotonClient client;

        try {
            client = GenerateClient.Initialize();
        } catch (Exception e) {
            System.out.println("Error occurred while initializing ExarotonClient");
            System.out.println("Error message: " + e.getMessage());
            System.exit(1);
        }


    }
}