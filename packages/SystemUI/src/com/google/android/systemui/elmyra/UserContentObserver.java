package com.google.android.systemui.elmyra;

import android.app.ActivityManager;
import android.app.SynchronousUserSwitchObserver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import java.util.function.Consumer;

public class UserContentObserver extends ContentObserver {
    private final Consumer<Uri> mCallback;
    private final Context mContext;
    private final Uri mSettingsUri;
    private final SynchronousUserSwitchObserver mUserSwitchCallback;

    /* renamed from: com.google.android.systemui.elmyra.UserContentObserver$1 */
    class C15861 extends SynchronousUserSwitchObserver {
        C15861() {
        }

        public void onUserSwitching(int i) throws RemoteException {
            UserContentObserver.this.updateContentObserver();
            UserContentObserver.this.mCallback.accept(UserContentObserver.this.mSettingsUri);
        }
    }

    public UserContentObserver(Context context, Uri uri, Consumer<Uri> consumer) {
        this(context, uri, consumer, true);
    }

    public UserContentObserver(Context context, Uri uri, Consumer<Uri> consumer, boolean z) {
        super(new Handler(context.getMainLooper()));
        this.mUserSwitchCallback = new C15861();
        this.mContext = context;
        this.mSettingsUri = uri;
        this.mCallback = consumer;
        if (z) {
            activate();
        }
    }

    private void updateContentObserver() {
        this.mContext.getContentResolver().unregisterContentObserver(this);
        this.mContext.getContentResolver().registerContentObserver(this.mSettingsUri, false, this, -2);
    }

    public void activate() {
        updateContentObserver();
        try {
            ActivityManager.getService().registerUserSwitchObserver(this.mUserSwitchCallback, "Elmyra/UserContentObserver");
        } catch (Throwable e) {
            Log.e("Elmyra/UserContentObserver", "Failed to register user switch observer", e);
        }
    }

    public void deactivate() {
        this.mContext.getContentResolver().unregisterContentObserver(this);
        try {
            ActivityManager.getService().unregisterUserSwitchObserver(this.mUserSwitchCallback);
        } catch (Throwable e) {
            Log.e("Elmyra/UserContentObserver", "Failed to unregister user switch observer", e);
        }
    }

    public void onChange(boolean z, Uri uri) {
        this.mCallback.accept(uri);
    }
}
