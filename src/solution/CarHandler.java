package solution;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pratice.Model;

//���ϸ��� ��Ī�ϴ� ���� ���� �������� ��� ����. �ϴ� car.xml�� ������θ� �Ľ�
public class CarHandler extends DefaultHandler{

	
		//���� �̺�Ʈ�� �߻���Ű�� ������� ��ġ�� �˱� ���� üũ ����
		boolean cars;
		boolean car;
		boolean brand;
		boolean name;
		boolean price;
		boolean color;
		
		
		/*
		 * �̺�Ʈ�� �߻��� ��, ������ ó���� �ؼ� ���������� xml�� �ؼ��� ����� ������ ������ �����Ϳ� ��Ƴ���!!(�÷��� �����ӿ� ����)
		 * */
		Map <String, String> cols; //<�÷�, �ڷ���>
		Vector <Vector> data;
		Vector vec; //VO, DTO ����
		/*
		public CarHandler(Model model){
			this.model=model;
		
		}
		*/
		
		//������ ��� �ᵵ ��.
		public void startDocument() throws SAXException {
			 data=new Vector<Vector>();
		}
		
		//<���� �±�>�� �߰ߵǸ� ȣ��Ǵ� �޼���
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			//�ν��Ͻ� �Ѱ��� ���� ���� �غ�����!!
			//DTO ���!!
			
			if(qName.equalsIgnoreCase("cars")){
				cars=true;
			}
			else if(qName.equalsIgnoreCase("car")){
				vec=new Vector();
				cols=new HashMap<String,String>();
				car=true;
			}
			else if(qName.equalsIgnoreCase("brand")){
				cols.put(qName,"varchar2(20)"); //qName�� ���Ϳ� ��´�
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
			
		
			//System.out.println(qName); //�±� �̸��� ���-> ���̺��� �÷����ӿ� �����ϴ� ����
		}


		//�ؽ�Ʈ�� �߰ߵǸ� ȣ��Ǵ� �޼���
		public void characters(char[] ch, int start, int length) throws SAXException {
			if(cars){		
				cars=false;
			}
			if(car){		//����ΰ� name�� �����ϸ�
				vec.add(new String(ch, start, length)); //ch�� ������� start���� length��ŭ
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
		
		//<�ݴ� �±�>�� �߰ߵǸ� ȣ��Ǵ� �޼��� 
		public void endElement(String uri, String localName, String qName) throws SAXException {
			//�� car���� ������ ���Ϳ� ����!!
			if(qName.equalsIgnoreCase("car")){
				data.add(vec);
			}
		}
		
		//������ ��� �ε����
		public void endDocument() throws SAXException {
			System.out.println("����� �� ������ "+data.size());
		}
	}
