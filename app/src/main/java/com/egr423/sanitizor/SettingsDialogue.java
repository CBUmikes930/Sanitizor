package com.egr423.sanitizor;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SettingsDialogue extends BottomSheetDialogFragment {


    private static final String PREFERENCE_FILE = "app_settings";

    public static Boolean playGameAudio;
    public static Boolean useDarkMode;
    public static Integer controlScheme;

    private RadioGroup controlSchemeRadioGroup;
    private Button muteUnmuteButton;
    private Button darkModeLightModeButton;
    private Button clearLeaderboardButton;

    private static SharedPreferences sharedPref;


    /**
     * Added by Chase Crossley on 10/6/2020
     *
     * inflates the settings_layout in res/layouts and morphs the layout into a modal sheet
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_layout, container, false);

        //get references to all all the button and objects in modal sheet
        controlSchemeRadioGroup = view.findViewById(R.id.control_scheme);
        muteUnmuteButton = view.findViewById(R.id.mute_unmute_button);
        darkModeLightModeButton = view.findViewById(R.id.dark_mode_light_mode_button);
        clearLeaderboardButton = view.findViewById(R.id.clear_local_leaderboard_button);

        //set the on click listeners for each button or possible preference
        controlSchemeRadioGroup.setOnCheckedChangeListener(setOnCheckedChangeListener(controlSchemeRadioGroup.getId()));
        muteUnmuteButton.setOnClickListener(setOnClickListenerById(muteUnmuteButton.getId()));
        darkModeLightModeButton.setOnClickListener(setOnClickListenerById(darkModeLightModeButton.getId()));
        clearLeaderboardButton.setOnClickListener(setOnClickListenerById(clearLeaderboardButton.getId()));

        //set the display to represent the current state of the buttons
        setDisplay(controlSchemeRadioGroup);
        setDisplay(muteUnmuteButton);
        setDisplay(darkModeLightModeButton);
        setDisplay(clearLeaderboardButton);

        return view;
    }

    /**
     * Added by Chase Crossley on 10/6/2020
     *
     * this needs to be overridden to allow the modal sheet to be fully expanded upon being shown
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    /**
     * Added by Chase Crossley on 10/6/2020
     *
     * a method to allows for preference button to display the correct setting
     * @param view: any object in relation to the preference screen
     * @throws IllegalArgumentException: if the view passed
     */
    // allows for preference button to display the correct setting
    private void setDisplay(View view) {
        switch (view.getId()) {
            case R.id.control_scheme:
                RadioGroup radioGroup = (RadioGroup) view;
                int check = sharedPref.getInt("controlScheme", R.id.gyro_controls);
                if (check != -1) {
                    radioGroup.check(check);
                } else {
                    radioGroup.clearCheck();
                }
                return;

            case R.id.mute_unmute_button:
                Button unMuteButton = (Button) view;
                String unMuteButtonText = "Mute";
                if (!playGameAudio) {
                    unMuteButtonText = "Unmute";
                }
                unMuteButton.setText(unMuteButtonText);
                return;

            case R.id.dark_mode_light_mode_button:
                Button darkModeButton = (Button) view;
                String darkModeButtonText = "Light Mode";
                if (!useDarkMode) {
                    darkModeButtonText = "Dark Mode";
                }
                darkModeButton.setText(darkModeButtonText);
                return;

            case R.id.clear_local_leaderboard_button:
                Button clearLeaderBoardButton = (Button) view;
                //todo dynamically set style?????????
                return;


            default:
                throw new IllegalArgumentException("id should be an id one of the associated buttons" +
                        "of the SettingsDialogue");

        }
    }

    /**
     * Added by Chase Crossley on 10/5/2020
     *
     * a method to set the onCheckedChangeListener for RadioGroups of SettingsDialogue Class
     * @param id: should correspond to any RadioGroup Preference id
     * @throws IllegalArgumentException: if id passed in is not a correct RadioGroup id associated
     * with the SettingsDialogue Class
     */
    private RadioGroup.OnCheckedChangeListener setOnCheckedChangeListener(int id) {
        switch (id) {
            case R.id.control_scheme:
                return new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        sharedPref.edit().putInt("controlScheme", checkedId).apply();
                        controlScheme = checkedId;
                    }

                };
            default:
                throw new IllegalArgumentException("id should be an id one of the associated " +
                        "RadioButtonGroup of the SettingsDialogue");
        }
    }

    /**
     * Added by Chase Crossley on 10/5/2020
     *
     * a method to set the setOnClickListener for Buttons of SettingsDialogue Class
     * @param id: should correspond to any Button preference id
     * @throws IllegalArgumentException: if id passed in is not a correct Button id associated
     *      * with the SettingsDialogue Class
     */
    private View.OnClickListener setOnClickListenerById(int id) {

        switch (id) {
            case R.id.mute_unmute_button:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playGameAudio = !playGameAudio;
                        onClickSettingsButton((Button) v, playGameAudio, "playGameAudio",
                                "Game audio has been turned");
                        setDisplay(muteUnmuteButton);
                    }
                };

            case R.id.dark_mode_light_mode_button:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        useDarkMode = !useDarkMode;
                        onClickSettingsButton((Button) v, useDarkMode, "useDarkMode",
                                "Dark Mode has been turned");
                        setDisplay(darkModeLightModeButton);
                    }
                };

            case R.id.clear_local_leaderboard_button:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendToast(v, "Local Database has been cleared.");
                        setDisplay(clearLeaderboardButton);
                    }
                };
            default:
                throw new IllegalArgumentException("id should be an id one of the associated buttons" +
                        "of the SettingsDialogue");
        }
    }


    /**
     * Added by Chase Crossley on 10/6/2020
     *
     * a helper method to for boolean onClickButtonListeners, reduced some redundancy
     * @param button: should correspond to any boolean button of the SettingsDialogue Class
     * @param booleanField: should correspond to the value of a changed boolean field in SettingsDialogue Class
     * @param key: should correspond to the sharedPreference key of the passed in booleanField
     * @param toastText: text that will let the user know how the preference has been changed
     */
    private void onClickSettingsButton(Button button, boolean booleanField, String key, String toastText) {
        sharedPref.edit().putBoolean(key, booleanField).apply();
        if (!booleanField) {
            toastText += " off.";
        } else {
            toastText += " on.";
        }
        sendToast(button, toastText);
        Log.d("SettingsDialogue",
                String.format("SettingsDialogue.%s: %s",
                        key, booleanField));
    }

    /**
     * Added by Chase Crossley on 10/6/2020
     *
     * helper class to reduce redundancy to display a toast message for a short time
     * @param v: allows for the context of the toast message
     * @param toastText: text that will let the user know how the preference has been changed
     */
    private void sendToast(View v, CharSequence toastText) {
        Context context = v.getContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, toastText, duration);
        toast.show();
    }

    /**
     * Added by Chase Crossley on 10/6/2020
     *
     * a helper method made to initialize the preferences in the SettingsDialogue class should only
     * be called once in MainActivity.java
     * @param context: allows for the ability to initialize the preferences in the
     */
    public static void initialize(Context context) {
        sharedPref = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        controlScheme = sharedPref.getInt("controlScheme", R.id.gyro_controls);
        playGameAudio = sharedPref.getBoolean("playGameAudio", true);
        useDarkMode = sharedPref.getBoolean("useDarkMode", true);
    }

}
