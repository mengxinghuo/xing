package com.truck.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.truck.common.Const;
import com.truck.common.ServerResponse;
import com.truck.dao.EntryDetailMapper;
import com.truck.dao.EntryMapper;
import com.truck.dao.RepertoryMapper;
import com.truck.dao.TransportMapper;
import com.truck.pojo.Entry;
import com.truck.pojo.EntryDetail;
import com.truck.pojo.Repertory;
import com.truck.pojo.Transport;
import com.truck.service.IEntryService;
import com.truck.service.IRepertoryService;
import com.truck.util.DateTimeUtil;
import com.truck.vo.EntryDetailVo;
import com.truck.vo.EntryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service("iEntryService")
public class EntryServiceImpl implements IEntryService {

    @Autowired
    private EntryMapper entryMapper;
    @Autowired
    private EntryDetailMapper entryDetailMapper;
    @Autowired
    private RepertoryMapper repertoryMapper;
    @Autowired
    private IRepertoryService iRepertoryService;
    @Autowired
    private TransportMapper transportMapper;

    /**
     * 入库单列表
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse getEntryList(Integer status, String declareNum, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Entry> entryList = entryMapper.selectAllList(status,declareNum);
        if(entryList.size() == 0){
            return ServerResponse.createByErrorMessage("未查到任何记录");
        }
        List<EntryVo> entryVoList = Lists.newArrayList();
        for(Entry entryItem : entryList){
            EntryVo entryVo = this.assembleEntry(entryItem);
            if(!StringUtils.isEmpty(entryVo.getEntryNo()))
            entryVoList.add(entryVo);
        }
        PageInfo pageInfo = new PageInfo(entryList);
        pageInfo.setList(entryVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse getEntryDetail(Integer entryId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<EntryDetail> entryDetailList = entryDetailMapper.selectEntryDetail(entryId);
        List<EntryDetailVo> entryDetailVoList = Lists.newArrayList();
        for(EntryDetail entryDetailItem : entryDetailList){
            EntryDetailVo entryDetailVo = this.assembleEntryDetail(entryDetailItem);
            entryDetailVoList.add(entryDetailVo);
        }
        PageInfo pageInfo = new PageInfo(entryDetailList);
        pageInfo.setList(entryDetailVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse updateEntryDetailStatus(Integer entryDetailId,Integer inspectStatus){
        if (entryDetailId == null || inspectStatus ==null) {
            return ServerResponse.createByErrorMessage("更新入库详情状态错误");
        }
        EntryDetail entryDetail = entryDetailMapper.selectByPrimaryKey(entryDetailId);
        Entry entry = entryMapper.selectByPrimaryKey(entryDetail.getEntryId());
        Transport transport = transportMapper.getByDeclareNum(entry.getDeclareNum());

        /*if(inspectStatus == 1){
            if(!StringUtils.isEmpty(entryDetail.getEntryNum())){
                if(entryDetail.getEntryNum() != entryDetail.getPurchaseNum()){
                    return ServerResponse.createByErrorMessage("数量不符，请修改，或请先清空实际数量");
                }
            }
        }*/
        if(Const.EntryStatusEnum.FINISH.getCode() == entry.getStatus()){
            return ServerResponse.createByErrorMessage("该单已入库，不可编辑");
        }
        entryDetail.setInspectStatus(inspectStatus);
        int rowCount = entryDetailMapper.updateByPrimaryKeySelective(entryDetail);
        if(rowCount > 0){
            if(Const.EntryStatusEnum.STANDBY.getCode() == entry.getStatus()){
                entry.setStatus(Const.EntryStatusEnum.CONFIRM.getCode());
                entryMapper.updateByPrimaryKeySelective(entry);
            }
            if(Const.TransportStatusEnum.OVER_CONFIRM.getCode() == transport.getStatus()){
                transport.setStatus(Const.TransportStatusEnum.ON_CHECK.getCode());
                transportMapper.updateByPrimaryKeySelective(transport);
            }
            return ServerResponse.createBySuccess("更新入库详情状态成功");
        }
        return ServerResponse.createByErrorMessage("更新入库详情状态失败");
    }

    public ServerResponse updateEntryDetailNum(Integer entryDetailId,Integer entryNum){
        if (entryDetailId == null || entryNum ==null) {
            return ServerResponse.createByErrorMessage("更新入库详情数量错误");
        }
        EntryDetail entryDetail = entryDetailMapper.selectByPrimaryKey(entryDetailId);
        Entry entry = entryMapper.selectByPrimaryKey(entryDetail.getEntryId());
        Transport transport = transportMapper.getByDeclareNum(entry.getDeclareNum());
        if(Const.EntryStatusEnum.FINISH.getCode() == entry.getStatus()){
            return ServerResponse.createByErrorMessage("该单已入库，不可编辑");
        }

        if(entryDetail.getInspectStatus() == 1){
            return ServerResponse.createByErrorMessage("请更改状态");
        }
        entryDetail.setEntryNum(entryNum);
        int rowCount = entryDetailMapper.updateByPrimaryKeySelective(entryDetail);
        if(rowCount > 0){
            if(Const.EntryStatusEnum.STANDBY.getCode() == entry.getStatus()){
                entry.setStatus(Const.EntryStatusEnum.CONFIRM.getCode());
                entryMapper.updateByPrimaryKeySelective(entry);
            }
            if(Const.TransportStatusEnum.OVER_CONFIRM.getCode() == transport.getStatus()){
                transport.setStatus(Const.TransportStatusEnum.ON_CHECK.getCode());
                transportMapper.updateByPrimaryKeySelective(transport);
            }
            return ServerResponse.createBySuccess("更新入库详情数量成功");
        }
        return ServerResponse.createByErrorMessage("更新入库详情数量失败");
    }

    public ServerResponse updateEntryDetailPosition(Integer entryDetailId,Integer entryPosition){
        if (entryDetailId == null || entryPosition ==null) {
            return ServerResponse.createByErrorMessage("更新入库详情位置错误");
        }
        EntryDetail entryDetail = entryDetailMapper.selectByPrimaryKey(entryDetailId);
        Entry entry = entryMapper.selectByPrimaryKey(entryDetail.getEntryId());
        Transport transport = transportMapper.getByDeclareNum(entry.getDeclareNum());
        if(Const.EntryStatusEnum.FINISH.getCode() == entry.getStatus()){
            return ServerResponse.createByErrorMessage("该单已入库，不可编辑");
        }

        entryDetail.setEntryPosition(entryPosition);
        int rowCount = entryDetailMapper.updateByPrimaryKeySelective(entryDetail);
        if(rowCount > 0){
            if(Const.EntryStatusEnum.STANDBY.getCode() == entry.getStatus()){
                entry.setStatus(Const.EntryStatusEnum.CONFIRM.getCode());
                entryMapper.updateByPrimaryKeySelective(entry);
            }
            if(Const.TransportStatusEnum.OVER_CONFIRM.getCode() == transport.getStatus()){
                transport.setStatus(Const.TransportStatusEnum.ON_CHECK.getCode());
                transportMapper.updateByPrimaryKeySelective(transport);
            }
            return ServerResponse.createBySuccess("更新入库详情位置成功");
        }
        return ServerResponse.createByErrorMessage("更新入库详情位置失败");
    }

    public ServerResponse updateEntryDetailIdOrDescs(Integer entryDetailId,Integer typeCategoryId,String errorDescs){
        if (entryDetailId == null || errorDescs==null) {
            return ServerResponse.createByErrorMessage("更新入库详情问题描述错误");
        }
        EntryDetail entryDetail = entryDetailMapper.selectByPrimaryKey(entryDetailId);
        Entry entry = entryMapper.selectByPrimaryKey(entryDetail.getEntryId());
        if(Const.EntryStatusEnum.FINISH.getCode() == entry.getStatus()){
            return ServerResponse.createByErrorMessage("该单已入库，不可编辑");
        }
        if(errorDescs!=null){
            if(entryDetail.getInspectStatus()==1){
                return ServerResponse.createByErrorMessage("已经标记没有问题，不能填写问题描述");
            }
            entryDetail.setErrorDescs(errorDescs);
        }
        int rowCount = entryDetailMapper.updateByPrimaryKeySelective(entryDetail);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("更新入库详情问题描述成功");
        }
        return ServerResponse.createByErrorMessage("更新入库详情问题描述失败");
    }

    public ServerResponse updateEntryDetailRemarks(EntryDetail entryDetails){
        if (entryDetails.getId() == null || entryDetails.getRemarks()==null) {
            return ServerResponse.createByErrorMessage("更新入库详情问题描述错误");
        }
        EntryDetail entryDetail = entryDetailMapper.selectByPrimaryKey(entryDetails.getId());
        Entry entry = entryMapper.selectByPrimaryKey(entryDetail.getEntryId());
        if(Const.EntryStatusEnum.FINISH.getCode() == entry.getStatus()){
            return ServerResponse.createByErrorMessage("该单已入库，不可编辑");
        }
        int rowCount = entryDetailMapper.updateByPrimaryKeySelective(entryDetails);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("更新入库详情问题描述成功");
        }
        return ServerResponse.createByErrorMessage("更新入库详情问题描述失败");
    }

    public EntryVo assembleEntry(Entry entry){
        EntryVo entryVo = new EntryVo();
        if(entry.getEntryNo() ==null)
            return entryVo;
        entryVo.setId(entry.getId());
        entryVo.setEntryNo(entry.getEntryNo());
        entryVo.setDeclareNum(entry.getDeclareNum());
        entryVo.setDestination(entry.getDestination());
        entryVo.setStatus(entry.getStatus());
        entryVo.setStatusDesc(Const.EntryStatusEnum.codeOf(entry.getStatus()).getValue());
        entryVo.setInspector(entry.getInspector());
        entryVo.setCreateTime(DateTimeUtil.dateToStr(entry.getCreateTime()));
        entryVo.setUpdateTime(DateTimeUtil.dateToStr(entry.getUpdateTime()));
        entryVo.setShipNum(entry.getShipNum());
        return entryVo;
    }

    public EntryDetailVo assembleEntryDetail(EntryDetail entryDetail){
        EntryDetailVo entryDetailVo = new EntryDetailVo();
        entryDetailVo.setId(entryDetail.getId());
        entryDetailVo.setEntryId(entryDetail.getEntryId());
        entryDetailVo.setCustomsClearance(entryDetail.getCustomsClearance());
        entryDetailVo.setDestination(entryDetail.getDestination());
        entryDetailVo.setPackageNo(entryDetail.getPackageNo());
        //entryDetailVo.setSerialNo(entryDetail.getSerialNo());
        entryDetailVo.setPartsNo(entryDetail.getPartsNo());
        entryDetailVo.setPartsName(entryDetail.getPartsName());
        entryDetailVo.setPartsEnName(entryDetail.getPartsEnName());
        entryDetailVo.setUnit(entryDetail.getUnit());
        entryDetailVo.setPurchaseNum(entryDetail.getPurchaseNum());
        //entryDetailVo.setPurchasePrice(entryDetail.getPurchasePrice());
        entryDetailVo.setSalesPrice(entryDetail.getSalesPrice());
        entryDetailVo.setDeviceType(entryDetail.getDeviceType());
        if(!StringUtils.isEmpty(entryDetail.getEntryPosition())){
            entryDetailVo.setEntryPosition(entryDetail.getEntryPosition());
            //拼接 位置代码
            List<Integer> idList = Lists.newArrayList();
            iRepertoryService.findDeepParentId(idList,entryDetailVo.getEntryPosition());
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = idList.size() - 1; i >= 0; i--) {
                Repertory repertory = repertoryMapper.selectByPrimaryKey(idList.get(i));
                if (repertory != null) {
                    stringBuilder.append("-"+repertory.getName());
                }
            }
            entryDetailVo.setPositionDesc(entryDetail.getCustomsClearance()+stringBuilder.toString());

        }
        if(!StringUtils.isEmpty(entryDetail.getInspectStatus())){
            entryDetailVo.setInspectStatus(entryDetail.getInspectStatus());
        }
        if(!StringUtils.isEmpty(entryDetail.getEntryNum())){
            entryDetailVo.setEntryNum(entryDetail.getEntryNum());
        }
        entryDetailVo.setCreateTime(DateTimeUtil.dateToStr(entryDetail.getCreateTime()));
        entryDetailVo.setUpdateTime(DateTimeUtil.dateToStr(entryDetail.getUpdateTime()));
        entryDetailVo.setShipNum(entryDetail.getShipNum());
        if(org.apache.commons.lang3.StringUtils.isNotBlank(entryDetail.getErrorDescs())){
            entryDetailVo.setErrorDescs(entryDetail.getErrorDescs());
        }
        Entry entry = entryMapper.selectByPrimaryKey(entryDetail.getEntryId());
        entryDetailVo.setEntryStatus(entry.getStatus());
        entryDetailVo.setEntryStatusDesc(Const.EntryStatusEnum.codeOf(entry.getStatus()).getValue());
        if(!StringUtils.isEmpty(entryDetail.getRemarks())){
            entryDetailVo.setRemarks(entryDetail.getRemarks());
        }
        return entryDetailVo;
    }
}
