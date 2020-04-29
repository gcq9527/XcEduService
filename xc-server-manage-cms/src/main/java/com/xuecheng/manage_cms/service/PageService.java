package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/1 17:46
 */
@Service
public class PageService {

    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsConfigRepository cmsConfigRepository;
    @Autowired
    CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    CmsSiteRepository cmsSiteRepository;

    /**
     * 页面查询方法
     * @param page 页码 从1开始技术
     * @param size  每页记录数
     * @param queryPageRequest  查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size,QueryPageRequest queryPageRequest){
        //自定义条件查询
        //自定义条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值对象
        CmsPage cmsPage = new CmsPage();
        //设置条件值 (站点id)
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //设置模板id作为查询条件 (模板id)
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        //设置页面别名作为查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //条件对象
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        //分页参数
        if (page <= 0){
            page = 1;
        }
        page = page  -1 ;
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable); //实现自定义条件查询并且分页查询
        //返回结果类型
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent()); //数据列表
        queryResult.setTotal(all.getTotalElements()); //数据总记录数
        //参数一操作成功 参数二·返回数据
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

/*    //新增页面
    public CmsPageResult add(CmsPage cmsPage){
        //校验页面名称 站点Id 页面webpath 的唯一性
        //根据页面名称 站点Id 页面webpath 去cms_page 集合 如果查到此页面以及存在
        CmsPage  cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),cmsPage.getSiteId(),cmsPage.getPageWebPath());
        if (cmsPage1 == null){ //为空 没有记录 进行插入
            //调用dao新增页面
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }
        //添加失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }*/

    //新增页面
    public CmsPageResult add(CmsPage cmsPage){
        if (cmsPage == null){
            //抛出异常 非法参数异常 指定异常信息的内容
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //校验页面名称 站点Id 页面webpath 的唯一性
        //根据页面名称 站点Id 页面webpath 去cms_page 集合 如果查到此页面以及存在
        CmsPage  cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),cmsPage.getSiteId(),cmsPage.getPageWebPath());
        if (cmsPage1 != null){ //为空 没有记录 进行插入
            //页面已经存在
            //抛出异常 异常内容就是页面已经存在
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //调用dao新增页面
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        //添加失败
   //     return new CmsPageResult(CommonCode.FAIL,null);
    }


