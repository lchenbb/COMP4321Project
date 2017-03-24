package helper;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/* Drop a note here if need to add any function

*/

/* Interface
This object converts bidirectionally from ID to word, ID to pageURL.

Constructor (Load from databse)
IDConvertTable table = new IDConvertTable("recordmanager","objectname");

Add
AddEntry is private! Indeed no need to add. Call getIDByWord/getIDByURL will add new entry if not found

Access By ID: return null if empty
public String getWordByID(Integer id)
public String getURLByID(Integer id)

Access By Word/URL: If this word/url doesn't exist, will add it to the hashtable and return the new ID
public Integer getIDByWord(String word)
public Integer getIDByURL(String url)

Refer to main for a demo.
*/

public class IDConvertTable
{
    private RecordManager recman;
    private HTree hashtable;

    public IDConvertTable(String recordmanager, String objectname) throws IOException
    {
        recman = RecordManagerFactory.createRecordManager(recordmanager);
        long recid = recman.getNamedObject(objectname);

        if (recid != 0)
            hashtable = HTree.load(recman, recid);
        else
        {
            hashtable = HTree.createInstance(recman);
            recman.setNamedObject( objectname, hashtable.getRecid() );
        }
        BiMap<String, Integer> wordbiMap = HashBiMap.create();
        BiMap<String, Integer> pagebiMap = HashBiMap.create();
        hashtable.put("word",wordbiMap);
        hashtable.put("url",pagebiMap);
    }

    public void finalize() throws IOException
    {
        recman.commit();
        recman.close();
    }

    private Integer addEntry(String type, String value) throws IOException
    {
        @SuppressWarnings("unchecked")
        BiMap<String, Integer> table = (BiMap<String, Integer>)hashtable.get(type);

        Integer id = new Integer(table.size());
        table.put(value,id);
        hashtable.put(type,table);
        return id;
    }
    public void delEntry(String word) throws IOException
    {
        hashtable.remove(word);
    }

    public String getWordByID(Integer id) throws IOException{
        BiMap<String, Integer> table = (BiMap<String, Integer>)hashtable.get("word");
        return table.inverse().get(id);
    }

    public Integer getIDByWord(String word) throws IOException{
        BiMap<String, Integer> table = (BiMap<String, Integer>)hashtable.get("word");
        if (!table.containsKey(word)){
          return addEntry("word",word);
        }
        else{
          return table.get(word);
        }
    }
    public String getURLByID(Integer id) throws IOException{
        BiMap<String, Integer> table = (BiMap<String, Integer>)hashtable.get("url");
        return table.inverse().get(id);
    }

    public Integer getIDByURL(String url) throws IOException{
        BiMap<String, Integer> table = (BiMap<String, Integer>)hashtable.get("url");
        if (!table.containsKey(url)){
          return addEntry("url",url);
        }
        else{
          return table.get(url);
        }
    }

    // public static void main(String[] args)
    // {
    //     try
    //     {
    //         IDConvertTable index = new IDConvertTable("IDConvertTable","ht1");
    //         System.out.println(index.getWordByID(0));
    //         System.out.println(index.getIDByWord("word"));
    //         System.out.println(index.getIDByWord("word1"));
    //         System.out.println(index.getIDByWord("word2"));
    //         System.out.println(index.getIDByWord("word"));
    //         System.out.println(index.getIDByWord("word"));
    //         System.out.println(index.getWordByID(0));
    //         index.finalize();
    //     }
    //     catch(IOException ex)
    //     {
    //         System.err.println(ex.toString());
    //     }
    //
    // }

}
