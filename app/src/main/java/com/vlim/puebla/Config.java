package com.vlim.puebla;

public class Config {

    //DB Version
    static int VERSION_DB = 6;

    private static final String HOST = "http://201.139.97.155:8080/";   /* Puebla */
    ////public static final String HOST = "http://54.183.159.116:8182/";   /* VLIM */

    // Servicios
    public static final String LOGIN_URL = HOST + "Escudo_Puebla/servicios/validaUsuario";
    public static final String GET_POSTS_URL = HOST + "Escudo_Puebla/servicios/getPosts";
    public static final String NUEVA_PUBLICACION_URL = HOST + "Escudo_Puebla/servicios/nuevoPost";
    public static final String NUEVA_PUBLICACION_SINMEDIOS_URL = HOST + "Escudo_Puebla/servicios/nuevoPostSN";
    public static final String REGISTRO_NUEVO_USUARIO_URL = HOST + "Escudo_Puebla/servicios/RegistroUsuario";   //idconv
    public static final String GET_TIPO_DOCUMENTO_URL = HOST + "Escudo_Puebla/servicios/getTipoDocumento";
    public static final String GET_COMENTARIOS_URL = HOST + "Escudo_Puebla/servicios/getComentarios";
    public static final String AGREGA_COMENTARIOS_URL = HOST + "Escudo_Puebla/servicios/agregarComentario";
    public static final String BOTON_PANICO_URL = HOST + "Escudo_Puebla/servicios/botonPanico";
    public static final String IMAGEN_CHAT_URL = HOST + "Escudo_Puebla/complementos/imagenesperfil/";
    // 911
    public static final String GET_SUBMOTIVOS_URL = HOST + "Escudo_Puebla/servicios/getSubmotivos";
    public static final String NUEVEONCE_URL = HOST + "Escudo_Puebla/servicios/911";
    public static final String NUEVEONCEARCHIVOS_URL = HOST + "Escudo_Puebla/servicios/911Archivos";
    public static final String GET_TIPO_USUARIOS_URL = HOST + "Escudo_Puebla/servicios/getTipoUsuarios";
    public static final String GET_CONVERSACIONES_URL = HOST + "Escudo_Puebla/servicios/getConversaciones";
    public static final String GET_MENSAJES_URL = HOST + "Escudo_Puebla/servicios/getMensajes";
    public static final String NUEVO_MENSAJE_URL = HOST + "Escudo_Puebla/servicios/nuevoMensaje";
    public static final String NUEVA_CONVERSACION_URL = HOST + "Escudo_Puebla/servicios/nuevoConv";
    public static final String NOTIFICACION_URL = HOST + "Escudo_Puebla/servicios/notification";
    public static final String UPDATE_IMAGEN_URL = HOST + "Escudo_Puebla/servicios/updateImagen"; //file, idusr
    public static final String UPDATE_TOKEN_URL = HOST + "Escudo_Puebla/servicios/updateToken"; //idusr, idusr
    public static final String UPDATE_NICKNAME_URL = HOST + "Escudo_Puebla/servicios/updateNickname";
    public static final String CAMBIO_PASS_URL = HOST + "/Escudo_Puebla/servicios/cambioPass";     // username, cel, newpass
    public static final String FOLIOS_URL = HOST + "Escudo_Puebla/servicios/getFolios";
    public static final String GET_MENSAJES_SECRETARIO_URL = HOST + "Escudo_Puebla/servicios/getMensajesSecretario";
    public static final String CERO89_ARCHIVOS_URL = HOST + "Escudo_Puebla/servicios/089Archivos";
    public static final String CERO89_URL = HOST + "Escudo_Puebla/servicios/089";
    public static final String BOTON_PANICO_UNIPOL_URL = HOST + "Escudo_Puebla/servicios/botonPanicoUNIPOL";
    /////////////////
    public static final String IMAGE_DIRECTORY_NAME = "Puebla";
    /////
    public static final String GET_CAT_089 = HOST + "Escudo_Puebla/servicios/getCat089";   //{"idusr":"valor"} || [{"id_cat089":valor, "nombre":"valor"}]
    public static final String GET_CAT_COLONIA = HOST + "Escudo_Puebla/servicios/getCatColonia";   //{"idusr":"valor"} || [{"id_cat_colonia":valor, "nombre":"valor"}]
    // Sigueme y cuidame
    public static final String NUEVA_RUTA_URL = HOST + "Escudo_Puebla/servicios/nuevaRuta";   //{"latusr":"valor","lonusr":"valor","latdest":"valor","londest":"valor","idusr":"valor","ruta":[{"lng":"valor","lat":"valor"},{"lng":"valor",º"lat":"valor"}]} || [{"respuesta":OK, "mensaje":num_ruta}] ó [{"respuesta":Error, "mensaje":Ocurrio un error, favor de comunicarse con el administrador}]
    public static final String CANCELA_VIAJE_URL = HOST + "Escudo_Puebla/servicios/cancelaViaje";   //{"idviaje":"valor"} || [{"respuesta":OK, "mensaje":Se cancelo el viaje correctamente}] ó [{"respuesta":Error, "mensaje":No se pudo cancelar el viaje}]
    public static final String BUFFER_RUTA_URL = HOST + "Escudo_Puebla/servicios/bufferRuta";   //{"idviaje":"valor", "lat":"valor","lng":"valor"} || [{"respuesta":OK, "mensaje":Sigue dentro de la ruta}] ó [{"respuesta":Error, "mensaje":Ha salido de la ruta, avisar a contactos}]
    // Ajustes cambio de password
    public static final String AJUSTES_CAMBIO_PASS_URL = HOST + "Escudo_Puebla/servicios/AjustescambioPass";   //{"idusr":"valor","newpass":"valor"} || [{"respuesta":OK, "mensaje":Se cambio el password}] ó [{"respuesta":Error, "mensaje":No se pudo cambiar el password}]
    // Contactos de emergencia
    public static final String GET_CONTACTOS_URL = HOST + "Escudo_Puebla/servicios/getContactos";   //{"idusr":"valor"} || [{"id_usuario_contacto":valor, "nombre_completo":"valor"}]
    public static final String GET_INFO_CONTACTO_URL = HOST + "Escudo_Puebla/servicios/getInfoContacto";   //{"idusr":"valor"} || [{"nombre_completo":valor, "telefono":"valor", "celular":"valor", "correo_contacto":"valor"}]
    public static final String INSERT_CONTACTO_URL = HOST + "Escudo_Puebla/servicios/insertContacto";   // {"idusr":"valor", "nombre":"valor", "tel":"valor", "cel":"valor", "correo":"valor"} || [{"respuesta":OK, "mensaje":Se agrego el contacto}] ó [{"respuesta":Error, "mensaje":Error al insertar contacto}]
    public static final String UPDATE_CONTACTO_URL = HOST + "Escudo_Puebla/servicios/updateContacto";   // {"idcontacto":"valor", "nombre":"valor", "tel":"valor", "cel":"valor", "correo":"valor"} || [{"respuesta":OK, "mensaje":Se editó el contacto}] ó [{"respuesta":Error, "mensaje":Error al editar contacto}]
    public static final String ELIMINA_CONTACTO_URL = HOST + "Escudo_Puebla/servicios/deleteContacto";   // {"deleteContacto":"valor"} || [{"respuesta":OK, "mensaje":Se borró el contacto}] ó [{"respuesta":Error, "mensaje":Error al borrar contacto}]
    // Info del usuario
    public static final String GET_INFO_USUARIO = HOST + "Escudo_Puebla/servicios/getInfoUsuario ";   //{"idusr":"valor"} ||  "direccion": "Calzada Melchor Ocampo", "telefono_casa": 55555555,  "celular": 5533357812, "id_identificacion": 4, "num_identificcion": "MH6PO3MN", "condicion_medica": "Pues escuche una rola muy feita y baile :("
    public static final String UPDATE_INFO_USUARIO = HOST + "Escudo_Puebla/servicios/updateInfoUsuario  ";   //{"idusr":"valor"} || [{"id_cat089":valor, "nombre":"valor"}]
}
