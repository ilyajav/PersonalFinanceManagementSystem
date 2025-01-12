import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.io.*;

public class FinanceManager {
    private Map<String, User> users;
    private User loggedInUser;
    private final Scanner scanner;

    private static final String PROMPT_LOGIN = "Enter login: ";
    private static final String PROMPT_PASSWORD = "Enter password: ";
    private static final String SUCCESSFUL_LOGIN_MESSAGE = "Successful login.";
    private static final String ERROR_LOGIN_MESSAGE = "Incorrect login or password.";
    private static final String PROMPT_NEW_LOGIN = "Enter new login: ";
    private static final String LOGIN_EXISTS_MESSAGE = "Login already exists.";
    private static final String PROMPT_NEW_PASSWORD = "Enter new password: ";
    private static final String REGISTRATION_SUCCESS_MESSAGE = "Registration successful.";

    private static final String MENU_TEXT = """
    1. Add income
    2. Add expense
    3. Set a budget
    4. Show statistics
    5. Transfer funds to another user
    6. Log out
    """;

    private static final String PROMPT_RECIPIENT = "Enter the login of the user you want to transfer funds to: ";
    private static final String USER_NOT_FOUND = "User not found.";
    private static final String PROMPT_AMOUNT = "Enter the transfer amount: ";
    private static final String AMOUNT_MUST_BE_POSITIVE = "The transfer amount must be positive.";
    private static final String NOT_ENOUGH_FUNDS = "Not enough funds to transfer.";
    private static final String TRANSFER_SUCCESS = "Transfer completed successfully.";
    private static final String INVALID_COMMAND = "Invalid command.";
    private static final String INVALID_AMOUNT = "Invalid amount. Please enter a valid number.";
    private static final String TRANSFER_TO_USER = "Transfer to user ";
    private static final String TRANSFER_FROM_USER = "Transfer from user ";
    private static final String DATA_SAVED = "Data saved.";
    private static final String DATA_LOADED = "Data loaded.";
    private static final String SAVING_ERROR = "Saving error: ";
    private static final String LOADING_ERROR = "Loading error: ";

    private static final String USERS_FILE = "usersAccount.dat";

    public FinanceManager() {
        this.scanner = new Scanner(System.in);
        loadUsers();
    }

    public void runProgram() {
        while (true) {
            if (loggedInUser == null) {
                handleGuestMenu();
            } else {
                userMenu();
            }
        }
    }


    private void handleGuestMenu() {
        System.out.println("1. Login\n2. Register\n3. Log out");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                loginUser();
                break;
            case "2":
                registerUser();
                break;
            case "3":
                saveUsers();
                return;
            default:
                System.out.println(INVALID_COMMAND);
        }
    }

    private void loginUser() {
        System.out.print(PROMPT_LOGIN);
        String username = scanner.nextLine();
        System.out.print(PROMPT_PASSWORD);
        String password = scanner.nextLine();

        if (authenticateUser(username, password)) {
            loggedInUser = users.get(username);
            System.out.println(SUCCESSFUL_LOGIN_MESSAGE);
        } else {
            System.out.println(ERROR_LOGIN_MESSAGE);
        }
    }

    private boolean authenticateUser(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }

    private void registerUser() {
        System.out.print(PROMPT_NEW_LOGIN);
        String username = scanner.nextLine();

        if (isLoginExists(username)) {
            System.out.println(LOGIN_EXISTS_MESSAGE);
            return;
        }

        System.out.print(PROMPT_NEW_PASSWORD);
        String password = scanner.nextLine();

        User newUser = createUser(username, password);
        users.put(username, newUser);

        System.out.println(REGISTRATION_SUCCESS_MESSAGE);
    }

    private boolean isLoginExists(String username) {
        return users.containsKey(username);
    }

    private User createUser(String username, String password) {
        return new User(username, password);
    }


    private void userMenu() {
        System.out.println(MENU_TEXT);

        String choice = scanner.nextLine();
        handleUserChoice(choice);
    }

    private void handleUserChoice(String choice) {
        switch (choice) {
            case "1":
                addIncome();
                break;
            case "2":
                addExpense();
                break;
            case "3":
                setBudget();
                break;
            case "4":
                showStatistics();
                break;
            case "5":
                transferFunds();
                break;
            case "6":
                logOut();
                break;
            default:
                System.out.println(INVALID_COMMAND);
        }
    }

    private void addIncome() {
        loggedInUser.getWallet().addIncome(scanner);
    }

    private void addExpense() {
        loggedInUser.getWallet().addExpense(scanner);
    }

    private void setBudget() {
        loggedInUser.getWallet().setBudget(scanner);
    }

    private void showStatistics() {
        loggedInUser.getWallet().displayUserStatistics();
    }

    private void logOut() {
        loggedInUser = null;
    }

    private void transferFunds() {
        String recipientUsername = getUserInput();
        User recipient = users.get(recipientUsername);

        if (recipient == null) {
            printErrorMessage(USER_NOT_FOUND);
            return;
        }

        double amount = getTransferAmount();
        if (amount <= 0) {
            printErrorMessage(AMOUNT_MUST_BE_POSITIVE);
            return;
        }

        if (canTransferFunds(amount)) {
            performTransfer(recipient, amount);
            System.out.println(TRANSFER_SUCCESS);
        } else {
            printErrorMessage(NOT_ENOUGH_FUNDS);
        }
    }

    private String getUserInput() {
        System.out.print(FinanceManager.PROMPT_RECIPIENT);

        return scanner.nextLine();
    }

    private double getTransferAmount() {
        System.out.print(PROMPT_AMOUNT);
        while (!scanner.hasNextDouble()) {
            System.out.println(INVALID_AMOUNT);
            scanner.next();
        }
        return scanner.nextDouble();
    }

    private boolean canTransferFunds(double amount) {
        return loggedInUser.getWallet().getBalance() >= amount;
    }

    private void performTransfer(User recipient, double amount) {
        loggedInUser.getWallet().addExpenseDirect(TRANSFER_TO_USER + recipient.getUsername(), amount);
        recipient.getWallet().addIncomeDirect(TRANSFER_FROM_USER + loggedInUser.getUsername(), amount);
    }

    private void printErrorMessage(String message) {
        System.out.println(message);
    }

    private void saveUsers() {
        try {
            writeUsersToFile();
            System.out.println(DATA_SAVED);
        } catch (IOException e) {
            handleSaveError(e);
        }
    }

    private void writeUsersToFile() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        }
    }

    private void handleSaveError(IOException e) {
        System.out.println(SAVING_ERROR + e.getMessage());
    }

    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            users = (Map<String, User>) ois.readObject();
            System.out.println(DATA_LOADED);
        } catch (FileNotFoundException e) {
            users = initializeUsersMap();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(LOADING_ERROR + e.getMessage());
            users = initializeUsersMap();
        }
    }

    private Map<String, User> initializeUsersMap() {
        return new HashMap<>();
    }
}