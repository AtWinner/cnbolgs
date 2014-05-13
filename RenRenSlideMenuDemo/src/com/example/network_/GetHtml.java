package com.example.network_;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;

public class GetHtml 
{
	private static final int MSG_SUCCESS = 0;// 获取成功的标识
	private static final int MSG_FAILURE = 1;// 获取失败的标识
	private static final int MSG_JsoupDOC = 2;//获取title
	
	private Document PrivateDoc;
	
	public static String HtmlStr;
	
	public String GetTitle(final String URL)
	{
		
//		new Thread(){
//			public void run(){
				try{
					Log.e("", "1");
					Document doc = Jsoup.connect(URL).get();
					PrivateDoc = doc;
					Log.e("", "2");
//					mHandler.obtainMessage(MSG_JsoupDOC, doc).sendToTarget();
				}
				catch(IOException e){
					Log.e("",e.getMessage());
				}
//			}
//		};
		return PrivateDoc.title();
	}
	public Document GetDoc(final String URL)
	{
		try{
			PrivateDoc = Jsoup.connect(URL).get();
		}
		catch(IOException e)
		{
			
		}
		return PrivateDoc;
	}
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case MSG_SUCCESS:
					HtmlStr = msg.obj.toString();break;
				case MSG_FAILURE:
					HtmlStr = msg.obj.toString();break;
				case MSG_JsoupDOC:
					PrivateDoc = (Document) msg.obj;break;
			}
		}
	};
	
	public String GetHtmlString()
	{//默认访问百度
		String testStr = "aa";
				try {
					
					testStr = testGetHtml("http://www.cnblogs.com");
			
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw, true));
					String str = sw.toString();
					
				}
		return testStr;
	}
	public String GetHtmlString(final String URL)
	{
		new Thread(){
			public void run(){
				try {
					String testStr="aa";
					testStr = testGetHtml(URL);
					mHandler.obtainMessage(MSG_SUCCESS, testStr).sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter sw = new StringWriter();  
		            e.printStackTrace(new PrintWriter(sw, true));  
		            String str = sw.toString();  
					mHandler.obtainMessage(MSG_FAILURE, str).sendToTarget();
				}
			}
		}.start();
		return HtmlStr;
	}
	public byte[] readStream(InputStream inputStream) throws Exception 
	{
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		while ((len = inputStream.read(buffer)) != -1) 
		{
			byteArrayOutputStream.write(buffer, 0, len);
		}

		inputStream.close();
		byteArrayOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}

	public String testGetHtml(String urlpath) throws Exception 
	{
		URL url = new URL(urlpath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(1000);//超时时间1秒钟
		conn.setRequestMethod("GET");

		if (conn.getResponseCode() == 200) 
		{
			InputStream inputStream = conn.getInputStream();
			byte[] data = readStream(inputStream);
			String html = new String(data);
			return html;
		}
		return "bb";
	}
	public static Bitmap returnBitMap(String url) 
	{   
		URL myFileUrl = null;   
		Bitmap bitmap = null;   
		try 
		{   
			myFileUrl = new URL(url);   
		} 
		catch (MalformedURLException e) 
		{   
			e.printStackTrace();   
		}   
		try 
		{   
		    HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();   
		    conn.setDoInput(true);   
		    conn.connect();   
		    InputStream is = conn.getInputStream();   
		    bitmap = BitmapFactory.decodeStream(is);   
		    is.close();   
		} 
		catch (IOException e) 
		{   
			e.printStackTrace();   
		}   
		return bitmap;   
	} 
}
