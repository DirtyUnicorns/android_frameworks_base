package com.google.android.systemui.elmyra.gates;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IActivityManager;
import android.app.TaskStackListener;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.os.Handler;
import android.util.Log;
import com.android.systemui.R;
import com.google.android.systemui.elmyra.actions.CameraAction;
import com.google.android.systemui.elmyra.gates.Gate.Listener;
import java.util.List;

public class CameraVisibility extends Gate {
    private final IActivityManager mActivityManager;
    private final CameraAction mCameraAction;
    private final String mCameraPackageName;
    private boolean mCameraShowing;
    private final Listener mGateListener = new C15962();
    private final KeyguardVisibility mKeyguardGate;
    private final PackageManager mPackageManager;
    private final PowerState mPowerState;
    private final TaskStackListener mTaskStackListener = new C15951();
    private final Handler mUpdateHandler;

    /* renamed from: com.google.android.systemui.elmyra.gates.CameraVisibility$1 */
    class C15951 extends TaskStackListener {
        C15951() {
        }

        public void onTaskStackChanged() {
            CameraVisibility.this.mUpdateHandler.post(new _$$Lambda$CameraVisibility$1$X_K32nTSgqALN1DA7GlsqyIM0Ns(CameraVisibility.this));
        }
    }

    /* renamed from: com.google.android.systemui.elmyra.gates.CameraVisibility$2 */
    class C15962 implements Listener {
        C15962() {
        }

        public void onGateChanged(Gate gate) {
            CameraVisibility.this.mUpdateHandler.post(new _$$Lambda$CameraVisibility$2$B_qu82ozOy_obNvcYz2PEaBQIyk(CameraVisibility.this));
        }
    }

    public CameraVisibility(Context context, CameraAction cameraAction) {
        super(context);
        this.mCameraAction = cameraAction;
        this.mPackageManager = context.getPackageManager();
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        this.mActivityManager = ActivityManager.getService();
        this.mKeyguardGate = new KeyguardVisibility(context);
        this.mPowerState = new PowerState(context);
        this.mKeyguardGate.setListener(this.mGateListener);
        this.mPowerState.setListener(this.mGateListener);
        this.mCameraPackageName = context.getResources().getString(R.string.google_camera_app_package_name);
        this.mUpdateHandler = new Handler(context.getMainLooper());
    }

    private boolean isCameraInForeground() {
        try {
            UserInfo currentUser = this.mActivityManager.getCurrentUser();
            int i = this.mPackageManager.getApplicationInfoAsUser(this.mCameraPackageName, 0, currentUser != null ? currentUser.id : 0).uid;
            List runningAppProcesses = this.mActivityManager.getRunningAppProcesses();
            int i2 = 0;
            while (i2 < runningAppProcesses.size()) {
                RunningAppProcessInfo runningAppProcessInfo = (RunningAppProcessInfo) runningAppProcesses.get(i2);
                if (runningAppProcessInfo.uid == i && runningAppProcessInfo.processName.equalsIgnoreCase(this.mCameraPackageName)) {
                    return runningAppProcessInfo.importance == 100;
                } else {
                    i2++;
                }
            }
        } catch (NameNotFoundException e) {
        } catch (Throwable e2) {
            Log.e("Elmyra/CameraVisibility", "Could not check camera foreground status", e2);
        }
        return false;
    }

    private boolean isCameraTopActivity() {
        try {
            List tasks = ActivityManager.getService().getTasks(1);
            return tasks.isEmpty() ? false : ((RunningTaskInfo) tasks.get(0)).topActivity.getPackageName().equalsIgnoreCase(this.mCameraPackageName);
        } catch (Throwable e) {
            Log.e("Elmyra/CameraVisibility", "unable to check task stack", e);
            return false;
        }
    }

    protected void updateCameraIsShowing() {
        boolean isCameraShowing = isCameraShowing();
        if (this.mCameraShowing != isCameraShowing) {
            this.mCameraShowing = isCameraShowing;
            notifyListener();
        }
    }

    protected boolean isBlocked() {
        return this.mCameraShowing && !this.mCameraAction.isAvailable();
    }

    public boolean isCameraShowing() {
        return isCameraTopActivity() && isCameraInForeground() && !this.mPowerState.isBlocking();
    }

    protected void onActivate() {
        this.mKeyguardGate.activate();
        this.mPowerState.activate();
        this.mCameraShowing = isCameraShowing();
        try {
            this.mActivityManager.registerTaskStackListener(this.mTaskStackListener);
        } catch (Throwable e) {
            Log.e("Elmyra/CameraVisibility", "Could not register task stack listener", e);
        }
    }

    protected void onDeactivate() {
        this.mKeyguardGate.deactivate();
        this.mPowerState.deactivate();
        try {
            this.mActivityManager.unregisterTaskStackListener(this.mTaskStackListener);
        } catch (Throwable e) {
            Log.e("Elmyra/CameraVisibility", "Could not unregister task stack listener", e);
        }
    }
}
