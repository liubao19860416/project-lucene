//package cn.test;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import org.apache.log4j.Logger;
//import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.document.Field.Index;
//import org.apache.lucene.document.Field.Store;
//import org.apache.lucene.index.CorruptIndexException;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.index.IndexWriterConfig;
//import org.apache.lucene.index.LogByteSizeMergePolicy;
//import org.apache.lucene.index.LogMergePolicy;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.search.BooleanClause.Occur;
//import org.apache.lucene.search.BooleanQuery;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TopScoreDocCollector;
//import org.apache.lucene.search.highlight.Highlighter;
//import org.apache.lucene.search.highlight.QueryScorer;
//import org.apache.lucene.search.highlight.SimpleFragmenter;
//import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
//import org.apache.lucene.search.highlight.TokenSources;
//import org.apache.lucene.search.similarities.Similarity;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.store.LockObtainFailedException;
//import org.apache.lucene.util.Version;
//import org.springframework.ui.Model;
//import org.wltea.analyzer.lucene.IKQueryParser;
//import org.wltea.analyzer.lucene.IKSimilarity;
//
//import cn.itcast.lucene.utils.Constants;
//
///**
// * lucene工具类
// * 
// * @author Administrator
// * 
// */
//public class LuceneUtil {
//	/**
//	 * 日志
//	 */
//	static Logger logger = Logger.getLogger(LuceneUtil.class);
//	
//	public static Integer totalNum=0;
//	
//
//	/**
//	 * 创建索引
//	 * @param data 要放入索引的一条记录
//	 * @return
//	 */
//	public static synchronized boolean createIndex(LuceneData data) {
//		IndexWriter indexWriter = null;
//		Directory d = null;
//		try {
//			d = FSDirectory.open(new File(Constants.filePath));
//			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_44,
//			        cn.itcast.lucene.utils.LuceneUtil.getAnalyzer());
//			// 3.6以后不推荐用optimize,使用LogMergePolicy优化策略
//			conf.setMergePolicy(optimizeIndex());
//			// 创建索引模式：CREATE，覆盖模式； APPEND，追加模式
//			File file = new File(Constants.filePath);
//			File[] f = file .listFiles();
//			if(f.length==0)    
//				conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
//			else
//				conf.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
//
//			indexWriter = new IndexWriter(d, conf);
//			//因为id是唯一的，如果之前存在就先删除原来的，在创建新的
//			Term term = new Term("id", data.getId());
//			indexWriter.deleteDocuments(term);
//			
//			Document doc = getDocument(data);
//			indexWriter.addDocument(doc);
//
//			logger.debug("索引结束,共有索引{}个" + indexWriter.numDocs());
//			//System.out.println("索引结束,共有索引{}个" + indexWriter.numDocs()+":"+doc.get("id")+":"+doc.get("author"));
//			// 自动优化合并索引文件,3.6以后不推荐用optimize,使用LogMergePolicy优化策略
//			// indexWriter.optimize();
//			indexWriter.commit();
//			return true;
//		} catch (CorruptIndexException e) {
//			e.printStackTrace();
//			logger.error("索引添加异常", e);
//		} catch (LockObtainFailedException e) {
//			e.printStackTrace();
//			logger.error("索引添加异常", e);
//		} catch (IOException e) {
//			e.printStackTrace();
//			logger.error("索引不存在", e);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("索引添加异常", e);
//		} finally {
//			if (indexWriter != null) {
//				try {
//					indexWriter.close();
//				} catch (CorruptIndexException e) {
//					e.printStackTrace();
//					logger.error("索引关闭异常", e);
//				} catch (IOException e) {
//					e.printStackTrace();
//					logger.error("索引关闭异常", e);
//				} finally {
//					try {
//						if (d != null && IndexWriter.isLocked(d)) {
//							IndexWriter.unlock(d);
//						}
//					} catch (IOException e) {
//						e.printStackTrace();
//						logger.error("解锁异常", e);
//					}
//				}
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * 更新索引
//	 * 
//	 * @param data
//	 * @return
//	 */
//	public static boolean updateIndex(LuceneData data) {
//		IndexWriter indexWriter = null;
//		Directory d = null;
//		try {
//			d = FSDirectory.open(new File(Constants.filePath));
//			while (d != null && IndexWriter.isLocked(d)) {// 如果文件锁住,等待解锁
//				Thread.sleep(1000);
//				logger.error("索引已经锁住，正在等待....");
//			}
//			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_44,
//			        cn.itcast.lucene.utils.LuceneUtil.getAnalyzer());
//			// 3.6以后不推荐用optimize,使用LogMergePolicy优化策略
//			conf.setMergePolicy(optimizeIndex());
//
//			indexWriter = new IndexWriter(d, conf);
//			Term term = new Term("id", data.getId());
//			// 不管更新与否，先删除原来的
//			indexWriter.deleteDocuments(term);
//
//			Document doc = getDocument(data);
//			indexWriter.addDocument(doc);
//			// indexWriter.optimize();
//
//			indexWriter.commit();
//			logger.debug("更新索引，文章ID为{}" + data.getId());
//			logger.debug("共有索引{}个" + indexWriter.numDocs());
//			return true;
//		} catch (CorruptIndexException e) {
//			e.printStackTrace();
//			logger.error("索引添加异常", e);
//		} catch (LockObtainFailedException e) {
//			e.printStackTrace();
//			logger.error("索引添加异常", e);
//		} catch (IOException e) {
//			e.printStackTrace();
//			logger.error("索引不存在", e);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("索引添加异常", e);
//		} finally {
//			if (indexWriter != null) {
//				try {
//					indexWriter.close();
//				} catch (CorruptIndexException e) {
//					e.printStackTrace();
//					logger.error("索引关闭异常", e);
//				} catch (IOException e) {
//					e.printStackTrace();
//					logger.error("索引关闭异常", e);
//				} finally {
//					try {
//						if (d != null && IndexWriter.isLocked(d)) {
//							IndexWriter.unlock(d);
//						}
//					} catch (IOException e) {
//						e.printStackTrace();
//						logger.error("解锁异常", e);
//					}
//				}
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * 根据id删除索引（id对应的那条document）
//	 * 
//	 * @param id
//	 *            document的id
//	 * @return
//	 */
//	public static boolean deleteIndex(String id) {
//		IndexWriter indexWriter = null;
//		Directory d = null;
//		try {
//			d = FSDirectory.open(new File(Constants.filePath));
//			while (d != null && IndexWriter.isLocked(d)) {// 如果文件锁住,等待解锁
//				Thread.sleep(1000);
//				logger.error("索引已经锁住，正在等待....");
//			}
//
////			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
////					Version.LUCENE_36, AnalyzerUtil.getIkAnalyzer());
//			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
//					Version.LUCENE_44, cn.itcast.lucene.utils.LuceneUtil.getAnalyzer());
//			indexWriter = new IndexWriter(d, indexWriterConfig);
//			Term term = new Term("id", id);
//			indexWriter.deleteDocuments(term);
////			indexWriter.optimize();
//			indexWriter.commit();
//			logger.debug("删除文章ID:{}的索引..." + id);
//			logger.debug("共有索引{}个" + indexWriter.numDocs());
//			indexWriter.close();
//			return true;
//		} catch (CorruptIndexException e) {
//			e.printStackTrace();
//			logger.error("索引删除异常", e);
//		} catch (LockObtainFailedException e) {
//			e.printStackTrace();
//			logger.error("索引删除异常", e);
//		} catch (IOException e) {
//			e.printStackTrace();
//			logger.error("索引不存在", e);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("索引删除异常", e);
//		} finally {
//			if (indexWriter != null) {
//				try {
//					indexWriter.close();
//				} catch (CorruptIndexException e) {
//					e.printStackTrace();
//					logger.error("索引关闭异常", e);
//				} catch (IOException e) {
//					e.printStackTrace();
//					logger.error("索引关闭异常", e);
//				} finally {
//					try {
//						if (d != null && IndexWriter.isLocked(d)) {
//							IndexWriter.unlock(d);
//						}
//					} catch (IOException e) {
//						e.printStackTrace();
//						logger.error("解锁异常", e);
//					}
//				}
//			}
//		}
//		return false;
//	}
//	
//	/**
//	 * @param fileds 要查询的综合字段 ex【 new String[]{ "contentTitle", "contentContext","keywords"};】
//	 * @param occurs 要查询的字段出现可能 ex【new Occur[] { Occur.SHOULD, Occur.SHOULD,Occur.SHOULD };】
//	 * @param keyWord 要查询的关键字
//	 * @param page 当前页
//	 * @param pageSize 分页数
//	 * @return
//	 */
//	public static ArrayList<LuceneData> search(String[] fileds, Occur[] occurs,String keyWord,Integer page,Integer pageSize) {
//		return search(fileds, occurs, keyWord,"","", page, pageSize);
//	}
//
//	/**
//	 * @param fileds 要查询的综合字段 ex【 new String[]{ "contentTitle", "contentContext","keywords"};】
//	 * @param occurs 要查询的字段出现可能 ex【new Occur[] { Occur.SHOULD, Occur.SHOULD,Occur.SHOULD };】
//	 * @param keyWord 要查询的关键字
//	 * @param subType 主类型
//	 * @param type 主类型下的子类型
//	 * @param page  当前页
//	 * @param pageSize 分页数
//	 * @return
//	 */
//	public static ArrayList<LuceneData> search(String[] fileds, Occur[] occurs,String keyWord,String bigtype,String subType,Integer page,Integer pageSize) {
//		try {
//			// ---------初始化---------------------------------------------------
////			IndexReader reader = IndexReader.open(FSDirectory.open(new File(Constants.filePath)));
////			IndexSearcher searcher = new IndexSearcher(reader);
//		    // 在索引器中使用IKSimilarity相似度评估器
////			searcher.setSimilarity(new IKSimilarity());
//		    
//			IndexSearcher indexSearcher = cn.itcast.lucene.utils.LuceneUtil.getIndexSearcher();
//            indexSearcher.setSimilarity((Similarity)new IKSimilarity());
//
//			// ----------设置过滤器------------------------------------------------
//			BooleanQuery booleanquery = new BooleanQuery();
//			// 综合查询   （查询条件1）
//			Query likequery = IKQueryParser.parseMultiField(fileds, keyWord,occurs);
//			booleanquery.add(likequery, Occur.MUST);
//			
//			//主类型过滤 （查询条件2）
//			if(bigtype.length()>0)
//			{
//				Query subquery = IKQueryParser.parse("bigtype", bigtype);
//				booleanquery.add(subquery, Occur.MUST);
//			}
//			//从类型过滤 （查询条件3）
//			if(subType.length()>0)
//			{
//				Query subquery = IKQueryParser.parse("type", subType);
//				booleanquery.add(subquery, Occur.MUST);
//			}
//			
//			//过滤数字区间
//			//NumericRangeQuery<Integer> spanquery = NumericRangeQuery.newIntRange("id", begin, end, true, true);
//			//booleanquery.add(spanquery, Occur.MUST);
//			
//			//过滤时间区间(时间的getTime比大小)
//			//NumericRangeQuery<Integer> spanquery = NumericRangeQuery.newLongRange("id", begin, end, true, true);
//			//booleanquery.add(spanquery, Occur.MUST);
//			
//			//-------------过滤filter--------------------------------------------------
//			
//			//-------------设置权值（其中一个方法在doc创建Field时field.setBoost）--------------------
//			
//			//-------------排序--------------------------------------------------------
//			/*多字段排序，设置在前面的会优先排序 //true:降序 false:升序
//			 * SortField[] sortFields = new SortField[3];
//			 * SortField top = new SortField("isTop", SortField.INT, true);
//			 * SortField hits = new SortField("contentHits", SortField.INT,true); 
//			 * SortField pubtime = new SortField("publishTime",SortField.LONG, true); 
//			 * sortFields[0] = top; 
//			 * sortFields[1] = hits;
//			 * sortFields[2] = pubtime; 
//			 * Sort sort = new Sort(sortFields);
//			 */
//			
//			//-------------搜索--------------------------------------------------------
//			//分页查询,lucene不支持分页查询，因为查询速度很快，所以我们就设置查询上限
//			TopScoreDocCollector topCollector = TopScoreDocCollector.create(page*pageSize, false);//上限
//			searcher.search(booleanquery, topCollector);
//			//查询结果的总数量
//			totalNum=topCollector.getTotalHits();
//			ScoreDoc[] docs = topCollector.topDocs((page - 1) * pageSize, pageSize).scoreDocs;//返回所需数据
//			
//			//高亮显示
//			SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
//			Highlighter highlighter = new Highlighter(simpleHtmlFormatter, new QueryScorer(booleanquery));
//			highlighter.setTextFragmenter(new SimpleFragmenter(100));
//			
//			ArrayList<LuceneData> list = new ArrayList<LuceneData>();
//			LuceneData data=null;
//			for (ScoreDoc scdoc : docs) {
//				Document document = searcher.doc(scdoc.doc);
//				data=new LuceneData();
//				//设置高壳
//				TokenStream tokenStream=null;
//				String name = document.get("name");
//				tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), scdoc.doc, "name", AnalyzerUtil.getIkAnalyzer());
//				name = highlighter.getBestFragment(tokenStream, name);
//				if(name==null)
//					name=document.get("name");
//				
//				String author = document.get("author");
//				tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), scdoc.doc, "author", AnalyzerUtil.getIkAnalyzer());
//				author = highlighter.getBestFragment(tokenStream, author);			
//				if(author==null)
//					author=document.get("author");
//				
//				String outline = document.get("outline");
//				tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), scdoc.doc, "outline", AnalyzerUtil.getIkAnalyzer());
//				outline = highlighter.getBestFragment(tokenStream, outline);				
//				if(outline==null)
//					outline=document.get("outline");
//				
//				String type = document.get("type");
//				tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), scdoc.doc, "type", AnalyzerUtil.getIkAnalyzer());
//				type = highlighter.getBestFragment(tokenStream, type);		
//				if(type==null)
//					type=document.get("type");
//
//				data.setId(document.get("id"));
//				data.setName(name);
//				data.setAuthor(author);
//				data.setOutline(outline);
//				data.setType(type);
//				data.setTypeid(document.get("typeid")) ;
//				data.setBigtype(document.get("bigtype"));
//				data.setUpdateTime(document.get("updateTime"));
//				data.setImgPath(document.get("imgPath"));
//				data.setImgUrlPath(document.get("imgUrlPath"));
//				data.setContent(document.get("content"));
//				data.setLink_url(document.get("link_url"));
//				data.setHot(Long.parseLong(document.get("hot")));
//				data.setClickPoint(Long.parseLong(document.get("clickPoint")));
//				
//				list.add(data);
//			}
//			return list;
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("搜索异常", e);
//			return new ArrayList<LuceneData>();
//		}
//	}
//
//	/**
//	 * 把传入的数据类型转换成Document
//	 * 
//	 * @param data
//	 * @return
//	 */
//	private static Document getDocument(LuceneData data) {
//		Document doc = new Document();
//		doc.add(new Field("id", data.getId(), Store.YES, Index.NOT_ANALYZED));
//		doc.add(new Field("name", data.getName(), Store.YES, Index.ANALYZED));
//		doc.add(new Field("author", data.getAuthor(), Store.YES,Index.ANALYZED));
//		doc.add(new Field("outline", data.getOutline(), Store.YES,Index.ANALYZED));
//		doc.add(new Field("type", data.getType(), Store.YES, Index.ANALYZED));
//		doc.add(new Field("updateTime", data.getUpdateTime(), Store.YES,Index.NOT_ANALYZED));
//		doc.add(new Field("imgPath", data.getImgPath(), Store.YES,Index.NOT_ANALYZED));
//		doc.add(new Field("imgUrlPath", data.getImgUrlPath()==null?"":data.getImgUrlPath(), Store.YES,Index.NOT_ANALYZED));
//		doc.add(new Field("content", data.getContent()==null?"":data.getContent(), Store.YES,Index.ANALYZED));
//		doc.add(new Field("link_url", data.getLink_url(), Store.YES,Index.NOT_ANALYZED));
//
//		doc.add(new Field("hot", Long.toString(data.getHot()), Store.YES,Index.NOT_ANALYZED));
//		doc.add(new Field("clickPoint", Long.toString(data.getClickPoint()),Store.YES, Index.NOT_ANALYZED));
//		
//		doc.add(new Field("bigtype", data.getBigtype(), Store.YES,Index.NOT_ANALYZED));
//		doc.add(new Field("typeid", data.getTypeid(), Store.YES,Index.NOT_ANALYZED));
//		return doc;
//	}
//
//	/**
//	 * 优化索引，返回优化策略
//	 * 
//	 * @return
//	 */
//	private static LogMergePolicy optimizeIndex() {
//		LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();
//
//		// 设置segment添加文档(Document)时的合并频率
//		// 值较小,建立索引的速度就较慢
//		// 值较大,建立索引的速度就较快,>10适合批量建立索引
//		// 达到50个文件时就和合并
//		mergePolicy.setMergeFactor(50);
//
//		// 设置segment最大合并文档(Document)数
//		// 值较小有利于追加索引的速度
//		// 值较大,适合批量建立索引和更快的搜索
//		mergePolicy.setMaxMergeDocs(5000);
//
//		// 启用复合式索引文件格式,合并多个segment
//		mergePolicy.setUseCompoundFile(true);
//		return mergePolicy;
//	}
//	
//	/**
//	 * 转换类型成lucene的data类型
//	 * @param list
//	 * @return
//	 */
//    public static ArrayList<LuceneData> transformation_Novel( ArrayList<Novel> list) {
//        ArrayList<LuceneData> transforlist = new ArrayList<LuceneData>();
//        LuceneData data = new LuceneData();
//        for (Model model : list) {
//            if (model instanceof Novel) {
//                data = new LuceneData();
//                Novel novel = (Novel) model;
//                data.setId(novel.getId() + "");
//                data.setName(novel.getName());
//                data.setAuthor(novel.getAuthor());
//                data.setOutline(novel.getOutline());
//                data.setType(novel.getNovelType().getName());
//                data.setTypeid(novel.getNovelType().getId() + "");
//                data.setBigtype("小说");
//                data.setUpdateTime(novel.getUpdateTime() + "");
//                data.setImgPath(novel.getImgPath());
//                data.setImgUrlPath(novel.getImgUrlPath());
//                data.setContent(novel.getContent());
//                data.setLink_url(novel.getLink_url());
//                data.setHot(novel.getHot());
//                data.setClickPoint(novel.getClickPoint());
//                transforlist.add(data);
//            }
//        }
//        return transforlist;
//    }
//	/**
//	 * 测试
//	 * @param args
//	 */
//	public static void main(String[] args)
//	{
////---------------------创建
////		ApplicationContext springContext = new ClassPathXmlApplicationContext(new String[]{"classpath:com/springResource/*.xml"});
////		NovelService novelService = (NovelService)springContext.getBean("novelService"); 
////		System.out.println("novelService"+novelService);
////		
////		ArrayList<Novel> list=novelService.getNovelList(21, 100);
////		ArrayList<LuceneData> transforlist=LuceneService.transformation(list);
////		for(LuceneData data : transforlist)
////		{
////			System.out.println("in"+LuceneService.createIndex(data));
////		}
//		
////---------------------搜索
//		String[] fileds=new String[]{ "name", "author","outline","type"};
//		Occur[] occurs=new Occur[] { Occur.SHOULD, Occur.SHOULD,Occur.SHOULD ,Occur.SHOULD };
//		ArrayList<LuceneData> list=LuceneUtil.search(fileds, occurs, "初雪", 1, 10);
//		
//		for(LuceneData data:list)
//		{
//			System.out.println(data);
//			System.out.println(data.getId()+":"+data.getAuthor());
//		}
//		System.out.println(list.size());
//	}
//}
