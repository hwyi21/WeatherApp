package lib;

import java.io.File;

public class FilePath {
	public static String resDir=new File("src/res").getAbsolutePath();
	public static String buttonDir=resDir+"./button/";
	public static String weatherIconDir=resDir+"./weather/";
	public static String selectIconDir=resDir+"./selectIcon/";
	public static String copyObjectDir=resDir+"./copyObject/";
	
	public static String getEXT(String path) {
		
		// 1) 파일명.확장자 추출	(마지막 슬래시 다음 ~ 끝)
		int lastIndex=path.lastIndexOf("\\");		// escape 시키면 특수문자가 기능을 타룰하여 그냥 일반문자 처리됨
		String fileName=path.substring(lastIndex+1, path.length());
		
		// 2) 파일명.확장자 로부터 확장자만 추출		(가장 마지막 . ~ 끝)
		lastIndex=fileName.lastIndexOf(".");
		String exit=fileName.substring((lastIndex)+1, fileName.length());
		return exit;
	}
	
}