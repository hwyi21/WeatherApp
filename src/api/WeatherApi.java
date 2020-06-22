package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import lib.GetDate;

public class WeatherApi {
	
	public static ArrayList main(String weatherDate , String searchnx, String searchny) throws IOException, ParseException {
		ArrayList weatherArray = new ArrayList();	
		Date date = new Date(); // 오늘 날짜
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // 날짜 출력 형식
		SimpleDateFormat sdfh = new SimpleDateFormat("HH"); // 시간 출력 형식
		SimpleDateFormat sdfm = new SimpleDateFormat("mm"); // 분 출력 형식
		
		int nowTime = Integer.parseInt(sdfh.format(date)); //현재 시간
		int nowTimem = Integer.parseInt(sdfm.format(date)); // 현재 분
		
		String h = sdfh.format(date); // 현재 시간의 스트링 형식
		String m = sdfm.format(date); // 현재 분의 스트링 형식
		String same=Integer.toString(nowTime+1)+"00"; //현재 시간보다 한 시간 빠른 시간의 예보를 조회하기 위한 변수

		String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst"; // 동네예보조회
		
		// 홈페이지에서 받은 키
		String serviceKey = "0xQizHLX9vBTgSIhlUuGrj%2BlASeJ00Kzb1YvK9HF7KR%2BdtQerb1rP%2FjjJhSBG%2F7fJPeAIsMgGyuxgHEHOTcmOg%3D%3D"; 
																																	
		String nx = searchnx; // 위도 60
		String ny = searchny; // 경도 127
		
		
		String baseDate = sdf.format(date); // 조회하고싶은 날짜
		String baseTime = "0500"; // API 제공 시간
		String dataType = "json"; // 타입 xml, json
		String numOfRows = "250"; // 한 페이지 결과 수 동네예보 -- 전날 05시 부터 225개의 데이터를 조회하면 모레까지의 날씨를 알 수 있음
		
		if(nowTime<9) {
			same="0"+Integer.toString(nowTime+1)+"00";
		}
		
		
		//현재 시간이 8시 전이라면 어제 날짜의 23시에 제공된 api 조회
		if(nowTime<=6) {
			baseDate = GetDate.getDate(-1);
			baseTime = "2300";
		}
		
		// 현재 시간보다 한 시간 빠른 시간의 예보를 조회하기 위한 baseTime 설정
				if(weatherDate=="now") {
					apiUrl =  "http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtFcst"; //초단기예보
					if(nowTime==0) { //00시 인 경우
						baseTime="2330";
					}else if(nowTime<=10) { //9시 전인 경우 baseTime을 0900 형식으로 설정
						baseDate = GetDate.getDate(0); //오늘 날짜
						if(nowTimem>=00&&nowTimem<=29) { //00분~29분 인 경우 baseTime을 한시간 전 30분으로 설정
							baseTime="0"+Integer.toString(nowTime-1)+"30";
						}else {
							if(nowTime==10) {
								baseTime=h+"30";						
							}else {
								baseTime=h+"30";
							}	
							baseTime=h+"30";
						}
					}else {//11시부터 23시까지 baseTime 설정
						if(nowTimem>=00&&nowTimem<=29) { //00분~29분 인 경우 baseTime을 한시간 전 30분으로 설정
							baseTime=Integer.toString(nowTime-1)+"30";
						}
						
						else { //
							baseTime=h+"30";
						}
						
					}

				}
		

		StringBuilder urlBuilder = new StringBuilder(apiUrl);
		urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey);
		urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); // 경도
		urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); // 위도
		urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /* 조회하고싶은 날짜 */
		urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /* 조회하고싶은 시간 AM 02시부터 3시간 단위 */
		urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(dataType, "UTF-8")); /* 타입 */
		urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8")); /* 한 페이지 결과 수 */

		// GET방식으로 전송해서 파라미터 받아오기
		URL url = new URL(urlBuilder.toString());
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");

		BufferedReader rd;
		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();
		String data = sb.toString();

		// Json parser를 만들어 만들어진 문자열 데이터를 객체화
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(data);
		// response 키를 가지고 데이터를 파싱
		JSONObject parse_response = (JSONObject) obj.get("response");
		// response 로 부터 body 찾기
		JSONObject parse_body = (JSONObject) parse_response.get("body");
		// body 로 부터 items 찾기
		JSONObject parse_items = (JSONObject) parse_body.get("items");
		JSONArray parse_item = (JSONArray) parse_items.get("item");
		// JSONObject item = (JSONObject) parse_item.get("item");
		
		
		for (int i = 0; i < parse_item.size(); i++) {
			
			JSONObject weatherInfo = (JSONObject) parse_item.get(i);
			String fcstDate = (String) weatherInfo.get("fcstDate"); 
			String fcstTime = (String) weatherInfo.get("fcstTime");
			
			//넘겨 받은 날짜와 동일한 날짜의 날씨를 weatherArray 배열에 담음
			if(fcstDate.equals(weatherDate)) {
				JSONObject weather = (JSONObject) parse_item.get(i);
				weatherArray.add(weather);		
			}	else if(weatherDate=="0") { //오늘 /내일/ 모레 모든 날씨 데이터를 weatherArray 배열에 담음
				JSONObject weather = (JSONObject) parse_item.get(i);
				weatherArray.add(weather);				
			}else if(weatherDate=="now") { // 현재 시간을 기준으로 1시간뒤 날씨 데이터를 weatherArray 배열에 담음
				if(same.equals("2400")) { //현재 시간이 24시라면
					if(fcstTime.equals("0000")) { // fcstTime이 "0000" 시 인 날씨 데이터를 weatherArray 배열에 담음
						JSONObject weather = (JSONObject) parse_item.get(i);
						weatherArray.add(weather);	
					}
				}else { // 현재 시간을 기준으로 1시간뒤 시간(same)과 fcstTime 이 동일한 날씨 데이터를 weatherArray 배열에 담음
					if(fcstTime.equals(same)) {
						JSONObject weather = (JSONObject) parse_item.get(i);
						weatherArray.add(weather);	
					}
					
				}
				
			}
						
		}
		
		return weatherArray;
	}
}
