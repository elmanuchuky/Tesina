angular
.module("module-data", [])
.controller("controller-data", function($scope){
    var cData = this;
    cData.image_="";
    localStorage.setItem('currentURLimg', 'https://i5.walmartimages.com/asr/f752abb3-1b49-4f99-b68a-7c4d77b45b40_1.39d6c524f6033c7c58bd073db1b99786.jpeg?odnHeight=450&odnWidth=450&odnBg=FFFFFF');
    cData.generatedKey;

    // Verifica la imagen
    cData.verifyImage = function(){
        if (isPossible){
            cData.uploadImage();
        }else{
            if (confirm("No se ha cargado una imagen o no es adecuada, desea continuar de todas formas?")){
                cData.uploadImage();
            }
        }
    }

    // Sube la imagen y llama a agregar la receta
    cData.uploadImage = function(){
        cData.generatedKey = db.ref('db/').child('imagesKeys').push().key;
        try{
            storageRef.child('entities/images/' + cData.generatedKey + '/' + file.name).put(file, metadata).then(function(snapshot) {
                localStorage.setItem('currentURLimg', snapshot.downloadURL);
                cData.addRecipe();
            }).catch(function(error) {
                alert('Error on the action: Upload failed', error);
                console.log(error)
            });
        }
        catch(err){
            cData.addRecipe();
            alert('La receta se ha subido sin imagenes');
        }
    }

    // Agrega una nueva receta segun lo ingresado en la vista
    cData.addRecipe = function(){
        var newRecipe = {
            date:firebase.database.ServerValue.TIMESTAMP,
            image:localStorage.getItem('currentURLimg'),
        };
        var newRecipeKey = cData.generatedKey;
        db.ref('db/recipes/' + newRecipeKey).update(newRecipe);

        localStorage.setItem('currentURLimg', 'https://i5.walmartimages.com/asr/f752abb3-1b49-4f99-b68a-7c4d77b45b40_1.39d6c524f6033c7c58bd073db1b99786.jpeg?odnHeight=450&odnWidth=450&odnBg=FFFFFF');
        return true
    }

    // Borra la receta indicada en la base de datos y tambien elimina la imagen correspondiente
    cData.deleteRecipe = function(){
        db.ref('db/recipes/' + cData.recipeId).once('value').then(function(snapshot){
            cData.currentRecipe = snapshot.val();
            cData.currentTimeType = Object.keys(cData.currentRecipe.timeType)[0]
            cData.currentRegion = Object.keys(cData.currentRecipe.region)[0]
            cData.currentDifficulty = Object.keys(cData.currentRecipe.difficulty)[0]
            cData.currentCategory = Object.keys(cData.currentRecipe.category)[0]
            db.ref('db/timeTypes/' + cData.currentTimeType + '/recipes').update({[cData.recipeId]:null})
            db.ref('db/regions/' + cData.currentRegion + '/recipes').update({[cData.recipeId]:null})
            db.ref('db/difficulties/' + cData.currentDifficulty + '/recipes').update({[cData.recipeId]:null})
            db.ref('db/categories/' + cData.currentCategory + '/recipes').update({[cData.recipeId]:null})
            db.ref('db/recipes').update({[cData.recipeId]:null});
        })
    }
});