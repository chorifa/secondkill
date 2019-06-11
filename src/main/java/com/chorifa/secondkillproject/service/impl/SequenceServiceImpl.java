package com.chorifa.secondkillproject.service.impl;

import com.chorifa.secondkillproject.dao.SequenceDataObjectMapper;
import com.chorifa.secondkillproject.dataobject.SequenceDataObject;
import com.chorifa.secondkillproject.service.SequenceService;
import com.chorifa.secondkillproject.service.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SequenceServiceImpl implements SequenceService {

    @Autowired
    private SequenceDataObjectMapper sequenceDataObjectMapper;

    @Autowired
    private RedisService redisService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderId() {
        StringBuilder stringBuilder = new StringBuilder();
        //订单号16位
        //前8位时间信息年月日
        LocalDateTime now = LocalDateTime.now();
        stringBuilder.append( now.format(DateTimeFormatter.ISO_DATE).replace("-","") );

        //中间6位自增序列
        SequenceDataObject sequenceDataObject = sequenceDataObjectMapper.getSequenceByName("order_info");
        int seq = sequenceDataObject.getCurrentValue();
        String sequence = String.valueOf(seq);

        sequenceDataObject.setCurrentValue(sequenceDataObject.getCurrentValue() + sequenceDataObject.getStep());
        sequenceDataObjectMapper.updateByPrimaryKeySelective(sequenceDataObject);

        // 补零
        for(int i = 0; i < 6-sequence.length(); i++)
            stringBuilder.append('0');
        stringBuilder.append(sequence);

        //最后2位分库分表位
        stringBuilder.append("00");

        return stringBuilder.toString();
    }

    @Override
    public String generateOrderIdWithRedis() {
        StringBuilder stringBuilder = new StringBuilder();
        //订单号20位
        //前8位时间信息年月日
        LocalDateTime now = LocalDateTime.now();
        stringBuilder.append( now.format(DateTimeFormatter.ISO_DATE).replace("-","") );

        //中间6位自增序列，获得的是增加后的值
        String sequence = String.valueOf(redisService.incr(SEQ_VALUE_KEY,SEQ_STEP));
        // TODO 溢出
        // 10位
        // 补零
        for(int i = 0; i < 10-sequence.length(); i++)
            stringBuilder.append('0');
        stringBuilder.append(sequence);

        //最后2位分库分表位
        stringBuilder.append("00");

        return stringBuilder.toString();
    }

}
