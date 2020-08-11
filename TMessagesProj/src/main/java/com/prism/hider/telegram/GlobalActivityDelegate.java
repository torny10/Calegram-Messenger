package com.prism.hider.telegram;

import android.app.Activity;

import com.prism.commons.utils.RecentUtils;
import com.prism.hider.vault.commons.Vault;
import com.prism.hider.vault.utils.SimpleVaultActivityDelegate;
import com.prism.lib.vault.signal.VaultVariant;

public class GlobalActivityDelegate extends SimpleVaultActivityDelegate {


    @Override
    public boolean onCreate(Activity context) {
        boolean r = super.onCreate(context);
        RecentUtils.setHideFromRecentEnable(context, DisguisePreference.hideFromRecent.get(context).read());
        return r;

    }

    @Override
    public Vault getVault(Activity activity) {
        return VaultVariant.instance();
    }
}
