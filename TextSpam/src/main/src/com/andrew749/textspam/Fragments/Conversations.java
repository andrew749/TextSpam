package com.andrew749.textspam.Fragments;

import java.sql.SQLException;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.andrew749.textspam.MainActivity;
import com.andrew749.textspam.R;
import com.andrew749.textspam.SwipeDismissListViewTouchListener;
import com.andrew749.textspam.Database.ConversationModel;
import com.andrew749.textspam.Database.DataSource;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.ShowcaseViews;
import com.espian.showcaseview.ShowcaseViews.ItemViewProperties;

/**
 * Created by andrew on 12/10/13.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class Conversations extends SherlockFragment {
	private DataSource dataSource;
	ListView lv;
	static ArrayAdapter<ConversationModel> adapter;
	List<ConversationModel> conversations;
	Button popupbutton;
	EditText editfield;
	TextView popuptextview;
	int frequency = 0;
	ConversationModel model;
	MainActivity activity;
	final String PREFS_NAME = "MyPrefsFile";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dataSource = new DataSource(getActivity());
		try {
			dataSource.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		conversations = dataSource.getAllConversations();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.conversationfragment, container,
				false);
		lv = (ListView) view.findViewById(R.id.listView);
		adapter = new ArrayAdapter<ConversationModel>(getActivity(),
				android.R.layout.simple_list_item_1, conversations);
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int i, long l) {
				dataSource.deleteConversationID(adapter.getItem(i).getId());
				conversations.remove(i);
				adapter.notifyDataSetChanged();
				return true;
			}

		});
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int i, long l) {
				model = new ConversationModel();
				DisplayMetrics displaymetrics = new DisplayMetrics();
				getActivity().getWindowManager().getDefaultDisplay()
						.getMetrics(displaymetrics);

				model = conversations.get(i);

				LayoutInflater inflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View v = inflater.inflate(R.layout.conversationpopup, null,
						true);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setView(v);
				AlertDialog dialog = builder.create();
				dialog.show();
				popupbutton = (Button) v.findViewById(R.id.popupbutton);
				editfield = (EditText) v.findViewById(R.id.popupedittext);
				popuptextview = (TextView) v.findViewById(R.id.popuptextview);
				for (String temp : model.getPhoneNumbers()) {
					popuptextview.append(" \n" + temp);

				}

				popupbutton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						frequency = Integer.parseInt(editfield.getText()
								.toString());
						Intent intent = new Intent();
						intent.setAction("com.andrew749.textspam.sendmessages");
						Bundle information = new Bundle();
						information.putString("message",
								model.getSendingString());
						information.putInt("freq", frequency);
						information.putSerializable("contact",
								model.getNumbersAsCustom());
						intent.putExtra("information", information);
						Log.d("Message", model.getSendingString());
						Log.d("Frequency", "" + frequency);
						Log.d("Recipient", "" + model.getNumbersAsCustom());
						getActivity().sendBroadcast(intent);
					}
				});
			}
		});
		SwipeDismissListViewTouchListener lvtouchlistener = new SwipeDismissListViewTouchListener(
				lv, new SwipeDismissListViewTouchListener.DismissCallbacks() {

					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							ConversationModel item = adapter.getItem(position);
							long itemid = item.getId();
							adapter.remove(item);
							dataSource.deleteConversationID(itemid);
							adapter.notifyDataSetChanged();
						}
					}

					@Override
					public boolean canDismiss(int position) {
						return true;
					}
				});
		lv.setAdapter(adapter);
		lv.setOnTouchListener(lvtouchlistener);
		lv.setOnScrollListener(lvtouchlistener.makeScrollListener());
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME,
				0);
		if (settings.getBoolean("my_first_time_conversations", true)) {
			/*
			 * Run tutorial because app is being launched for the first time
			 */
			doConversationTutorial();
			// record the fact that the app has been started at least once
			settings.edit().putBoolean("my_first_time_conversations", false)
					.commit();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.conversationmenu, menu);
	}

	public interface conversationCommunication {
		public void toggleDrawer();
	}

	public void doConversationTutorial() {
		ShowcaseViews views = new ShowcaseViews(activity);
		views.addView(new ItemViewProperties(R.id.listView,
				R.string.conversations_swipe_to_clear_title,
				R.string.conversations_swipe_to_clear_tutorial));
		views.addView(new ItemViewProperties(R.id.clearallconversations,
				R.string.conversations_clearall_title,
				R.string.conversations_clearall_tutorial,
				ShowcaseView.ITEM_ACTION_ITEM));
		if(Build.VERSION.SDK_INT>=14){
			if(ViewConfiguration.get(getSherlockActivity().getApplicationContext()).hasPermanentMenuKey()){
				views.addView(new ItemViewProperties(R.id.conversation_tutorial,
						R.string.conversations_tutorial_title,
						R.string.conversations_tutorial));
			}else{
		views.addView(new ItemViewProperties(R.id.conversation_tutorial,
				R.string.conversations_tutorial_title,
				R.string.conversations_tutorial,
				ShowcaseView.ITEM_ACTION_OVERFLOW));
		}}else{
			views.addView(new ItemViewProperties(R.id.conversation_tutorial,
					R.string.conversations_tutorial_title,
					R.string.conversations_tutorial));
		}
		views.show();
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.clearallconversations:
			lv.clearChoices();
			conversations.removeAll(conversations);
			adapter.notifyDataSetInvalidated();
			dataSource.deleteAllConversations();
			break;
		case R.id.conversation_tutorial:
			doConversationTutorial();
			break;
		case android.R.id.home:
			activity.toggleDrawer();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setHasOptionsMenu(true);
		this.activity = (MainActivity) activity;
	}

}
