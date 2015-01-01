package sysu.project.lee.sportslife.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * 朋友管理类（暂未实现）
 */
public class FriendManager {
	private List<Friend> friends;

	public FriendManager() {
		friends = new ArrayList<Friend>();
	}

	public List<Friend> getFriendList() {
		return friends;
	}
}
