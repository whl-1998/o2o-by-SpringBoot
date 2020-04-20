package com.whl.o2o.service;

import com.whl.o2o.dto.AwardExecution;
import com.whl.o2o.dto.ImageHolder;
import com.whl.o2o.entity.Award;

public interface AwardService {

	AwardExecution getAwardList(Award awardCondition, int pageIndex, int pageSize);

	AwardExecution getAwardById(long awardId);

	AwardExecution addAward(Award award, ImageHolder thumbnail);

	AwardExecution modifyAward(Award award, ImageHolder thumbnail);
}
