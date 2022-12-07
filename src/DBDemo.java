import java.sql.*;

public class DBDemo {
  public static void main(String[] args) throws SQLException, ClassNotFoundException {
    // Load the JDBC driver
    Class.forName("org.mariadb.jdbc.Driver");
    System.out.println("Driver loaded");

    // Try to connect
    Connection connection = DriverManager.getConnection
      ("jdbc:mariadb://localhost/grenoble_eat", "dxkkxn", "dxkkxn");
    Client c = new Client(connection);
    c.browseCategories();
    System.out.println();
    // System.out.println(c.getRestaurants("cuisine savoyarde"));

    System.out.println("It works!");

    connection.close();
  }
}
