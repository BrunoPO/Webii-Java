function novoArquivo(){
    document.getElementsByClassName("inputFile")[0].click();
}
function novaPasta() {
    var Pasta = prompt("Por favor digite o nome da pasta", "Nova_Pasta");
    if (Pasta != null) {
        Pasta = Pasta.replace(" ","_");
        document.getElementsByClassName("NomePastaAddInput")[0].setAttribute('value',Pasta);
        document.getElementsByClassName("NomePastaAddButton")[0].click();
    }
}
var v;
function clickedTd(x){
    console.log("entrou")
    v=x.target;
    while(!v.classList.contains("clickebleTd")){
        v = v.parentElement;
    }
    radio = v.getElementsByTagName("input")[0];
    if(radio.checked){
        v.children[1].children[1].click();
        v.children[0].checked = false;
        v.classList.remove("active");
    }else{
        var oldcheck=document.getElementsByClassName("active");
        while(oldcheck.length>0){
            oldcheck[0].getElementsByTagName("input")[0].checked = false;
            oldcheck[0].classList.remove("active")
        }
        radio.checked = true;
    }
    if(radio.checked){
        v.classList.add("active")
    }else{
        v.classList.remove("active")
    }
    console.log(v);
}
function delet(){
    var oldcheck=document.getElementsByClassName("active");
    if(oldcheck.length>0){
        var td = oldcheck[0];
        td.getElementsByTagName("input")[0].checked = false;
        td.classList.remove("active")
        td.children[2].children[1].click();
        td.getElementsByTagName("input")[0].checked = false;
        td.classList.remove("active")
    }
}
function compart(){
    var oldcheck=document.getElementsByClassName("active");
    //var Users = prompt("Digite os usuarios seperados por virgula", "'log','1','2'");
    var Users = inputPopUp.value;
    console.log(Users);
    if(oldcheck.length>0 && Users!= null && Users!= ""){
        localStorage.setItem("user", Users );
        var td = oldcheck[0];
        td.getElementsByTagName("input")[0].checked = false;
        td.classList.remove("active")
        td.children[2].children[1].value=Users;
        localStorage.setItem("user", td.children[2].children[1].value );
        //td.children[2].children[1].setAttribute('value',Users);
        //console.log(td.children[2].children[1].value);
        td.children[2].children[2].click();
        td.getElementsByTagName("input")[0].checked = false;
        td.classList.remove("active")
        popUp.hide();background.hide();
    }
}
var DelPressed = false;
var clickbleTds=document.getElementsByClassName("clickebleTd");
for(var i=0;i<clickbleTds.length;i++){
    console.log("ok");
    clickbleTds[i].addEventListener("click", clickedTd)
}
document.onkeypress = function(event) {
    var key = event.keyCode || event.charCode;
    if( key == 46 ){
        DelPressed = true;
        console.log("DelDown");
        delet();
    }
};