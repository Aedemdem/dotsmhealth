package com.damsdev.tbc.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class AktivitasModel implements Parcelable {
//    private Map<String, String> cretedAt;
    private String idPasien;
    private String total;
    private String tglMulai;
    private String tglSelesai;

    public AktivitasModel() {
    }

    public AktivitasModel(String idPasien, String total, String tglMulai, String tglSelesai) {
        this.idPasien = idPasien;
        this.total = total;
        this.tglMulai = tglMulai;
        this.tglSelesai = tglSelesai;
    }


    protected AktivitasModel(Parcel in) {
        idPasien = in.readString();
        total = in.readString();
        tglMulai = in.readString();
        tglSelesai = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idPasien);
        dest.writeString(total);
        dest.writeString(tglMulai);
        dest.writeString(tglSelesai);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AktivitasModel> CREATOR = new Creator<AktivitasModel>() {
        @Override
        public AktivitasModel createFromParcel(Parcel in) {
            return new AktivitasModel(in);
        }

        @Override
        public AktivitasModel[] newArray(int size) {
            return new AktivitasModel[size];
        }
    };

    public String getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(String idPasien) {
        this.idPasien = idPasien;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTglMulai() {
        return tglMulai;
    }

    public void setTglMulai(String tglMulai) {
        this.tglMulai = tglMulai;
    }

    public String getTglSelesai() {
        return tglSelesai;
    }

    public void setTglSelesai(String tglSelesai) {
        this.tglSelesai = tglSelesai;
    }
}
