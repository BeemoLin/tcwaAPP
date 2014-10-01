package tw.brad.android.apps.eHomeApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)){
			Intent startservice=new Intent(context,NoticeService.class);
			startservice.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			context.startService(startservice);
		}
	}

}
