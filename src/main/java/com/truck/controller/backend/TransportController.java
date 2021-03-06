package com.truck.controller.backend;

import com.google.common.collect.Maps;
import com.truck.common.Const;
import com.truck.common.ResponseCode;
import com.truck.common.ServerResponse;
import com.truck.dao.TransportMapper;
import com.truck.pojo.Admin;
import com.truck.pojo.Entry;
import com.truck.pojo.Transport;
import com.truck.service.FileService;
import com.truck.service.IExportsListsService;
import com.truck.service.ITransportService;
import com.truck.util.JsonUtil;
import com.truck.util.Post4;
import com.truck.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/manage/transport/")
public class TransportController {

    private static  final Logger logger = LoggerFactory.getLogger(TransportController.class);
    @Autowired
    private ITransportService iTransportService;
    @Autowired
    private FileService fileService;
    @Autowired
    private IExportsListsService iExportsListsService;
    @Autowired
    private TransportMapper transportMapper;

    /**
     * 出口录入信息
     * @param session
     * @param transport
     * @return
     */
    @RequestMapping("add_transport.do")
    @ResponseBody
    public ServerResponse addTransport(HttpSession session, Transport transport){
        Admin admin = (Admin)session.getAttribute(Const.CURRENT_ADMIN);
        if(admin == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员用户未登录，请登录");
        }
        return iTransportService.addTransport(admin.getAdminId(),transport);
    }

    /**
     * 修改运输信息
     * @param session
     * @param transport
     * @return
     */
    @RequestMapping("update_transport.do")
    @ResponseBody
    public ServerResponse updateTransport(HttpSession session,Transport transport){
        Admin admin = (Admin)session.getAttribute(Const.CURRENT_ADMIN);
        if(admin == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员用户未登录，请登录");
        }
        return iTransportService.updateTransport(admin.getAdminId(),transport);
    }

    /**
     * 删除运输信息
     * @param session
     * @param id
     * @return
     */
    @RequestMapping("del_transport.do")
    @ResponseBody
    public ServerResponse updateTransport(HttpSession session,Integer id){
        Admin admin = (Admin)session.getAttribute(Const.CURRENT_ADMIN);
        if(admin == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员用户未登录，请登录");
        }
        return iTransportService.delTransport(admin.getAdminId(),id);
    }


    /**
     * 进口完善信息
     * @param id
     * @param salesList
     * @return
     */
    @RequestMapping("consummate_transport.do")
    @ResponseBody
    public ServerResponse consummateTransport(Integer id,HttpServletRequest request,
                                              @RequestParam(value = "salesList",required = false) MultipartFile[] salesList){
        Transport transport = transportMapper.selectByPrimaryKey(id);
        if(transport.getStatus() > Const.TransportStatusEnum.OVER_CONFIRM.getCode()){
            return ServerResponse.createByErrorMessage("该记录无法进行修改");
        }
        //进行查看并且进行删除判断
        ServerResponse checkEntry = iTransportService.checkEntryByDeclareNum(transport.getDeclareNum());
        if(checkEntry.getStatus() == 1){
            return checkEntry;
        }
        int status = 0;
        ServerResponse serverResponse2 = iExportsListsService.checkExcel(salesList,request,status);
        if(!serverResponse2.isSuccess()) {
            return serverResponse2;
        }
        ServerResponse serverResponse = iTransportService.createEntry(id);
        Map salesMap = uploadFileCDNExcel(salesList,request,serverResponse,status);
        String[] urlS= (String[])salesMap.get("file_path");
        StringBuffer filePath = new StringBuffer();
        for(int i=0;i<urlS.length;i++){
            if(i==0){
                filePath.append(urlS[i]);
            }else{
                filePath.append(",").append(urlS[i]);
            }
        }
        return iTransportService.consummateTransport(id,filePath.toString());
    }

    /**
     * 主机清单上传接口
     * @param id
     * @param request
     * @param salesList
     * @return
     */
    @RequestMapping("host_transport.do")
    @ResponseBody
    public ServerResponse hostTransport(Integer id,HttpServletRequest request,
                                              @RequestParam(value = "salesList",required = false) MultipartFile[] salesList){
        Transport transport = transportMapper.selectByPrimaryKey(id);
        if(transport.getStatus() > Const.TransportStatusEnum.OVER_CONFIRM.getCode()){
            return ServerResponse.createByErrorMessage("该记录无法进行修改");
        }
        //重新上传  重新导入
        //进行查看并且进行删除判断
        ServerResponse checkEntry = iTransportService.checkZhuJiEntryByDeclareNum(transport.getDeclareNum());
        if(checkEntry.getStatus() == 1){
            return checkEntry;
        }

        int status = 1;
        ServerResponse serverResponse2 = iExportsListsService.checkExcel(salesList,request,status);
        if(!serverResponse2.isSuccess()) {
            return serverResponse2;
        }
        ServerResponse serverResponse = iTransportService.createHostEntry(id);
        Map salesMap = uploadFileCDNExcel(salesList,request,serverResponse,status);
        String[] urlS= (String[])salesMap.get("file_path");
        StringBuffer filePath = new StringBuffer();
        for(int i=0;i<urlS.length;i++){
            if(i==0){
                filePath.append(urlS[i]);
            }else{
                filePath.append(",").append(urlS[i]);
            }
        }
        return iTransportService.hostTransport(id,filePath.toString());
    }

