package com.vlim.puebla;

public class Config {

    // http://54.183.159.116:8182/Escudo_Puebla/servicios/estatus

    //private static final String HOST = "http://187.217.215.234:8080/";   /* Puebla */
    private static final String HOST = "http://54.183.159.116:8182/";   /* VLIM */

    // En Puebla
    public static final String LOGIN_URL = HOST + "Escudo_Puebla/servicios/validaUsuario";
    public static final String GET_POSTS_URL = HOST + "Escudo_Puebla/servicios/getPosts";
    public static final String NUEVA_PUBLICACION_URL = HOST + "Escudo_Puebla/servicios/nuevoPost";
    public static final String REGISTRO_NUEVO_USUARIO_URL = HOST + "Escudo_Puebla/servicios/RegistroUsuario";   //idconv
    public static final String GET_TIPO_DOCUMENTO_URL = HOST + "Escudo_Puebla/servicios/getTipoDocumento";
    public static final String GET_COMENTARIOS_URL = HOST + "Escudo_Puebla/servicios/getComentarios";
    public static final String AGREGA_COMENTARIOS_URL = HOST + "Escudo_Puebla/servicios/agregarComentario";
    public static final String BOTON_PANICO_URL = HOST + "Escudo_Puebla/servicios/botonPanico";
    // 911
    public static final String SUBMOTIVOS_URL = HOST + "Escudo_Puebla/servicios/getSubmotivos";
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
    public static final String CAMBIO_PASS_URL = HOST + "/Escudo_Puebla/servicios/cambioPass";     //username, cel, newpass
    public static final String FOLIOS_URL = HOST + "Escudo_Puebla/servicios/getFolios";
    public static final String GET_MENSAJES_SECRETARIO_URL = HOST + "Escudo_Puebla/servicios/getMensajesSecretario";
    public static final String CERO89_ARCHIVOS_URL = HOST + "Escudo_Puebla/servicios/089Archivos";
    public static final String CERO89_URL = HOST + "Escudo_Puebla/servicios/089";
    public static final String BOTON_PANICO_UNIPOL_URL = HOST + "Escudo_Puebla/servicios/botonPanicoUNIPOL";
    public static final String NUEVA_PUBLICACION_SINMEDIOS_URL = HOST + "Escudo_Puebla/servicios/nuevoPostSN";
    /////////////////
    public static final String IMAGE_DIRECTORY_NAME = "Puebla";
}
