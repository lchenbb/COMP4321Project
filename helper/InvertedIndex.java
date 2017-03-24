package helper;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class InvertedIndex
{
    private RecordManager recman;
    private HTree hashtable;

    static class WordInfoInPage implements Serializable{
      private int frequency;
      private SortedSet<Integer> wordposition;  //No-duplicate allowed

      public WordInfoInPage(){
        frequency = 0;
        wordposition = new TreeSet<Integer>();
      }
      public WordInfoInPage(Set<Integer> wp){
        wordposition = new TreeSet<Integer>(wp);
        frequency = wordposition.size();
      }
      public void addByPosition(Set<Integer> newwp){
        wordposition.addAll(newwp);
        frequency = wordposition.size();
      }

    }

    public InvertedIndex(String recordmanager, String objectname) throws IOException
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
    }

    public void finalize() throws IOException
    {
        recman.commit();
        recman.close();
    }

    public void addEntry(Integer wordID, Integer pageID, Set<Integer> positions) throws IOException
    {
        @SuppressWarnings("unchecked")
        Map<Integer, WordInfoInPage> content = (Map<Integer, WordInfoInPage>)hashtable.get(wordID);

        if (content==null){
          content = new TreeMap<>();
        }

        WordInfoInPage wordinfo = (WordInfoInPage)content.get(pageID);
        if (wordinfo==null){
          wordinfo = new WordInfoInPage(positions);
        }
        else{
          wordinfo.addByPosition(positions);
        }
        content.put(pageID,wordinfo);
        hashtable.put(wordID,content);
    }
    public void delEntry(String word) throws IOException
    {
        hashtable.remove(word);
    }
    public void printAll() throws IOException
    {
        IDConvertTable table = new IDConvertTable("PageAndWordIDMapTable","PageAndWordIDMapTable");

        FastIterator iter = hashtable.keys();
        Integer wordid;
        while( (wordid=(Integer)iter.next()) != null ) {
          System.out.println(table.getWordByID(wordid));
          @SuppressWarnings("unchecked")
          Map<Integer, WordInfoInPage> content = (Map<Integer, WordInfoInPage>)hashtable.get(wordid);

          for (Integer pageid : content.keySet()) {
            System.out.print("PageID " + pageid);
            System.out.print("; Frequency " + content.get(pageid).frequency + "; Position: ");
            Iterator iterator = content.get(pageid).wordposition.iterator();
            while (iterator.hasNext()){
              System.out.print(iterator.next() + " ");
            }
            System.out.println("");
          }
          System.out.println("---------");
        }
    }

    // public static void main(String[] args)
    // {
    //     try
    //     {
    //       InvertedIndex index = new InvertedIndex("InvertedIndex","ht1");
    //
    //       // Set<Integer> p1 = new HashSet<Integer>(Arrays.asList(10, 20, 30));
    //       // Set<Integer> p2 = new HashSet<Integer>(Arrays.asList(1, 2, 3));
    //       // Set<Integer> p3 = new HashSet<Integer>(Arrays.asList(1, 2, 3, 7, 5));
    //       // index.addEntry(0, 2, p1);
    //       // index.addEntry(1, 2, p2);
    //       // index.addEntry(2, 1, p1);
    //       // index.addEntry(0, 23, p2);
    //       // index.addEntry(0, 101, p1);
    //       // index.addEntry(1, 1, p1);
    //       // index.addEntry(2, 1, p3);
    //       index.printAll();
    //       index.finalize();
    //     }
    //     catch(IOException ex)
    //     {
    //         System.err.println(ex.toString());
    //     }
    //
    // }

}
