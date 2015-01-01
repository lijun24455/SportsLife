package sysu.project.lee.sportslife.Excercise;


import java.util.ArrayList;
import java.util.List;

import sysu.project.lee.sportslife.Friend.Friend;

/**
 * 运动实体类
 */
public class Exercise implements Cloneable {
    private int ExerType; // 运动类型 0->跑步，1->骑行，2->步行，3->跳绳
    private String ExerTime; // 开始时间
    private int ExerCal; // 预定卡路里量
    private String Destination; // 预定地点

    private int TotalTime; // 总运动时间
    private int TotalCal; // 总卡路里量
    private int TotalCount; // 步数
    private int TotalDistance;//总路程
    private List<Friend> friends;
    private boolean isFinish;


    /**
     * 跳绳运动只需要记录类型和开始时间
     * @param EType     运动类型
     * @param Etime     运动开始时间
     */
    public Exercise(int EType, String Etime){
        ExerType = EType;
        ExerTime = Etime;
        ExerCal = TotalCount = TotalCal = TotalTime = 0;
        Destination = null;
        isFinish = false;
    }


    /**
     * 跑步，骑行，步行运动需要记录类型时间和距离
     * @param EType 运动类型
     * @param ETime 运动时间
     * @param Dest  运动距离
     */
	public Exercise(int EType, String ETime, String Dest) {
		ExerType = EType;
		ExerTime = ETime;
		Destination = Dest;
		ExerCal = TotalCount = TotalCal = TotalTime = 0;
        isFinish = false;
		friends = new ArrayList<Friend>();
	}

	public Exercise(int EType, String ETime, String Dest, int ECal) {
		// TODO Auto-generated constructor stub
		ExerType = EType;
		ExerTime = ETime;
		Destination = Dest;
		ExerCal = ECal;
        TotalCount = TotalCal = TotalTime = 0;
		friends = new ArrayList<Friend>();
        isFinish = false;
	}


	public int getTotalDistance() {
		return TotalDistance;
	}

	public void setTotalDistance(int t) {
        TotalDistance = t;
	}

	public int getTotalTime() {
		return TotalTime;
	}

	public void setTotalTime(int t) {
		TotalTime = t;
	}

	public int getTotalCal() {
		return TotalCal;
	}

	public void setTotalCal(int c) {
		TotalCal = c;
	}

	public String getTime() {
		return ExerTime;
	}

	public int getType() {
		return ExerType;
	}

	public String getDest() {
		return Destination;
	}

	public int getTotalCount() {
		return TotalCount;
	}

	public void setTotalCount(int c) {
        TotalCount = c;
	}

	public void setFinish(boolean b) {
        isFinish = b;
	}

	public boolean getFinish() {
		return isFinish;
	}

	public Object clone() throws CloneNotSupportedException {
		Exercise cloned = (Exercise) super.clone();
		cloned.friends.addAll(friends);
		return cloned;
	}
}
