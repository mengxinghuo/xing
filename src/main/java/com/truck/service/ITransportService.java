package com.truck.service;

import com.truck.common.ServerResponse;
import com.truck.pojo.Entry;
import com.truck.pojo.Transport;

public interface ITransportService {

    ServerResponse addTransport(Integer adminId, Transport transport);

    ServerResponse updateTransport(Integer adminId, Transport transport);

    ServerResponse delTransport(Integer adminId, Integer id);

    ServerResponse consummateTransport( Integer id, String salesList);

    ServerResponse hostTransport( Integer id, String zhuJiList);

    ServerResponse getAllList(Integer status,int pageNum,int pageSize);

    ServerResponse createEntry(Integer id);

    ServerResponse createHostEntry(Integer id);

    ServerResponse checkEntryByDeclareNum(String declareNum);

    ServerResponse checkZhuJiEntryByDeclareNum(String declareNum);

    ServerResponse<Transport> getTransByEntry(Entry entry);
}
