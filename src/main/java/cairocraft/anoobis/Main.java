package cairocraft.anoobis;

import cairocraft.utils.DisplayGen;
import cairocraft.utils.GenerateClient;
import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.account.Account;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.subscriber.ConsoleSubscriber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    private static ExarotonClient mUserClient = null;
    private static Server mCurrServer = null;

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

        String apiKey = "";
        boolean userExists = false;
        boolean apiSave = false;
        String userName = "";
        String[] configArray = new String[3];

        if (!DevMode) {
            // First check if config file exists
            try {
                File configFile = new File("main.config");
                Scanner myScanner = new Scanner(configFile);

                try {
                    String[] apiSplit = (myScanner.nextLine()).split("=");
                    if (apiSplit.length == 2) {
                        apiKey = apiSplit[1];
                    }
                    userExists = Boolean.parseBoolean((myScanner.nextLine()).split("=")[1]);
                    apiSave = Boolean.parseBoolean((myScanner.nextLine()).split("=")[1]);
                    userName = (myScanner.nextLine()).split("=")[1];
                } catch (NoSuchElementException e) {
                    System.out.println("Error occurred while reading the config file");
                    System.exit(1);
                }
                myScanner.close();
                // Initialize with all fields since user exists (username may not be specified)
                DisplayGen.Initialize(userExists, apiSave, userName);
            } catch (FileNotFoundException e) {
                // File doesn't exist so do the call as if no user exists
                DisplayGen.Initialize(userExists, apiSave);
            }

            configArray = DisplayGen.WelcomeMessage();

            if (apiKey.isEmpty()) {
                // Only use userInput APIKey if none on file
                apiKey = configArray[0];
            }
        }


        // Then initialize the client
        //ExarotonClient client = null;

        try {
            if (DevMode) {
                mUserClient = GenerateClient.Initialize();
            } else {
                mUserClient = GenerateClient.Initialize(apiKey);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Error occurred while initializing ExarotonClient");
            System.err.println("Error message: " + e.getMessage());
            System.exit(1);
        }

        if (!DevMode) {

            try {
                Account account = mUserClient.getAccount();
                userName = account.getName(); // Sets username if API key given but no username is stored
                System.out.println("Welcome, " + userName + "!");
            } catch (APIException e) {
                System.err.println("Error occurred while getting account info");
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
        }

        // Now we can do everything we wanted to do
        // Server currServer = null;
        String serverName = "No Server Being Managed";
        String serverStatus;
        boolean isOnline = false;
        String serverId = "";
        int inputNum = 0;
        while (inputNum != 6) {
            // Init while loop for main menu
            if (mCurrServer != null) {
                serverName = mCurrServer.getName();
                isOnline = mCurrServer.hasStatus(ServerStatus.ONLINE);
            }
            if (isOnline) {
                serverStatus = "online";
            } else {
                serverStatus = "offline";
            }
            System.out.println("Enter the corresponding number/letter to make your selection\n");
            System.out.println("Current Server: " + serverName + " | Currently " + serverStatus + "\n");
            System.out.println("1. Change server being managed");
            System.out.println("2. Establish Connection with the server's Console");
            System.out.println("3. Start the server");
            System.out.println("4. Stop the server");
            System.out.println("5. Modify server files");
            System.out.println("6. Exit");


            inputNum = MenuInputParse(1, 6);

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

    private static int MenuInputParse(int firstOption, int lastOption) {
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
                if (userConverted < firstOption || userConverted > lastOption) {
                    throw new NumberFormatException("Number out of range");
                }
                rightFormat = true;
            } catch (NumberFormatException e) {
                if (e.getMessage().equals("Number out of range")) {
                    System.out.println("Please enter a number " + firstOption + "-" + lastOption);
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

        Server[] serverList = null;
        try {
            serverList = mUserClient.getServers();
        } catch (APIException e) {
            System.out.println("Error occurred while getting server list");
            System.exit(1);
        }

        // Then print them in order
        for (int i = 0; i < serverList.length; i++) {
            System.out.println((i + 1) + ". Server Name: " + serverList[i].getName());
            System.out.println("   Server Address: " + serverList[i].getAddress() + "\n");
        }

        System.out.print("Enter the corresponding number to choose which server you would like to manage: ");
        // Need to subtract 1 because we have a zero-indexed list rather than 1-indexed
        int chosenServer = MenuInputParse(1, serverList.length) - 1;

        mCurrServer = serverList[chosenServer];
        System.out.println("\n" + mCurrServer.getName() + " is now being managed!\n");

    }

    private static void ConsoleConnectSubMenu() {
        if (!ServerExists()) {
            return;
        }
        System.out.println("You are about to be connected to the console associated with your server.");
        System.out.println("If you type 'q', you will exit the console menu without stopping the server.");
        System.out.print("Type 'y' if you want to proceed or type 'n' if you want to go back: ");

        Scanner myScanner = new Scanner(System.in);
        String answer = myScanner.nextLine();
        while (!answer.equalsIgnoreCase("y") && !answer.equalsIgnoreCase("n")) {
            // If user enters anything other than y or n (case-insensitive), then make them answer again
            System.out.print("Please answer y/n: ");
            answer = myScanner.nextLine();
        }

        if (answer.equalsIgnoreCase("y")) {
            System.out.println();
            mCurrServer.subscribe("console");


            mCurrServer.addConsoleSubscriber(new ConsoleSubscriber() {
                @Override
                public void line(String line) {
                    System.out.println(line);
                }
            });
            Scanner commandScanner = new Scanner(System.in);
            String userCommand = "";
            while (!userCommand.equals("q")) {
                if (!userCommand.isEmpty()) {
                    // Don't execute command if blank
                    try {
                        mCurrServer.executeCommand(userCommand);
                    } catch (APIException e) {
                        System.out.println("Error occurred while executing the user's command");
                        System.exit(1);
                    }

                }

                // Read input from user
                userCommand = commandScanner.nextLine();
            }

            // Unsub and close everything
            mCurrServer.unsubscribe();
            System.out.println("Server console has been closed!\n");
        }

    }

    private static void StartServer() {
        if (!ServerExists()) {
            return;
        }

        try {
            mCurrServer.start();
        } catch (APIException e) {
            System.out.println("Error occurred while starting the server. Exiting...");
            System.exit(1);
        }

        System.out.println("\nServer has been started!\n");
    }

    private static void StopServer() {
        if (!ServerExists()) {
            return;
        }

        try {
            mCurrServer.stop();
        } catch (APIException e) {
            System.out.println("Error occurred while stopping the server. Exiting...");
            System.exit(1);
        }

        System.out.println("\nServer has been stopped!\n");
    }

    private static void ModifyFilesSubMenu() {
        if (!ServerExists()) {
            return;
        }

        System.out.println("1. Create a new file");
        System.out.println("2. Create a new directory");
        System.out.println("3. Replace a file");
        System.out.println("4. Delete a file");
        System.out.println("5. Delete a directory");
        System.out.println("6. Go back to the main menu\n");

        int inputNum = MenuInputParse(1, 6);
        Scanner myScanner = new Scanner(System.in);
        switch (inputNum) {
            case 1:
                System.out.print("\nPlease enter the file name, including the directory: ");
                //ServerFile currFile = new ServerFile(mUserClient, mCurrServer, )
        }

    }

    private static boolean ServerExists() {
        if (mCurrServer == null) {
            System.out.println("\nYou need to select a server to manage first!\n");
            return false;
        }
        return true;
    }

}