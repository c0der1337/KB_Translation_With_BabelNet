import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.mit.jwi.item.IPointer;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelPointer;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSenseSource;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetSource;
import it.uniroma1.lcl.jlt.util.Language;

public class Test {
	private static HashMap<Integer, String> entitiesNumWord = new HashMap<Integer, String>();
	private static HashMap<String, Integer> entitiesWordNum = new HashMap<String, Integer>();
	private static int numOfentities = 0;
	static String output; // Wordpair to print in txt
	static String trans="";  // Translation

	public static void main(String[] args) throws IOException {

		loadEntitiesFromSocherFile("C://Users//Patrick//Documents//master arbeit//original_code//data//Wordnet//entities.txt");

		BabelNet bn = BabelNet.getInstance();
		
		PrintWriter writer = new PrintWriter("C://Users//Patrick//Documents//master arbeit//original_code//data//translated_entities.txt", "UTF-8");
		
		//Get a WordNet DE Translation for each entity
		for (int i = 0; i < entitiesNumWord.size(); i++) {
			String entity_name = getEntitiesClearName(i); 
			//System.out.println("entity_name: "+entity_name);
			List<BabelSynset> byl = bn.getSynsets(Language.EN, entity_name);
			//trans = getWordnetDETranslation(byl);
			output= entitiesNumWord.get(i) +"|"+ getWordnetDETranslation(byl);
			writer.println(output);
			//trans="";
		}

		writer.close();
		
	}
	
	public static void loadEntitiesFromSocherFile(String path) throws IOException{
		FileReader fr = new FileReader(path);
	    BufferedReader br = new BufferedReader(fr);
	    String line = br.readLine();
	    int entities_counter = 0;
	    while (line != null) {
	    	if (entities_counter < 10) {
				System.out.println("line: "+line);
			}
	    	entitiesNumWord.put(entities_counter, line);
	    	entitiesWordNum.put(line,entities_counter);
	    	line = br.readLine();
	    	entities_counter++;
		}   
	    br.close();
	    //number of entities need increased by one to handle the zero entry
	    numOfentities = entities_counter;
	    System.out.println(numOfentities + " Entities loaded");
	}
	
	/**
	 * Transforms __ENTITY_NAME_1 to ENTITY_NAME
	 * @param entityNumber: index number of the word
	 * @return entityName in the form that is usable for a BabelNet query
	 */
	public static String getEntitiesClearName(int entityNumber){
		try {
			return  entitiesNumWord.get(entityNumber).substring(2, entitiesNumWord.get(entityNumber).lastIndexOf("_"));
		} catch (Exception e) {
			return entitiesNumWord.get(entityNumber).substring(2);			
		}
	}
	
	public static String getWordnetDETranslation(List<BabelSynset> byl){
		boolean translated =false;
		// For each entity there are several Synsets
		for (int j = 0; j < byl.size(); j++) {
			// Iterating over the Senses and try to find a Wordnet Translation to German
			for (int j2 = 0; j2 < byl.get(j).getSenses(Language.DE).size(); j2++) {
				try {
					BabelSenseSource SenseSource = byl.get(j).getSenses(Language.DE).get(j2).getSource();
					if (SenseSource.equals(BabelSenseSource.WNTR) & translated==false ) {
						// Catch the first translation
						trans= byl.get(j).getSenses(Language.DE).get(j2).getSenseString();
						//System.out.println("translation: " + byl.get(j).getSenses(Language.DE).get(j2).getSenseString());
						//System.out.println("entity_name: "+entity_name+ " = "+trans);
						translated=true;
					}
					//System.out.println("getTranslations(): "+byl.get(j).getSenses(Language.DE).get(j2).getSenseString());
				} catch (Exception e) {
					System.out.println("No_WNTR_FOUND!");
					trans = "NO_WNTR_DE_FOUND";
					translated=true;
				}
			}			
		}
		if (translated == false) {
			trans = "NO_WNTR_DE_FOUND";
		}
		return trans;
	}

}
