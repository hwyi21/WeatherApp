package todolist;

public class TodoList {
	private int todolist_no; 
	private int member_no; 
	private StatusTable statusTable;
	private int status_no;
	private String content;
	private String duedate;
	
	public int getTodolist_no() {
		return todolist_no;
	}
	public void setTodolist_no(int todolist_no) {
		this.todolist_no = todolist_no;
	}
	public int getMember_no() {
		return member_no;
	}
	public void setMember_no(int member_no) {
		this.member_no = member_no;
	}
	public StatusTable getStatusTable() {
		return statusTable;
	}
	public void setStatusTable(StatusTable statusTable) {
		this.statusTable = statusTable;
	}
	public int getStatus_no() {
		return status_no;
	}
	public void setStatus_no(int status_no) {
		this.status_no = status_no;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDuedate() {
		return duedate;
	}
	public void setDuedate(String duedate) {
		this.duedate = duedate;
	}		
		
}
