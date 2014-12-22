package sysu.project.lee.sportslife.UI;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import sysu.project.lee.sportslife.Excercise.ExerciseHistoryActivity;
import sysu.project.lee.sportslife.News.UI.NewsCollectionActivity;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.User.UserEntity;


public class MeFragment extends Fragment {

    private RelativeLayout itemShowHistory = null;
    private RelativeLayout newsCollection = null;
    private UserEntity currentUser = null;
    private CircleImageView mUserPhoto = null;
    private String mUserPhotoPATH = null;
    private TextView mUserEmail = null;

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
    }

    private void initViews() {
        itemShowHistory = (RelativeLayout) getView().findViewById(R.id.lo_show_history);
        newsCollection = (RelativeLayout) getView().findViewById(R.id.lo_show_favorite);
        mUserPhoto = (CircleImageView) getView().findViewById(R.id.iv_userphoto);
        mUserPhotoPATH = currentUser.getmPhotoPath();
        mUserPhoto.setImageBitmap(BitmapFactory.decodeFile(mUserPhotoPATH));
        mUserEmail = (TextView) getView().findViewById(R.id.tv_username);
        mUserEmail.setText(currentUser.getmEmail());
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
