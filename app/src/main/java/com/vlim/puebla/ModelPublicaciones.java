package com.vlim.puebla;

class ModelPublicaciones {
    private String titulo, usuario, descripcion, fecha, imagenUsuario, imagen, imagen2, video, ncomentarios, idpublicacion;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenUsuario() {
        return imagenUsuario;
    }

    public void setImagenUsuario(String imagenUsuario) {
        this.imagenUsuario = imagenUsuario;
    }

    public String getImagenMensaje() {
        return imagen;
    }
    public void setImagenMensaje(String imagen) {
        this.imagen = imagen;
    }

    public String getImagen2Mensaje() {
        return imagen2;
    }
    public void setImagen2Mensaje(String imagen2) {
        this.imagen2 = imagen2;
    }

    public void setVideoMensaje(String video) {
        this.video = video;
    }

    public String getVideoMensaje() {
        return video;
    }

    public String getNComentarios() {
        return ncomentarios;
    }

    public void setNComentarios(String ncomentarios) {
        this.ncomentarios = ncomentarios;
    }

    public String getIdPublicacion() {
        return idpublicacion;
    }

    public void setIdPublicacion(String idpublicacion) {
        this.idpublicacion = idpublicacion;
    }
}
