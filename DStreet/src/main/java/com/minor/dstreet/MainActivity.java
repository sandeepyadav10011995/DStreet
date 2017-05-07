package com.minor.dstreet;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import adapters.BackGround;
import adapters.SuggestionsAdapter;

public class MainActivity extends BaseActivity {

	private Fragment mContent;
	ImageButton sb;
	TextView title;
	InputMethodManager ime;
	public MainActivity() {
		super(R.string.app_name);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(this, BackGround.class));
		// setSlidingActionBarEnabled(true);
        Intent n = getIntent();
        if(getIntent()!=null) {
            Bundle bundle = n.getExtras();
            if (bundle != null) {
                Log.e("ARG", "EXIST");
                if (bundle.containsKey("eq")) {
                    Log.e("EQ", "EXIST");
                    if (bundle.getBoolean("eq")) {
                        Log.e("eq", "true");
                        Bundle data = new Bundle();
                        Fragment4 eq = new Fragment4();
                        mContent = eq;
                        data.putString("q", bundle.getString("q"));
                        data.putString("type", bundle.getString("type"));
                        data.putString("name", bundle.getString("name"));
                        data.putString("f",gId());
                        eq.setArguments(data);
                        switchContent(eq);
                    }
                }
            }
        }

		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new Fragment1();


		ime = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		ActionBar actionBar = getSupportActionBar();
		getSupportActionBar().setCustomView(R.layout.search);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		sb = (ImageButton) actionBar.getCustomView().findViewById(R.id.sb);
		title = (TextView)actionBar.getCustomView().findViewById(R.id.title);
		sb.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// only will trigger it if no physical keyboard is open
				search.setVisibility(View.VISIBLE);
				search.requestFocus();
				ime.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
				search.setSelection(search.getText().length());
				sb.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				
			}
		});
		
		search = (AutoCompleteTextView)	actionBar.getCustomView().findViewById(R.id.et);
        search.setThreshold(2);
        search.setAdapter(new SuggestionsAdapter(this, search.getText().toString()));
        search.setSelectAllOnFocus(true);
        search.clearFocus();
		
        
        search.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int pos,
					long id) {
				String term = parent.getItemAtPosition(pos).toString();
				String[] lines = term.split("\\r?\\n");
				String name = lines[0];
				String code = lines[1].substring(lines[1].indexOf(":")+1, lines[1].length()).trim();
				String type = lines[1].substring(0, lines[1].indexOf(":"));
				Log.d("DATA", name+" "+type+":"+code);
                search.setText("");
				if(isNetworkAvailable()){
					if(term.replace(" ", "")!=null){
						Bundle data = new Bundle();	
				        data.putString("q", code);
				        data.putString("type", type);
				        data.putString("name", name);
				        data.putString("f", gId());
				        Fragment4 eq = new Fragment4();
				        eq.setArguments(data);		
				        Log.d("C", gId()+" frag");
				        mContent = eq;
				        getSupportFragmentManager().beginTransaction()
							.replace(R.id.content_frame, eq).commit();
				        getSlidingMenu().showContent();
				        search.setVisibility(View.GONE);
						sb.setVisibility(View.VISIBLE);
						title.setVisibility(View.VISIBLE);
						ime.hideSoftInputFromWindow(search.getApplicationWindowToken(), 0);
					}
					else{
						Toast.makeText(getApplicationContext(), "Enter a search term!", 
								Toast.LENGTH_LONG).show();
					}
				}
				else
					Toast.makeText(getApplicationContext(), "Internet not available", 
							Toast.LENGTH_LONG).show();
			}
		});
        
        search.setOnEditorActionListener(new OnEditorActionListener() {
        	@Override
        	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		        return false;
        	}
        });
		
		// set the Above View
		setContentView(R.layout.content_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();
        AdView mAdView;
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SampleListFragment()).commit();
	}

    public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment, fragment.getClass().toString()).commit();
		getSlidingMenu().showContent();
	}
	
	public String gId(){
		
		String c = mContent.getClass().toString();
		if(c.endsWith("1")){
			Log.d("C1", c);
			return "1";
		}
		else if(c.endsWith("2")){
			Log.d("C2", c);
			return "2";
		}
		else if(c.endsWith("3")){
			Log.d("C3", c);
			return "3";
		}
        else if(c.equals("PortView")){
            return "PortView";
        }
		else{
			Log.d("C", c);
			return "0";
		}
	}
	
	@Override
	public void onBackPressed() {
		
		Log.d("Back", "Pressed");
		String c = mContent.getClass().toString();
		String r =Fragment4.class.toString();
		Log.d("Current", c);
		Log.d("F4", r);
		if(search.getVisibility()==View.VISIBLE){
			
			search.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
			sb.setVisibility(View.VISIBLE);
			ime.hideSoftInputFromWindow(search.getApplicationWindowToken(), 0);
			return;
		}else if(r.equals(c)){
			
			String from = Fragment4.getFrom();
			Log.d("From", from+"st Act");

            if(from.equals("1"))
                switchContent(new Fragment1());
            else if(from.equals("2"))
                switchContent(new Fragment2());
            else if(from.equals("3"))
                switchContent(new Fragment3());
            else if(from.equals("6"))
                switchContent(new Fragment6());
            else if(from.equals("PortView"))
                switchContent(new PortView());

			
		}else if(c.equals(PortView.class.toString())){

            switchContent(new Fragment6());

        }else if(c.equals(Add.class.toString())){
            Add a = new Add();
            Bundle b = new Bundle();
            b.putString("id", a.gID());
            b.putString("name", a.gName());
            a.setArguments(b);
            switchContent(new Fragment6());
        }
        else{
			MainActivity.this.finish();
		}

			
		
	}
	
}