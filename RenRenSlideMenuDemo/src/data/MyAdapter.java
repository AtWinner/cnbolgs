package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.renrenslidemenudemo.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	private LayoutInflater inflator;
	private Element singerListDiv;
	public MyAdapter(Element singer){
		super();
		singerListDiv = singer;
	}
	public List<Map<String, Object>> GetItems()
	{
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> title = new HashMap<String, Object>();
		title.put("Row", "RowNumber");
		Log.e("","2");
		Elements links = singerListDiv.getElementsByClass("titlelnk"); 
		String Text="";
		for (Element link: links) {
			String EleClass = link.attr("class").trim();
			String Right = "titlelnk"; 
			if(EleClass.equals(Right))
			{
	            String linkHref = link.attr("href"); 
	            String linkText = link.text().trim();
	            //Text += (link.attr("class")+","+linkHref+"\n"+linkText+"\n");
	            //Text += (linkText+"\n");
	            //TextView tx = new TextView(getApplicationContext());
	            Map<String, Object> item = new HashMap<String, Object>();
	            item.put("row", linkText);
	            item.put("URL", linkHref);
	            items.add(item);
			}
		}
		return items;
	}
	
	public List<Map<String, Object>> GetContents()
	{
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> title = new HashMap<String, Object>();
		title.put("Row", "RowNumber");
		
		Log.e("","2");
		Elements links = singerListDiv.getElementsByTag("a"); 
		String Text="";
		for (Element link: links) { 
			String linkHref = link.attr("href");
			String linkText = link.text().trim();
			// Text += (link.attr("class")+","+linkHref+"\n"+linkText+"\n");
			// Text += (linkText+"\n");
			// TextView tx = new TextView(getApplicationContext());
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("row", linkText);
			item.put("URL", linkHref);
			items.add(item);

		}
		return items;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
