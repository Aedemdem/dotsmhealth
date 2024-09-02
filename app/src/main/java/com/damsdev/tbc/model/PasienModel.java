package com.damsdev.tbc.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class PasienModel implements Parcelable {
    //    private Map<String, String> createdAt;
    private String idPasien;
    private String nama;
    private String kelamin;
    private String tglLahir;
    private String alamat;
    private String noHp;
    private String email;
    private String idNakes;
    private String pendidikan;
    private String pekerjaan;
    private String mulaiPengobatan;

    public PasienModel() {
    }

    public PasienModel(
            String idPasien,
            String nama,
            String keelamin,
            String tglLahir,
            String alamat,
            String noHp,
            String email,
            String idNakes,
            String pendidikan,
            String pekerjaan,
            String mulaiPengobatan) {
//        this.createdAt = createdAt;
        this.idPasien = idPasien;
        this.nama = nama;
        this.kelamin = keelamin;
        this.tglLahir = tglLahir;
        this.alamat = alamat;
        this.noHp = noHp;
        this.email = email;
        this.idNakes = idNakes;
        this.pendidikan = pendidikan;
        this.pekerjaan = pekerjaan;
        this.mulaiPengobatan = mulaiPengobatan;
    }

    protected PasienModel(Parcel in) {
//        createdAt = in.readMap(createdAt,null);
//        createdAt = new HashMap<String, String>();
//        in.readMap(createdAt,getClass().getClassLoader());
        idPasien = in.readString();
        nama = in.readString();
        kelamin = in.readString();
        tglLahir = in.readString();
        alamat = in.readString();
        noHp = in.readString();
        email = in.readString();
        idNakes = in.readString();
        pendidikan = in.readString();
        pekerjaan = in.readString();
        mulaiPengobatan = in.readString();
    }

    public static final Creator<PasienModel> CREATOR = new Creator<PasienModel>() {
        @Override
        public PasienModel createFromParcel(Parcel in) {
            return new PasienModel(in);
        }

        @Override
        public PasienModel[] newArray(int size) {
            return new PasienModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
//        parcel.writeMap(createdAt);
        parcel.writeString(idPasien);
        parcel.writeString(nama);
        parcel.writeString(kelamin);
        parcel.writeString(tglLahir);
        parcel.writeString(alamat);
        parcel.writeString(noHp);
        parcel.writeString(email);
        parcel.writeString(idNakes);
        parcel.writeString(pendidikan);
        parcel.writeString(pekerjaan);
        parcel.writeString(mulaiPengobatan);
    }

//    public Map<String, String> getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(Map<String, String> createdAt) {
//        this.createdAt = createdAt;
//    }

    public String getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(String idPasien) {
        this.idPasien = idPasien;
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

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdNakes() {
        return idNakes;
    }

    public void setIdNakes(String idNakes) {
        this.idNakes = idNakes;
    }

    public String getPendidikan() {
        return pendidikan;
    }

    public void setPendidikan(String pendidikan) {
        this.pendidikan = pendidikan;
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public void setPekerjaan(String pekerjaan) {
        this.pekerjaan = pekerjaan;
    }

    public String getMulaiPengobatan() {
        return mulaiPengobatan;
    }

    public void setMulaiPengobatan(String mulaiPengobatan) {
        this.mulaiPengobatan = mulaiPengobatan;
    }
}