    //根据页面id查询
    public CmsPage getById(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()){
            CmsPage cmsPage = optional.get();
            return cmsPage;
        }
        return null;
    }

    //根据页面id修改
    public CmsPageResult update(String id,CmsPage cmsPage){
        //根据id从数据库中查询出信息
        CmsPage one = this.getById(id);
        if (one != null){
            //准备要更新的数据
            //设置要更新的数据
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
                //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //修改静态页面
            one.setDataUrl(cmsPage.getDataUrl());
            //提交修改
            cmsPageRepository.save(one);
            return new CmsPageResult(CommonCode.SUCCESS,one);
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }
    //根据id删除页面
    public ResponseResult delete(String id){
        //先查询一下
        Optional<CmsPage> option = cmsPageRepository.findById(id);
        if (option.isPresent()){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);

    }
    //根据id查询config
    public CmsConfig getConfigById(String id){
        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
        if (optional.isPresent()){
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }
        return null;
    }

    //页面静态化方法
    /**
     * 静态化程学获取页面的DataUrl
     *
     * 1.静态化程序远程请求DataUrl获取数据模型/获取数据模型
     *      1.先通过PageId查询到cmsPage对象
     *      2.通过cmsPage对象拿到DataUrl
     *      3.通过 ResponseEntity<Map> forEntity =  restTemplate.getForEntity(dataUrl,Map.class);发送请求 拿到数据模型
     * 2。静态化程序获取页面的模板信息 参数 pageId
     *      1.通过PageId拿到CmsPage对象
     *      2.通过CmsPage对象的getTemplateId拿到模板id
     *      3.通过使用模板id查询处 cmsTemplateRepository.findById 查询到CmsTemplate对象的信息
     *      4.通过 模板文件id 查询出模板对象
     *            GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId));
     *      5.通过GridFsFile的id打开下载流   GridFsDownLoadStream gridfsDownLoadStream =  gridFSBucket.openDownloadStream(gridFsFile.getObjectId());
     *      6.通过创建GridFsResource对象  拿到内容
     *          GridFsResource gridFsResource = new GridFsResource(gridFsFile,gridFSDownloadStream);
     *      7.然后使用 IOUtils.toString
     *          同时参数需要 GridfsResource的InputStream 以及UTF-8编码
     *          String body = IOUtils.toString(gridFsResource.getInputStream(),"utf-8")
     *          return body; 返回模板内容
     * 3.执行页面静态化
     *         参数(String Template,Map model) 模板 数据
     *      1.静态页面 = 模板 + 数据
     *      2. 创建配置对象
     *         Configuration configuration = new Configuration(Configuration.getVersion());
     *      3.使用StringTemplateLoader对象
     *          先调用其set设置模板名称,以及模板内容
     *            stringTemplateLoader.putTemplate("template",templateContent);
     *      4.configuration 配置模板加载器 调用sett方法加载到对象中
     *         configuration.setTemplateLoader(stringTem?plateLoader);
     *      4.通过configuration的 Template template = configuration.getTemplate("template");方法 拿到模板对象
     *      5.使用 String html = FreeMarkerTemplateUtils.processTemplateIntoString(template,model);
     *          返回对应的页面
     *
     * @param pageId
     * @return
     */
    public String getPageHtml(String pageId) {
        //获取数据模型
        Map model = getModelByPageId(pageId);
        if (model == null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //获取页面模板的信息
        String template = getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(template)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //模板加上数据
        //执行页面静态化
        String html = generateHtml(template,model);
        return html;
    }

    //获取数据模型
    private Map getModelByPageId(String pageId){
        //取出页面的信息
        CmsPage cmspage = this.getById(pageId);
        if (cmspage == null){
            //页面找不到
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXCEPTION);
        }
        //取出页面的dataurl
        String dataUrl = cmspage.getDataUrl();
        if(StringUtils.isEmpty(dataUrl)){
            //返回dataurl为空
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXCEPTION);
        }
        //通过restTemplate请求dataurl获取数据
        ResponseEntity<Map> forEntity =  restTemplate.getForEntity(dataUrl,Map.class);
        Map body = forEntity.getBody();
        return body;
    }

    //执行消息静态化
    private String generateHtml(String templateContent, Map model){
        //创建配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateContent);
        //向configuration 配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板内容
        try {
            Template template = configuration.getTemplate("template");
            //调用api进行静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template,model);
            return content;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    //获取页面的模板信息
    public String getTemplateByPageId(String PageId){
        //通过pageid获取到cmspage对象
        CmsPage cmspage = this.getById(PageId);
        if (cmspage == null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXCEPTION);
        }
        //获取页面模板id
        String templateId = cmspage.getTemplateId();
        if (StringUtils.isEmpty(templateId)){
            //模板为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //查询模板信息
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()){
            //拿到模板对象
            CmsTemplate cmsTemplate = optional.get();
            //获取模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();

            //从GridFs中取模板文件内容
            //根据文件id查询文件
            GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开一个下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFsFile.getObjectId());
            //创建GridFsResource对象 获取流
            GridFsResource gridFsResource = new GridFsResource(gridFsFile,gridFSDownloadStream);
            //从流中取出数据
            try{
                String content = IOUtils.toString(gridFsResource.getInputStream(),"utf-8");
                return content;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 页面发布
     * 分成三个方法来执行
     * 1.执行页面静态化
     * 2.将页面静态化保存到GridFs中 (String pageId,String htmlTemplate) id 以及内容
     *      1.存储GridFs对象需要配置其对应的配置类 Mongodb
     *      2.通过pageId查询到CmsPage对象
     *      3.将 htmlcontent内容转成输入流
     *             InputStream inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
     *      4.将html文件内容保存到GridFs grifsTemplate调用其store方法 传入输入流以及 page名字
     *            ObjectId objectId = gridFsTemplate.store(inputStream,cmsPage.getPageName());
     *      5. 更新HtmlFilId 然后保存
     *      cmsPage.setHtmlFileId(objectId.toHexString());
     *         cmsPageRepository.save(cmsPage);
     * 3.向mq发送消息参数(pageId)
     *      1.通过pageId拿到CmsPage对象
     *      2.创建Map集合用于发送消息
     *          Map<String,String> map = new HashMap<>();
     *          map.put(pageId,pageId);
     *         然后转换成JSON格式
     *      3.通过RabbitMQTemplate.convertAndSend()发送消息
     * @param pageId
     * @return
     */
    public ResponseResult post(String pageId){
        //执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        //将页面静态化文件保存到GridFs中
        saveHtml(pageId,pageHtml);
        //向MQ发消息
        sendPostPage(pageId);
        return  new ResponseResult(CommonCode.SUCCESS);
    }
    //向mq返送消息
    private void sendPostPage(String pageId){
        //先得到页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAN);
        }
        //创建消息对象
        Map<String,String> msg = new HashMap<>();
        msg.put("pageId",pageId);
        //转换成json串
        String jsonString = JSON.toJSONString(msg);
        //发送给mq
        //站点id
        String siteId = cmsPage.getSiteId();
        rabbitTemplate.convertAndSend(RabbitConfig.EX_ROUTING_CMS_POSTPAGE,siteId,jsonString);
    }

    //保存html到GridFs
    private CmsPage saveHtml(String pageId,String htmlContent){
        //先得到页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAN);
        }
        ObjectId objectId = null;

        try {
            //将htmlcontent内容转成输入流
            InputStream inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            //将html文件内容保存到GridFs
            objectId = gridFsTemplate.store(inputStream,cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将html文件id更新到cmsPage中
        cmsPage.setHtmlFileId(objectId.toHexString());
        //保存cmspage
        cmsPageRepository.save(cmsPage);
        return cmsPage;
        //将html文件id更新到CmsPage中
    }

    //保存页面 有则更新 没有则添加
    public CmsPageResult save(CmsPage cmsPage) {
        //判断页面是否存在
        CmsPage one = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (one != null){
            //进行更新
            return this.update(one.getPageId(),cmsPage);
        }
        return this.add(cmsPage);
    }

    //一键发布页面
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {

        //将页面信息存储到cms_page 集合中
        CmsPageResult save = this.save(cmsPage);
        if (!save.isSuccess()) { //不存在
            ExceptionCast.cast(CommonCode.FAIL);
        }
        CmsPage cmsPageSave = save.getCmsPage();
        //得到页面id
        String pageId = cmsPageSave.getPageId();
        //执行页面发布 (先静态化 保存GridFs 向MQ发送信息)
        ResponseResult post = this.post(pageId);
        if (!post.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //拼接页面Url
        String siteId = cmsPageSave.getSiteId();
        CmsSite cmsSite = findCmsSiteById(siteId);
        //页面url
        String pageUrl = cmsSite.getSiteDomain() + cmsSite.getSiteWebPath() + cmsPageSave.getPageWebPath() + cmsPageSave.getPageName();
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);


    }
    //根据站点id查询道站点信息
    public CmsSite findCmsSiteById(String id){
        Optional<CmsSite> byId = cmsSiteRepository.findById(id);
        if (byId.isPresent()){
            return byId.get();
        }
        return null;
    }
}



























