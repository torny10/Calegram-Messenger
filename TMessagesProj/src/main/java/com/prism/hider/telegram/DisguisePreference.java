package com.prism.hider.telegram;

import com.prism.commons.model.PreferenceModelHolder;
import com.prism.commons.trigger.TriggerProcess;
import com.prism.commons.utils.InitOnceP;
import com.prism.commons.utils.PreferenceWrapper;

public class DisguisePreference {


    private static final String PREFERENCE_NAME = "disguise_preference";


    private static InitOnceP<PreferenceWrapper, Void> wrapperHolder = new InitOnceP<PreferenceWrapper, Void>(
            v -> new PreferenceWrapper(PREFERENCE_NAME));

    private static final String ALLOW_SCREEN_CAPTURE = "ALLOW_SCREEN_CAPTURE";
    public static PreferenceModelHolder<Boolean> allowScreenCapture = new PreferenceModelHolder<>(
            wrapperHolder.get(null), ALLOW_SCREEN_CAPTURE, true,
            Boolean.class);

    private static final String HIDE_FROM_RECENT = "HIDE_FROM_RECENT";
    public static PreferenceModelHolder<Boolean> hideFromRecent = new PreferenceModelHolder<>(
            wrapperHolder.get(null), HIDE_FROM_RECENT, false,
            Boolean.class);

    private static final String DISGUISE_HINT_COUNT = "disguise_hint_count";
    public static PreferenceModelHolder<Integer> disguiseHintCount = new PreferenceModelHolder<>(
            wrapperHolder.get(null), DISGUISE_HINT_COUNT, 0,
            Integer.class);


}
