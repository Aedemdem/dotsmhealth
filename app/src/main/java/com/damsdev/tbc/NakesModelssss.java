package com.damsdev.tbc;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.damsdev.tbc.model.NakesModel;

import java.util.HashMap;
import java.util.Map;

public class NakesModelssss implements Parcelable {
    public static final Creator<NakesModel> CREATOR = new Creator<NakesModel>() {
        @Override
        public NakesModel createFromParcel(Parcel in) {
            return new NakesModel(in);
        }

        @Override
        public NakesModel[] newArray(int size) {
            return new NakesModel[size];
        }
    };
    private Map<String, String> createdAt;
    private String nama;
    private String tglLahir;
    private String alamat;
    private String email;
    private String noHp;

    public NakesModelssss() {
    }

    public NakesModelssss(Map<String, String> createdAt, String nama, String tglLahir, String alamat, String email, String noHp) {
        this.createdAt = createdAt;
        this.nama = nama;
        this.tglLahir = tglLahir;
        this.alamat = alamat;
        this.email = email;
        this.noHp = noHp;
    }

    protected NakesModelssss(Parcel in) {
        createdAt = new HashMap<String, String>();
        in.readMap(createdAt, getClass().getClassLoader());
        nama = in.readString();
        tglLahir = in.readString();
        alamat = in.readString();
        email = in.readString();
        noHp = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeMap(createdAt);
        parcel.writeString(nama);
        parcel.writeString(tglLahir);
        parcel.writeString(alamat);
        parcel.writeString(email);
        parcel.writeString(noHp);
    }

    public Map<String, String> getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Map<String, String> createdAt) {
        this.createdAt = createdAt;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTglLahir() {
        return tglLahir;
    }

    public void setTglLahir(String tglLahir) {
        this.tglLahir = tglLahir;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }
}
