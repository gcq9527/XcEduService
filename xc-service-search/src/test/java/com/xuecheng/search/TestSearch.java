package com.xuecheng.search;

import com.sun.xml.internal.stream.XMLInputFactoryImpl;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.mapping.TextScore;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/15 14:25
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {
    //高
    @Autowired
    RestHighLevelClient client;
    //低
    @Autowired
    RestClient restClient;
    //搜索全部记录
    @Test
    public void testSearchAll()throws Exception{
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //搜索方式
        //mathAllQuery搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置源字端过滤,第一个参数结果集包括那些字段,第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"}, new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配刀总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度最高的文档
        SearchHit[] searchHits = hits.getHits();
        //日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(SearchHit hit:searchHits){
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //
            String name = (String) sourceAsMap.get("name");
            //由于前边设置了源文档字段过滤，没有在里面的属性是拿取不到的
            String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
//            价格
            Double pirce = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp =dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(pirce);
        }

    }

    //term查询
    @Test
    public void testTermQuery()throws Exception{
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //页码
        int page = 1;
        //每页记录数
        int size = 1;
        //计算出记录起始下标
        int from = (page-1)*size;
        searchSourceBuilder.from(from);//起始记录下标 从0开始
        searchSourceBuilder.size(size);//每页显示的记录数
        //搜索方式
        //mathAllQuery搜索全部
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置源字端过滤,第一个参数结果集包括那些字段,第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"}, new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配刀总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度最高的文档
        SearchHit[] searchHits = hits.getHits();
        //日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(SearchHit hit:searchHits){
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //
            String name = (String) sourceAsMap.get("name");
            //由于前边设置了源文档字段过滤，没有在里面的属性是拿取不到的
            String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
//            价格
            Double pirce = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp =dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(pirce);
        }
    }

    //分页查询
    @Test
    public void testSearchPage()throws Exception{
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //页码
        int page = 1;
        //每页记录数
        int size = 1;
        //计算出记录起始下标
        int from = (page-1)*size;
        searchSourceBuilder.from(from);//起始记录下标 从0开始
        searchSourceBuilder.size(size);//每页显示的记录数
        //搜索方式
        //mathAllQuery搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置源字端过滤,第一个参数结果集包括那些字段,第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"}, new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配刀总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度最高的文档
        SearchHit[] searchHits = hits.getHits();
        //日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(SearchHit hit:searchHits){
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //
            String name = (String) sourceAsMap.get("name");
            //由于前边设置了源文档字段过滤，没有在里面的属性是拿取不到的
            String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
//            价格
            Double pirce = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp =dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(pirce);
        }
    }


    //根据id查询
    @Test
    public void testTemQueryById()throws Exception{
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
     /*   //页码
        int page = 1;
        //每页记录数
        int size = 1;
        //计算出记录起始下标
        int from = (page-1)*size;
        searchSourceBuilder.from(from);//起始记录下标 从0开始
        searchSourceBuilder.size(size);//每页显示的记录数*/
        //搜索方式
        //根据id查询
        String[] ids = new String[]{"1","2","3"};
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
        //设置源字端过滤,第一个参数结果集包括那些字段,第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"}, new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配刀总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度最高的文档
        SearchHit[] searchHits = hits.getHits();
        //日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(SearchHit hit:searchHits){
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //
            String name = (String) sourceAsMap.get("name");
            //由于前边设置了源文档字段过滤，没有在里面的属性是拿取不到的
            String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
//            价格
            Double pirce = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp =dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(pirce);
        }
    }

    //MatchQuery
    @Test
    public void testMatchQuery()throws Exception{
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
     /*   //页码
        int page = 1;
        //每页记录数
        int size = 1;
        //计算出记录起始下标
        int from = (page-1)*size;
        searchSourceBuilder.from(from);//起始记录下标 从0开始
        searchSourceBuilder.size(size);//每页显示的记录数*/
        //搜索方式
        //MatchQuery
        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发")
                .minimumShouldMatch("70%"));
        //设置源字端过滤,第一个参数结果集包括那些字段,第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"}, new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配刀总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度最高的文档
        SearchHit[] searchHits = hits.getHits();
        //日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(SearchHit hit:searchHits){
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //
            String name = (String) sourceAsMap.get("name");
            //由于前边设置了源文档字段过滤，没有在里面的属性是拿取不到的
            String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
