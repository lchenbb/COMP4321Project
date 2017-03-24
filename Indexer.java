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
	private InvertedIndex titleInvertedIndex;
	private InvertedIndex bodyInvertedIndex;

	private Hashtable< String, Set<Integer> > hash;

	private String titleInvertedIndexName = "TitleInvertedIndex";
	private String bodyInvertedIndexName = "BodyInvertedIndex";

	public Indexer()
	{
		stopwords = new HashSet();
		porter = new Porter();
		hash = new Hashtable< String, Set<Integer> >();
		try{
			titleInvertedIndex = new InvertedIndex(titleInvertedIndexName, titleInvertedIndexName);
			bodyInvertedIndex = new InvertedIndex(bodyInvertedIndexName, bodyInvertedIndexName);
		}catch(IOException e){
			e.printStackTrace();
		}
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
			line = line.trim();
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
	private Vector<WordAndItsPosition> processContent(String content)
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
					String word = st.nextToken().trim();
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

	private Integer pageToPageID(String page){
		return 0;
	}

	private void insertIndexIntoFile(Vector<WordAndItsPosition> vec, InvertedIndex invertedIndex, String page)
	throws IOException
	{
		Enumeration<String> words;

		for(int i=0;i<vec.size();++i){
			WordAndItsPosition temp = vec.get(i);
			Set<Integer> set = hash.get(temp.word);
			if(set!=null){
				set.add(temp.position);
				hash.put(temp.word,set);
			}else{
				set = new HashSet();
				set.add(temp.position);
				hash.put(temp.word, set);
			}
		}
		words = hash.keys();
		while(words.hasMoreElements()){
			String word = words.nextElement();
			invertedIndex.addEntry(word,pageToPageID(page),hash.get(word));
		}
		//invertedIndex.printAll();
		invertedIndex.finalize();
		hash.clear();
	}

	public void index(String url, String content)
	{
		try{
			long begin = System.currentTimeMillis();

			String title;// title
			String body;// content in page body
			int indexOfFirstReturn = content.indexOf("\n");
			title = content.substring(0,indexOfFirstReturn);
			body = content.substring(indexOfFirstReturn);

			System.out.println("seperate time: "+(System.currentTimeMillis()-begin));
			begin = System.currentTimeMillis();
			
			Vector<WordAndItsPosition> titleVec = processContent(title);
			Vector<WordAndItsPosition> bodyVec = processContent(body);

			System.out.println("process time: "+(System.currentTimeMillis()-begin));
			begin = System.currentTimeMillis();

			insertIndexIntoFile(titleVec, titleInvertedIndex, url);
			insertIndexIntoFile(bodyVec, bodyInvertedIndex, url);

			System.out.println("insertion time: "+(System.currentTimeMillis()-begin));
			

		}catch(Exception e){

		}
	}
}