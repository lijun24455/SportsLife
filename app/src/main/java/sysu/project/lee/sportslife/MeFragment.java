package sysu.project.lee.sportslife;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import sysu.project.lee.sportslife.Excercise.ExerciseHistoryActivity;
import sysu.project.lee.sportslife.News.UI.NewsCollectionActivity;


public class MeFragment extends Fragment {

    private RelativeLayout itemShowHistory = null;
    private RelativeLayout newsCollection = null;

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

        itemShowHistory = (RelativeLayout) getView().findViewById(R.id.lo_show_history);

        itemShowHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ExerciseHistoryActivity.class);
                startActivity(intent);
            }
        });
        newsCollection = (RelativeLayout) getView().findViewById(R.id.lo_show_favorite);

        newsCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), NewsCollectionActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
