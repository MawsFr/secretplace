package fr.jackdaw.secretplaces;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


public class FragmentListPlaces extends Fragment {

	private FragmentManager fm;
	private Activity activity;
	private Context mContext;
	private ListView liste;

	public FragmentListPlaces(FragmentManager fragmentManager,
			Activity activity) {
		this.fm = fragmentManager;
		this.activity = activity;
		mContext = this.activity.getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list_places, container, false);

		liste = (ListView) v.findViewById(R.id.liste_places_liste);
		initFields();
		ArrayList<String> maListe = new ArrayList<String>();
		maListe.add("Permier Element" );
		maListe.add("Second Element" );
		maListe.add("Troisieme Element" );
		maListe.add("Quatrieme Element" );
		maListe.add("Cinquieme Element" );

		liste.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1,maListe));
		return v;
	}
	
	public void initFields(){
		System.out.println(liste);

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
}