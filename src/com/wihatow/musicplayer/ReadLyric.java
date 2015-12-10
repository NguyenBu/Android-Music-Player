package com.wihatow.musicplayer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

public class ReadLyric {
	private String title;
	private String artist;
	private String album;
	private String by;
	ArrayList<Long> singleTimeList = new ArrayList<Long>();
	ArrayList<String> singleLyricList = new ArrayList<String>();
	private String totalLyric;
	private String handledLyric;
	boolean hasReaded = false;

	public ReadLyric(String lyricPath) {
		try {
			FileInputStream inputStream = new FileInputStream(lyricPath);
			int number = 0;
			byte[] buffer = new byte[2048];
			totalLyric = "";
			String encode = getStringEcode(lyricPath);
			while ((number = inputStream.read(buffer)) > 0) {
				totalLyric += new String(buffer, 0, number, encode);
			}
			inputStream.close();
			handledLyric = totalLyric.replaceAll("\\[\\D+.*\\D+\\]", "");
			handledLyric = handledLyric.replaceAll("\\[\\D+.*\\]", "");
			handledLyric = handledLyric.replaceAll("\\n", "");
			if (lyricPath.endsWith(".trc")) {
				handledLyric = handledLyric.replaceAll("<[0-9]*>", "");
			}
			hasReaded = true;
		} catch (Exception e) {
			hasReaded = false;
		}
	}

	public static String getStringEcode(String fileName) throws Exception {
		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(
				fileName));
		int p = (bin.read() << 8) + bin.read();
		bin.close();
		String code = "";
		// 其中的 0xefbb、0xfffe、0xfeff这些都是这个文件的前面两个字节的16进制数
		switch (p) {
		case 0xefbb:
		case 0x5b61:
			code = "UTF-8";
			break;
		case 0xfffe:
			code = "Unicode";
			break;
		case 0xfeff:
			code = "UTF-16BE";
			break;
		default:
			code = "GBK";
			break;
		}

		return code;
	}

	public String getHandledLyric() {
		if (!hasReaded) {
			return null;
		}
		return handledLyric;
	}

	public String getTitle() {
		if (!hasReaded) {
			return null;
		}
		if (totalLyric.contains("[ti:")) {
			title = totalLyric.substring(totalLyric.indexOf("[ti:") + 4,
					totalLyric.indexOf("]", totalLyric.indexOf("[ti:")));
		}
		return title;
	}

	public String getArtist() {
		if (!hasReaded) {
			return null;
		}
		if (totalLyric.contains("[ar:")) {
			artist = totalLyric.substring(totalLyric.indexOf("[ar:") + 4,
					totalLyric.indexOf("]", totalLyric.indexOf("[ar:")));
		}
		return artist;
	}

	public String getAlbum() {
		if (!hasReaded) {
			return null;
		}
		if (totalLyric.contains("[al:")) {
			album = totalLyric.substring(totalLyric.indexOf("[al:") + 4,
					totalLyric.indexOf("]", totalLyric.indexOf("[al:")));
		}
		return album;
	}

	public String getBy() {
		if (!hasReaded) {
			return null;
		}
		if (totalLyric.contains("[by:")) {
			by = totalLyric.substring(totalLyric.indexOf("[by:") + 4,
					totalLyric.indexOf("]", totalLyric.indexOf("[by:")));
		}
		return by;
	}

	public ArrayList<Long> getSingleTime() {
		if (!hasReaded) {
			return null;
		}
		String temString = handledLyric.replaceAll("\\][^\\[]*\\[", "`");
		temString = temString.replaceAll("\\].*", "");
		temString = temString.replaceAll("\\[", "");
		temString = temString.replaceAll(":", ".");
		String[] timeStrings = temString.split("`");
		int minuts;
		int seconds;
		int miliseconds;
		long time;
		String[] dataStrings;
		try {
			for (int i = 0; i < timeStrings.length; i++) {
				dataStrings = timeStrings[i].split("\\.");
				if (i == 0) {// 去除UTF-8的ROM标记-65279字符
					minuts = 0;
				} else {
					minuts = Integer.parseInt(dataStrings[0]);
				}
				seconds = Integer.parseInt(dataStrings[1]);
				miliseconds = Integer.parseInt(dataStrings[2]);
				time = minuts * 60 * 1000 + seconds * 1000 + miliseconds;
				singleTimeList.add(time);
			}
		} catch (Exception e) {
			singleTimeList = null;
		}

		return singleTimeList;
	}

	public ArrayList<String> getSingleLyric() {
		if (!hasReaded) {
			return null;
		}
		String[] temp = handledLyric.split("\\[\\d+:\\d+\\.\\d+\\]");
		for (int i = 1; i < temp.length; i++) {// 屏蔽\65279非法字符
			singleLyricList.add(temp[i]);
		}
		return singleLyricList;
	}

	public String getTotalLyric() {
		if (!hasReaded) {
			return null;
		}
		return totalLyric;
	}
}
