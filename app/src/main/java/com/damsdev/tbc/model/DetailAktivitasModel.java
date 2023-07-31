package com.damsdev.tbc.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DetailAktivitasModel implements Parcelable {
    private String idAktivitas;
    private String hariKe;
    private String tgl;
    private String status;

    protected DetailAktivitasModel(Parcel in) {
        idAktivitas = in.readString();
        hariKe = in.readString();
        tgl = in.readString();
        status = in.readString();
    }

    public static final Creator<DetailAktivitasModel> CREATOR = new Creator<DetailAktivitasModel>() {
        @Override
        public DetailAktivitasModel createFromParcel(Parcel in) {
            return new DetailAktivitasModel(in);
        }

        @Override
        public DetailAktivitasModel[] newArray(int size) {
            return new DetailAktivitasModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(idAktivitas);
        parcel.writeString(hariKe);
        parcel.writeString(tgl);
        parcel.writeString(status);
    }

    public String getIdAktivitas() {
        return idAktivitas;
    }

    public void setIdAktivitas(String idAktivitas) {
        this.idAktivitas = idAktivitas;
    }

    public String getHariKe() {
        return hariKe;
    }

    public void setHariKe(String hariKe) {
        this.hariKe = hariKe;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
