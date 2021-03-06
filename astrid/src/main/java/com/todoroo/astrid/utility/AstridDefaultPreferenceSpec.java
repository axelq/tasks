package com.todoroo.astrid.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;

import com.todoroo.andlib.service.ContextManager;
import com.todoroo.andlib.utility.Preferences;
import com.todoroo.astrid.activity.BeastModePreferences;
import com.todoroo.astrid.core.SortHelper;
import com.todoroo.astrid.data.Task;

import org.joda.time.DateTime;
import org.tasks.R;

public class AstridDefaultPreferenceSpec extends AstridPreferenceSpec {

    public static interface PreferenceExtras {
        public void setExtras(Context context);
    }

    @Override
    public void setIfUnset() {
        PreferenceExtras extras = new PreferenceExtras() {
            @Override
            public void setExtras(Context context) {
                String dragDropTestInitialized = "android_drag_drop_initialized"; //$NON-NLS-1$
                if (!Preferences.getBoolean(dragDropTestInitialized, false)) {
                    SharedPreferences publicPrefs = AstridPreferences.getPublicPrefs(context);
                    if (publicPrefs != null) {
                        Editor edit = publicPrefs.edit();
                        if (edit != null) {
                            edit.putInt(SortHelper.PREF_SORT_FLAGS, SortHelper.FLAG_DRAG_DROP);
                            edit.putInt(SortHelper.PREF_SORT_SORT, SortHelper.SORT_AUTO);
                            edit.commit();
                            Preferences.setInt(AstridPreferences.P_SUBTASKS_HELP, 1);
                        }
                    }
                    Preferences.setBoolean(dragDropTestInitialized, true);
                }
                BeastModePreferences.setDefaultOrder(context);
            }
        };

        setPrefs(extras);
    }

    private static void setPrefs(PreferenceExtras extras) {
        Context context = ContextManager.getContext();
        SharedPreferences prefs = Preferences.getPrefs(context);
        Editor editor = prefs.edit();
        Resources r = context.getResources();

        setPreference(prefs, editor, r, R.string.p_default_urgency_key, 0);
        setPreference(prefs, editor, r, R.string.p_default_importance_key, 2);
        setPreference(prefs, editor, r, R.string.p_default_hideUntil_key, 0);
        setPreference(prefs, editor, r, R.string.p_default_reminders_key, Task.NOTIFY_AT_DEADLINE | Task.NOTIFY_AFTER_DEADLINE);
        setPreference(prefs, editor, r, R.string.p_rmd_default_random_hours, 0);
        setPreference(prefs, editor, r, R.string.p_fontSize, 16);
        setPreference(prefs, editor, r, R.string.p_showNotes, false);

        setPreference(prefs, editor, r, R.string.p_field_missed_calls, true);

        setPreference(prefs, editor, r, R.string.p_end_at_deadline, true);

        setPreference(prefs, editor, r, R.string.p_rmd_persistent, true);

        setPreference(prefs, editor, r, R.string.p_show_today_filter, true);
        setPreference(prefs, editor, r, R.string.p_show_recently_modified_filter, true);
        setPreference(prefs, editor, r, R.string.p_show_not_in_list_filter, true);

        setPreference(prefs, editor, r, R.string.p_calendar_reminders, true);

        setPreference(prefs, editor, r, R.string.p_use_dark_theme, false);

        setPreference(prefs, editor, r, R.string.p_force_phone_layout, false);

        setPreference(prefs, editor, r, R.string.p_show_quickadd_controls, true);

        setPreference(prefs, editor, r, R.string.p_show_task_edit_comments, true);

        setPreference(prefs, editor, r, R.string.p_taskRowStyle_v2, "1"); //$NON-NLS-1$

        setPreference(prefs, editor, r, R.string.p_use_date_shortcuts, false);

        setPreference(prefs, editor, r, R.string.p_save_and_cancel, false);

        setPreference(prefs, editor, r, R.string.p_hide_plus_button, true);

        extras.setExtras(context);

        migrateToNewQuietHours();

        editor.commit();
    }

    static void migrateToNewQuietHours() {
        boolean hasMigrated = Preferences.getBoolean(R.string.p_rmd_hasMigrated, false);

        if(!hasMigrated) {
            boolean quietHoursEnabled = Preferences.getIntegerFromString(R.string.p_rmd_quietStart_old, -1) >= 0;
            Preferences.setBoolean(R.string.p_rmd_enable_quiet, quietHoursEnabled);

            if (quietHoursEnabled) {
                setTime(R.string.p_rmd_quietStart_old, R.string.p_rmd_quietStart, 22);
                setTime(R.string.p_rmd_quietEnd_old, R.string.p_rmd_quietEnd, 10);
            }
            setTime(R.string.p_rmd_time_old, R.string.p_rmd_time, 18);
            Preferences.setBoolean(R.string.p_rmd_hasMigrated, true);
        }
    }

    private static void setTime(int oldResourceId, int newResourceId, int defValue) {
        int hour = Preferences.getIntegerFromString(oldResourceId, defValue);
        int millisOfDay = new DateTime().withMillisOfDay(0).withHourOfDay(hour).getMillisOfDay();
        Preferences.setInt(newResourceId, millisOfDay);
    }
}
