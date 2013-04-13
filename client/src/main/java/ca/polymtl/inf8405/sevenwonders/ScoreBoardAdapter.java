package ca.polymtl.inf8405.sevenwonders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.polymtl.inf8405.sevenwonders.model.PlayerInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class ScoreBoardAdapter extends SimpleAdapter{

	private List<PlayerInfo> scores_ = new ArrayList<PlayerInfo>();
	private int[] colors = new int[] { 0x30ffffff, 0x30808080 };
	
	public ScoreBoardAdapter(Context context, List<? extends Map<String, String>> data,
			 int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		scores_ = (List<PlayerInfo>) data;
	}
	
	public void updateScore(List<PlayerInfo> newInfo){
		scores_ = newInfo;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  View view = super.getView(position, convertView, parent);

	  int colorPos = position % colors.length;
	  view.setBackgroundColor(colors[colorPos]);
	  return view;
	}

}
