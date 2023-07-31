package com.damsdev.tbc.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class RequestModel implements Parcelable {
    public static final Creator<RequestModel> CREATOR = new Creator<RequestModel>() {
        @Override
        public RequestModel createFromParcel(Parcel in) {
            return new RequestModel(in);
        }

        @Override
        public RequestModel[] newArray(int size) {
            return new RequestModel[size];
        }
    };
    private String idPasien;
    private String idNakes;
    private String nmPasien;
    private String alamatPasien;

    public RequestModel() {
    }

    public RequestModel(String idPasien, String idNakes, String nmPasien, String alamatPasien) {
        this.idPasien = idPasien;
        this.idNakes = idNakes;
        this.nmPasien = nmPasien;
        this.alamatPasien = alamatPasien;
    }

    protected RequestModel(Parcel in) {
        idPasien = in.readString();
        idNakes = in.readString();
        nmPasien = in.readString();
        alamatPasien = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(idPasien);
        parcel.writeString(idNakes);
        parcel.writeString(nmPasien);
        parcel.writeString(alamatPasien);
    }

    public String getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(String idPasien) {
        this.idPasien = idPasien;
    }

    public String getIdNakes() {
        return idNakes;
    }

    public void setIdNakes(String idNakes) {
        this.idNakes = idNakes;
    }

    public String getNmPasien() {
        return nmPasien;
    }

    public void setNmPasien(String nmPasien) {
        this.nmPasien = nmPasien;
    }

    public String getAlamatPasien() {
        return alamatPasien;
    }

    public void setAlamatPasien(String alamatPasien) {
        this.alamatPasien = alamatPasien;
    }
}
