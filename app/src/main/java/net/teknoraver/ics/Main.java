package net.teknoraver.ics;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;

public class Main extends Activity implements OnClickListener {
	private SharedPreferences pref;
	private EditText et;
	private CheckBox sf, st, sp, touch;
	private SeekBar speed;
	private static final String showf = "showf", showt = "showt", speak = "speak", tta = "tta", speedv = "speedv", txt = "txt";

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input);

		pref = PreferenceManager.getDefaultSharedPreferences(this);

		et = findViewById(R.id.text);
		sf = findViewById(R.id.showflag);
		st = findViewById(R.id.showtext);
		sp = findViewById(R.id.speak);
		touch = findViewById(R.id.touch);
		speed = findViewById(R.id.speed);

		touch.setOnClickListener(this);

		sf.setChecked(pref.getBoolean(showf, true));
		st.setChecked(pref.getBoolean(showt, true));
		sp.setChecked(pref.getBoolean(speak, true));
		final boolean ttabool = pref.getBoolean(tta, false);
		touch.setChecked(ttabool);
		speed.setProgress(pref.getInt(speedv, 5));
		speed.setEnabled(!ttabool);
		et.setText(pref.getString(txt, ""));

		findViewById(R.id.show).setOnClickListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		final SharedPreferences.Editor editor = pref.edit();

		editor.putBoolean(showf, sf.isChecked());
		editor.putBoolean(showt, st.isChecked());
		editor.putBoolean(speak, sp.isChecked());
		editor.putBoolean(tta, touch.isChecked());
		editor.putInt(speedv, speed.getProgress());
		editor.putString(txt, et.getText().toString());

		editor.apply();
	}

	@Override
	public void onClick(final View view) {
		if (view.getId() == R.id.show) {
			if (!sf.isChecked() && !st.isChecked() && !sp.isChecked())
				return;

			int showspeed = -1;
			if (!touch.isChecked())
				showspeed = 15 - speed.getProgress();

			startActivity(new Intent(this, ICS.class)
								  .putExtra(ICS.stringcmd, (" " + et.getText()).toUpperCase() + " ")
								  .putExtra(ICS.flagcmd, sf.isChecked())
								  .putExtra(ICS.textcmd, st.isChecked())
								  .putExtra(ICS.speakcmd, sp.isChecked())
								  .putExtra(ICS.speedcmd, showspeed));
		}
		if (view.getId() == R.id.touch)
			speed.setEnabled(!touch.isChecked());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		if(item.getItemId() == R.id.about) {
		new AlertDialog.Builder(this)
				.setTitle(R.string.about)
				.setMessage(R.string.aboutt)
				.setIcon(android.R.drawable.ic_dialog_info)
				.show();
		return true;
//		}
//		return false;
	}
}
