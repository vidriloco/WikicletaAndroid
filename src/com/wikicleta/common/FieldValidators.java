package com.wikicleta.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class FieldValidators {

	private static String EMAILREGEXP = "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
	private static Pattern pattern = Pattern.compile(EMAILREGEXP);
	
	public static boolean isFieldEmpty(String fieldValue) {
		return TextUtils.isEmpty(fieldValue);
	}
	
	public static boolean isFieldLongerThan(String fieldValue, int size) {
		return fieldValue.length() > size;
	}
	
	public static boolean isFieldAValidEmail(String fieldValue) {
		Matcher matcher = pattern.matcher(fieldValue);
		return matcher.matches();
	}
	
	public static boolean isFieldShorterThan(String fieldValue, int size) {
		return fieldValue.length() < size;
	}
	
	public static boolean fieldsMatch(String firstFieldValue, String secondFieldValue) {
		return firstFieldValue.equals(secondFieldValue);
	}
	
}
