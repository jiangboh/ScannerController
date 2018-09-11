package com.bravo.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * 文件处理工具类
 * @author MING_JIN_TIAN
 * @时间： 2015-8-7
 */
public class FileUtils {

	public static final long B = 1;
	public static final long KB = B * 1024;
	public static final long MB = KB * 1024;
	public static final long GB = MB * 1024;

	private final String BASE_DIR = "SmartController";
	private final String LOG_DIR = "Logs/";
	private final String FILE_DIR = "File/";
	private Context context;

	public FileUtils(Context context) {
		this.context = context;
	}

	public void initPath() {
		String basePath = getBasePath();
		makeSureFolderExist(basePath + File.separator + LOG_DIR);
		makeSureFolderExist(basePath + File.separator + FILE_DIR);
	}

	public String getLogCacheDir() {
		String basePath = getBasePath();
		makeSureFolderExist(getBasePath() + File.separator + LOG_DIR);
		return basePath + File.separator + LOG_DIR;
	}

	public String getFileCacheDir() {
		String basePath = getBasePath();
		makeSureFolderExist(basePath + File.separator + FILE_DIR);
		return basePath + File.separator + FILE_DIR;
	}
	
	public void makeSureFolderExist(String path) {
		File file_path = new File(path);
		if (file_path.isFile()) {// 若为文件，则直接删除文件
			file_path.delete();
		}
		if (!file_path.exists()) {// 并创建目录
			file_path.mkdirs();
			Uri uri = Uri.fromFile(file_path);
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
			context.sendBroadcast(intent);
		}
	}
	/**
	 * 格式化文件大小<b> 带有单位
	 * 
	 * @param size
	 * @return
	 */
	public String formatFileSize(long size) {
		StringBuilder sb = new StringBuilder();
		String u = null;
		double tmpSize = 0;
		if (size < KB) {
			sb.append(size).append("B");
			return sb.toString();
		} else if (size < MB) {
			tmpSize = (double)size/(double)KB;
			u = "KB";
		} else if (size < GB) {
			tmpSize = (double)size/(double)MB;
			u = "MB";
		} else {
			tmpSize = (double)size/(double)GB;
			u = "GB";
		}
		return sb.append(Utils.formatDecimaD(tmpSize,2)).append(u).toString();
	}

	public String getBasePath() {
		String basePath = null;
		if (!Utils.isSdCardMounted()) {
			basePath = context.getCacheDir().getAbsolutePath()
					+ File.separator + BASE_DIR;
		} else {
			if(Build.VERSION.SDK_INT >= 23&& ContextCompat.checkSelfPermission(context,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED){
				basePath = context.getExternalFilesDir(BASE_DIR).getPath();
			}else{
				basePath = Environment.getExternalStorageDirectory().getPath()
						+ File.separator + BASE_DIR;
			}
		}
		return basePath;
	}

	/**
	 * 从流中读取文件
	 *
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public String readTextInputStream(InputStream is) throws IOException {
		StringBuffer strbuffer = new StringBuffer();
		String line;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is));
			while ((line = reader.readLine()) != null) {
				strbuffer.append(line).append("\r\n");
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return strbuffer.toString();
	}

	/**
	 * 分页加载大文本文件
	 *
	 * @param file
	 * @param page
	 * @return
	 */
	public String openBigTxtFile(File file,int page,int pageSize){
		RandomAccessFile readFile = null;
		try {
			readFile = new RandomAccessFile(file,"r");
			readFile.seek(page*pageSize);
			byte[] chs = new byte[pageSize];
			readFile.read(chs);
			return new String(chs, Charset.forName("utf-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(readFile != null){
				try {
					readFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}


	/**
	 * 将文本内容写入文件
	 * 
	 * @param file
	 * @param str
	 * @throws IOException
	 */
	public void writeTextFile(File file, String str) throws IOException {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new FileOutputStream(file));
			out.write(str.getBytes());
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * 将文本内容写入文件
	 *
	 * @param file
	 * @param str
	 * @throws IOException
	 */
	public void writeTextFile(File file, String str,boolean addMode) throws IOException {
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(file,addMode), "utf-8");
			osw.append(str);
			osw.close();
		} finally {
			if (osw != null) {
				osw.close();
			}
		}
	}

	/**
	 * 获取一个文件夹大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public long getFileSize(File f) {
		long size = 0;
		File flist[] = f.listFiles();
		if (flist != null && flist.length > 0) {
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					size = size + getFileSize(flist[i]);
				} else {
					size = size + flist[i].length();
				}
			}
		}
		return size;
	}

	/**
	 * 获取文件夹中以tag结尾的所有文件
	 *
	 * @param strPath
	 * @param tag
	 * @return
	 */
	public ArrayList<File> getFileList(String strPath,String tag) {
		ArrayList<File> filelist = new ArrayList<File>();
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		if (files == null)
			return null;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				getFileList(files[i].getAbsolutePath(),tag);
			} else {
				if(files[i].getName().toLowerCase().endsWith(tag))
					filelist.add(files[i]);
			}
		}
		return filelist;
	}

	/**
	 * 将数据流写入文件
	 * @param is
	 * @param filePath
	 */
	public void moveFile(InputStream is, String filePath) {
		if (is == null)
			return;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(filePath));
			byte[] buff = new byte[1024];
			int s = -1;
			while ((s = is.read(buff)) != -1) {
				fos.write(buff, 0, buff.length);
				fos.flush();
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public void deleteFile(File file) {// 删除指定目录上面的所有文件及子目录（包含子目录下面的所有文件）
		if (file.exists() && file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null && files.length > 0) {
				for (File f : files) {
					if (f.isFile()) {
						f.delete();
					} else {
						deleteFile(f);
					}
				}
			}
			file.delete();

		}
	}

	/**
	 * 清除SD卡上存储的数据
	 */
	public void clearSDCardCache(){
		deleteFile(new File(getBasePath()));
	}

}