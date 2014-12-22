package sysu.project.lee.sportslife.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.User.UserEntity;
import sysu.project.lee.sportslife.Utils.ToastUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private Button clearDB = null;
    private Button testSkip = null;
    private CheckBox cbRememberPassWord = null;
    private CheckBox cbAutoLogin = null;
    private View btnToRegisterView = null;

    private SharedPreferences sp = null;

    private final int PASSWORD_ERROR = 0;
    private final int EMAIL_NOT_EXIST = 1;
    private final int LOGIN_SUCCESS =2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        initViews();
        populateAutoComplete();
        initListener();
        testButtons();

    }

    private void initListener() {
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

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        btnToRegisterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cbRememberPassWord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    if(sp.edit().putBoolean("PASSWORD_REMEMBER", true).commit()){
                        ToastUtils.show(LoginActivity.this, "已经记住密码");
                    }else{
                        ToastUtils.show(LoginActivity.this, "记住密码失败");
                    }

                }else {
                    if (sp.edit().putBoolean("PASSWORD_REMEMBER", false).commit()){
                        ToastUtils.show(LoginActivity.this, "取消记住密码");
                    }else{
                        ToastUtils.show(LoginActivity.this, "取消记住密码失败");
                    }
                }


            }
        });

        cbAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                cbRememberPassWord.setChecked(isChecked);
                if (isChecked){
                    if (sp.edit().putBoolean("AUTO_LOGIN", true).commit()){
                        ToastUtils.show(LoginActivity.this, "下一次将自动登录");
                    }else{
                        ToastUtils.show(LoginActivity.this, "自动登录设置失败");
                    }
                }else {
                    if (sp.edit().putBoolean("AUTO_LOGIN", false).commit()){
                        ToastUtils.show(LoginActivity.this, "取消自动登录");
                    }else{
                        ToastUtils.show(LoginActivity.this, "取消自动登录失败");
                    }
                }
            }
        });
    }

    private void initViews() {
        sp = getSharedPreferences("userInfo", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        cbAutoLogin = (CheckBox) findViewById(R.id.cb_auto_login);
        cbRememberPassWord = (CheckBox) findViewById(R.id.cb_remember_password);
        btnToRegisterView = findViewById(R.id.btn_to_register);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        if (sp.getBoolean("PASSWORD_REMEMBER", false)){
            cbRememberPassWord.setChecked(true);
            mEmailView.setText(sp.getString("USER_EMAIL", ""));
            mPasswordView.setText(sp.getString("USER_PASSWORD", ""));
            if (sp.getBoolean("AUTO_LOGIN",false)){
                cbAutoLogin.setChecked(true);


                attemptLogin();
            }

        }
    }

    private void testButtons() {
        testSkip = (Button) findViewById(R.id.btn_test);
        testSkip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);

        }
        });
        clearDB = (Button) findViewById(R.id.btn_clearDB);
        clearDB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("userDB", "=beforDelete===>现在数据库内条目数量：" + DataSupport.findAll(UserEntity.class).size());
                DataSupport.deleteAll(UserEntity.class);
                Log.i("userDB","=afterDelete===>现在数据库内条目数量："+ DataSupport.findAll(UserEntity.class).size());
            }
        });
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mEmail;
        private final String mPassword;
        private UserEntity tmpUser;

        UserLoginTask(String email, String password) {
            tmpUser = null;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            List<UserEntity> list = DataSupport.where("mEmail=?", mEmail).find(UserEntity.class);

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return 0;
            }
            if (list.size() == 0){
//                mEmailView.setError("帐号不存在");
                return EMAIL_NOT_EXIST;

            }else {
                UserEntity currentUser = list.get(0);
                if (!currentUser.getmPassword().equals(mPassword)){
                    return PASSWORD_ERROR;
                }else {
                    tmpUser = currentUser;
                    return LOGIN_SUCCESS;
                }
            }

            // TODO: register the new account here.
        }


        @Override
        protected void onPostExecute(Integer flag) {
            mAuthTask = null;
            showProgress(false);

//            if (success) {
//                finish();
//            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
//            }
            switch (flag){
                case LOGIN_SUCCESS:
                    if (cbRememberPassWord.isChecked()){
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("USER_EMAIL", mEmail);
                        editor.putString("USER_PASSWORD", mPassword);
                        editor.commit();
                    }
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("USER_INFO", tmpUser);
                    intent.putExtras(bundle);
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    break;
                case EMAIL_NOT_EXIST:
                    mEmailView.setError("帐号不存在");
                    break;
                case PASSWORD_ERROR:
                    mPasswordView.setError("密码输入有误");
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}



