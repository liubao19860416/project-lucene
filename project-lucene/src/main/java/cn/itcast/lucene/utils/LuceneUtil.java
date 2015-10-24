package cn.itcast.lucene.utils;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * Lucene配置文件读取及初始化工具类
 * 
 * @author Liubao
 * @2015年7月26日
 * 
 */
public class LuceneUtil {
    
    private LuceneUtil() {
        super();
    }

    private static Directory directory;
    private static Analyzer analyzer;
    private static IndexWriterConfig indexWriterConfig;
    
    static {
        try {
            //初始化目录信息
            directory = FSDirectory.open(new File(Constants.filePath));
            //初始化分词器
            analyzer = new IKAnalyzer();
            //初始化索引创建配置信息对象
            indexWriterConfig = new IndexWriterConfig(Version.LUCENE_44,analyzer);
            indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    public static Analyzer getAnalyzer() {
        return analyzer;
    }

    /**
     * 获取IndexWriter对象
     */
    public static IndexWriter getIndexWriter() {
        IndexWriter indexWriter=null;
      //初始化IndexWriter对象
        try {
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            System.out.println("索引indexWriter对象创建了...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexWriter;
    }

    /**
     * 获取IndexSearcher对象
     */
    public static IndexSearcher getIndexSearcher() {
        IndexSearcher indexSearcher=null;
        //初始化IndexSearcher对象
        try {
            IndexReader indexReader = DirectoryReader.open(directory);
            indexSearcher = new IndexSearcher(indexReader);
            System.out.println("索引indexSearcher对象创建了...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexSearcher;
    }

    /**
     * 关闭资源
     */
    public static void closeIndexWriter(IndexWriter indexWriter) {
        if (indexWriter != null) {
            try {
                indexWriter.close(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void closeIndexReader(IndexSearcher indexSearcher) {
        if (indexSearcher != null) {
            IndexReader indexReader = indexSearcher.getIndexReader();
            try {
                if(indexReader!=null){
                    indexReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    

}
