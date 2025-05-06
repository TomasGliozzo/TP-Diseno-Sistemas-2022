function confirmarEliminacion(id) {
  document.getElementById("eliminar").value = id;
  document.getElementById("modalEliminar").style.display = 'block';
}

function eliminar() {
    var urlEliminar = document.getElementById("urlEliminar").value;
    var id = document.getElementById("eliminar").value;

    $.ajax({
        type: "DELETE",
        url: urlEliminar+id,
        success: function(result){
            location.reload(true);
        }
    });
}

function cerrarModal(){
  document.getElementsByClassName("modal")[0].style.display = 'none';
}