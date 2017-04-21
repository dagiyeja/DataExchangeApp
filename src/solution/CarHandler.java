package solution;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pratice.Model;

//파일마다 매칭하는 것은 추후 패턴으로 배울 예정. 일단 car.xml을 대상으로만 파싱
public class CarHandler extends DefaultHandler{

	
		//현재 이벤트를 발생시키는 실행부의 위치를 알기 위한 체크 변수
		boolean cars;
		boolean car;
		boolean brand;
		boolean name;
		boolean price;
		boolean color;
		
		
		/*
		 * 이벤트가 발생할 때, 적절한 처리를 해서 최종적으로 xml의 해석된 결과를 이차원 형태의 데이터에 담아놓자!!(컬렉션 프레임웍 강추)
		 * */
		Map <String, String> cols; //<컬럼, 자료형>
		Vector <Vector> data;
		Vector vec; //VO, DTO 역할
		/*
		public CarHandler(Model model){
			this.model=model;
		
		}
		*/
		
		//생성자 대신 써도 됨.
		public void startDocument() throws SAXException {
			 data=new Vector<Vector>();
		}
		
		//<시작 태그>가 발견되면 호출되는 메서드
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			//인스턴스 한개를 담을 벡터 준비하자!!
			//DTO 대용!!
			
			if(qName.equalsIgnoreCase("cars")){
				cars=true;
			}
			else if(qName.equalsIgnoreCase("car")){
				vec=new Vector();
				cols=new HashMap<String,String>();
				car=true;
			}
			else if(qName.equalsIgnoreCase("brand")){
				cols.put(qName,"varchar2(20)"); //qName을 벡터에 담는다
				brand=true;
			}
			else if(qName.equalsIgnoreCase("name")){
				cols.put(qName,"varchar2(30)");
				name=true;
			}
			else if(qName.equalsIgnoreCase("price")){
				cols.put(qName, "number");
				price=true;
			}
			else if(qName.equalsIgnoreCase("color")){
				cols.put(qName, "varchar2(20)");
				color=true;
			} 
			
		
			//System.out.println(qName); //태그 이름들 출력-> 테이블에는 컬럼네임에 대응하는 개념
		}


		//텍스트가 발견되면 호출되는 메서드
		public void characters(char[] ch, int start, int length) throws SAXException {
			if(cars){		
				cars=false;
			}
			if(car){		//실행부가 name에 도달하면
				vec.add(new String(ch, start, length)); //ch를 대상으로 start부터 length만큼
				car=false;
			}else if(brand){		
				vec.add(new String(ch, start, length));
				brand=false;
			}else if(name){		
				vec.add(new String(ch, start, length));
				name=false;
			}else if(price){		
				vec.add(new String(ch, start, length));
				price=false;
			}else if(color){		
				vec.add(new String(ch, start, length));
				color=false;
			}
			
		}
		
		//<닫는 태그>가 발견되면 호출되는 메서드 
		public void endElement(String uri, String localName, String qName) throws SAXException {
			//각 car마다 이차원 벡터에 담자!!
			if(qName.equalsIgnoreCase("car")){
				data.add(vec);
			}
		}
		
		//문서가 모두 로드된후
		public void endDocument() throws SAXException {
			System.out.println("담겨진 총 차수는 "+data.size());
		}
	}
