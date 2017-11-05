
var config = {
    apiKey: "AIzaSyDd7yEdTJlXa5PLPFW5dvgt1DUqg3vFbIE",
    authDomain: "identificador-de-mosquitos.firebaseapp.com",
    databaseURL: "https://identificador-de-mosquitos.firebaseio.com",
    projectId: "identificador-de-mosquitos",
    storageBucket: "identificador-de-mosquitos.appspot.com",
    messagingSenderId: "89270737076"
};
firebase.initializeApp(config);
var db = firebase.database();
var storageRef = firebase.storage().ref();
