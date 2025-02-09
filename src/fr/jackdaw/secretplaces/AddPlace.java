package fr.jackdaw.secretplaces;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import fr.jackdaw.utils.Constants;

@SuppressLint("NewApi")
public class AddPlace extends ActionBarActivity {

	private EditText txtTitle;
	private Button btnTakePicture;
	private Button btnAdd;
	private ImageView imgPicture;
	protected Date dateActuelle;
	private Button btnCancel;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int MAX_IMAGE_SIZE = 1000;
	public Activity activity;
	private Location myLocation;
	private float longitude;
	private float latitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_place);
		
		activity = this;

		initFields();
		initialisationActionBar();

		btnTakePicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
			    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

			    // start the image capture Intent
			    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.pull_in_from_top, R.anim.pull_out_to_top);
			}
		});
		
		btnAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendPlace(txtTitle.getText().toString(), longitude, latitude);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_place, menu);
		return true;
	}
	

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.pull_in_from_top, R.anim.pull_out_to_top);
	}

	public void initFields(){
		txtTitle = (EditText) findViewById(R.id.add_place_edttext_title);
		btnTakePicture = (Button) findViewById(R.id.add_place_btn_take_picture);
		imgPicture = (ImageView) findViewById(R.id.add_place_img_show_picture);
		
		btnAdd = (Button) findViewById(R.id.add_place_btn_add);
		btnCancel = (Button) findViewById(R.id.add_place_btn_cancel);
		btnAdd.setEnabled(false);
	}

	public void initialisationActionBar() {
		ActionBar actionbar = getSupportActionBar();

		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayShowCustomEnabled(false);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
	}

	@SuppressLint("NewApi")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	        	Bitmap bmp = BitmapFactory.decodeFile(fileUri.getPath());
	        	ExifInterface ei = null;
				try {
					ei = new ExifInterface(fileUri.getPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Bitmap bmpResized = scaleDown(bmp, MAX_IMAGE_SIZE, true);
	        	int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

	        	switch(orientation) {
	        	    case ExifInterface.ORIENTATION_ROTATE_90:
	        	    	System.out.println("90");
	        	    	imgPicture.setImageBitmap(bmpResized);
	        	        imgPicture.setRotation(90);
	        	        break;
	        	    case ExifInterface.ORIENTATION_ROTATE_180:
	        	    	System.out.println("180");
	        	    	imgPicture.setImageBitmap(bmpResized);
	        	    	imgPicture.setRotation(180);
	        	        break;
	        	    case ExifInterface.ORIENTATION_ROTATE_270:
	        	    	System.out.println("270");
	        	    	imgPicture.setImageBitmap(bmpResized);
	        	    	imgPicture.setRotation(270);
	        	        break;
	        	}
	        	
	        	btnAdd.setEnabled(true);
	        	
	        	LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
	            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
	            longitude = (float) myLocation.getLongitude();
	            latitude = (float) myLocation.getLatitude();
	        } else if (resultCode == RESULT_CANCELED) {
	        	
	        } else {
	        	
	        }
	    }
	}

	public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
	    float ratio = Math.min(
	            (float) maxImageSize / realImage.getWidth(),
	            (float) maxImageSize / realImage.getHeight());
	    int width = Math.round((float) ratio * realImage.getWidth());
	    int height = Math.round((float) ratio * realImage.getHeight());

	    Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
	            height, filter);
	    return newBitmap;
	}	
	
	public void sendPlace(String name, float longi, float latt) {
		AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

		client.setTimeout(999999999);
		JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

			public void onSuccess(int statusCode,org.apache.http.Header[] headers,org.json.JSONObject response) {
				System.out.println("success"+response);
				finish();
				overridePendingTransition(R.anim.pull_in_from_top, R.anim.pull_out_to_top);
			}

			public void onFailure(int statusCode,org.apache.http.Header[] headers, Throwable throwable,	org.json.JSONObject response) {
				System.out.println("failure json"+response);
				finish();
				overridePendingTransition(R.anim.pull_in_from_top, R.anim.pull_out_to_top);
				
			}

			public void onFailure(int statusCode,org.apache.http.Header[] headers,String result, Throwable throwable) {
				System.out.println("failure string"+result);
				finish();
				overridePendingTransition(R.anim.pull_in_from_top, R.anim.pull_out_to_top);
			}
		};

		System.out.println(Constants.URL_API+"?func=record&titre="+name+"&lat="+latt+"&lon="+longi);
		client.get(Constants.URL_API+"?func=record&titre="+name+"&lat="+latt+"&lon="+longi, null, responseHandler);

	}
}
