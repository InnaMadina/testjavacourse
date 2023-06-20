package main;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    Scanner scanner = new Scanner(System.in);
    List<Equation> equations = new ArrayList<>();
    DBConnection dbConnection = new DBConnection();

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    private void run() {
        equations = readFromDB();
        while (true) {
            int m = menu();
            if (m == 0) return;
            if (m == 1) {
                String equation = scanner.nextLine();
                boolean check = checkParenthesis(equation);
                if (check) {
                    System.out.println("Parenthesis correct");
                } else {
                    System.out.println("Parenthesis wrong");
                    continue;
                }
                String[] parts = equation.split("=");
                Expression left = new ExpressionBuilder(parts[0]).variable("x").build();
                Expression right = new ExpressionBuilder(parts[1]).variable("x").build();
                boolean res1 = left.validate(false).isValid();
                boolean res2 = right.validate(false).isValid();

                if (res1 && res2) {
                    System.out.println("correct");
                    saveToDB(equation);
                    inputRoots(equation);
                } else {
                    System.out.println("wrong");
                }
            }
            if (m == 2) {
                showAllEquations();
                showRoots();
            }
        }
    }

    private void showRoots() {
        System.out.println("id of equation?");
        int id = Integer.parseInt(scanner.nextLine());
        List<Root> roots = dbConnection.findRoots(id);
        for (Root root : roots) {
            System.out.println("x = " + root.getValue());
        }
    }

    private void showAllEquations() {
        System.out.println("-----------------------------------");
        for (Equation equation : equations) {
            System.out.println(equation);
        }
        System.out.println("-----------------------------------");
    }

    private void saveToDB(String text) {
        dbConnection.addEquation(text);
        equations = readFromDB();
    }

    private List<Equation> readFromDB() {
        return dbConnection.findAllEquations();
    }

//    private List<String> readFromDB() {
//        List<String> list = new ArrayList<>();
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader("file.txt"));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                list.add(line);
//            }
//            reader.close();
//        } catch (IOException ignored) {
//        }
//        return list;
//    }


    private void inputRoots(String text) {
        Equation eq = dbConnection.findEquation(text);
        if (eq == null) return;
        while (true) {
            System.out.println("root: ");
            double x = Double.parseDouble(scanner.nextLine());
            String[] part = text.split("=");
            Expression left = new ExpressionBuilder(part[0]).variable("x").build();
            double leftValue = left.setVariable("x", x).evaluate();
            Expression right = new ExpressionBuilder(part[1]).variable("x").build();
            double rightValue = right.setVariable("x", x).evaluate();
            if (Math.abs(leftValue - rightValue) < 1e-9) {
                dbConnection.addRoot(x, eq);
            } else {
                System.out.println("is not root");
            }
            System.out.println("more? (y/n)");
            String answer = scanner.nextLine().toLowerCase();
            if (!answer.startsWith("y")) break;
        }
    }

//    private void saveToDB(String equation) {
//        equations.add(equation);
//        try {
//            PrintWriter out = new PrintWriter("file.txt");
//            for (String s : equations) {
//                out.println(s);
//            }
//            out.close();
//        } catch (IOException ignored) {
//        }
//    }

    private int menu() {
        System.out.println("Choose action:");
        System.out.println("1. Add equation");
        System.out.println("2. Show equations");
        System.out.println("0. Exit");
        return Integer.parseInt(scanner.nextLine());
    }

    private boolean checkParenthesis(String equation) {
        String[] parts = equation.split("=");
        return check(parts[0]) && check(parts[1]);
    }

    private boolean check(String part) {
        int count = 0;
        for (int i = 0; i < part.length(); i++) {
            if (part.charAt(i) == '(') count++;
            else if (part.charAt(i) == ')') {
                count--;
                if (count < 0) return false;
            }
        }
        return count == 0;
    }
}
