package diary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import lib.FilePath;
import lib.GetDate;
import lib.ImageLabel;
import lib.TextLabel;
import main.Frame;
import main.WeatherMain;

public class DiaryPage extends Frame {
	JPanel p_left; // 좌측 컨테이너
	JLabel la_titleL; // 제목
	JLabel la_weatherText; // "오늘 날씨"
	public static ImageLabel[] la_weatherImages = new ImageLabel[5]; // 선택할 날씨 이미지 5개
	JLabel la_feelingText; // "오늘 기분"
	public static ImageLabel[] la_feelImages = new ImageLabel[5];
	String[] selectedImage = { "sunny", "cloudy", "rain", "windy", "thunder" };
	JPanel p_thumb; // 선택한 파일 썸네일
	JButton bt_choose; // 파일 선택 버튼
	JFileChooser chooser; // 파일 chooser
	JLabel la_thumb; // 썸네일
	ImageIcon thumbIcon;
	JTextArea area; // 텍스트입력 공간
	JScrollPane scroll; // 텍스트입력 공간 포함하는 스크롤
	ImageLabel la_upload; // 업로드 버튼
	JLabel la_empty1;
	JLabel la_empty2;

	JPanel p_right; // 우측 컨테이너
	ImageLabel bt_lookUp; // 목록 조회 버튼
	JLabel la_titleR; // 제목
	JPanel content;
	Thread getDbThread;
	FileInputStream fis;
	FileOutputStream fos;
	File file;
	String imageName="";
	Connection con;
	public static int weatherType = 50;
	public static int feelType = 50;

