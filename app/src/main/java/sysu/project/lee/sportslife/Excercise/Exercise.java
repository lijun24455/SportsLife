package sysu.project.lee.sportslife.Excercise;


import java.util.ArrayList;
import java.util.List;

import sysu.project.lee.sportslife.Friend.Friend;

public class Exercise implements Cloneable {
    private String ExerType; // 运动类型
    private String ExerTime; // 开始时间
    private int ExerCal; // 预定卡路里量
    private int TotalTime; // 总运动时间
    private int TotalCal; // 总卡路里量
    private String Destination; // 预定地点
    private int Count; // 步数
	private List<Friend> friends;
	private boolean finish;
	private int Distance;


	public Exercise(String EType, String ETime, String Dest) {
		ExerType = EType;
		ExerTime = ETime;
		Destination = Dest;
		ExerCal = Count = TotalCal = TotalTime = 0;
		finish = false;
		friends = new ArrayList<Friend>();
	}

	public Exercise(String EType, String ETime, String Dest, int ECal) {
		// TODO Auto-generated constructor stub
		ExerType = EType;
		ExerTime = ETime;
		Destination = Dest;
		ExerCal = ECal;
		Count = TotalCal = TotalTime = 0;
		friends = new ArrayList<Friend>();
		finish = false;
	}

	public Exercise(String EType, String ETime, String Dest, int ECal,
			List<Friend> fs) {
		ExerType = EType;
		ExerTime = ETime;
		Destination = Dest;
		ExerCal = ECal;
		Count = TotalCal = TotalTime = 0;
		friends = new ArrayList<Friend>();
		finish = false;
	}

	public int getDistance() {
		return Distance;
	}

	public void setDistance(int t) {
		Distance = t;
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

	public String getType() {
		return ExerType;
	}

	public String getDest() {
		return Destination;
	}

	public int getCount() {
		return Count;
	}

	public void setCount(int c) {
		Count = c;
	}

	public void setfinish(boolean b) {
		finish = b;
	}

	public boolean getfinish() {
		return finish;
	}

	public Object clone() throws CloneNotSupportedException {
		Exercise cloned = (Exercise) super.clone();
		cloned.friends.addAll(friends);
		return cloned;
	}
}
