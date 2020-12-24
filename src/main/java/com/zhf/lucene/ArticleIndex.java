package com.zhf.lucene;

import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zhf.entity.Article;
import com.zhf.util.DateUtil;
import com.zhf.util.StringUtil;

@Component("articleIndex")
public class ArticleIndex {

    private Directory dir = null;

    @Value("${lucenePath}")
    private String lucenePath;

    /**
     * 获取IndexWriter实例
     * @return
     * @throws Exception
     */
    public IndexWriter getWriter()throws Exception {
        dir = FSDirectory.open(Paths.get(lucenePath));
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(dir, iwc);
        return writer;
    }

    /**
     * 添加帖子索引
     * @param article
     * @return
     */
    public boolean addIndex(Article article) {
        /** 防止多写，多线程操作干扰. */
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            IndexWriter writer = getWriter();
            Document doc = new Document();
            doc.add(new StringField("id",String.valueOf(article.getId()),Field.Store.YES));
            doc.add(new TextField("name",article.getName(), Field.Store.YES));
            doc.add(new StringField("publishDate", DateUtil.formatDate(new Date(), "yyyy-MM-dd"),Field.Store.YES));
            doc.add(new TextField("content",article.getContent(),Field.Store.YES));
            writer.addDocument(doc);
            writer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }finally {
            lock.unlock();
        }
        return true;
    }

    /**
     * 更新帖子索引
     * @param article
     */
    public boolean updateIndex(Article article){
        ReentrantLock lock=new ReentrantLock();
        lock.lock();
        try {
            IndexWriter writer = getWriter();
            Document doc=new Document();
            doc.add(new StringField("id",String.valueOf(article.getId()),Field.Store.YES));
            doc.add(new TextField("name",article.getName(),Field.Store.YES));
            doc.add(new StringField("publishDate",DateUtil.formatDate(new Date(), "yyyy-MM-dd"),Field.Store.YES));
            doc.add(new TextField("content",article.getContent(),Field.Store.YES));
            writer.updateDocument(new Term("id",String.valueOf(article.getId())), doc);
            writer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }finally{
            lock.unlock();
        }
        return true;
    }

    /**
     * 删除帖子索引
     * @param id
     */
    public void deleteIndex(String id){
        ReentrantLock lock=new ReentrantLock();
        lock.lock();
        try{
            IndexWriter writer = getWriter();
            writer.deleteDocuments(new Term("id",id));
            /** 强制删除. */
            writer.forceMergeDeletes();
            writer.commit();
            writer.close();
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }

    /**
     * 查询帖子信息
     * @param q 分词查询关键字
     * @return
     * @throws Exception
     */
    public List<Article> search(String q)throws Exception{
        dir=FSDirectory.open(Paths.get(lucenePath));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher is=new IndexSearcher(reader);
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        SmartChineseAnalyzer analyzer=new SmartChineseAnalyzer();
        QueryParser parser=new QueryParser("name",analyzer);
        Query query=parser.parse(q);
        QueryParser parser2=new QueryParser("content",analyzer);
        Query query2=parser2.parse(q);
        booleanQuery.add(query,BooleanClause.Occur.SHOULD);
        booleanQuery.add(query2,BooleanClause.Occur.SHOULD);
        TopDocs hits=is.search(booleanQuery.build(), 100);
        QueryScorer scorer=new QueryScorer(query);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
        SimpleHTMLFormatter simpleHTMLFormatter=new SimpleHTMLFormatter("<b><font color='red'>","</font></b>");
        Highlighter highlighter=new Highlighter(simpleHTMLFormatter, scorer);
        highlighter.setTextFragmenter(fragmenter);
        List<Article> articleList=new LinkedList<Article>();
        for(ScoreDoc scoreDoc:hits.scoreDocs){
            Document doc=is.doc(scoreDoc.doc);
            Article article=new Article();
            article.setId(Integer.parseInt(doc.get(("id"))));
            article.setPublishDateStr(doc.get(("publishDate")));
            String name=doc.get("name");
            String content=StringUtil.stripHtml(doc.get("content"));
            if(name!=null){
                TokenStream tokenStream = analyzer.tokenStream("name", new StringReader(name));
                String hName=highlighter.getBestFragment(tokenStream, name);
                if(StringUtil.isEmpty(hName)){
                    article.setName(name);
                }else{
                    article.setName(hName);
                }
            }
            if(content!=null){
                TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(content));
                String hContent=highlighter.getBestFragment(tokenStream, content);
                if(StringUtil.isEmpty(hContent)){
                    if(content.length()<=200){
                        article.setContent(content);
                    }else{
                        article.setContent(content.substring(0, 200));
                    }
                }else{
                    article.setContent(hContent);
                }
            }
            articleList.add(article);
        }
        return articleList;
    }

    /**
     * 查询相关帖子信息五高亮
     * @param q 查询关键字
     * @return
     * @throws Exception
     */
    public List<Article> searchNoHighLighter(String q)throws Exception{
        dir=FSDirectory.open(Paths.get(lucenePath));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher is=new IndexSearcher(reader);
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        SmartChineseAnalyzer analyzer=new SmartChineseAnalyzer();
        QueryParser parser=new QueryParser("name",analyzer);
        Query query=parser.parse(q);
        QueryParser parser2=new QueryParser("content",analyzer);
        Query query2=parser2.parse(q);
        booleanQuery.add(query,BooleanClause.Occur.SHOULD);
        booleanQuery.add(query2,BooleanClause.Occur.SHOULD);
        TopDocs hits=is.search(booleanQuery.build(), 10);
        List<Article> articleList=new LinkedList<Article>();
        for(ScoreDoc scoreDoc:hits.scoreDocs){
            Document doc=is.doc(scoreDoc.doc);
            Article article=new Article();
            article.setId(Integer.parseInt(doc.get(("id"))));
            String name=doc.get("name");
            article.setName(name);
            articleList.add(article);
        }
        return articleList;
    }
}
