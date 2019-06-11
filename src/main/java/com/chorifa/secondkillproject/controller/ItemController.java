package com.chorifa.secondkillproject.controller;

import com.chorifa.secondkillproject.controller.viewobject.ItemVO;
import com.chorifa.secondkillproject.error.BusinessException;
import com.chorifa.secondkillproject.error.EmBusinessError;
import com.chorifa.secondkillproject.response.CommonReturnType;
import com.chorifa.secondkillproject.service.ItemService;
import com.chorifa.secondkillproject.service.model.ItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController("item")
@RequestMapping("/item")
// DEFAULT_ALLOWED_HEADERS:允许跨域传输所有的header参数，将用于使用token放入header域做session共享的跨域请求
// DEFAULT_ALLOW_CREDENTIALS = true:需配合前端设置xhrFields授信后使得跨域session共享
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 本地文件(前端) -> localhost的跨域请求
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    // 创建商品，注意是value不是name
    @PostMapping(value = "/createItem", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {
        // 封装ItemModel
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);
        // 使用service
        itemModel = itemService.createItem(itemModel);
        return CommonReturnType.create(itemService.createVO(itemModel));
    }

    // 商品详情页
    @GetMapping(value = "/get") //, consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id) throws BusinessException {
        ItemModel itemModel = itemService.getItemById(id);
        if(itemModel == null)
            throw new BusinessException(EmBusinessError.ITEM_NOT_EXIST);
        ItemVO itemVO = itemService.createVO(itemModel);
        return CommonReturnType.create(itemVO);
    }

    // 商品列表
    @GetMapping(value = "/itemlist") //, consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType listItem(){
        List<ItemModel> itemModelList = itemService.listItem();
        // 用stream转换为VO
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> itemService.createVO(itemModel)).collect(Collectors.toList());

        return CommonReturnType.create(itemVOList);
    }

}
