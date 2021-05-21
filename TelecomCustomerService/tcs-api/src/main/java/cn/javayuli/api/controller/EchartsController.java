package cn.javayuli.api.controller;

import cn.javayuli.api.entity.Call;
import cn.javayuli.api.entity.Contacts;
import cn.javayuli.api.entity.DimensionDate;
import cn.javayuli.api.service.CallService;
import cn.javayuli.api.service.ContactsService;
import cn.javayuli.api.service.DimensionDateService;
import cn.javayuli.api.vo.DataOut;
import cn.javayuli.api.vo.DataOutWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hanguilin
 *
 * 控制器
 */
@RestController
@RequestMapping("/web")
public class EchartsController {

    @Autowired
    private DimensionDateService dimensionDateService;

    @Autowired
    private CallService callService;

    @Autowired
    private ContactsService contactsService;

    /**
     * 根据用户和日期维度查询数据
     *
     * @param type 类型
     * @param phone 用户号码
     * @return
     */
    @GetMapping("/data/{type}/{phone}")
    public DataOutWrapper getData(@PathVariable String type, @PathVariable String phone) {
        Contacts contacts = contactsService.getOne(Wrappers.lambdaQuery(Contacts.class).eq(Contacts::getTelephone, phone));
        if (contacts == null) {
            return null;
        }
        List<DimensionDate> dimensionDates;
        switch (type) {
            case "year":
                dimensionDates = dimensionDateService.list(Wrappers.lambdaQuery(DimensionDate.class).eq(DimensionDate::getMonth, "").eq(DimensionDate::getDay, ""));
                break;
            case "month":
                dimensionDates = dimensionDateService.list(Wrappers.lambdaQuery(DimensionDate.class).ne(DimensionDate::getMonth, "").eq(DimensionDate::getDay, ""));
                break;
            case "day":
                dimensionDates = dimensionDateService.list(Wrappers.lambdaQuery(DimensionDate.class).ne(DimensionDate::getMonth, "").ne(DimensionDate::getDay, ""));
                break;
            default:
                throw new RuntimeException("no type to query");
        }
        Map<Integer, DimensionDate> dimensionDateMap = dimensionDates.stream().collect(Collectors.toMap(DimensionDate::getId, Function.identity()));
        Integer contactsId = contacts.getId();
        List<String> callIdList = dimensionDateMap.keySet().stream().map(o -> o + "_" + contactsId).collect(Collectors.toList());
        List<Call> callList = callService.list(Wrappers.lambdaQuery(Call.class).in(Call::getIdDateContact, callIdList));
        List<DataOut> dataOuts = callList.stream().map(o -> {
            DimensionDate dimensionDate = dimensionDateMap.get(o.getIdDateDimension());
            DataOut dataOut = new DataOut();
            dataOut.setDate(dimensionDate.getFormatDate());
            dataOut.setCallSum(o.getCallSum());
            dataOut.setCallDurationSum(o.getCallDurationSum());
            return dataOut;
        }).sorted(Comparator.comparing(DataOut::getDate)).collect(Collectors.toList());
        return new DataOutWrapper(dataOuts, contacts.getName());
    }

    /**
     * 获取所有用户
     *
     * @return
     */
    @GetMapping("/data/contacts")
    public List<Contacts> getContacts(){
        return contactsService.list();
    }
}
