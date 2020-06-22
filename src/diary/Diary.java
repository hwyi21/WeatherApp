package diary;

public class Diary {
	private int diary_no;
	private int member_no;
	private String regist_date;
	private String regist_time;
	private String weathertype;
	private String feeltype;
	private String image;
	private String content;

	public int getDiary_no() {
		return diary_no;
	}

	public void setDiary_no(int diary_no) {
		this.diary_no = diary_no;
	}

	public int getMember_no() {
		return member_no;
	}

	public void setMember_no(int member_no) {
		this.member_no = member_no;
	}

	public String getRegist_date() {
		return regist_date;
	}

	public void setRegist_date(String regist_date) {
		this.regist_date = regist_date;
	}

	public String getRegist_time() {
		return regist_time;
	}

	public void setRegist_time(String regist_time) {
		this.regist_time = regist_time;
	}

	public String getWeathertype() {
		return weathertype;
	}

	public void setWeathertype(String weathertype) {
		this.weathertype = weathertype;
	}

	public String getFeeltype() {
		return feeltype;
	}

	public void setFeeltype(String feeltype) {
		this.feeltype = feeltype;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
