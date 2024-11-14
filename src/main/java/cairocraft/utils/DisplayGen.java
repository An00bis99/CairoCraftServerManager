package cairocraft.utils;

import java.util.Scanner;

public class DisplayGen {
    private static final String welcomeMessage = "Welcome to An00bis's Exaroton Server Manager!";
    private static final String welcomeBackMessage = "Welcome back to An00bis's Exaroton Server Manager";
    private static final String APIKeyPrompt = "Please enter your Exaroton API Key.\n";
    private static String helpMessage;

    private static boolean mUserExists;
    private static boolean mSaveAPIKey;
    private static String mUserName;

    public void Initialize(boolean userExists, boolean saveAPIKey, String userName) {
        mUserExists = userExists;
        mSaveAPIKey = saveAPIKey;
        mUserName = userName;
    }

    public void Initialize(boolean userExists, boolean saveAPIKey) {
        Initialize(userExists, saveAPIKey, "");
    }

    public void WelcomeMessage() {
        // Only call this one if no existing API Key is found
        if (!mUserExists) {
            System.out.println(welcomeMessage);
        } else if (mSaveAPIKey) {
            String userWelcome = welcomeBackMessage + ", " + mUserName + "!";
            System.out.println(userWelcome);
        } else {
            // This is when the user has launched program before but declined
            // to save their API key
            System.out.println(welcomeBackMessage + "!");

        }

        Scanner myScanner = new Scanner(System.in);

    }
}
