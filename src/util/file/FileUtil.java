package util.file;

import java.io.File;

public class FileUtil {
	String path;

	
	//Ȯ���ڸ� ������ ���ϸ� �����ϱ� 
	//c:/data/test.jpg , mario.png ->�ΰ��� ��� ��� �����̸�(test, mario)�� ������ �� �ְ�
	//m.a.rio.png
	public static String getOnlyName(String path){
		int last=path.lastIndexOf("."); //split�� m.a.rio.png�� ���� ��쿣 �� �� �����Ƿ� ������
		return path.substring(0, last);
		
	}
	/*
	public static void main(String[] args) {
		System.out.println(getOnlyName("m.a.rio.png"));
	}*/
}
