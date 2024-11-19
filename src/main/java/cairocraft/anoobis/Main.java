package cairocraft.anoobis;

import cairocraft.utils.DisplayGen;
import cairocraft.utils.GenerateClient;
import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.account.Account;
import com.exaroton.api.server.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    private static final Account mUserAccount = null;

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
            // Only use userInput APIKey if none on file
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
        Server currServer = null;
        String serverName = "No Server Being Managed";
        String serverId = "";
        int inputNum = 0;
        while (inputNum != 6) {
            // Init while loop for main menu
            System.out.println("Enter the corresponding number/letter to make your selection\n");
            System.out.println("Current Server: " + serverName + "\n");
            System.out.println("1. Change server being managed");
            System.out.println("2. Establish Connection with the server's Console");
            System.out.println("3. Start the server");
            System.out.println("4. Stop the server");
            System.out.println("5. Modify server files");
            System.out.println("6. Exit");


            inputNum = MenuInputParse();

            switch (inputNum) {
                case 1:
                    // Change server
                    ChangeServerSubMenu();
                    break;
                case 2:
                    // Connect to server's console
                    ConsoleConnectSubMenu();
                    break;
                case 3:
                    // Start selected server
                    StartServer();
                    break;
                case 4:
                    // Stop server
                    StopServer();
                    break;
                case 5:
                    ModifyFilesSubMenu();
                    break;
                case 6:
                    // Exit program
                    break;
            }

        }

        System.out.println("\nSee you soon!");
    }

    private static int MenuInputParse() {
        // Returns num parsed from user input
        // Used for numbered option menus
        Scanner myScanner = new Scanner(System.in);

        // Interpret input
        boolean rightFormat = false;
        int userConverted = 0;

        while (!rightFormat) {
            String userAnswer = myScanner.nextLine();
            try {
                userConverted = Integer.parseInt(userAnswer);
                if (userConverted < 1 || userConverted > 6) {
                    throw new NumberFormatException("Number out of range");
                }
                rightFormat = true;
            } catch (NumberFormatException e) {
                if (e.getMessage().equals("Number out of range")) {
                    System.out.println("Please enter a number 1-6.");
                } else {
                    System.out.println("Please input a valid number.");
                }
            }
        }

        return userConverted;
    }

    private static void ChangeServerSubMenu() {
        // First get available servers
        System.out.println("Here are the servers associated with your account:\n");
        // Assign a number to each account
        

    }

    private static void ConsoleConnectSubMenu() {

    }

    private static void StartServer() {

    }

    private static void StopServer() {

    }

    private static void ModifyFilesSubMenu() {

    }

}