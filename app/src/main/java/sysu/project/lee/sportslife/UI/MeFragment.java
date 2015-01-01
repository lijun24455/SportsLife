package sysu.project.lee.sportslife.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import de.hdodenhof.circleimageview.CircleImageView;
import sysu.project.lee.sportslife.Excercise.ExerciseHistoryActivity;
import sysu.project.lee.sportslife.News.UI.NewsCollectionActivity;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.User.UserEntity;



public class MeFragment extends Fragment {

    private RelativeLayout itemShowHistory = null;
    private RelativeLayout newsCollection = null;
    private RelativeLayout setting = null;
    private UserEntity currentUser = null;
    private CircleImageView mUserPhoto = null;
    private String mUserPhotoPATH = null;
    private TextView mUserEmail = null;
    private Button mSignoutBtn = null;
    private SharedPreferences sp = null;

    private static final int CHANGE_REQUEST_CODE = 0;
    private static final int RESULT_REQUEST_CODE = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container,false);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sp = getActivity().getSharedPreferences("userInfo", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);

        currentUser = (UserEntity) getActivity().getIntent().getSerializableExtra("USER_INFO");

        initViews();

        itemShowHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("CURRENT_USER", currentUser);
                intent.setClass(getActivity(), ExerciseHistoryActivity.class);
                startActivity(intent);
            }
        });


        newsCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("CURRENT_USER", currentUser);
                intent.setClass(getActivity(), NewsCollectionActivity.class);
                startActivity(intent);
            }
        });

        mSignoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putBoolean("AUTO_LOGIN", false).commit();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), EditCustomerActivity.class);
                startActivityForResult(intent, CHANGE_REQUEST_CODE);
            }
        });
    }

    private void initViews() {
        itemShowHistory = (RelativeLayout) getView().findViewById(R.id.lo_show_history);
        newsCollection = (RelativeLayout) getView().findViewById(R.id.lo_show_favorite);
        setting = (RelativeLayout) getView().findViewById(R.id.lo_setting);
        mUserPhoto = (CircleImageView) getView().findViewById(R.id.iv_userphoto);
        mUserPhotoPATH = currentUser.getmPhotoPath();
        mUserPhoto.setImageBitmap(BitmapFactory.decodeFile(mUserPhotoPATH));
        mUserEmail = (TextView) getView().findViewById(R.id.tv_username);
        if (!TextUtils.isEmpty(currentUser.getmName())){
            mUserEmail.setText(currentUser.getmName());
        }else{
            mUserEmail.setText(currentUser.getmEmail());
        }
        mSignoutBtn = (Button) getView().findViewById(R.id.btn_signout);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        currentUser = DataSupport.find(UserEntity.class, currentUser.getId());


        switch (resultCode) {
            case -1:
                if (!TextUtils.isEmpty(currentUser.getmName())){
                    mUserEmail.setText(currentUser.getmName());
                    mUserPhoto.setImageBitmap(BitmapFactory.decodeFile(currentUser.getmPhotoPath()));
                }
                break;
            case 0:

                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
