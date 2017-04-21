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
	File file; //파싱 대상 xml 파일!!
	CarHandler carHandler;
	MyModel model;
	String tableName; //오라클 작업 테이블
	DBManager manager;
	Connection con;
	
	public AppMain() {
		setLayout(new GridLayout(1, 2));
		p_west=new JPanel();
		p_east=new JPanel();
		bt_xml=new JButton("XML 열기");
		bt_oracle=new JButton("Oracle에 저장");
		bt_json=new JButton("json으로 export");
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
				manager.disConnect(con); //db닫기
				System.exit(0); //프로세스 종료
			}
			
		});
		
		setSize(1000,550);
		setLocationRelativeTo(null);
		setVisible(true);
		//setDefaultCloseOperation(EXIT_ON_CLOSE); ->db를 안닫고 창만 닫으므로 쓰면 안됨
	}
	
	//xml 파일 열기
	public void open(){
		SAXParserFactory factory=SAXParserFactory.newInstance(); //싱글턴은 아닌걸로.
		try {
			SAXParser parser=factory.newSAXParser();
			parser.parse(file, carHandler=new CarHandler()); //파싱 들어가는 시점에 핸들러를 생성
			
			//JTable에 xml 파싱결과를 출력하기!!
			//hashmap과 vector 서로 변환이 가능
			Vector vec=new Vector(carHandler.cols.keySet());
			//System.out.println(vec.get(0));
			model=new MyModel(vec, carHandler.data);
			table.setModel(model);//테이블에 모델 적용!!
			table.updateUI();;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//오라클에 저장
	public void save(){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		StringBuffer sql=new StringBuffer();
		//
		tableName=FileUtil.getOnlyName(file.getName());  //테이블 이름 추출
		
		//존재 여부를 판단하고, 테이블 생성
		//일반 유저가 보유한 테이블 중 테이블 이름이 car인 것이 있는지 조회
		sql.append("select table_name from user_tables");
		sql.append(" where table_name=?");
		
		try {
			pstmt=con.prepareStatement(sql.toString());
			pstmt.setString(1, tableName.toUpperCase()); //첫번째 물음표는 테이블 이름으로 지정, 테이블명이 대문자로 됨
			rs=pstmt.executeQuery();
			
			//레코드가 있다면... (중복될 테이블이 존재)
			if(rs.next()){
				//이미 존재했던 테이블을 제거(drop)!
				sql.delete(0, sql.length()); //"select table_name from user_tables"를 지움. 스트링 버퍼는 계속 쌓이므로
				sql.append("drop table "+tableName); 
			//	System.out.println(sql.toString());
				pstmt=con.prepareStatement(sql.toString());
				pstmt.executeUpdate(); //DDL인 경우 반환값 없다!
				JOptionPane.showMessageDialog(this,"이미 존재하는 테이블 삭제");
			}
			
			//테이블 생성!!
			sql.delete(0, sql.length());
			sql.append("create table "+tableName+"(");
			sql.append(tableName+"_id number primary key");
			
			//Map이 보유한 컬럼 갯수만큼 반복문
			Set set=carHandler.cols.keySet(); 
			Iterator it=set.iterator(); //map은 순서 없으니까 있게 만들어줌
			
			while(it.hasNext()){ //있을 때까지
				//brand, price name값이 키로 존재
				String key=(String)it.next(); //key 추출 ->컬럼명
				
				//varchar2(20) , number..
				String value=carHandler.cols.get(key);
				
				sql.append(","+key+" "+value);
			}
			sql.append(")"); //String이니까 안에 ;찍지 않음
			
			//생성 쿼리 수행
			pstmt=con.prepareStatement(sql.toString());
			
			pstmt.executeUpdate();
			JOptionPane.showMessageDialog(this, tableName+"생성 완료!!");
			
			//시퀀스 생성!!
			sql.delete(0, sql.length());
			sql.append("select sequence_name from user_sequences");
			sql.append(" where sequence_name=?");
			pstmt=con.prepareStatement(sql.toString());
			pstmt.setString(1, ("seq_"+tableName).toUpperCase());
			rs=pstmt.executeQuery();

			if(!rs.next()){ //레코드가 없다면
				//시퀀스 생성
				sql.delete(0, sql.length());
				sql.append("create sequence seq_"+tableName);
				sql.append(" increment by 1 start with 1");
				pstmt=con.prepareStatement(sql.toString());
				pstmt.executeUpdate();
				JOptionPane.showMessageDialog(this, "시퀀스 생성");
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
			JOptionPane.showMessageDialog(this, "DB에 저장되었습니다");
			
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
	
	//json으로 export 하기
	public void export(){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		//오라클에 들어있는 테이블의 레코드를 JSON으로 표현해보기!!
		StringBuffer sql=new StringBuffer();
		
		sql.append("select * from "+tableName);
		try {
			pstmt=con.prepareStatement(sql.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY); //rs움직임 가능
			rs=pstmt.executeQuery();
			rs.last(); //커서를 제일 마지막 레코드로 보낸다 
			int total=rs.getRow(); //rs의 개수
			rs.beforeFirst(); //커서 다시 원상복귀
			
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
				//배열의 n-2까지 
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
