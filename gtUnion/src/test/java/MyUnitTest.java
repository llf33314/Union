import com.alibaba.fastjson.JSON;
import com.gt.union.UnionApplication;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.card.service.IUnionCardService;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * 测试类
 * Created by Administrator on 2017/8/11 0011.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UnionApplication.class)// 指定spring-boot的启动类
public class MyUnitTest {

    @Autowired
    private IUnionCardService unionCardService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private SocketService socketService;

    @Test
    public void test1() throws Exception {
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("status","004");
        result.put("only",1234864658);
        socketService.socketSendMessage(PropertiesUtil.getSocketKey() + CommonUtil.toInteger(33), JSON.toJSONString(result),"");
    }

    @Test
    public void testIntersection() throws Exception {
        List<UnionMain> list = new ArrayList<>();
        List<UnionMain> list2 = new ArrayList<>();
        UnionMain unionMain = new UnionMain();
        unionMain.setId(1);
        list.add(unionMain);
        list2.add(unionMain);
        List<UnionMain> result = unionMainService.intersection(list, list2);
        System.out.println(JSON.toJSONString(result));
    }

    @Test
    public void testListByMemberIdList() throws Exception {
        List<UnionMember> unionMemberList = this.unionMemberService.listByIds(Arrays.asList(1, 2));
        System.out.println(JSON.toJSONString(unionMemberList));
    }

    public static void main(String[] args) {
        System.out.println(Double.valueOf("1.20"));
    }
}
