package com.egr423.sanitizor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class SettingsDialogue extends BottomSheetDialogFragment {


    private View view;

    public static boolean playGameAudio = true;
    public static boolean useDarkMode = true;
    public static boolean useGyroControls = false;

    private RadioGroup controlSchemeRadioGroup;
    private Button muteUnmuteButton;
    private Button darkModeLightModeButton;
    private Button clearLeaderboardButton;

    private static SharedPreferences sharedPref;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_layout, container, false);

        Context context = getActivity();
        sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);

        controlSchemeRadioGroup = view.findViewById(R.id.control_scheme);
        muteUnmuteButton = view.findViewById(R.id.mute_unmute_button);
        darkModeLightModeButton = view.findViewById(R.id.dark_mode_light_mode_button);
        clearLeaderboardButton = view.findViewById(R.id.clear_local_leaderboard_button);

        controlSchemeRadioGroup.setOnCheckedChangeListener(setOnCheckedChangeListener(controlSchemeRadioGroup.getId()));
        muteUnmuteButton.setOnClickListener(setOnClickListenerById(muteUnmuteButton.getId()));
        darkModeLightModeButton.setOnClickListener(setOnClickListenerById(darkModeLightModeButton.getId()));
        clearLeaderboardButton.setOnClickListener(setOnClickListenerById(clearLeaderboardButton.getId()));

        setDisplay(controlSchemeRadioGroup);
        setDisplay(muteUnmuteButton);
        setDisplay(darkModeLightModeButton);
        setDisplay(clearLeaderboardButton);

        return view;
    }

    private void setDisplay(View view) {
        switch (view.getId()) {
            case R.id.control_scheme:
                RadioGroup radioGroup = (RadioGroup) view;
                int check = sharedPref.getInt("controlScheme", -1);
                if (check != -1) {
                    radioGroup.check(check);
                } else {
                    radioGroup.clearCheck();
                }

            case R.id.mute_unmute_button:
                Button button = (Button) view;
                String buttonText = "Mute";
                if (!playGameAudio) {
                    buttonText = "Unmute";
                }
                button.setText(buttonText);

            case R.id.dark_mode_light_mode_button:

            case R.id.clear_local_leaderboard_button:

            default:
                throw new IllegalArgumentException("id should be an id one of the associated buttons" +
                        "of the SettingsDialogue");

        }
    }

    private RadioGroup.OnCheckedChangeListener setOnCheckedChangeListener(int id) {
        switch (id) {
            case R.id.control_scheme:
                return new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        sharedPref.edit().putInt("controlScheme", checkedId).apply();
                    }
                };
            default:
                throw new IllegalArgumentException("id should be an id one of the associated " +
                        "RadioButtonGroup of the SettingsDialogue");
        }
    }

    private View.OnClickListener setOnClickListenerById(int id) {

        switch (id) {
            case R.id.mute_unmute_button:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playGameAudio = !playGameAudio;
                        sharedPref.edit().putBoolean("PLAY_GAME_AUDIO", playGameAudio).apply();
                        String toastText = "Game audio has been turned on.";
                        if (!playGameAudio) {
                            toastText = "Game audio has been turned off.";
                        }
                        Context context = v.getContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, toastText, duration);
                        toast.show();
                        Log.d("SettingsDialogue",
                                String.format("SettingsDialogue.PLAY_GAME_AUDIO: %s",
                                        playGameAudio));
                        setDisplay(muteUnmuteButton);
                    }
                };

            case R.id.dark_mode_light_mode_button:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("SettingsDialogue",
                                String.format("SettingsDialogue.USE_DARK_MODE: %s",
                                        useDarkMode));
                        sharedPref.edit().putBoolean("USE_DARK_MODE", useDarkMode).apply();
                    }
                };

            case R.id.clear_local_leaderboard_button:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                };
            default:
                throw new IllegalArgumentException("id should be an id one of the associated buttons" +
                        "of the SettingsDialogue");
        }
    }
}
