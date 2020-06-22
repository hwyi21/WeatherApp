package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DustApi {
	public static Dust main(String location, String location2) throws IOException, ParserConfigurationException, XPathExpressionException, SAXException{
		String sido = location;
		if(sido.equals("전라북도")) {
			sido="전북";
		}else if(sido.equals("전라남도")) {
			sido="전남";
		}else if(sido.equals("충청북도")) {
			sido="충북";
		}else if(sido.equals("충청남도")) {
			sido="충남";
		}else if(sido.equals("경상북도")) {
			sido="경북";
		}else if(sido.equals("경상남도")) {
			sido="경남";
		}else {
			sido = sido.substring(0, 2);			
		}
		Dust dust = new Dust();
		StringBuilder urlBuilder = new StringBuilder( 
				"http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst"); /*
																														 * URL
																														 */

		urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8")
				+ "=8kL6j5npaYfEumELz%2FZOSev5k7Q2fITc0%2BbY8lATUjNzDgxyjALsK9kDn0TBs%2BNK6IfviiDBlUgIxNrAB1YvyQ%3D%3D"); /*
																															 * Service
																															 * Key
																															 */
		urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "="
				+ URLEncoder.encode("100", "UTF-8")); /* 한 페이지 결과 수 */
		urlBuilder.append(
				"&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /* 페이지 번호 */
		urlBuilder.append(
				"&" + URLEncoder.encode("sidoName", "UTF-8") + "=" + URLEncoder.encode(sido, "UTF-8")); /* 측정소 지역*/
		urlBuilder.append("&" + URLEncoder.encode("searchCondition", "UTF-8") + "=" + URLEncoder.encode("HOUR", "UTF-8"));
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
		
		// xml document 생성
		InputSource is = new InputSource(new StringReader(sb.toString()));
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

		// xpath 생성
		XPath xpath = XPathFactory.newInstance().newXPath();

		
		
		//측정소 개수 가져오기
		NodeList getTotalCount = (NodeList) xpath.evaluate("/response/body/totalCount", document,XPathConstants.NODESET);
		int totalCount = Integer.parseInt(getTotalCount.item(0).getTextContent());
		
		//측정소 지역
		NodeList getCityName = (NodeList) xpath.evaluate("/response/body/items/item/cityName", document,XPathConstants.NODESET);
		// 미세먼지 미세먼지(PM10) 농도
		NodeList value_pm10Value = (NodeList) xpath.evaluate("/response/body/items/item/pm10Value", document,XPathConstants.NODESET);
		// 오존 농도
		NodeList value_o3Value = (NodeList) xpath.evaluate("/response/body/items/item/o3Value", document,XPathConstants.NODESET);
		
		for(int i=0; i<totalCount;i++) {
			String cityName = getCityName.item(i).getTextContent();
			
			if(cityName.equals(location2)) {
				int pm10Value = Integer.parseInt(value_pm10Value.item(i).getTextContent());
				String o3Value = value_o3Value.item(i).getTextContent();
				dust.setPm10Value(pm10Value);
				dust.setO3Value(o3Value);
				
				break;
			}			
		}
		return dust;
	}
}