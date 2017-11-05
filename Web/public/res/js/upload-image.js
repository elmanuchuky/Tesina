var file;
var metadata;
var isPossible;

// Sube un fichero al stg de FB
function handleFileSelect(evt){
    try{
        isPossible = true;
        file = evt.target.files[0];
        if (file.size > (1024 * 1024 * 1.1)){
            alert("El archivo es demasiado pesado. Max 1.1Mb");
            isPossible = false;
        }
        img = new Image();
        img.src = window.URL.createObjectURL(file);
        if (!(/\.(jpeg|jpg)$/i).test(file.name)){
            isPossible = false;
            alert("La extension no es valida");
        }
        metadata = {
            'contentType': file.type
        }
    }
    catch(err){
        isPossible = false;
        console.log(err);
        alert(err);
    }
}

document.getElementById('file').addEventListener('change', handleFileSelect, false);