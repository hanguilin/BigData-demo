package cn.javayuli.api.service.impl;

import cn.javayuli.api.dao.ContactsDao;
import cn.javayuli.api.entity.Contacts;
import cn.javayuli.api.service.ContactsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author hanguilin
 *
 * 联系人信息
 */
@Service
public class ContactsServiceImpl extends ServiceImpl<ContactsDao, Contacts> implements ContactsService {
}
