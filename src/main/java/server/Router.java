package server;

import db.EntityManagerHelper;
import domain.controllers.*;
import domain.controllers.Calculadora.CalculadoraController;
import domain.controllers.FE.FEController;
import domain.controllers.Medicionaes.CargaMedicionesController;
import domain.controllers.Menu.GuiaRecomendacionesController;
import domain.controllers.Menu.IndexController;
import domain.controllers.Miembro.MiembroController;
import domain.controllers.Miembro.SolicitudController;
import domain.controllers.Organizacion.ContactoController;
import domain.controllers.Organizacion.OrganizacionController;
import domain.controllers.Organizacion.SectorController;
import domain.controllers.Trayecto.*;
import domain.controllers.Usuario.LoginController;
import domain.controllers.Usuario.RegisterController;
import domain.modelo.entities.Entidades.Usuario.Permiso;
import helpers.AccesoRecursosPropiosHelper;
import helpers.PermisosHelper;
import middlewares.AuthMiddleware;
import spark.Spark;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.utils.BooleanHelper;
import spark.utils.HandlebarsTemplateEngineBuilder;

public class Router {

    private static HandlebarsTemplateEngine engine;

    private static void initEngine() {
        Router.engine = HandlebarsTemplateEngineBuilder
                .create()
                .withDefaultHelpers()
                .withHelper("isTrue", BooleanHelper.isTrue)
                .build();
    }

