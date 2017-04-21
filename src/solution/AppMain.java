package solution;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import util.file.FileUtil;

public class AppMain extends JFrame implements ActionListener{
	JPanel p_west, p_east;
	JButton bt_xml, bt_oracle, bt_json;
	JTable table;
	JTextArea area;
	JScrollPane sc_table, sc_area;
	File file; //�Ľ� ��� xml ����!!
	CarHandler carHandler;
	MyModel model;
	String tableName; //����Ŭ �۾� ���̺�
	DBManager manager;
	Connection con;
	
	public AppMain() {
		setLayout(new GridLayout(1, 2));
		p_west=new JPanel();
		p_east=new JPanel();
		bt_xml=new JButton("XML ����");
		bt_oracle=new JButton("Oracle�� ����");
		bt_json=new JButton("json���� export");
		table=new JTable();
		area=new JTextArea();
		sc_table=new JScrollPane(table);
		sc_area=new JScrollPane(area);
		file=new File("C:/java_workspace2/DataExchangeApp/data/car.xml");
		manager=DBManager.getInstance();
		con=manager.getConnection();
		
		area.setPreferredSize(new Dimension(500, 400));
		
		
		p_west.add(bt_xml);
		p_west.add(bt_oracle);
		p_west.add(sc_table);
		p_east.add(bt_json);
		p_east.add(sc_area);
		
		add(p_west);
		add(p_east);
		
		bt_xml.addActionListener(this);
		bt_oracle.addActionListener(this);
		bt_json.addActionListener(this);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				manager.disConnect(con); //db�ݱ�
				System.exit(0); //���μ��� ����
			}
			
		});
		
		setSize(1000,550);
		setLocationRelativeTo(null);
		setVisible(true);
		//setDefaultCloseOperation(EXIT_ON_CLOSE); ->db�� �ȴݰ� â�� �����Ƿ� ���� �ȵ�
	}
	
	//xml ���� ����
	public void open(){
		SAXParserFactory factory=SAXParserFactory.newInstance(); //�̱����� �ƴѰɷ�.
		try {
			SAXParser parser=factory.newSAXParser();
			parser.parse(file, carHandler=new CarHandler()); //�Ľ� ���� ������ �ڵ鷯�� ����
			
			//JTable�� xml �Ľ̰���� ����ϱ�!!
			//hashmap�� vector ���� ��ȯ�� ����
			Vector vec=new Vector(carHandler.cols.keySet());
			//System.out.println(vec.get(0));
			model=new MyModel(vec, carHandler.data);
			table.setModel(model);//���̺� �� ����!!
			table.updateUI();;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//����Ŭ�� ����
	public void save(){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		StringBuffer sql=new StringBuffer();
		//
		tableName=FileUtil.getOnlyName(file.getName());  //���̺� �̸� ����
		
		//���� ���θ� �Ǵ��ϰ�, ���̺� ����
		//�Ϲ� ������ ������ ���̺� �� ���̺� �̸��� car�� ���� �ִ��� ��ȸ
		sql.append("select table_name from user_tables");
		sql.append(" where table_name=?");
		
		try {
			pstmt=con.prepareStatement(sql.toString());
			pstmt.setString(1, tableName.toUpperCase()); //ù��° ����ǥ�� ���̺� �̸����� ����, ���̺���� �빮�ڷ� ��
			rs=pstmt.executeQuery();
			
			//���ڵ尡 �ִٸ�... (�ߺ��� ���̺��� ����)
			if(rs.next()){
				//�̹� �����ߴ� ���̺��� ����(drop)!
				sql.delete(0, sql.length()); //"select table_name from user_tables"�� ����. ��Ʈ�� ���۴� ��� ���̹Ƿ�
				sql.append("drop table "+tableName); 
			//	System.out.println(sql.toString());
				pstmt=con.prepareStatement(sql.toString());
				pstmt.executeUpdate(); //DDL�� ��� ��ȯ�� ����!
				JOptionPane.showMessageDialog(this,"�̹� �����ϴ� ���̺� ����");
			}
			
			//���̺� ����!!
			sql.delete(0, sql.length());
			sql.append("create table "+tableName+"(");
			sql.append(tableName+"_id number primary key");
			
			//Map�� ������ �÷� ������ŭ �ݺ���
			Set set=carHandler.cols.keySet(); 
			Iterator it=set.iterator(); //map�� ���� �����ϱ� �ְ� �������
			
			while(it.hasNext()){ //���� ������
				//brand, price name���� Ű�� ����
				String key=(String)it.next(); //key ���� ->�÷���
				
				//varchar2(20) , number..
				String value=carHandler.cols.get(key);
				
				sql.append(","+key+" "+value);
			}
			sql.append(")"); //String�̴ϱ� �ȿ� ;���� ����
			
			//���� ���� ����
			pstmt=con.prepareStatement(sql.toString());
			
			pstmt.executeUpdate();
			JOptionPane.showMessageDialog(this, tableName+"���� �Ϸ�!!");
			
			//������ ����!!
			sql.delete(0, sql.length());
			sql.append("select sequence_name from user_sequences");
			sql.append(" where sequence_name=?");
			pstmt=con.prepareStatement(sql.toString());
			pstmt.setString(1, ("seq_"+tableName).toUpperCase());
			rs=pstmt.executeQuery();

			if(!rs.next()){ //���ڵ尡 ���ٸ�
				//������ ����
				sql.delete(0, sql.length());
				sql.append("create sequence seq_"+tableName);
				sql.append(" increment by 1 start with 1");
				pstmt=con.prepareStatement(sql.toString());
				pstmt.executeUpdate();
				JOptionPane.showMessageDialog(this, "������ ����");
			}
			
			//insert!!
			sql.delete(0, sql.length());
			sql.append("insert into car(car_id, brand,name,price,color)");
			sql.append(" values(seq_car.nextval,?,?,?,?)");
			
			pstmt=con.prepareStatement(sql.toString());
			
			for(int a=0; a<table.getRowCount(); a++){
				for(int i=0; i<table.getColumnCount(); i++){
					String value=(String)table.getValueAt(0, i);
					pstmt.setString((i+1), value); //brand
				}
				pstmt.executeUpdate();
			}
			JOptionPane.showMessageDialog(this, "DB�� ����Ǿ����ϴ�");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	//json���� export �ϱ�
	public void export(){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		//����Ŭ�� ����ִ� ���̺��� ���ڵ带 JSON���� ǥ���غ���!!
		StringBuffer sql=new StringBuffer();
		
		sql.append("select * from "+tableName);
		try {
			pstmt=con.prepareStatement(sql.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY); //rs������ ����
			rs=pstmt.executeQuery();
			rs.last(); //Ŀ���� ���� ������ ���ڵ�� ������ 
			int total=rs.getRow(); //rs�� ����
			rs.beforeFirst(); //Ŀ�� �ٽ� ���󺹱�
			
			sql.delete(0, sql.length());
			sql.append("{");
			sql.append(" \"cars\":[ ");
			
			while(rs.next()){
				sql.append("{");
				sql.append("\"brand\":\""+rs.getString("brand")+"\",");
				sql.append("\"name\":\""+rs.getString("name")+"\",");
				sql.append("\"price\":"+rs.getInt("price")+",");
				sql.append("\"color\":\""+rs.getString("color")+"\",");
				
				System.out.println(rs.getString("color"));
				//�迭�� n-2���� 
				if(total-2>=0){
					sql.append("},");
				}else{
					sql.append("}");
				}
				total--;
			}
			
			sql.append("]");
			sql.append("}");
			area.setText(sql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	

	public void actionPerformed(ActionEvent e) {
		Object obj=e.getSource();
		if(obj==bt_xml){
			open();
			
		}else if(obj==bt_oracle){
			save();
			
		}else if(obj==bt_json){
			export();
		}
		
	}
	
	public static void main(String[] args) {
		new AppMain();

	}


}
