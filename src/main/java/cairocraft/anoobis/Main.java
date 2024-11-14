package cairocraft.anoobis;

import cairocraft.utils.DisplayGen;
import cairocraft.utils.GenerateClient;
import com.exaroton.api.ExarotonClient;

import java.io.File;
import java.io.FileNotFoundException;
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
        String userName = "";
        boolean userExists = false;
        boolean apiSave = false;
        try {
            File configFile = new File("main.config");
            Scanner myScanner = new Scanner(configFile);

            try {
                apiKey = myScanner.nextLine();
                userName = myScanner.nextLine();
                userExists = myScanner.nextBoolean();
                apiSave = myScanner.nextBoolean();
            } catch (NoSuchElementException e) {
                System.out.println("Error occurred while reading the config file");
                System.exit(1);
            }
            myScanner.close();

            DisplayGen.Initialize(userExists, apiSave);
        } catch (FileNotFoundException e) {
            // File doesn't exist so do the call as if no user exists
            DisplayGen.Initialize(userExists, apiSave);
        }

        String[] configArray = new String[3];
        configArray = DisplayGen.WelcomeMessage();


        // Then initialize the client
        ExarotonClient client;

        try {
            client = GenerateClient.Initialize();
        } catch (Exception e) {
            System.out.println("Error occurred while initializing ExarotonClient");
            System.out.println("Error message: " + e.getMessage());
            System.exit(1);
        }

        //


    }
}