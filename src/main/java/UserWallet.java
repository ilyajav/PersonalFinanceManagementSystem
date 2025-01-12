import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.*;

public class UserWallet implements Serializable {

    private static final double INITIAL_BALANCE = 0.0;

    private static final String ENTER_INCOME_CATEGORY = "Enter income category: ";
    private static final String CATEGORY_CANNOT_BE_EMPTY = "Category cannot be empty.";
    private static final String ENTER_AMOUNT = "Enter the amount: ";
    private static final String AMOUNT_MUST_BE_POSITIVE = "The amount must be positive.";
    private static final String INVALID_AMOUNT_ENTERED = "Invalid amount entered. Please enter a valid number.";
    private static final String INCOME_ADDED = "Income added.";
    private static final String ENTER_EXPENSE_CATEGORY = "Enter the expense category: ";
    private static final String CONSUMPTION_ADDED = "Consumption added.";
    private static final String ENTER_BUDGET_CATEGORY = "Enter category: ";
    private static final String ENTER_BUDGET = "Enter budget: ";
    private static final String BUDGET_MUST_BE_POSITIVE = "The budget must be positive.";
    private static final String BUDGET_SET = "The budget has been set.";
    private static final String OVERALL_BALANCE = "Overall balance: ";
    private static final String TOTAL_REVENUES = "Total revenues: ";
    private static final String INCOME_BY_CATEGORY = "Income by category: ";
    private static final String TOTAL_EXPENSES = "Total expenses: ";
    private static final String EXPENSES_BY_CATEGORY = "Expenses by category: ";
    private static final String BUDGETS_BY_CATEGORY = "Budgets by category: ";
    private static final String WARNING_BUDGET_EXCEEDED = "Warning: You have exceeded your budget for this category ";
    private static final String INVALID_NUMBER_FORMAT = "Invalid number format";

    private double balance;
    private final Map<String, Double> income;
    private final Map<String, Double> expense;
    private final Map<String, Double> budgets;

    public UserWallet() {
        this.balance = INITIAL_BALANCE;
        this.income = new HashMap<>();
        this.expense = new HashMap<>();
        this.budgets = new HashMap<>();
    }

    public double getBalance() {
        return balance;
    }

    public void addIncome(Scanner scanner) {
        System.out.print(ENTER_INCOME_CATEGORY);

        String category = scanner.nextLine().trim();

        if (category.isEmpty()) {
            printMessage(CATEGORY_CANNOT_BE_EMPTY);
            return;
        }

        System.out.print(ENTER_AMOUNT);
        double amount;

        try {
            amount = Double.parseDouble(scanner.nextLine());
            if (amount <= 0) {
                printMessage(AMOUNT_MUST_BE_POSITIVE);
                return;
            }
        } catch (NumberFormatException e) {
            printMessage(INVALID_AMOUNT_ENTERED);
            return;
        }

        addIncomeDirect(category, amount);
        printMessage(INCOME_ADDED);
    }

    private void printMessage(String message) {
        System.out.println(message);
    }

    public void addExpense(Scanner scanner) {
        System.out.print(ENTER_EXPENSE_CATEGORY);
        String category = scanner.nextLine().trim();

        if (category.isEmpty()) {
            printMessage(CATEGORY_CANNOT_BE_EMPTY);
            return;
        }

        System.out.print(ENTER_AMOUNT);
        double amount;

        try {
            amount = Double.parseDouble(scanner.nextLine());
            if (amount <= 0) {
                printMessage(AMOUNT_MUST_BE_POSITIVE);
                return;
            }
        } catch (NumberFormatException e) {
            printMessage(INVALID_AMOUNT_ENTERED);
            return;
        }

        addExpenseDirect(category, amount);
        printMessage(CONSUMPTION_ADDED);
    }

    public void addIncomeDirect(String category, double amount) {
        income.put(category, income.getOrDefault(category, 0.0) + amount);
        balance += amount;
    }

    public void addExpenseDirect(String category, double amount) {
        if (amount <= 0) {
            System.out.println(AMOUNT_MUST_BE_POSITIVE);
            return;
        }

        double currentExpense = expense.getOrDefault(category, 0.0);
        double newExpense = currentExpense + amount;

        expense.put(category, newExpense);
        balance -= amount;

        if (budgets.containsKey(category) && newExpense > budgets.get(category)) {
            System.out.printf(WARNING_BUDGET_EXCEEDED + "%s!%n", category);
        }
    }



    public void setBudget(Scanner scanner) {
        System.out.print(ENTER_BUDGET_CATEGORY);
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            System.out.println(CATEGORY_CANNOT_BE_EMPTY);
            return;
        }

        System.out.print(ENTER_BUDGET);
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount <= 0) {
                System.out.println(BUDGET_MUST_BE_POSITIVE);
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println(INVALID_NUMBER_FORMAT);
            return;
        }

        budgets.put(category, amount);
        System.out.println(BUDGET_SET);
    }

    public void displayUserStatistics() {
        double totalRevenues = income.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalExpenses = expense.values().stream().mapToDouble(Double::doubleValue).sum();

        System.out.printf("%s: %.2f%n", OVERALL_BALANCE, balance);
        System.out.printf("%s: %.2f%n", TOTAL_REVENUES, totalRevenues);
        System.out.println(INCOME_BY_CATEGORY + income);
        System.out.printf("%s: %.2f%n", TOTAL_EXPENSES, totalExpenses);
        System.out.println(EXPENSES_BY_CATEGORY + expense);
        System.out.println(BUDGETS_BY_CATEGORY + budgets);

        budgets.forEach((category, budget) -> {
            double spent = expense.getOrDefault(category, 0.0);
            double left = budget - spent;
            System.out.printf("Category: %s, Budget: %.2f, Left: %.2f%n", category, budget, left);
        });
    }
}