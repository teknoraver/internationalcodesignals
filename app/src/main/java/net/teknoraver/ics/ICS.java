package net.teknoraver.ics;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class ICS extends Activity implements Runnable, OnInitListener, OnTouchListener {
	private final int[] flagsId = new int[]{
			R.drawable.n0, R.drawable.n1, R.drawable.n2, R.drawable.n3, R.drawable.n4, R.drawable.n5, R.drawable.n6, R.drawable.n7,
			R.drawable.n8, R.drawable.n9, 0, 0, 0, 0, 0, 0, 0,
			R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h,
			R.drawable.i, R.drawable.j, R.drawable.k, R.drawable.l, R.drawable.m, R.drawable.n, R.drawable.o, R.drawable.p,
			R.drawable.q, R.drawable.r, R.drawable.s, R.drawable.t, R.drawable.u, R.drawable.v, R.drawable.w, R.drawable.x,
			R.drawable.y, R.drawable.z
	};
	private final String[] codes = new String[]{
			"ZERO", "ONE", "TWO", "THREE", "FOWER", "FIFE", "SIX", "SEVEN",
			"EIGHT", "NINER", null, null, null, null, null, null, null,
			"ALPHA", "BRAVO", "CHARLIE", "DELTA", "ECHO", "FOXTROT", "GOLF", "HOTEL",
			"INDIA", "JULIET", "KILO", "LIMA", "MIKE", "NOVEMBER", "OSCAR", "PAPA",
			"QUEBEC", "ROMEO", "SIERRA", "TANGO", "UNIFORM", "VICTOR", "WHISKEY", "X-RAY",
			"YANKEE", "ZULU"
	};
//	private final Drawable flags[] = new Drawable[codes.length];

	static final String stringcmd = "string";
	static final String flagcmd = "showflag";
	static final String textcmd = "showtext";
	static final String speakcmd = "speak";
	static final String speedcmd = "speed";
	private TextView code;
	private TextToSpeech tts;
	private int pos;
	private String string;
	private Handler handler;
	private int speed;
	private final Bundle params = new Bundle();
	boolean showflag;
	boolean showtext;

	private final Runnable Updater = new Runnable() {
		private boolean show = true;

		@Override
		public void run() {
			if (show)
				showChar(string.charAt(pos++));
			else
				showChar(' ');
			show = !show;
		}
	};

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);

		final LinearLayout ll = findViewById(R.id.showlayout);
		code = findViewById(R.id.code);

		showflag = getIntent().getBooleanExtra(flagcmd, true);
		showtext = getIntent().getBooleanExtra(textcmd, true);
		if (getIntent().getBooleanExtra(speakcmd, true))
			tts = new TextToSpeech(this, this);
		string = getIntent().getStringExtra(stringcmd);

		pos = 0;
		speed = getIntent().getIntExtra(speedcmd, 5);

		handler = new Handler();

		if (speed < 0) {
			handler.post(Updater);
			ll.setOnTouchListener(this);
		} else
			new Thread(this).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (tts != null)
			tts.shutdown();
	}

	private Point getSize(char c) {
		// FIXME use dynamic values from the Drawable
		final int LW = 480, LH = 480;
		final int NW = 800, NH = 400;
		int iw = 1, ih = 1;

		if (c >= '0' && c <= '9') {
			iw = NW;
			ih = NH;
		} else if (c >= 'A' && c <= 'Z') {
			iw = LW;
			ih = LH;
		}

		DisplayMetrics metrics = getResources().getDisplayMetrics();

		int sw = metrics.widthPixels, sh = metrics.heightPixels;
		if (showtext)
			sh = sh * 3 / 4;

		if (iw / ih > sw / sh)
			return new Point(sw, sw * ih / iw);
		else
			return new Point(sh * iw / ih, sh);
	}

	private Drawable getFlag(final char c) {
		Drawable flags = getResources().getDrawable(flagsId[c - '0']);
		Point p = getSize(c);
		flags.setBounds(0, 0, p.x, p.y);
		return flags;
	}

	private void showChar(final char c) {
		Drawable flag = null;
		String text = null;
		if (c >= '0' && c <= '9' || c >= 'A' && c <= 'Z') {
			if (showflag)
				flag = getFlag(c);
			if (showtext)
				text = codes[c - '0'];
			if (tts != null) {
				String letter = codes[c - '0'];
				if (c >= '1' && c <= '3' || c == '6')
					letter = String.valueOf(c);
				tts.speak(letter, TextToSpeech.QUEUE_FLUSH, params, letter);
			}
		}
		code.setText(text);
		code.setCompoundDrawables(null, flag, null, null);
	}

	@Override
	public void run() {
		pos = 0;
		try {
			while (pos < string.length() && !isFinishing()) {
				handler.post(Updater);
				Thread.sleep(speed * 100L);
				handler.post(Updater);
				while (tts.isSpeaking())
					Thread.sleep(speed * 10L);
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		string = null;
		finish();
	}

	@Override
	public void onInit(final int status) {
		tts.setLanguage(Locale.ENGLISH);
		if (speed >= 0)
			tts.setSpeechRate(speed / 10f);
	}

	@Override
	public boolean onTouch(final View v, final MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (pos == string.length() - 1) {
				finish();
				return true;
			}
			handler.post(Updater);
			handler.post(Updater);
		}
		return true;
	}
}
