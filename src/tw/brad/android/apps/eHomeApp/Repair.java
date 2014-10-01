package tw.brad.android.apps.eHomeApp;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Repair extends Activity {
	Spinner pos, type, detail;
	SimpleAdapter posAdapter, typeAdapter, detailAdapter;
	String[] pos_data = {
			"請選擇", "玄關", "客廳", "餐廳", "廚房",
	};
	String[][] type_data ={
			{"請選擇", "玄關-0", "玄關-1", "玄關-2", "玄關-3"},
			{"請選擇", "客廳-0", "客廳-1", "客廳-2", "客廳-3"},
			{"請選擇", "餐廳-0", "餐廳-1", "餐廳-2", "餐廳-3"},
			{"請選擇", "廚房-0", "廚房-1", "廚房-2", "廚房-3"}
	};
	Button ok, back;
	EditText tel;
	/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repair);
		
		pos = (Spinner)findViewById(R.id.spinner_pos);
		type = (Spinner)findViewById(R.id.spinner_type);
		detail = (Spinner)findViewById(R.id.spinner_detail);
		tel = (EditText)findViewById(R.id.Explain);
		
		String[] from = {"posItem"};
		int[] to = {R.id.item_pos};
		
		ArrayList<HashMap<String,String>> data = 
				new ArrayList();
		for (int i=0; i< pos_data.length; i++){
			HashMap<String,String> dd = new HashMap();
			dd.put(from[0], pos_data[i]);
			data.add(dd);
		}
		
		posAdapter = new SimpleAdapter(this, 
				data, R.layout.positem, from, to);
		pos.setAdapter(posAdapter);
		pos.setPadding(0, 8, 0, 8);
		pos.setPrompt("請選擇");
		pos.setSelection(-1);
		pos.setSelected(false);		
		
		pos.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int index, long arg3) {
				chPosToType(index);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		ok = (Button)findViewById(R.id.ok);
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doOK();
			}
		});

		back = (Button)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAnon();
				Repair.this.finish();
			}
		});
		
	}
	
	private void showAnon(){
		Intent it = new Intent(this, Announcement.class);
		startActivity(it);
		
	}
	
	private void doOK(){
		int pos_sel = pos.getSelectedItemPosition();
		String pos_ok = pos_data[pos_sel];
		String type_ok = type_data[pos_sel][type.getSelectedItemPosition()];
		String tel_ok = tel.getText().toString();
		
		Toast.makeText(this, pos_ok + ":" + type_ok + ":" +
						tel_ok, Toast.LENGTH_LONG).show();		
	}
	
	/*private void chPosToType(int index){
		String[] from = {"posItem"};
		int[] to = {R.id.item_pos};
		
		ArrayList<HashMap<String,String>> data = 
				new ArrayList();
		for (int i=0; i< type_data[index].length; i++){
			HashMap<String,String> dd = new HashMap();
			dd.put(from[0], type_data[index][i]);
			data.add(dd);
		}
		
		typeAdapter = new SimpleAdapter(this, 
				data, R.layout.positem, from, to);
		type.setAdapter(typeAdapter);
		type.setPadding(0, 8, 0, 8);
		type.setPrompt("請選擇");
		type.setSelection(-1);
		type.setSelected(false);	
	}*/
}

