package com.wlinsk.ansure.service.manage;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wlinsk.ansure.model.dto.app.req.ManagerAppQueryPageReqDTO;
import com.wlinsk.ansure.model.dto.app.req.ManagerReviewAddReqDTO;
import com.wlinsk.ansure.model.dto.app.req.ManagerUpdateAppReqDTO;
import com.wlinsk.ansure.model.dto.app.resp.ManagerAppQueryPageRespDTO;

/**
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
public interface ManagerAppService {
    void updateApp(ManagerUpdateAppReqDTO reqDTO);

    void reviewApp(ManagerReviewAddReqDTO reqDTO);

    IPage<ManagerAppQueryPageRespDTO> queryPage(ManagerAppQueryPageReqDTO req);

    void deleteById(String appId);
}
