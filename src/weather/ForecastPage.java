// 날씨 예보 페이지
package weather;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import api.Dust;
import api.DustApi;
import api.WeatherApi;
import lib.FilePath;
import lib.GetDate;
import main.WeatherMain;

public class ForecastPage extends JPanel {
	ImageIcon weatherIcon;
	Image img;
	JLabel nowIcon; // 현재 날씨 아이콘
	JLabel m_icon; // 아침 날씨 아이콘
	JLabel n_icon; // 저녁 날씨 아이콘
	JPanel dustInfo;
	JLabel la_coment;
	JLabel la_pm10Value;
	JLabel la_o3Value;
	JLabel la_state;
	JLabel dustIcon;

	WeatherMain weatherMain;
	

	public ForecastPage(String date, WeatherMain weatherMain) {
		this.weatherMain = weatherMain;
		if (date.equals(GetDate.getDate(0))) { // 현재 날씨 정보 호출
			gettodayWeatherInfo(date);
		} else { // 내일 / 모레 날씨 정보 호출
			getWeatherInfo(date);
		}

	}

	// 현재 날씨 정보 호출
	public void gettodayWeatherInfo(String weatherDate) {

		JPanel container = new JPanel();

		JPanel nowInfo = new JPanel();
		JLabel l_temp = new JLabel(); // 온도
		JLabel l_sky = new JLabel("맑음"); // 하늘 상태
		JLabel l_reh = new JLabel("습도"); // 습도
		JLabel l_wsd = new JLabel("바람"); // 바람 세기
		JLabel l_lowghighTemp = new JLabel(); // 최저/최고 기온

		ArrayList temp = new ArrayList(); // 기온
		ArrayList ptyInfo = new ArrayList(); // 강수 형태

		try {
			ArrayList nowWeather = WeatherApi.main("now", weatherMain.nx, weatherMain.ny); // 현재 온도를 담은 객체
			ArrayList weatherList = WeatherApi.main(weatherDate, weatherMain.nx, weatherMain.ny); // 오늘 날씨를 담은 객체
			// 최저/최고 기온 구하기
			for (int i = 0; i < weatherList.size(); i++) {
				JSONObject weatherInfo = (JSONObject) weatherList.get(i);
				String category = (String) weatherInfo.get("category");
				if (category.equals("T3H")) {
					String getTemp = (String) weatherInfo.get("fcstValue");
					temp.add(getTemp);
				}
			}
			l_lowghighTemp = new JLabel((String) Collections.min(temp) + " / " + (String) Collections.max(temp));

			// 현재 날씨 정보 구하기
			for (int i = 0; i < nowWeather.size(); i++) {
				JSONObject weatherInfo = (JSONObject) nowWeather.get(i);
				String category = (String) weatherInfo.get("category");
				String fcstValue = (String) weatherInfo.get("fcstValue");

				// 기온
				if (category.equals("T1H")) {
					l_temp = new JLabel(fcstValue + "℃ ");
				}

				// 강수형태
				if (category.equals("PTY")) {
					String getValue = fcstValue;
					ptyInfo.add(getValue);
				}

				// 하늘 상태
				if (category.equals("SKY")) {
					if (fcstValue.equals("1")) {
						nowIcon = makeIcon(FilePath.weatherIconDir + "sunny.png");
						l_sky.setText("맑음" + getPTY(ptyInfo, "today"));
					} else if (fcstValue.equals("3")) {
						nowIcon = makeIcon(FilePath.weatherIconDir + "partlycloudy.png");
						l_sky = new JLabel("구름 많음" + getPTY(ptyInfo, "today"));
					} else if (fcstValue.equals("4")) {
						nowIcon = makeIcon(FilePath.weatherIconDir + "cloudy.png");
						l_sky = new JLabel("흐림" + getPTY(ptyInfo, "today"));
					}
				}

				// 습도
				if (category.equals("REH")) {
					l_reh = new JLabel("습도 : " + fcstValue + "% ");
				}

				// 바람세기
				if (category.equals("WSD")) {
					l_wsd = new JLabel("바람 : " + fcstValue + "m/s ");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 오늘 날씨 정보 디자인
		container.setPreferredSize(new Dimension(400, 200));
		container.setBackground(Color.YELLOW);
		nowIcon.setPreferredSize(new Dimension(180, 100));
		nowIcon.setHorizontalAlignment(JLabel.CENTER);
		nowInfo.setPreferredSize(new Dimension(220, 200));
		nowInfo.setBackground(Color.YELLOW);
		l_temp.setPreferredSize(new Dimension(220, 40));
		l_temp.setHorizontalAlignment(JLabel.LEFT);
		l_temp.setFont(new Font("돋움", Font.BOLD, 20));
		l_sky.setPreferredSize(new Dimension(220, 40));
		l_sky.setHorizontalAlignment(JLabel.LEFT);
		l_sky.setFont(new Font("돋움", Font.BOLD, 20));
		l_reh.setPreferredSize(new Dimension(220, 30));
		l_reh.setHorizontalAlignment(JLabel.LEFT);
		l_reh.setFont(new Font("돋움", Font.BOLD, 14));
		l_wsd.setPreferredSize(new Dimension(220, 30));
		l_wsd.setHorizontalAlignment(JLabel.LEFT);
		l_wsd.setFont(new Font("돋움", Font.BOLD, 14));
		l_lowghighTemp.setPreferredSize(new Dimension(220, 30));
		l_lowghighTemp.setHorizontalAlignment(JLabel.LEFT);
		l_lowghighTemp.setFont(new Font("돋움", Font.BOLD, 14));
		container.setLayout(new BorderLayout());
		container.add(nowIcon, BorderLayout.WEST);
		nowInfo.add(l_temp);
		nowInfo.add(l_sky);
		nowInfo.add(l_reh);
		nowInfo.add(l_wsd);
		nowInfo.add(l_lowghighTemp);
		container.add(nowInfo, BorderLayout.EAST);

		add(container, BorderLayout.WEST);

		// 미세먼지 농도
		dustInfo = new JPanel();
		dustIcon = new JLabel();
		la_state = new JLabel();
		la_coment = new JLabel();
		la_pm10Value = new JLabel();

		la_o3Value = new JLabel();

		dustInfo.setPreferredSize(new Dimension(300, 200));
		dustInfo.setBackground(Color.WHITE);

		la_state.setPreferredSize(new Dimension(280, 20));
		la_coment.setPreferredSize(new Dimension(280, 20));
		la_pm10Value.setPreferredSize(new Dimension(280, 20));
		la_o3Value.setPreferredSize(new Dimension(280, 20));

		la_state.setHorizontalAlignment(JLabel.CENTER);
		la_coment.setHorizontalAlignment(JLabel.CENTER);

		la_state.setFont(new Font("돋움", Font.BOLD, 14));
		la_coment.setFont(new Font("돋움", Font.BOLD, 14));
		la_pm10Value.setFont(new Font("돋움", Font.BOLD, 12));
		la_o3Value.setFont(new Font("돋움", Font.BOLD, 12));

		try {
			getDustData();
		} catch (XPathExpressionException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		}
	}

	// 내일 / 모레 날씨 구하기
	public void getWeatherInfo(String weatherDate) {
		removeAll();

		ArrayList temp = new ArrayList(); // 기온
		ArrayList skyInfo = new ArrayList(); // 하늘상태
		ArrayList popInfo = new ArrayList(); // 강수확률
		ArrayList ptyInfo = new ArrayList(); // 강수형태

		try {

			ArrayList weatherList = WeatherApi.main(weatherDate, weatherMain.nx, weatherMain.ny);
			for (int i = 0; i < weatherList.size(); i++) {
				JSONObject weatherInfo = (JSONObject) weatherList.get(i);

				String category = (String) weatherInfo.get("category");
				String fcstTime = (String) weatherInfo.get("fcstTime");
				String fcstValue = (String) weatherInfo.get("fcstValue");

				// 기온을 ArrayList에 담기
				if (category.equals("T3H")) {
					String getTemp = fcstValue;
					temp.add(getTemp);
				}

				// 하늘 상태을 ArrayList에 담기
				if (category.equals("SKY")) {
					String getValue = fcstValue;
					skyInfo.add(getValue);
				}

				// 강수량을 ArrayList에 담기
				if (category.equals("POP")) {
					String getValue = fcstValue;
					popInfo.add(getValue);
				}

				// 강수형태를 ArrayList에 담기
				if (category.equals("PTY")) {
					String getValue = fcstValue;
					ptyInfo.add(fcstValue);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// 오전 날씨 정보
		JPanel morning = new JPanel();
		JLabel l_morning = new JLabel("오전");
		JPanel m_info = new JPanel(); // 오전 날씨 정보
		JLabel m_temp = new JLabel((String) Collections.min(temp) + " ℃ "); // 최저 기온
		JLabel m_pop = new JLabel("강수 확률 : " + getAvgValue(popInfo, "am") + " %");
		JLabel m_sky = new JLabel("흐림");

		// 하늘 상태 구하기
		if (getAvgValue(skyInfo, "am").equals("1") || getAvgValue(skyInfo, "am").equals("2")) {
			m_icon = makeIcon(FilePath.weatherIconDir + "sunny.png");
			m_sky.setText("맑음" + getPTY(ptyInfo, "am"));
		} else if (getAvgValue(skyInfo, "am").equals("3")) {
			m_icon = makeIcon(FilePath.weatherIconDir + "partlycloudy.png");
			m_sky.setText("구름 많음" + getPTY(ptyInfo, "am"));
		} else if (getAvgValue(skyInfo, "am").equals("4")) {
			m_icon = makeIcon(FilePath.weatherIconDir + "cloudy.png");
			m_sky.setText("흐림" + getPTY(ptyInfo, "am"));
		}

		morning.setPreferredSize(new Dimension(350, 200));
		morning.setBackground(Color.YELLOW);
		m_info.setPreferredSize(new Dimension(300, 80));
		m_info.setBackground(Color.YELLOW);
		l_morning.setPreferredSize(new Dimension(350, 30));
		l_morning.setHorizontalAlignment(JLabel.CENTER);
		l_morning.setFont(new Font("돋움", Font.BOLD, 20));
		m_icon.setPreferredSize(new Dimension(200, 50));
		m_icon.setBackground(Color.BLACK);
		m_icon.setHorizontalAlignment(JLabel.RIGHT);
		m_temp.setPreferredSize(new Dimension(100, 50));
		m_temp.setHorizontalAlignment(JLabel.CENTER);
		m_temp.setFont(new Font("돋움", Font.BOLD, 30));
		m_sky.setPreferredSize(new Dimension(350, 30));
		m_sky.setHorizontalAlignment(JLabel.CENTER);
		m_sky.setFont(new Font("돋움", Font.BOLD, 20));
		m_pop.setPreferredSize(new Dimension(350, 30));
		m_pop.setHorizontalAlignment(JLabel.CENTER);
		m_pop.setFont(new Font("돋움", Font.BOLD, 20));

		morning.add(l_morning);
		m_info.setLayout(new GridLayout(1, 2));
		m_info.add(m_icon);
		m_info.add(m_temp);
		morning.add(m_info);
		morning.add(m_sky);
		morning.add(m_pop);

		add(morning, BorderLayout.WEST);

		// 오후 날씨 정보
		JPanel afternoon = new JPanel();
		JLabel l_afternoon = new JLabel("오후");
		JPanel n_info = new JPanel(); // 오후 날씨 정보
		JLabel n_temp = new JLabel((String) Collections.max(temp) + " ℃ "); // 최고 기온
		JLabel n_pop = new JLabel("강수 확률 : " + getAvgValue(popInfo, "pm") + " %");
		JLabel n_sky = new JLabel("흐림");

		// 오후 하늘상태 구하기
		if (getAvgValue(skyInfo, "pm").equals("1") || getAvgValue(skyInfo, "pm").equals("2")) {
			n_icon = makeIcon(FilePath.weatherIconDir + "sunny.png");
			n_sky.setText("맑음" + getPTY(ptyInfo, "pm"));
		} else if (getAvgValue(skyInfo, "pm").equals("3")) {
			n_icon = makeIcon(FilePath.weatherIconDir + "partlycloudy.png");
			n_sky.setText("구름 많음" + getPTY(ptyInfo, "pm"));
		} else if (getAvgValue(skyInfo, "pm").equals("4")) {
			n_icon = makeIcon(FilePath.weatherIconDir + "cloudy.png");
			n_sky.setText("흐림" + getPTY(ptyInfo, "pm"));
		}

		afternoon.setPreferredSize(new Dimension(350, 200));
		afternoon.setBackground(Color.YELLOW);
		n_info.setPreferredSize(new Dimension(300, 80));
		n_info.setBackground(Color.YELLOW);
		l_afternoon.setPreferredSize(new Dimension(350, 30));
		l_afternoon.setHorizontalAlignment(JLabel.CENTER);
		l_afternoon.setFont(new Font("돋움", Font.BOLD, 20));
		n_icon.setPreferredSize(new Dimension(200, 50));
		n_icon.setBackground(Color.BLACK);
		n_icon.setHorizontalAlignment(JLabel.RIGHT);
		n_temp.setPreferredSize(new Dimension(100, 50));
		n_temp.setHorizontalAlignment(JLabel.CENTER);
		n_temp.setFont(new Font("돋움", Font.BOLD, 30));
		n_sky.setPreferredSize(new Dimension(350, 30));
		n_sky.setHorizontalAlignment(JLabel.CENTER);
		n_sky.setFont(new Font("돋움", Font.BOLD, 20));
		n_pop.setPreferredSize(new Dimension(350, 30));
		n_pop.setHorizontalAlignment(JLabel.CENTER);
		n_pop.setFont(new Font("돋움", Font.BOLD, 20));

		afternoon.add(l_afternoon);
		n_info.setLayout(new GridLayout(1, 2));
		n_info.add(n_icon);
		n_info.add(n_temp);
		afternoon.add(n_info);
		afternoon.add(n_sky);
		afternoon.add(n_pop);

		add(afternoon, BorderLayout.EAST);

	}

	// 오전오후의 원하는 카테고리의 날씨 값 구하기
	public String getAvgValue(ArrayList listName, String ampm) {
		int m_totalTemp = 0; // 오전
		int n_totalTemp = 0; // 오후
		int avg = 0;

		if (listName.size() <= 1) {
			avg = Integer.parseInt((String) listName.get(0));
		} else {
			// 오전일 경우
			if (ampm == "am") {
				for (int i = 0; i < (listName.size() / 2); i++) {
					m_totalTemp = m_totalTemp + Integer.parseInt((String) listName.get(i));
				}
				avg = (int) m_totalTemp / (listName.size() / 2);
			}
			// 오후일 경우
			else if (ampm == "pm") {
				for (int i = listName.size() / 2; i < listName.size(); i++) {
					n_totalTemp = n_totalTemp + Integer.parseInt((String) listName.get(i));
				}
				avg = (int) n_totalTemp / (listName.size() / 2);
			}

		}
		return Integer.toString(avg);
	}

	// 오늘/오전/오후 강수 형태 구하고 날씨 아이콘 생성
	public String getPTY(ArrayList listName, String ampm) {
		String result = null;

		if (listName.size() <= 1) {
			result = " ";
		} else {
			// 오늘
			if (ampm == "today") {
				if (listName.get(0).equals("0")) {
					result = "";
				} else if (listName.get(0).equals("1")) {
					result = " , 비";
					nowIcon = makeIcon(FilePath.weatherIconDir + "rain.png");
				} else if (listName.get(0).equals("2")) {
					result = " , 비/눈";
					nowIcon = makeIcon(FilePath.weatherIconDir + "r/img/rain.png");
				} else if (listName.get(0).equals("3")) {
					result = " , 눈";
					nowIcon = makeIcon(FilePath.weatherIconDir + "snow.png");
				} else if (listName.get(0).equals("4")) {
					result = " , 소나기";
					nowIcon = makeIcon(FilePath.weatherIconDir + "rain.png");
				}
			}

			// 오전일 경우
			else if (ampm == "am") {

				for (int i = 0; i < (listName.size() / 2); i++) {
					if (listName.get(i).equals("0")) {
						result = "";
					} else if (listName.get(i).equals("1")) {
						result = " , 비";
						m_icon = makeIcon(FilePath.weatherIconDir + "rain.png");
					} else if (listName.get(i).equals("2")) {
						result = " , 비/눈";
						m_icon = makeIcon(FilePath.weatherIconDir + "rain.png");
					} else if (listName.get(i).equals("3")) {
						result = " , 눈";
						m_icon = makeIcon(FilePath.weatherIconDir + "snow.png");
					} else if (listName.get(i).equals("4")) {
						result = " , 소나기";
						m_icon = makeIcon(FilePath.weatherIconDir + "rain.png");
					}
					if (!listName.get(i).equals("0")) {
						break;
					}
				}
			}
			// 오후일 경우
			else if (ampm == "pm") {
				for (int i = (listName.size() / 2); i < listName.size(); i++) {
					if (listName.get(i).equals("0")) {
						result = "";
					} else if (listName.get(i).equals("1")) {
						result = " , 비";
						n_icon = makeIcon(FilePath.weatherIconDir + "rain.png");
					} else if (listName.get(i).equals("2")) {
						result = " , 비/눈";
						n_icon = makeIcon(FilePath.weatherIconDir + "rain.png");
					} else if (listName.get(i).equals("3")) {
						result = " , 눈";
						n_icon = makeIcon(FilePath.weatherIconDir + "snow.png");
					} else if (listName.get(i).equals("4")) {
						result = " , 소나기";
						n_icon = makeIcon(FilePath.weatherIconDir + "rain.png");
					}
					if (!listName.get(i).equals("0")) {
						break;
					}
				}
			}
		}
		return result;

	}

	public JLabel makeIcon(String path) {
		weatherIcon = new ImageIcon(path);
		img = weatherIcon.getImage();
		img = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
		return new JLabel(weatherIcon = new ImageIcon(img));
	}

	public void getDustData() throws IOException, ParserConfigurationException, XPathExpressionException, SAXException {
		Dust dust = DustApi.main(weatherMain.searchFirstSep, weatherMain.searchSecondSep);
		
		int pm10Value = dust.getPm10Value();
		String o3Value = dust.getO3Value();

		if (pm10Value <= 30) {
			la_state.setText("좋음");
			la_coment.setText("밖으로 외출해보는건 어떨까요?");
			dustIcon = makeIcon(FilePath.weatherIconDir + "good.png");

		} else if (pm10Value <= 50) {
			dustIcon = makeIcon(FilePath.weatherIconDir + "soso.png");
			la_state.setText("보통");
			la_coment.setText("그럭저럭 좋은 날이에요.");

		} else if (pm10Value <= 100) {
			la_coment.setText("공기가 탁하네요..");
			la_state.setText("나쁨");
			dustIcon = makeIcon(FilePath.weatherIconDir + "bad.png");

		} else {
			la_state.setText("매우 나쁨");
			la_coment.setText("마스크 꼭 착용하고 외출하세요!");
			dustIcon = makeIcon(FilePath.weatherIconDir + "sobad.png");
		}

		la_pm10Value.setText("미세먼지            " + pm10Value + "㎍/m³");
		la_o3Value.setText("오존                  " + o3Value);

		dustInfo.add(dustIcon);
		dustInfo.add(la_state);
		dustInfo.add(la_coment);
		dustInfo.add(la_pm10Value);
		dustInfo.add(la_o3Value);

		add(dustInfo, BorderLayout.WEST);

	}

}
