package tw.brad.android.apps.eHomeApp;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/*import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;*/
/*
public class WebViewActivity extends Activity {
	private SharedPreferences sp;
	
	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.webview);
			webView = (WebView) findViewById(R.id.webView1);
			//Log.i("AKAI",webView.getTitle());
			webView.getSettings().setJavaScriptEnabled(true);
			WebSettings settings = webView.getSettings();
			settings.setDefaultTextEncodingName("utf-8");
			webView.loadUrl(GoUrl());
		
			//String customHtml = "<html><body><h1>Hello, WebView</h1></body></html>";
			//webView.loadData(customHtml, "text/html", "UTF-8");
		}catch(Exception err){
			
		}
	}
	private String GoUrl(){
		try{
		sp = getSharedPreferences("ehome", MODE_PRIVATE);
		String p_ip=sp.getString("username", "xxx").trim()+sp.getString("passwd", "xxx").trim();
		Log.i("AKAI", "http://www.lohaslife.com.tw/cctest/index.php?pip="+p_ip);
		return "http://www.lohaslife.com.tw/cctest/index.php?pip="+p_ip;
		}catch(Exception err){
			return "";
		}
	}
	
}*/
//===========================================================================================
	
	public class WebViewActivity extends Activity {
	    /** Called when the activity is first created. */
		WebView wv;
		ProgressDialog pd;
		/*由index.php 使用p_ip機制  =社區+帳號 */
		private final String serverUrl="http://app.lohaslife.com.tw/uj001/";
		//private final String pipcode="yj001";
		private SharedPreferences sp;
		Handler handler;
	    
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.webview);
	        
	        try{
		        init();//执行初始化函数
		        
		        
		        Intent it = getIntent();
				String msgtxt = it.getStringExtra("msg"); 
				
				if(msgtxt!=null){
					if(msgtxt.equals("Front")){
						 loadurl(wv,GoFrontUrl());//"http://www.pocketdigi.com"
						
					}else if(msgtxt.equals("Back")){
						loadurl(wv,GoBackUrl());
					}
				}else{
					 loadurl(wv,GoFrontUrl());
				}
	        }catch(Exception e){}
	       
	        handler=new Handler(){
	        	public void handleMessage(Message msg)
	    	    {//定义一个Handler，用于处理下载线程与UI间通讯
	    	      if (!Thread.currentThread().isInterrupted())
	    	      {
	    	        switch (msg.what)
	    	        {
	    	        case 0:
	    	        	//pd.show();//显示进度对话框        	
	    	        	break;
	    	        case 1:
	    	        	//pd.hide();//隐藏进度对话框，不可使用dismiss()、cancel(),否则再次调用show()时，显示的对话框小圆圈不会动。
	    	        	break;
	    	        }
	    	      }
	    	      super.handleMessage(msg);
	    	    }
	        };
	    }
	    private void init(){//初始化
	    	wv=(WebView) findViewById(R.id.webView1);
	    	
	    	WebSettings wvset  = wv.getSettings();
	    	
	    	wvset.setJavaScriptEnabled(true);//可用JS
	        //wv.zoomIn();// setSupportZoom(true);
	        //wv.zoomOut(); wv.setBuiltInZoomControls(true); 
	    	//====放大縮小====
	    	wvset.setBuiltInZoomControls(true);// .setBuiltInZoomControls // 顯示放大縮小controler
	    	wvset.setSupportZoom(true); // 可以縮放
	    	wvset.setDefaultZoom(ZoomDensity.FAR  );// 預設版面大小
	    	//====放大縮小====
	    	
	    	wv.setScrollBarStyle(0);//滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
	        wv.setWebViewClient(new WebViewClient(){   
	        	@Override
	        	public void onReceivedError(WebView view, int errorCode,
	        			String description, String failingUrl) {
	        		// TODO Auto-generated method stub
	        		try{
		        		if (failingUrl.indexOf("p_ip") > 0){
		        			loadurl(wv,"");//防止網址夾帶的參數倍看到
		        		}
	        		 }catch(Exception e){}
	        		//Log.i("AKAI",String.valueOf(failingUrl.indexOf("p_ip")) );//String.valueOf重要
	        		 
	        		super.onReceivedError(view, errorCode, description, failingUrl);
	        	}
	            //提交出去如果有回應則會觸發此function並帶出進入的網址
	        	//若提交出去無回應會有反應麼
	        	public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
	            	try{
		        		Log.i("AKAI", "TEST:"+url);
		            	loadurl(view,url);//载入网页
	            	 }catch(Exception e){}
	                return true;   
	            }//重写点击动作,用webview载入
	        });
	        wv.setWebChromeClient(new WebChromeClient(){
	        	
	        	public void onProgressChanged(WebView view,int progress){//载入进度改变而触发 
	             	try{
		        		if(progress==100){
		             		//Log.i("AKAI", "tttt");
		            		handler.sendEmptyMessage(1);//如果全部载入,隐藏进度对话框
		            	}  
	             	}catch(Exception e){}
	                super.onProgressChanged(view, progress);   
	            }   
	        });
	    	pd=new ProgressDialog(WebViewActivity.this);
	        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pd.setMessage("載入中...");
	    }
	    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回键(GOOD)有分網頁返回與退出
	        /*if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {   
	            wv.goBack();   
	            return true;   
	        }else*/ 
	    	if(keyCode == KeyEvent.KEYCODE_BACK){
	        	ConfirmExit();//按了返回键，但已经不能返回，则执行退出确认
	        	return true; 
	        }   
	        return super.onKeyDown(keyCode, event);   
	    }
	    public void ConfirmExit(){//退出确认
	    	AlertDialog.Builder ad=new AlertDialog.Builder(WebViewActivity.this);
	    	ad.setTitle("退出");
	    	ad.setMessage("是否退出?");
	    	ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//退出按钮
				@Override
				public void onClick(DialogInterface dialog, int i) {
					// TODO Auto-generated method stub
					WebViewActivity.this.finish();//关闭activity
				}
			});
	    	ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
					//不退出不用执行任何操作
				}
			});
	    	ad.show();//显示对话框
	    }
	    //包在thread裡面
	    public void loadurl(final WebView view,final String url){
	    	new Thread(){
	        	public void run(){
	        		try{
		        		handler.sendEmptyMessage(0);
		        		view.loadUrl(url);//载入网页
	        		}catch(Exception e){}
	        	}
	        }.start();
	    }
	    private String GoFrontUrl(){
			try{
				sp = getSharedPreferences("ehome", MODE_PRIVATE);
				
				//String p_ip=pipcode+sp.getString("username", "xxx").trim();//+sp.getString("passwd", "xxx").trim();
				String p_ip=sp.getString("pip", "xxx").trim();
				Log.i("AKAI", "代碼pip="+p_ip);
				//String p_ip=sp.getString("username", "xxx").trim()+sp.getString("passwd", "xxx").trim();
				//Log.i("AKAI", "http://www.lohaslife.com.tw/cctest/index.php?p_ip="+p_ip);
				return serverUrl+"index.php?p_ip="+p_ip;
			}catch(Exception err){
				return "";
			}
		}
	    private String GoBackUrl(){
			try{
			sp = getSharedPreferences("ehome", MODE_PRIVATE);
			String p_ip=sp.getString("username", "xxx").trim()+sp.getString("passwd", "xxx").trim();
			//Log.i("AKAI", "http://www.lohaslife.com.tw/cctest/index.php?p_ip="+p_ip);
			return "http://www.lohaslife.com.tw/cctest/backstage/index.php?p_ip="+p_ip;
			}catch(Exception err){
				return "";
			}
		}
	}
	//=============================================================================================