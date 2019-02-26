package com.aiwue.activiti.controller.base;


import com.aiwue.activiti.mapper.StaffMapper;
import com.aiwue.common.consts.staff.StaffStatusEnum;
import com.aiwue.common.exception.AiwueException;
import com.aiwue.common.exception.ErrorEnum;
import com.aiwue.common.page.PaginationResult;
import com.aiwue.common.utils.AssembleErrJsonUtil;
import com.aiwue.common.utils.IpUtils;
import com.aiwue.common.utils.RandomStrUtil;
import com.alibaba.fastjson.JSONObject;
import com.aiwue.common.entity.hr.Staff;
import com.aiwue.common.response.RespEntity;
import com.aiwue.common.response.RespListEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public abstract class BaseController {
    private static Logger logger = LoggerFactory.getLogger(BaseController.class);
    @Autowired
    private StaffMapper staffMapper;

    public String callbackFunName = null;
    public Integer staffId = -1;
    public String accessToken = "";
    public Staff staff = null;
    public String ipAddr = ""; //访问的Ip地址
    public String traceId = ""; //跟踪id，为一个24位字符串
    public String paramStr = ""; //调用参数

    /**
     * @param request
     * @throws AiwueException
     * @description 根据接口头文件中的userId或者accesstoken来返回该用户对象，如果接口中不包含userId信息，这返回null
     * @author 赵以宝
     * @date 2016年8月1日 上午9:17:03
     */
    public void preprocess(HttpServletRequest request) throws AiwueException {

        preProcess(request);

        if (staffId != -1 && StringUtils.isNotBlank(accessToken)){
            staff = staffMapper.selectByPrimaryKey(staffId);
            if (staff != null) {
                if (!accessToken.equals(staff.getAccessToken())) {
                    staff = null;
                }
            }
        }
        if (staff == null) {
            staffId = -1;
        }
    }

    /**
     * @param request
     * @throws AiwueException
     * @description 根据接口头文件中的staffId或者accesstoken来验证该用户是否是一个有效的用户，如果是，返回该用户对象
     * @author 赵以宝
     * @date 2016年8月1日 上午9:28:35
     */
    public void validate(HttpServletRequest request) throws AiwueException {
        preProcess(request);

        if (staffId == -1 || StringUtils.isBlank(accessToken)) {
            throw new AiwueException(ErrorEnum.ERR_USER_LOGIN_IS_OVERDUE);
        }
        //-------该员工是否存在，且密码是否正确------
        staff = staffMapper.selectByPrimaryKey(staffId);
        if (staff != null) {
            if (!accessToken.equals(staff.getAccessToken())) {
                staff = null;
            }
        }
        if (staff == null){
            staffId = -1;
            throw new AiwueException(ErrorEnum.ERR_USER_LOGIN_IS_OVERDUE);
        }

        //------判断当前员工的状态---如果为删除，抛出异常,----
        if(staff.getStatus().equals(StaffStatusEnum.REMOVED.getValue())
                ||staff.getStatus().equals(StaffStatusEnum.LEAVE.getValue())){
            logger.error("BaseController->validate->" + ErrorEnum.ERR_USER_FORBIDDEN_LOGIN.getInnMessage() + ", staffId=" + staffId);
            throw new AiwueException(ErrorEnum.ERR_USER_FORBIDDEN_LOGIN);
        }

        //-------判断accessToken有效时间-------
        Date date = staff.getTokenValidTime();
        if(date != null && date.before(new Date())){
            logger.error("BaseController->validate->" + ErrorEnum.ERR_USER_ACCESSTOKEN_IS_NULL_OR_ERR.getInnMessage() + ", staffId=" + staffId);
            throw new AiwueException(ErrorEnum.ERR_USER_ACCESSTOKEN_IS_NULL_OR_ERR);
        }

    }

    /**
     * @param request
     * @return void    返回类型
     * @throws
     * @description 对调用接口参数进行预处理
     * @author 赵以宝
     * @date 2016年8月1日 下午12:11:54
     */
    private void preProcess(HttpServletRequest request) throws AiwueException {
        try {
            ipAddr = IpUtils.getIpAddress(request);
        } catch (Exception e) {
        }
        String rUrl = request.getRequestURL().toString();

        String staffIdStr = StringUtils.trim(request.getParameter("staffId"));
        accessToken = StringUtils.trim(request.getParameter("accessToken"));
        callbackFunName = StringUtils.trim(request.getParameter("callbackparam"));

        try {
            staffId = Integer.parseInt(staffIdStr);
        } catch (NumberFormatException e) {
            staffId = -1;
        }
        traceId = RandomStrUtil.genRandomTraceIdString();
        paramStr = getRequestParams(request);
        logger.info("URL=" + rUrl + "&ipAddress=" + ipAddr +"&staffId= " + staffId + "&traceId=" + traceId + paramStr);
    }
    /**
     * @param request
     * @return User    返回类型
     * @description 返回一个staff对象
     * @author 赵以宝
     * @date 2016年7月18日 下午5:51:55
     */
    public Staff getLoginUser(HttpServletRequest request) {
        Object o = request.getSession().getAttribute("staff");
        return o == null ? null : (Staff) o;
    }

    /**
     * @param request
     * @return User    返回类型
     * @description 返回一个不包含敏感信息的user对象
     * @author 赵以宝
     * @date 2016年7月18日 下午5:51:36
     */
    public Staff getSafeLoginUser(HttpServletRequest request) {
        //Staff staff = (Staff)request.getSession().getAttribute("staff");
        Staff staff = new Staff();
        staff.setId(1);
        staff.setName("ceshi");
        if (staff != null) {
            staff.setPassword("");
            staff.setIdCard("");
        }
        return staff == null ? null : (Staff) staff;
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * @param json
     * @return String    返回类型
     * @description 返回一个支持JSONP的API结果字符串。本系统所有API返回的结果都是以如下格式
     * {
     * ecode："",
     * emsg:"",
     * result: 结果
     * }
     * @author 赵以宝
     * @date 2016年8月2日 下午6:44:20
     */
    public String AssembleJSONPResult(Object json) {

        JSONObject jsonResult = AssembleErrJsonUtil.assembleSuccessJSON();
        jsonResult.put("result", json);
        String jsonString = "";
        if (!StringUtils.isBlank(callbackFunName)) {
            jsonString = callbackFunName + "(";
        }

        jsonString += jsonResult.toJSONString();

        if (!StringUtils.isBlank(callbackFunName)) {
            jsonString += ")";
        }
        return jsonString;
    }

    /**
     * @param json
     * @return String    返回类型
     * @description 返回一个支持JSONP的API结果字符串。本系统所有API返回的结果都是以如下格式，包含动态jsonp
     * {
     * ecode："",
     * emsg:"",
     * result: 结果
     * }
     * @author 赵以宝
     * @date 2016年8月2日 下午6:44:20
     */
    public String AssembleJSONPResultJsonpParam(Object json, String jsonpParam) {

        JSONObject jsonResult = AssembleErrJsonUtil.assembleSuccessJSON();
        jsonResult.put("result", json);
        String jsonString = "";
        if (!StringUtils.isBlank(jsonpParam)) {
            jsonString = jsonpParam + "(";
        }

        jsonString += jsonResult.toJSONString();

        if (!StringUtils.isBlank(jsonpParam)) {
            jsonString += ")";
        }
        System.out.println(jsonString);
        return jsonString;
    }

    /**
     * @param json
     * @return String    返回类型
     * @throws
     * @description 返回一个支持JSONP的分页的列表结果
     * @author 赵以宝
     * @date 2016年8月3日 上午9:52:23
     */
    public String AssembleJSONPPaginationResult(Object json) {

        JSONObject jsonResult = AssembleErrJsonUtil.assembleSuccessJSON();
        jsonResult.putAll((JSONObject) json);
        String jsonString = "";
        if (!StringUtils.isBlank(callbackFunName)) {
            jsonString = callbackFunName + "(";
        }

        jsonString += jsonResult.toJSONString();

        if (!StringUtils.isBlank(callbackFunName)) {
            jsonString += ")";
        }
        return jsonString;
    }

    /**
     * @param json
     * @return String    返回类型
     * @throws
     * @description 返回一个支持JSONP的分页的列表结果，并包含动态的jsonp包裹
     * @author 赵以宝
     * @date 2016年8月3日 上午9:52:23
     */
    public String AssembleJSONPPaginationResultJsonpParam(Object json, String jsonpParam) {

        JSONObject jsonResult = AssembleErrJsonUtil.assembleSuccessJSON();
        jsonResult.putAll((JSONObject) json);
        String jsonString = "";
        if (!StringUtils.isBlank(jsonpParam)) {
            jsonString = jsonpParam + "(";
        }

        jsonString += jsonResult.toJSONString();

        if (!StringUtils.isBlank(jsonpParam)) {
            jsonString += ")";
        }
        return jsonString;
    }

    /**
     * @param
     * @return 返回类型
     * @description 返回jsonp包裹的error信息
     * @author 段华微
     * @date 2018-02-02 11:36:40
     */
    public String AssembleJSONPErrorResult(ErrorEnum errorEnum) {

        JSONObject jsonResult = AssembleErrJsonUtil.assembleJSON(errorEnum);
        String jsonString = "";
        if (!StringUtils.isBlank(callbackFunName)) {
            jsonString = callbackFunName + "(";
        }

        jsonString += jsonResult.toJSONString();

        if (!StringUtils.isBlank(callbackFunName)) {
            jsonString += ")";
        }
        return jsonString;
    }



    /**
     * @Description: 返回不带结果的正确的响应
     * @date: 2018/5/30 16:26
     * @auther: huo
     */
    public  RespEntity returnSuccessJsonResult(){
        return returnSuccessJsonResult(null);
    }

    /**
     * @Description: 返回待结果的正确响应
     * @param: object
     * @return: Object
     * @date: 2018/5/30 16:27
     * @auther: huo
     */
    public  RespEntity returnSuccessJsonResult(Object object){
        RespEntity respEntity = new RespEntity(object);
        return respEntity;
    }

    /**
     * @Description: 返回分页listjson
     * @param: object
     * @return: Object
     * @date: 2018/5/30 16:27
     * @auther: huo
     */
    public  RespListEntity returnSuccessJsonListResult(PaginationResult object){
        return returnSuccessJsonListResult(object,null);

    }
    public  RespListEntity returnSuccessJsonListResult(PaginationResult object,Map map){
        RespListEntity respEntity = new RespListEntity(object);
        respEntity.setEnu(map);
        return respEntity;
    }

    public  RespListEntity returnSuccessJsonListResult(List<Object> object){
        RespListEntity respEntity = new RespListEntity(object);
        return respEntity;
    }
    /**
     * @Description: 根据 ErrorEnum 返回不带结果的信息
     * @param: errorEnum
     * @return: Object
     * @date: 2018/5/30 16:27
     * @auther: huo
     */
    public  RespEntity returnJsonResult(ErrorEnum errorEnum){
        return returnJsonResult(errorEnum,null);
    }

    /**
     * @Description: 根据 ErrorEnum 返回带结果的信息
     * @param: errorEnum
     * @param: object
     * @return: Object
     * @date: 2018/5/30 16:28
     * @auther: huo
     */
    public  RespEntity returnJsonResult(ErrorEnum errorEnum,Object object){
        RespEntity respEntity = new RespEntity(errorEnum,object);
        return respEntity;
    }
    /**
     * @Description: 根据 ErrorEnum 返回带结果的信息
     * @param: errorEnum
     * @param: object
     * @return: Object
     * @date: 2018/5/30 16:28
     * @auther: huo
     */
    public  RespEntity returnJsonAndMapResult(Object object,Map map){
        RespEntity respEntity = new RespEntity(object);
        respEntity.setEnu(map);
        return respEntity;
    }

    /**
     * 获取调用参数，并返回参数字符串
     * @param request
     * @return
     */
    public String getRequestParams(HttpServletRequest request) {
        StringBuilder result = new StringBuilder();
        Map map = request.getParameterMap();
        Set keSet = map.entrySet();
        for (Iterator itr = keSet.iterator(); itr.hasNext(); ) {
            Map.Entry me = (Map.Entry) itr.next();
            Object ok = me.getKey();
            Object ov = me.getValue();
            String[] value = new String[1];
            if (ov instanceof String[]) {
                value = (String[]) ov;
            } else {
                value[0] = ov.toString();
            }
            result.append("&");
            result.append(ok);
            result.append("=");
            for (int k = 0; k < value.length; k++) {
                result.append(value[k]);
                if (k < value.length -1) {
                    result.append(",");
                }
            }

        }
        return result.toString();
    }


    /**
     * 拦截异常集中处理
     *
     * @return
     * @throws IOException
     */
    @ExceptionHandler
    @ResponseBody
    public void exception(Exception e, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        e.printStackTrace();
        String message;
        if (e instanceof AiwueException) {
            message = AssembleErrJsonUtil.AssembleErr2Json(((AiwueException) e).getExceptionError());
        } else  {
            message = AssembleErrJsonUtil.AssembleErr2Json(ErrorEnum.ERR_SYSTEM_ERROR);
        }
        response.getWriter().print(message);
    }
}
