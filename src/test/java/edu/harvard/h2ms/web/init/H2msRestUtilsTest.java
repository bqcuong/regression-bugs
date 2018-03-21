package edu.harvard.h2ms.web.init;

import edu.harvard.h2ms.service.utils.H2msRestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
public class H2msRestUtilsTest {

    @Test
    public void format_week_success(){
        H2msRestUtils  utils = new H2msRestUtils();
        Integer week = 1;
        Integer year = 2018;
        String results = utils.formatWeek(week, year);
        Assert.isTrue(results.contains("1st (2018)"));
        week = 2;
        results = utils.formatWeek(week, year);
        Assert.isTrue(results.contains("2nd (2018)"));
        week = 21;
        results = utils.formatWeek(week, year);
        Assert.isTrue(results.contains("21st (2018)"));
        week = 22;
        results = utils.formatWeek(week, year);
        Assert.isTrue(results.contains("22nd (2018)"));
        week = 23;
        results = utils.formatWeek(week, year);
        Assert.isTrue(results.contains("23rd (2018)"));
    }

}
