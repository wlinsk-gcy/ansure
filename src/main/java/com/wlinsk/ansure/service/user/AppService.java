package com.wlinsk.ansure.service.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wlinsk.ansure.model.dto.app.req.*;
import com.wlinsk.ansure.model.dto.app.resp.QueryAppDetailsRespDTO;
import com.wlinsk.ansure.model.dto.app.resp.QueryAppListRespDTO;
import com.wlinsk.ansure.model.dto.app.resp.QueryAppPageRespDTO;
import com.wlinsk.ansure.model.entity.App;

import java.util.List;

/**
 *
 */
public interface AppService extends IService<App> {

    String addApp(AddAppReqDTO dto);

    void deleteApp(DeleteAppReqDTO reqDTO);

    IPage<QueryAppPageRespDTO> queryPage(QueryAppPageReqDTO reqDTO);

    QueryAppDetailsRespDTO queryById(String appId);

    void updateApp(UpdateAppReqDTO reqDTO);

    IPage<QueryAppPageRespDTO> queryMyPage(QueryMyAppPageReqDTO reqDTO);

    List<QueryAppListRespDTO> queryList(QueryAppListReqDTO reqDTO);

    QueryAppListRespDTO queryLast();


}
