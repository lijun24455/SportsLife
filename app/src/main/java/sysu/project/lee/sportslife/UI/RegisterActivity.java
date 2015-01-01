package sysu.project.lee.sportslife.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.User.UserEntity;
import sysu.project.lee.sportslife.User.Utils;
import sysu.project.lee.sportslife.Utils.ToastUtils;

/**
 * Created by lee on 14年12月13日.
 */
public class RegisterActivity extends Activity{

    private UserEntity newUser = null;
    private CircleImageView btnChoosePhoto = null;
    private AutoCompleteTextView mEmailView = null;
    private EditText mPassWord1 = null;
    private EditText mPassWord2 = null;
    private Button btnRegist = null;
    private String[] items = {"选择本地图片","拍照获取"};

    /* 图片文件命名 */
    private static final String IMAGE_REAR = ".jpg";
    private String IMAGE_FILE_NAME = "IMG";
    private String IMAGE_RADOM_CODE = "" + (int)(Math.random()*1000);

    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        Log.i("Radom","radom--------->"+IMAGE_RADOM_CODE);

        initDataBase();
        initView();
        initListener();

    }

    private void initDataBase() {
        SQLiteDatabase db = Connector.getWritableDatabase();
    }

    private void initView() {
        btnChoosePhoto = (CircleImageView) findViewById(R.id.register_btn_choose_photo);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.register_edittext_email);
        mPassWord1 = (EditText) findViewById(R.id.register_edittext_password1);
        mPassWord2 = (EditText) findViewById(R.id.register_edittext_password2);
        btnRegist = (Button) findViewById(R.id.register_btn_regist);
        newUser = new UserEntity();
    }

    private void initListener() {

        btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        mEmailView.setOnFocusChangeListener(new CheckUseremailListener());
        mPassWord1.setOnFocusChangeListener(new CheckUserPassword1Listener());
        mPassWord2.setOnFocusChangeListener(new CheckUserPassword2Listener());

        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View focusView = null;
                boolean cancel = false;

                String userEmail = mEmailView.getText().toString();
                String userPassword1 = mPassWord1.getText().toString();
                String userPassword2 = mPassWord2.getText().toString();
                String userPhotoPath = Utils.IMAGE_PATH_BASE_DIR + IMAGE_FILE_NAME + IMAGE_RADOM_CODE + IMAGE_REAR;

                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(userPassword1) || TextUtils.isEmpty(userPassword2) ) {
                    mPassWord1.setError("请输入密码");
                    focusView = mPassWord1;
                    cancel = true;
                }

                if(!isPassword1Valid(userPassword1)){
                    ToastUtils.show(RegisterActivity.this, "密码只能包含数字，字母和下划线，至少为8位");
                    mPassWord1.setError("密码格式错误");
                    mPassWord2.setText(null);
                    focusView = mPassWord1;
                    cancel = true;
                }else if(!isSameToPassword1(userPassword1, userPassword2)){
                    mPassWord2.setError("密码不一致，请重新输入");
                    mPassWord2.setText(null);
                    focusView = mPassWord2;
                    cancel = true;
                }

                // Check for a valid email address.
                if (TextUtils.isEmpty(userEmail)) {
                    mEmailView.setError(getString(R.string.error_field_required));
                    focusView = mEmailView;
                    cancel = true;
                } else if (!isEmailValid(userEmail)) {
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    focusView = mEmailView;
                    cancel = true;
                }

                if(cancel){
                    focusView.requestFocus();
                }else{
                    createNewUserItem(userEmail, userPassword1, userPhotoPath);
                    if(isExistedInDB(newUser)){
                        mEmailView.requestFocus();
                        mEmailView.setError("邮箱已被注册，请更换");
                    }else {
                        ToastUtils.show(RegisterActivity.this, "Mail:"+userEmail+";Password:"+userPassword1);
                        insertNewUserIntoDB(newUser);
                        loginByNewUser(newUser);
                    }
                }

            }
        });


    }

    private void loginByNewUser(UserEntity newUser) {
        try {
            Thread.sleep(1000 * 2);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("USER_INFO", newUser);
            intent.putExtras(bundle);
            intent.setClass(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void createNewUserItem(String userEmail, String userPassword1, String userPhotoPath) {
        newUser.setmEmail(userEmail);
        newUser.setmPassword(userPassword1);
        newUser.setmPhotoPath(userPhotoPath);
    }

    private class CheckUseremailListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            mEmailView.setError(null);
            String strEmail = mEmailView.getText().toString();
            if(!TextUtils.isEmpty(strEmail)){
                if(!isEmailValid(strEmail)){
                    mEmailView.setError("此邮箱已经被注册");
                }else{
                    mEmailView.setError(null);
                }
            }else{
                mEmailView.setError("请输入邮箱作为登录帐号");
            }
        }
    }

    private class CheckUserPassword1Listener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            mPassWord1.setError(null);
            String strPassword1 = mPassWord1.getText().toString();
            if(!TextUtils.isEmpty(strPassword1)){
                if(!isPassword1Valid(strPassword1)){
                    ToastUtils.show(RegisterActivity.this, "密码只能包含数字，字母和下划线，至少为8位");
                    mPassWord1.setError("密码格式错误");
                }else{
                    mPassWord1.setError(null);
                }
            }else{
                mPassWord1.setError(null);
            }

        }
    }

    private class CheckUserPassword2Listener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            mPassWord2.setError(null);
            String strPassword2 = mPassWord2.getText().toString();
            if (!TextUtils.isEmpty(strPassword2)){
                if (!isSameToPassword1(mPassWord1.getText().toString(), strPassword2)){
                    mPassWord2.setError("输入不一致");
                }else{
                    mPassWord2.setError(null);
                }
            }else{
                mPassWord2.setError(null);
            }
        }
    }

    private boolean isSameToPassword1(String password1, String password2) {
        return password2.equals(password1);
    }

    private boolean isPassword1Valid(String strPassword1) {
        if (strPassword1.length()>=8){
            return true;
        }
        return false;
    }

    private boolean isEmailValid(String strEmail) {
        return strEmail.contains("@");
    }

    private boolean isExistedInDB(UserEntity user){
        List<UserEntity> list = DataSupport.select("mEmail").find(UserEntity.class);
        for(UserEntity u : list){
            if (user.getmEmail().equals(u.getmEmail())){
                return true;
            }
        }
        return false;
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("设置头像")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intentFromGallery = new Intent();
                                intentFromGallery.setType("image/*"); // 设置文件类型
                                intentFromGallery
                                        .setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intentFromGallery,
                                        IMAGE_REQUEST_CODE);
                                break;
                            case 1:

                                Intent intentFromCapture = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                // 判断存储卡是否可以用，可用进行存储
                                if (Utils.hasSdcard()) {

                                    intentFromCapture.putExtra(
                                            MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(Utils.IMAGE_PATH_BASE_DIR,
                                                    IMAGE_FILE_NAME)));
                                }

                                startActivityForResult(intentFromCapture,
                                        CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {

            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (Utils.hasSdcard()) {
                        File tempFile = new File(
                                Utils.IMAGE_PATH_BASE_DIR
                                        + IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(RegisterActivity.this, "未找到存储卡，无法存储照片！",
                                Toast.LENGTH_LONG).show();
                    }

                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param data
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            if (saveBitmapToSDCard(photo)){
                ToastUtils.show(RegisterActivity.this, "头像保存成功");
                Drawable drawable = new BitmapDrawable(photo);
                btnChoosePhoto.setImageDrawable(drawable);
            }
        }
    }

    private boolean saveBitmapToSDCard(Bitmap bitmap){
        File file = new File(Utils.IMAGE_PATH_BASE_DIR , IMAGE_FILE_NAME + IMAGE_RADOM_CODE + IMAGE_REAR);
        if (file.exists()){
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void insertNewUserIntoDB(UserEntity user){
        if(user.save()){
            ToastUtils.show(RegisterActivity.this, "注册成功，正在进入……userID:"+user.getId());
        }else{
            ToastUtils.show(RegisterActivity.this, "注册失败，请检查网络问题。");
        }
    }

}
