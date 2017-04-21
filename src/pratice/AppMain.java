package pratice;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class AppMain extends JFrame implements ActionListener{
	JPanel p_west, p_east;
	JButton bt_xml, bt_oracle, bt_json;
	JTable table;
	JTextArea area;
	JScrollPane sc_table, sc_area;
	JFileChooser chooser;
	DBManager manager=DBManager.getInstance();
	Model model;
	SAXParserFactory factory;
	SAXParser parser;
	AppHandler handler;
	File file;
	
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
		chooser=new JFileChooser("C:/java_workspace2/DataExchangeApp/data");
		
		
		area.setPreferredSize(new Dimension(450, 400));
		
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
		
		setSize(900,550);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//��ư�� ������ xml������ ���� �ִ� ���̾�α� â�� ����->�����ϸ� table�� ���
	public void loadXML(){
		int result=chooser.showOpenDialog(this);
		
		model=new Model(file);
		factory=SAXParserFactory.newInstance();
		if(result==JFileChooser.APPROVE_OPTION){
			file=chooser.getSelectedFile();
			try {
				parser=factory.newSAXParser();
				parser.parse(file, handler=new AppHandler(model));
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			table.setModel(model);
			table.updateUI();
		}
				
		
		
	}
	
	//���̺��� ����Ŭ�� ����->�̹� ���̺��� �����Ѵٸ� drop�� ����, �ٽ� ����
	public void loadOracle(){
		//PreparedStatement pstmt=null;
		//ResultSet rs=null;
		//String tableName=file.getName().split("\\")[0];
		//create table member(member_id,name varchar2(20), age number, phone varch2, gender char(3));
		//create sequence seq_member_id start with 1 increment by 1;
		
		
				
	}
	
	//xml ������ json���� ���
	public void exportJSON(){
		
	}

	public void actionPerformed(ActionEvent e) {
		Object obj=e.getSource();
		if(obj==bt_xml){
			
			
		}else if(obj==bt_oracle){
			
			
		}else if(obj==bt_json){
			
		}
	}
	
	public static void main(String[] args) {
		new AppMain();

	}


}
