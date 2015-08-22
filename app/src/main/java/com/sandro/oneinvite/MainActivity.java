package com.sandro.oneinvite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.sandro.oneinvite.model.Result;
import com.sandro.oneinvite.receiver.AlarmReceiver;
import com.sandro.oneinvite.restclient.RestClient;
import com.sandro.oneinvite.sharedpreferences.SharedPreferencesManager;
import com.sandro.oneinvite.utils.InternetUtils;
import com.sandro.oneinvite.utils.NotificationSettingsUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private LinearLayout rootLayout;
    private TextView textViewLink;
    private TextView textViewPosition;
    private TextView textViewReferrals;
    private TextView textViewTotal;

    private AlarmReceiver alarmReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmReceiver = new AlarmReceiver();

        Button buttonShare = (Button) findViewById(R.id.activity_main_button_share);
        rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
        textViewLink = (TextView) findViewById(R.id.text_view_link);
        textViewPosition = (TextView) findViewById(R.id.text_view_position);
        textViewReferrals = (TextView) findViewById(R.id.text_view_referrals);
        textViewTotal = (TextView) findViewById(R.id.text_view_total);

        alarmReceiver.setAlarm(MainActivity.this);

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.activity_main_share_message));
                share.putExtra(Intent.EXTRA_TEXT, textViewLink.getText());

                startActivity(Intent.createChooser(share, getString(R.string.activity_main_share_message_action)));
            }
        });

        initToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshUserInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            refreshUserInfo();

            return true;
        }

        if (id == R.id.action_configure_userid) {
            configureUserId();

            return true;
        }

        if (id == R.id.action_configure_notifications) {
            new MaterialDialog.Builder(this)
                .title(R.string.activity_main_configure_notifications_dialog_title)
                .content(R.string.activity_main_configure_notifications_dialog_content)
                .items(R.array.notification_preference_values)
                .itemsCallbackSingleChoice(SharedPreferencesManager.getNotificationSettings(MainActivity.this), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {
                        //Save the current user selection
                        SharedPreferencesManager.setNotificationSettings(MainActivity.this, position);

                        //Handle the selection
                        alarmReceiver.cancelAlarm(MainActivity.this);

                        if (NotificationSettingsUtils.getNumberOfHoursBetweenCheck(position) != null) {
                            alarmReceiver.setAlarm(MainActivity.this);
                        }

                        return true;
                    }
                })
                .positiveText(R.string.global_confirm)
                .show();

            return true;
        }

        if (id == R.id.action_about) {
            new LibsBuilder()
                .withAboutDescription(getString(R.string.aboutlibraries_description))
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withActivityTheme(R.style.AppTheme)
                .withActivityTitle(getString(R.string.aboutlibraries_title))
                .start(this);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    private boolean isValidUserId() {
        if (TextUtils.isEmpty(SharedPreferencesManager.getUserId(MainActivity.this))) {
            Snackbar.make(rootLayout, getString(R.string.activity_main_snackbar_invalid_userid), Snackbar.LENGTH_LONG)
                .setAction(R.string.global_configure, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        configureUserId();
                    }
                })
                .show();

            return false;
        }

        return true;
    }


    private void configureUserId() {
        new MaterialDialog.Builder(this)
            .title(R.string.activity_main_configure_userid_dialog_title)
            .content(R.string.activity_main_configure_userid_dialog_description)
            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            .autoDismiss(false)
            .positiveText(R.string.global_confirm)
            .neutralText(R.string.activity_main_configure_email_dialog_register_email)
            .negativeText(R.string.global_dismiss)
            .input(getString(R.string.activity_main_configure_userid_dialog_hint), SharedPreferencesManager.getUserId(MainActivity.this), new MaterialDialog.InputCallback() {
                @Override
                public void onInput(MaterialDialog dialog, CharSequence input) {
                }
            }).callback(new MaterialDialog.ButtonCallback() {

            @Override
            public void onPositive(MaterialDialog dialog) {
                dialog.dismiss();
                SharedPreferencesManager.setUserId(MainActivity.this, dialog.getInputEditText().getText().toString().toUpperCase());
                refreshUserInfo();
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onNeutral(MaterialDialog dialog) {
                registerUserEmail();
                dialog.dismiss();
            }

        }).show();
    }

    private void refreshUserInfo() {

        if (InternetUtils.isInternetAvailable(MainActivity.this)) {

            if (isValidUserId()) {

                final MaterialDialog progressDialog = new MaterialDialog.Builder(this).title(R.string.global_loading)
                    .content(R.string.global_please_wait)
                    .progress(true, 0)
                    .show();

                new RestClient().getOnePlusService().getUserRank(SharedPreferencesManager.getUserId(MainActivity.this), new Callback<Result>() {

                    @Override
                    public void success(Result result, Response response) {
                        if (result.getData() == null) {
                            final Snackbar snackbar = Snackbar.make(rootLayout, getString(R.string.activity_main_error_userid_not_registered), Snackbar.LENGTH_LONG);

                            snackbar.setAction(R.string.global_register, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    registerUserEmail();
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                            progressDialog.dismiss();

                            return;
                        }

                        textViewReferrals.setText(result.getData().getRef_count());
                        textViewPosition.setText(result.getData().getRank());
                        textViewTotal.setText(result.getData().getTotal());
                        textViewLink.setText(String.format("https://oneplus.net/invites?kolid=%s", result.getData().getKid()));

                        SharedPreferencesManager.setUserPosition(MainActivity.this, result.getData().getRank());

                        progressDialog.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        Snackbar.make(rootLayout, getString(R.string.global_generic_error), Snackbar.LENGTH_SHORT);
                    }

                });
            }
        } else {
            textViewLink.setText(R.string.activity_main_error_internet_textviewlink);
            final Snackbar snackbar = Snackbar.make(rootLayout, getString(R.string.global_connect_to_internet), Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction(R.string.global_retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshUserInfo();

                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
    }

    private void registerUserEmail() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://oneplus.net/invites?kolid=6GRM1"));

        startActivity(browserIntent);
    }

}
