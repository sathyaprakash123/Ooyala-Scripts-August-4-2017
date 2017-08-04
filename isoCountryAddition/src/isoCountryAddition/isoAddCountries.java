package isoCountryAddition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class isoAddCountries {

	TreeMap<String, String> countryCode = new TreeMap();
	Element country1;

	public void csvtoHash(File csvfile) throws IOException {
		File csvFileValue = csvfile;
		String line = "";
		String cvsSplitBy = ";";

		BufferedReader br = new BufferedReader(new FileReader(csvFileValue));

		while ((line = br.readLine()) != null) {

			String[] country = line.split(cvsSplitBy);
			countryCode.put(country[0], country[1]);
		}

	}

	public void ReplaceLocationISO(File old_file_entry, File new_file_entry)
			throws ParserConfigurationException, SAXException, IOException, TransformerException

	{

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(old_file_entry);
		doc.getDocumentElement().normalize();

		NodeList country_node_parent = doc.getElementsByTagName("country");
		Element country_element = (Element) country_node_parent.item(0);
		NodeList country_children = country_element.getElementsByTagName("children");

		// Element childofcountry = (Element)
		// country_element.getElementsByTagName("children");
		
		

		for (int i = 0; i < country_children.getLength(); i++) {

			Node oldnode = country_children.item(i);
			oldnode.getParentNode().removeChild(oldnode);

		}

		Element sourcelement = doc.getDocumentElement();
		Element countries = (Element) sourcelement.getElementsByTagName("country").item(0);
		Element countrychil = doc.createElement("children");
		int id = 1001;

		Iterator it = countryCode.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry pair = (Map.Entry) it.next();
			id = id + 1;
			country1 = doc.createElement("option-child");
			country1.setAttribute("id", Integer.toString(id));
			country1.setAttribute("name", pair.getValue().toString());
			country1.setAttribute("display_name", pair.getKey().toString());
			country1.setAttribute("commentable", "false");
			country1.setAttribute("default", "false");
			country1.setAttribute("value", pair.getValue().toString());
			countrychil.appendChild(country1);

		}

		
		countries.appendChild(countrychil);
		


		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new_file_entry);
		transformer.transform(source, result);

		// For console Output.
		StreamResult consoleResult = new StreamResult(System.out);
		transformer.transform(source, consoleResult);

	}

	public static void main(String args[])
			throws IOException, ParserConfigurationException, SAXException, TransformerException {

		String new_file_location = "/Users/sathya/git/Zoomin_Country_data/new_files/new_flex-metadatadefinition-raw.xml";
		//String new_file_location = args[2];
		File new_file = new File(new_file_location);

		String xml_file_location = "/Users/sathya/git/Zoomin_Country_data/old_files/flex-metadatadefinition-raw.xml";
		//String xml_file_location = args[0];
		File file1_metadata = new File(xml_file_location);

		String csv_file_location = "/Users/sathya/Desktop/Countries-ISO-3166-1-alpha-2-Flex.csv";
		//String csv_file_location = args[1];
		File iso_csv_file = new File(csv_file_location);

		isoAddCountries iso = new isoAddCountries();
		iso.csvtoHash(iso_csv_file);

		InputStream is = new FileInputStream(xml_file_location);
		OutputStream os = new FileOutputStream(new_file_location);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}

		iso.ReplaceLocationISO(file1_metadata, new_file);

	}

}
