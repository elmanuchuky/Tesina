var innerHTML1 = function(){
    document.getElementById("loginnav").innerHTML = '<a href="index.html">Inicio</a><a href="#" data-toggle="modal" data-target="#miModalLogin">Iniciar sesion</a><a href="#" data-toggle="modal" data-target="#miModalSignup">Registrarse</a>'
}

var innerHTML2 = function(){
    firebase.auth().onAuthStateChanged(function(user) {
        if (user != null)
            document.getElementById("loginnav").innerHTML = '<p style="padding:15px;">' + user.email + '</p><a href="index.html">Inicio</a><a href="newRecipe.html">Nueva receta</a><a href="#" onclick="desconectar()">Desconectarse</a><br>'
    })
}

var desconectar = function(){
    firebase.auth().onAuthStateChanged(function(user) {
    if(user != null){
        firebase.auth().signOut().then(function() {
            alert("Se ha desconectado");
            innerHTML1();
            }, function(error) {
            alert("Ha ocurrido un error")
        })}
    });
}

document.getElementsByTagName("body")[0].onload = function(){
    firebase.auth().onAuthStateChanged(function(user){
        if (user == null){
            innerHTML1();
        } else{
            innerHTML2();
        }
    })
}

var iniciarGoogle = function(){
    var provider = new firebase.auth.GoogleAuthProvider();
    provider.addScope('https://www.googleapis.com/auth/plus.login');
    firebase.auth().signInWithRedirect(provider);
    //$('#miModalSignup').modal('hide')
}

var iniciarFacebook = function(){
    var provider = new firebase.auth.FacebookAuthProvider();
    //provider.addScope('user_birthday');
    firebase.auth().signInWithRedirect(provider);
}

var iniciarSesion = function(){
    if (document.getElementById("getuserl").value != "" && document.getElementById("getpasswordl").value != ""){
        firebase.auth().signInWithEmailAndPassword(document.getElementById("getuserl").value, document.getElementById("getpasswordl").value).then(function(v){
            alert ("Sesion de " + v.email + " iniciada")
            // llenar la DB (hacer metodo separado)
            innerHTML2();
            return;
        }, function(r){
            if(r.code == "auth/user-disabled"){
                alert("El usuario esta deshabilitado");
                return;
            } else if (r.code == "auth/invalid-email"){
                alert("El email ingresado no es valido");
                return;
            } else if (r.code== "auth/operation-not-allowed"){
                alert("La cuenta por el momento no es valida");
                return;
            } else if(r.code== "auth/wrong-password"){
                alert("Contraseña equivocada porfavor verifique");
                return;
            } else{
                alert("El usuario indicado no se encuentra registrado");
                return;
            }
        })
    } else if (document.getElementById("getuserl").value == ""){
        alert("Indique el usuario!");
        document.getElementById("getuserl").focus();
    } else if (document.getElementById("getpasswordl").value == ""){
        alert("Escriba la contraseña!");
        document.getElementById("getpasswordl").focus();
    }
}

var registrar = function(){
    if (document.getElementById("getuserr").value != "" && document.getElementById("getpasswordr").value != ""){
        firebase.auth().createUserWithEmailAndPassword(document.getElementById("getuserr").value, document.getElementById("getpasswordr").value).then(function(v){
            alert ("Usuario registrado con exito")
            $('#miModalSignup').modal('hide')
            // llenar la DB (hacer metodo separado)
            innerHTML2();
            return;
        }, function(r){
            if(r.code == "auth/email-already-in-use"){
                alert("Este email ya esta en uso");
                return;
            } else if (r.code == "auth/invalid-email"){
                alert("El email ingresado no es valido");
                return;
            } else if (r.code == "auth/operation-not-allowed"){
                alert("El usuario/contraseña no coincide porfavor verifique");
                return;
            } else if(r.code= "auth/weak-password"){
                alert("La contraseña debe poseer al menos seis caracteres");
                return;
            }
        })
    } else if (document.getElementById("getuserr").value == ""){
        alert("Indique el usuario!");
        document.getElementById("getuserr").focus();
    } else if (document.getElementById("getpasswordr").value == ""){
        alert("Escriba la contraseña!");
        document.getElementById("getpasswordr").focus();
    }
}