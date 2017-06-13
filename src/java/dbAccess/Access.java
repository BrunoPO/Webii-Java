package dbAccess;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Access {
    private Connection connection = null;
    private Statement statement = null;
    
    public Access(){
        URL url = getClass().getResource("Data.db");
        System.out.println("Teste: "+url);
        String urls = url.toString();
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:"+urls);
            this.statement = connection.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(Access.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(Access.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void connectionClose(){
        try {
            this.statement.close();
            this.connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(Access.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private StringBuilder sqlStringToVector(List<String> Lista){
        StringBuilder sb = new StringBuilder();
        //sb.append('[');
        sb.append(Lista);
        //sb.setCharAt(sb.length()-1, ' ');
        for(int i=0;i<sb.length();i++){
            if(i > 1 && sb.charAt(i) == ' ' &&  (sb.charAt(i-1) == '\"' || sb.charAt(i-1) == ']' )){
                sb.setCharAt(i, ',');
            }else if(sb.charAt(i) == '(')
                sb.setCharAt(i, '[');
            else if (sb.charAt(i) == ')'){
                sb.setCharAt(i, ']');
                if(sb.charAt(i+1) == ')')
                    if(i+3 < sb.length()-1 && sb.charAt(i+3) != ','){
                        if(sb.charAt(i+3) == '[')
                        sb.setCharAt(i+2, ']');
                        sb.setCharAt(i+2, ',');
                    }
                    
            }
            
        }
        if(sb.charAt(sb.length()-1) == ','){
            sb.delete(sb.length()-1,1);
        }
        return sb;
    }
    public ResultSet selectSQL(String query){    
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(Access.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }
    public ResultSet insertSQL(String query) throws SQLException, IllegalAccessException, InstantiationException {    
        ResultSet rs = null;

        statement.executeUpdate(query);
        rs = statement.executeQuery("SELECT last_insert_rowid() as ID;");
        //statement.close();
        return rs;
    }
       
}