package com.example.renrenslidemenudemo;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.network_.GetHtml;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

public class Detail extends Activity {
	private String URL;
	private int myBlack;
	private GetHtml GM;
	private String link;//ͼƬ·��
	private LinearLayout layout;
	private ScrollView scrollview;
	private LinearLayout.LayoutParams params;
	private int PictureNum1;
	private int PictureNum2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_main);
		URL = getIntent().getStringExtra("URL");
		Resources resources = getBaseContext().getResources();  //���Ĵ���
		myBlack = resources.getColor(R.color.Black);
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		scrollview = new ScrollView(this);
		scrollview.setScrollbarFadingEnabled(false);
		PictureNum1 = 0;
		PictureNum2 = 0;
		SetLayout();//��ҳ���е����ݽ��г�ʵ
		scrollview.addView(layout,params);
		addContentView(scrollview,params);
	}
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg)
		{
			try{
				Bitmap BM = (Bitmap)msg.obj;
				ImageView IV= new ImageView(Detail.this);
				IV.setImageBitmap(BM);
				layout.addView(IV, params);
				Log.e("", "1");
				//addContentView(layout,params);
				TextView HandleTV = new TextView(Detail.this);
				HandleTV.setText("ͼƬ"+(++PictureNum2));
				HandleTV.setGravity(Gravity.CENTER);
				layout.addView(HandleTV,params);
				
			}
			catch(Exception e)
			{
				Log.e("", e.getMessage());
			}
		}
	};
	private void SetLayout()
	{
		//�����ı���ҳ���е���ʾ
		GetHtml GT = new GetHtml();
		Element singerListDiv = GT.GetDoc(URL).getElementsByAttributeValue("class", "post").first(); 
		Elements Divcnbolgs_code = singerListDiv.getElementsByClass("cnblogs_code");
		Elements Divcode = singerListDiv.getElementsByClass("code");
		Elements images = singerListDiv.getElementsByTag("img");
		String HtmlStr = singerListDiv.html();
//		Log.e("", "msgaaa");
//		for(Element span : spans)
//		{
//			Log.e("", "msg");
//			String Text = span.text();
//			TextView TT = new TextView(this);
//			TT.setText(Text);
//			TT.setTextColor(myBlack);
//			layout.addView(TT, params);
//		}
		for(Element image : images)
		{
			link = image.attr("src");
			
			if((!image.attr("alt").equals("���ƴ���"))&&(!image.attr("class").equals("code_img_closed"))&&(!image.attr("class").equals("code_img_opened")))
			{
				new Thread() {
					public void run() {
						Bitmap BB = GetHtml.returnBitMap(link);
						mHandler.obtainMessage(1, BB).sendToTarget();
					}
				}.start();
				//��ͼƬ��HTML�滻��һ����ʾ
				String ActionSpan = "<font color='#ff0000' style='display:block;'>ͼƬ"+ (++PictureNum1) + "����ײ���¼</font></br>";
				HtmlStr = HtmlStr.replace(image.toString(), ActionSpan);
				
			}
			
			//Toast.makeText(getApplicationContext(), HtmlStr, Toast.LENGTH_LONG).show();
			
		}
		for(Element Coder : Divcnbolgs_code)
		{//��һ�����͵Ĵ���
			
		}
		for(Element Coder : Divcode)
		{//����ڶ������͵Ĵ���
			
		}
		TextView TT = new TextView(this);
		//TT.setHeight(900);
		TT.setMovementMethod(ScrollingMovementMethod.getInstance());
		//TT.setScrollbarFadingEnabled(false);
		TT.setText(Html.fromHtml(HtmlStr));
		layout.addView(TT, params);
		TT.setTextColor(myBlack);
		String width = getWindowManager().getDefaultDisplay().getWidth() +"";
		
		//Toast.makeText(getApplicationContext(), width, Toast.LENGTH_LONG).show();
		
	}
	

}
