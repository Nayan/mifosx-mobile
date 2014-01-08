package org.mifosx.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosx.mobile.db.DataBaseHandler;
import org.mifosx.mobile.util.Constants;
import org.mifosx.mobile.util.UserFunctions;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity {

    private UserLoginTask mAuthTask = null;

    private String mMifosInstance;
    private String mUsername;
    private String mPassword;

    private ProgressDialog mProgressDialog;
    private EditText mMifosInstanceView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mMifosInstanceView = (EditText) findViewById(R.id.mifosInstance);

        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        mMifosInstance = mMifosInstanceView.getText().toString();
        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        //TODO: check for valid mifos instance url

        if (cancel) {
            focusView.requestFocus();
        } else {
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            mProgressDialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
            mProgressDialog.setProgressStyle(mProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Loging in..");
            mProgressDialog.setCancelable(false);

            Map loginParams = new HashMap();
            loginParams.put(Constants.MIFOS_INSTANCE, mMifosInstance);
            loginParams.put(Constants.USER_NAME, mUsername);
            loginParams.put(Constants.PASSWORD, mPassword);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute(loginParams);
        }
    }

    public class UserLoginTask extends AsyncTask<Map, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Map... params) {
            Map<String, String> loginParams = params[0];
            UserFunctions userFunctions = new UserFunctions();
            JSONObject result = userFunctions.loginUser(loginParams.get(Constants.MIFOS_INSTANCE), loginParams.get(Constants.USER_NAME), loginParams.get(Constants.PASSWORD));
            if (!result.isNull("base64EncodedAuthenticationKey")) {
                DataBaseHandler db = new DataBaseHandler(getApplicationContext());
                try {
                    db.addUser(result.getString(Constants.KEY_USERID),result.getString(Constants.KEY_USERID),result.getString(Constants.KEY_USERID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            mProgressDialog.dismiss();

            if (success) {
                Intent main = new Intent(getApplicationContext(),
                        MainActivity.class);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mProgressDialog.dismiss();
        }
    }
}
