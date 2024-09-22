package util;

import java.sql.*;

public class DatabaseConnector {

    private static final String URL = "jdbc:mysql://localhost:3306/hospitality";
    private static final String USER = "root";
    private static final String PASSWORD = "jain.dev@2003";

    public static Connection getConnection() throws SQLException{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            System.out.println(e);
            // return null;
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
//
//import java.sql.*;
//
//public class jdbc_connect {
//    public static void main(String args[]) {
//        try {
//            // driver
//            Class.forName("com.mysql.cj.jdbc.Driver");
//
//            // connection string
//            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_practice", "root", "jain.dev@2003");
//
//            // create table using statement object
//            Statement stmt = con.createStatement();
//
//            // query
//            stmt.executeUpdate("create table employee (e_no int, e_name varchar(30), e_sal int)");
//            System.out.println("Table created successfully");
//            con.close();
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//    }
//}
