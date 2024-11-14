package cairocraft.anoobis;

import cairocraft.utils.DisplayGen;
import cairocraft.utils.GenerateClient;
import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.account.Account;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // CLI Flags
        // -d stands for Developer mode
        //      This will read in the API token from a .env file
        //      rather than taking it from the user (if they don't already
        //      have it on file) and storing it in the local directory
        boolean DevMode = false;

        for (String arg : args) {
            switch (arg) {
                case "-d":
                    // Enable developer mode
                    DevMode = true;
                    break;
                case "-h":
                    // Display commands
                    System.out.println("Here are the command line arguments you can use:");
                    System.out.println("-d");
                    System.out.println("\tLet's you access developer mode which reads your API key from a .env file\n");
                    System.out.println("-h");
                    System.out.println("\tPrints out all available command line args (like how we're doing right now!)\n");
                    System.exit(0);
                    break;
            }
        }

        // First check if config file exists
        String apiKey = "";
        boolean userExists = false;
        boolean apiSave = false;
        String userName = "";
        try {
            File configFile = new File("main.config");
            Scanner myScanner = new Scanner(configFile);

            try {
                apiKey = (myScanner.nextLine()).split("=")[1];
                userExists = Boolean.parseBoolean((myScanner.nextLine()).split("=")[1]);
                apiSave = Boolean.parseBoolean((myScanner.nextLine()).split("=")[1]);
                userName = (myScanner.nextLine()).split("=")[1];
            } catch (NoSuchElementException e) {
                System.out.println("Error occurred while reading the config file");
                System.exit(1);
            } finally {
                myScanner.close();
            }

            // Initialize with all fields since user exists (username may not be specified)
            DisplayGen.Initialize(userExists, apiSave, userName);
        } catch (FileNotFoundException e) {
            // File doesn't exist so do the call as if no user exists
            DisplayGen.Initialize(userExists, apiSave);
        }

        String[] configArray = new String[3];
        configArray = DisplayGen.WelcomeMessage();

        if (apiKey.isEmpty()) {
            apiKey = configArray[0];
        }


        // Then initialize the client
        ExarotonClient client = null;

        try {
            if (DevMode) {
                client = GenerateClient.Initialize();
            } else {
                client = GenerateClient.Initialize(apiKey);
            }
        } catch (Exception e) {
            System.out.println("Error occurred while initializing ExarotonClient");
            System.out.println("Error message: " + e.getMessage());
            System.exit(1);
        }

        try {
            Account account = client.getAccount();
            userName = account.getName(); // Sets username if API key given but no username is stored
            System.out.println("Welcome, " + userName + "!");
        } catch (APIException e) {
            System.out.println("Error occurred while getting account info");
            System.exit(1);
        }

        // Then write back the user's preferences
        try {
            FileWriter configWrite = new FileWriter("main.config");
            String apiToWrite = "";
            if (configArray[2].equals("True")) {
                apiToWrite += apiKey;
            }
            configWrite.write("API=" + apiToWrite + "\n");
            configWrite.write("userExists=" + configArray[1] + "\n");
            configWrite.write("apiSave=" + configArray[2] + "\n");
            configWrite.write("userName=" + userName + "\n");
            configWrite.close();

        } catch (IOException e) {
            System.out.println("Error occurred while writing the config file");
            System.exit(1);
        }

        // Now we can do everything we wanted to do
        String inputString = "";
        while (!inputString.equals("q")) {
            // Init while loop for main menu

        }

    }


}