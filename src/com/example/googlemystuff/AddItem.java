package com.example.googlemystuff;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.googlemystuff.beans.Item;
import com.example.googlemystuff.db_helper.MyDatabaseHelper;
import com.example.googlemystuff.ui.EditTextWithSpeakButton;

public class AddItem extends Activity {

	protected static final int CAMERA_REQUEST = 0;
	Button submitButton;
	Button addPhoto;
	private static final int VOICE_RECOGNITION_REQUEST_CODE1 = 1234;
	private static final int VOICE_RECOGNITION_REQUEST_CODE2 = 1235;
	
	VoiceEngine voiceEngine;
	
	EditTextWithSpeakButton itemNameView;
	EditTextWithSpeakButton locationView;

	Bitmap bitMap;
	static int TAKE_PICTURE = 1;
	private MyDatabaseHelper dbHelper;
	private boolean isUpdate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_item);
		dbHelper = new MyDatabaseHelper(this);
		voiceEngine = new VEngine();
		
		itemNameView = new EditTextWithSpeakButton(this, (EditText) findViewById(R.id.item), voiceEngine);
		locationView = new EditTextWithSpeakButton(this, (EditText) findViewById(R.id.location), voiceEngine);
		submitButton = (Button) findViewById(R.id.submit);
		addPhoto = (Button) findViewById(R.id.addPhoto);
		
		Intent intent = getIntent();
		if(intent != null){
			prepareForUpdate(intent);
		}

		submitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				// save to database
				// bitmap is the image data
				// item has the item name info
				// location has the location name
				String itemName = itemNameView.getText().toString();
				String location = locationView.getText().toString();
				Item item = new Item(itemName, bitMap, location);
				if(isUpdate){
					dbHelper.updateItem(item);
				}else{
					dbHelper.addItem(item);
				}
				

				Intent intent = new Intent(AddItem.this, MainActivity.class);
				startActivity(intent);

			}

		});

		addPhoto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				// start camera activity
				startActivityForResult(intent, TAKE_PICTURE);
				// your write code

			}
		});
	}

	
	
	private void prepareForUpdate(Intent intent) {
		
		String itemId = intent.getStringExtra(MainActivity.ITEM_ID);
		if(itemId == null)
			return;
		isUpdate = true;
		Item item = dbHelper.getItemById(Integer.parseInt(itemId));
		
		itemNameView.setText(item.getName());
		locationView.setText(item.getLocation());
		bitMap = item.getImage();
		
	}


	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK
				&& intent != null) {
			// get bundle
			Bundle extras = intent.getExtras();

			// get bitmap
			bitMap = (Bitmap) extras.get("data");

		}
	
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE1){
			String topMatch = getTopMatch(resultCode, intent);
			itemNameView.updateText(topMatch);
		}else if(requestCode == VOICE_RECOGNITION_REQUEST_CODE2){
			String topMatch = getTopMatch(resultCode, intent);
			locationView.updateText(topMatch);
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}



	private String getTopMatch(int resultCode, Intent intent) {
		String topResult = null;
		if (resultCode == RESULT_OK) {

			ArrayList<String> textMatchList = intent
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			if (!textMatchList.isEmpty()) {
				// If first Match contains the 'search' word
				// Then start search
				topResult = textMatchList.get(0);
				
				
			}
			// Result code for various error.
		} else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
			showToastMessage("Audio Error");
		} else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
			showToastMessage("Client Error");
		} else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
			showToastMessage("Network Error");
		} else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
			showToastMessage("No Match");
		} else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
			showToastMessage("Server Error");
		}
		return topResult;
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	void showToastMessage(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	public class VEngine implements VoiceEngine{

		
		
		

		public VEngine() {
			PackageManager pm = getPackageManager();
			List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
					RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
			if (activities.size() == 0) {
				EditTextWithSpeakButton.speakEnabled = false;
				// speakButton.setText("Recognizer not present");
			}
		}

		public void startVoiceRecognitionActivity(int id) {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
					"provide your voice input...");
			if(id==R.id.item)
				startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE1);
			else
				startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE2);
		}

		

	}
	
	
}
