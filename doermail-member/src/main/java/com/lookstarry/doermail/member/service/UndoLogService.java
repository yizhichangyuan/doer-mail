package com.lookstarry.doermail.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.doermail.member.entity.UndoLogEntity;

import java.util.Map;

/**
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 13:59:56
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

