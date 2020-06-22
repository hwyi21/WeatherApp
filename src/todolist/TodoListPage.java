// 오늘의 할일 페이지
package todolist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import main.WeatherMain;

public class TodoListPage extends JPanel {
	JPanel content;
	JPanel registArea;

	JLabel title;
	JTextField write; // 내용 입력
	JButton regist; // 등록 버튼
	JButton edit; // 수정 버튼
	JButton delete; // 삭제 버튼
	JButton all_todolist; // 모든 todolist 조회

	JTable table;
	JScrollPane scroll;

	JComboBox statusBox; // todolist의 상태를 알려주는 콤보박스

	TodoListModel model;
	MyTodoList myTodoList;
	WeatherMain weatherMain;
	
	int todolist_no; // todolist 글 번호
	int row;
	int col;

	

	public TodoListPage(WeatherMain weatherMain, String date, boolean hasAuth, int member_no) { // hasAuth 로그인 상태 / member_no 로그인한 계정 정보
		this.weatherMain=weatherMain;
		content = new JPanel();
		registArea = new JPanel();

		this.title = new JLabel("오늘의 할일");
		all_todolist = new JButton("나의 todolist");
		edit = new JButton("수정");
		delete = new JButton("삭제");
		write = new JTextField(50);
		regist = new JButton("등록");
		table = new JTable(model = new TodoListModel());
		scroll = new JScrollPane(table);

		TableColumn comm = table.getColumnModel().getColumn(1);
		statusBox = new JComboBox();
		statusBox.addItem("할 일");
		statusBox.addItem("진행 중");
		statusBox.addItem("완료");
		comm.setCellEditor(new DefaultCellEditor(statusBox));

		table.setRowHeight(30);
		table.setBackground(Color.WHITE);


		// 디자인
		registArea.setPreferredSize(new Dimension(700, 80));
		registArea.setBackground(Color.CYAN);
		table.getColumnModel().getColumn(0).setPreferredWidth(8);
		table.getColumnModel().getColumn(1).setPreferredWidth(30);
		table.getColumnModel().getColumn(2).setPreferredWidth(300);
		table.getColumnModel().getColumn(3).setPreferredWidth(80);
		scroll.setPreferredSize(new Dimension(680, 175));
		scroll.getViewport().setBackground(Color.WHITE);
		table.setBackground(Color.WHITE);
		title.setFont(new Font("돋움", Font.BOLD, 20));
		title.setHorizontalAlignment(JLabel.LEFT);
		title.setPreferredSize(new Dimension(400, 30));

		content.setPreferredSize(new Dimension(700, 220));
		content.setBackground(Color.CYAN);

		registArea.add(title);
		registArea.add(all_todolist);
		registArea.add(edit);
		registArea.add(delete);
		registArea.add(write);
		registArea.add(regist);
		content.add(scroll);

		setLayout(new BorderLayout());
		add(registArea, BorderLayout.NORTH);
		add(content);

		// 로그인 상태인 경우 todolist 목록 로드
		if (hasAuth) {
			load(date, member_no);		
		}
		
		// todolist 등록
		regist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(hasAuth) {
					insertTodo(date, member_no);
				}else {
					JOptionPane.showMessageDialog(TodoListPage.this, "로그인 후 이용 ");
				}
			}
		});

		// todolist 수정
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(hasAuth) {
					editTodo(date, member_no);
				}else {
					JOptionPane.showMessageDialog(TodoListPage.this, "로그인 후 이용 ");
				}
			}
		});

		// todolist 삭제
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(hasAuth) {
					deleteTodo(date, member_no);
				}else {
					JOptionPane.showMessageDialog(TodoListPage.this, "로그인 후 이용 ");
				}
			}
		});

		// 나의 모든 todolist 조회
		all_todolist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(hasAuth) {
					myTodoList = new MyTodoList(weatherMain, 600, 500, 900, 300, "나의 TodoList",hasAuth, member_no, date);
				}else {
					JOptionPane.showMessageDialog(TodoListPage.this, "로그인 후 이용 ");
				}
				
			}
		});

		// table과 마우스리스너 연결
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				row = table.getSelectedRow();
				col = table.getSelectedColumn();
				String value = (String) table.getValueAt(row, 0);
				todolist_no = Integer.parseInt(value);// 클릭시마다 선택한 todolist_no를 보관함
			}
		});
		
		
	}

	// 테이블 조회
	public void load(String dueDate, int member_no) {
		StringBuilder sql = new StringBuilder();
		sql.append("select todolist.todolist_no, status.status");
		sql.append(", todolist.content, todolist.duedate");
		sql.append(" from todolist, status");
		sql.append(" where todolist.status_no=status.status_no");
		sql.append(" and todolist.member_no=?");
		sql.append(" and todolist.duedate=?");
		sql.append(" order by todolist.todolist_no asc");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = weatherMain.con.prepareStatement(sql.toString());
			pstmt.setInt(1, member_no);
			pstmt.setString(2, dueDate);

			rs = pstmt.executeQuery();
			List list = new ArrayList();

			while (rs.next()) {
				StatusTable statusTable = new StatusTable();
				TodoList todolist = new TodoList();

				todolist.setStatusTable(statusTable);

				todolist.setTodolist_no(rs.getInt("todolist_no"));
				statusTable.setStatus(rs.getString("status"));
				todolist.setContent(rs.getString("content"));
				todolist.setDuedate(rs.getString("duedate"));

				list.add(todolist); 
			}
			model.list = list;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			weatherMain.connectionManager.closeDB(pstmt, rs);
		}
		table.updateUI();
	}
	
	// todo리스트 삽입
	public void insertTodo(String dueDate, int member_no) {
		String sql = "insert into todolist(todolist_no, member_no, status_no, content, duedate)";
		sql += " values(seq_todolist.nextval,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			weatherMain.con.setAutoCommit(false);
			pstmt = weatherMain.con.prepareStatement(sql);

			pstmt.setInt(1, member_no);
			pstmt.setInt(2, 1); // todolist 상태는 "할일"

			// 텍스트필드가 비어있는데 등록하려는 경우 얼럿창 띄우기
			if (write.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "내용을 입력해주세요");
				return;
			} else {
				pstmt.setString(3, write.getText());
			}
			pstmt.setString(4, dueDate);

			int result = pstmt.executeUpdate();
			if (result == 0) {
				JOptionPane.showMessageDialog(this, "등록실패");
			} else {
				JOptionPane.showMessageDialog(this, "등록 성공");
				weatherMain.con.commit(); // 커밋
				load(dueDate, member_no);
				write.setText("");
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

	// todo리스트 수정
	public void editTodo(String dueDate, int member_no) {
		if (todolist_no == 0) {
			JOptionPane.showMessageDialog(this, "수정하실 레코드를 선택하세요");
			return;
		}
		int result = JOptionPane.showConfirmDialog(this, "수정하시겠습니까?");
		if (result == JOptionPane.OK_OPTION) {
		
			String sql = "update todolist set status_no=(select status_no from status where status.status=?), content=?, duedate=? where todolist_no="
					+ todolist_no;

			int success = 0;
			PreparedStatement pstmt = null;
			try {
				weatherMain.con.setAutoCommit(false);
				pstmt = weatherMain.con.prepareStatement(sql);
				pstmt.setString(1, (String) table.getValueAt(row, 1));
				pstmt.setString(2, (String) table.getValueAt(row, 2));
				pstmt.setString(3, (String) table.getValueAt(row, 3));
				success = pstmt.executeUpdate();// DML(insert,delete,update)
				
				if (success == 0) {
					JOptionPane.showMessageDialog(this, "수정 실패");
				} else {
					JOptionPane.showMessageDialog(this, "수정 성공");
					weatherMain.con.commit();
					load(dueDate, member_no);
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

	// todo리스트 삭제
	public void deleteTodo(String dueDate, int member_no) {
		// 유저가 선택한 레코드를 삭제한다.
		// 체크사항 1. 선택한 레코드가 있는지 여부 2. 삭제할 의지 확인
		if (todolist_no == 0) {
			JOptionPane.showMessageDialog(this, "삭제하실 레코드를 선택하세요");
			return;
		}

		// 삭제할 자격이 생기면
		int result = JOptionPane.showConfirmDialog(this, "삭제하시겠습니까?");
		if (result == JOptionPane.OK_OPTION) {

			String sql = "delete from todolist where todolist_no=" + todolist_no;

			int success = 0;
			PreparedStatement pstmt = null;
			try {
				weatherMain.con.setAutoCommit(false);
				pstmt =weatherMain.con.prepareStatement(sql);
				success = pstmt.executeUpdate();
				
				if (success == 0) {
					JOptionPane.showMessageDialog(this, "삭제 실패");
				} else {
					JOptionPane.showMessageDialog(this, "삭제 성공");
					weatherMain.con.commit();
					load(dueDate, member_no);
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
				weatherMain.connectionManager.closeDB(pstmt); // DML이므로..
			}

		}
	}
}
