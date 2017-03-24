import java.io.File;
import java.util.Scanner;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import java.util.StringTokenizer;
import java.net.URL;
import helper.*;
import org.htmlparser.beans.StringBean;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.beans.LinkBean;

class WordAndItsPosition{
	public String word;
	public int position;
	WordAndItsPosition(){
		word="";
		position=-1;
	}
}

public class Indexer{
	private HashSet stopwords;
	private Porter porter;

	public Indexer()
	{
		stopwords = new HashSet();
		porter = new Porter();
		try{
			loadStopWordsList("stopwords.txt");
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}

	// load the stop words from stopwords.txt
	// all of the words is in lower case
	private void loadStopWordsList(String file) throws FileNotFoundException
	{
		Scanner in = new Scanner(new File(file));
		while(in.hasNextLine()){
			String line = in.nextLine();
			line = line.toLowerCase();
			stopwords.add(line);
		}
		in.close();
	}

	// turn the word into stem
	private String turnWordIntoStem(String word)
	{
		return porter.stripAffixes(word);
	}

	private Boolean isStopWord(String word){
		return stopwords.contains(word);
	}

	// remove all stopwords, get the stem and its position
	public Vector<WordAndItsPosition> processContent(String content)
	{
		content = content.toLowerCase();
		int pos = 0;
		Vector<WordAndItsPosition> result = new Vector<WordAndItsPosition>();

		String[] sentences = content.split("[^A-Za-z0-9 ]");
		for(int i=0;i<sentences.length;++i){
			String sentence = sentences[i].trim();
			if(!sentence.equals("")){
				int num=0;

				StringTokenizer st = new StringTokenizer(sentence," ");
				while(st.hasMoreTokens()){
					String word = st.nextToken();
					if(!isStopWord(word)){
						String stem = turnWordIntoStem(word);
						WordAndItsPosition tuple = new WordAndItsPosition();
						tuple.word = stem;
						tuple.position = pos;
						result.add(tuple);
						num++;
						pos++;
					}
				}

				if(num!=0)pos++;
			}
		}

		return result;
	}

	public void index(String content)
	{
		try{
			String title;// title
			String body;// content in page body
			int indexOfFirstReturn = content.indexOf("\n");
			title = content.substring(0,indexOfFirstReturn);
			body = content.substring(indexOfFirstReturn);
			
			Vector<WordAndItsPosition> titleVec = processContent(title);
			Vector<WordAndItsPosition> bodyVec = processContent(body);

			System.out.println();
			System.out.println(title);
			for(int i=0;i<titleVec.size();++i){
				WordAndItsPosition temp = titleVec.get(i);
				System.out.println(temp.word+" "+temp.position);
			}




		}catch(Exception e){

		}
	}
}