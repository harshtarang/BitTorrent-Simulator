package edu.ufl.cise.test;

import java.util.HashMap;


public class SomeClass {
	
	private static volatile SomeClass instance;
	private HashMap<String, String> map1;
	private HashMap<String, String> map2;
	private Integer value;

	public static SomeClass getInstance() {
		if (instance == null) {
			synchronized (SomeClass.class) {
				if (instance == null)
					instance = new SomeClass();
			}
		}
		return instance;
	}
	
	private SomeClass(){
		this.map1 = new HashMap<String, String>();
		this.map2 = new HashMap<String, String>();
		value = 0;
	}

	public HashMap<String, String> getMap1() {
		return map1;
	}

	public void setMap1(HashMap<String, String> map1) {
		this.map1 = map1;
	}

	public HashMap<String, String> getMap2() {
		return map2;
	}

	public void setMap2(HashMap<String, String> map2) {
		this.map2 = map2;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	
	
}
