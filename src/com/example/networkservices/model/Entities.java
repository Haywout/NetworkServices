package com.example.networkservices.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class Entities {
	private ArrayList<Integer> indices = new ArrayList<Integer>();
	
	public Entities(JSONArray indicesArr) throws JSONException {
		for (int i = 0; i < indicesArr.length(); i++) {
			indices.add(indicesArr.getInt(i));
		}
	}
	
	public int getIndexEen() {
		return indices.get(0);
	}
	
	public int getIndexTwee() {
		return indices.get(1);
	}
	
}
