import com.gt.union.common.util.DateUtil;
import org.junit.Test;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/22 0022.
 */
public class DateUtilTest {
    @Test
    public void getCurrentWeekTest() {
        System.out.println(DateUtil.getCurrentWeek());
    }

    @Test
    public void addDaysTest() {
        Date currentDate = DateUtil.getCurrentDate();
        System.out.println("今天是：" + DateUtil.getWeek(currentDate));

        System.out.println("昨天是：" + DateUtil.getWeek(DateUtil.addDays(currentDate, -1)));

        System.out.println("明天是：" + DateUtil.getWeek(DateUtil.addDays(currentDate, 1)));
    }
}
