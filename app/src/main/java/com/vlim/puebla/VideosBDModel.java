package com.vlim.puebla;

public class VideosBDModel {
    public String medioVideo;
    public String medioName;

    public VideosBDModel(String medioImage, String medioName) {
        this.medioVideo = medioImage;
    }

    public String getMedioVideo() {
        return medioVideo;
    }

    public void setMedioVideo(int productVideo) {
        this.medioVideo = medioVideo;
    }
    public String getMedioName() {
        return medioName;
    }

    public void setMedioName(String productName) {
        this.medioName = medioName;
    }


}