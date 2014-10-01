package tw.brad.android.apps.eHomeApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Index extends Activity {
	private Button Announcement, Mail, Repair, Inquiry;
	private Intent intent_a,  intent_m, intent_r, intent_i;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index);
		Announcement = (Button)findViewById(R.id.Announcement);
		Mail = (Button)findViewById(R.id.Mail);
		Repair = (Button)findViewById(R.id.Repair);
		Inquiry = (Button)findViewById(R.id.Inquiry);
		
		intent_a = new Intent(this,Announcement.class);
		intent_m = new Intent(this,Mail.class);
		intent_r = new Intent(this,Repair.class);
		//intent_i = new Intent(this,Inquiry.class);
		
		Announcement.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(intent_a);
			}
		});
		
		Mail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(intent_m);
			}
		});
		
		Repair.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(intent_r);
			}
		});
		
		/*Inquiry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(intent_i);
			}
		});*/
		
		
	}
	

}
