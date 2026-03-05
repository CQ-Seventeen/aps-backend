package com.santoni.iot.aps.application.bom;

import com.santoni.iot.aps.application.bom.command.BatchCreateStyleCommand;
import com.santoni.iot.aps.application.bom.command.CreateStyleCommand;
import com.santoni.iot.aps.application.bom.command.OperateOuterStyleCommand;
import com.santoni.iot.aps.application.bom.command.UpdateStyleCommand;
import com.santoni.iot.aps.application.bom.dto.StyleComponentDTO;
import com.santoni.iot.aps.application.bom.dto.StyleSkuDTO;
import com.santoni.iot.aps.application.bom.query.*;
import com.santoni.iot.aps.application.support.dto.BatchOperateResultDTO;
import com.santoni.iot.aps.application.support.dto.PageResult;
import com.santoni.iot.aps.application.bom.dto.StyleDTO;

public interface StyleApplication {

    void createStyle(CreateStyleCommand cmd);

    BatchOperateResultDTO batchCreateStyle(BatchCreateStyleCommand cmd);

    void updateStyle(UpdateStyleCommand cmd);

    PageResult<StyleDTO> pageQueryStyle(PageStyleQuery query);

    StyleDTO getStyleDetail(StyleDetailQuery query);

    StyleDTO getStyleDetailByCode(StyleDetailByCodeQuery query);

    StyleComponentDTO getStyleComponentDetail(StyleComponentDetailQuery query);

    void importOuterStyle(OperateOuterStyleCommand command);
}
