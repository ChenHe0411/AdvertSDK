package com.ymzz.plat.alibs.zwu;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * 文件下载
 * 
 * @author Woody
 *
 */
public class FileUtil {
	/** SD卡的路径 **/
	public static final String SDCARD_ROOT = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	/** 文件的路径 **/
	public static final String FILE_ROOT = SDCARD_ROOT + "/Android"
			+ File.separator + "asds" + File.separator;
	/** 用于保存图片 **/
	private static Bitmap mBitmap;

	/**
	 * Get image from network
	 * 
	 * @param path
	 *            The path of image
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] getImage(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		InputStream inStream = conn.getInputStream();
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return readStream(inStream);
		}
		return null;
	}

	/**
	 * Get Steam from URL
	 * 
	 * @param urlPath
	 *            The path of image
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream getFileStream(String urlPath) throws Exception {
		InputStream inputStream = null;
		URL url = new URL(urlPath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			inputStream = conn.getInputStream();
		}
		return inputStream;
	}

	/**
	 * GetURLFileLength from URL
	 * 
	 * @param urlPath
	 *            The path of image
	 * @return InputStream
	 * @throws Exception
	 */
	public static int getURLFileLength(String urlPath) throws Exception {
		int length = 0;
		URL url = new URL(urlPath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			length = conn.getContentLength();
		}
		conn.disconnect();
		return length;
	}

