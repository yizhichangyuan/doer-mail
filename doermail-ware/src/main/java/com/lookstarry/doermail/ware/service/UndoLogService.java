package com.lookstarry.doermail.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.doermail.ware.entity.UndoLogEntity;

import java.util.Map;

/**
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 14:03:12
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

