
function aceptarSolicitud(id) {
    document.getElementById("solicitud_id").value = id;
    document.getElementById("accion_solicitud").value = "APROBADA";
}

function rechazarSolicitud(id) {
    document.getElementById("solicitud_id").value = id;
    document.getElementById("accion_solicitud").value = "RECHAZADA";
}

function eliminarSolicitud(id) {
    document.getElementById("solicitud_id").value = id;
}

function eliminarContacto(id) {
    document.getElementById("contacto_id").value = id;
}


function eliminar(nombre_id, id) {
    document.getElementById(nombre_id).value = id;
}

function agregarMiembro(valor) {
    let elemento = document.getElementById("lista_miembros");
    elemento.value = elemento.value + valor + ',';
}