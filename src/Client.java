import java.jdbc.*;

public static class Client{

    private static PreparedStatement getPwdStm = "SELECT motDePasse FROM Client WHERE mailClient LIKE ?";

    public static boolean essaiConnection(String mail, String password){
        PreparedStatement final = getPwdStm;
        final.setString(1, mail);
        final.prepareStatement();
    }

}