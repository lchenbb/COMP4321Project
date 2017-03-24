import java.io.File;
import java.util.Scanner;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import org.htmlparser.beans.StringBean;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import java.util.StringTokenizer;
import org.htmlparser.beans.LinkBean;
import java.net.URL;
import helper.*;

public class Indexer{
	private HashSet stopwords;
	private String SEPERATOR = "|";
	private Porter porter;
	public Indexer(){
		stopwords = new HashSet();
		porter = new Porter();
		try{
			loadStopWordsList("stopwords.txt");
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}

	// get the page content from ur;
	public String getPage(String _url) throws ParserException
	{
		StringBean bean = new StringBean();
		bean.setURL(_url);
		bean.setLinks(false);
		return bean.getStrings();
	}
	
	// load the stop words from stopwords.txt
	// all of the words is in lower case
	public void loadStopWordsList(String file) throws FileNotFoundException
	{
		Scanner in = new Scanner(new File(file));
		while(in.hasNextLine()){
			String line = in.nextLine();
			line = line.toLowerCase();
			stopwords.add(line);
		}
		in.close();
	}

	// replace all characters which are NOT alphabet or numbers with whitespace
	// and turn them into lower case
	public String preprocssingString(String word){
		word = word.replaceAll("[^A-Za-z0-9]"," ");
		word = word.toLowerCase();
		return word;
	}

	// remove all stopwords from string
	public String removeStopWords(String content)
	{
		String result = "";
		StringTokenizer st = new StringTokenizer(content," ");
		while(st.hasMoreTokens()){
			String word = st.nextToken();
			if(!stopwords.contains(word)){
				result += word+SEPERATOR;
			}
		}
		return result;
	}

	public void index(String url)
	{
		try{
			String page = getPage(url);
			String title;// title
			String bodyString;// content in page body
			int indexOfFirstReturn = page.indexOf("\n");
			title = page.substring(0,indexOfFirstReturn);
			bodyString = page.substring(indexOfFirstReturn);

			title = preprocssingString(title);
			bodyString = preprocssingString(bodyString);
			title = removeStopWords(title);
			bodyString = removeStopWords(bodyString);
			//System.out.println("title: "+title+"\n");
			//System.out.println("body:  "+bodyString+"\n");
			Vector<String> stemInTitle = new Vector<String>();
			Vector<String> stemInBody = new Vector<String>();

			StringTokenizer st1 = new StringTokenizer(title,SEPERATOR);
			while(st1.hasMoreTokens()){
				String word = st1.nextToken();
				stemInTitle.add(porter.stripAffixes(word));
			}
			StringTokenizer st2 = new StringTokenizer(bodyString,SEPERATOR);
			while(st2.hasMoreTokens()){
				String word = st2.nextToken();
				stemInBody.add(porter.stripAffixes(word));
			}

			for(int i=0;i<stemInTitle.size();++i){
				System.out.print(stemInTitle.get(i)+SEPERATOR);
			}
			System.out.println();
			System.out.println();
			for(int i=0;i<stemInBody.size();++i){
				System.out.print(stemInBody.get(i)+SEPERATOR);
			}
			System.out.println();

			InvertedIndex into = new InvertedIndex("a","B");
			into.finalize();


		}catch(ParserException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void main (String[] args)
	{
		String url;
		if(args.length==0){
			url = "http://www.cse.ust.hk";
		}else if(args.length==1){
			url = args[0];
		}else{
			System.out.println("Please enter a url for indexing, or using the default url defined in program");
			return;
		}
		Indexer indexer = new Indexer();
		indexer.index(url);
	}

	public void printAll(String title,Vector<String> bodyStrings){
		System.out.println(title);
		for(int i=0;i<bodyStrings.size();++i){
			System.out.println(bodyStrings.get(i));
		}
		/*
			Enumeration keys = stopwords.keys();
			while(keys.hasMoreElements()){
				String key = (String) keys.nextElement();
				System.out.println(key+":666");
			}*/
	}
}