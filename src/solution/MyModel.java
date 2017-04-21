package solution;

import java.io.File;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

//CarHandler의 이차원 벡터를 넘겨받으면 됨
public class MyModel extends AbstractTableModel{
	Vector <String>cols;
	Vector <Vector> data; //넘겨받으므로 생성할 필요 없다
	File file;
	CarHandler carHandler;
	
	//xml 파싱한 결과를 아래의 인수로 넘겨받자!!
	public MyModel(Vector cols,Vector data) {
		this.data=data;
		this.cols=cols;
		
	}
	

	public int getRowCount() {
		return data.size();
	}

	public int getColumnCount() {
		return cols.size();
	}
	
	public String getColumnName(int col) {
		return cols.get(col);
	}
	
	public Object getValueAt(int row, int col) {
		return data.get(row).get(col);
	}

}
