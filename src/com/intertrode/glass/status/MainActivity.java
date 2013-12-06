package com.intertrode.glass.status;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.BatteryManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;

import com.google.android.glass.app.Card;

public class MainActivity extends Activity {
	public static boolean isNetworkConnected(Context c) {
		ConnectivityManager conManager = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conManager.getActiveNetworkInfo();
		return (netInfo != null && (netInfo.isConnected() && netInfo
				.isAvailable()));
	}

	public static NetworkInfo networkInfo(Context c) {
		ConnectivityManager conManager = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conManager.getActiveNetworkInfo();
		return netInfo;
	}

	private BroadcastReceiver mGlassStatusReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {
			int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			float batteryPct = (level / (float) scale) * 100;
			String cardText = "Battery Level: " + Float.toString(batteryPct)
					+ "%\n";

			String networkState = "No network.";
			if (MainActivity.isNetworkConnected(c)) {
				networkState = networkInfo(c).getTypeName();
			} else {
				networkState = "None: " + networkInfo(c).getReason();
			}
			networkState = "Network: " + networkState + "\n";
			
			Long uptimeMillis;
			String uptimeText = "\n";
			uptimeMillis = SystemClock.uptimeMillis();
			Long u = uptimeMillis / 1000;
			Long seconds = u % 60;
			u = u/60;
			Long minutes = u % 60;
			u = u/60;
			Long hours = u % 24;
			u = u/24;
			Long days = u;
			
			Long ten = Long.valueOf(10);
			
			String dayLabel = "days";
			if(days.equals(1L)) {
				dayLabel = "day";
			}
			
			String hoursLabel = String.valueOf(hours);
			if(hours.compareTo(ten) < 0) {
				hoursLabel = "0" + hours;
			}

			String minutesLabel = String.valueOf(minutes);
			if(minutes.compareTo(ten) < 0) {
				minutesLabel = "0" + minutes;
			}

			String secondsLabel = String.valueOf(seconds);
			if(seconds.compareTo(ten) < 0) {
				secondsLabel = "0" + seconds;
			}
			
			uptimeText = "Uptime: " + days + " " + dayLabel + " " + hoursLabel + ":" + minutesLabel + ":" + secondsLabel;

			Card card = new Card(c);
			cardText = "*** GLASS STATUS ***\n" + cardText + networkState + uptimeText;
			card.setText(cardText);
			View cardView = card.toView();
			setContentView(cardView);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		registerReceiver(mGlassStatusReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
	}
	
	@Override
	protected void onStop()
	{
	    unregisterReceiver(mGlassStatusReceiver);
	    super.onStop();
	}
}