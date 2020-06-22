package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import lib.GetDate;
import todolist.TodoListPage;
import weather.ForecastPage;
import weather.TimeLinePage;

public class Login extends Frame {
	JPanel loginBox;
	JPanel form; // 로그인 폼
	JLabel la_id;
	JLabel la_pw;
	JLabel la_name;
	JTextField t_id; // 아이디 입력
	JPasswordField t_pw; // 비밀번호 입력
	JTextField t_name;
	JButton bt_login; // 로그인 버튼
	JButton bt_regist; // 회원가입 버튼

	TodoListPage todoListPage;

	public Login(WeatherMain weatherMain,  int width, int height, int x, int y , String title) {
		super(weatherMain, width, height, x, y, title);
		
		todoListPage = null;
		loginBox = new JPanel();
		form = new JPanel();
		la_id = new JLabel("ID");
		la_pw = new JLabel("PassWord");
		t_id = new JTextField();
		t_pw = new JPasswordField();
		bt_login = new JButton("로그인");
		bt_regist = new JButton("회원가입");

		// 스타일 적용
		loginBox.setBackground(Color.WHITE);
		loginBox.setPreferredSize(new Dimension(250, 170));
		form.setPreferredSize(new Dimension(200, 100));
		form.setBackground(Color.WHITE);

		// 레이아웃 변경
		form.setLayout(new GridLayout(3, 2));

		// 조립
		form.add(la_id);
		form.add(t_id);
		form.add(la_pw);
		form.add(t_pw);

		loginBox.add(form);
		loginBox.add(bt_login);
		loginBox.add(bt_regist);


		add(loginBox);
		

		// 버튼에 리스너 연결
		bt_login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginCheck();
			}
		});

		bt_regist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTitle("회원가입");
				regist();
			}
		});

		t_pw.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {			
					loginCheck();
				}
			}
		});
	}

	public void loginCheck() {

		String sql = "select * from member where member_id=? and member_passwd=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = weatherMain.con.prepareStatement(sql);
			pstmt.setString(1, t_id.getText()); // id
			pstmt.setString(2, new String(t_pw.getPassword()));

			rs = pstmt.executeQuery(); // 쿼리 수행

			if (rs.next()) {// 일치하는 데이터 존재함
				weatherMain.hasAuth = true;
				
				JOptionPane.showMessageDialog(this, t_id.getText() + " 님 로그인 되었습니다.");

				weatherMain.bt_login.setText("로그아웃");
				weatherMain.member_no = rs.getInt(1); // 로그인 계정 정보(member_no)
				
				// 오늘 날짜의 날씨 정보 호출
				Thread thread = new Thread() {
					public void run() {
						weatherMain.showForecast(GetDate.getDate(0));
					}
				};
				thread.start();
												
				dispose();

			} else {
				// 일치하는 데이터 없음
				weatherMain.hasAuth = false;
				JOptionPane.showMessageDialog(this, "로그인 실패.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			weatherMain.connectionManager.closeDB(pstmt, rs);
		}

	}

	public void regist() {
		loginBox.removeAll();
		la_name = new JLabel("이름");
		t_name = new JTextField();
		JButton bt_insertMember = new JButton("회원가입");
		t_id.setText("");
		t_pw.setText("");
		
		loginBox.setPreferredSize(new Dimension(250, 250));
		form.setPreferredSize(new Dimension(200, 120));
		form.setLayout(new GridLayout(4, 2));
		form.add(la_id);
		form.add(t_id);
		form.add(la_pw);
		form.add(t_pw);
		form.add(la_name);
		form.add(t_name);

		loginBox.add(form);
		loginBox.add(bt_insertMember);
		add(loginBox);
		loginBox.updateUI();

		bt_insertMember.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registMember();
			}
		});

	}

	public void registMember() {
		String sql = "insert into member(member_no, member_id, member_passwd, member_name)";
		sql += " values(seq_member.nextval,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			weatherMain.con.setAutoCommit(false);
			pstmt = weatherMain.con.prepareStatement(sql);

			pstmt.setString(1, t_id.getText());
			pstmt.setString(2, new String(t_pw.getPassword()));
			pstmt.setString(3, t_name.getText());

			int result = pstmt.executeUpdate();
			
			if (result == 0) {
				JOptionPane.showMessageDialog(this, "등록실패");
			} else {
				JOptionPane.showMessageDialog(this, "회원가입 성공");
				weatherMain.con.commit(); // 커밋
				dispose();
				new Login(weatherMain, 250, 220, 800, 300, "로그인");
			}
		} catch (SQLException e) {
			try {
				weatherMain.con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				weatherMain.con.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			weatherMain.connectionManager.closeDB(pstmt); // 자원해제
		}

	}

}
