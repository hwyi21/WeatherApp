package lib;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//day가 0인 경우 오늘 날짜 
// 1 = 내일
// 2 = 모레
// -1 = 어제
public class GetDate {
	public static String getDate(int day) {
		Date date = new Date(); //오늘 날짜
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, day);
		String todayDate = sdf.format(date);
		todayDate = sdf.format(cal.getTime()); 

		return todayDate;
	}
		
	public static String text_todayDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("M월 d일");
		String todayDate = sdf.format(date);
		return todayDate;
	}
	
	public static String text_nowTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("H시 m분");
		String nowTime = sdf.format(date);
		return nowTime;
	}
}