	/**
	 * Get data from stream
	 * 
	 * @param inStream
	 * @return byte[]
	 * @throws Exception
	 */
	private static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	/**
	 * 保存文件
	 * 
	 * @param fileName
	 */
	public static void saveOtherFile(String netPath, String localPath,
			String fileName, InputStream defaultStream) {
		File file = null;
		FileOutputStream output = null;
		InputStream input = null;
		try {
			// 创建文件夹
			creatSDDir(localPath);

			// 获取写入流和资源的长度
			if (defaultStream != null) {
				input = defaultStream;
			} else {
				input = getFileStream(netPath);
				if (input == null) {
					input = getFileStream(netPath);
				}
			}

			// 如果预下载文件存在，那么不需要重新下载
			file = new File(fileName);
			if (file.exists() &&defaultStream == null) {
				return;
			}

			// 创建文件和输出流，开始写入文件
			file = creatSDFile(fileName);
			output = new FileOutputStream(fileName);
			byte buffer[] = new byte[4 * 1024];
			int read;
			while ((read = input.read(buffer)) > 0) {
				output.write(buffer, 0, read);
			}
			
			MyLogcat.log("文件:" + fileName + "下载成功！");
			
			Thread.sleep(500);
			input.close();
			output.close();
		} catch (Exception e) {
			file.delete();
			e.printStackTrace();
		}
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public static File creatSDFile(String filePath) throws IOException {
		File file = new File(filePath);
		// if (!file.exists()) {
		// file.createNewFile();
		// }
		return file;
	}

	/**
	 * 创建文件
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static File createFile(String filePath) throws IOException {
		File file = new File(filePath);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public static File creatSDDir(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			boolean bl = dir.mkdirs();
			if (!bl) {
				MyLogcat.log("创建目录失败：" + path);
			}
		}
		return dir;
	}

	/**
	 * 删除目录
	 * 
	 * @param dir
	 *            将要删除的目录路径
	 */
	public static void deleteDir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		boolean success = file.delete();
		if (!success) {
			MyLogcat.log("Not empty directory: " + path);
			deleteFullDir(file);
			MyLogcat.log("Successfully to delete empty directory: " + path);
		} else {
			MyLogcat.log("Failed deleted empty directory: " + path);
		}
	}

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 * 
	 * @param dir
	 *            将要删除的文件目录
	 * @return boolean Returns "true" if all deletions were successful. If a
	 *         deletion fails, the method stops attempting to delete and returns
	 *         "false".
	 */
	private static boolean deleteFullDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下的文件
			for (int i = 0; i < children.length; i++) {
				boolean success = new File(dir, children[i]).delete();
				if (!success) {
					deleteFullDir(new File(dir, children[i]));
				}
				MyLogcat.log("文件" + children[i] + "删除成功！");
			}
			return true;
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	/**
	 * 将String转化成流
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static InputStream StringTOInputStream(String in) throws Exception {
		ByteArrayInputStream input = new ByteArrayInputStream(in.getBytes());
		return input;
	}

	/**
	 * 保存文件
	 * 
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public static void saveFile(String path, String fileName,
			InputStream inStream) throws IOException {
		try {
			// 定义一个带缓冲的输入流
			BufferedInputStream bis = new BufferedInputStream(inStream);
			// 创建保存目录
			creatSDDir(SDCARD_ROOT);
			creatSDDir(path);
			// 创建图片的路径
			File file = creatSDFile(fileName);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			// 如果是图片文件，用BitmapFactory读；如果是其他文件，用BufferedOutputStream
			if (fileName.contains(".jpg") || fileName.contains(".png")) {
				mBitmap = BitmapFactory.decodeStream(inStream);
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			} else {
				saveSdCard(path, fileName, inStream);
			}
			// 将缓存区的数据全部写出
			bos.flush();
			// 关闭流
			bis.close();
			bos.close();
			MyLogcat.log("文件名：" + fileName + "保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存文件到SD卡
	 * 
	 * @param path
	 * @param filename
	 * @param inStream
	 * @throws Exception
	 */
	public static void saveSdCard(String path, String filename,
			InputStream inStream) throws Exception {
		creatSDDir(path);
		// 获取SD卡路径
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
				inStream));
		String lines = "";
		StringBuffer bs = new StringBuffer();
		while ((lines = bufferReader.readLine()) != null) {
			bs.append(lines);
			// MyLogcat.log(lines);
		}
		String body = bs.toString();
		File saveFile = creatSDFile(filename);
		saveFile.createNewFile();
		FileWriter fileWritter = new FileWriter(filename, false);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(body);
		bufferWritter.close();
		MyLogcat.log("文件名：" + filename + "保存成功！");
	}

	/**
	 * 读取指定路径上的资源文件，将资源文件转化成字符串
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static String readSdCard(String path) throws Exception {
		File openFile = creatSDFile(path);
		BufferedReader reader = new BufferedReader(new FileReader(openFile));
		String lines = "";
		StringBuffer bs = new StringBuffer();
		while ((lines = reader.readLine()) != null) {
			bs.append(lines);
		}
		reader.close();
		return bs.toString();
	}

	/**
	 * 复制文件到指定文件夹
	 * 
	 * @param prePath
	 * @param newPath
	 */
	public static void copyFile(String prePath, String newPath) {
		File file = new File(prePath);
		if (file.exists()) {
			FileInputStream fi = null;
			FileOutputStream fo = null;
			FileChannel in = null;
			FileChannel out = null;
			try {
				fi = new FileInputStream(prePath);
				fo = new FileOutputStream(newPath);
				in = fi.getChannel();// 得到对应的文件通道
				out = fo.getChannel();// 得到对应的文件通道
				in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fi.close();
					in.close();
					fo.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将字符串转成MD5值
	 * 
	 * @param bytes
	 * @return
	 */
	public static String getMD5(byte[] bytes) {
		String s = null;
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(bytes);
			byte tmp[] = md.digest();
			// MD5 的计算结果是一个 128 位的长整数， 用字节表示就是 16 个字节
			char str[] = new char[16 * 2];
			// 每个字节用 16 进制表示的话，使用两个字符，所以表示成 16 进制需要 32 个字符
			// 表示转换结果中对应的字符位置
			int k = 0;
			// 从第一个字节开始，对 MD5 的每一个字节转换成 16 进制字符的转换
			for (int i = 0; i < 16; i++) {
				// 取第 i 个字节
				byte byte0 = tmp[i];
				// 取字节中高 4 位的数字转换,
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				// 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			s = new String(str); // 换后的结果转换为字符串

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
}