    /**
     * 查询所有列表，带分页 可根据状态查询
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("get_all_list.do")
    @ResponseBody
    public ServerResponse getAllList(@RequestParam(value = "status",required = false) Integer status,
                                     @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                     @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        return iTransportService.getAllList(status,pageNum,pageSize);
    }

    public Map uploadFileCDNExcel(MultipartFile[] files, HttpServletRequest request,ServerResponse serverResponse,Integer status) {
        Map resultMap = Maps.newHashMap();
        MultipartFile file = null;
        String path = request.getSession().getServletContext().getRealPath("upload");
        logger.info("服务器path: {}",path);
        String targetFileName = null;
        int success = 0;
        String[] urlS = new String[files.length];
        try {
            for (int i = 0; i < files.length; i++) {
                targetFileName = fileService.uploadReturnCDN(files[i], path);
                if (StringUtils.isNotBlank(targetFileName)) {
                    logger.info("targetFileName的值: {}",targetFileName);
                    urlS[i] = PropertiesUtil.getProperty("field") +targetFileName;
                    targetFileName = targetFileName.substring(targetFileName.lastIndexOf("/")+1);
                    if(serverResponse.isSuccess()){
                        logger.info("serverResponse.isSuccess()======");
                        if(status == 0){
                            logger.info("status == 0======");
                            iExportsListsService.bachInsertExports(Integer.parseInt(serverResponse.getData().toString()),path+"/"+targetFileName);
                        }else if(status == 1){
                            logger.info("status == 1======");
                            //远程调用  批量插入

                            String url = "http://localhost:8086/manage/transport/batch_insert_exports.do";
                            StringBuffer sb = new StringBuffer();
                            sb.append("entryId=").append(Integer.parseInt(serverResponse.getData().toString())).append("&path=").append(path+"/"+targetFileName);
                            Post4.connectionUrl(url, sb,null);

                        }
                /*        File targetFile = new File(path, targetFileName);
                        Boolean results = targetFile.delete();
                        logger.info("删除结果:{}",results);*/
                    }
                }
                success++;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        resultMap.put("success", true);
        resultMap.put("msg", "成功导入" + success + "文件");
        resultMap.put("file_path", urlS);

        return resultMap;
    }

    /**
     * 直接上传导入
     * @param files
     * @param request
     * @param id
     * @return
     */
    @RequestMapping("upload_excel.do")
    @ResponseBody
    public Map uploadFileCDNExcels(@RequestParam(value = "upload_file", required = false) MultipartFile[] files, HttpServletRequest request,Integer id) {
        Map resultMap = Maps.newHashMap();
        MultipartFile file = null;
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = null;
        int success = 0;
        String[] urlS = new String[files.length];
        try {
            for (int i = 0; i < files.length; i++) {
                targetFileName = fileService.uploadReturnCDN(files[i], path);
                if (StringUtils.isNotBlank(targetFileName)) {

                    urlS[i] = PropertiesUtil.getProperty("field") + PropertiesUtil.getProperty("uploadUrl") +targetFileName;
                    if(id ==null){
                        id=new Random().nextInt(1000);
                    }
                        iExportsListsService.bachInsertExports(id,path+"/"+targetFileName);
                        File targetFile = new File(path, targetFileName);
                        Boolean results = targetFile.delete();
                        logger.info("删除结果:{}",results);
                }
                success++;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        resultMap.put("success", true);
        resultMap.put("msg", "成功导入" + success + "文件");
        resultMap.put("file_path", urlS);
        resultMap.put("entryId", id);

        return resultMap;
    }



    /**
     * 跨系统查询transport
     * @param entry
     * @return
     */
    @RequestMapping("get_trans_by_entry.do")
    @ResponseBody
    public ServerResponse<Transport> getTransByEntry(Entry entry){
        return iTransportService.getTransByEntry(entry);
    }

}
