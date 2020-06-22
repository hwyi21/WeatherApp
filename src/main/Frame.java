package main;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Frame extends JFrame{
	public WeatherMain weatherMain;
	int width;
	int height;
	int x;
	int y;
	String title;
	
	public Frame(WeatherMain weatherMain, int width, int height, int x, int y , String title) {
		this.weatherMain = weatherMain;
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
		this.title=title;
		
		this.setSize(width, height);
		this.setLocation(x, y);
		this.setTitle(title);
		this.setVisible(true);
	}
}
