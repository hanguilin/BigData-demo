package cn.javayuli.api.service.impl;

import cn.javayuli.api.dao.CallDao;
import cn.javayuli.api.entity.Call;
import cn.javayuli.api.service.CallService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author hanguilin
 *
 * 通话信息
 */
@Service
public class CallServiceImpl extends ServiceImpl<CallDao, Call> implements CallService {
}