    public static void init() {
        Spark.port(getHerokuAssignedPort());
        Router.initEngine();
        Spark.staticFileLocation("/public");
        Router.configure();
    }
    public static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 9000; //return default port if heroku-port isn't set (i.e. on localhost)
    }
    private static void configure(){
        // Login Register
        LoginController loginController = new LoginController();
        RegisterController registerController = new RegisterController();
        //
        IndexController indexController = new IndexController();
        GuiaRecomendacionesController guiaRecomendacionesController = new GuiaRecomendacionesController();
        CalculadoraController calculadoraController = new CalculadoraController();

        // Recursos
        SolicitudController solicitudController = new SolicitudController();
        ContactoController contactoController = new ContactoController();
        MiembroController miembroController = new MiembroController();
        OrganizacionController organizacionController = new OrganizacionController();
        CargaMedicionesController cargaMedicionesController = new CargaMedicionesController();
        TrayectoController trayectoController = new TrayectoController();
        TramoController tramoController = new TramoController();
        SectorController sectorController = new SectorController();
        SectorTerritorialController sectorTerritorialController = new SectorTerritorialController();
        AgenteSectorialController agenteSectorialController = new AgenteSectorialController();

        // Transportes
        TransporteEcologicoController transporteEcologicoController = new TransporteEcologicoController();
        TrasporteParticularController trasporteParticularController = new TrasporteParticularController();
        TransporteContratadoController transporteContratadoController = new TransporteContratadoController();
        TransportePublicoController transportePublicoController = new TransportePublicoController();
        ParadaController paradaController = new ParadaController();
        DireccionController direccionController = new DireccionController();
        // FE
        FEController feController = new FEController();
        ReporteController reporteController = new ReporteController();



        Spark.path("/login", () -> {
            Spark.get("", loginController::pantallaDeLogin, engine);
            Spark.post("", loginController::login);
            Spark.post("/logout", loginController::logout);
        });

        Spark.get("/prohibido", loginController::prohibido, engine);

        Spark.path("/registro", () -> {
            Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

            Spark.get("", registerController::mostrar, Router.engine);
            Spark.post("", registerController::registrar);
        });

        Spark.path("/organizacion/:id", () -> {
            Spark.before("", AuthMiddleware::verificarSesion);
            Spark.before("/*", AuthMiddleware::verificarSesion);

            Spark.before("", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_ORGANIZACION, Permiso.EDITAR_ORGANIZACION)
                        || !AccesoRecursosPropiosHelper.mismaOrganizacion(request)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.before("/*", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_ORGANIZACION, Permiso.EDITAR_ORGANIZACION)
                        || !AccesoRecursosPropiosHelper.mismaOrganizacion(request)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            // SOLICITUDES
            Spark.path("/solicitudes", () -> {
                Spark.before("", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_SOLICITUD)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.before("/*", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_SOLICITUD)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                Spark.get("", solicitudController::mostrarSolicitudesOrganizacion, Router.engine);
                Spark.post("", solicitudController::confirmarSolicitud);
            });

            // CONTACTOS
            Spark.path("/contactos", () -> {
                Spark.before("", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_CONTACTO, Permiso.CREAR_CONTACTO,
                            Permiso.EDITAR_CONTACTO, Permiso.ELIMIAR_CONTACTO)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.before("/*", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_CONTACTO, Permiso.CREAR_CONTACTO,
                            Permiso.EDITAR_CONTACTO, Permiso.ELIMIAR_CONTACTO)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.after("", (request, response) -> EntityManagerHelper.closeEntityManager() );
                Spark.after("*", (request, response) -> EntityManagerHelper.closeEntityManager() );

                Spark.get("", contactoController::mostrarTodos, Router.engine);
                Spark.get("/crear", contactoController::crear, engine);
                Spark.get("/:id_contacto/editar", contactoController::editar, engine);
                Spark.post("/:id_contacto/editar", contactoController::modificar);
                Spark.post("/crear", contactoController::guardar);
                Spark.delete("/eliminar/:id_contacto", contactoController::eliminar);
            });

            // MEDICIONES
            Spark.path("/mediciones", () -> {
                Spark.before("", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.CARGA_MEDICIONES)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.before("/*", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.CARGA_MEDICIONES)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                //Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                //Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                Spark.get("", cargaMedicionesController::mostrar, Router.engine);
                Spark.post("/cargar", cargaMedicionesController::guardar);
            });

            // SECTORES
            Spark.path("/sectores", ()-> {
                Spark.before("", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_SECTOR, Permiso.CREAR_SECTOR,
                            Permiso.EDITAR_SECTOR, Permiso.ELIMIAR_SECTOR)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.before("/*", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_SECTOR, Permiso.CREAR_SECTOR,
                            Permiso.EDITAR_SECTOR, Permiso.ELIMIAR_SECTOR)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                Spark.get("/crear", sectorController::agregarSector, engine);
                Spark.get("/:id_sector/editar", sectorController::editar, engine);
                Spark.get("", sectorController::mostrarTodos, engine);
                Spark.post("/crear", sectorController::crearSector);
                Spark.post("/:id_sector/editar", sectorController::modificar);
            });

            // CALCULADORA
            Spark.path("/calculadora", () -> {
                Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                Spark.get("", calculadoraController::mostrarCalculadoraOrganizacion, Router.engine);
                Spark.post("", calculadoraController::calcularHCOrganizacion);
            });

            Spark.get("/editar", organizacionController::editar, engine);
            Spark.post("/:id/editar", organizacionController::modificar);

            Spark.get("/editar/cambiar_contrasenia", organizacionController::cambiarContrasenia, engine);
            Spark.post("/editar/cambiar_contrasenia", organizacionController::modificarContrasenia);

            // INDEX
            Spark.get("/index", indexController::mostrarIndexOrganizacion, Router.engine);

            // RECOMENDACIONES
            Spark.get("/recomendaciones", guiaRecomendacionesController::mostrarRecomendacionesOrganizacion, Router.engine);

            // REPORTE
            Spark.get("/reporte", organizacionController::reporteOrganizacion, engine);
            Spark.get("/reporte_composicion", organizacionController::reporteCompuestoOrganizacion, engine);
        });

        Spark.path("/miembro/:id", () -> {
            Spark.before("", AuthMiddleware::verificarSesion);
            Spark.before("/*", AuthMiddleware::verificarSesion);

            Spark.before("", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_MIEMBRO, Permiso.EDITAR_MIEMBRO)
                        || !AccesoRecursosPropiosHelper.mismoMiembro(request)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.before("/*", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_MIEMBRO, Permiso.EDITAR_MIEMBRO)
                        || !AccesoRecursosPropiosHelper.mismoMiembro(request)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            // SOLICITUDES
            Spark.path("/solicitudes", () -> {
                Spark.before("", AuthMiddleware::verificarSesion);
                Spark.before("/*", AuthMiddleware::verificarSesion);

                Spark.before("", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_SOLICITUD, Permiso.CREAR_SOLICITUD,
                            Permiso.EDITAR_SOLICITUD, Permiso.ELIMIAR_SOLICITUD)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.before("/*", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_SOLICITUD, Permiso.CREAR_SOLICITUD,
                            Permiso.EDITAR_SOLICITUD, Permiso.ELIMIAR_SOLICITUD)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                Spark.get("", solicitudController::mostrarSolicitudesMiembro, Router.engine);
                Spark.get("/crear", solicitudController::crear, engine);
                Spark.post("/crear", solicitudController::guardar);
                Spark.post("/crear/:id_organizacion", solicitudController::obtenerOrganizaciones);
                Spark.delete("/eliminar/:solicitud_id", solicitudController::eliminar);
            });

            // TRAYECTOS
            Spark.path("/trayectos", () -> {
                Spark.before("", AuthMiddleware::verificarSesion);
                Spark.before("/*", AuthMiddleware::verificarSesion);

                Spark.before("", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_TRAYECTO, Permiso.CREAR_TRAYECTO,
                            Permiso.EDITAR_TRAYECTO, Permiso.ELIMIAR_TRAYECTO)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.before("/*", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_TRAYECTO, Permiso.CREAR_TRAYECTO,
                            Permiso.EDITAR_TRAYECTO, Permiso.ELIMIAR_TRAYECTO)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                Spark.get("", trayectoController::mostrarTodos, Router.engine);
                Spark.get("/crear", trayectoController::crearTrayecto, engine);
                Spark.get("/:id_trayecto/editar", trayectoController::editarTrayecto, engine);
                Spark.post("/crear",trayectoController::guardarTrayecto);
                Spark.post("/:id_trayecto/editar", trayectoController::modificarTrayecto);
                Spark.delete("/eliminar/:id_trayecto", trayectoController::eliminarTrayecto);

                Spark.path("/:id_trayecto/tramos", () -> {
                    Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                    Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                    Spark.get("", tramoController::mostrarTodos, engine);

                    Spark.get("/crear", tramoController::crear, engine);
                    Spark.post("/crear", tramoController::guardarVehiculo);
                    Spark.post("/crear/guardar", tramoController::guardarTramo);
                    Spark.post("/crear/transporte/:id_transporte", tramoController::obtenerTipoVehiculo);

                    Spark.get("/crear/direcciones", direccionController::mostrarCargaDireccionTramo, engine);
                    Spark.post("/crear/provincia_inicio/:id_provincia", direccionController::actualizarProvinciaInicio);
                    Spark.post("/crear/municipio_inicio/:id_municipio", direccionController::actualizarMunicipioInicio);
                    Spark.post("/crear/localidad_inicio/:id_localidad", direccionController::actualizarLocalidadInicio);
                    Spark.post("/crear/provincia_fin/:id_provincia", direccionController::actualizarProvinciaFin);
                    Spark.post("/crear/municipio_fin/:id_municipio", direccionController::actualizarMunicipioFin);
                    Spark.post("/crear/localidad_fin/:id_localidad", direccionController::actualizarLocalidadFin);
                    Spark.post("/crear/calle_inicio/:calle/:numero", direccionController::actualizarCalleInicio);
                    Spark.post("/crear/calle_fin/:calle/:numero", direccionController::actualizarCalleFin);

                    //Spark.get("/:id_tramo/editar", tramoController::editar, engine);
                    //Spark.post("/:id_tramo/agregarMiembro", tramoController::agregarMiembro);
                    //Spark.post("/:id_tramo/eliminarMiembro", tramoController::eliminarMiembro);

                    Spark.delete("/eliminar/:id_tramo", tramoController::eliminar);
                });
            });

            Spark.path("/editar", () -> {
                Spark.before("", AuthMiddleware::verificarSesion);
                Spark.before("/*", AuthMiddleware::verificarSesion);

                Spark.before("", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_MIEMBRO, Permiso.EDITAR_MIEMBRO)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.before("/*", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_MIEMBRO, Permiso.EDITAR_MIEMBRO)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                Spark.get("", miembroController::editar, engine);
                Spark.post("", miembroController::modificar);
                Spark.get("/cambiar_contrasenia", miembroController::cambiarContrasenia, engine);
                Spark.post("/cambiar_contrasenia", miembroController::modificarContrasenia);
            });

            Spark.get("/mis_organizaciones", miembroController::mostrarOrganizaciones, engine);

            // CALCULADORA
            Spark.path("/calculadora", () -> {
                Spark.before("", AuthMiddleware::verificarSesion);
                Spark.before("/*", AuthMiddleware::verificarSesion);

                Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                Spark.get("", calculadoraController::mostrarCalculadoraMiembro, Router.engine);
                Spark.post("", calculadoraController::calcularHCMiembro);
            });

            // INDEX
            Spark.get("/index", indexController::mostrarIndexMiembro, Router.engine);

            // RECOMENDACIONES
            Spark.get("/recomendaciones", guiaRecomendacionesController::mostrarRecomendacionesMiembro, Router.engine);
        });

        Spark.path("/transportes", () -> {
            Spark.before("", AuthMiddleware::verificarSesion);
            Spark.before("/*", AuthMiddleware::verificarSesion);

            Spark.before("", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_TRANSPORTE, Permiso.CREAR_TRANSPORTE,
                        Permiso.EDITAR_TRANSPORTE, Permiso.ELIMIAR_TRANSPORTE)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.before("/*", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_TRANSPORTE, Permiso.CREAR_TRANSPORTE,
                        Permiso.EDITAR_TRANSPORTE, Permiso.ELIMIAR_TRANSPORTE)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
            Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

            // ServicioContratado
            Spark.path("/contratados", () -> {
                Spark.get("", transporteContratadoController::mostrarTransportesContratado, Router.engine);
                Spark.get("/crear", transporteContratadoController::crearTransporteContratado, engine);
                Spark.get("/:id_transporte/editar", transporteContratadoController::editarTransporteContratado, engine);
                Spark.post("/:id_transporte/editar", transporteContratadoController::modificarTransporteContratado);
                Spark.post("/crear", transporteContratadoController::guardarTransporteContratado);
                Spark.delete("/eliminar/:id_transporte", transporteContratadoController::eliminarTransporteContratado);

                Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
            });

            // TransporteEcologico
            Spark.path("/ecologicos", () -> {
                Spark.after("", (request, response) -> EntityManagerHelper.closeEntityManager() );
                Spark.after("*", (request, response) -> EntityManagerHelper.closeEntityManager() );

                Spark.get("", transporteEcologicoController::mostrarTransportesEcologicos, Router.engine);
                Spark.get("/crear", transporteEcologicoController::crearTransporteEcologico, engine);
                Spark.get("/:id_transporte/editar", transporteEcologicoController::editarTransporteEcologico, engine);
                Spark.post("/:id_transporte/editar", transporteEcologicoController::modificarTransporteEcologico);
                Spark.post("/crear", transporteEcologicoController::guardarTransporteEcologico);
                Spark.delete("/eliminar/:id_transporte", transporteEcologicoController::eliminarTransporteEcologico);
            });

            // TransportePublico
            Spark.path("/publicos", () -> {
                Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                Spark.get("", transportePublicoController::mostrarTodos, Router.engine);
                Spark.get("/crear", transportePublicoController::crear, engine);
                Spark.get("/:id_transporte/editar", transportePublicoController::editar, engine);
                Spark.post("/:id_transporte/editar", transportePublicoController::modificar);
                Spark.post("/crear", transportePublicoController::guardar);
                Spark.delete("/eliminar/:id_transporte", transportePublicoController::eliminar);

                Spark.path("/:id_transporte/paradas", () -> {
                    Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                    Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                    Spark.get("", paradaController::mostrarTodos, engine);
                    Spark.get("/crear", paradaController::crear, engine);
                    Spark.get("/:id_parada/ver", paradaController::ver, engine);
                    Spark.post("/crear/provincia/:id_provincia", paradaController::actualizarProvincia);
                    Spark.post("/crear/municipio/:id_provincia/:id_municipio", paradaController::actualizarMunicipio);
                    Spark.post("/crear", paradaController::guardar);
                    Spark.delete("/eliminar/:id_parada", paradaController::eliminar);
                });
            });

            // VehiculoParticular
            Spark.path("/particulares", () -> {
                Spark.after("", (request, response) ->  EntityManagerHelper.closeEntityManager() );
                Spark.after("*", (request, response) -> EntityManagerHelper.closeEntityManager() );

                Spark.get("", trasporteParticularController::mostrarTransportesParticular, Router.engine);
                Spark.get("/crear", trasporteParticularController::crearTransporteParticular, engine);
                Spark.get("/:id_transporte/editar", trasporteParticularController::editarTransporteParticular, engine);
                Spark.post("/:id_transporte/editar", trasporteParticularController::modificarTransporteParticular);
                Spark.post("/crear", trasporteParticularController::guardarTransporteParticular);
                Spark.delete("/eliminar/:id_transporte", trasporteParticularController::eliminarTransporteParticular);
            });
        });

        Spark.path("/fes", () -> {
            Spark.before("", AuthMiddleware::verificarSesion);
            Spark.before("/*", AuthMiddleware::verificarSesion);

            Spark.before("", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_FE, Permiso.CREAR_FE,
                        Permiso.EDITAR_FE, Permiso.ELIMIAR_FE)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.before("/*", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_FE, Permiso.CREAR_FE,
                        Permiso.EDITAR_FE, Permiso.ELIMIAR_FE)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
            Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

            Spark.get("", feController::mostrarTodos, Router.engine);
            Spark.get("/crear", feController::crear, engine);
            Spark.get("/:id_fe/editar", feController::editar, engine);
            Spark.post("/:id_fe/editar", feController::modificar);
            Spark.post("/crear", feController::guardar);
            Spark.delete("/eliminar/:id_fe", feController::eliminar);
        });

        Spark.path("/organizaciones", () -> {
            Spark.before("", AuthMiddleware::verificarSesion);
            Spark.before("/*", AuthMiddleware::verificarSesion);

            Spark.before("", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_ORGANIZACION, Permiso.CREAR_ORGANIZACION,
                        Permiso.EDITAR_ORGANIZACION, Permiso.ELIMIAR_ORGANIZACION)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.before("/*", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_ORGANIZACION, Permiso.CREAR_ORGANIZACION,
                        Permiso.EDITAR_ORGANIZACION, Permiso.ELIMIAR_ORGANIZACION)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
            Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

            Spark.get("", organizacionController::mostrarTodos, Router.engine);
            Spark.get("/crear", organizacionController::crear, engine);
            Spark.get("/:id/editar", organizacionController::editar, engine);
            Spark.post("/:id/editar", organizacionController::modificar);
            Spark.post("/crear", organizacionController::guardar);
            Spark.post("/crear/provincia/:id_provincia", organizacionController::actualizarProvincia);
            Spark.post("/crear/municipio/:id_provincia/:id_municipio", organizacionController::actualizarMunicipio);
            Spark.get("/:id/editar/cambiar_contrasenia", organizacionController::cambiarContrasenia, engine);
            Spark.post("/:id/editar/cambiar_contrasenia", organizacionController::modificarContrasenia);
            Spark.delete("/eliminar/:id_organizacion", organizacionController::eliminar);
        });

        Spark.path("/miembros", () -> {
            Spark.before("", AuthMiddleware::verificarSesion);
            Spark.before("/*", AuthMiddleware::verificarSesion);

            Spark.before("", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_MIEMBRO,
                        Permiso.EDITAR_MIEMBRO, Permiso.ELIMIAR_MIEMBRO)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.before("/*", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_MIEMBRO,
                        Permiso.EDITAR_MIEMBRO, Permiso.ELIMIAR_MIEMBRO)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
            Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

            Spark.get("", miembroController::mostrarTodos, Router.engine);
            Spark.delete("/eliminar/:id_miembro", miembroController::eliminar);

            Spark.path("/:id/editar", () -> {
                Spark.get("", miembroController::editar, engine);
                Spark.post("", miembroController::modificar);
                Spark.get("/cambiar_contrasenia", miembroController::cambiarContrasenia, engine);
                Spark.post("/cambiar_contrasenia", miembroController::modificarContrasenia);
            });
        });



        Spark.path("/api/reportes", () -> {
            Spark.get("/organizacion/:id_organizacion", reporteController::mostrarReporteOrganizacion);
            Spark.get("/organizacion/:id_organizacion/:periodo", reporteController:: mostrarReporteComposicionOrganizacion);
            Spark.get("/agente-sectorial/:id_agente", reporteController:: mostrarReporteEvolucionAgente);
            Spark.get("/agente-sectorial/:id_agente/:periodicidad/:periodo", reporteController:: mostrarReporteComposicionAgente);
        });

        Spark.path("/sectores-territoriales",()->{
            Spark.before("", AuthMiddleware::verificarSesion);
            Spark.before("/*", AuthMiddleware::verificarSesion);

            Spark.before("", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request,
                        Permiso.VER_SECTOR_TERRITORIAL,
                        Permiso.EDITAR_SECTOR_TERRITORIAL,
                        Permiso.ELIMINAR_SECTOR_TERRITORIAL,
                        Permiso.CREAR_SECTOR_TERRITORIAL)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.before("/*", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request,
                        Permiso.VER_SECTOR_TERRITORIAL,
                        Permiso.EDITAR_SECTOR_TERRITORIAL,
                        Permiso.ELIMINAR_SECTOR_TERRITORIAL,
                        Permiso.CREAR_SECTOR_TERRITORIAL)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
            Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

            Spark.get("", sectorTerritorialController::mostrarTodos, Router.engine);
            Spark.post("/eliminar", sectorTerritorialController::eliminar);
            Spark.path("/crear",()->{

                Spark.get("", sectorTerritorialController::crear, engine);
                Spark.post("/guardar", sectorTerritorialController::guardar);

                Spark.post("/provincia_inicio/:id_provincia", sectorTerritorialController::actualizarProvinciaInicio);
                Spark.post("/municipio_inicio/:id_municipio", sectorTerritorialController::actualizarMunicipioInicio);
                Spark.post("/localidad_inicio/:id_localidad", sectorTerritorialController::actualizarLocalidadInicio);
                Spark.post("/crear/:ciudad/:calle/:numero", sectorTerritorialController::actualizarCiudadInicio);


            });

            /*
            Spark.path("/:id/editar", () -> {
                Spark.get("", sectorTerritorialController::editar, engine);
                Spark.post("", sectorTerritorialController::modificar);
            });
             */
        });

        Spark.path("/agentes-sectoriales",()->{
            Spark.before("", AuthMiddleware::verificarSesion);
            Spark.before("/*", AuthMiddleware::verificarSesion);

            Spark.before("", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request,
                        Permiso.VER_AGENTE_SECTORIAL,
                        Permiso.EDITAR_AGENTE_SECTORIAL,
                        Permiso.ELIMINAR_AGENTE_SECTORIAL,
                        Permiso.CREAR_AGENTE_SECTORIAL)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.before("/*", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request,
                        Permiso.VER_AGENTE_SECTORIAL,
                        Permiso.EDITAR_AGENTE_SECTORIAL,
                        Permiso.ELIMINAR_AGENTE_SECTORIAL,
                        Permiso.CREAR_AGENTE_SECTORIAL)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
            Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

            Spark.get("", agenteSectorialController::mostrarTodos, Router.engine);
            Spark.post("/eliminar", agenteSectorialController::eliminar);

            //Spark.get("/crear", agenteSectorialController::crear, Router.engine);
            //Spark.post("/crear", agenteSectorialController::guardar);

            Spark.path("/:id/editar", () -> {
                Spark.get("", agenteSectorialController::editar, engine);
                Spark.post("", agenteSectorialController::modificar);
                Spark.get("/cambiar_contrasenia", agenteSectorialController::cambiarContrasenia, engine);
                Spark.post("/cambiar_contrasenia", agenteSectorialController::modificarContrasenia);
            });
        });

        Spark.path("/agente-sectorial/:id", () -> {
            Spark.before("", AuthMiddleware::verificarSesion);
            Spark.before("/*", AuthMiddleware::verificarSesion);

            Spark.before("", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_AGENTE_SECTORIAL, Permiso.EDITAR_AGENTE_SECTORIAL)
                        || !AccesoRecursosPropiosHelper.mismoAgenteSectorial(request)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));

            Spark.before("/*", ((request, response) -> {
                if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_AGENTE_SECTORIAL, Permiso.EDITAR_AGENTE_SECTORIAL)
                        || !AccesoRecursosPropiosHelper.mismoAgenteSectorial(request)) {
                    response.redirect("/prohibido");
                    Spark.halt();
                }
            }));
            // INDEX
            Spark.get("/index", indexController::mostrarIndexAgenteSectorial, Router.engine);

            //MI-PERFIL
            Spark.path("/editar", () -> {
                Spark.before("", AuthMiddleware::verificarSesion);
                Spark.before("/*", AuthMiddleware::verificarSesion);

                Spark.before("", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_AGENTE_SECTORIAL, Permiso.EDITAR_AGENTE_SECTORIAL)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.before("/*", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_AGENTE_SECTORIAL, Permiso.EDITAR_AGENTE_SECTORIAL)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                Spark.get("",agenteSectorialController::editarPerfil, Router.engine);
                Spark.post("",agenteSectorialController::modificarPerfil);

                Spark.get("/cambiar_contrasenia",agenteSectorialController::cambiarContraseniaPerfil,Router.engine);
                Spark.post("/cambiar_contrasenia",agenteSectorialController::modificarContraseniaPerfil);

            });

            //MI-SECTOR-TERRITORIAL
            Spark.path("/sector-territorial", ()->{
                Spark.before("", AuthMiddleware::verificarSesion);
                Spark.before("/*", AuthMiddleware::verificarSesion);

                Spark.before("", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_SECTOR_TERRITORIAL)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.before("/*", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_SECTOR_TERRITORIAL)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                Spark.get("",agenteSectorialController::mostrarSectorTerritorial,Router.engine);
            });

            //MIS-REPORTES
            Spark.path("/reportes", () -> {
                Spark.before("", AuthMiddleware::verificarSesion);
                Spark.before("/*", AuthMiddleware::verificarSesion);

                Spark.before("", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_REPORTES)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.before("/*", ((request, response) -> {
                    if(!PermisosHelper.usuarioTienePermisos(request, Permiso.VER_REPORTES)) {
                        response.redirect("/prohibido");
                        Spark.halt();
                    }
                }));

                Spark.after("", (request, response) -> { EntityManagerHelper.closeEntityManager(); });
                Spark.after("*", (request, response) -> { EntityManagerHelper.closeEntityManager(); });

                Spark.get("", agenteSectorialController::mostrarReportes, Router.engine);
            });
        });

    }

}