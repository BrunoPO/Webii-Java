package beans;

import javax.servlet.http.*;
import Arquivo.Arquivo;
import User.Client;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.entity.StringEntity;
import java.io.*;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.servlet.http.HttpServletResponse;
import javax.mail.MessagingException;
import org.apache.http.concurrent.Cancellable;
import javax.faces.component.UIInput;
/**
 *
 * @author prog
 */

public class LoginBeanSession {
    protected String ShaSubstitui;
    private List<Arquivo> migalha;
    private List<Arquivo> arquis;
    private String login,senha,Branch,nomePastaAdd;

    public String getNomePastaAdd() {
        return nomePastaAdd;
    }

    public void setNomePastaAdd(String nomePastaAdd) {
        this.nomePastaAdd = nomePastaAdd;
    }
    private String mensagem;
    private Part file;
    private String OAuth;
    private Boolean wasUpdate;
    protected Client cli;
    private UIInput usernameField;
    public boolean getCli(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
        cli =(Client) session.getAttribute("User");
        if(cli != null)
            migalha = cli.getMigalha();
        else
            return false;
        return true;
    }
    public boolean updateCli(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
        if(migalha != null)
           cli.setMigalha(migalha); 
        session.setAttribute("User", cli);
        return true;
    }
    public String compart(){
        Arquivo item = new Arquivo();
        System.out.println();
        getCli();
        System.out.println("-------- Open Compart");
        System.out.println(item.toString());
        item.getNome();
        String path="";
        int c = 0;
        
        if(migalha.get(0).getPath() == null)
            c++;
        String ref = migalha.get(0+c).getPath();
        int barIndex = ref.indexOf("/");
        String Base = ref.subSequence(barIndex,ref.length()).toString();
        ref = ref.subSequence(0,barIndex).toString();
        System.out.println("Resultado:"+ref+" - "+Base);
        for (int i=1+c;i<migalha.size();i++){
            path+=migalha.get(i).getPath()+"/";
        }
        
        if(Base.equals("/"))
           Base ="";
        System.out.println("Teste Nome Path:"+path);
        path+=item.getNome();
        path = ref+"/"+path;
        System.out.println("PathCompart:"+path);
        
        
        
        System.out.println("---------Open Bd");
        /*
        select ID from usuarios where login in ('log','1','2')
        INSERT INTO PastaCompart (Path,Id_Dono) Values(path,dono) 
        INSERT INTO Compartilhado Values(pasta,userFor,dono)
        */
        //String Users = "'log','1','2'";
        System.out.println(getNomePastaAdd());
        if(getNomePastaAdd() != null)
            cli.criaFolderCompart(path,getNomePastaAdd(),ref);
        else{
            System.out.println(getNomePastaAdd());
            System.out.println(nomePastaAdd);
        }
        System.out.println("---------Exit Bd");
        System.out.println("-------- Exit Compart");
        return "sucesso";
    }
    
    
}