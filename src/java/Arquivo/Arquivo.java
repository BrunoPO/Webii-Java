package Arquivo;

public class Arquivo {
    private String Caminho,id,nome,desc,ImgPath,Path;
    private int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    public String getPath() {return Path;}

    public void setPath(String Path) {this.Path = Path;}

    public String getImgPath() {
        return ImgPath;
    }

    public void setImgPath(String ImgPath) {
        this.ImgPath = ImgPath;
    }
    private Boolean Pasta,isNull,isOutdated;

    public Boolean getIsOutdated() {
        return isOutdated;
    }

    public void setIsOutdated(Boolean isOutdated) {
        this.isOutdated = isOutdated;
    }

    public Boolean getIsNull() {
        return isNull;
    }

    public void setIsNull(Boolean isNull) {
        this.isNull = isNull;
    }
    
    public String getCaminho() {
        return Caminho;
    }

    public void setCaminho(String Caminho) {
        this.Caminho = Caminho;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean IsPasta() {
        return Pasta;
    }

    public void setPasta(Boolean Pasta) {
        this.Pasta = Pasta;
    }
    
}
