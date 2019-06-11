package com.chorifa.secondkillproject.service;

import com.chorifa.secondkillproject.service.model.PromoModel;

public interface PromoService {

    // 根据itemId获取即将开始的或者正在进行的promo
    PromoModel getPromoByItemId(Integer itemId);

}
