package beans;


import entidades.cliente.Cliente;
import User.Client;
import java.io.IOException;
import java.util.Map;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.spi.Bean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpSession;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hibernate.Session;
import util.GenericDAO;

public class RequestBean {
    private HtmlInputText nome;
    private HtmlInputText email;
    private HtmlInputText senha;
    private HtmlInputText login;
    private HtmlInputText confirma;
    private Client cli;

    public String logout(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
        session.invalidate();
        return "logout";
    }
    
    public String cadastroUser(){
        System.out.println("Entrou no cadastro");
        Cliente cliente= new Cliente();
        cliente.setNome(nome.getValue().toString());
        cliente.setEmail(email.getValue().toString());
        cliente.setLogin(login.getValue().toString());
        cliente.setSenha(senha.getValue().toString());
        GenericDAO<Cliente> dao = new GenericDAO(Cliente.class);
        Serializable newId = dao.save(cliente);
        System.out.println("Cliente salvo com sucesso id:"+newId.toString());
        System.out.println("Saiu do cadastro");
        return cadastroGit(newId.toString());
    }
    
    private String getSha(String Branch){
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(new HttpGet("https://api.github.com/repos/BrunoPO/TesteGit/branches/"+Branch));
            JsonObject jo = Json.createReader(response.getEntity().getContent()).readObject();
            jo = jo.getJsonObject("commit");
            return jo.getString("sha");
        } catch (IOException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public String cadastroGit(String id){
        String OAuth = new Tokens().OAuth();
        String shakey="";
        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            shakey=getSha("master");
            httpClient = HttpClientBuilder.create().build();
            JsonObject jo = Json.createObjectBuilder()
                .add("ref","refs/heads/"+id)
                .add("sha",shakey)
                .build();
            
            System.out.println("Testando json+="+jo.toString());
            String url = "https://api.github.com/repos/BrunoPO/Testegit/git/refs?access_token="+OAuth;
            System.out.println(url);
            HttpPost request = new HttpPost(url);
            StringEntity params =new StringEntity(jo.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            System.out.println("Testando params+="+params);
            System.out.println(response.getEntity().toString());
            System.out.println(request.getMethod());
            System.out.println("Fim testando json");
            return "sucesso";
        }catch (Exception ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "falha";
        
    }
    
    public String delete(){
        System.out.println("Entrou no delete");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        LoginBean loginbean = (LoginBean) session.getAttribute("loginBean");
        this.cli = loginbean.cli;
        System.out.println(loginbean);
        
        
        if(cli!=null){
            String id = cli.getId();
            System.out.println(cli.getNome());
            cli.delete();
            System.out.println("Saiu do delete");
            if(deleteGit(id)){
                session.invalidate();
                System.out.println("Out");
                return "logout";
            }else{
                session.invalidate();
                System.out.println("Falha");
                return "falha";
            }
        }else{
            return "falha";
        }
    }
    
    public boolean deleteGit(String id){
        try {
            String OAuth = new Tokens().OAuth();
            System.out.println("Trying Delete id:"+id);
            String url = "https://api.github.com/repos/BrunoPO/TesteGit/git/refs/heads/"+id+"?access_token="+OAuth;
            HttpClient httpClient = HttpClientBuilder.create().build();
            MyDelete request= new MyDelete(url);
            HttpResponse response = httpClient.execute(request);
            System.out.println(url);
            return true;
        } catch (IOException ex) {
            return false;
            //Logger.getLogger(RequestBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public String AtualizaUser(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        LoginBean loginbean = (LoginBean) session.getAttribute("loginBean");
        this.cli = loginbean.cli;
        System.out.println(loginbean);
        String loginC ;String nomeC;String emailC;String senhaC;
        if(login.getSubmittedValue()!=null){
            loginC=login.getSubmittedValue().toString();
        }else{
            loginC=login.getValue().toString();
        }
        if(nome.getSubmittedValue()!=null){
            nomeC=nome.getSubmittedValue().toString();
        }else{
            nomeC=nome.getValue().toString();
        }
        if(email.getSubmittedValue()!=null){
            emailC=email.getSubmittedValue().toString();
        }else{
            emailC=email.getValue().toString();
        }
        if(senha.getSubmittedValue()!=null){
            senhaC=senha.getSubmittedValue().toString();
        }else{
            senhaC=senha.getValue().toString();
        }
        if(cli!=null && cli.confirmaSenha(confirma.getValue().toString())){
            System.out.println(login.getValue());
            System.out.println(cli.getIdInt()+" - "+nomeC+" - "+emailC+" - "+loginC+" - "+senhaC);
            cli.update(nomeC,emailC,loginC,senhaC);
            System.out.println("Cliente atualizado com sucesso id:"+cli.getId());
            System.out.println("Saiu do atualização");
            session.setAttribute("loginBean",loginbean);
            return "sucesso";
        }else{
            return "falha";
        }
    }
    
    public Client getCli() {return cli;}
    public void setCli(Client cli) {this.cli = cli;}
    public HtmlInputText getNome() {
        return nome;
    }

    public void setNome(HtmlInputText nome) {
        this.nome = nome;
    }

    public HtmlInputText getEmail() {
        return email;
    }

    public void setEmail(HtmlInputText email) {
        this.email = email;
    }

    public HtmlInputText getSenha() {
        return senha;
    }

    public void setSenha(HtmlInputText senha) {
        this.senha = senha;
    }

    public HtmlInputText getLogin() {
        return login;
    }

    public void setLogin(HtmlInputText login) {
        this.login = login;
    }
    

    public HtmlInputText getConfirma() {
        return confirma;
    }

    public void setConfirma(HtmlInputText confirma) {
        this.confirma = confirma;
    }
    
}
