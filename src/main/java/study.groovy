import com.wuweibi.study.utils.HttpClientUtil
import groovy.json.JsonSlurper

/**
 * Created by marker on 2019/4/27.
 */


def cookie = "";

//def a = doStudy(cookie, "46346", "20180321142945222770257689610")
//println a;

def courseList = getCourseList(cookie)

def items = courseList.items
for(def a : items){

    def classId = a.classId;
    println '正在学习课程：' + a.asName

    if(a.asName == '专科英语1'){
        continue
    }

    def subjectList = getSubject(cookie, a.courseId);
    def subjects = subjectList.list;

    for(def b : subjects){

        println '正在学习章节：' + b.conFrameName
        def videoList = getVideo(cookie, b.conFrameCode);

        for(def c : videoList.activitys){
            println '正在学习视频：' + c.conActivityName


            def s = doStudy(cookie, classId, c.res.rcode);

            if(c.res.istran == "mp4"){
                doStudyVideo(cookie,classId, s.resId, c.res.rname, c.res.rbtimesd)
                println '学习完';
            }

        }
    }
}






/**
 * 获取课程
 * @param cookie
 * @return
 */
def getCourseList(String cookie){
    def headers = [:]
    headers.cookie = cookie
    def param = [:]
    param.start = 1
    param.limit = 20
    param.pageNumber = 1
    String s = HttpClientUtil.doGet("http://teach.ynou.edu.cn/eduCourseBaseinfo/getStuCouseInfo.action", param, headers);
    def jsonSlurper = new JsonSlurper()
    def object = jsonSlurper.parseText(s.toString())
    return object;
}

/**
 * 获取章节
 * @param cookie
 * @param courseId
 */
def getSubject(String cookie, def courseId) {
    def headers = [:]
    headers.cookie = cookie
    def param = [:]
    param.courseId = courseId
    String s = HttpClientUtil.doGet("http://teach.ynou.edu.cn/eduCourseBaseinfo/courseCatalog.action", param, headers);
    def jsonSlurper = new JsonSlurper()
    def object = jsonSlurper.parseText(s.toString())
    return object
}

/**
 * 获取视频
 * @param cookie
 * @param conFrameCode
 * @return
 */
def getVideo(String cookie, def conFrameCode) {
    def headers = [:]
    headers.cookie = cookie
    def param = [:]
    param.frameCode = conFrameCode
    String s = HttpClientUtil.doGet("http://teach.ynou.edu.cn/play/genVideoList.action", param, headers);
    def jsonSlurper = new JsonSlurper()
    def object = jsonSlurper.parseText(s.toString())
    return object
}

/**
 * 学习
 * @param cookie
 * @param rcode
 */
def doStudy(String cookie, classId, rcode) {
    def headers = [:]
    headers.cookie = cookie
    def param = [:]
    param.pkId = rcode
    param.classId = classId
    String s = HttpClientUtil.doGet("http://teach.ynou.edu.cn/play/returnPlayUrl.action", param, headers);
    def jsonSlurper = new JsonSlurper()
    def object = jsonSlurper.parseText(s.toString())
    return object
}

/**
 * 学习视频
 * @param cookie
 * @param rcode
 */
def doStudyVideo(String cookie, classId, rcode, courseId, videoLen) {
    def headers = [:]
    headers.cookie = cookie
    def param = [:]
    param.pkId = rcode+""
    param.courseId = courseId
    param.videoLen = videoLen
    param.viewLen = videoLen
    param.classId = classId
    String s = HttpClientUtil.doGet("http://teach.ynou.edu.cn/play/viewReport.action", param, headers);
    return s;
}