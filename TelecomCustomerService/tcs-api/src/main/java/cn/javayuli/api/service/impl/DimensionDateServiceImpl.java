package cn.javayuli.api.service.impl;

import cn.javayuli.api.dao.DimensionDateDao;
import cn.javayuli.api.entity.DimensionDate;
import cn.javayuli.api.service.DimensionDateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author hanguilin
 *
 * 时间维度
 */
@Service
public class DimensionDateServiceImpl extends ServiceImpl<DimensionDateDao, DimensionDate> implements DimensionDateService {
}
