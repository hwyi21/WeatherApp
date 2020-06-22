package main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import api.DustApi;
import dbconnection.ConnectionManager;
import diary.DiaryPage;
import lib.GetDate;
import recommend.RecommendPage;
import todolist.TodoListPage;
import weather.ForecastPage;
import weather.TimeLinePage;

public class WeatherMain extends JFrame {
	
	JPanel container; // 날씨 선택 + 날씨 영역 + 시간대별 날씨가 모두 들어갈 패널
	JPanel dayMenu; // 날짜 선택
	JPanel weatherArea; // 날씨 영역
	JPanel timeLine; // 시간대별 날씨
	public JPanel todoList; // 오늘의 할일

	JButton bt_login; // 로그인 버튼
	JButton bt_diary; // 다이어리 버튼
	JButton bt_recommend; // 추천 버튼

	JComboBox<String> firstSepCb;
	JComboBox<String> secondSepCb;
	JComboBox<String> thirdSepCb;
	JButton search;
	ForecastPage forecastPage; // 날씨 정보
	TimeLinePage timeLinePage; // 시간별 날씨 정보
	TodoListPage todoListPage; // todolist
	public String searchFirstSep = "서울특별시";
	public String searchSecondSep = "종로구";
	String searchThirdSep = "종로1.2.3.4가동";
	public String nx = "60";
	public String ny = "127";

	JLabel w_today; // 오늘
	JLabel w_tomorrow;// 내일
	JLabel w_afterTomorrow; // 모레

	Login login; // 로그인
	DiaryPage diaryPage;
	RecommendPage recommendPage;

	// 데이터베이스 연결
	public ConnectionManager connectionManager;
	public Connection con;

	boolean hasAuth = false; // 로그인한 경우 true, 안한경우 false
	int member_no; // 로그인한 계정 정보

	String todayDate = GetDate.getDate(0); // 오늘날짜 구하기
	String tomorrowDate = GetDate.getDate(1); // 내일날짜 구하기
	String afterTomorrowDate = GetDate.getDate(2); // 모레 날짜 구하기

