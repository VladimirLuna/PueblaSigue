package com.vlim.puebla;

public class Config {

    private static final String HOST = "http://187.217.215.234:8080/";   /* Puebla */

    // En el 54
    public static final String BOTON_PANICO_UNIPOL_URL = HOST + "Escudo_Yucatan/servicios/botonPanicoUNIPOL";
    public static final String CERO89_URL = HOST + "Escudo_Yucatan/servicios/089";
    public static final String CERO89_ARCHIVOS_URL = HOST + "Escudo_Yucatan/servicios/089Archivos";

    public static final String GET_CAT_MUJER = HOST + "/Escudo_Yucatan/servicios/getCatMujer";     //idusr, descripcion, lugar, lat, lon, file, idcatmujer
    public static final String REPORTE_MUJER_ARCHIVOS = HOST + "/Escudo_Yucatan/servicios/reporteMujerArchivos";     //idusr, descripcion, lugar, lat, lon, file, idcatmujer
    public static final String REPORTE_MUJER_SINARCHIVOS = HOST + "/Escudo_Yucatan/servicios/reporteMujer";     //idusr, descripcion, lugar, lat, lon, idcatmujer

    // En Yucatán
    public static final String NUEVA_PUBLICACION_URL = HOST + "Escudo_Yucatan/servicios/nuevoPost";
    public static final String NUEVA_PUBLICACION_SINMEDIOS_URL = HOST + "Escudo_Yucatan/servicios/nuevoPostSN";
    public static final String URLcomentariosSecre = HOST + "Escudo_Yucatan/servicios/getMensajesSecretario";
    public static final String URLnuevoMensajeSecretario = HOST + "Escudo_Yucatan/servicios/nuevoMensaje";
    public static final String UPDATE_NICKNAME_URL = HOST + "Escudo_Yucatan/servicios/updateNickname";
    public static final String LOGIN_URL = HOST + "Escudo_Yucatan/servicios/validaUsuario";
    public static final String GET_POSTS_URL = HOST + "Escudo_Yucatan/servicios/getPosts";
    public static final String GET_COMENTARIOS_URL = HOST + "Escudo_Yucatan/servicios/getComentarios";
    public static final String AGREGA_COMENTARIOS_URL = HOST + "Escudo_Yucatan/servicios/agregarComentario";
    public static final String UPDATE_IMAGEN_URL = HOST + "Escudo_Yucatan/servicios/updateImagen"; //file, idusr
    public static final String GET_TIPO_DOCUMENTO_URL = HOST + "Escudo_Yucatan/servicios/getTipoDocumento";
    public static final String REGISTRO_NUEVO_USUARIO_URL = HOST + "Escudo_Yucatan/servicios/RegistroUsuario";   //idconv
    public static final String CAMBIO_PASS_URL = HOST + "/Escudo_Yucatan/servicios/cambioPass";     //username, cel, newpass
    /////////////////

    // 911
    public static final String SUBMOTIVOS_URL = HOST + "Escudo_Yucatan/servicios/getSubmotivos";
    public static final String FOLIOS_URL = HOST + "Escudo_Yucatan/servicios/getFolios";
    public static final String NUEVEONCE_URL = HOST + "Escudo_Yucatan/servicios/911";
    public static final String NUEVEONCEARCHIVOS_URL = HOST + "Escudo_Yucatan/servicios/911Archivos";
    /////////////////

    // URL imagenes avisos
    public static final String URLaviso1 = HOST + "Escudo_Yucatan/complementos/publicidad/1.png";
    public static final String URLaviso2 = HOST + "Escudo_Yucatan/complementos/publicidad/2.png";
    public static final String URLaviso3 = HOST + "Escudo_Yucatan/complementos/publicidad/3.png";
    public static final String URLaviso4 = HOST + "Escudo_Yucatan/complementos/publicidad/4.png";
    public static final String URLaviso5 = HOST + "Escudo_Yucatan/complementos/publicidad/5.png";
    public static final String URLaviso6 = HOST + "Escudo_Yucatan/complementos/publicidad/6.png";
    public static final String URLaviso7 = HOST + "Escudo_Yucatan/complementos/publicidad/7.png";
    public static final String URLaviso8 = HOST + "Escudo_Yucatan/complementos/publicidad/8.png";
    public static final String URLaviso9 = HOST + "Escudo_Yucatan/complementos/publicidad/9.png";
    public static final String URLaviso10 = HOST + "Escudo_Yucatan/complementos/publicidad/10.png";
    public static final String URLaviso11 = HOST + "Escudo_Yucatan/complementos/publicidad/11.png";
    public static final String URLaviso12 = HOST + "Escudo_Yucatan/complementos/publicidad/12.png";
    public static final String URLaviso13 = HOST + "Escudo_Yucatan/complementos/publicidad/13.png";
    public static final String URLaviso14 = HOST + "Escudo_Yucatan/complementos/publicidad/14.png";
    public static final String URLaviso15 = HOST + "Escudo_Yucatan/complementos/publicidad/15.png";
    public static final String URLaviso16 = HOST + "Escudo_Yucatan/complementos/publicidad/16.png";
    public static final String URLaviso17 = HOST + "Escudo_Yucatan/complementos/publicidad/17.png";
    public static final String URLaviso18 = HOST + "Escudo_Yucatan/complementos/publicidad/18.png";
    public static final String URLaviso19 = HOST + "Escudo_Yucatan/complementos/publicidad/19.png";
    public static final String URLaviso20 = HOST + "Escudo_Yucatan/complementos/publicidad/20.png";
    /////////////////

    public static final String IMAGE_DIRECTORY_NAME = "EscudoYucatan";
}
