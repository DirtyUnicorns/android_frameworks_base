package com.google.android.systemui.elmyra;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IElmyraServiceGestureListener extends IInterface {

    public static abstract class Stub extends Binder implements IElmyraServiceGestureListener {

        private static class Proxy implements IElmyraServiceGestureListener {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onGestureDetected() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraServiceGestureListener");
                    this.mRemote.transact(2, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void onGestureProgress(float f, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraServiceGestureListener");
                    obtain.writeFloat(f);
                    obtain.writeInt(i);
                    this.mRemote.transact(1, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static IElmyraServiceGestureListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.systemui.elmyra.IElmyraServiceGestureListener");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IElmyraServiceGestureListener)) ? new Proxy(iBinder) : (IElmyraServiceGestureListener) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface("com.google.android.systemui.elmyra.IElmyraServiceGestureListener");
                        onGestureProgress(parcel.readFloat(), parcel.readInt());
                        return true;
                    case 2:
                        parcel.enforceInterface("com.google.android.systemui.elmyra.IElmyraServiceGestureListener");
                        onGestureDetected();
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            }
            parcel2.writeString("com.google.android.systemui.elmyra.IElmyraServiceGestureListener");
            return true;
        }
    }

    void onGestureDetected() throws RemoteException;

    void onGestureProgress(float f, int i) throws RemoteException;
}
