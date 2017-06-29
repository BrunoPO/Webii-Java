package beans;

import javax.servlet.http.*;
import Arquivo.Arquivo;
import User.Client;
import actions.cliente.ClienteDAO;
import entidades.cliente.Cliente;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.concurrent.Cancellable;
import javax.faces.component.UIInput;
/**
 *
 * @author prog
 */
@ManagedBean(name="loginBean")
@SessionScoped
public class LoginBean {
    protected String ShaSubstitui;
    private List<Arquivo> migalha;
    private List<Arquivo> arquis;
    private String login,senha,Branch,nomePastaAdd;
    private String mensagem;
    private Part file;
    private String OAuth;
    private Boolean wasUpdate;
    private int numfile,numfolder;
    
    @ManagedProperty(value="#{cli}")
    protected Client cli;
    
    private UIInput stringField;

    public UIInput getStringField() {return stringField;}
    public void setStringField(UIInput stringField) {this.stringField = stringField;}
    
    protected List<String> extensoes; 
    public String back(){
        if(migalha.size()>=2){
            migalha.remove(migalha.size()-1);
        }
        return atualizar(migalha.get(migalha.size()-1));
    }                
    public String compart(Arquivo item){
        System.out.println("-------- Open Compart");
        System.out.println(item.toString());
        item.getNome();
        
        List<String> args = divideMigalha();
        String ref = args.get(0);
        String Base = args.get(1);
        String path = args.get(2);
        
        System.out.println("Teste Nome Path:"+path);
        path+=item.getNome();
        path = ref+"/"+path;
        System.out.println("PathCompart:"+path);
        
        
        
        System.out.println("---------Open Bd");
        System.out.println("Teste:"+stringField.getSubmittedValue().toString());
        /*select ID from usuarios where login in ('log','1','2')
        INSERT INTO PastaCompart (Path,Id_Dono) Values(path,dono) 
        INSERT INTO Compartilhado Values(pasta,userFor,dono)
        */
        //String Users = "'log','1','2'";
        //System.out.println("Users"+Users);
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
    public String processar() {
        System.out.print("open func processar");
        if(cli != null ) {
            System.out.println("Logado");
            return "sucesso";
        }else{
            cli = new Client();
            System.out.println("Login: "+login);
            System.out.println("Senha: "+senha);
            if(cli.Conferir(login, senha)){
                setMensagem("");
                
                extensoes= new ArrayList<String>();
                extensoes.add("pdf");extensoes.add("doc");extensoes.add("ppt");extensoes.add("xls");extensoes.add("txt");extensoes.add("html");
                Branch = cli.getId();
                wasUpdate = false;
                OAuth = new Tokens().OAuth();
                migalha = new ArrayList<Arquivo>();
                atualizar(null);
                return "sucesso";
            }else{
                setMensagem("email ou senha incopativel");
                return "falha";
            } 
        }
    }
    
    
    public String interacao(Arquivo item) {
        if(item.IsPasta()){
            if(migalha.indexOf(item)==0 && item.getPath() == null)
                return atualizarFromBd();
            else
                return atualizar(item);
        }else
            return download(item);
    }
    //args 0-ref 1-Base 2-path
    public List<String> divideMigalha(){
        List<String> args = new ArrayList();
        args.add("");args.add("");args.add("");
        int c = 0;
        String path="";
        if(migalha.get(0).getPath() == null)
            c++;
        String ref = migalha.get(0+c).getPath();
        String Base ="";
        System.out.println("divideMigalha-Ref:"+ref);
        if(c==1){
            int barIndex = ref.indexOf("/");
            if(barIndex > -1 ){
                Base = ref.subSequence(barIndex,ref.length()).toString();
                ref = ref.subSequence(0,barIndex).toString();
            }
        }else{
            ref = Branch;
        }
        System.out.println("Resultado-divideMigalha:"+ref+" - "+Base);
        for (int i=1+c;i<migalha.size();i++){
            path+=migalha.get(i).getPath()+"/";
        }
        if(Base.equals("/"))
           Base ="";
        args.set(0, ref);args.set(1, Base);args.set(2, path);
        return args;
    }
    public String download(Arquivo item){
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        List<String> args = divideMigalha();
        String ref = args.get(0);
        String Base = args.get(1);
        String path = args.get(2);
        
        path += item.getNome();
        String fileURL="https://raw.githubusercontent.com/BrunoPO/TesteGit/"+ref+"/"+path;
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new URL(fileURL).openStream());
            final byte data[] = new byte[1024];
            int count;
            response.setContentType("application/octet-stream");
            response.setContentLength((int) in.available());
            response.setHeader( "Content-Disposition",
                     String.format("attachment; filename=\"%s\"", item.getNome()));

            OutputStream out = response.getOutputStream();
            while ((count = in.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "sucesso";
    }
    
    public String upload() {
        Boolean Substitui = false;
        System.out.println("Open Teste Upload--------");
        String nomeOriginal = file.getSubmittedFileName();
        int dotIndex = nomeOriginal.lastIndexOf(".");
        String fileContent = "";
        String Ext = nomeOriginal.subSequence(dotIndex,nomeOriginal.length()).toString();
        System.out.println(nomeOriginal);
        nomeOriginal = nomeOriginal.subSequence(0,dotIndex).toString();
        System.out.println(nomeOriginal);
        Boolean b = false;
        String nome=nomeOriginal;
        int i=0;
        ShaSubstitui = "";
        if(Substitui){
            b=true;
              for(Arquivo arq : arquis){
                if(b && arq.getNome().equals(nome+Ext)){
                    System.out.println("Achou"+arq.getNome()+" - "+arq.getId());
                    ShaSubstitui = arq.getId();
                    b = false;
                }
              } 
        }else{
            while(!b){
              b=true;
              for(Arquivo arq : arquis){
                if(b && arq.getNome().equals(nome+Ext)){
                    b = false;
                    break;
                }
              }  
              if(i==0)
                  i++;
              else
                nome=nomeOriginal+"_"+(i++);
            }
        }
        
        nome += Ext;
        System.out.println(nome);
        System.out.println(ShaSubstitui);
        uploadNovo(nome);
        wasUpdate=true;
        System.out.println("Exit Teste Upload--------");
        System.out.println("Open Atualizado--------");
        String resp=atualizar(migalha.get(migalha.size()-1));
        System.out.println("Exit Atualizado--------");
        System.out.println(resp);
        resp=((resp=="sucesso")?"recarregar":resp);
        System.out.println(resp);
        return resp;
        
    }
    public String deleta(Arquivo item){
        try {
            System.out.println("Open Teste Deletar--------");
            List<String> args = divideMigalha();
            String ref = args.get(0);
            String Base = args.get(1);
            String path = args.get(2);
            
            String shakey =item.getId();
            
            System.out.println("Teste Nome Path:"+path);
            path+=item.getNome();
            path = Base+"/"+path;
            HttpClient httpClient = HttpClientBuilder.create().build();
            System.out.println("----Open Sha Delete---");
            System.out.println(shakey);
            System.out.println("----Exit Sha Delete---");
            JsonObject j = Json.createObjectBuilder()
                    .add("message", "Add")
                    .add("sha", shakey)
                    .add("branch", ref)
                    .add("committer",
                            Json.createObjectBuilder()
                                    .add("name", "BrunoPO")
                                    .add("email", "Bruno@Teste.io")
                    )
                    .build();
            String url = "https://api.github.com/repos/BrunoPO/TesteGit/contents"+path+"?access_token="+OAuth;
            System.out.println("Url>:"+url);
            MyDelete request= new MyDelete(url);
            StringEntity params= new StringEntity(j.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            wasUpdate=true;
            System.out.println("Exit Teste Deletar--------");
            return atualizar(migalha.get(migalha.size()-1));
        } catch (IOException ex) {
            return "falha";
        } 
    }
    public String novaPasta() {
        if(getNomePastaAdd()==null)
            return "falha";
        
        List<String> args = divideMigalha();
        String ref = args.get(0);
        String Base = args.get(1);
        String path = args.get(2);
        
        try {
            System.out.println("Open Teste novaPasta--------");
            HttpClient httpClient = HttpClientBuilder.create().build();
            JsonObject j = Json.createObjectBuilder()
                .add("message", "Add")
                .add("content", "anNvbi1jb21wbGV0by8KanNvbi1zZXRlLwpqc29uLwo=")
                .add("branch", ref)
                .add("committer",
                        Json.createObjectBuilder()
                                .add("name", "BrunoPO")
                                .add("email", "Bruno@Teste.io")
                )
                .build();
            System.out.println(path+getNomePastaAdd());
            HttpPut request= new HttpPut("https://api.github.com/repos/BrunoPO/TesteGit/contents/"+path+getNomePastaAdd()+"/Novo.txt?access_token="+OAuth);
            StringEntity params = new StringEntity(j.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            System.out.println("Exit Teste novaPasta--------");
            return atualizar(null);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
            return "falha";
        } catch (IOException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
            return "falha";
        }
    }
    public String uploadNovo(String name){
        try {
            System.out.println("Open Teste UploadNovo--------");
            String fileContent = new Scanner(file.getInputStream())
                .useDelimiter("\\A").next();
            fileContent = Base64.getEncoder().encodeToString(fileContent.getBytes());
            
            List<String> args = divideMigalha();
            String ref = args.get(0);
            String Base = args.get(1);
            String path = args.get(2);
            
            System.out.println(path);
            path = Base+"/"+path;
            path+=name;
            
            System.out.println("Teste Nome Path:"+path);
            String shakey ="";
            HttpClient httpClient = HttpClientBuilder.create().build();
            if(ShaSubstitui=="")
                shakey=getSha(Branch);
            else
                shakey=ShaSubstitui;
            System.out.println("----Open Sha Upload---");
            System.out.println(ShaSubstitui);
            System.out.println(shakey);
            System.out.println(fileContent);
            System.out.println("----Exit Sha Upload---");
            JsonObject j = Json.createObjectBuilder()
                .add("message", "Add")
                .add("content", fileContent)
                .add("sha", shakey)
                .add("branch", ref)
                .add("committer",
                        Json.createObjectBuilder()
                                .add("name", "BrunoPO")
                                .add("email", "Bruno@Teste.io")
                )
                .build();
            String url ="https://api.github.com/repos/BrunoPO/TesteGit/contents"+path+"?access_token="+OAuth;
            System.out.println(url);
            HttpPut request= new HttpPut(url);
            StringEntity params= new StringEntity(j.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            System.out.println("Exit Teste UploadNovo--------");
            return "sucesso";
        } catch (IOException ex) {
            return "falha";
        } 
        
    }
    public String atualizarFromBd(){
        Arquivo a = new Arquivo();
        a.setNome("Base");
        a.setPath(null);
        a.setPasta(true);
        migalha = new ArrayList<Arquivo>();
        migalha.add(a);
        arquis = cli.FolderCompart();
        return "sucesso";
    }
    public String atualizar(Arquivo item){
        numfile=0;numfolder=0;
        System.out.print("open func atualizar");
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response;
        JsonArray c;
        arquis = new ArrayList<Arquivo>();
        try {
            if(item==null){
                System.out.println("Item é null");
                httpClient = HttpClientBuilder.create().build();
                Arquivo a = new Arquivo();
                a.setNome("Base");
                a.setPath(Branch+"/");
                a.setPasta(true);
                migalha = new ArrayList<Arquivo>();
                migalha.add(a);
            }else{
                System.out.println("Base:Dir");
                int index = migalha.indexOf(item);
                if(index>-1){
                    while(migalha.size() > index+1){
                        migalha.remove(index+1);
                    }
                }else{
                    migalha.add(item);
                } 
            }
            List<String> args = divideMigalha();
            String ref = args.get(0);
            String Base = args.get(1);
            String path = args.get(2);
            
            System.out.println("Resultado:"+ref+" - "+Base);
            System.out.println(path);
            path = Base + "/" + path;
            if(!(path.equals("/") || path.equals("")))
                path = path.subSequence(0,path.length()-1).toString();
            
            System.out.println("Path:"+path);
            
            httpClient = HttpClientBuilder.create().build();
            String url = "https://api.github.com/repos/BrunoPO/TesteGit/contents"+path+"?ref="+ref;
            System.out.println(url);
            response = httpClient.execute(new HttpGet(url));
            c = Json.createReader(response.getEntity().getContent()).readArray();
            int i =0;
            for(JsonValue b : c ){
                Arquivo a = new Arquivo();
                String nome = ((JsonObject)b).getString("name");
                if(((JsonObject)b).getString("type").equals("dir")){
                   a.setPasta(true);
                   numfolder++;
                   a.setPath(nome);
                }else{
                    a.setPasta(false);
                    numfile++;
                    int dotIndex = nome.lastIndexOf(".");
                    String ext = nome.subSequence(dotIndex+1,nome.length()).toString();
                    boolean achouExt = false;
                    for(String exts:extensoes){
                        if(ext.equals(exts)){
                            achouExt = true;
                            break;
                        }
                    }
                    if(achouExt)
                        a.setImgPath(ext+".svg");
                    else{
                        a.setImgPath("default.svg");
                    }
                }
                a.setNome(nome);
                a.setId(((JsonObject)b).getString("sha")) ;
                if(a != null)
                    arquis.add(a);
            }
            System.out.println(numfile);
            System.out.println(numfolder);
            System.out.print(arquis);
            System.out.print("exit func atualizar Deu certo");
            return "sucesso";
        } catch (IOException ex) {
            System.out.print("exit func atualizar falhou");
            return "failure";
        }
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
    
    public Client getCli() {
        return cli;
    }

    public void setCli(Client cli) {
        this.cli = cli;
    }
    
    public int getNumfile() {
        return numfile;
    }

    public void setNumfile(int numfile) {
        this.numfile = numfile;
    }

    public int getNumfolder() {
        return numfolder;
    }

    public void setNumfolder(int numfolder) {
        this.numfolder = numfolder;
    }
    
    public String getNomePastaAdd() {return nomePastaAdd;}
    public void setNomePastaAdd(String nomePastaAdd) {this.nomePastaAdd = nomePastaAdd;}
    public Part getFile() {return file;}
    public void setFile(Part file) {this.file = file;}
    public List<Arquivo> getArquis() {return arquis;}
    public void setArquis(List<Arquivo> arquis) {this.arquis = arquis;}
    public List<Arquivo> getMigalha() {return migalha;}
    public void setMigalha(List<Arquivo> migalha) {this.migalha = migalha;}
    public String getLogin() {return login;}
    public void setLogin(String login) {this.login = login;}
    public String getSenha() {return senha;}
    public void setSenha(String senha) {this.senha = senha;}
    public String getMensagem() {return mensagem;}
    public void setMensagem(String mensagem) {this.mensagem = mensagem;}
}
class MyDelete extends HttpPost
    {
        public MyDelete(String url){
            super(url);
        }
        @Override
        public String getMethod() {
            return "DELETE";
        }
    }