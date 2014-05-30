package info.sodapanda.sodaplayer.utils;

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import info.sodapanda.sodaplayer.R;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

/**
 * A class for annotating a CharSequence with spans to convert textual emoticons
 * to graphical ones.
 */
public class SmileyParser {
	public static final int DEFAULT_SMILEY_TEXTS = R.array.default_smiley_texts;
	//public static final int DEFAULT_SMILEY_NAMES = R.array.default_smiley_names;
	private final Context mContext;
	private final String[] mSmileyTexts;
	private final Pattern mPattern;
	private final HashMap<String, Integer> mSmileyToRes;

	// Singleton stuff
	private static SmileyParser sInstance = null;

	public static SmileyParser getInstance() {
		return sInstance;
	}

	public static void init(Context context) {
		// GH - added a null check so instances will get reused
		if (sInstance == null)
			sInstance = new SmileyParser(context);
	}

	public static void destroyInstance() {
		if (sInstance != null) {
			sInstance = null;
		}
	}

	private SmileyParser(Context context) {
		mContext = context;
		mSmileyTexts = mContext.getResources().getStringArray(DEFAULT_SMILEY_TEXTS);
		mSmileyToRes = buildSmileyToRes();
		mPattern = buildPattern();
	}

	// NOTE: if you change anything about this array, you must make the
	// corresponding change
	// to the string arrays: default_smiley_texts and default_smiley_names in
	// res/values/arrays.xml
	public static final int[] DEFAULT_SMILEY_RES_IDS = {
		R.drawable.f1_1,
		R.drawable.f1_2,
		R.drawable.f1_3,
		R.drawable.f1_4,
		R.drawable.f1_5,
		R.drawable.f1_6,
		R.drawable.f1_7,
		R.drawable.f1_8,
		R.drawable.f1_9,
		R.drawable.f1_10,
		R.drawable.f1_11,
		R.drawable.f1_12,
		R.drawable.f1_13,
		R.drawable.f1_14,
		R.drawable.f1_15,
		R.drawable.f1_16,
		R.drawable.f1_17,
		R.drawable.f1_18,
		R.drawable.f1_19,
		R.drawable.f1_20,
		R.drawable.f1_21,
		R.drawable.f1_22,
		R.drawable.f1_23,
		R.drawable.f1_24,
		R.drawable.f1_25,
		R.drawable.f1_26,
		R.drawable.f1_27,
		R.drawable.f1_28,
		R.drawable.f1_29,
		R.drawable.f1_30,
		R.drawable.f1_31,
		R.drawable.f1_32,
		R.drawable.f1_33,
		R.drawable.f1_34,
		R.drawable.f1_35,
		R.drawable.f1_36,
		R.drawable.f1_37,
		R.drawable.f1_38,
		R.drawable.f1_39,
		R.drawable.f1_40,
		R.drawable.fvip_1,
		R.drawable.fvip_2,
		R.drawable.fvip_3,
		R.drawable.fvip_4,
		R.drawable.fvip_5,
		R.drawable.fvip_6,
		R.drawable.fvip_7,
		R.drawable.fvip_8,
		R.drawable.fvip_9,
		R.drawable.fvip_10,
		R.drawable.fvip_11,
		R.drawable.fvip_12,
		R.drawable.fvip_13,
		R.drawable.fvip_14,
		R.drawable.fvip_15,
		R.drawable.fvip_16,
		R.drawable.fvip_17,
		R.drawable.fvip_18,
		R.drawable.fvip_19,
		R.drawable.fvip_20,
		R.drawable.fvip_21,
		R.drawable.fvip_22,
		R.drawable.fvip_23,
		R.drawable.fvip_24,
		R.drawable.fvip_25,
		R.drawable.fvip_26,
		R.drawable.fvip_27,
		R.drawable.fvip_28,
		R.drawable.fvip_29,
		R.drawable.fvip_30,
		R.drawable.fvip_31,
		R.drawable.fvip_32,
		R.drawable.fvip_33,
		R.drawable.fvip_34,
		R.drawable.fvip_35,
		R.drawable.fvip_36,
		R.drawable.fvip_37,
		R.drawable.fvip_38,
		R.drawable.fvip_39,
		R.drawable.fvip_40,
		R.drawable.fvip_41,
		R.drawable.fvip_42,
		R.drawable.fvip_43,
		R.drawable.fvip_44,
		R.drawable.fvip_45,
		R.drawable.fvip_46,
		R.drawable.fvip_47,
		R.drawable.fvip_48,
		R.drawable.fvip_49,
		R.drawable.fvip_50,
		R.drawable.fvip_51,
		R.drawable.fvip_52,
		R.drawable.fvip_53,
		R.drawable.fvip_54,
		R.drawable.fvip_55,
		R.drawable.fvip_56,
		R.drawable.fvip_57,
		R.drawable.fvip_58
	};

	/**
	 * Builds the hashtable we use for mapping the string version of a smiley
	 * (e.g. ":-)") to a resource ID for the icon version.
	 */
	private HashMap<String, Integer> buildSmileyToRes() {
		if (DEFAULT_SMILEY_RES_IDS.length != mSmileyTexts.length) {
			// Throw an exception if someone updated DEFAULT_SMILEY_RES_IDS
			// and failed to update arrays.xml
			throw new IllegalStateException("Smiley resource ID/text mismatch");
		}

		HashMap<String, Integer> smileyToRes = new HashMap<String, Integer>(mSmileyTexts.length);
		for (int i = 0; i < mSmileyTexts.length; i++) {
			smileyToRes.put(mSmileyTexts[i], DEFAULT_SMILEY_RES_IDS[i]);
		}
		return smileyToRes;
	}

	/**
	 * Builds the regular expression we use to find smileys in
	 * {@link #addSmileySpans}.
	 */
	private Pattern buildPattern() {
		// Set the StringBuilder capacity with the assumption that the average
		// smiley is 3 characters long.
		StringBuilder patternString = new StringBuilder(mSmileyTexts.length * 3);

		// Build a regex that looks like (:-)|:-(|...), but escaping the smilies
		// properly so they will be interpreted literally by the regex matcher.
		patternString.append('(');
		for (String s : mSmileyTexts) {
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		// Replace the extra '|' with a ')'
		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");

		return Pattern.compile(patternString.toString());
	}

	/**
	 * Adds ImageSpans to a CharSequence that replace textual emoticons such as
	 * :-) with a graphical version.
	 * 
	 * @param text
	 *            A CharSequence possibly containing emoticons
	 * @return A CharSequence annotated with ImageSpans covering any recognized
	 *         emoticons.
	 */
	public CharSequence addSmileySpans(CharSequence text) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);

		Matcher matcher = mPattern.matcher(text);
		while (matcher.find()) {
			int resId = mSmileyToRes.get(matcher.group());
			builder.setSpan(new ImageSpan(mContext, resId), matcher.start(),matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return builder;
	}
}
