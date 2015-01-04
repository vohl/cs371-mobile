package amaturehour.androidtictactoe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by vohl on 1/3/15.
 */
public class Settings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("ttt_prefs");
        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences prefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        final ListPreference difficultyLevelPref = (ListPreference)findPreference("difficulty_level");
        String difficulty = prefs.getString("difficulty_level", getResources().getString(R.string.difficulty_hard));
        difficultyLevelPref.setSummary((CharSequence)difficulty);

        difficultyLevelPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                difficultyLevelPref.setSummary((CharSequence)newValue);

                //Since we are handling the pref, we must save it
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString("difficulty_level", newValue.toString());
                ed.commit();
                return true;
            }
        });

        final EditTextPreference victoryMessagePref = (EditTextPreference)findPreference("victory_message");
        String victory = prefs.getString("victory_message", getResources().getString(R.string.result_human_wins));
        victoryMessagePref.setSummary((CharSequence)victory);

        victoryMessagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                victoryMessagePref.setSummary((CharSequence)newValue);

                SharedPreferences.Editor ed = prefs.edit();
                ed.putString("victory_message", newValue.toString());
                ed.commit();
                return true;
            }
        });
    }
}
