/*
 * Copyright (C) 2015 TeamEos project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Simple preference class implementing ActionHolder interface to assign
 * actions to buttons. It is ABSOLUTELY IMPERITIVE that the preference
 * key is identical to the target ConfigMap tag in ActionConstants 
 */

package org.carbonrom.carbonfibers.preferences;

import java.util.Map;

import com.android.internal.util.hwkeys.ActionConstants.ConfigMap;
import com.android.internal.util.hwkeys.ActionConstants.Defaults;
import com.android.internal.util.hwkeys.ActionHolder;
import com.android.internal.util.hwkeys.Config.ActionConfig;
import com.android.internal.util.hwkeys.Config.ButtonConfig;

import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.Utils;

public class ActionPreference extends Preference implements ActionHolder {
    private Defaults mDefaults;
    private ConfigMap mMap;
    private ActionConfig mAction;
    private ActionConfig mDefaultAction;

    public ActionPreference(Context context) {
        this(context, null);
    }
    private final View.OnClickListener mClickListener = v -> performClick(v);

    private boolean mAllowDividerAbove;
    private boolean mAllowDividerBelow;

    private IOverlayManager mOverlayManager;

     public ActionPreference(Context context, AttributeSet attrs) {
         super(context, attrs);
 
        mOverlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE));

         TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Preference);
 
         mAllowDividerAbove = TypedArrayUtils.getBoolean(a, R.styleable.Preference_allowDividerAbove,
                 R.styleable.Preference_allowDividerAbove, false);
         mAllowDividerBelow = TypedArrayUtils.getBoolean(a, R.styleable.Preference_allowDividerBelow,
                 R.styleable.Preference_allowDividerBelow, false);
         a.recycle();
 
         setLayoutResource(R.layout.action_preference);
     }

    public ActionPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ActionPreference(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs);
    }

    @Override
    public String getTag() {
        return this.getKey();
    }

    @Override
    public void setTag(String tag) {
        this.setKey(tag);
    }

    @Override
    public Defaults getDefaults() {
        return mDefaults;
    }

    @Override
    public void setDefaults(Defaults defaults) {
        mDefaults = defaults;
        final String tag = this.getKey();
        for (Map.Entry<String, ConfigMap> entry : defaults.getActionMap().entrySet()) {
            if (((String) entry.getKey()).equals(tag)) {
                mMap = entry.getValue();
                break;
            }
        }
    }

    @Override
    public ConfigMap getConfigMap() {
        return mMap;
    }

    @Override
    public void setConfigMap(ConfigMap map) {
        mMap = map;
    }

    @Override
    public ButtonConfig getButtonConfig() {
        return null;
    }

    @Override
    public void setButtonConfig(ButtonConfig button) {
    }

    @Override
    public ActionConfig getActionConfig() {
        return mAction;
    }

    @Override
    public void setActionConfig(ActionConfig action) {
        mAction = action;
        this.setSummary(action.getLabel());
    }

    @Override
    public ButtonConfig getDefaultButtonConfig() {
        return null;
    }

    @Override
    public void setDefaultButtonConfig(ButtonConfig button) {

    }

    @Override
    public ActionConfig getDefaultActionConfig() {
        return mDefaultAction;
    }

    @Override
    public void setDefaultActionConfig(ActionConfig action) {
        mDefaultAction = action;
    }
 
    private boolean isUsingWhiteAccent() {
        OverlayInfo themeInfo = null;
        try {
            themeInfo = mOverlayManager.getOverlayInfo("com.accents.white",
                    UserHandle.USER_CURRENT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return themeInfo != null && themeInfo.isEnabled();
    }
}
