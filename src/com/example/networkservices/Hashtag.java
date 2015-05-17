package com.example.networkservices;

import org.json.JSONException;
import org.json.JSONObject;

public class Hashtag extends Entities {

	public Hashtag(JSONObject entitieObj) throws JSONException {		
		super(entitieObj.getJSONArray("indices"));
		// TODO Auto-generated constructor stub
	}

}
