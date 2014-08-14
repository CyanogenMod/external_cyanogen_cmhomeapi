package org.cyanogenmod.launcher.cardprovider;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import org.cyanogenmod.launcher.home.api.CMHomeApiManager;

public class ApiCardPackageChangedReceiver extends BroadcastReceiver {
    public static final String PACKAGE_CHANGED_DISABLE_PROVIDER = "providerDisabled";
    public static final String PACKAGE_CHANGED_ENABLE_PROVIDER = "providerEnabled";
    public static final String CMHOME_CONTENT_PROVIDER_NAME =
            "org.cyanogenmod.launcher.home.api.provider.CmHomeContentProvider";

    CMHomeApiManager mCardManager;

    public ApiCardPackageChangedReceiver(CMHomeApiManager cmHomeApiManager) {
        mCardManager = cmHomeApiManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String packageName = intent.getData().getSchemeSpecificPart();

        if (packageName == null || packageName.length() == 0) {
            // they sent us a bad intent
            return;
        }


        String action = intent.getAction();

        if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
            String[] changedComponents =
                    intent.getStringArrayExtra(Intent.EXTRA_CHANGED_COMPONENT_NAME_LIST);
            boolean isApiProvider = false;
            if (changedComponents != null) {
                for (String component : changedComponents) {
                    if (component.contains(CMHOME_CONTENT_PROVIDER_NAME)) {
                        isApiProvider = true;
                    }
                }
            }

            // The API Provider has changed
            if (isApiProvider) {
                PackageManager pm = context.getPackageManager();
                ComponentName componentName = new ComponentName(packageName,
                                                                CMHOME_CONTENT_PROVIDER_NAME);
                int enabledStatus = pm.getComponentEnabledSetting(componentName);

                if (enabledStatus == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                    enabledStatus == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
                    action = PACKAGE_CHANGED_ENABLE_PROVIDER;
                } else {
                    action = PACKAGE_CHANGED_DISABLE_PROVIDER;
                }
            }
        }
        mCardManager.onPackageChanged(action, packageName);
    }
}
