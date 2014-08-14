package org.cyanogenmod.launcher.cardprovider;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.util.Log;
import org.cyanogenmod.launcher.home.api.CMHomeApiManager;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContentProvider;

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

        if (Intent.ACTION_PACKAGE_CHANGED.equals(action) ||
            Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
            String providerName = packageName + CmHomeApiCardProvider.CARD_AUTHORITY_APPEND_STRING;
            ProviderInfo info = context.getPackageManager().resolveContentProvider(providerName,
                                                                                   0);
            if (info != null) {
                action = PACKAGE_CHANGED_ENABLE_PROVIDER;
            } else {
                action = PACKAGE_CHANGED_DISABLE_PROVIDER;
            }
        }
        mCardManager.onPackageChanged(action, packageName);
    }
}
