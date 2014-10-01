package tw.brad.android.apps.eHomeApp;

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

public class CommunityService extends Service {
	private Timer timer;
	private SharedPreferences sp;
	private String username, passwd, phonenum, phoneid;
	private SharedPreferences.Editor editor;
	//public static String commTxt;
	//----使用陣列----
	/*private static Boolean[] initFlag={false,false};
	private static long[] cycleCount={0,0};
	private final long[] refreshCount={1439,1439};*/
	private final String serverUrl="http://www.lohaslife.com.tw/cctest/";
	//"http://114.35.102.244/cctest/";
	private static Boolean initFlag;
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
		timer = new Timer();	 
		//timer.schedule(new MyTask(), 3000, 1000 * 10);	 				//郵件
		timer.schedule(new AnnouncementTask(),10);	
	}
	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("ehome", MODE_PRIVATE);
		username = sp.getString("username", "xxx");
		passwd = sp.getString("passwd", "xxx");
		phonenum = sp.getString("phonenum", "xxx");
		phoneid = sp.getString("phoneid", "xxx");
		
		editor = sp.edit();
		 
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
				//AddcycleCount(1);
				timer.cancel();
			}catch(Exception err){
				
			}
		}
	}
	private void Mark(){
		/*private void AddcycleCount(int index){
		cycleCount[index]+=1;
		if(cycleCount[index]>refreshCount[index]) {
			cycleCount[index]=0;
			initFlag[index]=false;
		}
	}*/
	}
	

	private String RemoteConnect(whichConnect con){
		String weburl="";
		String ret="";
		try{
		switch(con){
		case conmail:
			//不同設區要修改
			weburl=serverUrl+"checkmail.php";
            break;
		case conannouncement:
			//不同設區要修改
			weburl=serverUrl+"checkannouncement.php";
			//weburl="http://www.lohaslife.com.tw/cctest/checkannouncement.php";
			 
			break;
		}
		// 與遠端連線
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(weburl);
		
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

	    ret = EntityUtils.toString(res.getEntity());			    
		 
		}catch(Exception err){
			
			ret="err";
		}
		return ret;
	}
	
	private void ProcessPackage(int index,String ret,whichConnect con,String parament){
		String data;
		try{
			if (ret.equals("0")){			
				// 沒有信件, 不做任何動作
				Log.i("AKAI","無信件");
				data="";
			}
			else {
				
				// 有信件, 進行通知
				String[] msg = ret.split("@@");
				String num = msg[0];
				String packageid = msg[1];
				//Log.i("AKAI",packageid);
				data=packageid;
				//Log.i("AKAI",commTxt);
			}
			BroadcastUi(data);
			
		}catch(Exception err){
			
			
		}
		
		
	}
	private void BroadcastUi(String data){
		Intent intent=new Intent();
		intent.putExtra("type", "community");
		intent.putExtra("package", data);
		intent.setAction("android.intent.action.community");//action与接收器相同
		sendBroadcast(intent);
	}
	 
}
