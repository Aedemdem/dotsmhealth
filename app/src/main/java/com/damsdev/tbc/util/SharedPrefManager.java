package com.damsdev.tbc.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    public static final String  SP_APP_NAME = "spAppName";
    public static final String  SP_ID_NAKES = "spIdNakes";
    public static final String  SP_ID_PASIEN = "spIdPasien";
    public static final String  SP_ID_AKTIVITAS = "spIdAktivitas";
    public static final String  SP_TERAKHIR_POST = "spTerakhirPost";
    public static final String  SP_IS_ALARM_AKTIF = "spIsAlarmAktif";
    public static final String  SP_ALARM_HOUR = "spAlarmHour";
    public static final String  SP_ALARM_MINUTS = "spAlarmMinutes";
    public static final String  SP_TGL_MULAI = "spTglMulai";
    public static final String  SP_TGL_SELESAI = "spTglSelesai";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context) {
        sp = context.getSharedPreferences(SP_APP_NAME, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveString(String keySP, String value) {
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value) {
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value) {
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public void clearAll() {
        spEditor.clear();
    }

    public String getSpIdNakes() {
        return sp.getString(SP_ID_NAKES, "");
    }

    public String getSpIdPasien() {
        return sp.getString(SP_ID_PASIEN, "");
    }

    public String getSpIdAktivitas() {
        return sp.getString(SP_ID_AKTIVITAS, "");
    }

    public String getSpTerakhirPost() {
        return sp.getString(SP_TERAKHIR_POST, "");
    }

    public String getSpIsAlarmAktif() {
        return sp.getString(SP_IS_ALARM_AKTIF, "false");
    }

    public String getSpAlarmHour() {
        return sp.getString(SP_ALARM_HOUR, "8");
    }

    public String getSpAlarmMinuts() {
        return sp.getString(SP_ALARM_MINUTS, "0");
    }

    public String getSpTglMulai() {
        return sp.getString(SP_TGL_MULAI, "0-0-0");
    }

    public String getSpTglSelesai() {
        return sp.getString(SP_TGL_SELESAI, "0-0-0");
    }

}
