package fr.jackdaw.secretplaces;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import fr.jackdaw.adapters.MainPageAdapter;
import fr.jackdaw.utils.Constants;

public class Map extends ActionBarActivity {

	private static final int DISTANCE = 1;
	private MainPageAdapter pageAdapter;
	private ViewPager mViewPager;
	private Button btnMap;
	private Button btnList;
	private int currentPage;
	private Date date_actuelle;
	private Location myLocation;
	private float longitude;
	private float latitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		initialisationActionBar();
		initFields();

		/** Implémentation et initialisation du viewPager des cartes */
		mViewPager = (ViewPager) findViewById(R.id.map_viewpager);
		System.out.println(getSupportFragmentManager());
		System.out.println(this);
		pageAdapter = new MainPageAdapter(getSupportFragmentManager(), this);
		mViewPager.setAdapter(pageAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

				if (mViewPager.getCurrentItem() == 0) {	
					currentPage = 0;
					mViewPager.setCurrentItem(currentPage);
					btnMap.setBackgroundResource(R.drawable.btn_menu_selected);;
					btnList.setBackgroundResource(R.drawable.btn_menu);
				} else if (mViewPager.getCurrentItem() == 1) {	
					currentPage = 1;
					mViewPager.setCurrentItem(currentPage);
					btnMap.setBackgroundResource(R.drawable.btn_menu);
					btnList.setBackgroundResource(R.drawable.btn_menu_selected);

				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});

		btnMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentPage = 0;
				mViewPager.setCurrentItem(currentPage);
				btnMap.setBackgroundResource(R.drawable.btn_menu_selected);;
				btnList.setBackgroundResource(R.drawable.btn_menu);
			}
		});

		btnList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentPage = 1;
				mViewPager.setCurrentItem(currentPage);
				btnMap.setBackgroundResource(R.drawable.btn_menu);
				btnList.setBackgroundResource(R.drawable.btn_menu_selected);
			}
		});

		refreshPlaces();
	}


	public void initFields(){
		btnMap = (Button) findViewById(R.id.map_button_map);
		btnList = (Button) findViewById(R.id.map_button_list);
	}

	public void initVars(){
		currentPage = 0;
	}

	public void initialisationActionBar() {
		ActionBar actionbar = getSupportActionBar();

		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.action_bar_custom, null);
		actionbar.setCustomView(v);	
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowTitleEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_refresh) {

		}
		if (id == R.id.action_add_place) {
			Intent intent = new Intent(Map.this, AddPlace.class);
			startActivity(intent);
			overridePendingTransition(R.anim.pull_in_from_top, R.anim.pull_out_to_top);
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String pathToOurFile = Environment.getExternalStorageDirectory().getPath() + "/foldername/" + date_actuelle.toString();
		System.out.println("photo prise");
	}

	public void refreshPlaces(){
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		longitude = (float) myLocation.getLongitude();
		latitude = (float) myLocation.getLatitude();

		getPlaces(DISTANCE, longitude, latitude);
	}

	public void getPlaces(int distance, float longi, float latt) {
		AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

		client.setTimeout(999999999);
		JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

			public void onSuccess(int statusCode,org.apache.http.Header[] headers,org.json.JSONObject response) {
				System.out.println("success"+response);
				FragmentMap fragmentMap = (FragmentMap)pageAdapter.getItem(0);
				for(int i = 0; i<response.length(); i++){
					try {
						JSONObject place = response.getJSONObject(i+"");
						
						Bitmap.Config conf = Bitmap.Config.ARGB_8888;
						Bitmap bmp = Bitmap.createBitmap(Constants.SIZE_THUMBNAIL, Constants.SIZE_THUMBNAIL, conf);
						Canvas canvas1 = new Canvas(bmp);

						// paint defines the text color,
						// stroke width, size
						Paint color = new Paint();
						color.setTextSize(35);
						color.setColor(Color.BLACK);

						//modify canvas
						canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
						    R.drawable.elephants), 0,0, color);
						
						fragmentMap.getMap().addMarker(new MarkerOptions()
						.position(new LatLng(place.getDouble("2"), place.getDouble("3")))
						.icon(BitmapDescriptorFactory.fromBitmap(bmp))
						.title(place.getString("1")));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			public void onFailure(int statusCode,org.apache.http.Header[] headers, Throwable throwable,	org.json.JSONObject response) {
				System.out.println("failure json"+response);

			}

			public void onFailure(int statusCode,org.apache.http.Header[] headers,String result, Throwable throwable) {
				System.out.println("failure string"+result);
			}
		};

		System.out.println(Constants.URL_API+"?func=places&distance="+distance+"&lat="+latt+"&lon="+longi);
		client.get(Constants.URL_API+"?func=places&distance="+distance+"&lat="+latt+"&lon="+longi, null, responseHandler);

	}
}
