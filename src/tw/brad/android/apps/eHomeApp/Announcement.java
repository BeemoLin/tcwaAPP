package tw.brad.android.apps.eHomeApp;

import tw.brad.android.apps.eHomeApp.ImageViewHelper;//引用類別

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
//import application.nelson.R;


public class Announcement extends Activity {
	private TextView txt;
	private ImageView img;
	private Button btnPrePage,btnNextPage,btnBig,btnSmall;
	private int pageIndex;
	private int pageCount;
	private MyReceiver receiver;
	
	private DisplayMetrics tdm;
	//private ListView lvwShowPic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DoUi();
		String picname=GetPicName();
		registerShowPicReceiver();
		GoShowPicService(picname);
		pageIndex=0;
	}
	private void DoUi(){
		//setContentView(R.layout.communitypic);
		//lvwShowPic = (ListView)findViewById(R.id.lvwShowPic);
		setContentView(R.layout.announcement);
		txt = (TextView)findViewById(R.id.anon_txt);
		img= (ImageView)findViewById(R.id.anon_img); 
		txt.setText("等待中...");
		btnPrePage =(Button)findViewById(R.id.btnPrePage);
		btnNextPage= (Button)findViewById(R.id.btnNextPage);
		
		//layout1 = (LinearLayout)findViewById(R.id.layout1);
		btnBig =(Button)findViewById(R.id.btnBig);
		btnSmall= (Button)findViewById(R.id.btnSmall);
		
		tdm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(tdm);
	
		ChangeBtnEnable(false,false);
		btnPrePage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(pageIndex>0) {
					ChangeBtnEnable(false,false);
					pageIndex-=1;
					registerShowPicReceiver();
					GoShowPicService("ChangePic",pageIndex);
				}
			}
		});
		btnNextPage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(pageIndex<pageCount) {
					ChangeBtnEnable(false,false);
					pageIndex+=1;
					registerShowPicReceiver();
					GoShowPicService("ChangePic",pageIndex);
				}
			}
		});	
     
	}
	
	private void ChangeBtnEnable(Boolean btnPre,Boolean btnNext){
		btnPrePage.setEnabled(btnPre);
		btnNextPage.setEnabled(btnNext);
	}
	
	private void ChangePage(){
		if( pageCount==1){
			ChangeBtnEnable(false,false);
		}else{//pageCount>1
			if(pageIndex<1  ){
				ChangeBtnEnable(false,true);
			}else if(pageIndex==pageCount-1 ){
				ChangeBtnEnable(true,false);
			}else{
				ChangeBtnEnable(true,true);
			}
		}	
	}
	
	private void GoShowPicService(String picname){
		try{
			Intent se = new Intent(this, ShowPicService.class);
			se.putExtra("cmdMode", "Init");
			se.putExtra("PicName", picname);
			startService(se);
		}catch(Exception err){
			
		}
		
	}
	private void GoShowPicService(String mode,int index){
		try{
			Intent se = new Intent(this, ShowPicService.class);
			se.putExtra("cmdMode", mode);
			se.putExtra("picIndex", index);
			startService(se);
		}catch(Exception err){
			
		}
		
	}
	
	
	private void registerShowPicReceiver(){
		try{
			receiver=new MyReceiver();
			IntentFilter filter=new IntentFilter();
			filter.addAction("android.intent.action.showpic");
			Announcement.this.registerReceiver(receiver,filter);
		}catch(Exception err){
			
		}
	}
	private class MyReceiver extends BroadcastReceiver {
		//自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try{
				byte[] byteArray = intent.getByteArrayExtra("Image");
				String tilte=intent.getStringExtra("Title");
			
				Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
				img.setImageBitmap(bmp);
				
				new ImageViewHelper(tdm,img,bmp,btnBig,btnSmall);  
				
				txt.setText(tilte);
				ChangePage();
				Announcement.this.unregisterReceiver(receiver);//解除註冊
				
			}catch(Exception err){
			}
		}
	}
	private String GetPicName(){
		try{
			Intent it = getIntent();
			String picName = it.getStringExtra("PicName");
			//Log.i("AKAI", String.valueOf(picName));
			String[] picpackage = picName.split("kais::kaie");
			pageCount=picpackage.length;
			return picName;
		}catch(Exception err){
			return " ";
		}
		
	}
	
	
	
	
	private void Mark(){
		/* private List<Map<String,Object>> getData(Bitmap bmp) {     
			 List<Map<String,Object> > list = new ArrayList<Map<String,Object>>();
			 Map<String,Object> map = new HashMap<String,Object>();
		     //map.put("title", "G1");
		     //map.put("info", "紅豆");
		     map.put("img", bmp);
		     list.add(map);
		     return list;
		private void ShowPic(Bitmap bmp){
		SimpleAdapter adapter = new SimpleAdapter(this, getData(bmp),R.layout.pic_adapter,
				//new String[] { "title", "info", "img" },new int[] { R.id.sItemTitle, R.id.sItemInfo, R.id.sItemIcon });
				new String[] { "img" },new int[] { R.id.sItemIcon });
		lvwShowPic.setAdapter(adapter);
	}
	
	public static Bitmap loadBitmap(String url) {
	    Bitmap bitmap = null;
	    InputStream in = null;
	    BufferedOutputStream out = null;

	    try {
	        in = new BufferedInputStream(new URL(url).openStream());

	        final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
	        out = new BufferedOutputStream(dataStream);
	        //copy(in, out);
	        
	        int temp;
	        while ((temp = in.read()) != -1){
	        	out.write(temp);
	        }	        
	        
	        out.flush();

	        final byte[] data = dataStream.toByteArray();
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        //options.inSampleSize = 1;

	        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
	    } catch (IOException e) {
	    } finally {
	    	try {
				in.close();
		    	out.close();
			} catch (IOException e) {
			}
	    }

	    return bitmap;
	}
	
	
	 public static Bitmap getBitmapFromURL(String src) {
	        try {
	            Log.i("AKAI",src);
	            URL url = new URL(src);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoInput(true);
	            connection.connect();
	            InputStream input = connection.getInputStream();
	            Bitmap myBitmap = BitmapFactory.decodeStream(input);
	            Log.i("AKAI","returned");
	            return myBitmap;
	        } catch (Exception err) {
	            err.printStackTrace();
	            Log.i("AKAI",err.toString());
	            return null;
	        }
	    }
	
	
	
	
	//img.setImageBitmap(loadBitmap("http://www.lohaslife.com.tw/cc77/backstage/news/20130208104553.jpg"));
		//img.setImageBitmap(getBitmapFromURL("http://www.lohaslife.com.tw/cc77/backstage/news/20130208104553.jpg"));

	
	
		 }*/
	}
	
}