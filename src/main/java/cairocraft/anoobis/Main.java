package cairocraft.anoobis;

import cairocraft.utils.DisplayGen;
import cairocraft.utils.GenerateClient;
import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.account.Account;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerFile;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.subscriber.ConsoleSubscriber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static java.nio.file.Files.isDirectory;

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
        String serverStatus = "unavailable";
        int serverState = 0;
        String serverId = "";
        int inputNum = 0;
        while (inputNum != 6) {
            // Init while loop for main menu
            if (mCurrServer != null) {
                serverName = mCurrServer.getName();
                serverState = mCurrServer.getStatus();
            }
            // TODO: Will change this into an array access with the array entries being the strings
            // TODO: Could also do ENUM
            switch (serverState) {
                case 0:
                    serverStatus = "offline";
                    break;
                case 1:
                    serverStatus = "online";
                    break;
                case 2:
                    serverStatus = "starting";
                    break;
                case 3:
                    serverStatus = "stopping";
                    break;
                case 4:
                    serverStatus = "restarting";
                    break;
                case 5:
                    serverStatus = "saving";
                    break;
                case 6:
                    serverStatus = "loading";
                    break;
                case 7:
                    serverStatus = "crashed";
                    break;
                case 8:
                    serverStatus = "pending";
                    break;
                case 9:
                    serverStatus = "transferring";
                    break;
                case 10:
                    serverStatus = "preparing";
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
        } else {
            if (mCurrServer.hasStatus(ServerStatus.ONLINE)) {
                System.out.println("Can't start the server as it is already started!\n");
            } else if ((mCurrServer.hasStatus(ServerStatus.OFFLINE) || mCurrServer.hasStatus(ServerStatus.CRASHED))) {
                System.out.println("Server is currently starting!\n");
                try {
                    mCurrServer.start();
                } catch (APIException e) {
                    // TODO: Gracefully handle the API Exception
                    System.out.println("Error occurred while starting the server. Exiting...");
                    System.exit(1);
                }
            } else {
                System.out.println("Server is currently starting!\n");
            }
        }
    }

    private static void StopServer() {
        if (!ServerExists()) {
            return;
        } else {
            if (mCurrServer.hasStatus(ServerStatus.OFFLINE) || mCurrServer.hasStatus(ServerStatus.CRASHED)) {
                System.out.println("Can't stop the server as it hasn't been started yet!\n");
            }
            else if (mCurrServer.hasStatus(ServerStatus.STOPPING)) {
                System.out.println("Server is already stopping!\n");
            }
            else if (mCurrServer.hasStatus(ServerStatus.ONLINE)) {
                try {
                    mCurrServer.stop();
                } catch (APIException e) {
                    System.out.println("Error occurred while stopping the server. Exiting...");
                    System.exit(1);
                }
            }
            else {
                System.out.println("Server is currently occupied!\n");
            }
        }
    }

    private static void ModifyFilesSubMenu() {
        if (!ServerExists()) {
            return;
        }

        int inputNum = 0;

        Scanner myScanner = new Scanner(System.in);
        String serverFilePath;
        String serverDir;
        String localFilePath;
        while (inputNum != 7) {
            System.out.println("\n1. Upload a new file (Overwrites existing files)");
            System.out.println("2. Download a file");
            System.out.println("3. Upload all files in a directory");
            System.out.println("4. Create a new directory");
            System.out.println("5. Delete a file");
            System.out.println("6. Delete a directory");
            System.out.println("7. Go back to the main menu\n");

            inputNum = MenuInputParse(1, 7);

            switch (inputNum) {
                case 1:
                    System.out.print("\nPlease enter the desired filename on the server, including the directory path: ");
                    serverFilePath = myScanner.nextLine();
                    System.out.println("\nNow enter the path of the file you want to upload, including the directory path: ");
                    localFilePath = myScanner.nextLine();
                    if (FileTransfer(true, serverFilePath, localFilePath) == 1) {
                        break;
                    }

                    System.out.println("\nServer file has been successfully uploaded!\n");
                    break;
                case 2:
                    System.out.println("\nPlease enter the name of the file you'd like to download from the server, including the directory path: ");
                    serverFilePath = myScanner.nextLine();
                    System.out.println("Now enter the name you want to give this file, including the directory path: ");
                    localFilePath = myScanner.nextLine();
                    // Now we download the file to the specified local path
                    if (FileTransfer(false, serverFilePath, localFilePath) == 1) {
                        break;
                    }

                    System.out.println("\nServer file has been successfully downloaded!\n");
                    break;
                case 3:
                    System.out.println("\nPlease enter the name of the server directory you'd like to upload to: ");
                    serverDir = myScanner.nextLine();
                    System.out.println("\nNow enter the local directory that contains the files you want to upload: ");
                    String localDir = myScanner.nextLine();
                    // Now we loop over every file in the local directory and upload it to the server directory
                    File localDirAccessible = new File(localDir);
                    File[] filesToUpload = localDirAccessible.listFiles();

                    if (filesToUpload == null) {
                        System.out.println("There are no files in the provided local directory. Aborting...\n");
                        break;
                    }

                    if (CreateDirectory(serverDir) == 1) {
                        break;
                    }

                    for (File fileToUpload : filesToUpload) {
                        String fullLocalPath = localDir + "/" + fileToUpload.getName();
                        String fullServerPath = serverDir + "/" + fileToUpload.getName();

                        if (FileTransfer(true, fullServerPath, fullLocalPath) == 1) {
                            break;
                        }
                    }

                    System.out.println("\nAll files have been successfully uploaded!\n");
                    break;
                case 4:
                    System.out.println("\nPlease enter the name of the server directory you'd like to create: ");
                    serverDir = myScanner.nextLine();
                    if (CreateDirectory(serverDir) == 1) {
                        break;
                    }

                    System.out.println("\nServer directory has been successfully created!\n");
                    break;
                case 5:
                    // Just do inverse of 1st case
                    System.out.println("\nPlease enter the name of the server file you'd like to delete: ");
                    serverFilePath = myScanner.nextLine();
                    if (DeleteOffServer(serverFilePath) == 1) {
                        break;
                    }

                    System.out.println("\nServer file has been successfully deleted!\n");
                    break;
                case 6:
                    // Delete the directory, empty or not
                    System.out.println("\nPlease enter the name of the server directory you'd like to delete: ");
                    serverDir = myScanner.nextLine();
                    if (DeleteOffServer(serverDir) == 1) {
                        break;
                    }

                    System.out.println("\nServer directory has been successfully deleted!\n");
                    break;
                case 7:
                    break;
            }
        }

    }

    private static int DeleteOffServer(String serverFilePath) {
        // Return 1 if unsuccessful
        ServerFile currFile = mCurrServer.getFile(serverFilePath);
        try {
            currFile.delete();
        } catch (APIException e) {
            System.out.println("Error occurred during deletion (Exaroton API). Reason: " + e.getMessage() + "\n");
            return 1;
        }
        return 0;
    }

    private static int CreateDirectory(String serverDir) {
        ServerFile currFile = mCurrServer.getFile(serverDir);
        try {
            currFile = mCurrServer.getFile(serverDir);
            currFile.createAsDirectory(); // No check needed, will do nothing if dir already exists
        } catch (APIException e) {
            // Problem creating the directory
            System.out.println("Error occurred while creating the directory. Reason: " + e.getMessage() + "\n");

            return 1;

        }

        return 0;
    }

    private static int FileTransfer(boolean isUpload, String serverFilePath, String localFilePath) {
        ServerFile currFile = mCurrServer.getFile(serverFilePath);
        if (isUpload) {
            // Check if given file path is another directory
            if (isDirectory(Paths.get(localFilePath))) {
                // Directory so, do recursion
                CreateDirectory(serverFilePath);
                File localDirAccessible = new File(localFilePath);
                File[] filesToUpload = localDirAccessible.listFiles();


                if (filesToUpload != null) {
                    for (File fileToUpload : filesToUpload) {
                        String fullLocalPath = localFilePath + "/" + fileToUpload.getName();
                        String fullServerPath = serverFilePath + "/" + fileToUpload.getName();

                        if (FileTransfer(true, fullServerPath, fullLocalPath) == 1) {
                            return 1;
                        }
                    }
                }

                return 0;
            }

            try {
                currFile.upload(Paths.get(localFilePath));

            } catch (InvalidPathException e) {
                System.out.println("The path to the file you want to upload doesn't exist. Aborting...\n");
                return 1;
            } catch (IOException e) {
                System.out.println("Error occurred while uploading the file. Reason: " + e.getMessage() + "\n");
                return 1;
            } catch (APIException e) {
                System.out.println("Error occurred while uploading the file (From Exaroton's API). Reason: " + e.getMessage() + "\n");
                return 1;
            }
            return 0;
        } else {
            try {
                currFile.download(Paths.get(localFilePath));
            } catch (IOException e) {
                System.out.println("Error occurred while downloading the file. Reason: " + e.getMessage() + "\n");
                return 1;
            } catch (APIException e) {
                System.out.println("Error occurred while downloading the file (From Exaroton's API). Reason: " + e.getMessage() + "\n");
                return 1;
            }
            return 0;
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