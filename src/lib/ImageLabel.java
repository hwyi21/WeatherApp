package lib;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import diary.DiaryFrame;
import diary.DiaryPage;
import main.WeatherMain;

public class ImageLabel extends JLabel {
	ImageIcon icon;
	Image img;

	String path;
	int width;
	int height;
	int clickCount;
	int thisNum=50;

	public ImageLabel(String path, int width, int height) {
		this.path = path;
		this.width = width;
		this.height = height;

		setPreferredSize(new Dimension(width, height));
		setImage(path, width, height);
	}

	public void ifEnteredSetImage(String path_2) {
		addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				setImage(path_2, width, height);
				ImageLabel.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			public void mouseExited(MouseEvent e) {
				setImage(path, width, height);
			}
		});
	}

	public void ifClickedSetImage(String path_2, ImageLabel[] list) {
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// 클릭 시 이미지 변경
				if(clickCount%2==0) {
					for(int i=0;i<list.length;i++) {
						list[i].setImage(list[i].path);
						list[i].clickCount=2;
						if(ImageLabel.this.equals(list[i]))  thisNum=i;
					}
					setImage(path_2);
					clickCount=1;
				} else {
					setImage(path);
					clickCount=0;
					thisNum=50;
				}
				if(list.equals(DiaryPage.la_weatherImages)) {
					DiaryPage.weatherType=thisNum;
				} else {
					DiaryPage.feelType=thisNum;
				}
			
			}
		});
	}

	
	
	public void ifClickedNewDiaryFram(WeatherMain weatherMain, DiaryPage diaryPage, int member_no, int num, String date, String time, String wt, String ft, String img, String content) {
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Thread thread=new Thread() {
					public void run() {
						new DiaryFrame(weatherMain, diaryPage, member_no, num, date, time, wt, ft, img, content);
					}
				};
				thread.start();
			}
		});
	}

	public void setImage(String path, int width, int height) {
		icon = new ImageIcon(path);
		img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		setIcon(icon);
	}
	
	public void setImage(String path) {
		setImage(path, width, height);
	}
}