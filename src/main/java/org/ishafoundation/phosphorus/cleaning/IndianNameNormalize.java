package org.ishafoundation.phosphorus.cleaning;

public class IndianNameNormalize implements Cleaner {
	
	public String clean(final String value) {
		return indianNameNormalize(value.replaceAll("[^a-zA-Z ]", "").toLowerCase());
	}
	
	private String indianNameNormalize(String s) {
		s = s.replace("aa", "a");
		s = s.replace("bb", "b");
		s = s.replace("ee", "i");
		s = s.replace("zh", "l");
		s = s.replace("oo", "u");
		s = s.replace("bh", "b");
		s = s.replace("dh", "d");
		s = s.replace("gh", "g");
		s = s.replace("jh", "j");
		s = s.replace("kh", "k");
		s = s.replace("sh", "s");
		s = s.replace("th", "t");
		s = s.replace("ck", "k");
		s = s.replace("kk", "k");
		s = s.replace("nn", "n");
		s = s.replace("mm", "m");
		s = s.replace("pp", "p");
		s = s.replace("ll", "l");
		s = s.replace("ty", "ti");
		s = s.replace("ot", "od");
		s = s.replace("iya", "ia");
		s = s.replace("ya", "ia");
		s = s.replace("sv", "s");
		s = s.replace("sw", "s");
		s = s.replace("my", "mi");
		s = s.replace("wi", "vi");
		return s;
	}
