package lib;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

public class TextLabel extends JLabel {

	public TextLabel(String text, int width, int height, int fontSize, int fontStyle, int position) {
		super(text);

		setPreferredSize(new Dimension(width, height));
		setFont(new Font("돋움", fontStyle, fontSize));
		setHorizontalAlignment(position);
	}

	// 글꼴 굵게, 가운데정렬 기본 지정
	public TextLabel(String text, int width, int height, int fontSize) {
		super(text);

		setPreferredSize(new Dimension(width, height));
		setFont(new Font("돋움", Font.BOLD, fontSize));
		setHorizontalAlignment(JLabel.CENTER);
	}
}