package com.damsdev.tbc.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Map;

public class NakesModel implements Parcelable {
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

    private Long createdAt;
    private String idNakes;
    private String nama;
    private String kelamin;
    private String tglLahir;
    private String alamat;
    private String email;
    private String noHp;

    public NakesModel() {
    }

    public NakesModel(String idNakes, String nama, String kelamin, String tglLahir, String alamat, String email, String noHp) {
        this.idNakes = idNakes;
        this.nama = nama;
        this.kelamin = kelamin;
        this.tglLahir = tglLahir;
        this.alamat = alamat;
        this.email = email;
        this.noHp = noHp;
    }

    public NakesModel(Parcel in) {
        idNakes = in.readString();
        nama = in.readString();
        kelamin = in.readString();
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
        parcel.writeString(idNakes);
        parcel.writeString(nama);
        parcel.writeString(kelamin);
        parcel.writeString(tglLahir);
        parcel.writeString(alamat);
        parcel.writeString(email);
        parcel.writeString(noHp);
    }

    public Map<String, String> getCreatedAt() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long getCratedAtLong() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getIdNakes() {
        return idNakes;
    }

    public void setIdNakes(String idNakes) {
        this.idNakes = idNakes;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKelamin() {
        return kelamin;
    }

    public void setKelamin(String kelamin) {
        this.kelamin = kelamin;
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
