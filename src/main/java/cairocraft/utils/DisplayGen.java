package cairocraft.utils;

import java.util.Scanner;

public class DisplayGen {
    private static final String welcomeMessage = "Welcome to An00bis's Exaroton Server Manager!";
    private static final String welcomeBackMessage = "Welcome back to An00bis's Exaroton Server Manager";

    private static final String APIKeyPrompt = "Please enter your Exaroton API Key: ";
    private static final String SaveAPIKeyPrompt = "Would you like to save your API Key for future use? Y/N: ";
    private static final String SaveAPIPromptError = "Please input Y for yes or N for no: ";
    private static String helpMessage;

    private static boolean mUserExists;
    private static boolean mSaveAPIKey;
    private static String mUserName;

    public static void Initialize(boolean userExists, boolean saveAPIKey, String userName) {
        mUserExists = userExists;
        mSaveAPIKey = saveAPIKey;
        mUserName = userName;
    }

    public static void Initialize(boolean userExists, boolean saveAPIKey) {
        Initialize(userExists, saveAPIKey, "");
    }

    public static String[] WelcomeMessage() {
        // Only call this one if no existing API Key is found
        String[] strArray = new String[4]; // First entry is for APIKey (empty means look for it in file), second is UserExist, third is for setting SaveAPIKey
        if (mSaveAPIKey) {
            // The caller already has access to the API Key as it has been saved
            String userWelcome = welcomeBackMessage + ", " + mUserName + "!";
            System.out.println(userWelcome);
            strArray[0] = "";
            strArray[1] = "True";
            strArray[2] = "True";
        } else {
            // Caller doesn't have access to API Key
            Scanner myScanner = new Scanner(System.in);
            if (!mUserExists) {
                // First launch of application
                System.out.println(welcomeMessage);
                System.out.print(APIKeyPrompt);

                String apiKey = myScanner.nextLine();
                strArray[0] = apiKey;
                strArray[1] = "False";

                System.out.print(SaveAPIKeyPrompt);
                String saveAPIAnswer = myScanner.nextLine();
                while (!saveAPIAnswer.equalsIgnoreCase("Y") && !saveAPIAnswer.equalsIgnoreCase("N")) {
                    // If user enters anything other than y or n (case-insensitive), then make them answer again
                    System.out.print(SaveAPIPromptError);
                    saveAPIAnswer = myScanner.nextLine();
                }

                if (saveAPIAnswer.equalsIgnoreCase("Y")) {
                    strArray[2] = "True";
                } else {
                    strArray[2] = "False";
                }

            } else {
                // This is when the user has launched program before but declined
                // to save their API key
                System.out.println(welcomeBackMessage + "!");
                System.out.print(APIKeyPrompt);

                String apiKey = myScanner.nextLine();
                strArray[0] = apiKey;
                strArray[1] = "True";
                strArray[2] = "False";

            }

        }

        return strArray;
    }
}
