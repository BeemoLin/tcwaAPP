package tw.brad.android.apps.eHomeApp;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class NoticeService extends Service {
	private Timer timer,timerMail;
	private SharedPreferences sp;
	private String username, passwd, phonenum, phoneid;
	private SharedPreferences.Editor editor;
	//----使用陣列----
	private static Boolean[] initFlag={false,false};
	private static long[] cycleCount={0,0};
	private final long[] refreshCount={1439,1439};
	private boolean isStartflag=true;
	/*由資料夾android:去每10sec搜尋alarm與mail訊號*/
	private final String serverUrl="http://app.lohaslife.com.tw/uj001/android/";
	enum whichConnect {
	    conmail, conannouncement 
	} //0郵件 1社區
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		
		/*if(isStartflag){
			isStartflag=false;
		}else{
			timerMail = new Timer();	 
			timerMail.schedule(new SeekMailTask(), 10);	
		}*/
	}
	
	//==============取得帳號密碼與啟動Timer開始到appserver抓資料比對(使用編號比對)==========
	@Override
	public void onCreate() {
		super.onCreate();
		
		timer = new Timer();	 
		int periodint=(int)(Math.random()*30)+10;
		String msg=String.valueOf(periodint);
		Log.i("AKAI","1.觸發時間:"+msg );
		timer.schedule(new MyTask(),  periodint*1000);	 				//郵件 3000
		//timer.schedule(new AnnouncementTask(),5000,1000 * 13 );		//社區公告
		
		sp = getSharedPreferences("ehome", MODE_PRIVATE);
		username = sp.getString("username", "xxx");
		passwd = sp.getString("passwd", "xxx");
		//phonenum = sp.getString("phonenum", "xxx");
		//phoneid = sp.getString("phoneid", "xxx");
		editor = sp.edit();
	}

	//==================10sec 比對資料一次=======================
	private class MyTask extends TimerTask {
		@Override
		public void run() {
			String ret="";
			//Random rdnnum=new Random();
			try {
				
				//sp = getSharedPreferences("ehome", MODE_PRIVATE);
				//---如果用其他帳號登入;要取得改變得值;service:oncreate只觸發一次等到結束service再次啟動才再觸發
				username = sp.getString("username", "xxx");
				passwd = sp.getString("passwd", "xxx");
				//phonenum = sp.getString("phonenum", "xxx");
				//phoneid = sp.getString("phoneid", "xxx");
				//editor = sp.edit();
				Log.i("AKAI", username);
				
				
				// 與遠端連線
				ret=RemoteConnect(whichConnect.conmail);
				
				timer.cancel(); 
				//因為住戶多,不能在同一時間點上大量連線,所以取亂數 讓連線時間錯開
				timer = new Timer();
				int periodint=(int)(Math.random()*50)+10;
				String msg=String.valueOf(periodint);
				Log.i("AKAI","2.觸發時間:"+msg );
				timer.schedule(new MyTask(),periodint*1000);
				
				if(ret.equals("err")) return;
				//Log.i("AKAI", ret);
				ProcessPackage(0,ret,whichConnect.conmail,"mailids");
				//四小時重新顯示郵件
				//AddcycleCount(0);
				
			    
			    
			}catch(Exception ee){
				Log.i("AKAI", ee.toString());
			}
			
		}
		
	}
	
	private class AnnouncementTask extends TimerTask {
		@Override
		public void run() {
			String ret="";
			try {
				// 與遠端連線
				ret=RemoteConnect(whichConnect.conannouncement);
				if(ret.equals("err")) return;
				
				ProcessPackage(1,ret,whichConnect.conannouncement,"announcementlids");
				//四小時重新顯示郵件
				AddcycleCount(1);
			}catch(Exception ee){
				
			}
		}
	}
	private void AddcycleCount(int index){
		cycleCount[index]+=1;
		if(cycleCount[index]>refreshCount[index]) {
			cycleCount[index]=0;
			initFlag[index]=false;
		}
	}
	
	
	
	//============連到appserver並取得PHP執行的結果==================
	private String RemoteConnect(whichConnect con){
		String weburl="";
		String ret="";
		try{
		switch(con){
		case conmail:
			//不同設區要修改
			weburl=serverUrl+"publishalarmandmail.php";
			//"http://114.35.102.244/cctest/checkmail.php";
            break;
		case conannouncement:
			//不同設區要修改
			weburl=serverUrl+"checkannouncement.php";
			//weburl="http://114.35.102.244/cctest/checkannouncement.php";
			break;
		}
		
		
		// 與遠端連線
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(weburl);
		
		StringBody strUsername = new StringBody(username);
		StringBody strPasswd = new StringBody(passwd);
		//StringBody strPhonenum = new StringBody(phonenum);
		//StringBody strPhoneid = new StringBody(phoneid);
		
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("username", strUsername);
		entity.addPart("passwd", strPasswd);
		//entity.addPart("phonenum", strPhonenum);
		//entity.addPart("phoneid", strPhoneid);
		
		post.setEntity(entity);				
		
		HttpResponse res = client.execute(post);

	    ret = EntityUtils.toString(res.getEntity());			    
		 
		}catch(Exception ee){
			Log.i("AKAI", ee.toString());
			ret="err";
		}
		return ret;
	}
	
	//============處理封包並比對前值與新的資料===========================
	private void ProcessPackage(int index,String ret,whichConnect con,String parament){
		int version=1;
		String showTxt=" "; 
		switch(version) { 
	        case 1: 
	        	String[] colclass =ret.split("kais,,kaie");
	        	String[] prelids=new String[6];
	        	
	        	prelids[0] = sp.getString("mailids", "xxx");//String premailids
	        	Log.i("AKAI", prelids[0]);
	        	prelids[1] = sp.getString("frontdoorids", "xxx");//String prefrontdoorids
	        	prelids[2] = sp.getString("backdoorids", "xxx");//String prebackdoorids
	        	prelids[3] = sp.getString("fireids", "xxx");//String prefireids
	        	prelids[4] = sp.getString("helpids", "xxx");//String prehelpids
	        	prelids[5] = sp.getString("windowids", "xxx");//String prewindowids
			
	        	/*if(colclass[0].equals("N") && colclass[1].equals("N") && colclass[2].equals("N") && colclass[3].equals("N") && colclass[4].equals("N") && colclass[5].equals("N")){
	        		Log.i("AKAI", "NOmsg");
	        	}else{*/
	        		boolean alarmtile=true;
	        		for(int i=0;i<6;i++){
		        		if(!colclass[i].equals("N") && !prelids[i].equals(colclass[i])){
		        			if(i<1){
		        				String[] msg = colclass[i].split("@@");
		    	    			//String num = msg[0];
		        				showTxt+="您有"+msg[0]+"封掛號信,";
		        			}else{
		        				if(alarmtile){
		        					showTxt+="警報訊息:";
		        					alarmtile=false;
		        				}
		        				 switch (i){
		        			        case 1:
		        			        	showTxt+="大門;";
		        			          break;
		        			        case 2:
		        			        	showTxt+="後門;";
		        			          break;
		        			        case 3:
		        			        	showTxt+="火警;";
		        			          break;
		        			        case 4:
		        			        	showTxt+="緊急壓扣;";  
		        			          break;
		        			        case 5: 
		        			        	showTxt+="窗戶;";  
		        			          break;
		        			      }
		        			}

		        		}
	        		}
	        	//}
	        	editor.putString("mailids", colclass[0]);//String premailids
	        	//Log.i("AKAI", colclass[0]);
	        	editor.putString("frontdoorids", colclass[1]);//String prefrontdoorids
	        	editor.putString("backdoorids", colclass[2]);//String prebackdoorids
	        	editor.putString("fireids", colclass[3]);//String prefireids
	        	editor.putString("helpids", colclass[4]);//String prehelpids
	        	editor.putString("windowids", colclass[5]);
	        	editor.commit();
	        	
				if(!showTxt.equals(" ")){
					NoticeMsg(showTxt.substring(0, showTxt.length()-1));
				}
	        	break;
	        default: 
	        	if (ret.equals("0")){			
	    			// 沒有信件, 不做任何動作
	    			if(!initFlag[index]){
	    				//doNotice("0","");
	    				initFlag[index]=true;
	    			}
	    			else{
	    				String premailids = sp.getString(parament, "");
	    				
	    				if (!premailids.equals("")){
	    					doNotice(index,"0","");
	    				}	
	    			}
	    			editor.putString(parament, "");
	    			editor.commit();
	    		}
	    		else {
	    			// 有信件, 進行通知
	    			String[] msg = ret.split("@@");
	    			String num = msg[0];
	    			String packageid = msg[1];
	    			
	    			// 先檢查是否已經發過通知
	    			String prepackageid = sp.getString(parament, "");
	    			
	    			if(!initFlag[index]){
	    				doNotice(index,num,packageid);
	    				initFlag[index]=true;
	    				editor.putString(parament, packageid);
	    				editor.commit();
	    			}
	    			else{
	    				if (!packageid.equals(prepackageid)){
	    					editor.putString(parament, packageid);
	    					editor.commit();
	    					doNotice(index,num,packageid);
	    				}
	    			} 
	    		}
	    		//elseif(ret.equals("1"))無效使用著
	            break;        
		    }
	}
	private void doNotice(int index,String num,String packageid){
		String title=(index<1)?"信件通知":"社區公告";
		
		// 取得 NotificationManager 物件
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		// 設定通知的圖示, 通知提示, 通知顯示的時間
		Notification notification;
		if(index<1){
			 notification = new Notification(R.drawable.email_icon, title, System.currentTimeMillis());
		}
		else{
			notification = new Notification(R.drawable.announcement, title, System.currentTimeMillis());	
		}
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		// 通知啟動的 Intent 物件
		//Intent notificationIntent = new Intent(this, Mail.class);
		Intent notificationIntent = new Intent(this, Welcome.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent.putExtra("msg",packageid);//click時候;mailids還是保留原來的直
		notificationIntent.putExtra("whichform",String.valueOf(index));
		
		// 以 Intent 來取得 PendingIntent 物件
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		
		if(num.equals("0")){
			String context=(index<1)?"您已無信件":"已無";
			notification.setLatestEventInfo(this, title, context,
					contentIntent);
		}
		else{
			String context=(index<1)?"封信件未領取....":"則社區公告";
			notification.setLatestEventInfo(this, title, "您有 " + num + context,
					contentIntent);
		}
		// 發出通知
		mNotificationManager.notify(1, notification);
	}
	
	
	
	
	private void NoticeMsg(String title){
		// 取得 NotificationManager 物件
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		// 設定通知的圖示, 通知提示, 通知顯示的時間
		Notification notification;
		
		notification = new Notification(R.drawable.announcement, "訊息通知", System.currentTimeMillis());//R.drawable.email_icon
		
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		// 通知啟動的 Intent 物件
		//Intent notificationIntent = new Intent(this, Mail.class);
		Intent notificationIntent = new Intent(this, Welcome.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent.putExtra("msg",title);//click時候;mailids還是保留原來的直
		//notificationIntent.putExtra("whichform",String.valueOf(index));
		
		// 以 Intent 來取得 PendingIntent 物件
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
	
			String context=title;
			notification.setLatestEventInfo(this, "訊息通知", context,
					contentIntent);
		 
		// 發出通知
		mNotificationManager.notify(1, notification);
	}
	
	private class SeekMailTask extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String ret="";
			try {
				// 與遠端連線
				ret=RemoteConnect(whichConnect.conmail);
				if(ret.equals("err")) return;
				
				ProcessMailPackage(ret);
				//四小時重新顯示郵件
				//AddcycleCount(1);
				timerMail.cancel();
			}catch(Exception err){
				
			}
		}
	}
	private void ProcessMailPackage(String ret){
		String data;
		try{
			if (ret.equals("0")){			
				// 沒有信件, 不做任何動作
				data="";
			}
			else {
				
				// 有信件, 進行通知
				String[] msg = ret.split("@@");
				String num = msg[0];
				String packageid = msg[1];
				data=packageid;
			}
			BroadcastUi(data);
			
		}catch(Exception err){
			
			
		}
		
	}
	
	private void BroadcastUi(String data){
		Intent intent=new Intent();
		intent.putExtra("type", "Mail");
		intent.putExtra("package", data);
		intent.setAction("android.intent.action.Mail");//action与接收器相同
		sendBroadcast(intent);
	}
	
	private void Mark(){
	/*private class MyTask extends TimerTask {
	@Override
	public void run() {
		
		try {
			// 與遠端連線
			HttpClient client = new DefaultHttpClient();
			//HttpPost post = new HttpPost("http://192.168.1.104/ehome/checkmail.php");
			HttpPost post = new HttpPost("http://114.35.102.244/cctest/checkmail.php");
			
			StringBody strUsername = new StringBody(username);
			StringBody strPasswd = new StringBody(passwd);
			StringBody strPhonenum = new StringBody(phonenum);
			StringBody strPhoneid = new StringBody(phoneid);
			
			MultipartEntity entity = new MultipartEntity();
			entity.addPart("username", strUsername);
			entity.addPart("passwd", strPasswd);
			entity.addPart("phonenum", strPhonenum);
			entity.addPart("phoneid", strPhoneid);
			
			post.setEntity(entity);				
			
			HttpResponse res = client.execute(post);

		    String ret = EntityUtils.toString(res.getEntity());			    
			String pack = ret;
			
			
			if (ret.equals("0")){			
				// 沒有信件, 不做任何動作
				//Log.i("AKAI","無信件");
				if(!initFlag){
					//doNotice("0","");
					initFlag=true;
				}
				else{
					String premailids = sp.getString("mailids", "");
					
					if (!premailids.equals("")){
						doNotice("0","");
					}	
				}
				editor.putString("mailids", "");
				editor.commit();
			}
			else {
				// 有信件, 進行通知
				String[] msg = ret.split("@@");
				String num = msg[0];
				String mailids = msg[1];
				
				// 先檢查是否已經發過通知
				String premailids = sp.getString("mailids", "");
				
				if(!initFlag){
					doNotice(num,mailids);
					initFlag=true;
				}
				else{
					if (!mailids.equals(premailids)){
						
						editor.putString("mailids", mailids);
						editor.commit();
					
						doNotice(num,mailids);
					}
				} 
			}
			//elseif(ret.equals("1"))無效使用著
			//四小時重新顯示郵件
			cycleCount+=1;
			if(cycleCount>refreshCount) {
				cycleCount=0;
				initFlag=false;
			}
		}catch(Exception ee){
			
		}
	}
}

*private void doNotice(String num,String mailids){
	// 取得 NotificationManager 物件
	NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	
	// 設定通知的圖示, 通知提示, 通知顯示的時間
	Notification notification = new Notification(R.drawable.email_icon, "信件通知", System.currentTimeMillis());
	
	notification.flags = Notification.FLAG_AUTO_CANCEL;
	
	// 通知啟動的 Intent 物件
	Intent notificationIntent = new Intent(this, Mail.class);
 
 
	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	notificationIntent.putExtra("msg",mailids);//click時候;mailids還是保留原來的直

	
	// 以 Intent 來取得 PendingIntent 物件
	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
	if(num.equals("0")){
		notification.setLatestEventInfo(this, "信件通知", "您已無信件",
				contentIntent);
	}
	else{
		notification.setLatestEventInfo(this, "信件通知", "您有 " + num + "封信件未領取....",
				contentIntent);
	}
	// 發出通知
	mNotificationManager.notify(1, notification);
	
}
*/
}
	
}