	public DiaryPage(WeatherMain weatherMain, int width, int height, int x, int y , String title, int member_no) {
		super(weatherMain, width, height, x, y, title);

		p_left = new JPanel();
		la_titleL = new TextLabel("Today's", 430, 70, 25);
		
		la_weatherText = new TextLabel("오늘의 날씨", 110, 120, 18);
		la_weatherImages[0] = new ImageLabel(FilePath.weatherIconDir + "sunny.png", 50, 50);
		la_weatherImages[1] = new ImageLabel(FilePath.weatherIconDir + "cloudy.png", 50, 50);
		la_weatherImages[2] = new ImageLabel(FilePath.weatherIconDir + "rain.png", 50, 50);
		la_weatherImages[3] = new ImageLabel(FilePath.weatherIconDir + "windy.png", 50, 50);
		la_weatherImages[4] = new ImageLabel(FilePath.weatherIconDir + "thunder.png", 50, 50);
		
		la_feelingText = new TextLabel("오늘의 기분", 110, 120, 18);
		la_feelImages[0] = new ImageLabel(FilePath.weatherIconDir + "sunny.png", 50, 50);
		la_feelImages[1] = new ImageLabel(FilePath.weatherIconDir + "cloudy.png", 50, 50);
		la_feelImages[2] = new ImageLabel(FilePath.weatherIconDir + "rain.png", 50, 50);
		la_feelImages[3] = new ImageLabel(FilePath.weatherIconDir + "windy.png", 50, 50);
		la_feelImages[4] = new ImageLabel(FilePath.weatherIconDir + "thunder.png", 50, 50);

		la_empty1 = new TextLabel("", 400, 30, 0);
		p_thumb = new JPanel(new BorderLayout());
		bt_choose = new JButton("사진 선택");
		thumbIcon = new ImageIcon(FilePath.buttonDir + "thumb.png");
		la_thumb = new JLabel() {
			public void paint(Graphics g) {
				g.drawImage(thumbIcon.getImage(), 0, 0, 120, 90, la_thumb);
			}
		};
		
		area = new JTextArea(9, 23);
		area.setLineWrap(true);
		scroll = new JScrollPane(area);
		la_empty2 = new TextLabel("", 400, 30, 0);
		la_upload = new ImageLabel(FilePath.buttonDir + "upload.png", 380, 60);

		p_right = new JPanel();
		bt_lookUp = new ImageLabel(FilePath.buttonDir + "refresh.png", 35, 35);
		la_titleR = new TextLabel("My Diary", 350, 70, 25);

		content = new JPanel();
		chooser = new JFileChooser(FilePath.resDir);

		// style
		p_left.setPreferredSize(new Dimension(430, 670));
		p_right.setPreferredSize(new Dimension(430, 670));
		content.setPreferredSize(new Dimension(430, 540));
		getContentPane().setBackground(Color.WHITE);
		p_left.setBackground(Color.WHITE);
		p_right.setBackground(Color.WHITE);
		content.setBackground(Color.WHITE);
		la_titleL.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
		la_titleR.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.red));
		la_thumb.setPreferredSize(new Dimension(120, 90));

		// add
		p_thumb.add(bt_choose, BorderLayout.NORTH);
		p_thumb.add(la_thumb);

		p_left.add(la_titleL);
		p_left.add(la_weatherText);
		p_left.add(la_weatherImages[0]);
		p_left.add(la_weatherImages[1]);
		p_left.add(la_weatherImages[2]);
		p_left.add(la_weatherImages[3]);
		p_left.add(la_weatherImages[4]);
		p_left.add(la_feelingText);
		p_left.add(la_feelImages[0]);
		p_left.add(la_feelImages[1]);
		p_left.add(la_feelImages[2]);
		p_left.add(la_feelImages[3]);
		p_left.add(la_feelImages[4]);
		p_left.add(la_empty1);
		p_left.add(p_thumb);
		p_left.add(scroll);
		p_left.add(la_empty2);
		p_left.add(la_upload);

		p_right.add(bt_lookUp);
		p_right.add(la_titleR);
		p_right.add(content);

		add(p_left, BorderLayout.WEST);
		add(p_right, BorderLayout.EAST);
		getDiaryList(member_no);

		la_upload.ifEnteredSetImage(FilePath.buttonDir + "upload_select.png");
		bt_lookUp.ifEnteredSetImage(FilePath.buttonDir + "refresh_select.png");

		for (int i = 0; i < la_weatherImages.length; i++) {
			la_weatherImages[i].ifClickedSetImage(FilePath.selectIconDir + selectedImage[i] + ".png", la_weatherImages);
		}
		for (int i = 0; i < la_feelImages.length; i++) {
			la_feelImages[i].ifClickedSetImage(FilePath.selectIconDir + selectedImage[i] + ".png", la_feelImages);
		}

		bt_choose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseFile();
			}
		});

		la_upload.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Thread thread=new Thread() {
					public void run() {
						if (weatherType>4 || feelType>4 || imageName.equals("") || area.getText().equals("")) {
							JOptionPane.showMessageDialog(DiaryPage.this, "선택하지 않은 항목이 있습니다.");
						} else {
							upLoad(member_no);							
						}
					}
				};
				thread.start();
			}
		});

		bt_lookUp.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Thread thread=new Thread() {
					public void run() {
						getDiaryList(member_no);
						
					}
				};
				thread.start();
			}
		});
	}

	// 이미지파일 선택 후 썸네일 표현
	public void chooseFile() {
		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				file = chooser.getSelectedFile();
				fis = new FileInputStream(file);
				thumbIcon = new ImageIcon(file.getAbsolutePath());
				la_thumb.repaint();
				
				long time = System.currentTimeMillis();
				String ext = FilePath.getEXT(file.getAbsolutePath());
				imageName = time + "." + ext;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean copyImage() {
		boolean result = false;
		try {
			fos = new FileOutputStream(FilePath.copyObjectDir + imageName);

			byte[] buff = new byte[1024];
			int data = -1;
			while (true) {
				data = fis.read(buff);
				if (data == -1)
					break;
				fos.write(buff);
			}
			result = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "이미지 경로를 다시 확인해주세요.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public void upLoad(int member_no) {
		int answer = JOptionPane.showConfirmDialog(this, "등록하시겠습니까?");
		if (answer == JOptionPane.OK_OPTION) {
			if (copyImage()) {
				String diaryContent = area.getText();
				String registDate = GetDate.text_todayDate();
				String registTime = GetDate.text_nowTime();

				String sql = "insert into diary(diary_no, member_no, regist_date, regist_time, weathertype, feeltype, image, content)";
				sql += " values(seq_diary.nextval, ?, ?, ?, ?, ?, ?, ?)";

				PreparedStatement pstmt = null;
				try {
					weatherMain.con.setAutoCommit(false);
					pstmt = weatherMain.con.prepareStatement(sql);
					pstmt.setInt(1, member_no); // member_no
					pstmt.setString(2, registDate); // regist_date
					pstmt.setString(3, registTime); // regist_time
					pstmt.setInt(4, weatherType + 1); // weathertype
					pstmt.setInt(5, feelType + 1); // feeltype
					pstmt.setString(6, imageName); // image
					pstmt.setString(7, diaryContent); // content

					int result = pstmt.executeUpdate();
					if (result != 0) {
						JOptionPane.showMessageDialog(this, "등록되었습니다.");
						weatherMain.con.commit();
						area.setText("");
						thumbIcon = new ImageIcon(FilePath.buttonDir+"thumb.png");
						la_thumb.repaint();

						la_weatherImages[0].setImage(FilePath.weatherIconDir + "sunny.png", 50, 50);
						la_weatherImages[1].setImage(FilePath.weatherIconDir + "cloudy.png", 50, 50);
						la_weatherImages[2].setImage(FilePath.weatherIconDir + "rain.png", 50, 50);
						la_weatherImages[3].setImage(FilePath.weatherIconDir + "windy.png", 50, 50);
						la_weatherImages[4].setImage(FilePath.weatherIconDir + "thunder.png", 50, 50);
						
						la_feelImages[0].setImage(FilePath.weatherIconDir + "sunny.png", 50, 50);
						la_feelImages[1].setImage(FilePath.weatherIconDir + "cloudy.png", 50, 50);
						la_feelImages[2].setImage(FilePath.weatherIconDir + "rain.png", 50, 50);
						la_feelImages[3].setImage(FilePath.weatherIconDir + "windy.png", 50, 50);
						la_feelImages[4].setImage(FilePath.weatherIconDir + "thunder.png", 50, 50);
						
						getDiaryList(member_no);
						imageName="";
					} else {
						JOptionPane.showMessageDialog(this, "등록에 실패했습니다.\n확인 후 다시 시도해주세요.");
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
					weatherMain.connectionManager.closeDB(pstmt);
				}
			}
		}
	}

	public void getDiaryList(int member_no) {
		setRightPanel();
		String sql = "select d.DIARY_NO , d.MEMBER_NO , d.REGIST_DATE , d.REGIST_TIME , w.WEATHERTYPE as weathertype, f.FEELTYPE as feeltype, d.IMAGE , d.CONTENT";
		sql += " from diary d, weathertype w, feeltype f where member_no=" + member_no;
		sql += " and d.weathertype=w.weather_id and d.feeltype=f.feel_id order by diary_no desc";

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			
			pstmt = weatherMain.con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Diary diary = new Diary();
				diary.setDiary_no(rs.getInt("diary_no"));
				diary.setMember_no(rs.getInt("member_no"));
				diary.setRegist_date(rs.getString("regist_date"));
				diary.setRegist_time(rs.getString("regist_time"));
				diary.setWeathertype(rs.getString("weathertype"));
				diary.setFeeltype(rs.getString("feeltype"));
				diary.setImage(rs.getString("image"));
				diary.setContent(rs.getString("content"));

				int num = diary.getDiary_no();
				String date = diary.getRegist_date();
				String time = diary.getRegist_time();
				String wt = diary.getWeathertype();
				String ft = diary.getFeeltype();
				String path = diary.getImage();
				String diaryText = diary.getContent();

				ImageLabel card = new ImageLabel(FilePath.copyObjectDir + diary.getImage(), 130, 130);
				card.ifClickedNewDiaryFram(weatherMain, this, member_no, num, date, time, wt, ft, path, diaryText);
				content.add(card);
			}
			content.updateUI();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			weatherMain.connectionManager.closeDB(pstmt, rs);
		}
	}

	// p_right의 모든 카드 삭제
	public void setRightPanel() {
		Component[] childList = content.getComponents();

		for (int i = 0; i < childList.length; i++) {
			if (childList[i] != la_titleR && childList[i] != bt_lookUp) {
				content.remove(childList[i]);
			}
		}
	}
}