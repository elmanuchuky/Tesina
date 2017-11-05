angular
    .module("module-data", [])
    .controller("controller-data", function($scope){
        var cData = this;
        cData.criteriaDescription="";
        cData.criteriaDescription_="";
        cData.criteriaOptions="";

        cData.generatedEntityKey;
        cData.entityNamePre="";
        cData.entityNamePos="";
        
        cData.email="";
        cData.password="";

        cData.image_="";
        localStorage.setItem('currentURLimg', 'https://i5.walmartimages.com/asr/f752abb3-1b49-4f99-b68a-7c4d77b45b40_1.39d6c524f6033c7c58bd073db1b99786.jpeg?odnHeight=450&odnWidth=450&odnBg=FFFFFF');
        cData.generatedKey;

        cData.criterias = new Object();
        cData.newEntityDescription = new Object();

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
                    cData.addEntity();
                }).catch(function(error) {
                    alert('Error on the action: Upload failed', error);
                    console.log(error)
                });
            }
            catch(err){
                cData.addEntity();
                alert('La receta se ha subido sin imagenes');
            }
        }

        // Lee los criterios de la DB y los almacena en cData.criterias
        db.ref('criteria/').once('value').then(function(snapshot){
            for (var criteria in snapshot.val()){
                var optionsString = '';
                var optionsList = new Object();
                for (var moption in snapshot.val()[criteria].options){
                    optionsString += snapshot.val()[criteria].options[moption].description + '\n';
                    optionsList[moption] = snapshot.val()[criteria].options[moption].description;
                }
                cData.criterias[criteria] = {criteriaName : criteria, criteriaDescription : snapshot.val()[criteria].description, criteriaOptions : optionsString.replace(/\n$/, ""), criteriaOptionsList : optionsList};
                $scope.$apply();
            }
        })

        // Actualiza segun lo seleccionado del desplegable, al textarea bindeado acorde a la DB
        cData.changeOptions = function(){
            var v  = document.getElementById("cbocriteria");
            cData.criteriaOptions = cData.criterias[cData.criteriaDescription].criteriaOptions;
            cData.criteriaDescription_ = v.options[v.selectedIndex].text;
        }

        // Agrega una opcion de un criterio en particular a la lista de opciones respectiva
        cData.addCriteriaToEntity = function(key){
            var v = document.getElementById(key);
            if (cData.newEntityDescription[key] == undefined)
                cData.newEntityDescription[key] = [v.options[v.selectedIndex].value];
            else
                if (cData.newEntityDescription[key].indexOf(v.options[v.selectedIndex].value) == -1)
                    cData.newEntityDescription[key].push(v.options[v.selectedIndex].value);
            console.log(cData.newEntityDescription);
        }

        // Quita una opcion de un criterio en particular a la lista de opciones respectiva
        cData.removeCriteriaToEntity = function(key){
            var v = document.getElementById(key);
            if (cData.newEntityDescription[key] != undefined){
                if (cData.newEntityDescription[key].indexOf(v.options[v.selectedIndex].value) > -1)
                    cData.newEntityDescription[key].splice(cData.newEntityDescription[key].indexOf(v.options[v.selectedIndex].value), 1);
                if (cData.newEntityDescription[key].length == 0)
                    delete cData.newEntityDescription[key];
            }
        }

        // Agrega una entidad a la DB segun los criterios seleccionados
        cData.addEntity = function(){
            // No hay problemas de que se hagan varias peticiones "al pedo de mas" ya que esta parte solo se usa poco y no es mucha demanda para el servidor
            db.ref('genus/' + cData.entityNamePre.toLowerCase() + '/')
                .update({[cData.entityNamePre.toLowerCase() + '-' + cData.entityNamePos.toLowerCase()] : true});
            db.ref('entities/' + cData.entityNamePre.toLowerCase() + '-' + cData.entityNamePos.toLowerCase() + '/')
                .update({image:localStorage.getItem('currentURLimg'), found : 0, genus : cData.entityNamePre.toLowerCase(), ["scientific-name"] : cData.entityNamePre.toLowerCase() + ' ' + cData.entityNamePos.toLowerCase(), species : cData.entityNamePos.toLowerCase()});
            for (var key in cData.newEntityDescription) {
                if (cData.newEntityDescription.hasOwnProperty(key)) {
                    var element = cData.newEntityDescription[key];
                    element.forEach(function(e) {
                        db.ref('entities/' + cData.entityNamePre.toLowerCase() + '-' + cData.entityNamePos.toLowerCase() + '/' + key + '/')
                            .update({[e] : true})
                        db.ref('criteria/' + key + '/options/' + e + '/species/')
                            .update({[cData.entityNamePre.toLowerCase() + '-' + cData.entityNamePos.toLowerCase()] : true})
                    }, this);
                }
            }
        }
        
        // Agrega una nuevo criterio segun lo ingresado en la vista
        cData.addCriteria = function(){
            alert('cData.criteriaDescription_ ' + cData.criteriaDescription_);
            db.ref('criteria/' + cData.criteriaDescription_).update({description:cData.criteriaDescription_});
            var optionsArray = cData.criteriaOptions.replace('\n\n', '\n').replace(/\n$/, "").split("\n"); // replace(/\n$/, "") consigue hacer el 'trim()' para los '\n'
            for (var i = 0; i < optionsArray.length; i++){
                db.ref('criteria/' + cData.criteriaDescription_ + '/options/' + optionsArray[i]).update({description:optionsArray[i], species:{value:true}});
            }
        }

        // Modifica un criterio segun lo ingresado en la vista
        cData.modifyCriteria = function(){
            db.ref('criteria/' + cData.criteriaDescription).update({description:cData.criteriaDescription});
            var optionsArray = cData.criteriaOptions.split("\n");
            for (var i = 0; i < optionsArray.length; i++){
                db.ref('criteria/' + cData.criteriaDescription + '/options/' + optionsArray[i]).update({description:optionsArray[i], species:{value:true}});
            }
        }

        // Borra el criterio indicado
        cData.deleteCriteria = function(){
            if (confirm("Esta seguro de que desea borrar el criterio '"+ cData.criteriaDescription + " '?"))
                db.ref('criteria').update({[cData.criteriaDescription]:null});
        }

        // Verifica si esta logueado o no
        cData.isSignedIn = function(){
            return firebase.auth().currentUser;
        }

        // Logueo
        cData.signIn = function(){
            firebase.auth().signInWithEmailAndPassword(cData.email, cData.password).catch(function(error) {
                alert(cData.email + "\n" + cData.password + "\n" + error.code + "\n" + error.message)
            }).then(function(){
                if (cData.isSignedIn())
                    document.getElementById("currentUserA").textContent = 'Conectado';
            });
        }

        // Deslogueo
        cData.signOut = function(){
            firebase.auth().signOut();
            if (cData.isSignedIn())
                document.getElementById("currentUserA").textContent = 'Visitante';
        }

        cData.signOut();
    });