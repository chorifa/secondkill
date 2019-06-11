package com.chorifa.secondkillproject.service.impl;

import com.chorifa.secondkillproject.dao.PromoDataObjectMapper;
import com.chorifa.secondkillproject.dataobject.PromoDataObject;
import com.chorifa.secondkillproject.service.PromoService;
import com.chorifa.secondkillproject.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDataObjectMapper promoDataObjectMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        PromoDataObject promoDataObject = promoDataObjectMapper.selectByItemId(itemId);

        PromoModel promoModel = convertFromDataObject(promoDataObject);
        if(promoModel == null) return null;

        // 判断活动时间是否合适
        // 没开始
        if(promoModel.getStartDate().isAfterNow())
            promoModel.setStatus(1);
        // 已结束
        else if(promoModel.getEndDate().isBeforeNow())
            promoModel.setStatus(3);
        // 进行中
        else promoModel.setStatus(2);

        return promoModel;
    }

    private PromoModel convertFromDataObject(PromoDataObject promoDataObject){
        if(promoDataObject == null) return null;
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDataObject,promoModel);
        promoModel.setStartDate(new DateTime(promoDataObject.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDataObject.getEndDate()));
        return promoModel;
    }
}
