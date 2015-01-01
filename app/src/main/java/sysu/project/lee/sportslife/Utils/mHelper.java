package sysu.project.lee.sportslife.Utils;

import android.widget.Toast;

import java.util.ArrayList;

import sysu.project.lee.sportslife.Excercise.Exercise;
import sysu.project.lee.sportslife.Excercise.ExerciseManager;
import sysu.project.lee.sportslife.News.UI.Section;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.User.UserEntity;

/**
 * Created by lee on 14年11月20日.
 */
public class mHelper extends mApplication {

    private ExerciseManager exerciseManager;
//    private FriendManager friendManager;
    // private ExercisePlan currentEP;
    private Exercise currentExercise;

    private UserEntity currentUser;

    private ArrayList<Section> feedList;

    private int step;

    private int Etype;  //0->run; 1->bike; 2->step; 3->skip
    private int num;

    public mHelper() {
        exerciseManager = new ExerciseManager();
//        friendManager = new FriendManager();
        step = 0;
        Etype = 0;
        num = 0;
        initFeedList();
    }

    private void initFeedList() {
        feedList = new ArrayList<Section>();

        Section mFeedCenter = new Section();
        mFeedCenter.setUrl("toCenter");
        mFeedCenter.setTitle("订阅中心");
        mFeedCenter.setBackgroundColor(R.drawable.ic_feed_center);
        mFeedCenter.setSelected(true);

        Section mNEOSection = new Section();
        mNEOSection.setTitle("牛男网");
        mNEOSection.setUrl(URLs.NEO_URL);

        Section section1 = new Section();
        section1.setTitle("测试1");
        section1.setUrl("1");

        Section section2 = new Section();
        section2.setTitle("测试2");
        section2.setUrl("2");

        Section section3 = new Section();
        section3.setTitle("测试3");
        section3.setUrl("3");

        Section section4 = new Section();
        section4.setTitle("测试4");
        section4.setUrl("4");

        feedList.add(mFeedCenter);
        feedList.add(mNEOSection);
        feedList.add(section1);
        feedList.add(section2);
        feedList.add(section3);
        feedList.add(section4);

    }

    public ArrayList<Section> getFeedList() {
        return feedList;
    }

    public void setFeedList(ArrayList<Section> feedList) {
        this.feedList = feedList;
    }

    public UserEntity getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserEntity currentUser) {
        this.currentUser = currentUser;
    }

    public ExerciseManager getExerManager() {
        return exerciseManager;
    }

//    public FriendManager getFriManager() {
//        return friendManager;
//    }

    public int getEtype() {
        return Etype;
    }

    public void setEtype(int t) {
        Etype = t;
    }

    public Exercise getCurrExercise() {
        return currentExercise;
    }

    public void setCurrentExercise(Exercise exer)
            throws CloneNotSupportedException {
        currentExercise = (Exercise) exer.clone();
    }

    public void setStep(int s) {
        step = s;
    }

    public int getStep() {
        return step;
    }

    public void setNum(int n) {
        num = n;
    }

    public int getNum() {
        return num;
    }

	/*
	 * public void setCurrentExercisePlan(ExercisePlan exerP) throws
	 * CloneNotSupportedException { currentEP = (ExercisePlan) exerP.clone(); }
	 */

    public void DisplayToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
