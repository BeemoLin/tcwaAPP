package tw.brad.android.apps.eHomeApp;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ShowPicService extends Service {
	private Timer timer;
	private String picName="";
	private static Boolean initFlag;
	private static List<String> picList = new ArrayList<String>();
	private static List<Bitmap> pictureList = new ArrayList<Bitmap>();
	private static List<String> pictureTitleList = new ArrayList<String>();
	private int picindex=0;
	//不同設區要修改
	private static final String webServerUrl="http://www.lohaslife.com.tw/cctest/backstage/news/";
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		//picName="http://www.lohaslife.com.tw/cc77/backstage/news/20130208104553.jpg";
		String cmdMode=GetCMD(intent);
		if(cmdMode.equals("Init")){
			LoadPic(intent);
		}else if(cmdMode.equals("ChangePic")){
			 final int index=GetPicIndex(intent);
			 Thread thread=new Thread(new Runnable() { 
					@Override
				    public void run(){ 
						//20130408
						GetABitmapFromURL(picList.get(index));
						BroadcastAShowPic(index);//single
						//BroadcastShowPic(index); //Load All 
				   } 
				});
				thread.start();
		}
	}
	@Override
	public void onCreate() {
		super.onCreate();
	}
	private void LoadPic(Intent intent){
		try{
			picList.clear();
			pictureList.clear();
			pictureTitleList.clear();
			for(String picname : GetPicName(intent)){
				//Log.i("AKAI", picname);
				picList.add(picname);
			}
			timer = new Timer();	 
			timer.schedule(new LoadPicTask(),100);
		}catch(Exception err){
			
		}
	}
	private class LoadPicTask extends TimerTask {//ShowPicTask
		@Override
		public void run() {
			try {
				 Thread thread=new Thread(new Runnable() { 
						@Override
					    public void run(){ 
							//20130408
							GetABitmapFromURL(picList.get(0));//single
							BroadcastAShowPic(0);
							/*GetBitmapFromURL(picList);//Load All 
							BroadcastShowPic(0);*/
					   } 
					});
					thread.start();
				//-----------------------------
				timer.cancel();
			}catch(Exception err){
			}
		}
	}
	
	private String GetCMD(Intent it){
		try{
			return it.getStringExtra("cmdMode");
		}catch(Exception err){
			return " ";
		}
	}
	private String[] GetPicName(Intent it){
			try{
				String picNamee = it.getStringExtra("PicName");
				//Log.i("AKAI", picNamee);
				return ProcessPicName(picNamee);
			}catch(Exception err){
				return null;
			}
	}
	private String[] ProcessPicName(String picname){
		int index=0;
		try{
			String[] picpackage = picname.split("kais::kaie");// .split("kais**kaie");
			String[] colpicinfo = new String[picpackage.length];
			for(String packagetxt : picpackage) {
				  String[] cols=packagetxt.split("kais..kaie");
				  colpicinfo[index]=cols[0];
				  if(cols.length>1) pictureTitleList.add(cols[1]);
				  index+=1;
			  }
			return colpicinfo;
		}catch(Exception err){
			Log.i("AKAI", err.toString());
			return null;
		}
	}
	private int GetPicIndex(Intent it){
		try{
			return it.getIntExtra("picIndex",0);
		}catch(Exception err){
			return 0;
		}
	}
	private class ShowPicTask extends TimerTask {//ShowPicTask
		@Override
		public void run() {
			try {
				BroadcastShowPic(picindex);
				timer.cancel();
			}catch(Exception err){
				
			}
		}
	}
	
	public static void GetABitmapFromURL(String src) {
        try {
            URL url = new URL(webServerUrl+src);
           // Log.i("AKAI", webServerUrl+src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            //Log.i("AKAI","returned");
            pictureList.clear();
            pictureList.add(myBitmap);
            //return myBitmap;
        } catch (Exception err) {
        	Log.i("AKAI", err.toString());
            //return null;
        }
    }
	private void BroadcastAShowPic(int index){
		try{
			Intent intent=new Intent();//要使用傳址
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			pictureList.get(0).compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			intent.putExtra("Image",byteArray);
			 if((index+1)<= pictureTitleList.size()) {
				 intent.putExtra("Title",pictureTitleList.get(index));
			 }else{
				 intent.putExtra("Title"," ");
			 }
			intent.setAction("android.intent.action.showpic");//action与接收器相同
			sendBroadcast(intent);
		}catch(Exception err){
			Log.i("AKAI", err.toString());
		}
	 	
	}
	
	
	 public static void GetBitmapFromURL(List<String> src) {//List<Bitmap>
		 URL url;
		 HttpURLConnection connection;
	        	for(String  weburl:src){
	        		try{
	        			url = new URL(webServerUrl+weburl);
	        			//Log.i("AKAI", webServerUrl+weburl);
		 	            connection = (HttpURLConnection) url.openConnection();
		 	            connection.setDoInput(true);
		 	            connection.connect();
		 	            InputStream input = connection.getInputStream();
		 	            Bitmap myBitmap = BitmapFactory.decodeStream(input);
		 	            pictureList.add(myBitmap);
		 	           //Log.i("AKAI", String.valueOf(pictureList.size()) );
	        		}catch(Exception err){
	        			Log.i("AKAI", err.toString());
	        			pictureList.add(null);
	        		}
	        		 
	        	}
	        	//return pictureList;
	        	/*for(int i=index;i<piccount;i++){
	        		picture[index]=null;
	        	}*/
	        	//return picture;
	    }
	 private void BroadcastShowPic(int index){
			try{
				Intent intent=new Intent();//要使用傳址
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				pictureList.get(index).compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				intent.putExtra("Image",byteArray);
				 if((index+1)<= pictureTitleList.size()) {
					 intent.putExtra("Title",pictureTitleList.get(index));
				 }else{
					 intent.putExtra("Title"," ");
				 }
				intent.setAction("android.intent.action.showpic");//action与接收器相同
				sendBroadcast(intent);
			}catch(Exception err){
				Log.i("AKAI", err.toString());
			}
		 	
		}
	 
	private void mark(){
		/* public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            //Log.i("AKAI","returned");
            return myBitmap;
        } catch (Exception err) {
            return null;
        }
    }*/
		/*private void BroadcastUi(Bitmap[] pic){
		Intent intent=new Intent();//要使用傳址
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		if(pic[0]!=null){
			pic[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			intent.putExtra("Image",byteArray);
		}else{
			intent.putExtra("Image",0);
		}
		//intent.putExtra("Image", pic);
		intent.setAction("android.intent.action.showpic");//action与接收器相同
		sendBroadcast(intent);
	}*/
	}
}
