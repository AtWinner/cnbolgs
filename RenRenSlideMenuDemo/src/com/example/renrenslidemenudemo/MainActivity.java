package com.example.renrenslidemenudemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.network_.GetHtml;

import data.MyAdapter;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.EventLog.Event;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class MainActivity extends Activity implements OnTouchListener {

	/**
	 * 滚动显示和隐藏menu时，手指滑动需要达到的速度。
	 */
	public static final int SNAP_VELOCITY = 200;

	/**
	 * 屏幕宽度值。
	 */
	private int screenWidth;

	/**
	 * menu最多可以滑动到的左边缘。值由menu布局的宽度来定，marginLeft到达此值之后，不能再减少。
	 */
	private int leftEdge;

	/**
	 * menu最多可以滑动到的右边缘。值恒为0，即marginLeft到达0之后，不能增加。
	 */
	private int rightEdge = 0;

	/**
	 * menu完全显示时，留给content的宽度值。
	 */
	private int menuPadding = 80;

	/**
	 * 主内容的布局。
	 */
	private View content;

	/**
	 * menu的布局。
	 */
	private View menu;

	/**
	 * menu布局的参数，通过此参数来更改leftMargin的值。
	 */
	private LinearLayout.LayoutParams menuParams;

	/**
	 * 记录手指按下时的横坐标。
	 */
	private float xDown;

	/**
	 * 记录手指移动时的横坐标。
	 */
	private float xMove;

	/**
	 * 记录手机抬起时的横坐标。
	 */
	private float xUp;

	/**
	 * menu当前是显示还是隐藏。只有完全显示或隐藏menu时才会更改此值，滑动过程中此值无效。
	 */
	private boolean isMenuVisible;

	/**
	 * 用于计算手指滑动的速度。
	 */
	private VelocityTracker mVelocityTracker;
	private LinearLayout LayoutCHeadInside;
	private Button testButton;
	private String LVURL;//保存ListView中显示的URL
	private int PageNumber = 1;//控制页号
	protected int clickNum = 0;
	private Button contents;
	private Button MainWeb;
	private Button Onload;
	private Button FirstPage;
	private Button NextPage;
	private ListView LV;
	private TextView TVPageNumber;
	private GetHtml GT = new GetHtml();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initValues();
		try{
		SetViewEvent();
		}
		catch(Exception e)
		{
			Log.e("",e.getMessage());
		}
		SetLayout();
		content.setOnTouchListener(this);
	}
	/**
	 * 实现对控件查询以及事件 的绑定
	 */
	private void SetViewEvent(){
		Log.e("", "SetViewEventStart");
		testButton = (Button)findViewById(R.id.testButton);
		contents = (Button)findViewById(R.id.contentss);
		MainWeb = (Button)findViewById(R.id.MainWeb);
		Onload = (Button)findViewById(R.id.Onload);
		LV = (ListView)findViewById(R.id.show);
		FirstPage = (Button)findViewById(R.id.FirstPage);
		NextPage = (Button)findViewById(R.id.nextPage);
		TVPageNumber = (TextView)findViewById(R.id.TVPageNumber);
		LayoutCHeadInside = (LinearLayout)findViewById(R.id.LayoutCHeadInside);
		LVURL = "http://www.cnblogs.com/";
		SetLink();
		testButton.setOnClickListener(new OnClickEvent());
		contents.setOnClickListener(new OnClickEvent());
		MainWeb.setOnClickListener(new OnClickEvent());
		Onload.setOnClickListener(new OnClickEvent());
		FirstPage.setOnClickListener(new OnClickEvent());
		NextPage.setOnClickListener(new OnClickEvent());
		LV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
			{
				ListView listView = (ListView)arg0;  
				HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(arg2);  
				String FinalURL = map.get("URL");
				if(!FinalURL.substring(0, 1).equals("h"))
				{//表示目录
					FinalURL = "http://www.cnblogs.com" + FinalURL;
					setListView(FinalURL);
					FirstPage.setEnabled(true);
					NextPage.setEnabled(true);
					PageNumber = 1;
				}
				else
				{//表示非目录，是标题
					Intent intent = new Intent();
					intent.putExtra("URL", FinalURL);
					intent.setClass(MainActivity.this, Detail.class);
					MainActivity.this.startActivity(intent);
					//MainActivity.this.finish();
				}
				LVURL = FinalURL;
				//Toast.makeText(getApplicationContext(), FinalURL, Toast.LENGTH_LONG).show();
			}
		});
		Log.e("", "SetViewEventSuccess");
	}
	/**
	 * 初始化一些关键性数据。包括获取屏幕的宽度，给content布局重新设置宽度，给menu布局重新设置宽度和偏移距离等。
	 */
	private void initValues() {
		WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		screenWidth = window.getDefaultDisplay().getWidth();
		content = findViewById(R.id.content);
		menu = findViewById(R.id.menu);
		testButton = (Button) findViewById(R.id.testButton);
		menuParams = (LinearLayout.LayoutParams) menu.getLayoutParams();
		// 将menu的宽度设置为屏幕宽度减去menuPadding
		menuParams.width = screenWidth - menuPadding;
		// 左边缘的值赋值为menu宽度的负数
		leftEdge = -menuParams.width;
		// menu的leftMargin设置为左边缘的值，这样初始化时menu就变为不可见
		menuParams.leftMargin = leftEdge;
		// 将content的宽度设置为屏幕宽度
		content.getLayoutParams().width = screenWidth;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		createVelocityTracker(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 手指按下时，记录按下时的横坐标
			xDown = event.getRawX();
			break;
		case MotionEvent.ACTION_MOVE:
			// 手指移动时，对比按下时的横坐标，计算出移动的距离，来调整menu的leftMargin值，从而显示和隐藏menu
			xMove = event.getRawX();
			int distanceX = (int) (xMove - xDown);
			if (isMenuVisible) {
				menuParams.leftMargin = distanceX;
			} else {
				menuParams.leftMargin = leftEdge + distanceX;
			}
			if (menuParams.leftMargin < leftEdge) {
				menuParams.leftMargin = leftEdge;
			} else if (menuParams.leftMargin > rightEdge) {
				menuParams.leftMargin = rightEdge;
			}
			menu.setLayoutParams(menuParams);
			break;
		case MotionEvent.ACTION_UP:
			// 手指抬起时，进行判断当前手势的意图，从而决定是滚动到menu界面，还是滚动到content界面
			xUp = event.getRawX();
			if (wantToShowMenu()) {
				if (shouldScrollToMenu()) {
					scrollToMenu();
				} else {
					scrollToContent();
				}
			} else if (wantToShowContent()) {
				if (shouldScrollToContent()) {
					scrollToContent();
				} else {
					scrollToMenu();
				}
			}
			recycleVelocityTracker();
			break;
		}
		return true;
	}
	/**
	 * 判断当前手势的意图是不是想显示content。如果手指移动的距离是负数，且当前menu是可见的，则认为当前手势是想要显示content。
	 * 
	 * @return 当前手势想显示content返回true，否则返回false。
	 */
	private boolean wantToShowContent() {
		return xUp - xDown < 0 && isMenuVisible;
	}

	/**
	 * 判断当前手势的意图是不是想显示menu。如果手指移动的距离是正数，且当前menu是不可见的，则认为当前手势是想要显示menu。
	 * 
	 * @return 当前手势想显示menu返回true，否则返回false。
	 */
	private boolean wantToShowMenu() {
		return xUp - xDown > 0 && !isMenuVisible;
	}

	/**
	 * 判断是否应该滚动将menu展示出来。如果手指移动距离大于屏幕的1/2，或者手指移动速度大于SNAP_VELOCITY，
	 * 就认为应该滚动将menu展示出来。
	 * 
	 * @return 如果应该滚动将menu展示出来返回true，否则返回false。
	 */
	private boolean shouldScrollToMenu() {
		return xUp - xDown > screenWidth / 2
				|| getScrollVelocity() > SNAP_VELOCITY;
	}

	/**
	 * 判断是否应该滚动将content展示出来。如果手指移动距离加上menuPadding大于屏幕的1/2，
	 * 或者手指移动速度大于SNAP_VELOCITY， 就认为应该滚动将content展示出来。
	 * 
	 * @return 如果应该滚动将content展示出来返回true，否则返回false。
	 */
	private boolean shouldScrollToContent() {
		return xDown - xUp + menuPadding > screenWidth / 2
				|| getScrollVelocity() > SNAP_VELOCITY;
	}

	/**
	 * 将屏幕滚动到menu界面，滚动速度设定为30.
	 */
	private void scrollToMenu() {
		new ScrollTask().execute(30);
	}

	/**
	 * 将屏幕滚动到content界面，滚动速度设定为-30.
	 */
	private void scrollToContent() {
		new ScrollTask().execute(-30);
	}

	/**
	 * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
	 * 
	 * @param event
	 *            content界面的滑动事件
	 */
	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	/**
	 * 获取手指在content界面滑动的速度。
	 * 
	 * @return 滑动速度，以每秒钟移动了多少像素值为单位。
	 */
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getXVelocity();
		return Math.abs(velocity);
	}

	/**
	 * 回收VelocityTracker对象。
	 */
	private void recycleVelocityTracker() {
		mVelocityTracker.recycle();
		mVelocityTracker = null;
	}

	class ScrollTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... speed) {
			int leftMargin = menuParams.leftMargin;
			// 根据传入的速度来滚动界面，当滚动到达左边界或右边界时，跳出循环。
			while (true) {
				leftMargin = leftMargin + speed[0];
				if (leftMargin > rightEdge) {
					leftMargin = rightEdge;
					break;
				}
				if (leftMargin < leftEdge) {
					leftMargin = leftEdge;
					break;
				}
				publishProgress(leftMargin);
				// 为了要有滚动效果产生，每次循环使线程睡眠20毫秒，这样肉眼才能够看到滚动动画。
				sleep(20);
			}
			if (speed[0] > 0) {
				isMenuVisible = true;
			} else {
				isMenuVisible = false;
			}
			return leftMargin;
		}

		@Override
		protected void onProgressUpdate(Integer... leftMargin) {
			menuParams.leftMargin = leftMargin[0];
			menu.setLayoutParams(menuParams);
		}

		@Override
		protected void onPostExecute(Integer leftMargin) {
			menuParams.leftMargin = leftMargin;
			menu.setLayoutParams(menuParams);
		}
	}

	/**
	 * 使当前线程睡眠指定的毫秒数。
	 * 
	 * @param millis
	 *            指定当前线程睡眠多久，以毫秒为单位
	 */
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressLint("NewApi")
	private void SetLayout()
	{
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		//Log.e("", screenWidth + "," + screenHeight);
		//LayoutCHead.
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) LayoutCHeadInside.getLayoutParams();
		params.height=screenHeight/10;
		params.width=screenHeight/10;
		LayoutCHeadInside.setLayoutParams(params);
		params = (LinearLayout.LayoutParams) LV.getLayoutParams();
		params.height = screenHeight * 7 / 10;
		LV.setLayoutParams(params);
		int a = testButton.getWidth();
		int b = testButton.getHeight();
		
		Log.e("", "SetLayoutSucess");
	}
	private void SetLink()
	{
		Log.e("", "SetLinkStart");
		Element singerListDiv = GT.GetDoc(LVURL).getElementsByAttributeValue("id", "post_list").first(); 
		setListView(singerListDiv);
		//MyAdapter adapter = new MyAdapter(singerListDiv, this);
//		Log.e("","2");
//		Elements links = singerListDiv.getElementsByTag("a"); 
//		String Text="";
//		for (Element link: links) { 
//
//			String EleClass = link.attr("class").trim();
//			String Right = "titlelnk";
//			if(EleClass.equals(Right))
//			{
//				Log.e("",link.className());
//	            String linkHref = link.attr("href"); 
//	            String linkText = link.text().trim();
//	            //Text += (link.attr("class")+","+linkHref+"\n"+linkText+"\n");
//	            Text += (linkText+"\n");
//	            //TextView tx = new TextView(getApplicationContext());
//	            TextView tx = new TextView(getApplication());
//	            tx.setText(linkText);
//	            setContentView(tx);
//			}
//		}
//		
		Log.e("", "SetLinkSucess");
	}
	private class OnClickEvent implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch(v.getId())
			{
			case R.id.MainWeb:
				LVURL = "http://www.cnblogs.com/";
				setListView(GT.GetDoc(LVURL).getElementsByAttributeValue("id", "post_list").first());
				scrollToContent();
				FirstPage.setEnabled(true);
				NextPage.setEnabled(true);
				PageNumber = 1;
				break;
			case R.id.Onload:
				OnLoadClick();
				break;
			case R.id.contentss:
				contentsClick();
				scrollToContent();
				break;
			case R.id.testButton:
				testButtonClick();
				break;
			case R.id.FirstPage:
				FirstPageClick();
				break;
			case R.id.nextPage:
				NextPageClick();
				break;
			}
		}
	}
	private void testButtonClick()
	{
		if(clickNum % 2 == 0)
		{
			scrollToMenu();
			clickNum++;
		}
		else
		{
			scrollToContent();
			clickNum++;
		}
		
	}
	private void setListView(Element singerListDiv){
		MyAdapter MA = new MyAdapter(singerListDiv);
		SimpleAdapter sa=new SimpleAdapter(
				 this,
				 MA.GetItems(),
				 R.layout.line,
				 new String []{"row","URL"},
				 new int [] {R.id.ItemLink,R.id.ItemURL}
				 );
		LV.setAdapter(sa);
	}
	private void contentsClick()
	{
		LVURL = "http://www.cnblogs.com/";
		BindData();
		FirstPage.setEnabled(false);
		NextPage.setEnabled(false);
	}
	private void FirstPageClick()
	{
		if(PageNumber > 1)
		{
			BindData("");
			PageNumber = 1;
			TVPageNumber.setText("第1页");
		}
		
	}
	private void NextPageClick()
	{
		PageNumber++;
		if(LVURL.equals("http://www.cnblogs.com/"))
		{
			BindData("sitehome/p/" + PageNumber);
		}
		else
		{
			BindData("" + PageNumber);
		}
		TVPageNumber.setText("第"+PageNumber+"页");
	}
	/***
	 * 通过获取LVURL来修改ListView中的数据,获得的是目录而已
	 */
	private void BindData()
	{
		Element Ele = GT.GetDoc(LVURL).getElementsByAttributeValue("id", "cate_item").first(); 
		MyAdapter MA = new MyAdapter(Ele);
		SimpleAdapter sa = new SimpleAdapter(
				this,
				MA.GetContents(),
				R.layout.line,
				new String []{"row","URL"},
				new int [] {R.id.ItemLink, R.id.ItemURL});
		LV.setAdapter(sa);
	}
	private void BindData(String PageNum)
	{
		Element Ele = GT.GetDoc(LVURL+PageNum).getElementsByAttributeValue("id", "post_list").first(); 
		MyAdapter MA = new MyAdapter(Ele);
		SimpleAdapter sa = new SimpleAdapter(
				this,
				MA.GetItems(),
				R.layout.line,
				new String []{"row","URL"},
				new int [] {R.id.ItemLink, R.id.ItemURL});
		LV.setAdapter(sa);
		Log.e("", LVURL+PageNum);
	}
	private void setListView(String href){
		Element Ele = GT.GetDoc(href).getElementsByAttributeValue("id", "post_list").first(); 
		MyAdapter MA = new MyAdapter(Ele);
		 SimpleAdapter sa=new SimpleAdapter(
				 this,
				 MA.GetItems(),
				 R.layout.line,
				 new String []{"row","URL"},
				 new int [] {R.id.ItemLink,R.id.ItemURL}
				 );
		 LV.setAdapter(sa);
	}
	private void OnLoadClick()
	{
		LayoutInflater layoutinflater = LayoutInflater.from(this);
		View viewAddEmployee = layoutinflater.inflate(R.layout.onload_page, null);
		
		new AlertDialog.Builder(this)
		.setTitle("登录中...")
		.setView(viewAddEmployee)
		.setPositiveButton("确定" , new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				Onload.setText(R.string.Quit);
			}
		})  
		.setNegativeButton("取消" , new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		})  
		.show();
	}
}

