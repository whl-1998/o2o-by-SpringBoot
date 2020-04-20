package com.whl.o2o.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.whl.o2o.dao.AwardDao;
import com.whl.o2o.dto.AwardExecution;
import com.whl.o2o.dto.ImageHolder;
import com.whl.o2o.entity.Award;
import com.whl.o2o.enums.AwardStateEnum;
import com.whl.o2o.enums.ShopStateEnum;
import com.whl.o2o.exceptions.AwardOperationException;
import com.whl.o2o.exceptions.ShopOperationException;
import com.whl.o2o.service.AwardService;
import com.whl.o2o.util.ImageUtil;
import com.whl.o2o.util.PageCalculator;
import com.whl.o2o.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AwardServiceImpl implements AwardService {
	@Autowired
	private AwardDao awardDao;

	private final static Logger logger = LoggerFactory.getLogger(AwardServiceImpl.class);

	@Override
	public AwardExecution getAwardList(Award awardCondition, int pageIndex, int pageSize) {
		int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
		List<Award> awardList = awardDao.queryAwardList(awardCondition, rowIndex, pageSize);
		int count = awardDao.queryAwardCount(awardCondition);
		AwardExecution ae = new AwardExecution();
		if (awardList.size() == count) {
			ae.setAwardList(awardList);
			ae.setCount(count);
		} else {
			return new AwardExecution(AwardStateEnum.INNER_ERROR);
		}
		return ae;
	}

	@Override
	public AwardExecution getAwardById(long awardId) {
		if (awardId <= 0) {
			return new AwardExecution(AwardStateEnum.EMPTY);
		}
		return new AwardExecution(AwardStateEnum.SUCCESS, awardDao.queryAwardByAwardId(awardId));
	}

	@Override
	@Transactional
	public AwardExecution addAward(Award award, ImageHolder thumbnail) {
		if (award == null || award.getShopId() == null) {
			return new AwardExecution(AwardStateEnum.EMPTY);
		}
		award.setCreateTime(new Date());
		award.setUpdateTime(new Date());
		award.setEnableStatus(1);
		award.setPriority(0);
		int effectedNum = awardDao.insertAward(award);
		if (effectedNum <= 0) {
			logger.error("添加商品失败, 返回0条变更");
			throw new AwardOperationException("添加奖品失败");
		}
		if (thumbnail != null && thumbnail.getImage() != null) {
			try {
				addThumbnail(award, thumbnail);
			} catch (Exception e) {
				logger.error("插入奖品图片失败: " + e.getMessage());
				throw new AwardOperationException("添加奖品失败");
			}
		}
		effectedNum = awardDao.insertAward(award);
		if (effectedNum <= 0) {
			logger.error("更新商品失败, 返回0条变更");
			throw new AwardOperationException("添加奖品失败");
		}
		return new AwardExecution(AwardStateEnum.SUCCESS, award);
	}

	@Override
	@Transactional
	public AwardExecution modifyAward(Award award, ImageHolder thumbnail) {
		if (award == null || award.getAwardId() == null) {
			return new AwardExecution(AwardStateEnum.EMPTY);
		}
		award.setUpdateTime(new Date());
		if (thumbnail != null && thumbnail.getImage() != null) {
			Award tempAward = awardDao.queryAwardByAwardId(award.getAwardId());
			if (tempAward.getAwardImg() != null) {// 若原award的图片字段不为空, 则删除原图片, 添加新的图片
				ImageUtil.deleteFileOrPath(tempAward.getAwardImg());
			}
			try {
				addThumbnail(award, thumbnail);
			} catch (Exception e) {
				logger.error("处理奖品图片失败: " + e.getMessage());
				throw new AwardOperationException("修改奖品失败: " + e.getMessage());
			}
		}
		int effectedNum = awardDao.updateAward(award);
		if (effectedNum <= 0) {
			logger.error("更新商品失败, 返回0条变更");
			throw new AwardOperationException("修改奖品失败");
		}
		return new AwardExecution(AwardStateEnum.SUCCESS, award);
	}

	private void addThumbnail(Award award, ImageHolder thumbnail) throws IOException {
		String dest = PathUtil.getShopImagePath(award.getShopId());
		String thumbnailAddr = ImageUtil.generateThumbnail(thumbnail, dest);
		award.setAwardImg(thumbnailAddr);
	}
}
