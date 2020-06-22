// 날씨 예보 페이지
package weather;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import api.WeatherApi;
import lib.GetDate;
import main.WeatherMain;

public class TimeLinePage extends JPanel {
	JPanel timeLine; // 시간대별 날씨
	JPanel p_north;

	JLabel temperature; // 기온
	JLabel precipitation;// 강수량
	JLabel wind; // 바람
	JLabel humidity; // 습도
	WeatherMain weatherMain;

	public TimeLinePage(String date, WeatherMain weatherMain) {
		this.weatherMain = weatherMain;
		timeLine = new JPanel();
		p_north = new JPanel();

		temperature = new JLabel("온도");
		precipitation = new JLabel("강수");
		wind = new JLabel("바람");
		humidity = new JLabel("습도");

		// 디자인
		temperature.setPreferredSize(new Dimension(50, 50));
		precipitation.setPreferredSize(new Dimension(50, 50));
		wind.setPreferredSize(new Dimension(50, 50));
		humidity.setPreferredSize(new Dimension(50, 50));

		temperature.setHorizontalAlignment(JLabel.RIGHT);
		precipitation.setHorizontalAlignment(JLabel.RIGHT);
		wind.setHorizontalAlignment(JLabel.RIGHT);
		humidity.setHorizontalAlignment(JLabel.RIGHT);

		temperature.setFont(new Font("돋움", Font.BOLD, 14));
		precipitation.setFont(new Font("돋움", Font.BOLD, 14));
		wind.setFont(new Font("돋움", Font.BOLD, 14));
		humidity.setFont(new Font("돋움", Font.BOLD, 14));

		p_north.setPreferredSize(new Dimension(700, 80));
		p_north.setBackground(Color.WHITE);

		timeLine.setPreferredSize(new Dimension(700, 220));
		timeLine.setBackground(Color.WHITE);

		p_north.add(temperature);
		p_north.add(precipitation);
		p_north.add(wind);
		p_north.add(humidity);

		setLayout(new BorderLayout());
		add(p_north, BorderLayout.NORTH);
		add(timeLine);

		showTemperature(date);

		// 시간대별 기온 보여주기
		temperature.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				timeLine.removeAll();
				showTemperature(date);
			}
		});

		// 시간대별 강수량 보여주기
		precipitation.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				timeLine.removeAll();
				showPrecipitation(date);
			}
		});

		// 시간대별 바람의 세기 보여주기
		wind.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				timeLine.removeAll();
				showWind(date);
			}
		});

		// 시간대별 습도 보여주기
		humidity.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				timeLine.removeAll();
				showHumidity(date);
			}
		});
	}

	// 기온 구하기
	public void showTemperature(String weatherDate) {
		if (weatherDate.equals(GetDate.getDate(0))) {
			getTimeForecast("0", "T3H");
		} else {
			getTimeForecast(weatherDate, "T3H");
		}

	}

	// 강수량 구하기
	public void showPrecipitation(String weatherDate) {
		if (weatherDate.equals(GetDate.getDate(0))) { // 오늘날짜의 날씨 데이터 조회시 오늘/내일/모레 날씨가 담겨진 데이터 정보 가져오기
			getTimeForecast("0", "POP");
		} else {
			getTimeForecast(weatherDate, "POP");
		}
	}

	// 바랍 구하기
	public void showWind(String weatherDate) {
		if (weatherDate.equals(GetDate.getDate(0))) { // 오늘날짜의 날씨 데이터 조회시 오늘/내일/모레 날씨가 담겨진 데이터 정보 가져오기
			getTimeForecast("0", "WSD");
		} else {
			getTimeForecast(weatherDate, "WSD");
		}
	}

	// 습도 구하기
	public void showHumidity(String weatherDate) {
		if (weatherDate.equals(GetDate.getDate(0))) { // 오늘날짜의 날씨 데이터 조회시 오늘/내일/모레 날씨가 담겨진 데이터 정보 가져오기
			getTimeForecast("0", "REH");
		} else {
			getTimeForecast(weatherDate, "REH");
		}
	}

	// 시간 별 예보 가져오기
	public void getTimeForecast(String weatherDate, String weatherCategory) {
		Thread thread = new Thread() {
			public void run() {
				int countCard = 0;
				try {
					ArrayList weatherList = WeatherApi.main(weatherDate, weatherMain.nx, weatherMain.ny);
					for (int i = 0; i < weatherList.size(); i++) {
						JSONObject timeForecast = (JSONObject) weatherList.get(i);
						String category = (String) timeForecast.get("category");
						if (category.equals(weatherCategory)) { // 온도 구하기
							String getValue = (String) timeForecast.get("fcstValue");
							String getTime = (String) timeForecast.get("fcstTime");
							if (getTime.equals("0000")) {
								getTime = "오전 12시";
							} else if (getTime.equals("0300")) {
								getTime = "오전 3시";
							} else if (getTime.equals("0600")) {
								getTime = "오전 6시";
							} else if (getTime.equals("0900")) {
								getTime = "오전 9시";
							} else if (getTime.equals("1200")) {
								getTime = "오후 12시";
							} else if (getTime.equals("1500")) {
								getTime = "오후 3시";
							} else if (getTime.equals("1800")) {
								getTime = "오후 6시";
							} else if (getTime.equals("2100")) {
								getTime = "오후 9시";
							}
							JPanel card = new JPanel();
							JLabel l_time = new JLabel(getTime); // 시간
							JLabel l_value = new JLabel(getValue); // 날씨 정보

							if (weatherCategory == "T3H") { // 기온
								l_value = new JLabel(getValue + " ℃ ");
							} else if (weatherCategory == "POP" || weatherCategory == "REH") { // 강수량 /습도
								l_value = new JLabel(getValue + " %");
							} else if (weatherCategory == "WSD") { // 바람의 세기
								l_value = new JLabel(getValue + " m/s");
							}
							card.setPreferredSize(new Dimension(80, 80));
							card.setBackground(Color.WHITE);
							l_time.setPreferredSize(new Dimension(70, 30));
							l_value.setPreferredSize(new Dimension(70, 30));
							l_time.setHorizontalAlignment(JLabel.CENTER);
							l_value.setHorizontalAlignment(JLabel.CENTER);
							card.add(l_time);
							card.add(l_value);
							timeLine.add(card);
							add(timeLine);
							updateUI();
							countCard++;
						}
						// 날씨 정보를 8개만 보여주기
						if (countCard == 8)
							break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

		};
		thread.start();
	}
}
