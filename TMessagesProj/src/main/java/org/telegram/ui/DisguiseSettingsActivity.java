/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prism.commons.utils.RecentUtils;
import com.prism.commons.utils.ScreenSecurityUtils;
import com.prism.hider.telegram.DisguisePreference;
import com.prism.hider.vault.commons.FingerprintUtils;
import com.prism.hider.vault.commons.Vault;
import com.prism.hider.vault.commons.certifier.FingerprintCertifier;
import com.prism.lib.vault.signal.VaultVariant;

import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class DisguiseSettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private AlertDialog progressDialog;
    @SuppressWarnings("FieldCanBeLocal")
    private LinearLayoutManager layoutManager;


    private int disguiseSectionRow;
    private int disguiseModeRow;
    private int disguiseModeDetailRow;
    private int resetPinRow;
    private int useFingerprintRow;
    private int screenCaptureRow;
    private int hideFromRecentRow;

    private int rowCount;


    private boolean[] clear = new boolean[2];



    private Vault vault;
    private boolean isSupportFingerprint;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        updateRows();

        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    @Override
    public View createView(Context context) {


        vault = VaultVariant.instance();
        boolean isSetup = vault.isSetup(context);
        isSupportFingerprint  = FingerprintUtils.isSupportFingerprint(context);
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(getParentActivity().getString(R.string.common_setting_disguise_setting));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setVerticalScrollBarEnabled(false);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((view, position) -> {
            if (!view.isEnabled()) {
                return;
            }
            if (position == disguiseModeRow) {
                if (view instanceof TextCheckCell) {
                    TextCheckCell cell = (TextCheckCell) view;
                    cell.setChecked(!cell.isChecked());
                    if (cell.isChecked()) {
                        vault.setupVault(getParentActivity(), false);
                    } else {
                        vault.disableVault(getParentActivity());
                    }
                }

            } else if (position == useFingerprintRow) {
                if (view instanceof TextCheckCell) {
                    TextCheckCell cell = (TextCheckCell) view;
                    cell.setChecked(!cell.isChecked());
                    FingerprintCertifier.instance().setEnable(getParentActivity(), cell.isChecked());
                }
            } else if (position == resetPinRow) {
                vault.setupVault(getParentActivity(), true);
            } else if (position == screenCaptureRow) {
                if (view instanceof TextCheckCell) {
                    TextCheckCell cell = (TextCheckCell) view;
                    boolean newValue = !cell.isChecked();
                    cell.setChecked(newValue);
                    DisguisePreference.allowScreenCapture.get(getParentActivity()).save(newValue);
                    ScreenSecurityUtils.setScreenCaptureAllowed(getParentActivity(), newValue);
                }
            } else if (position == hideFromRecentRow) {
                if (view instanceof TextCheckCell) {
                    TextCheckCell cell = (TextCheckCell) view;
                    boolean newValue = !cell.isChecked();
                    cell.setChecked(newValue);
                    RecentUtils.setHideFromRecentEnable(getParentActivity(), newValue);
                    DisguisePreference.hideFromRecent.get(getParentActivity()).save(newValue);
                }
            }
        });

        return fragmentView;
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
    }

    private void updateRows() {
        rowCount = 0;
        disguiseSectionRow = rowCount++;
        disguiseModeRow = rowCount++;
        disguiseModeDetailRow = rowCount++;
        resetPinRow = rowCount++;
        useFingerprintRow = rowCount++;
        screenCaptureRow = rowCount++;
        hideFromRecentRow = rowCount++;

        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == disguiseModeRow ||
                    (position == resetPinRow && vault.isSetup(mContext) )||
                    position == useFingerprintRow && vault.isSetup(mContext) ||
                    position == screenCaptureRow || position == hideFromRecentRow
                    ;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(mContext);
                    break;
                case 2:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                default:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == resetPinRow) {
                        textCell.setText(mContext.getString(R.string.common_setting_reset_pin), false);
                    }
                    break;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == disguiseModeDetailRow) {
                        privacyCell.setText(mContext.getString(R.string.common_setting_desc_disguise_mode));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == disguiseSectionRow) {
                        headerCell.setText(mContext.getString(R.string.common_setting_disguise_setting));
                    }
                    break;
                case 3:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (position == disguiseModeRow) {
                        textCheckCell.setTextAndCheck(mContext.getString(R.string.common_setting_disguise_mode),
                                vault.isSetup(mContext), true);
                    } else if (position == useFingerprintRow) {
                        boolean useFingerprint = vault.isSetup(mContext) && isSupportFingerprint
                                && FingerprintCertifier.instance().isEnable(mContext);
                        textCheckCell.setTextAndCheck(mContext.getString(R.string.checkbox_use_fingerprint),
                                useFingerprint, true);
                    } else if (position == screenCaptureRow) {
                        boolean allowScreenCapture = DisguisePreference.allowScreenCapture.get(mContext).read();
                        textCheckCell.setTextAndCheck(mContext.getString(R.string.common_setting_allow_screen_capture),
                                allowScreenCapture, true);
                    } else if (position == hideFromRecentRow) {
                        boolean hideFromRecent = DisguisePreference.hideFromRecent.get(mContext).read();
                        textCheckCell.setTextAndCheck(mContext.getString(R.string.common_setting_desc_hide_from_recent),
                                hideFromRecent, false);

                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == resetPinRow) {
                return 0;
            } else if (position == disguiseModeDetailRow) {
                return 1;
            } else if (position == disguiseSectionRow) {
                return 2;
            } else if (position == disguiseModeRow || position == useFingerprintRow
                        || position == screenCaptureRow || position == hideFromRecentRow) {
                return 3;
            }
            return 0;
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        return themeDescriptions;
    }
}
