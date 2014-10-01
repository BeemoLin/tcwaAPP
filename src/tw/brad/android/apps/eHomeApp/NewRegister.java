package tw.brad.android.apps.eHomeApp;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.R.integer;
import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

//====================設定帳號密碼頁面==============================
public class NewRegister extends Activity {
	
	private EditText username, passwd; //, phonenum
	private String susername, spasswd; //, sphonenum
	
	private Button ok, reset ,close,button;
	private AlertDialog.Builder dialog1, dialog2, dialog3;
	private ProgressDialog progressDialog;
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	
	/*由index.php同層的checkreg.php check帳號密碼*/
	
	/*1.service:mail+alarm 2.webviewactivity使用p_ip進入 */
	private final String serverUrl="http://app.lohaslife.com.tw/uj001/";//(不同社區修改)
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.newreg);
		username = (EditText)findViewById(R.id.username);
		passwd = (EditText)findViewById(R.id.passwd);
		//phonenum = (EditText)findViewById(R.id.phonenum);
		
		ok = (Button)findViewById(R.id.newreg_ok);
		reset = (Button)findViewById(R.id.newreg_reset);
		
		try{
			sp = getSharedPreferences("ehome", MODE_PRIVATE);
			username.setText(sp.getString("username", "xxx"));
			passwd.setText(sp.getString("passwd", "xxx"));
			//editor = sp.edit();
		}catch(Exception err){
			//讀不到發生錯誤 don't care
		}
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doOK();
			}
		});
		
		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doReset();
			}
		});
	}
	
	//====================登入成功開始啟動service==============
	private void startMyService(){
		Intent it = new Intent(this, NoticeService.class);
		startService(it);
	}
	//========================重置==========================
	private void doReset(){
		username.setText("");
		//phonenum.setText("");
		passwd.setText("");
	}
	
	//========================登入==========================
	@SuppressLint("NewApi")
	private void doOK(){
		// 遠端 App Server 註冊新帳號
		susername = username.getText().toString();
		spasswd = passwd.getText().toString();
		//sphonenum = phonenum.getText().toString();
		//| sphonenum.equals("")
		if (susername.equals("") | spasswd.equals("") ) {
			dialog1 = new AlertDialog.Builder(NewRegister.this, R.style.AppDialog);
			//dialog.setIcon(android.R.drawable.ic_dialog_info);
			dialog1.setIcon(android.R.drawable.ic_dialog_alert);
			dialog1.setTitle("未完成");
			dialog1.setMessage("帳號、密碼是否填寫!");
			dialog1.setPositiveButton("確認", null);
			dialog1.show();
			return;
		}		
		progressDialog = new ProgressDialog(NewRegister.this,R.style.AppDialog);
		progressDialog.setTitle("登入中．．．");
		progressDialog.setMessage("請稍候．．．");
		progressDialog.setCancelable(false);
		progressDialog.show();
		//Log.i("test"," show");
		Thread thread=new Thread(new Runnable() 
		{ 
			@Override
		    public void run() 
		   { 
				Message msg = Message.obtain();
				TelephonyManager tmgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
				String phoneid = tmgr.getDeviceId();
				
				HttpClient client = new DefaultHttpClient();
				//不同設區要修改
				HttpPost post = new HttpPost(serverUrl+"checkreg.php");
				try {
					StringBody strUsername = new StringBody(susername);
					StringBody strPasswd = new StringBody(spasswd);
					StringBody strPhonenum = new StringBody("11111");
					StringBody strPhoneid = new StringBody(phoneid);
					
					MultipartEntity entity = new MultipartEntity();
					entity.addPart("username", strUsername);
					entity.addPart("passwd", strPasswd);
					entity.addPart("phonenum", strPhonenum);
					entity.addPart("phoneid", strPhoneid);
					
					post.setEntity(entity);
					
					HttpResponse res = client.execute(post);
					InputStream in = res.getEntity().getContent();
					int temp; StringBuffer sb = new StringBuffer();
					
					while ( (temp = in.read()) != -1)
					{
						sb.append((char)temp);
					}
					in.close();
					
					String ret = sb.toString();
					String[] code=ret.split(":");
					
					if (code[0].equals("0"))
					{
						/*FileOutputStream fout = openFileOutput("ehome.data", MODE_PRIVATE);
						fout.write(phoneid.getBytes());
						fout.flush();
						fout.close();*/
						sp = getSharedPreferences("ehome", MODE_PRIVATE);
						editor = sp.edit();
						editor.putString("username", susername);//成功才把值填入文字檔裡面
						editor.putString("passwd", spasswd);
						editor.putString("pip", code[1]);
						//editor.putString("phonenum", sphonenum);
						//editor.putString("phoneid", phoneid);
						editor.commit();
						Log.i("test", "完成遠端註冊");
						// 完成遠端註冊，開始啟動服務並帶到歡迎畫面的頁面
						startMyService();//設定帳密正確才開始進入服務模式
						progressDialog.dismiss();
						//startIndex();
						finish();//success 結束activity
						//---jump webview screen
						//GoWebView();有問題
					}
					else 
					{
						msg.what = 1;
						NewRegister.this.handler.sendMessage(msg);
						progressDialog.dismiss();
						Log.i("test", "遠端新增註冊帳號失敗");
					}
				} catch (Exception e) 
					{
						msg.what = 2;
						NewRegister.this.handler.sendMessage(msg);
						progressDialog.dismiss();
						Log.i("test"," 新增註冊帳號失敗，請確認網路是否正常");
					}  
		   } 
		});
		thread.start();//開始執行緒
	}
	/*private void startIndex(){
		//Intent ix = new Intent(this, Index.class);
		Intent ix = new Intent(this,Mail.class);
		startActivity(ix);
		Log.i("test", "startIndex1");
		finish();
	}*/
	
	//========================執行緒=========================
	private Handler handler = new Handler() {
		
		@SuppressLint("NewApi")
		public void handleMessage(Message msg)
		{
			if(msg.what == 0)
			{
				progressDialog = new ProgressDialog(NewRegister.this,R.style.AppDialog);
				progressDialog.setTitle("登入中．．．");
				progressDialog.setMessage("請稍候．．．");
				progressDialog.setCancelable(true);
				progressDialog.show();
				Log.i("test"," show");
			}
			else
			{
				if (msg.what == 1)
				{
					dialog2 = new AlertDialog.Builder(NewRegister.this,R.style.AppDialog);
					dialog2.setIcon(android.R.drawable.ic_dialog_info);
					dialog2.setTitle("登入失敗！");
					dialog2.setMessage("請確認帳號、密碼是否輸入正確！");
					dialog2.setPositiveButton("確認", null);
					dialog2.show();
					Log.i("test", "what=1");
					return;
				}
				else
				{
					dialog3 = new AlertDialog.Builder(NewRegister.this,R.style.AppDialog);
					dialog3.setIcon(android.R.drawable.ic_dialog_info);
					dialog3.setTitle("登入失敗！");
					dialog3.setMessage("請確認網路是否正常！");
					dialog3.setPositiveButton("確認", null);
					dialog3.show();
					Log.i("test", "what=2");	
				}
			}
			super.handleMessage(msg);
		}
	};
	
	private void GoWebView(){
		Intent intent = new Intent(NewRegister.this, WebViewActivity.class);
		startActivity(intent);
		//finish();最上一層所以不用finish();
	}
}
