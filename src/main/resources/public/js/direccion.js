function pedirMunicipios(id, id_select) {
    var path = document.getElementById(id).value;
    var id = document.getElementById(id_select).value;

    $.ajax({
        type: "POST",
        url: path+"/"+id,
        success: function(result){
            location.reload(true);
        }
    });
}

function pedirLocalidades(id, id_padre, id_hijo) {
    var path = document.getElementById(id).value;
    var id_padre = document.getElementById(id_padre).value;
    var id_hijo = document.getElementById(id_hijo).value;

    $.ajax({
        type: "POST",
        url: path+"/"+id_padre+"/"+id_hijo,
        success: function(result){
            location.reload(true);
        }
    });
}

function obtenerSectores(idPath, id) {
    var path = document.getElementById(idPath).value;
    var id_organizacion = document.getElementById(id).value;

    //alert(path+"/"+ id_organizacion);

    $.ajax({
        type: "POST",
        url: path+"/"+ id_organizacion,
        success: function(result){
            location.reload(true);
        }
    });
}


function transporteSeleccionado(idPath, id) {
    var path = document.getElementById(idPath).value;
    var id = document.getElementById(id).value;

    $.ajax({
        type: "POST",
        url: path+"/"+ id,
        success: function(result){
            location.reload(true);
        }
    });
}

function pedir(id, id_select, prefijo) {
    var path = document.getElementById(id).value;
    var id = document.getElementById(id_select).value;

    $.ajax({
        type: "POST",
        url: path+prefijo+"/"+id,
        success: function(result){
            location.reload(true);
        }
    });
}

function enviarCalle(id, id_calle, id_numero) {
    var path = document.getElementById(id).value;
    var calle = document.getElementById(id_calle).value;
    var numero = document.getElementById(id_numero).value;

    $.ajax({
        type: "POST",
        url: path+"/"+calle+"/"+numero,
        success: function(result){
            location.reload(true);
        }
    });
}

function enviarCiudad(id,id_ciudad, id_calle, id_numero) {
    var path = document.getElementById(id).value;
    var ciudad = document.getElementById(id_ciudad).value;
    var calle = document.getElementById(id_calle).value;
    var numero = document.getElementById(id_numero).value;

    $.ajax({
        type: "POST",
        url: path+"/"+ciudad+"/"+calle+"/"+numero,
        success: function(result){
            location.reload(true);
        }
    });
}
