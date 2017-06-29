/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

    
import Arquivo.Arquivo;
import actions.cliente.ClienteDAO;
import dbAccess.Access;
import entidades.cliente.Cliente;
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
    private Cliente cli;
    private List<Arquivo> migalha;

    public List<Arquivo> getMigalha() {
        return migalha;
    }

    public void setMigalha(List<Arquivo> migalha) {
        this.migalha = migalha;
    }
    public String getId() {
        return cli.getID().toString();
    }
    public Integer getIdInt() {
        return cli.getID();
    }
    
    public String getLogin() {
        return cli.getLogin();
    }

    public void setLogin(String login) {
        //this.login = login;
    }

    public String getNome() {
        return cli.getNome();
    }

    public void setNome(String nome) {
        //this.nome = nome;
    }

    public String getEmail() {
        return cli.getEmail();
    }

    public void setEmail(String email) {
        //this.email = email;
    }
    public boolean delete(){
        new ClienteDAO().delete(cli);
        //this.cli = new ClienteDAO().updateCliente(nome,email,login,senha);
        System.out.println("Apos retorno");
        System.out.println(cli);
        System.out.println(cli.getNome());
        if(cli==null)
           return true;
        else 
           return false;
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
    /*public boolean Alterar(String nome,String email,String login,String senha){
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
    }*/
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
                System.out.println("Rs:"+getId());
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
        String query = "select Id_Dono,Path from PastaCompart where Id in (select Id_Pasta from Compartilhado where Id_User="+getId()+");";
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
    public boolean confirmaSenha(String tentaSenha){
        System.out.println(tentaSenha+" - "+cli.getSenha());
        return tentaSenha.equals(cli.getSenha());
    }
    public boolean update(String nome,String email,String login,String senha){
        Integer ID=cli.getID();
        if(nome.equals("")) nome=cli.getNome();
        if(email.equals("")) email=cli.getNome();
        if(login.equals("")) login=cli.getLogin();
        if(senha.equals("")) senha=cli.getSenha();
        
        Cliente clienteAlter = new Cliente();
        clienteAlter.setID(ID);clienteAlter.setNome(nome);clienteAlter.setEmail(email);clienteAlter.setLogin(login);clienteAlter.setSenha(senha);
        ClienteDAO clienteDAO = new ClienteDAO();
        clienteDAO.update(clienteAlter);
        //this.cli = new ClienteDAO().updateCliente(nome,email,login,senha);
        System.out.println("Apos retorno");
        System.out.println(cli);
        System.out.println(cli.getNome());
        if(cli==null)
           return false;
        else 
           return true;
    }
    public boolean Conferir(String login,String senha){
        this.cli = new ClienteDAO().validateCliente(login,senha);
        System.out.println("Apos retorno");
        System.out.println(cli);
        System.out.println(cli.getNome());
        if(cli==null)
           return false;
        else 
           return true;
    }
}
