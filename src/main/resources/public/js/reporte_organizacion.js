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


function obtenerHCOrganizacion(path) {
    alert(path);

    var xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function() {
        if (this.readyState == 4) {
            if (this.status == 200) {
                let respuestaJSON = JSON.parse(this.responseText);

                document.getElementById("respuestaJSON").innerText = respuestaJSON;
            }

            if (this.status == 404) {
                console.log("Page not found.");
            }
        }
    }

    xhttp.open("GET", path, true);
    xhttp.send();
}


/*
const data = {
    "organizaciones": [
        {
            "nombre": "mostaza",
            "composiciones": [
                {
                    "composicion": "Combustion Fija",
                    "valor": 92
                },
                {
                    "composicion": "Logistica",
                    "valor": 13
                },
                {
                    "composicion": "Transporte",
                    "valor": 59
                }
            ]
        }
    ]
}

    $("#calcular-composicion").click(function () {
        //var url = "@Url.Action("mostrarComposicion", "ReportesController")";
        var periodicidad = $("#periodicidad").val();
        var periodo = $("#periodo").val();
        var organizacion = $("#organizacion").val()

        console.log("periodo")

        /*$.ajax({
            type: "POST",
            url: "reportes/calcular-composicion/" + periodicidad + "/" + periodo,
            success: function (data) {
                console.log(data);
                //printDonutChart();
            }

        })
        */
    })



function donutOrganizacion(organizacion) {
    let composiciones = organizacion.composiciones.map(comp => comp.composicion)
    let valores = organizacion.composiciones.map(comp => comp.valor)

    let figure = document.getElementById('composicion-organizacion-chart')

    printDonutChart(composiciones,valores,figure)
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


function lineOrganizacion(organizacion){
    let anios = ['2019', '2020', '2021', '2022']
    let valores = [34, 394, 290, 539]

    let evolChartContainer = document.getElementById('evolucion-organizacion-chart')

    printLineChart(anios,valores,evolChartContainer)
}


function printLineChart(variables, valores, nodo) {
    //eliminar el canvas si ya lo tiene
    let canvas = nodo.firstChild
    nodo.removeChild(canvas)
    let miCanvas = document.createElement('canvas')

    //crear el chart
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


donutOrganizacion(data.organizaciones[0]);
lineOrganizacion(data.organizaciones[0]);
*/