
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private Connection conn;
    private static PreparedStatement subcategoriesStmt;
    private static PreparedStatement resInCatStmt;

    public void prepareAllStatements() {
        try {
            subcategoriesStmt = conn.prepareStatement("SELECT nomCategorieFille FROM aPourCategorieMere"
                                                 +" WHERE nomCategorie LIKE ?");
            resInCatStmt = conn.prepareStatement(
                    " with recursive succesor as"
                    +" (select nomCategorieFille as cat from aPourCategorieMere"
                    +" where nomCategorie LIKE ?"
                    +" union all"
                    +" select nomCategorieFille from succesor, aPourCategorieMere"
                    +" where succesor.cat = aPourCategorieMere.nomCategorie)"
                    +" select distinct nomRestaurant"
                    +" from Restaurant r, aPourCategorie apc, succesor s"
                    +" where r.mailRestaurant = apc.mailRestaurant and"
                    +"      (apc.nomCategorie = s.cat or"
                    +"      apc.nomCategorie LIKE ?)");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public Client(Connection conn) {
        this.conn = conn;
        prepareAllStatements();
    }

    public void browseCategories() {
        String root = getRoot();
        System.out.println(root);
        System.out.println(getSubcategories(root));
        System.out.println(getSubcategories(getSubcategories(root).get(0)));
    }

    public String getRoot() {
        String root = "";
        try {
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT nomCategorie FROM Categories"
                              +" WHERE nomCategorie NOT IN "
                              +" (SELECT nomCategorieFille FROM aPourCategorieMere)");
            int count = 0;
            while (res.next()) {
                root = res.getString(1);
                count++;
            }
            assert (count == 1);
        } catch (SQLException e) {
            e.printStackTrace ();
        }
        return root;
    }
    /*
     * Renvoie tous les restaurants appartenant a la categorie category et
     * tous ses filles.
     * @param category
     */
    public List<String> getRestaurants(String category) {
        List<String> restaurants = new ArrayList<>();
        try {
            resInCatStmt.setString(1, category);
            resInCatStmt.setString(2, category);
            ResultSet res = resInCatStmt.executeQuery();
            while (res.next()) {
                restaurants.add(res.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace ();
        }
        return restaurants;

    }
    public List<String> getSubcategories(String category) {
        List<String> subcategories = new ArrayList<>();
        try {
            subcategoriesStmt.setString(1, category);
            ResultSet res = subcategoriesStmt.executeQuery();
            while (res.next()) {
                subcategories.add(res.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace ();
        }
        return subcategories;

    }

}
