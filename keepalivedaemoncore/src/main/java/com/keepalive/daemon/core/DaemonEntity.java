package com.keepalive.daemon.core;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

public class DaemonEntity implements Parcelable {
    public String[] args;
    public String niceName;

    public Intent serviceIntent;
    public Intent broadcastIntent;
    public Intent instrumentationIntent;

    public static final Creator<DaemonEntity> CREATOR = new Creator<DaemonEntity>() {
        @Override
        public DaemonEntity createFromParcel(Parcel parcel) {
            return new DaemonEntity(parcel);
        }

        @Override
        public DaemonEntity[] newArray(int i) {
            return new DaemonEntity[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public DaemonEntity() {
    }

    protected DaemonEntity(Parcel parcel) {
        args = parcel.createStringArray();
        niceName = parcel.readString();
        if (parcel.readInt() != 0) {
            serviceIntent = Intent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            broadcastIntent = Intent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            instrumentationIntent = Intent.CREATOR.createFromParcel(parcel);
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(args);
        parcel.writeString(niceName);
        if (serviceIntent == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            serviceIntent.writeToParcel(parcel, i);
        }
        if (broadcastIntent == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            broadcastIntent.writeToParcel(parcel, i);
        }
        if (instrumentationIntent == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            instrumentationIntent.writeToParcel(parcel, i);
        }
    }

    public static DaemonEntity create(String str) {
        byte[] decode = Base64.decode(str, 2);
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(decode, 0, decode.length);
        obtain.setDataPosition(0);
        return CREATOR.createFromParcel(obtain);
    }

    @Override
    public String toString() {
        Parcel obtain = Parcel.obtain();
        writeToParcel(obtain, 0);
        String encodeToString = Base64.encodeToString(obtain.marshall(), 2);
        obtain.recycle();
        return encodeToString;
    }
}
