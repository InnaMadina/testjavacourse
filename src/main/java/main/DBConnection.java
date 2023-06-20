package main;

import lombok.SneakyThrows;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {
    private Connection connection;

    @SneakyThrows
    public DBConnection() {
        connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/equations", "eugeny", "123");
    }

    @SneakyThrows
    public List<Equation> findAllEquations() {
        List<Equation> result = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from equation");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            String text = rs.getString("text");
            Equation e = new Equation();
            e.setId(id);
            e.setText(text);
            result.add(e);
        }
        ps.close();
        return result;
    }

    @SneakyThrows
    public void addEquation(String text) {
        try (PreparedStatement ps = connection.prepareStatement("insert into equation (text) values (?)")) {
            ps.setString(1, text);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("!!! error while add equation !!!");
        }
    }

    @SneakyThrows
    public Equation findEquation(String text) {
        try (PreparedStatement ps = connection.prepareStatement("select * from equation where text = ?")) {
            ps.setString(1, text);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                //String text = rs.getString("text");
                Equation equation = new Equation();
                equation.setText(text);
                equation.setId(id);
                return equation;
            } else {
                return null;
            }
        }
    }

    @SneakyThrows
    public void addRoot(double x, Equation eq) {
        try (PreparedStatement ps = connection.prepareStatement("insert into root (value, equation_id) values (?, ?)")) {
            ps.setDouble(1, x);
            ps.setInt(2, eq.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("!!! error while add root !!!");
        }
    }

    @SneakyThrows
    public List<Root> findRoots(int id) {
        List<Root> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("select * from root where equation_id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id1 = rs.getInt("id");
                double value = rs.getDouble("value");
                result.add(new Root(id1, value));
            }
        }
        return result;
    }
}
