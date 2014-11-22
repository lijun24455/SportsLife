package sysu.project.lee.sportslife.Database;

import java.util.List;
import java.util.Map;

/**
 * 定义好增删改查接口
 */
public interface HistoryService {

    public boolean Insert(Object[] params);

    public boolean Delete(Object[] params);

    public boolean Update();

    // 使用 Map<String, String> 做一个封装，比如说查询数据库的时候返回的单条记录
    public Map<String, String> viewHistory(String[] selectionArgs);

    // 使用 List<Map<String, String>> 做一个封装，比如说查询数据库的时候返回的多条记录
    public List<Map<String, String>> listHistoryMaps(String[] selectionArgs);
}