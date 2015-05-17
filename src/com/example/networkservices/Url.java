package com.example.networkservices;

import org.json.JSONException;
import org.json.JSONObject;

public class Url extends Entities {

	public Url(JSONObject urlObj) throws JSONException {
			
		super(urlObj.getJSONArray("indices"));
		
	}

}
