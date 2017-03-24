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

class Tester{
	// get the page content from ur;
	public static String getPage(String _url) throws ParserException
	{
		StringBean bean = new StringBean();
		bean.setURL(_url);
		bean.setLinks(false);
		return bean.getStrings();
	}

	static String[] urls = {
		"https://course.cse.ust.hk/comp4321/labs/TestPages/testpage.htm",
		"https://course.cse.ust.hk/comp4321/labs/TestPages/ust_cse.htm",
		"https://course.cse.ust.hk/comp4321/labs/TestPages/Movie/1.html",
		"https://course.cse.ust.hk/comp4321/labs/TestPages/Movie/4.html",
		"http://www.cse.ust.hk",
		"http://www.ust.hk"
	};

	public static void main (String[] args)
	{
		try{
			long begin = System.currentTimeMillis();

			long time = System.currentTimeMillis();

			String url;

			begin = System.currentTimeMillis();
			Indexer indexer = new Indexer();
			System.out.println("indexer construction time: "+(System.currentTimeMillis()-begin));

			for(int i=0;i<urls.length;++i){
				begin = System.currentTimeMillis();
				url = urls[i];
				String content = getPage(url);
				System.out.println("get page time: "+(System.currentTimeMillis()-begin));

				begin = System.currentTimeMillis();
				indexer.index(url,content);
				System.out.println("index time: "+(System.currentTimeMillis()-begin));
			}

			begin = System.currentTimeMillis();
			indexer.writeIndexesIntoFile();
			System.out.println("write data time: "+(System.currentTimeMillis()-begin));

			System.out.println("total time: "+(System.currentTimeMillis()-time));
		}
		catch(ParserException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
