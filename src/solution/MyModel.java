package solution;

import java.io.File;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

//CarHandler�� ������ ���͸� �Ѱܹ����� ��
public class MyModel extends AbstractTableModel{
	Vector <String>cols;
	Vector <Vector> data; //�Ѱܹ����Ƿ� ������ �ʿ� ����
	File file;
	CarHandler carHandler;
	
	//xml �Ľ��� ����� �Ʒ��� �μ��� �Ѱܹ���!!
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
