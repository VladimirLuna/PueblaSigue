package com.vlim.puebla;

public class FotosBDModel {
    public String medioImage;
    public String medioName;

    public FotosBDModel(String medioImage, String medioName) {
        this.medioImage = medioImage;
    }

    public String getMedioImage() {
        return medioImage;
    }

    public void setMedioImage(int productImage) {
        this.medioImage = medioImage;
    }
    public String getMedioName() {
        return medioName;
    }

    public void setMedioName(String productName) {
        this.medioName = medioName;
    }


}
