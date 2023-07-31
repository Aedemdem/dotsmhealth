package com.damsdev.tbc.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AktivitasDetailModel implements Parcelable {
//    private Map<String, String> cretedAt;
    private String idAktivitas;
    private String hari;
    private String tgl;
    // minum/tidak
    private String status;

    public AktivitasDetailModel() {
    }

    public AktivitasDetailModel(String idAktivitas, String hari, String tgl, String status) {
        this.idAktivitas = idAktivitas;
        this.hari = hari;
        this.tgl = tgl;
        this.status = status;
    }

    protected AktivitasDetailModel(Parcel in) {
        idAktivitas = in.readString();
        hari = in.readString();
        tgl = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idAktivitas);
        dest.writeString(hari);
        dest.writeString(tgl);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AktivitasDetailModel> CREATOR = new Creator<AktivitasDetailModel>() {
        @Override
        public AktivitasDetailModel createFromParcel(Parcel in) {
            return new AktivitasDetailModel(in);
        }

        @Override
        public AktivitasDetailModel[] newArray(int size) {
            return new AktivitasDetailModel[size];
        }
    };

    public String getIdAktivitas() {
        return idAktivitas;
    }

    public void setIdAktivitas(String idAktivitas) {
        this.idAktivitas = idAktivitas;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
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
