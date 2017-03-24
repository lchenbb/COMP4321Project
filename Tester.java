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

	public static void main (String[] args)
	{
		long begin = System.currentTimeMillis();
		String url;
		if(args.length==0){
			url = "http://www.cse.ust.hk";
		}else if(args.length==1){
			url = args[0];
		}else{
			System.out.println("Please enter a url for indexing, or using the default url defined in program");
			return;
		}
		try{
			String content = getPage(url);

			Indexer indexer = new Indexer();
			indexer.index(url,content);
		}catch(ParserException e){
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis()-begin);
	}
}