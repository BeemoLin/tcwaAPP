package tw.brad.android.apps.eHomeApp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

//import com.mkyong.android.WebViewActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class Welcome extends Activity {
	//private ImageView img;
	private Button btnMail,btnCommunity,btnFix;
	private TimerTask task1, task2;
	private Timer timer1, timer2;
	private MyReceiver receiver;
	private String mailMsg,mailWhichform;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		//img = (ImageView)findViewById(R.id.img_welcome);
		btnMail=(Button)findViewById(R.id.btnMail);
		btnCommunity=(Button)findViewById(R.id.btnCommunity);
		btnFix=(Button)findViewById(R.id.btnFix);
		//btnFix.setVisibility(4);
		//GetMailNews();
		try {
			/*BufferedReader bin = 
					new BufferedReader( 
							new InputStreamReader(openFileInput("ehome.data")));
			bin.close();*/
			
			// 啟動郵件背景服務 註冊完startMyService所以此處不用
			//startMyService();
		
			btnMail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					GoRegister();
				}
			});
			btnCommunity.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					GoWebView("Front");
					
				}
			});
			btnFix.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					GoWebView("Back");
					
					//doOK();
					//ShowFix();
				}
			});
			//goCheck1();註解:把此畫面當最上一層
			//finish();
		}catch(Exception e){
			Log.i("AKAI", e.toString());
		}			
	}
	
	//==================================================================
	//設定帳號密碼:成功轉GoWebview與NoticeService(服務)
	private void GoRegister(){
		Intent intent = new Intent(Welcome.this, NewRegister.class);
		startActivity(intent);
		//finish();最上一層所以不用finish();
	}
	//登入前台:分前,後台(不顯示);
	private void GoWebView(String type){
		Intent intent = new Intent(Welcome.this, WebViewActivity.class);
		if(type.equals("Front")){
			intent.putExtra("msg","Front");
		}else if(type.equals("Back")){
			intent.putExtra("msg","Back");
		}
		startActivity(intent);
		//finish();最上一層所以不用finish();
	}
	//==================================================================
	
	private void GetMailNews(){
		try{
			Intent it = getIntent();
			mailMsg = it.getStringExtra("msg"); 
			mailWhichform = it.getStringExtra("whichform"); 
			mailMsg=(mailMsg!=null)?mailMsg:"";
			mailWhichform=(mailWhichform!=null)?"0":"0";
			Log.i("AKAI",mailMsg);
			Log.i("AKAI",mailWhichform);
		}catch(Exception err){
			Log.i("AKAI","Wrong");
		}
	}
	private void ShowMail(){
		registerMailReceiver();
		GoMailService();
	}
	private void registerMailReceiver(){
		//-----註冊接收器-----
		try{
		receiver=new MyReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.Mail");
		Welcome.this.registerReceiver(receiver,filter);
		//-------------------	
		}catch(Exception err){
			
		}
	}
	private void GoMailService(){
		try{
		Intent se = new Intent(this, NoticeService.class);
		startService(se);
		}catch(Exception err){
			
		}
	}
	private void ShowMailView(String mailpackage){
		try{
			Intent GoMail = new Intent(Welcome.this, Mail.class);
			GoMail.putExtra("msg",mailpackage);
			GoMail.putExtra("whichform","0");
			startActivity(GoMail);
		}catch(Exception err){
		}
	}
	
	
	private void ShowCommunity() {
		registerCommunityReceiver();
		GoCommunityService();
	}
	private void registerCommunityReceiver(){
		//-----註冊接收器-----
		try{
		receiver=new MyReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.community");
		Welcome.this.registerReceiver(receiver,filter);
		//-------------------	
		}catch(Exception err){
			
		}
	}
	private void GoCommunityService(){
		try{
		Intent se = new Intent(this, CommunityService.class);
		startService(se);
		}catch(Exception err){
			
		}
	}
	private void ShowCommunityView(String communitypackage){
		try{
		Intent GoCommunity = new Intent(Welcome.this, Mail.class);
		GoCommunity.putExtra("msg",communitypackage);
		GoCommunity.putExtra("whichform","1");
		startActivity(GoCommunity);
		}catch(Exception err){
			
		}
	}
	private void startMyService(){
		Intent se = new Intent(this, NoticeService.class);
		startService(se);
		//Log.i("test", "startMyService");
	}
	
	private class MyReceiver extends BroadcastReceiver {
		//自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try{
				Bundle bundle=intent.getExtras();
				String type=bundle.getString("type");
				String typepackage=bundle.getString("package");
				Welcome.this.unregisterReceiver(receiver);//解除註冊
				
				 
				if(type.equals("Mail")){
					ShowMailView(typepackage);
					 
				}else{
					ShowCommunityView(typepackage);
					 
				}
				
			}catch(Exception err){
				
			}
			
		}
		/*public MyReceiver(){
			System.out.println("MyReceiver");
			//初始化工作
		}*/
 
	}
	
	private void ShowFix(){
	
	}
	
	private void goCheck(){
		task1 = new TimerTask(){ 
			public void run(){				
					GoRegister();					
					//Log.i("test", "尚未註冊");
					cancel();
			     }    
			 };    
		 timer1 = new Timer();  
			   timer1.schedule(task1, 100);			   
	}

	private void goCheck1(){
		task2 = new TimerTask(){ 
			public void run(){				
					startIndex();					
					//Log.i("test", "test2");
					cancel();
			     }    
			 };    
		timer2 = new Timer();  
			   timer2.schedule(task2, 3000);			   
	}
	
	private void startIndex(){
		//Intent ix = new Intent(this, Index.class);
		Intent ix = new Intent(this,Mail.class);
		startActivity(ix);
		//Log.i("test", "startIndex");
		finish();
	}

	private void Mark(){
		/*catch (FileNotFoundException e) {
		// 視為尚未註冊
		goCheck(); 			
	} catch (IOException e) {
		// 存取失敗
		//Log.i("test", "I/O ERROR");
		//Toast.makeText(this, "I/O ERROR", Toast.LENGTH_SHORT).show();
	}*/	
	}
}
