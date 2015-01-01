package sysu.project.lee.sportslife.News.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;

/**
 * 文件操作工具类
 * @author lee
 *
 */

public class FileUtils
{

	public static String getSDRootPath()
	{
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			return Environment.getExternalStorageDirectory() + "";
		} else
		{
			return null;
		}
	}

	/**
	 * @description 将二进制流写入文件
	 * @param in    InputStream类型，输入流
	 * @param path  String类型，路径
	 * @param fileName  String类型，文件名
	 * @return boolean  boolean类型，返回操作结果
	 */
	public static boolean saveToFile(InputStream in, String path,
			String fileName)
	{
		FileOutputStream out = null;
		byte buffer[] = new byte[4 * 1024];

		File file = new File(path, fileName);
		try
		{
			file.createNewFile();
			out = new FileOutputStream(file);
			while ((in.read(buffer)) != -1)
			{
				out.write(buffer);
			}
			return true;
		} catch (IOException e1)
		{
			e1.printStackTrace();
			return false;
		} finally
		{
			if (null != out)
			{
				try
				{
					out.flush();
					out.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (null != in)
			{
				try
				{
					in.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @deprecated 获取目录文件大小
	 * 
	 * @param dir   文件路径
	 * @return  long类型，文件大小
	 */
	public static long getDirSize(File dir)
	{
		if (dir == null)
		{
			return 0;
		}
		if (!dir.isDirectory())
		{
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files)
		{
			if (file.isFile())
			{
				dirSize += file.length();
			} else if (file.isDirectory())
			{
				dirSize += file.length();
				dirSize += getDirSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

	/**
	 * @deprecated 转换文件大小
	 * 
	 * @param fileS long类型，文件大小
	 * @return B/KB/MB/GB   String类型，文件类型
	 */
	public static String formatFileSize(long fileS)
	{
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024)
		{
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576)
		{
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824)
		{
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else
		{
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * @deprecated 删除目录(包括：目录里的所有文件)
	 * 
	 * @param dirName   String类型，文件路径
	 * @return  boolean类型，返回操作结果
	 */
	public static boolean deleteDirectory(String dirName)
	{
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!dirName.equals(""))
		{
			File newPath = new File(dirName);
			checker.checkDelete(newPath.toString());
			if (newPath.isDirectory())
			{
				String[] listfile = newPath.list();
				try
				{
					for (int i = 0; i < listfile.length; i++)
					{
						File deletedFile = new File(newPath.toString()
								+ File.separator + listfile[i].toString());
						deletedFile.delete();
					}
					newPath.delete();
					status = true;
				} catch (Exception e)
				{
					e.printStackTrace();
					status = false;
				}

			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * @description 根据文件绝对路径创建文件/布覆盖原文件
	 * @param name String类型 文件绝对路径
	 * @return  File类型，返回文件对象
	 */
	public static File newAbsoluteFile(String name)
	{
		File file = new File(name);
		file.getParentFile().mkdirs();
		try
		{
			file.createNewFile();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return file;
	}
}
