
document.addEventListener("DOMContentLoaded", function(event) {

    const showNavbar = (toggleId, navId, bodyId, headerId) =>{
        const toggle = document.getElementById(toggleId),
        nav = document.getElementById(navId),
        bodypd = document.getElementById(bodyId),
        headerpd = document.getElementById(headerId)

        // Validate that all variables exist
        if(toggle && nav && bodypd && headerpd){
            toggle.addEventListener('click', ()=>{
                // show navbar
                nav.classList.toggle('show')
                // change icon
                toggle.classList.toggle('bx-x')
                // add padding to body
                bodypd.classList.toggle('body-pd')
                // add padding to header
                headerpd.classList.toggle('body-pd')
            })
        }
    }

    showNavbar('header-toggle','nav-bar','body-pd','header')

    /*===== LINK ACTIVE =====*/
    const linkColor = document.querySelectorAll('.nav_link')

    function colorLink(){
        if(linkColor){
            linkColor.forEach(l=> l.classList.remove('active'))
            this.classList.add('active')
        }
    }

    linkColor.forEach(l=> l.addEventListener('click', colorLink))

    // Reportes Organizacion
    const reporteOrganizacion = document.getElementById("path_reporte_organizacion");
    if(reporteOrganizacion) {
        const path = reporteOrganizacion.value;
        obtenerHCOrganizacion(path);
    }

    const reporteComposicionOrganizacion = document.getElementById("path_reporte_composicion_organizacion");
    if(reporteComposicionOrganizacion) {
        const path = reporteComposicionOrganizacion.value;
        const anio = document.getElementById("anio").value;

        obtenerComposicionOrganizacion(path+anio)
    }

    //Reporte Evolucion Agente sectorial
    const reporteEvolucionAgente = document.getElementById("path_reporte_agente");
    if(reporteEvolucionAgente) {
        const pathEvolucionAgente = reporteEvolucionAgente.value;
        obtenerHCAgente(pathEvolucionAgente);
    }
});

//Calcular composicion agente sectorial
let calcularComposicionButton = document.getElementById("calcular-composicion");
if(calcularComposicionButton) {
    let pathComposicionAgente = null;
    calcularComposicionButton.addEventListener("click", function (){
        pathComposicionAgente = document.getElementById("path_reporte_agente").value + "/" + document.getElementById("periodicidad").value + "/" + document.getElementById("periodo").value;
        obtenerComposicionAgente(pathComposicionAgente);
    })
}



function obtenerHCOrganizacion(path) {
    var xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function() {
        if (this.readyState == 4) {
            if (this.status == 200) {
                let respuestaJSON = JSON.parse(this.responseText);
                console.log(respuestaJSON)
                lineOrganizacion(respuestaJSON);
            }

            if (this.status == 404) {
                console.log("Page not found.");
            }
        }
    }

    xhttp.open("GET", path, true);
    xhttp.send();
}


function lineOrganizacion(organizacion) {
    let evolChartContainer = document.getElementById('evolucion-organizacion-chart')
    printLineChart(organizacion.anios, organizacion.valores, evolChartContainer)
}

//JSON Evol Agente
function obtenerHCAgente(path) {
    var xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function() {
        if (this.readyState == 4) {
            if (this.status == 200) {
                let respuestaJSON = JSON.parse(this.responseText);
                console.log(respuestaJSON)
                lineAgente(respuestaJSON);
            }

            if (this.status == 404) {
                console.log("Page not found.");
            }
        }
    }

    xhttp.open("GET", path, true);
    xhttp.send();
}


function lineAgente(historial) {
    let evolChartContainer = document.getElementById('evolucion-agente-chart')
    printLineChart(historial.anios, historial.valores, evolChartContainer)
}

//JSON Composicion Agente
function obtenerComposicionAgente(path) {
    var xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function() {
        if (this.readyState == 4) {
            if (this.status == 200) {
                let respuestaJSON = JSON.parse(this.responseText);
                console.log(respuestaJSON)
                donutAgente(respuestaJSON);
            }

            if (this.status == 404) {
                console.log("Page not found.");
            }
        }
    }

    xhttp.open("GET", path, true);
    xhttp.send();
}

function donutAgente(composiciones) {
    let compChartContainer = document.getElementById('composicion-agente-chart')
    compChartContainer.innerHTML = ''
    printDonutChart(composiciones.composiciones, composiciones.valores, compChartContainer)
}

function printLineChart(variables, valores, nodo) {
    let canvas = nodo.firstChild
    nodo.removeChild(canvas)
    let miCanvas = document.createElement('canvas')

    // Creo el chart
    miChart = new Chart(miCanvas, {
        type: 'line',
        data: {
            labels: variables,
            datasets: [{
                label: 'HC en kgCO2eq',
                data: valores,
                backgroundColor: 'rgb(220, 240, 224)',
                borderColor: 'rgb(39, 198, 109)',
                fill: true,
            }]
        },
        options: {
            plugins: {
                legend: {
                position: 'bottom'
                }
            },
            manteinAspectRadio: true
        }
    })

    nodo.appendChild(miCanvas)
}

const styles = {
    color: {
        solids: [
            'rgb(247, 213, 25)',
            'rgb(115, 102, 241)',
            'rgb(39, 198, 109)',
            'rgb(333, 90, 194)',
            'rgb(1, 120, 351)']
    }
}

function printDonutChart(variables, valores, nodo) {
    let miCanvas = document.createElement('canvas')

    //crear el chart
    miChart = new Chart(miCanvas, {
        type: 'doughnut',
        data: {
            labels: variables,
            datasets: [{
                data: valores,
                backgroundColor: styles.color.solids.map(color => color)
            }]
        },
        options: {
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    })

    nodo.appendChild(miCanvas)
}


function cambiarGrafico() {
    const reporteComposicionOrganizacion = document.getElementById("path_reporte_composicion_organizacion");
    const path = reporteComposicionOrganizacion.value;
    const anio = document.getElementById("anio").value;

    obtenerComposicionOrganizacion(path+anio);
}

function obtenerComposicionOrganizacion(path) {
    var xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function() {
        if (this.readyState == 4) {
            if (this.status == 200) {
                let respuestaJSON = JSON.parse(this.responseText);
                console.log(respuestaJSON)
                donutOrganizacion(respuestaJSON);
            }

            if (this.status == 404) {
                console.log("Page not found.");
            }
        }
    }

    xhttp.open("GET", path, true);
    xhttp.send();
}

function donutOrganizacion(composiciones) {
    let compChartContainer = document.getElementById('composicion-organizacion-chart')
    compChartContainer.innerHTML = ''
    printDonutChart(composiciones.composiciones, composiciones.valores, compChartContainer)
}