//            价格
            Double pirce = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp =dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(pirce);
        }
    }


    //MultiQuery
    @Test
    public void testMultiQuery()throws Exception{
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //搜索方式
        //MultiQuery
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring css","name","description")
        .minimumShouldMatch("50%")
        .field("name",10));
        //设置源字端过滤,第一个参数结果集包括那些字段,第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"}, new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配刀总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度最高的文档
        SearchHit[] searchHits = hits.getHits();
        //日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(SearchHit hit:searchHits){
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //
            String name = (String) sourceAsMap.get("name");
            //由于前边设置了源文档字段过滤，没有在里面的属性是拿取不到的
            String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
//            价格
            Double pirce = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp =dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(pirce);
        }
    }

    //BoolQueyry
    @Test
    public void testBoolQuery()throws Exception{
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //搜索方式
        //MultiQuery
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);

        //再定义一个termQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");

        //定义一个boolQuery 用来拼接 must就是两个都要为true
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        //设置源字端过滤,第一个参数结果集包括那些字段,第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"}, new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配刀总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度最高的文档
        SearchHit[] searchHits = hits.getHits();
        //日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(SearchHit hit:searchHits){
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //
            String name = (String) sourceAsMap.get("name");
            //由于前边设置了源文档字段过滤，没有在里面的属性是拿取不到的
            String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
//            价格
            Double pirce = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp =dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(pirce);
        }
    }


    //Filter
    @Test
    public void testFilterQuery()throws Exception{
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //搜索方式
        //MultiQuery
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);

        //再定义一个termQuery
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");

        //定义一个boolQuery 用来拼接 must就是两个都要为true
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        //定义顾虑器
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        //大于60 小于100
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));

        searchSourceBuilder.query(boolQueryBuilder);
        //设置源字端过滤,第一个参数结果集包括那些字段,第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"}, new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配刀总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度最高的文档
        SearchHit[] searchHits = hits.getHits();
        //日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(SearchHit hit:searchHits){
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //
            String name = (String) sourceAsMap.get("name");
            //由于前边设置了源文档字段过滤，没有在里面的属性是拿取不到的
            String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
//            价格
            Double pirce = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp =dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(pirce);
        }
    }


    //Sort
    @Test
    public void testSortQuery()throws Exception{
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //搜索方式


        //再定义一个termQuery
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");

        //定义一个boolQuery 用来拼接 must就是两个都要为true
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //大于60 小于100
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));

        searchSourceBuilder.query(boolQueryBuilder);
        //添加排序
        searchSourceBuilder.sort("studymodel", SortOrder.DESC); //降序
        searchSourceBuilder.sort("price",SortOrder.ASC);//升序

        //设置源字端过滤,第一个参数结果集包括那些字段,第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"}, new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配刀总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度最高的文档
        SearchHit[] searchHits = hits.getHits();
        //日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(SearchHit hit:searchHits){
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //
            String name = (String) sourceAsMap.get("name");
            //由于前边设置了源文档字段过滤，没有在里面的属性是拿取不到的
            String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
//            价格
            Double pirce = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp =dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(pirce);
        }
    }



    //Highlight
    @Test
    public void testHighlight()throws Exception{
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        //搜索方式
        //MultiQuery
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("开发框架", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);

        //再定义一个termQuery
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");

        //定义一个boolQuery 用来拼接 must就是两个都要为true
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        //定义顾虑器
        //大于60 小于100
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));


        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
       //设置高亮
        searchSourceBuilder.highlighter(highlightBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        //设置源字端过滤,第一个参数结果集包括那些字段,第二个参数表示结果集不包含那些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"}, new String[]{});
        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配刀总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度最高的文档
        SearchHit[] searchHits = hits.getHits();
        //日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(SearchHit hit:searchHits){
            //文档的主键
            String id = hit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //源文档内容
            String name = (String) sourceAsMap.get("name");
            //取出高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields!= null){
                //取出name高亮字段
                HighlightField namehighlightField = highlightFields.get("name");
                if (namehighlightField != null){
                    Text[] fragments = namehighlightField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text text:fragments){
                        stringBuffer.append(text);
                    }
                    name = stringBuffer.toString();
                }
            }


            //由于前边设置了源文档字段过滤，没有在里面的属性是拿取不到的
            String description = (String) sourceAsMap.get("description");
            //学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
//            价格
            Double pirce = (Double) sourceAsMap.get("price");
            //日期
            Date timestamp =dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println(name);
            System.out.println(description);
            System.out.println(studymodel);
            System.out.println(pirce);
        }
    }
}