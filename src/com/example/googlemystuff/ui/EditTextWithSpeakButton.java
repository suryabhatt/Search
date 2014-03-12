package com.example.googlemystuff.ui;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.example.googlemystuff.VoiceEngine;

public class EditTextWithSpeakButton extends EditText{
	
	VoiceEngine engine;
	public static boolean speakEnabled = true;
	EditText textView;
	Context context;
	
	public EditTextWithSpeakButton(Context context, AttributeSet attrs, EditText textView, VoiceEngine engine) {
	    super(context, attrs);
	    init(context, textView, engine);
	}
	
	public EditTextWithSpeakButton(Context context, EditText textView, VoiceEngine engine) {
	    super(context);
	    init(context, textView, engine);
	}

	public EditTextWithSpeakButton(Context context, AttributeSet attrs, int defStyle, EditText textView) {
	    super(context, attrs, defStyle);
	    init(context, textView, engine);
	}
	
	public void init(Context context, final EditText textView, VoiceEngine e) {
		this.context = context;
		this.textView = textView;
		engine = e;
		
		textView.setOnTouchListener(new RightDrawableOnTouchListener(textView) {
			
			@Override
			public boolean onDrawableTouch(MotionEvent event) {
				
				engine.startVoiceRecognitionActivity(textView.getId());
				event.setAction(MotionEvent.ACTION_CANCEL);
			    return false;
			}
		});
	}

	public void updateText(CharSequence text) {
		textView.setText(text);
	}
	
	@Override
	public Editable getText() {
		return textView.getText();
	}

}
