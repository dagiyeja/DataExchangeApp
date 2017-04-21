package pratice;

import java.io.File;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class Model extends AbstractTableModel{
	Vector <String>columnName=new Vector<String>();
	Vector <Vector> data=new Vector<Vector>();
	File file;
	AppHandler handler;
	
	
	//xml파일 누를때마다 columnName을 새로 붙임
	public Model(File file) {
		this.file=file;
		
		for(int i=0; i<getColumnCount(); i++){
			columnName.addElement("이름");
			columnName.addElement("나이");
			columnName.addElement("연락처");
			columnName.addElement("성별");
		}
	}
	

	public int getRowCount() {
		return data.size();
	}

	public int getColumnCount() {
		return columnName.size();
	}
	
	public String getColumnName(int col) {
		return columnName.get(col);
	}
	
	public Object getValueAt(int row, int col) {
		return data.get(row).get(col);
	}

}
