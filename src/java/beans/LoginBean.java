/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;




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

/**
 *
 * @author prog
 */
public class LoginBean {
    protected String ShaSubstitui;
    private List<Arquivo> migalha;
    private List<Arquivo> arquis;
    private String login,senha,Branch,nomePastaAdd;
    private String mensagem;
    private Part file;
    private String OAuth;
    private Boolean wasUpdate;
    protected List<String> extensoes; 
    public String back(){
        if(migalha.size()>=2){
            migalha.remove(migalha.size()-1);
        }
        return atualizar(migalha.get(migalha.size()-1));
    }
    public String processar() {
        System.out.print("open func processar");
        if (login.equals(senha)) {
            extensoes= new ArrayList<String>();
            extensoes.add("pdf");extensoes.add("doc");extensoes.add("ppt");extensoes.add("xls");extensoes.add("txt");extensoes.add("html");
            Branch = login;
            wasUpdate = false;
            OAuth = new Tokens().OAuth();
            migalha = new ArrayList<Arquivo>();
            atualizar(null);
            return "sucesso";
        } else {
            mensagem = "Login ou senha inválida";
            return "fracasso";
        }
    }
    public String cadastro(){
        if (login.equals(senha)) {
            OAuth = new Tokens().OAuth();
            
            String shakey="";
            HttpClient httpClient = HttpClientBuilder.create().build();

            try {
                shakey=getSha(Branch);
                httpClient = HttpClientBuilder.create().build();
                JsonObject jo = Json.createObjectBuilder()
                        .add("ref","refs/heads/"+login)
                        .add("sha",shakey)
                .build();
                System.out.println("Testando json+="+jo.toString());
                HttpPost request = new HttpPost("https://api.github.com/repos/BrunoPO/Testegit/git/refs?access_token="+OAuth);
                StringEntity params =new StringEntity(jo.toString());
                request.addHeader("content-type", "application/json");
                request.setEntity(params);
                HttpResponse response = httpClient.execute(request);
                System.out.println("Testando params+="+params);
                System.out.println(response.getEntity().toString());
                System.out.println(request.getMethod());
                System.out.println("Fim testando json");

            }catch (Exception ex) {
                Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return "sucesso";
    }
    public String interacao(Arquivo item) {
        if(item.IsPasta()){
            return atualizar(item);
        }else
            return download(item);
    }
    public String download(Arquivo item){
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        String path = "";
                for(int i = 1;i<migalha.size();i++){
                    path += migalha.get(i).getNome()+"/";
                }
        path += item.getNome();
        String fileURL="https://raw.githubusercontent.com/BrunoPO/TesteGit/"+Branch+"/"+path;
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
    
    public String upload() throws MessagingException {
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
        String resp=atualizar(null);
        System.out.println("Exit Atualizado--------");
        System.out.println(resp);
        resp=((resp=="sucesso")?"recarregar":resp);
        System.out.println(resp);
        return resp;
        
    }
    public String deleta(Arquivo item){
        try {
            System.out.println("Open Teste Deletar--------");
            String path = "";
            for(int i = 1;i<migalha.size();i++){
                path += migalha.get(i).getNome()+"/";
            }
            path += item.getNome();
            System.out.println("Teste Nome Path:"+path);
            String shakey =item.getId();
            HttpClient httpClient = HttpClientBuilder.create().build();
            System.out.println("----Open Sha Delete---");
            System.out.println(shakey);
            System.out.println("----Exit Sha Delete---");
            JsonObject j = Json.createObjectBuilder()
                    .add("path", path)
                    .add("message", "Add")
                    .add("sha", shakey)
                    .add("branch", Branch)
                    .add("committer",
                            Json.createObjectBuilder()
                                    .add("name", "BrunoPO")
                                    .add("email", "Bruno@Teste.io")
                    )
                    .build();
            MyDelete request= new MyDelete("https://api.github.com/repos/BrunoPO/TesteGit/contents/"+path+"?access_token="+OAuth);
            StringEntity params= new StringEntity(j.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            wasUpdate=true;
            System.out.println("Exit Teste Deletar--------");
            return atualizar(null);
        } catch (IOException ex) {
            System.out.println("IOException Messagem == "+ex.getMessage().contains("201")+" - "+ex.getMessage());
            //if( ex.getMessage().contains("404") || ex.getMessage().contains("402"))
                return "falha";
        } 
    }
    public String novaPasta() {
        if(getNomePastaAdd()==null){
            return "falha";
        }
        String path = "";
        for(int i = 1;i<migalha.size();i++){
            path += migalha.get(i).getNome()+"/";
        }
        try {
            System.out.println("Open Teste novaPasta--------");
            HttpClient httpClient = HttpClientBuilder.create().build();
            JsonObject j = Json.createObjectBuilder()
                .add("message", "Add")
                .add("content", "anNvbi1jb21wbGV0by8KanNvbi1zZXRlLwpqc29uLwo=")
                .add("branch", Branch)
                .add("committer",
                        Json.createObjectBuilder()
                                .add("name", "BrunoPO")
                                .add("email", "Bruno@Teste.io")
                )
                .build();
            HttpPut request= new HttpPut("https://api.github.com/repos/BrunoPO/TesteGit/contents/"+path+getNomePastaAdd()+"/.gitignore?access_token="+OAuth);
            StringEntity params = new StringEntity(j.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            wasUpdate=true;
            System.out.println("Exit Teste novaPasta--------");
            //atualizar(null);
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
            String path = "";
            for(int i = 1;i<migalha.size();i++){
                path += migalha.get(i).getNome()+"/";
            }
            path += name;
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
                    .add("branch", Branch)
                    .add("committer",
                            Json.createObjectBuilder()
                                    .add("name", "BrunoPO")
                                    .add("email", "Bruno@Teste.io")
                    )
                    .build();
            HttpPut request= new HttpPut("https://api.github.com/repos/BrunoPO/TesteGit/contents/"+path+"?access_token="+OAuth);
            StringEntity params= new StringEntity(j.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            System.out.println("Exit Teste UploadNovo--------");
            return "sucesso";
        } catch (IOException ex) {
            System.out.println("IOException Messagem == "+ex.getMessage().contains("201")+" - "+ex.getMessage());
            //if( ex.getMessage().contains("404") || ex.getMessage().contains("402"))
                return "falha";
        } 
        
    }
    
    public String atualizar(Arquivo item){
        System.out.print("open func atualizar");
        arquis = new ArrayList<Arquivo>();
        String shakey ="";  
        JsonArray c;
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response;
        JsonObject jo;
        try {
            if(item != null && item.IsPasta()){
                System.out.println("Pasta não é null");
                System.out.println(item.getNome());
                shakey = item.getId();
                int index = migalha.indexOf(item);
                if(index>-1){
                        while(migalha.size() > index+1){
                            migalha.remove(index+1);
                        }
                 }else{
                     migalha.add(item);
                 }
            }else{
                System.out.println("Passou was:"+wasUpdate);    
                if(wasUpdate){
                    System.out.println("Open Desatualizado");
                    shakey = getSha(Branch);
                    migalha.get(0).setId(shakey);
                    httpClient = HttpClientBuilder.create().build();
                    response = httpClient.execute(new HttpGet("https://api.github.com/repos/BrunoPO/TesteGit/git/trees/"+shakey));
                    jo = Json.createReader(response.getEntity().getContent()).readObject();
                    System.out.println(jo);
                    JsonArray j = jo.getJsonArray("tree");
                    //Contents co = repo.contents();
                    System.out.println(j);
                    for(int i=1;i<migalha.size();i++){
                      System.out.println("Teste se igual");
                        System.out.println(migalha.get(i).getId());
                      for(JsonValue b : j ){
                          if(((JsonObject)b).getString("type").equals("tree") && ((JsonObject)b).getString("path").equals(migalha.get(i).getNome())){
                              migalha.get(i).setId(((JsonObject)b).getString("sha"));
                              System.out.println(migalha.get(i).getId());
                              break;
                          }
                      }
                      System.out.println("Fim Teste se igual");
                    }
                    shakey=migalha.get(migalha.size()-1).getId();
                    System.out.println("Exit Desatualizado");
                }else{ 
                    System.out.println("Item é null");
                    httpClient = HttpClientBuilder.create().build();
                    shakey = shakey = getSha(Branch);
                    Arquivo a = new Arquivo();
                    a.setNome("Base");
                    a.setId(shakey);
                    a.setPasta(true);
                    migalha = new ArrayList<Arquivo>();
                    migalha.add(a);
                }
            }
            httpClient = HttpClientBuilder.create().build();
            response = httpClient.execute(new HttpGet("https://api.github.com/repos/BrunoPO/TesteGit/git/trees/"+shakey));
            jo = Json.createReader(response.getEntity().getContent()).readObject();
            System.out.println(jo);
            c = jo.getJsonArray("tree");
            System.out.print(c);
            int i =0;
            for(JsonValue b : c ){
                Arquivo a = new Arquivo();
                String nome = ((JsonObject)b).getString("path");
                if(((JsonObject)b).getString("type").equals("tree")){
                   a.setPasta(true);
                }else{
                    a.setPasta(false);
                    int dotIndex = nome.lastIndexOf(".");
                    String ext = nome.subSequence(dotIndex+1,nome.length()).toString();
                    boolean achouExt = false;
                    for(String exts:extensoes){
                        if(ext.equals(exts)){
                            System.out.println("Iguais"+exts+ext);
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
                a.setId( ((JsonObject)b).getString("sha")) ;
                if(a != null)
                    arquis.add(a);
            }
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