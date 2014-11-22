package sysu.project.lee.sportslife.Utils;

import android.widget.Toast;

import sysu.project.lee.sportslife.Excercise.Exercise;
import sysu.project.lee.sportslife.Excercise.ExerciseManager;

/**
 * Created by lee on 14年11月20日.
 */
public class mHelper extends mApplication {

    private ExerciseManager exerciseManager;
//    private FriendManager friendManager;
    // private ExercisePlan currentEP;
    private Exercise currentE;
    private int step;
    private int Etype;
    private int num;

    public mHelper() {
        exerciseManager = new ExerciseManager();
//        friendManager = new FriendManager();
        step = 0;
        Etype = 0;
        num = 0;
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
        return currentE;
    }

    public void setCurrentExercise(Exercise exer)
            throws CloneNotSupportedException {
        currentE = (Exercise) exer.clone();
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
