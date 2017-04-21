package util.file;

import java.io.File;

public class FileUtil {
	String path;

	
	//확장자를 제외한 파일명만 추출하기 
	//c:/data/test.jpg , mario.png ->두가지 경우 모두 파일이름(test, mario)만 추출할 수 있게
	//m.a.rio.png
	public static String getOnlyName(String path){
		int last=path.lastIndexOf("."); //split은 m.a.rio.png와 같은 경우엔 쓸 수 없으므로 한정됨
		return path.substring(0, last);
		
	}
	/*
	public static void main(String[] args) {
		System.out.println(getOnlyName("m.a.rio.png"));
	}*/
}
