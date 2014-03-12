package com.example.googlemystuff;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemystuff.beans.Item;
import com.example.googlemystuff.db_helper.MyDatabaseHelper;
import com.example.googlemystuff.ui.EditTextWithSpeakButton;

public class MainActivity extends Activity implements TextWatcher {

	static final String ITEM_ID = "item_id";
	MyDatabaseHelper dbHelper;
	VoiceEngine voiceEngine;
	ListView listView;

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private EditTextWithSpeakButton searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbHelper = new MyDatabaseHelper(getApplicationContext());
		voiceEngine = this.new VEngine();

		searchView = new EditTextWithSpeakButton(this,
				(EditText) findViewById(R.id.autoCompleteTextView1),
				voiceEngine);
		searchView.addTextChangedListener(this);

		listView = (ListView) findViewById(R.id.listView1);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int arg2, long arg3) {
				if (deleteItem(view)) {
					listView.setAdapter(null);
					updateListViewBasedOnSearchString("");
					return true;
				}
				return false;
			}
		});
		final Intent i = new Intent(this, AddItem.class);
		Button addItemButton = (Button) findViewById(R.id.button1);
		addItemButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform some action on click
				startActivity(i);
			}
		});

	}

	/*
	 * private void startVoiceRecognitionActivity() { Intent intent = new
	 * Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	 * intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	 * RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	 * intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
	 * "What are you searching for..."); startActivityForResult(intent,
	 * VOICE_RECOGNITION_REQUEST_CODE); }
	 */

	@Override
	public void afterTextChanged(Editable arg0) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		updateListViewBasedOnSearchString(s.toString());
	}

	private void updateListViewBasedOnSearchString(String search) {
		List<Item> allItems = dbHelper.getAllItems(search);
		if (allItems.isEmpty())
			return;
		// System.out.println(s.toString());
		MyListAdapter adapter = new MyListAdapter(this, allItems);
		listView.setAdapter(adapter);
	}

	public void updateItem(View v) {

		String itemId = ((TextView) v.findViewById(R.id.itemId)).getText()
				.toString();
		final Intent i = new Intent(this, AddItem.class);
		i.putExtra(ITEM_ID, itemId);
		startActivity(i);

	}

	public boolean deleteItem(View v) {
		String itemId = ((TextView) v.findViewById(R.id.itemId)).getText()
				.toString();
		dbHelper.deleteItemById(itemId);
		// updateListViewBasedOnSearchString("");
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

			// If Voice recognition is successful then it returns RESULT_OK
			if (resultCode == RESULT_OK) {

				ArrayList<String> textMatchList = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				if (!textMatchList.isEmpty()) {
					// If first Match contains the 'search' word
					// Then start search
					String topResult = textMatchList.get(0);
					searchView.updateText(topResult);
					updateListViewBasedOnSearchString(topResult);
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
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Helper method to show the toast message
	 **/
	void showToastMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	public void speechInputReceived(String topResult) {
		searchView.updateText(topResult);
		updateListViewBasedOnSearchString(topResult);
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
					"What are you searching for...");
			startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		}

		void showToastMessage(String message) {
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
					.show();
		}

	}
}