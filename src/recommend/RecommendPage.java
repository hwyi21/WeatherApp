package recommend;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import api.WeatherApi;
import lib.TextLabel;
import main.Frame;
import main.WeatherMain;

public class RecommendPage extends Frame {
	JPanel p_container; // 메인 패널
	TextLabel la_title; // "Recommend Place"

	ArrayList<Recommend> recommendList = new ArrayList<Recommend>();

	public RecommendPage(WeatherMain weatherMain, int width, int height, int x, int y, String title, String firstSep,
			String secondSep, String thirdSep) {
		super(weatherMain, width, height, x, y, title);
		p_container = new JPanel();
		la_title = new TextLabel("Recommend Place", 900, 70, 30);

		p_container.setPreferredSize(new Dimension(860, 480));
		p_container.setBackground(Color.WHITE);
		setBackground(Color.WHITE);
		p_container.add(la_title);
		add(p_container);

		getRecommendPlace(firstSep, secondSep, thirdSep);
	}

	public String getWeatherState() {
		String result = "맑음";
		try {
			ArrayList nowWeather = WeatherApi.main("now", weatherMain.nx, weatherMain.ny);

			for (int i = 0; i < nowWeather.size(); i++) {
				JSONObject weatherInfo = (JSONObject) nowWeather.get(i);
				String category = (String) weatherInfo.get("category");
				String fcstValue = (String) weatherInfo.get("fcstValue");

				// 기온
				if (category.equals("SKY")) {
					if (fcstValue.equals("1")) {
						result = "맑음";
					} else if (fcstValue.equals("3") || fcstValue.equals("4")) {
						result = "흐림/안개";
					}
				} else if (category.equals("PTY")) {
					if (fcstValue.equals("1") || fcstValue.equals("4")) {
						result = "비/소나기";
					} else if (fcstValue.equals("3")) {
						result = "눈";
					}
				} else if (category.equals("T1H")) {
					int temp = Integer.parseInt(fcstValue);
					if (temp >= 30) {
						result = "더움";
					}
				}
			}
		} catch (NumberFormatException | IOException | ParseException e) {
			e.printStackTrace();
		}
		
		return result;

	}

	public void getRecommendPlace(String firstSep, String secondSep, String thirdSep) {
		String weatherName = getWeatherState();

		setContainerPanel();
		
		
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select id, name, address, phone, image from store where id IN "
				+ "(select store_id from recommend" + " where weather_id = (select id from weather where name = ?)"
				+ " and location_id = (select id from location where FIRST_SEP =? AND SECOND_SEP =? AND THIRD_SEP =?))";
		try {
			pstmt = weatherMain.con.prepareStatement(sql);
			pstmt.setString(1, weatherName);
			pstmt.setString(2, firstSep);
			pstmt.setString(3, secondSep);
			pstmt.setString(4, thirdSep);

			rs = pstmt.executeQuery();
					
			while (rs.next()) {
				Recommend r = new Recommend();

				r.setId(rs.getInt("id"));
				r.setName(rs.getString("name"));
				r.setAddress(rs.getString("address"));
				r.setPhone(rs.getString("phone"));
				r.setImage(rs.getString("image"));

				recommendList.add(r);
				
				JPanel card = createPanel(r.getImage(), r.getName(), r.getAddress(), r.getPhone());
				p_container.add(card);	
				
			}
			if(recommendList.size()==0) {
				JPanel empty = new JPanel();
				TextLabel empty_title = new TextLabel("추천 장소가 없습니다 ㅠㅠ", 900, 70, 30);
				empty.setPreferredSize(new Dimension(900,400));
				empty.setBackground(Color.ORANGE);
				
				empty.add(empty_title);
				p_container.add(empty);
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			weatherMain.connectionManager.closeDB(pstmt, rs);
		}

	}

	public void setContainerPanel() {

		Component[] childList = p_container.getComponents();

		for (int i = 0; i < childList.length; i++) {
			if (childList[i] != la_title) {
				p_container.remove(childList[i]);
			}
		}
		p_container.updateUI();
	}

	public JPanel createPanel(String imgPath, String name, String address, String phone) {
		JPanel card = new JPanel();
		card.setPreferredSize(new Dimension(200, 320));
		card.setBackground(Color.ORANGE);
		URL url;
		try {
			url = new URL(imgPath);
			ImageIcon icon = new ImageIcon(url);
			Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
			icon = new ImageIcon(image);
			JLabel img = new JLabel(icon);
			img.setPreferredSize(new Dimension(150, 150));
			TextLabel la_name = new TextLabel(name, 170, 50, 17);
			TextLabel la_address = new TextLabel(address, 170, 50, 12);
			TextLabel la_phone = new TextLabel(phone, 170, 50, 12);
			card.add(img);
			card.add(la_name);
			card.add(la_address);
			card.add(la_phone);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return card;
	}

}
