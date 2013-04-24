package ca.polymtl.inf8405.sevenwonders.controller;

import java.util.HashMap;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import ca.polymtl.inf8405.sevenwonders.api.Civilisation;

public class CivilisationLoader {

	private static HashMap<Civilisation, Drawable> civiDataBase_ = new HashMap<Civilisation, Drawable>();
	private static CivilisationLoader instance_;
	
	private CivilisationLoader(){
		
	}
	
	public static CivilisationLoader getInstance(){
		if (instance_ == null)
			instance_ = new CivilisationLoader();
		return instance_;
	}
	
	public Drawable getDrawable(Context context, Civilisation civi){
		if (civiDataBase_.containsKey(civi))
			return civiDataBase_.get(civi);
		else {
			// Access to other application
			PackageManager pm = context.getPackageManager();
			try {
				Resources resources = pm.getResourcesForApplication("ca.polymtl.inf8405.sevenwondersassets");
				int id = resources.getIdentifier(civi.toString().toLowerCase(), "drawable", 
						"ca.polymtl.inf8405.sevenwondersassets");
				if (id != 0){
					Drawable d = resources.getDrawable(id);
					civiDataBase_.put(civi, d);
					return d;
				}
				else 
					return null;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				Log.e("CardLoader", "Exception:" + e.getMessage());
			}
			return null;
		}
	}

}
