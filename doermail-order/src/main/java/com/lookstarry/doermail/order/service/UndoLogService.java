package com.lookstarry.doermail.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.doermail.order.entity.UndoLogEntity;

import java.util.Map;

/**
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 13:49:09
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

