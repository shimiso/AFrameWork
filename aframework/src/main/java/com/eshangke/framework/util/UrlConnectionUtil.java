package com.eshangke.framework.util;

import com.eshangke.framework.bean.FormFile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * http工具类
 * 
 * @author 史明松
 */
public class UrlConnectionUtil {
	/**
	 * 
	 * 多文件和表单数据类型提交
	 * @param params
	 * @param formFiles
	 * @param urlPath 
	 * @author 史明松
	 * @update 2012-8-3 下午4:59:53
	 */
	public static InputStream postFormData(Map<String,String> params, FormFile[] formFiles, String urlPath) {
		try { 
			String BOUNDARY = "---------7d4a6d158c9"; // 定义数据分隔线
			URL url = new URL(urlPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive"); 
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
			OutputStream out = new DataOutputStream(conn.getOutputStream());
			// 构造文本类型参数的实体数据
			StringBuilder textEntity = new StringBuilder();
			if (params != null && !params.isEmpty()) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					textEntity.append("--");
					textEntity.append(BOUNDARY);
					textEntity.append("\r\n");
					textEntity.append("Content-Disposition: form-data; name=\""
							+ entry.getKey() + "\"\r\n\r\n");
					textEntity.append(entry.getValue());
					textEntity.append("\r\n");
				}
			}
			out.write("\r\n".getBytes());// 写完HTTP请求头后根据HTTP协议再写一个回车换行
			out.write(textEntity.toString().getBytes());// 把所有文本类型的实体数据发送出来

			for (FormFile uploadFile : formFiles) {
				StringBuilder sb = new StringBuilder();
				sb.append("--");
				sb.append(BOUNDARY);
				sb.append("\r\n");
				sb.append("Content-Disposition: form-data;name=\"" + uploadFile.getParameterName()
						+ "\";filename=\"" + uploadFile.getFilname() + "\"\r\n");
				sb.append("Content-Type:"+uploadFile.getContentType()+"\r\n\r\n");
				byte[] data = sb.toString().getBytes();
				out.write(data);
				DataInputStream in = new DataInputStream(uploadFile.getInStream());
				int bytes = 0;
				byte[] bufferOut = new byte[1024];
				while ((bytes = in.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
				in.close();
			}
			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
			out.write(end_data);
			out.flush();
			out.close();
			return conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * InputStream 转 byte[]
	 * @param input
	 * @return
	 * @throws IOException
     */
	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
		return output.toByteArray();
	}
	/**
	 *
	 * 使用HTTP的POST方法提交xml数据.
	 * @param xml 提交的xml数据
	 * @param urlPath 请求路径
	 * @return
	 * @author 史明松
	 * @update Feb 7, 2012 7:04:15 PM
	 */
	public static InputStream postXml(String xml, String urlPath) {
		try {
			URL url=new URL(urlPath);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			byte[] buff=xml.getBytes("UTF-8");
			conn.setConnectTimeout(10*1000);
			conn.setDoOutput(true); // 允许输出
			conn.setUseCaches(false); // 不允许缓存
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Length", String.valueOf(buff.length));
			conn.setRequestProperty("content-type", "text/html");
			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(buff);
			outStream.flush();
			outStream.close();
			if(conn.getResponseCode()==200){
				//printResponse(conn);
				return conn.getInputStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 使用HTTP的POST方法提交的表单.
	 * @param urlPath 请求路径
	 * @param params 请求参数
	 * @param encoding 请求参数编码
	 * @return  返回InputStream
	 * @throws Exception
	 * @author 史明松
	 * @update May 19, 2011 12:33:44 AM
	 */
	public static InputStream postForm(String urlPath, Map<String,String> params, String encoding){
		try {
			StringBuilder sb=new StringBuilder();
			for(Map.Entry<String,String> entry : params.entrySet()){
				sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(),encoding) );
				sb.append("&");
			}
			sb.deleteCharAt(sb.length()-1);
			byte[] data=sb.toString().getBytes();
			URL url=new URL(urlPath);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(8*1000);
			conn.setDoOutput(true);//发送post请求必须设置允许输出
			conn.setUseCaches(false);//不适用Cache
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");//维持长连接
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Length", String.valueOf(data.length));
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			DataOutputStream dataOutStream=new DataOutputStream(conn.getOutputStream());
			dataOutStream.write(data);
			dataOutStream.flush();
			dataOutStream.close();
			if(conn.getResponseCode()==200){
				//printResponse(conn);
				return conn.getInputStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * 上传文件
	 * @param urlPath
	 * @param filePath
	 * @return 返回true 表示成功，false 失败
	 * @author 隽强
	 * @update 2012-7-31 下午6:11:38
	 */
	public static boolean postFile(String urlPath, String filePath){
		try {
			URL url = new URL(urlPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setChunkedStreamingMode(1024 * 1024);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			File file = new File(filePath);
			conn.setRequestProperty("Content-Type", "multipart/form-data;file="
					+ java.net.URLEncoder.encode(file.getName(),"UTF-8"));
			conn.setRequestProperty("filename", file.getName());		 
			OutputStream out = new DataOutputStream(conn.getOutputStream());
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			in.close();
			out.flush();
			out.close();
			printResponse(conn);
			
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * 获取返回信息.
	 * @param conn 
	 * @author 史明松
	 * @update Feb 7, 2012 6:18:42 PM
	 */
	public static String printResponse(HttpURLConnection conn){
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append("\n" + line);
			}
			System.out.println("==>" + sb);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
