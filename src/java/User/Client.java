/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

    
import Arquivo.Arquivo;
import dbAccess.Access;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author prog
 */
public class Client {
    private String nome,email,login,id;
    private List<Arquivo> migalha;

    public List<Arquivo> getMigalha() {
        return migalha;
    }

    public void setMigalha(List<Arquivo> migalha) {
        this.migalha = migalha;
    }
    public String getId() {
        return id;
    }
    
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public boolean Deletar(){
        try {
            String query = "DELETE FROM Usuarios where id = "+getId()+";";
            System.out.println("Delete query:"+query);
            Access db = new Access();
            db.insertSQL(query);
            db.connectionClose();
        } catch (SQLException | IllegalAccessException | InstantiationException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    public boolean Alterar(String nome,String email,String login,String senha){
        System.out.println("ID:"+getId());
        String query = "UPDATE Usuarios SET nome='"+nome+"',email='"+email+"',login='"+login+"',senha='"+senha+"' WHERE ID = "+this.id+";";
        
        Access db = new Access();
        try {
            db.insertSQL(query);
            db.connectionClose();
            return true;
        } catch (SQLException | IllegalAccessException | InstantiationException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    public boolean Cadastrar(String nome,String email,String login,String senha){
        String query = "INSERT INTO Usuarios (nome,email,login,senha) VALUES ('"+nome+"','"+email+"','"+login+"','"+senha+"');";
        Access db = new Access();
        try {
            db.insertSQL(query);
            db.connectionClose();
        } catch (SQLException | IllegalAccessException | InstantiationException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        System.out.println("Novo user, log:"+login);
        return true;
    }
    public boolean criaFolderCompart(String path,String Users,String dono){
        try {
            /*
            select ID from usuarios where login in ('log','1','2')
            INSERT INTO PastaCompart (Path,Id_Dono) Values(path,dono)
            INSERT INTO Compartilhado Values(pasta,userFor,dono)
            */
            String query = "INSERT INTO PastaCompart (Path,Id_Dono) Values(\""+path+"\",\""+dono+"\")";
            ResultSet rs = null;
            Access db = new Access();
            System.out.println("query: "+query);
            rs = db.insertSQL(query);
            if(rs != null){
                int id_pasta = rs.getInt(1);
                System.out.println("Rs:"+id);
                query = "select ID from usuarios where login in ("+Users+")";
                rs = db.selectSQL(query);
                List<Integer> Ids = new ArrayList<Integer>();
                System.out.println("query:"+query);
                while(rs.next()){
                    Ids.add(rs.getInt(1));
                }
                for(int id : Ids){
                    query = "INSERT INTO Compartilhado Values(\""+id_pasta+"\",\""+id+"\",\""+dono+"\")";
                    System.out.println("query:"+query);
                    db.insertSQL(query);
                }
            }else{
                System.out.println("Rs:Null");
            }
        } catch (SQLException | IllegalAccessException | InstantiationException ex) {
            
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    public List<Arquivo> FolderCompart(){
        List<Arquivo> compArqui = new ArrayList ();
        String query = "select Id_Dono,Path from PastaCompart where Id in (select Id_Pasta from Compartilhado where Id_User="+this.id+");";
        ResultSet rs = null;
        Access db = new Access();
        System.out.println("query: "+query);
        rs = db.selectSQL(query);
        try {
            if(rs != null){
                while(rs.next()){
                    Arquivo arq = new Arquivo();
                    String id_dono = rs.getString("Id_Dono");
                    String path = rs.getString("Path");
                    arq.setPath(path);
                    arq.setPasta(true);
                    int BarIndex = path.lastIndexOf("/");
                    String fileName = path.subSequence(BarIndex+1,path.length()).toString();
                    arq.setNome(fileName);
                    if(arq!=null)
                        compArqui.add(arq);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return compArqui;
    }
    public boolean Conferir(String login,String senha){
        try {
            String ID = "";
            String User_nome="";
            String User_login="";
            String User_email="";
            ResultSet rs = null;
            Access db = new Access();
            String query = "Select * from Usuarios where login='"+login+"' and senha='"+senha+"'";
            System.out.println("query: "+query);
            rs = db.selectSQL(query);
            System.out.println("rs: "+rs);

            if(rs != null){
                while(rs.next()){
                    ID = rs.getString("ID");
                    User_nome = rs.getString("nome");
                    User_login = rs.getString("login");
                    User_email = rs.getString("email");
                }
                this.nome=User_nome;
                this.login=User_login;
                this.email=User_email;
                this.id=ID;
                System.out.println("Criacao_1ID"+this.id);
                System.out.println("Criacao_2ID"+ID);
                db.connectionClose();
                if(User_nome!=""){
                    return true;
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
