package com.xuecheng.manage_course.service;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.annotation.ExceptionProxy;

import java.awt.print.Pageable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/8 10:38
 */
@Service
public class CourseService {
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    CourseMarketRepoitory courseMarketRepoitory;
    @Autowired
    CoursePicRepository coursePicRepository;
    @Autowired
    CmsPageClient cmsPageClient;
    @Autowired
    CoursePubRepository coursePubRepository;
    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;
    @Autowired
    TeachplanMediaPubRepository teachplanMediaPubRepository;

    public TeachplanNode findTeachplanList(String courseId){
        return teachplanMapper.selectList(courseId);
    }

    /**
     *
     * @param teachplan
     * @return
     */
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan){
        if (teachplan == null ||
                StringUtils.isEmpty(teachplan.getCourseid()) ||
                StringUtils.isEmpty(teachplan.getPname())){
            //参数异常
            ExceptionCast.cast(CommonCode.INVALID_PARAN);
        }
        //课程id
        String courseId = teachplan.getCourseid();
        //传入的parentId
        String parentId = teachplan.getParentid();
        if (StringUtils.isEmpty(parentId)){
            //取出该课程的根节点
              parentId = this.getTeachplanRoot(courseId);
        }
        Optional<Teachplan> optional = teachplanRepository.findById(parentId);
        Teachplan teachplan1 = optional.get();
        //父节点的级别
        String grids = teachplan1.getGrade();
        //新节点
        Teachplan teachplan1New = new Teachplan();
        BeanUtils.copyProperties(teachplan,teachplan1New); //将内容拷贝到新节点中
        teachplan.setParentid(parentId);
        teachplan.setCourseid(courseId);

        if (grids.equals("1")){
            teachplan.setGrade("2");//级别 根据父结点的级别来设置
        }else{
            teachplan.setGrade("3");
        }
        //报道到数据库
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 查询课程根节点 查询不到 自动添加跟节点
     * @param courseId
     * @return
     */
    private String getTeachplanRoot(String courseId){

        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()){
            return null;
        }
        CourseBase courseBase = optional.get();
        //查询课程的根节点 通过课程节点以及父节点查询
        List<Teachplan> teachplanList =  teachplanRepository.findByCourseidAndParentid(courseId,"0");
        //查询不到 要自动添加跟节点
        if (teachplanList == null || teachplanList.size() <= 0 ){
            Teachplan teachplan = new Teachplan();
            teachplan.setParentid("0");
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseId);
            teachplan.setGrade("1");
            teachplan.setStatus("0");
            teachplanRepository.save(teachplan);
            return teachplan.getId();
        }
        //返回根节点id
        return teachplanList.get(0).getId();
    }


    //查询我的课程
    public QueryResponseResult<CourseInfo> findCouserInfo(String companyId,int page, int size, CourseListRequest courseListRequest ){
        if (courseListRequest == null){
            courseListRequest = new CourseListRequest();
        }
        //验证数据
        if (page <= 0){
            page = 0;
        }
        if (size<=0){
            size = 0;
        }
        //将公司id传入dao
        courseListRequest.setCompanyId(companyId);
        //分页
        PageHelper.startPage(page,size);

        Page<CourseInfo> couseInfo = courseMapper.findCourseListPage(courseListRequest);
        QueryResult<CourseInfo> result = new QueryResult<>();
        result.setList(couseInfo.getResult());//数据
        result.setTotal(couseInfo.getTotal());//总记录数
        return new QueryResponseResult<CourseInfo>(CommonCode.SUCCESS, result);
    }

    public ResponseResult addCourseBase(CourseBase courseBase){
        if (courseBase == null){
            courseBase = new CourseBase();
        }
        //保存课程信息
        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CourseBase getCourseBaseById(String CourseId){
        Optional<CourseBase> optional = courseBaseRepository.findById(CourseId);
        if (optional.isPresent()){
            return optional.get();

        }
        return null;
    }
    @Transactional
    public ResponseResult updateCourseBase(String id,CourseBase courseBase){
        CourseBase one = this.getCourseBaseById(id);
        if (one == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAN);
        }
        one.setName(courseBase.getName());
        one.setMt(courseBase.getMt());
        one.setSt(courseBase.getSt());
        one.setGrade(courseBase.getGrade());
        one.setStudymodel(courseBase.getStudymodel());
        one.setUsers(courseBase.getUsers());
        one.setDescription(courseBase.getDescription());
        courseBaseRepository.save(one);
        return  new ResponseResult(CommonCode.SUCCESS);
    }
    //查询课程市场对象
    public CourseMarket getCourseMarketById(String courseId){
        Optional<CourseMarket> option = courseMarketRepoitory.findById(courseId);
        if (option.isPresent()){
            return option.get();
        }
        return null;
    }


    public CourseMarket updateCourseMarket(String id,CourseMarket courseMarket){
        CourseMarket one =this.getCourseMarketById(id);
        if (one != null){
            one.setCharge(courseMarket.getCharge());
            one.setStartTime(courseMarket.getStartTime());//课程有效期，开始时间
            one.setEndTime(courseMarket.getEndTime());//课程有效期，结束时间
            one.setPrice(courseMarket.getPrice());
            one.setQq(courseMarket.getQq());
            one.setValid(courseMarket.getValid());
            courseMarketRepoitory.save(one);//保存
        }else{//为空
            one = new CourseMarket();
            BeanUtils.copyProperties(courseMarket,one);
            //设置课程id
            one.setId(id);
            courseMarketRepoitory.save(one);
        }
        return one;
    }

    //向课程管理数据添加课程与图片的关联信息
    //有了就更新 没有就添加
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        //课程图片信息
        CoursePic coursePic = null;
        //查询课程图片
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if (optional.isPresent()){
            coursePic = optional.get();
        }
        if (coursePic == null){
            coursePic = new  CoursePic();
        }
        coursePic.setPic(pic);
        coursePic.setCourseid(courseId);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    //查询课程图片
    public CoursePic findCourseId(String courseId) {
        Optional<CoursePic> byId = coursePicRepository.findById(courseId);
        if (byId.isPresent()){
            CoursePic coursePic = byId.get();
            return coursePic;
        }
        return null;
    }
    //删除课程图片
    @Transactional
    public ResponseResult deleteCourseId(String courseId) {
        long result = coursePicRepository.deleteByCourseid(courseId);
        if(result > 0 ){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    //查询课程视图 包括 基本信息 图片 营销  课程计划
    public CourseView getCourseView(String id) {
        CourseView courseView = new CourseView();
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        if (optional.isPresent()){
            CourseBase courseBase = optional.get();
            courseView.setCourseBase(courseBase);
        }
        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if (optional.isPresent()){
            CoursePic coursePic = picOptional.get();
            courseView.setCoursePic(coursePic);
        }

        //课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepoitory.findById(id);
        if (marketOptional.isPresent()){
            CourseMarket courseMarket = marketOptional.get();
            courseView.setCourseMarket(courseMarket);
        }
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);

            return courseView;

    }
    @Value("${course‐publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course‐publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course‐publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course‐publish.siteId}")
    private String publish_siteId;
    @Value("${course‐publish.templateId}")
    private String publish_templateId;
    @Value("${course‐publish.previewUrl}")
    private String previewUrl;


    //根据id查询课程基本信息
    public CourseBase findCourseBaseById(String courseId){
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);
        if(baseOptional.isPresent()){
            CourseBase courseBase = baseOptional.get();
            return courseBase;
        }
        ExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);
        return null;
    }

    //课程预览
    public CoursePublishResult preview(String id) {
        CourseBase courseBaseById = findCourseBaseById(id);
        //请求cms添加页面
        //准备cmsPage信息
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId); //站点id
        cmsPage.setDataUrl(publish_dataUrlPre+id);//数据模型
        cmsPage.setPageName(id+".html");//页面名称
        cmsPage.setPageAliase(courseBaseById.getName()); //页面别名 课程名称
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//模板id
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        //远程调用cms
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if (!cmsPageResult.isSuccess()){
            //抛出异常
                return new CoursePublishResult(CommonCode.FAIL, null);
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();
        //拼装页面预览的url
        String previewUrl1 = previewUrl + pageId;
        //返回CoursePublicshReuslt对象。实现页面预览
        return new CoursePublishResult(CommonCode.SUCCESS,previewUrl1);
    }

    //课程发布
    @Transactional
    public CoursePublishResult publish(String id) {
        //查询课程
        CourseBase courseBaseById = findCourseBaseById(id);
        //请求cms添加页面
        //准备cmsPage信息
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId); //站点id
        cmsPage.setDataUrl(publish_dataUrlPre+id);//数据模型
        cmsPage.setPageName(id+".html");//页面名称
        cmsPage.setPageAliase(courseBaseById.getName()); //页面别名 课程名称
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//模板id
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        //调用cms一键发布接口 将课程详情到服务器
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (!cmsPostPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //保存课程发布状态为已发布
        CourseBase courseBase = saveCoursePubState(id);
        if (courseBase == null){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //保存课程索引信息
        //创建一个Coursepub对象
        CoursePub coursePub = createCoursePub(id);
        //将coursepub对象保存到数据库
        saveCoursePub(id,coursePub);
        //缓存课程的信息
        //得到页面url
        String pageUrl = cmsPostPageResult.getPageUrl();
        //向teacholanMediaPub中保存课程媒资信息

        this.saveTeachplanMedia(id);

        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }
//    向teacholanMediaPub中保存课程媒资信息
    private void saveTeachplanMedia(String courseId){
        //先删除teachplan中MediaPub中的数据
        teachplanMediaPubRepository.deleteByCourseId(courseId);
        //从teachplanMedia中查询
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseId(courseId);

        List<TeachplanMediaPub> teachplanMediaPubs = new ArrayList<>();
        //将teachplanMediaList数据放到teachplanMediaPub中
        for (TeachplanMedia teachplanMedia : teachplanMediaList){
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia,teachplanMediaPub);
            //添加时间戳
            teachplanMediaPub.setTimestamp(new Date());
            teachplanMediaPubs.add(teachplanMediaPub);
        }
        //将techolanMediaList插入到teachplanMediaPub
        teachplanMediaPubRepository.saveAll(teachplanMediaPubs);
    }


    //更改课程为已发布 202002
    public CourseBase saveCoursePubState(String courseId){
        CourseBase courseBaseById = this.findCourseBaseById(courseId);
        courseBaseById.setStatus("202002");
        courseBaseRepository.save(courseBaseById);
        return courseBaseById;
    }
    //将coursePub保存到数据库
    private CoursePub saveCoursePub(String id,CoursePub coursePub){
        CoursePub coursePubNew = null;
        //根据课程id查询到coursepub
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);
        if (coursePubOptional.isPresent()){
            coursePubNew = coursePubOptional.get();
        }else{
            coursePubNew = new CoursePub();
        }
        //将coursePub对象中的信息保存到coursePubNew中
        BeanUtils.copyProperties(coursePub,coursePubNew);
        coursePubNew.setId(id);
        //时间戳 给logstach使用
        coursePubNew.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(date);
        coursePubRepository.save(coursePubNew);
        return coursePubNew;
    }

    //创建cougrsepub对象
    public CoursePub createCoursePub(String id){
        CoursePub coursePub = new CoursePub();
        //根据id查询到course_base
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (courseBaseOptional.isPresent()){
            CourseBase courseBase = courseBaseOptional.get();
            BeanUtils.copyProperties(courseBase,coursePub);
        }
        //查询课程图片
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(id);
        if (coursePicOptional.isPresent()){
            CoursePic Coursepic = coursePicOptional.get();
            BeanUtils.copyProperties(Coursepic,coursePub);
        }

        //课程营销信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepoitory.findById(id);
        if (courseMarketOptional.isPresent()){
            CourseMarket courseMarket = courseMarketOptional.get();
            BeanUtils.copyProperties(courseMarket,coursePub);
        }
        //课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        String jsonString = JSON.toJSONString(teachplanNode);
        //将课程计划信息json串保存到course——pub中
        coursePub.setTeachplan(jsonString);
        return coursePub;
    }

    //  保存课程计划与媒资文件的关联
    public ResponseResult savemedia(TeachplanMedia teachplanMedia) {
        if (teachplanMedia == null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())){
            //非法参数
            ExceptionCast.cast(CommonCode.INVALID_PARAN);
        }
        //校验课程计划是否时3级
        String teachplanId = teachplanMedia.getTeachplanId();
        //查询到课程计划
        Optional<Teachplan> optinal = teachplanRepository.findById(teachplanId);
        if (!optinal.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAN);
        }
        //查询到课程计划
        Teachplan teachplan = optinal.get();
        String grade = teachplan.getGrade();
        //取出等级
        if (StringUtils.isEmpty(grade) || !grade.equals("3")){
            //只允许选择第三季的课程计划管理视频
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }
        Optional<TeachplanMedia> mediaOptional = teachplanMediaRepository.findById(teachplanId);
        TeachplanMedia one = null;
        if (mediaOptional.isPresent()){
            one = mediaOptional.get();
        }else{
            one = new TeachplanMedia();
        }

        //将TeachplanMedia保存到数据库
        one.setCourseId(teachplan.getCourseid());//课程id
        one.setMediaId(teachplanMedia.getMediaId());//媒资文件id
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());//媒资文件名称
        one.setMediaUrl(teachplanMedia.getMediaUrl());//媒资文件url
        one.setTeachplanId(teachplanId);
        teachplanMediaRepository.save(one);

        return new ResponseResult(CommonCode.SUCCESS);

    }
}