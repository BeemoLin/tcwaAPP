package tw.brad.android.apps.eHomeApp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class Mail extends Activity {
	private static List<Map<String, String>> maillist = new ArrayList<Map<String, String>>();
	//private TextView msg;
	private ListView lvwMail;
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private TextView txtLine,txtLine2; 
	private final String mailForm="0";
	private final String communityForm="1";
	private final String noPicnName="---";
	private final int colsNum=5;
	private final int visible=0;
	private final int invisible=4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent it = getIntent();
		String msgtxt = it.getStringExtra("msg"); 
		String whichform = it.getStringExtra("whichform"); //0:mail;1:community
		
			if(whichform!=null){
				if(whichform.equals(communityForm)){
					setContentView(R.layout.community);//可由getStringExtra封包去判別要用哪個版面
					lvwMail=(ListView)findViewById(R.id.lvwMail);
					
					TextView txtMailType=(TextView)findViewById(R.id.txtMailType);
					txtMailType.setVisibility(invisible);
					lvwMail.setOnItemClickListener(new OnItemClickListener(){
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
							// TODO Auto-generated method stub
							   ListView listView = (ListView) arg0;
								//listView.getItemAtPosition(arg2).toString()
								int index=(int)arg3;
								Map<String, String> arowinfo= maillist.get(index);
								//map 1.keySet key的集合  2.values value的集合  
								Collection<String> get=arowinfo.values();
								for(String element :get){
									if(element!=noPicnName){
										//Log.i("AKAI", element);
										ShowCommunityPicView(element);
									}
									break;
								}
						}
					        
					   });
				
				}
				else{
					setContentView(R.layout.mail);
					lvwMail=(ListView)findViewById(R.id.lvwMail);
				}
				txtLine = (TextView)findViewById(R.id.textView1);
				txtLine2=(TextView)findViewById(R.id.textView2);
				ChangeLine(visible,invisible);
				
				AddMailList(msgtxt,whichform);  
			}	
	}

	private void ShowCommunityPicView(String picName){
			try{
				Intent GoPic = new Intent(Mail.this, Announcement.class);
				GoPic.putExtra("PicName",picName);
				//Log.i("AKAI", picName);
				startActivity(GoPic);
			}catch(Exception err){
			}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		 if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){    
			 ChangeLine(invisible,visible);
			 // txtLine.setText("----------------------------------------------------------------------------------------") ;
		  }else{  
			  ChangeLine(visible,invisible);
			  //txtLine.setText("-----------------------------------------------------");
		  }
	}
	private void ChangeLine(int one,int two){
		 txtLine.setVisibility(one);
		 txtLine2.setVisibility(two);
		/*VISIBLE:可见的，值0
		INVISIBLE:不可见，但占据一定空间布局，值4
		GONE:不可见，不占据任何空间布局，值8*/
	}
	
	private void AddMailList(String mailtxt,String whichform){
		//map--<key,value> List 列;Map 欄
		//String month=(intmonth<10)?"0"+String.valueOf(intmonth):String.valueOf(intmonth);
		////20130401更改封包格式:1@@12345{,,}testjjk{,,}郵件{,,}2013-03-19 11:18{;;}
		//String.valueOf:將任何型態轉成字串
		
		try{
			//Log.i("AKAI",mailtxt);
			maillist.clear();
			String[] amailpackage = mailtxt.split("kais;;kaie");
			  for(String packagetxt : amailpackage) {
				  String[] colmailinfo=packagetxt.split("kais,,kaie");
				  Map<String,String> map = new HashMap<String,String>();//增加行
				  //map.put("letternum",colmailinfo[0]);
				  map.put("recievename",colmailinfo[1]);
				  map.put("sendname",colmailinfo[2]);
				  if(whichform.equals(mailForm)){//郵件
					  map.put("lettertype",colmailinfo[3]);
				  }else{//社區
					  map.put("lettertype"," ");//" "空字串
				  }
				  map.put("recievedate",colmailinfo[4]);
				  if(whichform.equals(communityForm)){
					  
					  if(colmailinfo.length>colsNum){//代表有圖檔
						  map.put("picname",colmailinfo[colsNum]);
					  }else{
						  map.put("picname",noPicnName);
					  }
				  }
				  maillist.add(0, map);
			    }
		     ShowLvwMail(maillist,whichform);
		}
		catch(Exception err){
			
		}
	}
	private void ShowLvwMail(List<Map<String, String>> list,String whichform){
	//----資料2維:列與行	
    //將Adapter中的資料給 listView
		//SimpleAdapter adapter;
		 //if(whichform.equals("0")){//郵件
		SimpleAdapter   adapter =new SimpleAdapter(this, list,R.layout.simple_adapter, 
				  new String[]{"recievename","sendname","lettertype","recievedate"},  new int[]{R.id.txtRecieveName ,R.id.txtSendName, R.id.txtMailType,R.id.txtRecieveDate });
		 //}else{//社區公告
		//	 adapter =new SimpleAdapter(this, list,R.layout.simple_adapter, 
		//			  new String[]{"recievename","sendname","recievedate"},  new int[]{R.id.txtRecieveName ,R.id.txtSendName,R.id.txtRecieveDate });
		 //}
		  lvwMail.setAdapter(adapter);	
		  //增加點下去有圖示功能
		  //lvwMail.setSelection(0);//改變列表顏色
	}
	private void Mark(){
		/*
		@Override
		public void onBackPressed() {
			try{
				//不給返回;要按正面按鈕才能離開
			}
			catch(Exception err){
				
				
			}
			 finally{
				//finish();
			}
			
			
		}*/
	}
	
}