	public WeatherMain() {
		container = new JPanel();
		dayMenu = new JPanel();
		weatherArea = new JPanel();
		timeLine = new JPanel();
		todoList = new JPanel();

		firstSepCb = new JComboBox<String>();
		secondSepCb = new JComboBox<String>();
		thirdSepCb = new JComboBox<String>();
		search = new JButton("검색");

		bt_login = new JButton("로그인");
		bt_diary = new JButton("다이어리");
		bt_recommend = new JButton("오늘의 추천");

		w_today = new JLabel("오늘");
		w_tomorrow = new JLabel("내일");
		w_afterTomorrow = new JLabel("모레");

		// 데이터베이스 연결
		connectionManager = connectionManager.getInstance();
		con = connectionManager.getConnection();

		// 디자인
		w_today.setPreferredSize(new Dimension(180, 50));
		w_tomorrow.setPreferredSize(new Dimension(180, 50));
		w_afterTomorrow.setPreferredSize(new Dimension(180, 50));
		w_today.setHorizontalAlignment(JLabel.CENTER);
		w_tomorrow.setHorizontalAlignment(JLabel.CENTER);
		w_afterTomorrow.setHorizontalAlignment(JLabel.CENTER);
		dayMenu.setPreferredSize(new Dimension(700, 70));
		w_today.setFont(new Font("돋움", Font.BOLD, 14));
		w_tomorrow.setFont(new Font("돋움", Font.BOLD, 14));
		w_afterTomorrow.setFont(new Font("돋움", Font.BOLD, 14));

		// 조립
		dayMenu.add(firstSepCb);
		dayMenu.add(secondSepCb);
		dayMenu.add(thirdSepCb);
		dayMenu.add(search);
		dayMenu.add(bt_login);
		dayMenu.add(bt_diary);
		dayMenu.add(bt_recommend);
		dayMenu.add(w_today);
		dayMenu.add(w_tomorrow);
		dayMenu.add(w_afterTomorrow);

		forecastPage = new ForecastPage(todayDate, this);
		weatherArea.add(forecastPage);
		timeLinePage = new TimeLinePage(todayDate, this);
		timeLine.add(timeLinePage);
		todoListPage = new TodoListPage(this, todayDate, hasAuth, member_no);
		todoList.add(todoListPage);
		container.setLayout(new BorderLayout());
		container.add(dayMenu, BorderLayout.NORTH);
		container.add(weatherArea, BorderLayout.CENTER);
		container.add(timeLine, BorderLayout.SOUTH);

		getFirstSep();
		firstSepCb.setSelectedItem(searchFirstSep);
		getSecondSep();
		secondSepCb.setSelectedItem(searchSecondSep);
		getThirdSep();
		thirdSepCb.setSelectedItem(searchThirdSep);

		this.setLayout(new BorderLayout());
		add(container, BorderLayout.CENTER);
		add(todoList, BorderLayout.SOUTH);
		setTitle("날씨 앱");
		pack();
		setVisible(true);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		// 윈도우 창 닫을 시 db 연결 해제
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				connectionManager.closeDB(con);
				System.exit(0);
			}
		});

		search.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				showSearchDate(todayDate);
			}
		});

		firstSepCb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				getSecondSep();
			}
		});
		secondSepCb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				getThirdSep();
			}
		});

		// 오늘 라벨 클릭 시 오늘 날짜의 날씨 / todolist 조회
		w_today.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				showSearchDate(todayDate);
			}
		});

		// 내일 라벨 클릭 시 내일 날짜의 날씨 / todolist 조회
		w_tomorrow.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				showSearchDate(tomorrowDate);
			}
		});

		// 모레 라벨 클릭 시 모레 날짜의 날씨 / todolist 조회
		w_afterTomorrow.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				showSearchDate(afterTomorrowDate);
			}
		});

		// 로그인 기능
		bt_login.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (!hasAuth) {
					login = new Login(WeatherMain.this, 250, 220, 800, 300, "로그인");
				} else if (hasAuth) {
					logout();
				}
			}

		});

		bt_diary.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (hasAuth) {
					diaryPage = new DiaryPage(WeatherMain.this, 960, 690, 500, 200, "다이어리", member_no);
				} else {
					JOptionPane.showMessageDialog(WeatherMain.this, "로그인 후 이용 가능합니다");
				}

			}
		});

		bt_recommend.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				recommendPage = new RecommendPage(WeatherMain.this, 1000, 600, 500, 200, "오늘의 추천 장소", searchFirstSep,
						searchSecondSep, searchThirdSep);
			}
		});

	}

	public void getFirstSep() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select distinct first_sep from location order by first_sep asc";

		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				String value = rs.getString("first_sep");
				firstSepCb.addItem(value);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectionManager.closeDB(pstmt, rs);
		}
	}

	public void getSecondSep() {

		secondSepCb.removeAllItems();
		thirdSepCb.removeAllItems();

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select distinct second_sep from location where first_sep=? order by second_sep asc";

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, (String) firstSepCb.getSelectedItem());
			rs = pstmt.executeQuery();

			while (rs.next()) {
				String value = rs.getString("second_sep");
				secondSepCb.addItem(value);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectionManager.closeDB(pstmt, rs);
		}
	}

	public void getThirdSep() {

		thirdSepCb.removeAllItems();

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select distinct third_sep from location where first_sep=? and second_sep=? order by third_sep asc";

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, (String) firstSepCb.getSelectedItem());
			pstmt.setString(2, (String) secondSepCb.getSelectedItem());
			rs = pstmt.executeQuery();

			while (rs.next()) {
				String value = rs.getString("third_sep");
				thirdSepCb.addItem(value);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectionManager.closeDB(pstmt, rs);
		}
	}
	
	//날짜 / 지역에 맞는 날씨 데이터 불러오기
	public void showSearchDate(String date) {
		searchFirstSep = (String) firstSepCb.getSelectedItem();
		searchSecondSep = (String) secondSepCb.getSelectedItem();
		searchThirdSep = (String) thirdSepCb.getSelectedItem();

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "select nx, ny from location where first_sep=? and second_sep=? and third_sep=?";

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, searchFirstSep);
			pstmt.setString(2, searchSecondSep);
			pstmt.setString(3, searchThirdSep);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				nx = rs.getString("nx");
				ny = rs.getString("ny");
			}
			showForecast(date);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectionManager.closeDB(pstmt, rs);
		}

	}

	// 날씨 정보 호출
	public void showForecast(String date) {
		Thread thread = new Thread() {
			public void run() {
				forecastPage = new ForecastPage(date, WeatherMain.this);
				resetWeatherArea(weatherArea, forecastPage);
			}
		};
		thread.start();
		
		Thread thread2 = new Thread() {
			public void run() {
				timeLinePage = new TimeLinePage(date, WeatherMain.this);
				resetWeatherArea(timeLine, timeLinePage);
				
			}
		};
		thread2.start();
		
		Thread thread3 = new Thread() {
			public void run() {
				todoListPage = new TodoListPage(WeatherMain.this, date, hasAuth, member_no);
				resetWeatherArea(todoList, todoListPage);
			}
		};
		thread3.start();
	}

	// 날씨 정보 호출 시 날짜에 맞는 정보를 가져오기 위한 UI 갱신
	public void resetWeatherArea(JPanel area, Component page) {
		area.removeAll();
		area.add(page);
		area.updateUI();
	}

	// 로그아웃
	public void logout() {
		int ans = JOptionPane.showConfirmDialog(this, "로그아웃 하시겠습니까?");
		if (ans == JOptionPane.OK_OPTION) { // 로그아웃 원하는 사람
			hasAuth = false;
			bt_login.setText("로그인");
			JOptionPane.showMessageDialog(this, "로그아웃되었습니다");

			// 로그 아웃 시 todolist 새로 생성
			todoListPage = new TodoListPage(this, todayDate, hasAuth, member_no);
			resetWeatherArea(todoList, todoListPage);
		}
	}

	public static void main(String[] args) {
		new WeatherMain();
	}

}
